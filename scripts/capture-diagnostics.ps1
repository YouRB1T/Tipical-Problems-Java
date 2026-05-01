param(
    [Parameter(Mandatory = $true)]
    [int] $ProcessId,

    [string] $OutputDir = "diagnostics"
)

$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$jcmdCommand = Get-Command jcmd -ErrorAction SilentlyContinue
if ($null -eq $jcmdCommand -and $env:JAVA_HOME) {
    $candidate = Join-Path $env:JAVA_HOME "bin/jcmd.exe"
    if (Test-Path $candidate) {
        $jcmdCommand = [pscustomobject]@{ Source = $candidate }
    }
}
if ($null -eq $jcmdCommand) {
    $defaultJdk = "C:\Program Files\Java\jdk-21\bin\jcmd.exe"
    if (Test-Path $defaultJdk) {
        $jcmdCommand = [pscustomobject]@{ Source = $defaultJdk }
    }
}
if ($null -eq $jcmdCommand) {
    throw "jcmd was not found. Install JDK 21 or add <JDK_HOME>/bin to PATH."
}

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$threadDumpPath = Join-Path $OutputDir "thread-dump-$timestamp.txt"
$heapDumpPath = Join-Path $OutputDir "heap-dump-$timestamp.hprof"
$threadReportPath = Join-Path $OutputDir "thread-report-$timestamp.csv"

& $jcmdCommand.Source $ProcessId Thread.print -e -l | Out-File -FilePath $threadDumpPath -Encoding utf8
& $jcmdCommand.Source $ProcessId GC.heap_dump $heapDumpPath | Out-Null

$rows = foreach ($line in Get-Content $threadDumpPath) {
    if (
        $line -match '^"(?<name>[^"]+)".*cpu=(?<cpu>[0-9.,]+)ms elapsed=(?<elapsed>[0-9.,]+)s' -or
        $line -match '^"(?<name>[^"]+)".*elapsed=(?<elapsed>[0-9.,]+)s.*cpu=(?<cpu>[0-9.,]+)ms'
    ) {
        $elapsed = [double]($Matches.elapsed -replace ',', '.')
        $cpu = [double]($Matches.cpu -replace ',', '.')
        $loadPercent = if ($elapsed -gt 0) { ($cpu / ($elapsed * 1000)) * 100 } else { 0 }
        [pscustomobject]@{
            ThreadName = $Matches.name
            ElapsedSeconds = [math]::Round($elapsed, 3)
            CpuMs = [math]::Round($cpu, 3)
            LoadPercent = [math]::Round($loadPercent, 3)
        }
    }
}

$rows |
    Sort-Object LoadPercent -Descending |
    Select-Object -First 10 |
    Export-Csv -Path $threadReportPath -NoTypeInformation -Encoding utf8

Write-Host "Thread dump: $threadDumpPath"
Write-Host "Heap dump: $heapDumpPath"
Write-Host "Thread report: $threadReportPath"
