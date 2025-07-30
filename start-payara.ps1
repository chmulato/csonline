# Script para iniciar o Payara Server 6 (Windows/PowerShell)
# Uso: pwsh ./start-payara.ps1 [dominio]

$payaraHome = "server\payara6"
$asadmin = "$payaraHome\bin\asadmin.bat"
$domain = if ($args.Count -ge 1) { $args[0] } else { "domain1" }

if (!(Test-Path $asadmin)) {
    Write-Host "Arquivo asadmin.bat não encontrado em $asadmin. Verifique o caminho do Payara."
    exit 1
}

Write-Host "Iniciando o Payara Server ($domain)..."
& $asadmin start-domain $domain

if ($LASTEXITCODE -eq 0) {
    Write-Host "Payara Server iniciado com sucesso!"
    Write-Host "Acesse: http://localhost:8080/"
    Write-Host "Console de administração: http://localhost:4848/"
} else {
    Write-Host "Falha ao iniciar o Payara Server. Verifique os logs."
    exit 1
}
