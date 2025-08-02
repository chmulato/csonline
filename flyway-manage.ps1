# Script PowerShell para execução de comandos Flyway
# Este script facilita a execução de comandos do Flyway via Maven
# Autor: CSOnline Team

# Verifica se o Maven está instalado
function Test-MavenInstalled {
    try {
        $mvnVersion = mvn --version
        Write-Host "Maven encontrado: $($mvnVersion[0])" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "Maven não encontrado. Por favor, instale o Maven antes de usar este script." -ForegroundColor Red
        return $false
    }
}

# Verifica se o container Docker do HSQLDB está rodando
function Test-DockerHsqldbRunning {
    try {
        $container = docker ps --filter "name=hsqldb" --format "{{.Names}}"
        if ($container -eq "hsqldb") {
            Write-Host "Container HSQLDB encontrado e rodando." -ForegroundColor Green
            return $true
        }
        else {
            Write-Host "Container HSQLDB não está rodando. Por favor, inicie o container antes de usar este script." -ForegroundColor Red
            Write-Host "Execute: docker-compose up -d" -ForegroundColor Yellow
            return $false
        }
    }
    catch {
        Write-Host "Erro ao verificar status do Docker. Verifique se o Docker está instalado e rodando." -ForegroundColor Red
        return $false
    }
}

# Executa um comando do Flyway via Maven
function Invoke-FlywayCommand {
    param (
        [Parameter(Mandatory=$true)]
        [string]$Command,
        
        [Parameter(Mandatory=$false)]
        [string]$AdditionalParams = ""
    )
    
    if (-not (Test-MavenInstalled)) {
        return
    }
    
    if (-not (Test-DockerHsqldbRunning)) {
        return
    }
    
    Write-Host "Executando Flyway $Command..." -ForegroundColor Cyan
    
    if ($AdditionalParams) {
        $fullCommand = "mvn flyway:$Command $AdditionalParams"
    }
    else {
        $fullCommand = "mvn flyway:$Command"
    }
    
    Write-Host "Comando: $fullCommand" -ForegroundColor Gray
    Invoke-Expression $fullCommand
}

# Menu principal
function Show-Menu {
    Clear-Host
    Write-Host "=== CSOnline - Gerenciador de Migrações Flyway ===" -ForegroundColor Cyan
    Write-Host "1. Mostrar informações das migrações (info)"
    Write-Host "2. Executar migrações pendentes (migrate)"
    Write-Host "3. Limpar banco de dados (clean) - CUIDADO!"
    Write-Host "4. Validar scripts de migração (validate)"
    Write-Host "5. Reparar tabela de histórico (repair)"
    Write-Host "6. Visualizar scripts SQL de migração"
    Write-Host "7. Executar todas as migrações com log detalhado"
    Write-Host "0. Sair"
    Write-Host ""
    
    $choice = Read-Host "Escolha uma opção"
    
    switch ($choice) {
        "1" { Invoke-FlywayCommand "info"; Pause; Show-Menu }
        "2" { Invoke-FlywayCommand "migrate"; Pause; Show-Menu }
        "3" { 
            $confirm = Read-Host "ATENÇÃO: Isso irá remover TODOS os dados do banco! Digite 'sim' para confirmar"
            if ($confirm -eq "sim") {
                Invoke-FlywayCommand "clean"
            }
            else {
                Write-Host "Operação cancelada." -ForegroundColor Yellow
            }
            Pause
            Show-Menu
        }
        "4" { Invoke-FlywayCommand "validate"; Pause; Show-Menu }
        "5" { Invoke-FlywayCommand "repair"; Pause; Show-Menu }
        "6" { 
            Write-Host "Scripts de migração disponíveis:" -ForegroundColor Cyan
            Get-ChildItem -Path "src\main\resources\db\migration" -Filter "*.sql" | ForEach-Object {
                Write-Host "- $($_.Name)" -ForegroundColor Yellow
                $content = Get-Content $_.FullName -Raw
                Write-Host $content.Substring(0, [Math]::Min(200, $content.Length)) -ForegroundColor Gray
                Write-Host "... (truncado)" -ForegroundColor Gray
                Write-Host ""
            }
            Pause
            Show-Menu
        }
        "7" { Invoke-FlywayCommand "migrate" "-Dflyway.logLevel=DEBUG"; Pause; Show-Menu }
        "0" { return }
        default { Write-Host "Opção inválida. Tente novamente." -ForegroundColor Red; Pause; Show-Menu }
    }
}

# Inicia o script
Show-Menu
