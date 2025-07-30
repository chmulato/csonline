# Script para preparar o artefato WAR do projeto
# Uso: execute este script na raiz do projeto para compilar e gerar o WAR
# Aceita argumentos extras do Maven, ex: -DskipTests

Write-Host "Iniciando build do projeto..."

$mvn = "mvn"

# Permite passar argumentos extras para o Maven
$extraArgs = $args -join " "

$cmd = "$mvn clean package $extraArgs"
Write-Host "Executando: $cmd"

Invoke-Expression $cmd

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build falhou. Corrija os erros antes de prosseguir."
    exit 1
}

$warFile = "target\csonline-1.0-SNAPSHOT.war"
if (Test-Path $warFile) {
    Write-Host "WAR gerado com sucesso: $warFile"
} else {
    Write-Host "WAR não encontrado. Verifique se o build foi concluído corretamente."
    exit 1
}
