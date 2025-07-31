# Script para configurar log customizado da aplicação no WildFly 31
# Uso: pwsh ./config-log-wildfly-31.ps1


$wildflyHome = Join-Path $PSScriptRoot 'server/wildfly-31.0.1.Final'
$cli = Join-Path $wildflyHome 'bin/jboss-cli.bat'
$logDir = Join-Path $PSScriptRoot 'logs'

Write-Host "[INFO] Configurando log da aplicação para $logDir no WildFly 31... (path relativo ao projeto)"

# 1. Cria o diretório de logs se não existir
if (!(Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
    Write-Host "[OK] Diretório de logs criado: $logDir"
} else {
    Write-Host "[OK] Diretório de logs já existe: $logDir"
}

# 2. Gera script CLI para configurar handler, path e logger
$commands = @"
/subsystem=logging/periodic-rotating-file-handler=APP_LOG:add(autoflush=true,append=true,formatter={"pattern-formatter"=>{"pattern"=>"%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"}},file={"relative-to"=>"csonline.log.dir","path"=>"app.log"},suffix=".yyyy-MM-dd")
/subsystem=logging/path=csonline.log.dir:add(path="logs")
/subsystem=logging/logger=com.caracore.cso:add(level=INFO,handlers=[APP_LOG])
"@

# 3. Executa o CLI
$cliScript = "$env:TEMP\wildfly-log.cli"
$commands | Set-Content -Path $cliScript -Encoding UTF8
Write-Host "[INFO] Executando jboss-cli para configurar logging..."
& $cli --connect --file=$cliScript
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Log customizado configurado no WildFly 31."
    Write-Host "[INFO] Reinicie o WildFly para garantir que a configuração está ativa."
} else {
    Write-Host "[ERRO] Falha ao configurar o log via CLI. Verifique a saída acima."
    exit 1
}
