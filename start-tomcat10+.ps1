# Script para iniciar o Tomcat 10+ (Windows/PowerShell)
# Uso: pwsh ./start-tomcat10+.ps1

$tomcatHome = "server\apache-tomcat-10.1.43"
$startup = "$tomcatHome\bin\startup.bat"

if (!(Test-Path $startup)) {
    Write-Host "Arquivo startup.bat não encontrado em $startup. Verifique o caminho do Tomcat."
    exit 1
}

Write-Host "Iniciando o Tomcat 10+ ..."
& $startup

if ($LASTEXITCODE -eq 0) {
    Write-Host "Tomcat iniciado com sucesso!"
    Write-Host "Acesse: http://localhost:8080/"
    Write-Host "Gerenciamento (se habilitado): http://localhost:8080/manager/html"
} else {
    Write-Host "Falha ao iniciar o Tomcat. Verifique os logs."
    exit 1
}