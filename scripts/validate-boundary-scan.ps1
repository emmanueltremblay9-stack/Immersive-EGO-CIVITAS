param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
)

$ErrorActionPreference = 'Stop'

$sourceRoots = @(
    (Join-Path $RepoRoot 'src/main/java'),
    (Join-Path $RepoRoot 'src/test/java')
) | Where-Object { Test-Path -LiteralPath $_ }

$importRules = @(
    [pscustomobject]@{
        Name = 'Immersive EGO implementation import'
        Pattern = '^\s*import\s+com\.oblixorprime\.immersiveego\.(?!civitas(?:\.|;))'
    },
    [pscustomobject]@{
        Name = 'MineColonies direct import'
        Pattern = '^\s*import\s+com\.minecolonies\.'
    },
    [pscustomobject]@{
        Name = 'MCA Reborn direct import'
        Pattern = '^\s*import\s+net\.conczin\.mca\.'
    },
    [pscustomobject]@{
        Name = 'Modern Companions direct import'
        Pattern = '(?i)^\s*import\s+.*modern[_\.]?companions'
    }
)

$gradleRules = @(
    [pscustomobject]@{
        Name = 'Compile-scope dependency on upstream runtime mod'
        Pattern = '(?i)^\s*(api|implementation|compileOnly|compileOnlyApi|testImplementation)\b.*(immersive[_-]?ego|minecolonies|minecraft[-_]?comes[-_]?alive|\bmca\b|modern[_-]?companions)'
    }
)

$violations = New-Object System.Collections.Generic.List[object]
$scannedFiles = 0

foreach ($root in $sourceRoots) {
    $files = Get-ChildItem -LiteralPath $root -Recurse -File |
        Where-Object { $_.Extension -in @('.java', '.kt') }
    foreach ($file in $files) {
        $scannedFiles++
        $relative = $file.FullName.Substring($RepoRoot.Length + 1).Replace('\', '/')
        $lineNumber = 0
        foreach ($line in Get-Content -LiteralPath $file.FullName) {
            $lineNumber++
            foreach ($rule in $importRules) {
                if ($line -match $rule.Pattern) {
                    $violations.Add([pscustomobject]@{
                        Path = $relative
                        Line = $lineNumber
                        Rule = $rule.Name
                        Text = $line.Trim()
                    })
                }
            }
        }
    }
}

$buildFiles = @(
    Join-Path $RepoRoot 'build.gradle',
    Join-Path $RepoRoot 'build.gradle.kts'
) | Where-Object { Test-Path -LiteralPath $_ }

foreach ($file in $buildFiles) {
    $scannedFiles++
    $relative = $file.Substring($RepoRoot.Length + 1).Replace('\', '/')
    $lineNumber = 0
    foreach ($line in Get-Content -LiteralPath $file) {
        $lineNumber++
        foreach ($rule in $gradleRules) {
            if ($line -match $rule.Pattern) {
                $violations.Add([pscustomobject]@{
                    Path = $relative
                    Line = $lineNumber
                    Rule = $rule.Name
                    Text = $line.Trim()
                })
            }
        }
    }
}

$result = if ($violations.Count -eq 0) { 'passed' } else { 'failed' }
$relativeSourceRoots = @($sourceRoots | ForEach-Object { $_.Substring($RepoRoot.Length + 1).Replace('\', '/') })
$violationReport = $violations.ToArray()
$report = [ordered]@{
    scannedFiles = $scannedFiles
    sourceRoots = $relativeSourceRoots
    result = $result
    violations = $violationReport
}

$buildDir = Join-Path $RepoRoot 'build'
New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
$reportPath = Join-Path $buildDir 'boundary-scan-report.json'
$report | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $reportPath -Encoding UTF8

if ($violations.Count -gt 0) {
    $summary = $violations | ForEach-Object { "$($_.Path):$($_.Line): $($_.Rule): $($_.Text)" }
    throw "Boundary scan failed:`n$($summary -join "`n")"
}

Write-Host "Boundary scan passed."
Write-Host "Scanned files: $scannedFiles"
Write-Host "Report: $reportPath"
