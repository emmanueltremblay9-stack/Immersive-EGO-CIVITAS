param(
    [string]$ModsDir = "",
    [string]$CacheDir = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$LogPath = Join-Path $RepoRoot "build\gametest-smoke.log"
$Gradle = Join-Path $RepoRoot "gradlew.bat"

New-Item -ItemType Directory -Force -Path (Join-Path $RepoRoot "build") | Out-Null

$stageArgs = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", (Join-Path $PSScriptRoot "stage-local-runtime-deps.ps1"))
if (-not [string]::IsNullOrWhiteSpace($ModsDir)) {
    $stageArgs += @("-ModsDir", $ModsDir)
}
if (-not [string]::IsNullOrWhiteSpace($CacheDir)) {
    $stageArgs += @("-CacheDir", $CacheDir)
}

& pwsh @stageArgs | Out-Host
if ($LASTEXITCODE -ne 0) {
    throw "Local runtime dependency staging failed with exit code $LASTEXITCODE."
}

& $Gradle --no-daemon runGameTestServer 2>&1 | Tee-Object -FilePath $LogPath
$gradleExitCode = $LASTEXITCODE
if ($gradleExitCode -ne 0) {
    throw "runGameTestServer failed with exit code $gradleExitCode. See $LogPath."
}

$log = Get-Content -LiteralPath $LogPath -Raw
$failurePatterns = @(
    "Crash report saved",
    "Failed to start the minecraft server",
    "ModLoadingException",
    "NoClassDefFoundError",
    "ClassNotFoundException",
    "has failed to load correctly",
    "Game test server crashed",
    "GameTest failed",
    "There were failed tests"
)

$matchedFailures = @()
foreach ($pattern in $failurePatterns) {
    if ($log.Contains($pattern)) {
        $matchedFailures += $pattern
    }
}

if ($matchedFailures.Count -gt 0) {
    throw "runGameTestServer log contains failure markers: $($matchedFailures -join ', '). See $LogPath."
}

$successMarkers = @(
    "required tests passed",
    "pinned runtime dependency check passed"
)
$missingSuccessMarkers = @()
foreach ($marker in $successMarkers) {
    if (-not $log.Contains($marker)) {
        $missingSuccessMarkers += $marker
    }
}

if ($missingSuccessMarkers.Count -gt 0) {
    throw "runGameTestServer log did not contain required success markers: $($missingSuccessMarkers -join ', '). See $LogPath."
}

[pscustomobject]@{
    task = "runGameTestServer"
    exitCode = $gradleExitCode
    logPath = $LogPath
    failureMarkers = $matchedFailures
    successMarkers = $successMarkers
    result = "passed"
} | ConvertTo-Json -Depth 4
