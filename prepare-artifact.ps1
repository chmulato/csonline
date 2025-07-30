# Script para preparar o artefato WAR do projeto e copiar para o Tomcat 10+
# Uso: execute este script na raiz do projeto para compilar, gerar e copiar o WAR
# Aceita argumentos extras do Maven, ex: -DskipTests

Write-Host "Iniciando build do projeto..."

$mvn = "mvn"
$tomcatWebapps = "server\apache-tomcat-10.1.43\webapps"
$warFile = "target\csonline.war"

# Permite passar argumentos extras para o Maven
$extraArgs = $args -join " "

$cmd = "$mvn clean package $extraArgs"
Write-Host "Executando: $cmd"

Invoke-Expression $cmd

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build falhou. Corrija os erros antes de prosseguir."
    exit 1
}

if (Test-Path $warFile) {
    Write-Host "WAR gerado com sucesso: $warFile"
    if (Test-Path $tomcatWebapps) {
        Write-Host "Copiando $warFile para $tomcatWebapps ..."
        Copy-Item $warFile $tomcatWebapps -Force
        Write-Host "WAR copiado para o Tomcat. Inicie o Tomcat com:"
        Write-Host "server\\apache-tomcat-10.1.43\\bin\\startup.bat"
        Write-Host "Acesse: http://localhost:8080/csonline/"
    } else {
        Write-Host "Diretório do Tomcat não encontrado: $tomcatWebapps"
    }
} else {
    Write-Host "WAR não encontrado. Verifique se o build foi concluído corretamente."
    exit 1
}