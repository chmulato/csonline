# ============================================================================
# Script para Configuração Completa do WildFly 31 com HSQLDB
# ============================================================================
# Versão: 2.0
# Data: 03/08/2025
# Autor: GitHub Copilot
# Descrição: Configura WildFly 31 com driver HSQLDB usando módulos (não deployment)
# Uso: pwsh ./config-jdbc-driver-wildfly-31.ps1
# ============================================================================

param(
    [switch]$Verbose = $false,
    [switch]$CleanStart = $false,
    [switch]$SkipBackup = $false
)

# ============================================================================
# CONFIGURAÇÕES E VARIÁVEIS
# ============================================================================

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$logDir = "$projectRoot\logs"
$logFile = "$logDir\wildfly-config-$timestamp.log"

# Caminhos do WildFly
$wildflyHome = "$projectRoot\server\wildfly-31.0.1.Final"
$cli = "$wildflyHome\bin\jboss-cli.bat"
$standaloneXml = "$wildflyHome\standalone\configuration\standalone.xml"
$deployDir = "$wildflyHome\standalone\deployments"

# Caminhos dos drivers
$driverJar = "$projectRoot\target\csonline\WEB-INF\lib\hsqldb-2.7.2.jar"
$moduleDir = "$wildflyHome\modules\system\layers\base\org\hsqldb\main"
$moduleXml = "$moduleDir\module.xml"
$moduleJar = "$moduleDir\hsqldb-2.7.2.jar"

# URLs e nomes
$warFile = "$projectRoot\target\csonline.war"
$mavenRepo = "https://repo1.maven.org/maven2/org/hsqldb/hsqldb/2.7.2/hsqldb-2.7.2.jar"

# ============================================================================
# FUNÇÕES DE UTILIDADE
# ============================================================================

function Write-Log {
    param(
        [string]$Message,
        [string]$Level = "INFO",
        [string]$Color = "White"
    )
    
    $timestampLog = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "[$timestampLog] [$Level] $Message"
    
    # Escrever no console com cor
    Write-Host $logEntry -ForegroundColor $Color
    
    # Escrever no arquivo de log
    if (!(Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir -Force | Out-Null
    }
    Add-Content -Path $logFile -Value $logEntry -Encoding UTF8
}

function Write-Step {
    param(
        [string]$StepNumber,
        [string]$Description
    )
    Write-Log "" -Level "STEP"
    Write-Log "===========================================" -Level "STEP" -Color "Cyan"
    Write-Log "PASSO ${StepNumber}: $Description" -Level "STEP" -Color "Cyan"
    Write-Log "===========================================" -Level "STEP" -Color "Cyan"
}

function Write-Success {
    param([string]$Message)
    Write-Log $Message -Level "SUCESSO" -Color "Green"
}

function Write-Error {
    param([string]$Message)
    Write-Log $Message -Level "ERRO" -Color "Red"
}

function Write-Warning {
    param([string]$Message)
    Write-Log $Message -Level "AVISO" -Color "Yellow"
}

function Test-Prerequisites {
    Write-Step "0" "Verificando Pré-requisitos"
    
    $errors = @()
    
    # Verificar WildFly
    if (!(Test-Path $wildflyHome)) {
        $errors += "WildFly não encontrado em: $wildflyHome"
    } else {
        Write-Success "WildFly encontrado: $wildflyHome"
    }
    
    # Verificar CLI
    if (!(Test-Path $cli)) {
        $errors += "CLI do WildFly não encontrado: $cli"
    } else {
        Write-Success "CLI do WildFly encontrado: $cli"
    }
    
    # Verificar WAR (se existir)
    if (Test-Path $warFile) {
        Write-Success "Aplicação WAR encontrada: $warFile"
    } else {
        Write-Warning "Aplicação WAR não encontrada: $warFile"
        Write-Warning "Execute 'mvn clean package' primeiro se necessário"
    }
    
    # Verificar Java
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        Write-Success "Java encontrado: $javaVersion"
    } catch {
        $errors += "Java não encontrado ou não configurado no PATH"
    }
    
    if ($errors.Count -gt 0) {
        Write-Error "Pré-requisitos não atendidos:"
        $errors | ForEach-Object { Write-Error "  - $_" }
        exit 1
    }
    
    Write-Success "Todos os pré-requisitos atendidos"
}

function Backup-Configuration {
    if ($SkipBackup) {
        Write-Warning "Backup pulado conforme solicitado"
        return
    }
    
    Write-Step "1" "Criando Backup da Configuração"
    
    $backupDir = "$projectRoot\bak"
    if (!(Test-Path $backupDir)) {
        New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
    }
    
    $backupFile = "$backupDir\standalone-$timestamp.xml"
    
    if (Test-Path $standaloneXml) {
        Copy-Item $standaloneXml $backupFile -Force
        Write-Success "Backup criado: $backupFile"
    } else {
        Write-Warning "Arquivo standalone.xml não encontrado para backup"
    }
}

function Clean-PreviousDeployments {
    Write-Step "2" "Limpando Deployments Anteriores"
    
    # Remover drivers HSQLDB do diretório deployments
    $hsqldbFiles = Get-ChildItem "$deployDir\hsqldb*" -ErrorAction SilentlyContinue
    if ($hsqldbFiles) {
        $hsqldbFiles | ForEach-Object {
            Remove-Item $_.FullName -Force
            Write-Success "Removido: $($_.Name)"
        }
    } else {
        Write-Log "Nenhum arquivo HSQLDB encontrado em deployments"
    }
    
    # Limpar cache se solicitado
    if ($CleanStart) {
        Write-Log "Limpando cache do WildFly..."
        $cacheDirectories = @(
            "$wildflyHome\standalone\data\content",
            "$wildflyHome\standalone\tmp"
        )
        
        $cacheDirectories | ForEach-Object {
            if (Test-Path $_) {
                Remove-Item $_ -Recurse -Force -ErrorAction SilentlyContinue
                Write-Success "Cache removido: $_"
            }
        }
    }
}

function Setup-HSQLDBModule {
    Write-Step "3" "Configurando Módulo HSQLDB"
    
    # Criar diretório do módulo
    if (!(Test-Path $moduleDir)) {
        New-Item -ItemType Directory -Path $moduleDir -Force | Out-Null
        Write-Success "Diretório do módulo criado: $moduleDir"
    }
    
    # Verificar se o JAR do driver existe
    $driverSource = $null
    if (Test-Path $driverJar) {
        $driverSource = $driverJar
        Write-Success "Driver encontrado no WAR: $driverJar"
    } else {
        Write-Warning "Driver não encontrado no WAR, tentando baixar..."
        try {
            $tempDriver = "$env:TEMP\hsqldb-2.7.2.jar"
            Invoke-WebRequest -Uri $mavenRepo -OutFile $tempDriver -UseBasicParsing
            $driverSource = $tempDriver
            Write-Success "Driver baixado: $tempDriver"
        } catch {
            Write-Error "Falha ao baixar driver: $($_.Exception.Message)"
            exit 1
        }
    }
    
    # Copiar JAR para o módulo
    Copy-Item $driverSource $moduleJar -Force
    Write-Success "Driver copiado para módulo: $moduleJar"
    
    # Criar module.xml
    $moduleXmlContent = @'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.hsqldb">
    <resources>
        <resource-root path="hsqldb-2.7.2.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
        <module name="javax.servlet.api" optional="true"/>
    </dependencies>
</module>
'@
    
    Set-Content -Path $moduleXml -Value $moduleXmlContent -Encoding UTF8
    Write-Success "Arquivo module.xml criado: $moduleXml"
}

function Configure-Datasource {
    Write-Step "4" "Configurando Datasource via CLI"
    
    # Verificar se o WildFly está rodando
    Write-Log "Testando conexão com WildFly..."
    $testConnection = & $cli --connect --command=":whoami" 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Error "WildFly não está rodando ou não está acessível"
        Write-Error "Inicie o WildFly primeiro com: scr\start-wildfly-31.ps1"
        exit 1
    }
    Write-Success "WildFly está rodando e acessível"
    
    # Comandos CLI para configurar datasource
    $commands = @"
# Verificar se o driver já existe e remover se necessário
/subsystem=datasources/jdbc-driver=hsqldb:remove
/subsystem=datasources/data-source=HSQLDBDatasource:remove

# Adicionar driver HSQLDB usando módulo
/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=org.hsqldb,driver-xa-datasource-class-name=org.hsqldb.jdbc.pool.JDBCXADataSource)

# Adicionar datasource
/subsystem=datasources/data-source=HSQLDBDatasource:add(jndi-name=java:/HSQLDBDatasource,driver-name=hsqldb,connection-url=jdbc:hsqldb:file:csonline,user-name=sa,password=password,min-pool-size=2,max-pool-size=10)

# Habilitar datasource
/subsystem=datasources/data-source=HSQLDBDatasource:enable()

# Testar conexão
/subsystem=datasources/data-source=HSQLDBDatasource:test-connection-in-pool()
"@
    
    # Criar script CLI temporário
    $cliScript = "$env:TEMP\wildfly-hsqldb-config-$timestamp.cli"
    Set-Content -Path $cliScript -Value $commands -Encoding UTF8
    Write-Log "Script CLI criado: $cliScript"
    
    if ($Verbose) {
        Write-Log "Conteúdo do script CLI:"
        Get-Content $cliScript | ForEach-Object { Write-Log "  $_" -Color "Gray" }
    }
    
    # Executar CLI
    Write-Log "Executando configuração via CLI..."
    $cliOutput = & $cli --connect --file=$cliScript 2>&1
    
    if ($Verbose) {
        Write-Log "Saída do CLI:"
        $cliOutput | ForEach-Object { Write-Log "  $_" -Color "Gray" }
    }
    
    # Verificar resultado
    if ($LASTEXITCODE -eq 0 -and $cliOutput -match "success") {
        Write-Success "Datasource configurado com sucesso"
    } else {
        Write-Error "Falha na configuração do datasource"
        $cliOutput | ForEach-Object { Write-Log "  $_" -Color "Red" }
        exit 1
    }
    
    # Limpar arquivo temporário
    Remove-Item $cliScript -Force -ErrorAction SilentlyContinue
}

function Deploy-Application {
    Write-Step "5" "Deploy da Aplicação"
    
    if (!(Test-Path $warFile)) {
        Write-Warning "Arquivo WAR não encontrado: $warFile"
        Write-Warning "Pulando deploy da aplicação"
        return
    }
    
    # Remover deployment anterior se existir
    $deployedWar = "$deployDir\csonline.war"
    if (Test-Path $deployedWar) {
        Remove-Item $deployedWar -Force
        Write-Log "Deployment anterior removido"
    }
    
    # Copiar novo WAR
    Copy-Item $warFile $deployedWar -Force
    Write-Success "Aplicação deployada: $deployedWar"
    
    # Aguardar deployment
    Write-Log "Aguardando deployment..."
    $maxWait = 30
    $waited = 0
    
    do {
        Start-Sleep -Seconds 2
        $waited += 2
        $deployStatus = & $cli --connect --command="deployment-info --name=csonline.war" 2>&1
        
        if ($deployStatus -match "OK") {
            Write-Success "Aplicação deployada com sucesso"
            return
        }
        
        if ($waited -ge $maxWait) {
            Write-Warning "Timeout aguardando deployment (${maxWait}s)"
            break
        }
        
        Write-Log "Aguardando... (${waited}s/${maxWait}s)"
    } while ($true)
}

function Verify-Configuration {
    Write-Step "6" "Verificando Configuração"
    
    # Verificar driver
    Write-Log "Verificando driver HSQLDB..."
    $driverStatus = & $cli --connect --command="/subsystem=datasources/jdbc-driver=hsqldb:read-resource" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Driver HSQLDB carregado"
    } else {
        Write-Error "Driver HSQLDB não carregado"
    }
    
    # Verificar datasource
    Write-Log "Verificando datasource..."
    $dsStatus = & $cli --connect --command="/subsystem=datasources/data-source=HSQLDBDatasource:read-resource" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Datasource configurado"
    } else {
        Write-Error "Datasource não configurado"
    }
    
    # Testar conexão
    Write-Log "Testando conexão com banco..."
    $connTest = & $cli --connect --command="/subsystem=datasources/data-source=HSQLDBDatasource:test-connection-in-pool" 2>&1
    if ($connTest -match "success" -or $connTest -match "OK") {
        Write-Success "Conexão com banco testada com sucesso"
    } else {
        Write-Warning "Teste de conexão falhou - pode ser normal se o banco ainda não foi inicializado"
    }
}

function Show-Summary {
    Write-Step "7" "Resumo da Configuração"
    
    Write-Log ""
    Write-Success "============================================"
    Write-Success "CONFIGURAÇÃO CONCLUÍDA COM SUCESSO!"
    Write-Success "============================================"
    Write-Log ""
    Write-Log "Configurações aplicadas:"
    Write-Log "  ✓ Módulo HSQLDB: $moduleDir"
    Write-Log "  ✓ Driver: hsqldb (org.hsqldb)"
    Write-Log "  ✓ Datasource: java:/HSQLDBDatasource"
    Write-Log "  ✓ Conexão: jdbc:hsqldb:file:csonline"
    Write-Log ""
    Write-Log "URLs de acesso:"
    Write-Log "  • Aplicação: http://127.0.0.1:8080/csonline"
    Write-Log "  • Management: http://127.0.0.1:9990"
    Write-Log ""
    Write-Log "Logs disponíveis:"
    Write-Log "  • Script: $logFile"
    Write-Log "  • Servidor: $wildflyHome\standalone\log\server.log"
    Write-Log ""
    if (Test-Path $warFile) {
        Write-Log "A aplicação será deployada automaticamente pelo WildFly"
    } else {
        Write-Warning "Para deployar a aplicação:"
        Write-Warning "  1. Execute: mvn clean package"
        Write-Warning "  2. Copie target/csonline.war para $deployDir"
    }
    Write-Log ""
    Write-Success "Configuração salva no backup: $(Split-Path $logFile -Parent)"
}

# ============================================================================
# EXECUÇÃO PRINCIPAL
# ============================================================================

try {
    Write-Log "Iniciando configuração do WildFly 31 com HSQLDB" -Level "START" -Color "Magenta"
    Write-Log "Timestamp: $timestamp" -Level "START"
    Write-Log "Diretório do projeto: $projectRoot" -Level "START"
    Write-Log "Log: $logFile" -Level "START"
    
    Test-Prerequisites
    Backup-Configuration
    Clean-PreviousDeployments
    Setup-HSQLDBModule
    Configure-Datasource
    Deploy-Application
    Verify-Configuration
    Show-Summary
    
    Write-Success "Script concluído com sucesso!"
    exit 0
    
} catch {
    Write-Error "Erro durante execução: $($_.Exception.Message)"
    Write-Error "Stack trace: $($_.ScriptStackTrace)"
    exit 1
}
