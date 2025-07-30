# Script para configurar e salvar a senha mestre do domínio Payara Server 6
# Uso: execute este script na raiz do projeto

$payaraHome = "server\payara6"
$asadmin = "$payaraHome\bin\asadmin.bat"
$domain = "domain1"

if (!(Test-Path $asadmin)) {
    Write-Host "Arquivo asadmin.bat não encontrado em $asadmin. Verifique o caminho do Payara."
    exit 1
}

Write-Host "Configurando senha mestre do domínio $domain..."
Write-Host "Você pode pressionar Enter para manter a senha padrão (changeit) ou digitar uma nova senha."

& $asadmin change-master-password --savemasterpassword=true $domain

if ($LASTEXITCODE -eq 0) {
    Write-Host "Senha mestre salva com sucesso! O domínio poderá ser iniciado sem prompt de senha."
} else {
    Write-Host "Falha ao configurar a senha mestre. Verifique os logs."
    exit 1
}
