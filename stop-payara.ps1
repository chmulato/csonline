# Script para parar o Payara Server 6 (Windows/PowerShell)
# Uso: execute este script na raiz do projeto

$payaraHome = "server\payara6"
$asadmin = "$payaraHome\bin\asadmin.bat"

if (!(Test-Path $asadmin)) {
    Write-Host "Arquivo asadmin.bat não encontrado em $asadmin. Verifique o caminho do Payara."
    exit 1
}

Write-Host "Parando o Payara Server (domain1)..."
& $asadmin stop-domain

if ($LASTEXITCODE -eq 0) {
    Write-Host "Payara Server parado com sucesso."
} else {
    Write-Host "Falha ao parar o Payara Server. Verifique os logs."
    exit 1
}
