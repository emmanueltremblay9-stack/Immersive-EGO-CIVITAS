param(
    [string]$ModsDir = "",
    [string]$CacheDir = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
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

function Resolve-SafeModsDirectory {
    param([Parameter(Mandatory = $true)][string]$Path)

    if ([string]::IsNullOrWhiteSpace($Path)) {
        throw "ModsDir cannot be blank."
    }

    $fullPath = [System.IO.Path]::GetFullPath($Path)
    if (Test-Path -LiteralPath $fullPath) {
        $item = Get-Item -LiteralPath $fullPath
        if (-not $item.PSIsContainer) {
            throw "ModsDir exists but is not a directory: $fullPath"
        }
        return $item.FullName
    }

    $leaf = Split-Path -Path $fullPath -Leaf
    if ($leaf -ne "mods") {
        throw "ModsDir does not exist and final segment is not 'mods'; refusing to create: $fullPath"
    }

    $parent = Split-Path -Path $fullPath -Parent
    if ([string]::IsNullOrWhiteSpace($parent) -or -not (Test-Path -LiteralPath $parent -PathType Container)) {
        throw "ModsDir parent does not exist; refusing to create full tree: $parent"
    }

    New-Item -ItemType Directory -Path $fullPath | Out-Null
    return (Resolve-Path -LiteralPath $fullPath).Path
}

function Get-Sha256 {
    param([Parameter(Mandatory = $true)][string]$Path)
    return (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash.ToLowerInvariant()
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

function Get-VerifiedArtifact {
    param(
        [Parameter(Mandatory = $true)][pscustomobject]$Artifact,
        [Parameter(Mandatory = $true)][string]$DestinationDir
    )

    $target = Join-Path $DestinationDir $Artifact.FileName
    if (Test-Path -LiteralPath $target) {
        $existingHash = Get-Sha256 -Path $target
        if ($existingHash -eq $Artifact.Sha256) {
            return $target
        }
        Remove-Item -LiteralPath $target
    }

    $downloadPath = "$target.download"
    if (Test-Path -LiteralPath $downloadPath) {
        Remove-Item -LiteralPath $downloadPath
    }

    Invoke-WebRequest -UseBasicParsing -Uri $Artifact.Url -OutFile $downloadPath
    $actualHash = Get-Sha256 -Path $downloadPath
    if ($actualHash -ne $Artifact.Sha256) {
        Remove-Item -LiteralPath $downloadPath -ErrorAction SilentlyContinue
        throw "SHA-256 mismatch for $($Artifact.FileName). Expected $($Artifact.Sha256), got $actualHash."
    }
    Move-Item -LiteralPath $downloadPath -Destination $target
    return $target
}

Import-LocalEnv -Path $LocalEnvPath

if ([string]::IsNullOrWhiteSpace($ModsDir)) {
    if (-not [string]::IsNullOrWhiteSpace($env:CODEX_MINECRAFT_MODS_DIR)) {
        $ModsDir = $env:CODEX_MINECRAFT_MODS_DIR
    }
    else {
        $ModsDir = $DefaultModsDir
    }
}

if ([string]::IsNullOrWhiteSpace($CacheDir)) {
    $CacheDir = Join-Path $RepoRoot "build\runtime-deps"
}

$ModsDir = Resolve-SafeModsDirectory -Path $ModsDir
$CacheDir = [System.IO.Path]::GetFullPath($CacheDir)
New-Item -ItemType Directory -Force -Path $CacheDir | Out-Null

$artifacts = @(
    [pscustomobject]@{
        ModId = "minecolonies"
        FileName = "minecolonies-1.1.1319-1.21.1.jar"
        Url = "https://www.curseforge.com/api/v1/mods/245506/files/8138370/download"
        Sha256 = "ab97c0eec45c3f2539ec31428e3c836bb30ba1c537af0c86f5ab4e38754f6a4d"
    },
    [pscustomobject]@{
        ModId = "structurize"
        FileName = "structurize-1.0.810-1.21.1-snapshot.jar"
        Url = "https://www.curseforge.com/api/v1/mods/298744/files/7643353/download"
        Sha256 = "7379ee90fde4abaeda6857d954e7cfa1ddb07a526bd425d3f7f23ae47d81ed14"
    },
    [pscustomobject]@{
        ModId = "blockui"
        FileName = "blockui-1.0.199-1.21.1-snapshot.jar"
        Url = "https://www.curseforge.com/api/v1/mods/522992/files/6367809/download"
        Sha256 = "238b2e9fda99620318dfa9197754b3f803fff73f7f711f1065ac900d2e4ee9ef"
    },
    [pscustomobject]@{
        ModId = "domum_ornamentum"
        FileName = "domum-ornamentum-1.0.223-snapshot-main.jar"
        Url = "https://www.curseforge.com/api/v1/mods/527361/files/7231908/download"
        Sha256 = "e208671d86050bd49e48b9524dfa12f1f642efefc8fa31eff88ef24709d31f83"
    },
    [pscustomobject]@{
        ModId = "multipiston"
        FileName = "multipiston-1.2.51-1.21.1-snapshot.jar"
        Url = "https://www.curseforge.com/api/v1/mods/303278/files/5783614/download"
        Sha256 = "d0eafb395fdf1e6962b5cb127f26488124dc7d5008361b164576423820f3782e"
    },
    [pscustomobject]@{
        ModId = "mca"
        FileName = "mca-neoforge-7.7.11+1.21.1.jar"
        Url = "https://github.com/Luke100000/minecraft-comes-alive/releases/download/7.7.11%2B1.21.1/mca-neoforge-7.7.11%2B1.21.1.jar"
        Sha256 = "8d569c0ae870e1fe098a7270f240780aa588f328512f64ffa0a6d74a886fc59f"
    },
    [pscustomobject]@{
        ModId = "modern_companions"
        FileName = "ModernCompanions-1.21.1-2.0-NeoForge.jar"
        Url = "https://www.curseforge.com/api/v1/mods/1391597/files/7902593/download"
        Sha256 = "fb7085db4f1f99f7fcd845470685ff9f86348a03db5727f06f74ce685e4ab312"
    }
)

$reportRows = @()
foreach ($artifact in $artifacts) {
    $sourcePath = Get-VerifiedArtifact -Artifact $artifact -DestinationDir $CacheDir
    $sourceHash = Get-Sha256 -Path $sourcePath
    $sourceMetadata = Get-JarMetadata -JarPath $sourcePath
    if ($null -eq $sourceMetadata -or $sourceMetadata.modId -ne $artifact.ModId) {
        throw "Artifact $($artifact.FileName) metadata did not match expected mod id $($artifact.ModId)."
    }

    $matchedInstalled = @()
    Get-ChildItem -LiteralPath $ModsDir -Filter "*.jar" -File | ForEach-Object {
        $metadata = $null
        try {
            $metadata = Get-JarMetadata -JarPath $_.FullName
        }
        catch {
            $metadata = $null
        }
        if ($null -ne $metadata -and $metadata.modId -eq $artifact.ModId) {
            $matchedInstalled += [pscustomobject]@{
                path = $_.FullName
                version = $metadata.version
            }
        }
    }

    Write-Host "Matched installed jars for removal because they identify as '$($artifact.ModId)':"
    if ($matchedInstalled.Count -eq 0) {
        Write-Host "  none"
    }
    else {
        $matchedInstalled | ForEach-Object { Write-Host "  $($_.path)" }
    }

    $deleted = @()
    foreach ($match in $matchedInstalled) {
        Remove-Item -LiteralPath $match.path
        $deleted += $match.path
    }

    $targetPath = Join-Path $ModsDir $artifact.FileName
    Copy-Item -LiteralPath $sourcePath -Destination $targetPath -Force
    $targetHash = Get-Sha256 -Path $targetPath
    $targetMetadata = Get-JarMetadata -JarPath $targetPath

    $remaining = @()
    Get-ChildItem -LiteralPath $ModsDir -Filter "*.jar" -File | ForEach-Object {
        try {
            $metadata = Get-JarMetadata -JarPath $_.FullName
            if ($null -ne $metadata -and $metadata.modId -eq $artifact.ModId) {
                $remaining += $_.FullName
            }
        }
        catch {
        }
    }

    $row = [pscustomobject]@{
        modId = $artifact.ModId
        version = $targetMetadata.version
        displayName = $targetMetadata.displayName
        sourceJar = $sourcePath
        installedJar = $targetPath
        sourceSha256 = $sourceHash
        installedSha256 = $targetHash
        hashMatch = ($sourceHash -eq $targetHash)
        deletedOldJars = $deleted
        remainingJarsForMod = $remaining
        remainingInstalledJarCount = $remaining.Count
    }

    if (-not $row.hashMatch) {
        throw "Installed hash mismatch for $($artifact.ModId)."
    }
    if ($row.remainingInstalledJarCount -ne 1) {
        throw "Expected exactly one installed jar for $($artifact.ModId); found $($row.remainingInstalledJarCount)."
    }

    $reportRows += $row
}

$report = [pscustomobject]@{
    modsDir = $ModsDir
    cacheDir = $CacheDir
    artifacts = $reportRows
    allHashesMatch = -not ($reportRows | Where-Object { -not $_.hashMatch })
    allSingleInstalled = -not ($reportRows | Where-Object { $_.remainingInstalledJarCount -ne 1 })
}

$reportPath = Join-Path $RepoRoot "build\runtime-deps-report.json"
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$report | ConvertTo-Json -Depth 8
