param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
)

$ErrorActionPreference = 'Stop'

$manifestPath = Join-Path $RepoRoot 'docs/CODE_ADAPTATION_MANIFEST.csv'
if (-not (Test-Path -LiteralPath $manifestPath)) {
    throw "Missing manifest: $manifestPath"
}

$requiredColumns = @(
    'Manifest ID',
    'Upstream Project',
    'Upstream Commit',
    'Original Path',
    'Destination Path',
    'Original Copyright',
    'License',
    'Adaptation Type',
    'Modifications',
    'Asset Included',
    'Reviewer',
    'Status'
)

$header = (Get-Content -LiteralPath $manifestPath -TotalCount 1).Split(',')
foreach ($column in $requiredColumns) {
    if ($header -notcontains $column) {
        throw "Manifest is missing required column: $column"
    }
}

$rows = Import-Csv -LiteralPath $manifestPath
$activeRows = @(
    $rows | Where-Object {
        $_.'Destination Path' -and
        $_.Status -and
        $_.Status -notin @('Template', 'Blocked')
    }
)

$manifestDestinations = @{}
foreach ($row in $activeRows) {
    $destination = $row.'Destination Path'.Replace('\', '/')
    if ($manifestDestinations.ContainsKey($destination)) {
        throw "Duplicate manifest destination path: $destination"
    }
    $manifestDestinations[$destination] = $row

    $absoluteDestination = Join-Path $RepoRoot $row.'Destination Path'
    if (-not (Test-Path -LiteralPath $absoluteDestination)) {
        throw "Manifest destination does not exist: $($row.'Destination Path')"
    }

    $content = Get-Content -LiteralPath $absoluteDestination -Raw
    foreach ($needle in @(
        'CIVITAS ADAPTED SOURCE',
        "Manifest ID: $($row.'Manifest ID')",
        "Original Project: $($row.'Upstream Project')",
        "Original Commit: $($row.'Upstream Commit')",
        "Original Path: $($row.'Original Path')"
    )) {
        if ($content -notlike "*$needle*") {
            throw "Adapted file missing source header field '$needle': $($row.'Destination Path')"
        }
    }
}

$sourceRoots = @(
    Join-Path $RepoRoot 'src/main/java',
    Join-Path $RepoRoot 'src/main/kotlin'
) | Where-Object { Test-Path -LiteralPath $_ }

foreach ($root in $sourceRoots) {
    $sourceFiles = Get-ChildItem -LiteralPath $root -Recurse -File -Include *.java,*.kt
    foreach ($file in $sourceFiles) {
        $relative = $file.FullName.Substring($RepoRoot.Length + 1).Replace('\', '/')
        $content = Get-Content -LiteralPath $file.FullName -Raw
        if ($content -like '*CIVITAS ADAPTED SOURCE*' -and -not $manifestDestinations.ContainsKey($relative)) {
            throw "Adapted source header has no manifest row: $relative"
        }
    }
}

$resourcesRoot = Join-Path $RepoRoot 'src/main/resources'
$forbiddenFragments = @(
    '/assets/minecolonies/',
    '/assets/mca/',
    '/assets/minecraft_comes_alive/',
    '/assets/modern_companions/',
    '/assets/humancompanions/',
    '/assets/basicweapons/',
    '/data/minecolonies/',
    '/data/mca/',
    '/data/minecraft_comes_alive/',
    '/data/modern_companions/',
    '/data/humancompanions/',
    '/data/basicweapons/'
)

if (Test-Path -LiteralPath $resourcesRoot) {
    $resourceFiles = Get-ChildItem -LiteralPath $resourcesRoot -Recurse -File
    foreach ($file in $resourceFiles) {
        $relative = '/' + $file.FullName.Substring($RepoRoot.Length + 1).Replace('\', '/').ToLowerInvariant()
        foreach ($fragment in $forbiddenFragments) {
            if ($relative.Contains($fragment)) {
                throw "Forbidden upstream resource namespace detected: $relative"
            }
        }
    }
}

Write-Host "Provenance validation passed."
Write-Host "Active adapted-source rows: $($activeRows.Count)"
