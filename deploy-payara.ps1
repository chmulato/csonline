# Script para deploy do WAR no Payara Server 6
# Uso: pwsh ./deploy-payara.ps1 [dominio]

$payaraHome = "server\payara6"
$warFile = "target\csonline-1.0-SNAPSHOT.war"
$domain = if ($args.Count -ge 1) { $args[0] } else { "domain1" }
$autodeployDir = "$payaraHome\glassfish\domains\$domain\autodeploy"

if (!(Test-Path $warFile)) {
    Write-Host "Arquivo WAR não encontrado em $warFile. Execute 'mvn clean package' antes."
    exit 1
}

if (!(Test-Path $autodeployDir)) {
    Write-Host "Diretório de autodeploy não encontrado: $autodeployDir"
    exit 1
}

Write-Host "Copiando $warFile para $autodeployDir..."
Copy-Item $warFile $autodeployDir -Force
Write-Host "Deploy enviado! O Payara fará o deploy automático."
Write-Host "Acesse: http://localhost:8080/csonline-1.0-SNAPSHOT/"
