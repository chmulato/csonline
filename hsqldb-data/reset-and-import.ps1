# ============================================================
# SCRIPT COMPLETO: RESET E IMPORTAÇÃO DO BANCO CSONLINE
# ============================================================
# Descrição: Apaga completamente o banco e reimporta dados
# Versão: 3.0
# Data: 2025-08-07
# Autor: CSOnline Team
# ============================================================

param(
    [string]$ExportFile = "database-export.sql",
    [string]$ServerUrl = "http://localhost:8080/csonline/api",
    [string]$AdminLogin = "empresa",
    [string]$AdminPassword = "empresa123",
    [switch]$Verbose,
    [switch]$DryRun,
    [switch]$NoBackup,
    [switch]$ForceReset,
    [switch]$Help
)

if ($Help) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT COMPLETO: RESET E IMPORTAÇÃO - CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`nO QUE ESTE SCRIPT FAZ:" -ForegroundColor Yellow
    Write-Host "1. 🔍 Verifica conexão com o servidor" -ForegroundColor White
    Write-Host "2. 💾 Faz backup dos dados atuais (opcional)" -ForegroundColor White
    Write-Host "3. 🗑️  Apaga TODOS os dados do banco" -ForegroundColor White
    Write-Host "4. 📥 Reimporta dados do arquivo SQL" -ForegroundColor White
    Write-Host "5. ✅ Valida a importação" -ForegroundColor White
    
    Write-Host "`nUSO:" -ForegroundColor Yellow
    Write-Host ".\reset-and-import.ps1                     # Execução padrão" -ForegroundColor White
    Write-Host ".\reset-and-import.ps1 -DryRun             # Simula sem fazer mudanças" -ForegroundColor White
    Write-Host ".\reset-and-import.ps1 -NoBackup           # Não faz backup antes" -ForegroundColor White
    Write-Host ".\reset-and-import.ps1 -ForceReset         # Não pede confirmação" -ForegroundColor White
    Write-Host ".\reset-and-import.ps1 -Verbose            # Logs detalhados" -ForegroundColor White
    
    Write-Host "`n⚠️  ATENÇÃO:" -ForegroundColor Red
    Write-Host "Este script APAGA TODOS OS DADOS do banco!" -ForegroundColor Red
    Write-Host "Use com cuidado em ambiente de produção!" -ForegroundColor Red
    
    exit 0
}

# Função para log com timestamp
function Write-Log {
    param($Message, $Color = "White")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor $Color
}

# Função para verificar conexão
function Test-ServerConnection {
    try {
        $response = Invoke-WebRequest -Uri "$ServerUrl/../" -Method HEAD -TimeoutSec 5
        return $true
    } catch {
        return $false
    }
}

# Função para obter token JWT
function Get-JWTToken {
    param($Login, $Password)
    try {
        $loginData = @{
            login = $Login
            password = $Password
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "$ServerUrl/login" -Method POST -ContentType "application/json" -Body $loginData
        return $response.token
    } catch {
        throw "Erro ao obter token JWT: $($_.Exception.Message)"
    }
}

# Função para criar headers
function Get-AuthHeaders {
    param($Token)
    return @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
}

# Função para obter contagem de registros
function Get-RecordCount {
    param($Endpoint, $Headers)
    try {
        $data = Invoke-RestMethod -Uri "$ServerUrl/$Endpoint" -Method GET -Headers $Headers
        if ($data -is [array]) {
            return $data.Count
        } else {
            return if ($data) { 1 } else { 0 }
        }
    } catch {
        return 0
    }
}

# Função para apagar registros
function Clear-AllData {
    param($Headers)
    
    Write-Log "🗑️ INICIANDO LIMPEZA COMPLETA DO BANCO..." -Color Red
    
    $endpoints = @("deliveries", "teams", "sms", "customers", "couriers")
    $totalDeleted = 0
    
    foreach ($endpoint in $endpoints) {
        try {
            $records = Invoke-RestMethod -Uri "$ServerUrl/$endpoint" -Method GET -Headers $Headers
            if ($records -and $records.Count -gt 0) {
                Write-Log "Limpando $endpoint ($($records.Count) registros)..." -Color Yellow
                
                foreach ($record in $records) {
                    if (-not $DryRun) {
                        try {
                            Invoke-RestMethod -Uri "$ServerUrl/$endpoint/$($record.id)" -Method DELETE -Headers $Headers | Out-Null
                            $totalDeleted++
                            if ($Verbose) {
                                Write-Log "  ✅ Deletado $endpoint ID $($record.id)" -Color Gray
                            }
                        } catch {
                            Write-Log "  ❌ Erro ao deletar $endpoint ID $($record.id): $($_.Exception.Message)" -Color Red
                        }
                    } else {
                        Write-Log "  [DRY-RUN] Deletaria $endpoint ID $($record.id)" -Color Gray
                        $totalDeleted++
                    }
                }
            } else {
                Write-Log "$endpoint já está vazio" -Color Gray
            }
        } catch {
            Write-Log "Erro ao acessar $endpoint`: $($_.Exception.Message)" -Color Red
        }
    }
    
    Write-Log "Total de registros deletados: $totalDeleted" -Color Red
    return $totalDeleted
}

# Função para executar backup
function Create-Backup {
    param($BackupFile)
    
    Write-Log "💾 Criando backup dos dados atuais..." -Color Cyan
    
    if ($DryRun) {
        Write-Log "[DRY-RUN] Criaria backup: $BackupFile" -Color Gray
        return $true
    }
    
    try {
        & ".\export-database.ps1" -OutputFile $BackupFile -Verbose:$Verbose
        if (Test-Path $BackupFile) {
            Write-Log "Backup criado com sucesso: $BackupFile" -Color Green
            return $true
        } else {
            Write-Log "Erro: Arquivo de backup não foi criado" -Color Red
            return $false
        }
    } catch {
        Write-Log "Erro ao criar backup: $($_.Exception.Message)" -Color Red
        return $false
    }
}

# Função para importar dados
function Import-Data {
    param($ImportFile, $Headers)
    
    Write-Log "📥 Importando dados do arquivo: $ImportFile" -Color Cyan
    
    if ($DryRun) {
        Write-Log "[DRY-RUN] Executaria importação avançada" -Color Gray
        return @{ success = $true; message = "Simulação de importação" }
    }
    
    try {
        # Usar o script de importação avançada
        $importArgs = @("-ExportFile", $ImportFile, "-ServerUrl", $ServerUrl, "-AdminLogin", $AdminLogin, "-AdminPassword", $AdminPassword)
        if ($Verbose) { $importArgs += "-Verbose" }
        if ($DryRun) { $importArgs += "-DryRun" }
        
        & ".\import-database-advanced.ps1" @importArgs
        
        return @{ success = $true; message = "Importação concluída" }
    } catch {
        return @{ success = $false; message = $_.Exception.Message }
    }
}

# Função para validar importação
function Validate-Import {
    param($Headers)
    
    Write-Log "🔍 Validando dados importados..." -Color Cyan
    
    $validation = @{
        users = Get-RecordCount -Endpoint "users" -Headers $Headers
        couriers = Get-RecordCount -Endpoint "couriers" -Headers $Headers
        customers = Get-RecordCount -Endpoint "customers" -Headers $Headers
        deliveries = Get-RecordCount -Endpoint "deliveries" -Headers $Headers
        teams = Get-RecordCount -Endpoint "teams" -Headers $Headers
        sms = Get-RecordCount -Endpoint "sms" -Headers $Headers
    }
    
    $total = $validation.Values | Measure-Object -Sum | Select-Object -ExpandProperty Sum
    
    Write-Log "Dados encontrados após importação:" -Color Cyan
    foreach ($key in $validation.Keys) {
        $color = if ($validation[$key] -gt 0) { "Green" } else { "Yellow" }
        Write-Log "  $key`: $($validation[$key])" -Color $color
    }
    
    Write-Log "Total de registros: $total" -Color Cyan
    return $validation
}

# Script principal
try {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "RESET COMPLETO E IMPORTAÇÃO - CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    if ($DryRun) {
        Write-Host "🧪 MODO DRY-RUN ATIVADO - SIMULAÇÃO APENAS" -ForegroundColor Yellow
    }
    
    Write-Log "Configuração:" -Color Gray
    Write-Log "  Arquivo: $ExportFile" -Color Gray
    Write-Log "  Servidor: $ServerUrl" -Color Gray
    Write-Log "  Login: $AdminLogin" -Color Gray
    Write-Log "  Backup: $(if ($NoBackup) { 'Não' } else { 'Sim' })" -Color Gray
    
    # 1. Verificar conexão
    Write-Log "🔍 Verificando conexão com o servidor..." -Color Yellow
    if (-not (Test-ServerConnection)) {
        throw "Servidor não está acessível em $ServerUrl"
    }
    Write-Log "Servidor está rodando" -Color Green
    
    # 2. Verificar arquivo de exportação
    if (-not (Test-Path $ExportFile)) {
        throw "Arquivo de exportação não encontrado: $ExportFile"
    }
    Write-Log "Arquivo de exportação encontrado: $ExportFile" -Color Green
    
    # 3. Obter autenticação
    Write-Log "🔑 Obtendo autenticação..." -Color Yellow
    if (-not $DryRun) {
        $token = Get-JWTToken -Login $AdminLogin -Password $AdminPassword
        $headers = Get-AuthHeaders -Token $token
        Write-Log "Token JWT obtido com sucesso" -Color Green
    } else {
        $headers = @{}
    }
    
    # 4. Verificar dados atuais
    Write-Log "📊 Verificando dados atuais..." -Color Yellow
    $currentData = if (-not $DryRun) { 
        Validate-Import -Headers $headers 
    } else { 
        @{ users = 8; couriers = 2; customers = 2; deliveries = 2; teams = 2; sms = 2 } 
    }
    $currentTotal = $currentData.Values | Measure-Object -Sum | Select-Object -ExpandProperty Sum
    
    # 5. Confirmar operação
    if (-not $ForceReset -and $currentTotal -gt 0) {
        Write-Host "`n⚠️  ATENÇÃO: Esta operação vai APAGAR $currentTotal registros!" -ForegroundColor Red
        Write-Host "Dados atuais no banco:" -ForegroundColor Yellow
        foreach ($key in $currentData.Keys) {
            if ($currentData[$key] -gt 0) {
                Write-Host "  $key`: $($currentData[$key])" -ForegroundColor Gray
            }
        }
        
        if (-not $DryRun) {
            $confirm = Read-Host "`nDeseja continuar? (S/N)"
            if ($confirm -notmatch '^[SsYy]') {
                Write-Log "Operação cancelada pelo usuário" -Color Yellow
                exit 0
            }
        }
    }
    
    # 6. Criar backup
    if (-not $NoBackup) {
        $backupFile = "backup-before-reset-$(Get-Date -Format 'yyyyMMdd_HHmmss').sql"
        $backupSuccess = Create-Backup -BackupFile $backupFile
        if (-not $backupSuccess -and -not $DryRun) {
            $continueWithoutBackup = Read-Host "Backup falhou. Continuar mesmo assim? (S/N)"
            if ($continueWithoutBackup -notmatch '^[SsYy]') {
                Write-Log "Operação cancelada devido ao erro no backup" -Color Red
                exit 1
            }
        }
    }
    
    # 7. Limpar dados
    $deletedCount = Clear-AllData -Headers $headers
    
    # 8. Importar dados
    $importResult = Import-Data -ImportFile $ExportFile -Headers $headers
    if (-not $importResult.success) {
        throw "Erro na importação: $($importResult.message)"
    }
    
    # 9. Validar importação
    if (-not $DryRun) {
        Start-Sleep -Seconds 2  # Aguardar para dados serem persistidos
    }
    $finalData = if (-not $DryRun) { 
        Validate-Import -Headers $headers 
    } else { 
        @{ users = 8; couriers = 2; customers = 2; deliveries = 2; teams = 2; sms = 2 } 
    }
    $finalTotal = $finalData.Values | Measure-Object -Sum | Select-Object -ExpandProperty Sum
    
    # 10. Relatório final
    Write-Host "`n============================================================" -ForegroundColor Cyan
    Write-Host "RELATÓRIO FINAL" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "📊 ESTATÍSTICAS:" -ForegroundColor Yellow
    Write-Host "  Registros originais: $currentTotal" -ForegroundColor Gray
    Write-Host "  Registros deletados: $deletedCount" -ForegroundColor Red
    Write-Host "  Registros importados: $finalTotal" -ForegroundColor Green
    
    if (-not $NoBackup) {
        Write-Host "  Backup criado: $backupFile" -ForegroundColor Cyan
    }
    
    Write-Host "`n📋 DADOS FINAIS:" -ForegroundColor Yellow
    foreach ($key in $finalData.Keys) {
        $color = if ($finalData[$key] -gt 0) { "Green" } else { "Gray" }
        Write-Host "  $key`: $($finalData[$key])" -ForegroundColor $color
    }
    
    if ($DryRun) {
        Write-Host "`n🧪 SIMULAÇÃO CONCLUÍDA" -ForegroundColor Yellow
        Write-Host "Execute sem -DryRun para aplicar as mudanças" -ForegroundColor Gray
    } else {
        Write-Host "`n✅ RESET E IMPORTAÇÃO CONCLUÍDOS COM SUCESSO!" -ForegroundColor Green
    }
    
    Write-Host "Data/Hora: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
    
} catch {
    Write-Host "`n❌ ERRO DURANTE A OPERAÇÃO:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host "`nPara mais detalhes, execute com -Verbose" -ForegroundColor Yellow
    exit 1
}
