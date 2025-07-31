param(
    [switch]$SomenteDriver
)

# 0. Checagem e configuração da VM Java
Write-Host "[INFO] Checando ambiente Java (JAVA_HOME e java -version)..."
if (-not $env:JAVA_HOME) {
    # Tenta detectar o Java em locais comuns (Windows)
    $provaveis = @(
        "$env:ProgramFiles\Java\jdk-11.0.20",
        "$env:ProgramFiles\Java\jdk-17.0.0",
        "$env:ProgramFiles\Java\jdk-21.0.0"
    )
    foreach ($j in $provaveis) {
        if (Test-Path $j) {
            $env:JAVA_HOME = $j
            $env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
            Write-Host "[OK] JAVA_HOME configurado para: $env:JAVA_HOME"
            break
        }
    }
}
if (-not $env:JAVA_HOME) {
    Write-Host "[ERRO] JAVA_HOME não está configurado. Configure manualmente antes de prosseguir."
    exit 1
}
$javaVersion = & java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERRO] Java não encontrado no PATH. Verifique a instalação do Java."
    exit 1
}
Write-Host "[OK] Java detectado:"
$javaVersion | ForEach-Object { Write-Host $_ }

# Script para configurar DataSource HSQLDB no WildFly 31 via CLI
# Uso: pwsh ./config-wildfly31-datasource.ps1


$root = Split-Path (Split-Path -Parent $MyInvocation.MyCommand.Definition) -Parent
$wildflyHome = Join-Path $root "server/wildfly-31.0.1.Final"
$cli = Join-Path $wildflyHome "bin/jboss-cli.bat"
$driverJar = Join-Path $root "target/csonline/WEB-INF/lib/hsqldb-2.7.2.jar" # Ajuste o caminho se necessário

Write-Host "[INFO] Iniciando configuração do DataSource HSQLDB no WildFly 31..."

# 1. Copia o driver JDBC para o WildFly (deploy)
Write-Host "[PASSO 1] Verificando driver JDBC em: $driverJar"
if (!(Test-Path $driverJar)) {
    Write-Host "[ERRO] Driver JDBC não encontrado em $driverJar."
    exit 1
}
Write-Host "[OK] Driver JDBC encontrado. Copiando para deployments..."
Copy-Item $driverJar "$wildflyHome\standalone\deployments\" -Force
if ($?) {
    Write-Host "[OK] Driver JDBC copiado para $wildflyHome\standalone\deployments."
} else {
    Write-Host "[ERRO] Falha ao copiar o driver JDBC."
    exit 1
}




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
if ($?) {
    Write-Host "[OK] Script CLI gerado com sucesso."
} else {
    Write-Host "[ERRO] Falha ao gerar o script CLI."
    exit 1
}
Write-Host "[DEBUG] Conteúdo do script CLI gerado:"
Get-Content $cliScript | ForEach-Object { Write-Host $_ }

Write-Host "[PASSO 4] Executando o jboss-cli para configurar o WildFly..."
$cliOutput = & $cli --connect --file=$cliScript 2>&1
Write-Host "[INFO] Saída do jboss-cli:"
Write-Host "--------------------------------------------------"
$cliOutput | ForEach-Object { Write-Host $_ }
Write-Host "--------------------------------------------------"
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] $acao configurado no WildFly 31 com sucesso."
    Write-Host "[INFO] Reinicie o WildFly para garantir que a configuração está ativa."
} else {
    Write-Host "[ERRO] Falha ao configurar via CLI. Veja detalhes acima e revise o script CLI."
    exit 1
}