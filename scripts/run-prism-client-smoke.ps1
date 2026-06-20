param(
    [string]$PrismExe = "",
    [string]$InstanceName = "1.21.1 TesT LaB",
    [string]$InstancePath = "",
    [int]$TimeoutSeconds = 180,
    [switch]$KeepRunning
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$BuildDir = Join-Path $RepoRoot "build"
$ReportPath = Join-Path $BuildDir "client-smoke-report.json"
$GradlePropertiesPath = Join-Path $RepoRoot "gradle.properties"
$DefaultPrismExe = "C:\Users\Emmanuel Tremblay\AppData\Local\Programs\PrismLauncher\prismlauncher.exe"
$DefaultInstancePath = "C:\Users\Emmanuel Tremblay\AppData\Roaming\PrismLauncher\instances\1.21.1 TesT LaB"

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null

function Get-ConfiguredVersion {
    param([Parameter(Mandatory = $true)][string]$Path)

    $line = Get-Content -LiteralPath $Path | Where-Object { $_ -match "^mod_version\s*=" } | Select-Object -First 1
    if ($null -eq $line) {
        throw "Unable to read mod_version from $Path."
    }

    return ($line -split "=", 2)[1].Trim()
}

function Read-SharedTextSince {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][long]$Offset
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return ""
    }

    $item = Get-Item -LiteralPath $Path
    $start = if ($item.Length -ge $Offset) { $Offset } else { 0 }
    $stream = [System.IO.File]::Open($Path, [System.IO.FileMode]::Open, [System.IO.FileAccess]::Read, [System.IO.FileShare]::ReadWrite)
    try {
        [void]$stream.Seek($start, [System.IO.SeekOrigin]::Begin)
        $reader = [System.IO.StreamReader]::new($stream)
        try {
            return $reader.ReadToEnd()
        }
        finally {
            $reader.Dispose()
        }
    }
    finally {
        $stream.Dispose()
    }
}

function Select-LogTextSince {
    param(
        [Parameter(Mandatory = $true)][AllowEmptyString()][string]$Text,
        [Parameter(Mandatory = $true)][datetime]$Since
    )

    $rows = @()
    $includeLine = $false
    $culture = [System.Globalization.CultureInfo]::InvariantCulture
    $style = [System.Globalization.DateTimeStyles]::AssumeLocal
    $lines = $Text -split "\r?\n"

    foreach ($line in $lines) {
        $timestampMatch = [regex]::Match($line, "^\[(\d{2}[A-Za-z]{3}\d{4} \d{2}:\d{2}:\d{2}\.\d{3})\]")
        if ($timestampMatch.Success) {
            $parsedTimestamp = [datetime]::MinValue
            $parsed = [datetime]::TryParseExact(
                $timestampMatch.Groups[1].Value,
                "ddMMMyyyy HH:mm:ss.fff",
                $culture,
                $style,
                [ref]$parsedTimestamp
            )
            $includeLine = $parsed -and $parsedTimestamp -ge $Since
        }

        if ($includeLine) {
            $rows += $line
        }
    }

    return $rows -join "`n"
}

function Get-FirstMatchingLine {
    param(
        [Parameter(Mandatory = $true)][AllowEmptyString()][string]$Text,
        [Parameter(Mandatory = $true)][string]$Pattern
    )

    $lines = $Text -split "\r?\n"
    for ($index = 0; $index -lt $lines.Count; $index++) {
        if ($lines[$index] -match $Pattern) {
            return [pscustomobject]@{
                lineNumber = $index + 1
                line = $lines[$index]
            }
        }
    }

    return $null
}

function Get-AllMatchingLines {
    param(
        [Parameter(Mandatory = $true)][AllowEmptyString()][string]$Text,
        [Parameter(Mandatory = $true)][string[]]$Patterns
    )

    $rows = @()
    $lines = $Text -split "\r?\n"
    for ($index = 0; $index -lt $lines.Count; $index++) {
        foreach ($pattern in $Patterns) {
            if ($lines[$index] -match $pattern) {
                $rows += [pscustomobject]@{
                    pattern = $pattern
                    lineNumber = $index + 1
                    line = $lines[$index]
                }
            }
        }
    }

    return $rows
}

function Get-ClientProcessInfo {
    param([Parameter(Mandatory = $true)][string]$InstancePath)

    $slashPath = $InstancePath.Replace("\", "/")
    $processes = @(Get-CimInstance Win32_Process | Where-Object {
        $_.Name -in @("java.exe", "javaw.exe") -and
        $null -ne $_.CommandLine -and
        ($_.CommandLine.Contains($InstancePath) -or $_.CommandLine.Contains($slashPath))
    })

    foreach ($process in $processes) {
        $windowProcess = Get-Process -Id $process.ProcessId -ErrorAction SilentlyContinue
        [pscustomobject]@{
            processId = $process.ProcessId
            name = $process.Name
            creationDate = $process.CreationDate
            mainWindowTitle = if ($windowProcess) { $windowProcess.MainWindowTitle } else { "" }
            responding = if ($windowProcess) { $windowProcess.Responding } else { $false }
            commandLine = $process.CommandLine
        }
    }
}

function Stop-SmokeProcesses {
    param(
        [Parameter(Mandatory = $true)][int[]]$ProcessIds,
        [Parameter(Mandatory = $true)][string]$InstancePath
    )

    $ids = @($ProcessIds)
    $ids += @(Get-ClientProcessInfo -InstancePath $InstancePath | Select-Object -ExpandProperty processId)
    $ids = @($ids | Where-Object { $_ } | Sort-Object -Unique)

    $rows = @()
    foreach ($id in $ids) {
        $process = Get-Process -Id $id -ErrorAction SilentlyContinue
        if (-not $process) {
            $rows += [pscustomobject]@{
                processId = $id
                action = "not-running"
                exited = $true
            }
            continue
        }

        $closed = $false
        try {
            $closed = $process.CloseMainWindow()
        }
        catch {
            $closed = $false
        }

        Start-Sleep -Seconds 8
        if (Get-Process -Id $id -ErrorAction SilentlyContinue) {
            Stop-Process -Id $id -Force
            Start-Sleep -Seconds 2
        }

        $rows += [pscustomobject]@{
            processId = $id
            action = if ($closed) { "close-window" } else { "force-stop" }
            exited = -not (Get-Process -Id $id -ErrorAction SilentlyContinue)
        }
    }

    return $rows
}

if ([string]::IsNullOrWhiteSpace($PrismExe)) {
    $PrismExe = $DefaultPrismExe
}
if ([string]::IsNullOrWhiteSpace($InstancePath)) {
    $InstancePath = $DefaultInstancePath
}

$PrismExe = [System.IO.Path]::GetFullPath($PrismExe)
$InstancePath = [System.IO.Path]::GetFullPath($InstancePath)
$MinecraftLogPath = Join-Path $InstancePath "minecraft\logs\latest.log"
$CrashReportDir = Join-Path $InstancePath "minecraft\crash-reports"
$LauncherLogPath = Join-Path ([System.IO.Path]::GetDirectoryName([System.IO.Path]::GetDirectoryName($InstancePath))) "logs\PrismLauncher-0.log"
$ExpectedVersion = Get-ConfiguredVersion -Path $GradlePropertiesPath
$ExpectedJarName = "immersive_ego_civitas-$ExpectedVersion.jar"

if (-not (Test-Path -LiteralPath $PrismExe)) {
    throw "Prism executable not found: $PrismExe"
}
if (-not (Test-Path -LiteralPath $InstancePath)) {
    throw "Prism instance path not found: $InstancePath"
}

$launcherOffset = if (Test-Path -LiteralPath $LauncherLogPath) { (Get-Item -LiteralPath $LauncherLogPath).Length } else { 0 }
$startedAt = Get-Date
$logSince = $startedAt.AddSeconds(-15)
$launchArgument = "--launch `"$InstanceName`""
$prismProcess = Start-Process -FilePath $PrismExe -ArgumentList $launchArgument -WindowStyle Hidden -PassThru

$successPatterns = [ordered]@{
    modJar = [regex]::Escape("Found mod file `"$ExpectedJarName`"")
    modVersion = [regex]::Escape("Immersive EGO: CIVITAS $ExpectedVersion (immersive_ego_civitas)")
    bootstrap = [regex]::Escape("Immersive EGO: CIVITAS bootstrap registered.")
    runtimeGuard = [regex]::Escape("immersive_ego_civitas pinned runtime dependency check passed:")
    commonSetup = [regex]::Escape("immersive_ego_civitas common setup complete; integration surfaces are intentionally neutral.")
    settingUser = "Setting user:"
    backend = "Backend library: LWJGL"
    openAl = "OpenAL initialized"
    soundEngine = "Sound engine started"
    blockAtlas = "minecraft:textures/atlas/blocks\.png-atlas"
}
$failurePatterns = @(
    "Crash report saved",
    "The game crashed",
    "ModLoadingException",
    "NoClassDefFoundError",
    "has failed to load correctly",
    "Failed to start Minecraft",
    "Minecraft has crashed",
    "Unable to launch"
)
$warningPatterns = @("ClassNotFoundException")

$result = "timeout"
$markerRows = @()
$failureRows = @()
$warningRows = @()
$clientProcesses = @()
$crashReports = @()
$launcherEvidence = ""

try {
    $deadline = $startedAt.AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $rawLogText = Read-SharedTextSince -Path $MinecraftLogPath -Offset 0
        $logText = Select-LogTextSince -Text $rawLogText -Since $logSince
        $launcherEvidence = Read-SharedTextSince -Path $LauncherLogPath -Offset $launcherOffset

        $markerRows = @()
        foreach ($name in $successPatterns.Keys) {
            $match = Get-FirstMatchingLine -Text $logText -Pattern $successPatterns[$name]
            $markerRows += [pscustomobject]@{
                name = $name
                matched = $null -ne $match
                lineNumber = if ($match) { $match.lineNumber } else { $null }
                line = if ($match) { $match.line } else { "" }
            }
        }

        $failureRows = @(Get-AllMatchingLines -Text $logText -Patterns $failurePatterns)
        $warningRows = @(Get-AllMatchingLines -Text $logText -Patterns $warningPatterns)
        $clientProcesses = @(Get-ClientProcessInfo -InstancePath $InstancePath)
        $windowReady = @($clientProcesses | Where-Object {
            $_.responding -and $_.mainWindowTitle -like "*Minecraft*"
        }).Count -gt 0

        $crashReports = @()
        if (Test-Path -LiteralPath $CrashReportDir) {
            $crashReports = @(Get-ChildItem -LiteralPath $CrashReportDir -Filter "*.txt" | Where-Object {
                $_.LastWriteTime -ge $startedAt
            } | Select-Object FullName, LastWriteTime, Length)
        }

        if ($failureRows.Count -gt 0 -or $crashReports.Count -gt 0) {
            $result = "failed"
            break
        }

        $missingMarkers = @($markerRows | Where-Object { -not $_.matched })
        if ($missingMarkers.Count -eq 0 -and $windowReady) {
            $result = "passed"
            break
        }

        Start-Sleep -Seconds 2
    }
}
finally {
    $cleanup = @()
    if (-not $KeepRunning) {
        $cleanup = @(Stop-SmokeProcesses -ProcessIds @($prismProcess.Id) -InstancePath $InstancePath)
    }

    $report = [pscustomobject]@{
        result = $result
        startedAt = $startedAt.ToString("o")
        timeoutSeconds = $TimeoutSeconds
        prismExe = $PrismExe
        prismProcessId = $prismProcess.Id
        launchArgument = $launchArgument
        instanceName = $InstanceName
        instancePath = $InstancePath
        minecraftLogPath = $MinecraftLogPath
        expectedVersion = $ExpectedVersion
        expectedJarName = $ExpectedJarName
        successMarkers = $markerRows
        failureMarkers = $failureRows
        warningMarkers = $warningRows
        clientProcesses = $clientProcesses
        crashReportsSinceLaunch = $crashReports
        launcherLogPath = $LauncherLogPath
        launcherLogEvidenceTail = (($launcherEvidence -split "\r?\n") | Select-Object -Last 40) -join "`n"
        cleanup = $cleanup
    }

    $report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $ReportPath -Encoding UTF8
    $report | ConvertTo-Json -Depth 8
}

if ($result -ne "passed") {
    exit 1
}
