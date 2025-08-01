# Script para deploy do WAR no WildFly 31 (Windows/PowerShell)
# Uso: pwsh ./deploy-wildfly-31.ps1


$root = Split-Path (Split-Path -Parent $MyInvocation.MyCommand.Definition) -Parent
$wildflyDeploy = Join-Path $root "server/wildfly-31.0.1.Final/standalone/deployments"
$warFile = Join-Path $root "target/csonline.war"


Write-Host "[INFO] Verificando arquivo WAR em: $warFile"
if (!(Test-Path $warFile)) {
    Write-Host "[ERRO] Arquivo WAR não encontrado em $warFile. Execute 'mvn clean package' antes."
    exit 1
}


Write-Host "[INFO] Verificando diretório de deployments do WildFly em: $wildflyDeploy"
if (!(Test-Path $wildflyDeploy)) {
    Write-Host "[ERRO] Diretório de deployments do WildFly não encontrado: $wildflyDeploy"
    exit 1
}

Write-Host "[INFO] Copiando $warFile para $wildflyDeploy ..."
try {
    Copy-Item $warFile $wildflyDeploy -Force -ErrorAction Stop
    Write-Host "[OK] Deploy enviado! O WildFly fará o deploy automático."
    Write-Host "[INFO] Acesse: http://localhost:8080/csonline/"
    Write-Host "[INFO] Se o WAR já estava implantado, o WildFly fará o redeploy automaticamente."
} catch {
    Write-Host "[ERRO] Erro ao copiar o WAR para o WildFly: $_"
    Write-Host "[ERRO] Verifique permissões de escrita e se o WildFly está rodando."
    exit 1
}