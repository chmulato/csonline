# Script para deploy do WAR no Tomcat 10+ (Windows/PowerShell)
# Uso: pwsh ./deploy-tomcat10+.ps1

$tomcatWebapps = "server\apache-tomcat-10.1.43\webapps"
$warFile = "target\csonline-1.0-SNAPSHOT.war"

if (!(Test-Path $warFile)) {
    Write-Host "Arquivo WAR não encontrado em $warFile. Execute 'mvn clean package' antes."
    exit 1
}

if (!(Test-Path $tomcatWebapps)) {
    Write-Host "Diretório webapps do Tomcat não encontrado: $tomcatWebapps"
    exit 1
}

Write-Host "Copiando $warFile para $tomcatWebapps ..."
try {
    Copy-Item $warFile $tomcatWebapps -Force -ErrorAction Stop
    Write-Host "Deploy enviado! O Tomcat fará o deploy automático."
    Write-Host "Acesse: http://localhost:8080/csonline-1.0-SNAPSHOT/"
    Write-Host "Se o WAR já estava implantado, o Tomcat fará o redeploy automaticamente."
} catch {
    Write-Host "Erro ao copiar o WAR para o Tomcat: $_"
    Write-Host "Verifique permissões de escrita e se o Tomcat está rodando."
    exit 1
}