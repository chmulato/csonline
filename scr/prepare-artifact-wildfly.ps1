# Script para preparar o artefato WAR do projeto e copiar para o WildFly 31
# Uso: execute este script na raiz do projeto para compilar, gerar e copiar o WAR
# Aceita argumentos extras do Maven, ex: -DskipTests


Write-Host "[INFO] Iniciando build do projeto..."

# Caminhos relativos à raiz do projeto
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
$mvn = "mvn"
$wildflyDeploy = Join-Path $root "server/wildfly-31.0.1.Final/standalone/deployments"
$warFile = Join-Path $root "target/csonline.war"

# Permite passar argumentos extras para o Maven
$extraArgs = $args -join " "

$cmd = "$mvn clean package $extraArgs"
Write-Host "[INFO] Executando: $cmd"

Invoke-Expression $cmd
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERRO] Build falhou. Corrija os erros antes de prosseguir."
    exit 1
}

Write-Host "[INFO] Verificando WAR gerado em: $warFile"
if (Test-Path $warFile) {
    Write-Host "[OK] WAR gerado com sucesso: $warFile"
    Write-Host "[INFO] Verificando diretório do WildFly em: $wildflyDeploy"
    if (Test-Path $wildflyDeploy) {
        Write-Host "[INFO] Copiando $warFile para $wildflyDeploy ..."
        try {
            Copy-Item $warFile $wildflyDeploy -Force -ErrorAction Stop
            Write-Host "[OK] WAR copiado para o WildFly. Inicie o WildFly com:"
            Write-Host "[INFO] $wildflyDeploy/../../bin/standalone.bat"
            Write-Host "[INFO] Acesse: http://localhost:8080/csonline/"
        } catch {
            Write-Host "[ERRO] Erro ao copiar o WAR para o WildFly: $_"
            Write-Host "[ERRO] Verifique permissões de escrita e se o WildFly está rodando."
            exit 1
        }
    } else {
        Write-Host "[ERRO] Diretório do WildFly não encontrado: $wildflyDeploy"
        exit 1
    }
} else {
    Write-Host "[ERRO] WAR não encontrado. Verifique se o build foi concluído corretamente."
    exit 1
}
