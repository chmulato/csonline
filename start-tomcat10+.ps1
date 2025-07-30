# Script para iniciar o Tomcat 10+ (Windows/PowerShell)
# Uso: pwsh ./start-tomcat10+.ps1

$tomcatHome = "server\apache-tomcat-10.1.43"
$startup = "$tomcatHome\bin\startup.bat"

if (!(Test-Path $startup)) {
    Write-Host "Arquivo startup.bat não encontrado em $startup. Verifique o caminho do Tomcat."
    exit 1
}

Write-Host "Iniciando o Tomcat 10+ ..."
Push-Location "$tomcatHome\bin"
$output = cmd /c startup.bat 2>&1
Pop-Location

Write-Host $output

if ($output -match "Tomcat started" -or $output -match "Server startup in") {
    Write-Host "Tomcat iniciado com sucesso!"
    Write-Host "Acesse: http://localhost:8080/"
    Write-Host "Gerenciamento (se habilitado): http://localhost:8080/manager/html"
} elseif ($output -match "CATALINA_HOME") {
    Write-Host "Erro: CATALINA_HOME não está definido corretamente. Execute o script a partir do diretório do projeto e verifique a instalação do Tomcat."
    exit 1
} else {
    Write-Host "Falha ao iniciar o Tomcat. Veja a saída acima e verifique os logs em server/apache-tomcat-10.1.43/logs/catalina.out."
    exit 1
}