# Script para parar o Tomcat 10

$TomcatHome = Join-Path $PSScriptRoot "server\apache-tomcat-10.1.43"
$env:CATALINA_HOME = $TomcatHome

Write-Host "Parando Tomcat..."

# Tenta usar o script de shutdown do Tomcat
$shutdownScript = Join-Path $TomcatHome "bin\shutdown.bat"
if (Test-Path $shutdownScript) {
    & $shutdownScript
    Write-Host "Comando shutdown.bat executado."
    Start-Sleep -Seconds 3
} else {
    Write-Host "shutdown.bat não encontrado."
}

# Garante que todos os processos Java do Tomcat sejam finalizados
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcesses) {
    foreach ($proc in $javaProcesses) {
        Write-Host "Finalizando processo Java (PID: $($proc.Id))..."
        Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
    }
    Write-Host "Todos os processos Java finalizados."
} else {
    Write-Host "Nenhum processo Java do Tomcat encontrado em execução."
}

Write-Host "Tomcat parado."
