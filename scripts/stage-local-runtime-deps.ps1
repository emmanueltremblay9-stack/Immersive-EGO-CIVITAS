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

    throw "ModsDir does not exist: $fullPath"
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

$requiredModIds = @(
    "immersive_ego",
    "minecolonies",
    "mca",
    "modern_companions",
    "structurize",
    "blockui",
    "domum_ornamentum",
    "multipiston",
    "apothic_attributes",
    "placebo",
    "marieslib",
    "waystones",
    "balm"
)

$reportRows = @()
foreach ($modId in $requiredModIds) {
    $matches = @()
    Get-ChildItem -LiteralPath $ModsDir -Filter "*.jar" -File | ForEach-Object {
        $metadata = $null
        try {
            $metadata = Get-JarMetadata -JarPath $_.FullName
        }
        catch {
            $metadata = $null
        }
        if ($null -ne $metadata -and $metadata.modId -eq $modId) {
            $matches += [pscustomobject]@{
                file = $_
                metadata = $metadata
            }
        }
    }

    if ($matches.Count -ne 1) {
        $candidateNames = $matches | ForEach-Object { $_.file.FullName }
        throw "Expected exactly one LAB jar for mod id '$modId'; found $($matches.Count): $($candidateNames -join ', ')"
    }

    $sourceJar = $matches[0].file
    $targetPath = Join-Path $CacheDir $sourceJar.Name
    Copy-Item -LiteralPath $sourceJar.FullName -Destination $targetPath -Force

    $sourceHash = Get-Sha256 -Path $sourceJar.FullName
    $targetHash = Get-Sha256 -Path $targetPath
    $targetMetadata = Get-JarMetadata -JarPath $targetPath

    $row = [pscustomobject]@{
        modId = $modId
        version = $targetMetadata.version
        displayName = $targetMetadata.displayName
        sourceJar = $sourceJar.FullName
        stagedJar = $targetPath
        sourceSha256 = $sourceHash
        stagedSha256 = $targetHash
        hashMatch = ($sourceHash -eq $targetHash)
        matchesInModsDir = $matches.Count
    }

    if (-not $row.hashMatch) {
        throw "Staged hash mismatch for $modId."
    }
    if ($targetMetadata.modId -ne $modId) {
        throw "Staged jar metadata mismatch for $modId."
    }

    $reportRows += $row
}

$report = [pscustomobject]@{
    modsDir = $ModsDir
    cacheDir = $CacheDir
    requiredModIds = $requiredModIds
    stagedArtifacts = $reportRows
    allHashesMatch = -not ($reportRows | Where-Object { -not $_.hashMatch })
    allSingleInModsDir = -not ($reportRows | Where-Object { $_.matchesInModsDir -ne 1 })
}

$reportPath = Join-Path $RepoRoot "build\local-runtime-stage-report.json"
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportPath -Encoding UTF8
$report | ConvertTo-Json -Depth 8
