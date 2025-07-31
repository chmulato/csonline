# Script para deploy do WAR no WildFly 31 (Windows/PowerShell)
# Uso: pwsh ./deploy-wildfly-31.ps1

$wildflyDeploy = "server\wildfly-31.0.1.Final\standalone\deployments"
$warFile = "target\csonline.war"

if (!(Test-Path $warFile)) {
    Write-Host "Arquivo WAR não encontrado em $warFile. Execute 'mvn clean package' antes."
    exit 1
}

if (!(Test-Path $wildflyDeploy)) {
    Write-Host "Diretório de deployments do WildFly não encontrado: $wildflyDeploy"
    exit 1
}

Write-Host "Copiando $warFile para $wildflyDeploy ..."
try {
    Copy-Item $warFile $wildflyDeploy -Force -ErrorAction Stop
    Write-Host "Deploy enviado! O WildFly fará o deploy automático."
    Write-Host "Acesse: http://localhost:8080/csonline/"
    Write-Host "Se o WAR já estava implantado, o WildFly fará o redeploy automaticamente."
} catch {
    Write-Host "Erro ao copiar o WAR para o WildFly: $_"
    Write-Host "Verifique permissões de escrita e se o WildFly está rodando."
    exit 1
}