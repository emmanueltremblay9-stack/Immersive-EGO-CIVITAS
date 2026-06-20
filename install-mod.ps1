param(
    [string]$ModsDir = "",
    [string]$BuildDir = "",
    [string]$ModId = "",
    [switch]$SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RepoRoot = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$LocalEnvPath = Join-Path $RepoRoot ".codex\local.env"
$DefaultModsDir = "C:\Users\Emmanuel Tremblay\AppData\Roaming\PrismLauncher\instances\1.21.1 TesT LaB\minecraft\mods"

function Import-LocalEnv {
    param([Parameter(Mandatory = $true)][string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return
    }

    Get-Content -LiteralPath $Path | ForEach-Object {
        $line = $_.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
            return
        }
        $parts = $line.Split("=", 2)
        if ($parts.Count -eq 2 -and -not [string]::IsNullOrWhiteSpace($parts[0])) {
            [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
        }
    }
}

function Get-GradleProperty {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$PropertiesPath
    )

    $match = Get-Content -LiteralPath $PropertiesPath |
        Where-Object { $_ -match "^\s*$([regex]::Escape($Name))\s*=" } |
        Select-Object -First 1
    if ($null -eq $match) {
        return $null
    }
    return ($match -split "=", 2)[1].Trim()
}

function Resolve-SafeDirectory {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$ExpectedLeaf
    )

    if ([string]::IsNullOrWhiteSpace($Path)) {
        throw "Directory path cannot be blank."
    }

    $fullPath = [System.IO.Path]::GetFullPath($Path)
    if (Test-Path -LiteralPath $fullPath) {
        $item = Get-Item -LiteralPath $fullPath
        if (-not $item.PSIsContainer) {
            throw "Path exists but is not a directory: $fullPath"
        }
        return $item.FullName
    }

    $leaf = Split-Path -Path $fullPath -Leaf
    if ($leaf -ne $ExpectedLeaf) {
        throw "Directory does not exist and final segment is not '$ExpectedLeaf'; refusing to create: $fullPath"
    }

    $parent = Split-Path -Path $fullPath -Parent
    if ([string]::IsNullOrWhiteSpace($parent) -or -not (Test-Path -LiteralPath $parent -PathType Container)) {
        throw "Directory parent does not exist; refusing to create full tree: $parent"
    }

    New-Item -ItemType Directory -Path $fullPath | Out-Null
    return (Resolve-Path -LiteralPath $fullPath).Path
}

function Resolve-BuildDirectory {
    param([string]$ExplicitBuildDir)

    if ([string]::IsNullOrWhiteSpace($ExplicitBuildDir)) {
        return (Resolve-Path -LiteralPath (Join-Path $RepoRoot "build\libs")).Path
    }

    if ([System.IO.Path]::IsPathRooted($ExplicitBuildDir)) {
        return (Resolve-Path -LiteralPath $ExplicitBuildDir).Path
    }

    return (Resolve-Path -LiteralPath (Join-Path $RepoRoot $ExplicitBuildDir)).Path
}

function Get-JarMetadata {
    param([Parameter(Mandatory = $true)][string]$JarPath)

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($JarPath)
    try {
        $entry = $zip.Entries | Where-Object { $_.FullName -eq "META-INF/neoforge.mods.toml" } | Select-Object -First 1
        if ($null -eq $entry) {
            return $null
        }
        $reader = New-Object System.IO.StreamReader($entry.Open())
        try {
            $content = $reader.ReadToEnd()
        }
        finally {
            $reader.Dispose()
        }

        $modIdMatch = [regex]::Match($content, '(?m)^\s*modId\s*=\s*"([^"]+)"')
        $versionMatch = [regex]::Match($content, '(?m)^\s*version\s*=\s*"([^"]+)"')
        $nameMatch = [regex]::Match($content, '(?m)^\s*displayName\s*=\s*"([^"]+)"')
        $licenseMatch = [regex]::Match($content, '(?m)^\s*license\s*=\s*"([^"]+)"')

        [pscustomobject]@{
            modId = if ($modIdMatch.Success) { $modIdMatch.Groups[1].Value } else { $null }
            version = if ($versionMatch.Success) { $versionMatch.Groups[1].Value } else { $null }
            displayName = if ($nameMatch.Success) { $nameMatch.Groups[1].Value } else { $null }
            license = if ($licenseMatch.Success) { $licenseMatch.Groups[1].Value } else { $null }
        }
    }
    finally {
        $zip.Dispose()
    }
}

function Get-Sha256 {
    param([Parameter(Mandatory = $true)][string]$Path)
    return (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash.ToLowerInvariant()
}

Import-LocalEnv -Path $LocalEnvPath

$GradlePropertiesPath = Join-Path $RepoRoot "gradle.properties"
if ([string]::IsNullOrWhiteSpace($ModId)) {
    $ModId = Get-GradleProperty -Name "mod_id" -PropertiesPath $GradlePropertiesPath
}
if ([string]::IsNullOrWhiteSpace($ModId)) {
    throw "Could not resolve mod_id from gradle.properties."
}

if ([string]::IsNullOrWhiteSpace($ModsDir)) {
    if (-not [string]::IsNullOrWhiteSpace($env:CODEX_MINECRAFT_MODS_DIR)) {
        $ModsDir = $env:CODEX_MINECRAFT_MODS_DIR
    }
    else {
        $ModsDir = $DefaultModsDir
    }
}

if (-not $SkipBuild) {
    & (Join-Path $RepoRoot "gradlew.bat") clean build
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed with exit code $LASTEXITCODE."
    }
}

$BuildDir = Resolve-BuildDirectory -ExplicitBuildDir $BuildDir
$ModsDir = Resolve-SafeDirectory -Path $ModsDir -ExpectedLeaf "mods"
$ReportPath = Join-Path $RepoRoot "build\install-report.json"

$excludedNamePattern = '(?i)(sources|javadoc|dev|plain|test|tests|api)'
$runtimeCandidates = @(
    Get-ChildItem -LiteralPath $BuildDir -Filter "*.jar" -File |
        Where-Object { $_.BaseName -notmatch $excludedNamePattern } |
        ForEach-Object {
            $metadata = Get-JarMetadata -JarPath $_.FullName
            [pscustomobject]@{
                file = $_
                metadata = $metadata
            }
        } |
        Where-Object { $null -ne $_.metadata -and $_.metadata.modId -eq $ModId }
)

if ($runtimeCandidates.Count -ne 1) {
    $candidateNames = $runtimeCandidates | ForEach-Object { $_.file.FullName }
    throw "Expected exactly one runtime jar for mod id '$ModId' in '$BuildDir'; found $($runtimeCandidates.Count): $($candidateNames -join ', ')"
}

$sourceJar = $runtimeCandidates[0].file
$sourceMetadata = $runtimeCandidates[0].metadata

$matchedInstalled = @()
Get-ChildItem -LiteralPath $ModsDir -Filter "*.jar" -File | ForEach-Object {
    $jar = $_
    $matchedBy = @()
    if ($jar.Name -like "$ModId*.jar" -or $jar.Name -like "immersive-ego-civitas*.jar") {
        $matchedBy += "filename"
    }

    $metadata = $null
    try {
        $metadata = Get-JarMetadata -JarPath $jar.FullName
        if ($null -ne $metadata -and $metadata.modId -eq $ModId) {
            $matchedBy += "mod metadata"
        }
    }
    catch {
        $metadata = $null
    }

    if ($matchedBy.Count -gt 0) {
        $matchedInstalled += [pscustomobject]@{
            path = $jar.FullName
            reason = ($matchedBy -join ", ")
            metadata = $metadata
        }
    }
}

Write-Host "Matched installed jars for removal because they identify as '$ModId':"
if ($matchedInstalled.Count -eq 0) {
    Write-Host "  none"
}
else {
    $matchedInstalled | ForEach-Object { Write-Host "  $($_.path) [$($_.reason)]" }
}

$deleted = @()
$previousVersions = @()
foreach ($match in $matchedInstalled) {
    if ($null -ne $match.metadata -and -not [string]::IsNullOrWhiteSpace($match.metadata.version)) {
        $previousVersions += $match.metadata.version
    }
    Remove-Item -LiteralPath $match.path
    $deleted += $match.path
}

$targetJar = Join-Path $ModsDir $sourceJar.Name
Copy-Item -LiteralPath $sourceJar.FullName -Destination $targetJar -Force

$sourceInfo = Get-Item -LiteralPath $sourceJar.FullName
$targetInfo = Get-Item -LiteralPath $targetJar
$sourceHash = Get-Sha256 -Path $sourceJar.FullName
$targetHash = Get-Sha256 -Path $targetJar
$installedMetadata = Get-JarMetadata -JarPath $targetJar

$remaining = @()
Get-ChildItem -LiteralPath $ModsDir -Filter "*.jar" -File | ForEach-Object {
    try {
        $metadata = Get-JarMetadata -JarPath $_.FullName
        if ($null -ne $metadata -and $metadata.modId -eq $ModId) {
            $remaining += $_.FullName
        }
    }
    catch {
    }
}

$report = [pscustomobject]@{
    modId = $ModId
    modName = $sourceMetadata.displayName
    previousVersion = if ($previousVersions.Count -gt 0) { ($previousVersions -join ", ") } else { $null }
    installedVersion = $sourceMetadata.version
    buildDir = $BuildDir
    modsDir = $ModsDir
    sourceJar = $sourceJar.FullName
    installedJar = $targetJar
    sourceSize = $sourceInfo.Length
    installedSize = $targetInfo.Length
    sourceSha256 = $sourceHash
    installedSha256 = $targetHash
    hashMatch = ($sourceHash -eq $targetHash)
    metadata = $installedMetadata
    deletedOldJars = $deleted
    remainingJarsForMod = $remaining
    remainingInstalledJarCount = $remaining.Count
    onlyInstalledJarRemains = ($remaining.Count -eq 1)
}

$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $ReportPath -Encoding UTF8

if (-not $report.hashMatch) {
    throw "Installed jar hash does not match source jar."
}
if (-not $report.onlyInstalledJarRemains) {
    throw "Expected exactly one installed jar for '$ModId'; found $($report.remainingInstalledJarCount)."
}
if ($installedMetadata.modId -ne $ModId -or $installedMetadata.version -ne $sourceMetadata.version) {
    throw "Installed jar metadata did not match source metadata."
}

$report | ConvertTo-Json -Depth 8
