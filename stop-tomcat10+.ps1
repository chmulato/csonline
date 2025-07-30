# Script para parar o Tomcat 10+ (Windows/PowerShell)
# Uso: pwsh ./stop-tomcat10+.ps1

$tomcatHome = "server\apache-tomcat-10.1.43"
$shutdown = "$tomcatHome\bin\shutdown.bat"

if (!(Test-Path $shutdown)) {
    Write-Host "Arquivo shutdown.bat não encontrado em $shutdown. Verifique o caminho do Tomcat."
    exit 1
}

Write-Host "Parando o Tomcat 10+ ..."
& $shutdown

if ($LASTEXITCODE -eq 0) {
    Write-Host "Tomcat parado com sucesso."
} else {
    Write-Host "Falha ao parar o Tomcat. Verifique os logs."
    exit 1
}