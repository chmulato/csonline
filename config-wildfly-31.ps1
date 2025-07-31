# Script para configurar DataSource HSQLDB no WildFly 31 via CLI
# Uso: pwsh ./config-wildfly31-datasource.ps1

$wildflyHome = "C:\dev\csonline\server\wildfly-31.0.1.Final"
$cli = "$wildflyHome\bin\jboss-cli.bat"
$driverJar = "C:\dev\csonline\target\lib\hsqldb-2.7.2.jar" # Ajuste o caminho se necessário

# 1. Copia o driver JDBC para o WildFly (deploy)
if (!(Test-Path $driverJar)) {
    Write-Host "[ERRO] Driver JDBC não encontrado em $driverJar."
    exit 1
}
Copy-Item $driverJar "$wildflyHome\standalone\deployments\" -Force
Write-Host "[OK] Driver JDBC copiado para deployments."

# 2. Comando CLI para criar o driver e o datasource
$commands = @"
deploy $driverJar
/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=deployment.hsqldb-2.7.2.jar,driver-class-name=org.hsqldb.jdbcDriver)
/subsystem=datasources/data-source=DS_csonline:add(jndi-name=java:/jdbc/csonlineDS,driver-name=hsqldb,connection-url=jdbc:hsqldb:mem:csonline;DB_CLOSE_DELAY=-1,user-name=sa,password=,min-pool-size=2,max-pool-size=10,enabled=true)
"@

# 3. Executa o CLI
$cliScript = "$env:TEMP\wildfly-ds.cli"
$commands | Set-Content -Path $cliScript -Encoding UTF8
& $cli --connect --file=$cliScript

Write-Host "[OK] DataSource HSQLDB configurado no WildFly 31."
Write-Host "Reinicie o WildFly para garantir que o datasource está ativo."