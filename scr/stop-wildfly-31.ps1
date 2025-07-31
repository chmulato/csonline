# Script para parar o WildFly 31 (Windows/PowerShell)
# Uso: pwsh ./stop-wildfly-31.ps1

$wildflyHome = "server\wildfly-31.0.1.Final"
$shutdown = "$wildflyHome\bin\jboss-cli.bat"

if (!(Test-Path $shutdown)) {
    Write-Host "Arquivo jboss-cli.bat não encontrado em $shutdown. Verifique o caminho do WildFly."
    exit 1
}

Write-Host "[INFO] Parando o WildFly 31 ..."
Push-Location "$wildflyHome\bin"
Write-Host "[DEBUG] Executando: jboss-cli.bat --connect command=:shutdown"
$output = cmd /c "jboss-cli.bat --connect command=:shutdown" 2>&1
Pop-Location

Write-Host "[INFO] Saída do comando de shutdown do WildFly:"
Write-Host "--------------------------------------------------"
$output | ForEach-Object { Write-Host $_ }
Write-Host "--------------------------------------------------"

if ($output -match "closed" -or $output -match "shutdown" -or $output -match "Disconnected") {
    Write-Host "[OK] WildFly parado com sucesso."
} else {
    Write-Host "[ERRO] Falha ao parar o WildFly. Veja a saída acima e verifique os logs em server/wildfly-31.0.1.Final/standalone/log/server.log."
    exit 1
}