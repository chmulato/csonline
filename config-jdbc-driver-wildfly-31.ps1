# Script para configurar apenas o driver JDBC HSQLDB no WildFly 31 via CLI
# Uso: pwsh ./config-jdbc-driver-wildfly-31.ps1

$wildflyHome = "C:\dev\csonline\server\wildfly-31.0.1.Final"
$cli = "$wildflyHome\bin\jboss-cli.bat"
$driverJar = "C:\dev\csonline\target\csonline\WEB-INF\lib\hsqldb-2.7.2.jar" # Ajuste o caminho se necessário

Write-Host "[INFO] Iniciando configuração do driver JDBC HSQLDB no WildFly 31..."

# 1. Copia o driver JDBC para o WildFly (deploy)
Write-Host "[PASSO 1] Verificando driver JDBC em: $driverJar"
if (!(Test-Path $driverJar)) {
    Write-Host "[ERRO] Driver JDBC não encontrado em $driverJar."
    exit 1
}
Write-Host "[OK] Driver JDBC encontrado. Copiando para deployments..."
Copy-Item $driverJar "$wildflyHome\standalone\deployments\" -Force
Write-Host "[OK] Driver JDBC copiado para $wildflyHome\standalone\deployments."

# 2. Comando CLI para criar o driver JDBC
Write-Host "[PASSO 2] Gerando script CLI para criar driver JDBC..."
$commands = @"
deploy $driverJar
/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=deployment.hsqldb-2.7.2.jar,driver-class-name=org.hsqldb.jdbcDriver)
"@

# 3. Executa o CLI
Write-Host "[PASSO 3] Gerando script CLI temporário em: $env:TEMP\wildfly-jdbc-driver.cli"
$cliScript = "$env:TEMP\wildfly-jdbc-driver.cli"
$commands | Set-Content -Path $cliScript -Encoding UTF8
Write-Host "[DEBUG] Conteúdo do script CLI gerado:"
Get-Content $cliScript | ForEach-Object { Write-Host $_ }

Write-Host "[PASSO 4] Executando o jboss-cli para configurar o WildFly..."
$cliOutput = & $cli --connect --file=$cliScript 2>&1
Write-Host "[DEBUG] Saída do jboss-cli:" -ForegroundColor Yellow
$cliOutput | ForEach-Object { Write-Host $_ }
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Driver JDBC HSQLDB configurado no WildFly 31 com sucesso."
    Write-Host "[INFO] Reinicie o WildFly para garantir que o driver está ativo."
} else {
    Write-Host "[ERRO] Falha ao configurar o driver JDBC via CLI. Veja detalhes acima e revise o script CLI."
    exit 1
}
