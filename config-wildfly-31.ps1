# Script para configurar DataSource HSQLDB no WildFly 31 via CLI
# Uso: pwsh ./config-wildfly31-datasource.ps1

$wildflyHome = "C:\dev\csonline\server\wildfly-31.0.1.Final"
$cli = "$wildflyHome\bin\jboss-cli.bat"
$driverJar = "C:\dev\csonline\target\csonline\WEB-INF\lib\hsqldb-2.7.2.jar" # Ajuste o caminho se necessário

Write-Host "[INFO] Iniciando configuração do DataSource HSQLDB no WildFly 31..."

# 1. Copia o driver JDBC para o WildFly (deploy)
Write-Host "[PASSO 1] Verificando driver JDBC em: $driverJar"
if (!(Test-Path $driverJar)) {
    Write-Host "[ERRO] Driver JDBC não encontrado em $driverJar."
    exit 1
}
Write-Host "[OK] Driver JDBC encontrado. Copiando para deployments..."
Copy-Item $driverJar "$wildflyHome\standalone\deployments\" -Force
Write-Host "[OK] Driver JDBC copiado para $wildflyHome\standalone\deployments."


# 2. Opção: configurar apenas o driver JDBC ou driver + datasource
param(
    [switch]$SomenteDriver
)

if ($SomenteDriver) {
    Write-Host "[PASSO 2] Gerando script CLI para criar apenas o driver JDBC..."
    $commands = @"
deploy $driverJar
/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=deployment.hsqldb-2.7.2.jar,driver-class-name=org.hsqldb.jdbcDriver)
"@
    $acao = "driver JDBC"
} else {
    Write-Host "[PASSO 2] Gerando script CLI para criar driver e datasource..."
    $commands = @"
deploy $driverJar
/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=deployment.hsqldb-2.7.2.jar,driver-class-name=org.hsqldb.jdbcDriver)
/subsystem=datasources/data-source=DS_csonline:add(jndi-name=java:/jdbc/csonlineDS,driver-name=hsqldb,connection-url=jdbc:hsqldb:mem:csonline;DB_CLOSE_DELAY=-1,user-name=sa,password=,min-pool-size=2,max-pool-size=10,enabled=true)
"@
    $acao = "driver JDBC e datasource"
}

# 3. Executa o CLI
Write-Host "[PASSO 3] Gerando script CLI temporário em: $env:TEMP\wildfly-ds.cli"
$cliScript = "$env:TEMP\wildfly-ds.cli"
$commands | Set-Content -Path $cliScript -Encoding UTF8
Write-Host "[DEBUG] Conteúdo do script CLI gerado:"
Get-Content $cliScript | ForEach-Object { Write-Host $_ }

Write-Host "[PASSO 4] Executando o jboss-cli para configurar o WildFly..."
$cliOutput = & $cli --connect --file=$cliScript 2>&1
Write-Host "[DEBUG] Saída do jboss-cli:" -ForegroundColor Yellow
$cliOutput | ForEach-Object { Write-Host $_ }
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] DataSource HSQLDB configurado no WildFly 31 com sucesso."
    Write-Host "[INFO] Reinicie o WildFly para garantir que o datasource está ativo."
} else {
    Write-Host "[ERRO] Falha ao configurar o datasource via CLI. Veja detalhes acima e revise o script CLI."
    exit 1
}