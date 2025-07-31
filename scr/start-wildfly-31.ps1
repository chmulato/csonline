# Script para iniciar o WildFly 31 (Windows/PowerShell)
# Uso: pwsh ./start-wildfly-31.ps1

$wildflyHome = "server\wildfly-31.0.1.Final"
$startup = "$wildflyHome\bin\standalone.bat"

if (!(Test-Path $startup)) {
    Write-Host "Arquivo standalone.bat não encontrado em $startup. Verifique o caminho do WildFly."
    exit 1
}

Write-Host "[INFO] Iniciando o WildFly 31 ..."
Push-Location "$wildflyHome\bin"
Write-Host "[DEBUG] Executando: $startup"
$output = cmd /c standalone.bat 2>&1
Pop-Location

Write-Host "[INFO] Saída do WildFly (standalone.bat):"
Write-Host "--------------------------------------------------"
$output | ForEach-Object { Write-Host $_ }
Write-Host "--------------------------------------------------"

if ($output -match "WildFly Full" -or $output -match "started in") {
    Write-Host "[OK] WildFly iniciado com sucesso!"
    Write-Host "[INFO] Acesse: http://localhost:8080/"
    Write-Host "[INFO] Gerenciamento (console): http://localhost:9990/console"
} else {
    Write-Host "[ERRO] Falha ao iniciar o WildFly. Veja a saída acima e verifique os logs em server/wildfly-31.0.1.Final/standalone/log/server.log."
    exit 1
}