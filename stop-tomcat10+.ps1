# Script para parar o Tomcat 10+ (Windows/PowerShell)
# Uso: pwsh ./stop-tomcat10+.ps1

$tomcatHome = "server\apache-tomcat-10.1.43"
$shutdown = "$tomcatHome\bin\shutdown.bat"

if (!(Test-Path $shutdown)) {
    Write-Host "Arquivo shutdown.bat não encontrado em $shutdown. Verifique o caminho do Tomcat."
    exit 1
}

Write-Host "Parando o Tomcat 10+ ..."
Push-Location "$tomcatHome\bin"
$output = cmd /c shutdown.bat 2>&1
Pop-Location

Write-Host $output

if ($output -match "Tomcat stopped" -or $output -match "Shutdown completed") {
    Write-Host "Tomcat parado com sucesso."
} elseif ($output -match "CATALINA_HOME") {
    Write-Host "Erro: CATALINA_HOME não está definido corretamente. Execute o script a partir do diretório do projeto e verifique a instalação do Tomcat."
    exit 1
} else {
    Write-Host "Falha ao parar o Tomcat. Veja a saída acima e verifique os logs em server/apache-tomcat-10.1.43/logs/catalina.out."
    exit 1
}