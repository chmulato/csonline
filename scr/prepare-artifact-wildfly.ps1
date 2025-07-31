# Script para preparar o artefato WAR do projeto e copiar para o WildFly 31
# Uso: execute este script na raiz do projeto para compilar, gerar e copiar o WAR
# Aceita argumentos extras do Maven, ex: -DskipTests

Write-Host "Iniciando build do projeto..."

$mvn = "mvn"
$wildflyDeploy = "server\wildfly-31.0.1.Final\standalone\deployments"
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
    if (Test-Path $wildflyDeploy) {
        Write-Host "Copiando $warFile para $wildflyDeploy ..."
        Copy-Item $warFile $wildflyDeploy -Force
        Write-Host "WAR copiado para o WildFly. Inicie o WildFly com:"
        Write-Host "server\wildfly-31.0.1.Final\bin\standalone.bat"
        Write-Host "Acesse: http://localhost:8080/csonline/"
    } else {
        Write-Host "Diretório do WildFly não encontrado: $wildflyDeploy"
    }
} else {
    Write-Host "WAR não encontrado. Verifique se o build foi concluído corretamente."
    exit 1
}
