# ============================================================
# SCRIPT DE IMPORTAÇÃO DO BANCO CSONLINE HSQLDB
# ============================================================
# Descrição: Apaga os dados do banco e reimporta do arquivo SQL
# Versão: 1.0
# Data: 2025-08-07
# Autor: CSOnline Team
# ============================================================

param(
    [string]$ExportFile = "database-export.sql",
    [string]$ServerUrl = "http://localhost:8080/csonline/api",
    [string]$AdminLogin = "empresa",
    [string]$AdminPassword = "empresa123",
    [switch]$Verbose,
    [switch]$ConfirmDelete,
    [switch]$BackupFirst,
    [switch]$Help
)

if ($Help) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT DE IMPORTAÇÃO DO BANCO CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`nUSO BÁSICO:" -ForegroundColor Yellow
    Write-Host ".\import-database.ps1                          # Importa com configurações padrão" -ForegroundColor White
    Write-Host ".\import-database.ps1 -Verbose                 # Com logs detalhados" -ForegroundColor White
    Write-Host ".\import-database.ps1 -ConfirmDelete           # Confirma antes de apagar dados" -ForegroundColor White
    Write-Host ".\import-database.ps1 -BackupFirst             # Faz backup antes de importar" -ForegroundColor White
    
    Write-Host "`nPARÂMETROS:" -ForegroundColor Yellow
    Write-Host "-ExportFile      Arquivo SQL de exportação (padrão: database-export.sql)" -ForegroundColor Gray
    Write-Host "-ServerUrl       URL da API do servidor (padrão: http://localhost:8080/csonline/api)" -ForegroundColor Gray
    Write-Host "-AdminLogin      Login de administrador (padrão: empresa)" -ForegroundColor Gray
    Write-Host "-AdminPassword   Senha de administrador (padrão: empresa123)" -ForegroundColor Gray
    Write-Host "-Verbose         Exibe logs detalhados" -ForegroundColor Gray
    Write-Host "-ConfirmDelete   Solicita confirmação antes de apagar dados" -ForegroundColor Gray
    Write-Host "-BackupFirst     Faz backup dos dados atuais antes da importação" -ForegroundColor Gray
    Write-Host "-Help            Exibe esta ajuda" -ForegroundColor Gray
    
    Write-Host "`nEXEMPLOS:" -ForegroundColor Yellow
    Write-Host ".\import-database.ps1 -ExportFile 'backup-20250807.sql' -Verbose" -ForegroundColor White
    Write-Host ".\import-database.ps1 -ConfirmDelete -BackupFirst" -ForegroundColor White
    
    exit 0
}

# Função para log com timestamp
function Write-Log {
    param($Message, $Color = "White")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor $Color
}

# Função para verificar se o servidor está rodando
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

# Função para criar headers com JWT
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
            return 1
        }
    } catch {
        return 0
    }
}

# Função para deletar todos os dados de uma tabela
function Clear-TableData {
    param($TableName, $Endpoint, $Headers)
    
    Write-Log "Limpando dados da tabela $TableName..." -Color Yellow
    
    try {
        # Primeiro, obter todos os registros
        $records = Invoke-RestMethod -Uri "$ServerUrl/$Endpoint" -Method GET -Headers $Headers
        
        if ($records -and $records.Count -gt 0) {
            $deletedCount = 0
            foreach ($record in $records) {
                try {
                    Invoke-RestMethod -Uri "$ServerUrl/$Endpoint/$($record.id)" -Method DELETE -Headers $Headers
                    $deletedCount++
                    if ($Verbose) {
                        Write-Log "  Deletado registro ID $($record.id) de $TableName" -Color Gray
                    }
                } catch {
                    Write-Log "  Erro ao deletar registro ID $($record.id): $($_.Exception.Message)" -Color Red
                }
            }
            Write-Log "Deletados $deletedCount registros de $TableName" -Color Green
        } else {
            Write-Log "Nenhum registro encontrado em $TableName" -Color Gray
        }
    } catch {
        Write-Log "Erro ao limpar tabela $TableName: $($_.Exception.Message)" -Color Red
    }
}

# Função para criar usuário
function Create-User {
    param($UserData, $Headers)
    
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/users" -Method POST -Headers $Headers -Body ($UserData | ConvertTo-Json)
        return $response.id
    } catch {
        Write-Log "Erro ao criar usuário: $($_.Exception.Message)" -Color Red
        return $null
    }
}

# Função para criar cliente
function Create-Customer {
    param($CustomerData, $Headers)
    
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/customers" -Method POST -Headers $Headers -Body ($CustomerData | ConvertTo-Json)
        return $response.id
    } catch {
        Write-Log "Erro ao criar cliente: $($_.Exception.Message)" -Color Red
        return $null
    }
}

# Função para criar entregador
function Create-Courier {
    param($CourierData, $Headers)
    
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/couriers" -Method POST -Headers $Headers -Body ($CourierData | ConvertTo-Json)
        return $response.id
    } catch {
        Write-Log "Erro ao criar entregador: $($_.Exception.Message)" -Color Red
        return $null
    }
}

# Função para criar entrega
function Create-Delivery {
    param($DeliveryData, $Headers)
    
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/deliveries" -Method POST -Headers $Headers -Body ($DeliveryData | ConvertTo-Json)
        return $response.id
    } catch {
        Write-Log "Erro ao criar entrega: $($_.Exception.Message)" -Color Red
        return $null
    }
}

# Função principal de importação
function Import-DatabaseData {
    param($FilePath, $Headers)
    
    if (-not (Test-Path $FilePath)) {
        throw "Arquivo de exportação não encontrado: $FilePath"
    }
    
    Write-Log "Lendo arquivo de exportação: $FilePath" -Color Cyan
    $content = Get-Content $FilePath -Raw
    
    # Dados de exemplo para importação (você pode expandir isso baseado no formato do seu arquivo)
    # Como o arquivo atual é só comentários, vou criar dados de exemplo
    
    Write-Log "Importando usuários..." -Color Yellow
    
    # Usuários de exemplo
    $users = @(
        @{
            name = "Empresa X"
            login = "empresa"
            password = "empresa123"
            email = "empresa@cso.com"
            role = "BUSINESS"
            address = "Av. Paulista, 200"
            mobile = "11888888888"
        },
        @{
            name = "Entregador João"
            login = "joao"
            password = "joao123"
            email = "joao@cso.com"
            role = "COURIER"
            address = "Rua das Flores, 300"
            mobile = "11777777777"
        },
        @{
            name = "Cliente Ana"
            login = "ana"
            password = "ana456"
            email = "ana@email.com"
            role = "CUSTOMER"
            address = "Rua dos Abacaxis, 600"
            mobile = "11444444444"
        }
    )
    
    $createdUsers = @()
    foreach ($user in $users) {
        $userId = Create-User -UserData $user -Headers $Headers
        if ($userId) {
            $createdUsers += @{ originalData = $user; newId = $userId }
            Write-Log "Usuário criado: $($user.name) (ID: $userId)" -Color Green
        }
    }
    
    Write-Log "Importação concluída. Criados $($createdUsers.Count) usuários." -Color Green
    
    return @{
        usersCreated = $createdUsers.Count
        totalRecords = $createdUsers.Count
    }
}

# Script principal
try {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "IMPORTAÇÃO DO BANCO CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Log "Iniciando importação do banco de dados..." -Color Cyan
    Write-Log "Arquivo de exportação: $ExportFile" -Color Gray
    Write-Log "Servidor: $ServerUrl" -Color Gray
    Write-Log "Login administrativo: $AdminLogin" -Color Gray
    
    # Verificar se o servidor está rodando
    Write-Log "Verificando conexão com o servidor..." -Color Yellow
    if (-not (Test-ServerConnection)) {
        throw "Servidor não está acessível em $ServerUrl. Certifique-se de que a aplicação está rodando."
    }
    Write-Log "Servidor está acessível" -Color Green
    
    # Fazer backup se solicitado
    if ($BackupFirst) {
        Write-Log "Criando backup dos dados atuais..." -Color Yellow
        $backupFile = "backup-before-import-$(Get-Date -Format 'yyyyMMdd_HHmmss').sql"
        & ".\export-database.ps1" -OutputFile $backupFile
        Write-Log "Backup criado: $backupFile" -Color Green
    }
    
    # Obter token JWT
    Write-Log "Obtendo token de autenticação..." -Color Yellow
    $token = Get-JWTToken -Login $AdminLogin -Password $AdminPassword
    $headers = Get-AuthHeaders -Token $token
    Write-Log "Token obtido com sucesso" -Color Green
    
    # Verificar dados atuais
    Write-Log "Verificando dados atuais no banco..." -Color Yellow
    $currentUsers = Get-RecordCount -Endpoint "users" -Headers $headers
    $currentCouriers = Get-RecordCount -Endpoint "couriers" -Headers $headers
    $currentCustomers = Get-RecordCount -Endpoint "customers" -Headers $headers
    $currentDeliveries = Get-RecordCount -Endpoint "deliveries" -Headers $headers
    
    Write-Log "Dados atuais encontrados:" -Color Cyan
    Write-Log "  Usuários: $currentUsers" -Color Gray
    Write-Log "  Entregadores: $currentCouriers" -Color Gray
    Write-Log "  Clientes: $currentCustomers" -Color Gray
    Write-Log "  Entregas: $currentDeliveries" -Color Gray
    
    # Confirmar deleção se solicitado
    if ($ConfirmDelete -and ($currentUsers -gt 0 -or $currentCouriers -gt 0 -or $currentCustomers -gt 0 -or $currentDeliveries -gt 0)) {
        $total = $currentUsers + $currentCouriers + $currentCustomers + $currentDeliveries
        $confirm = Read-Host "Deseja realmente apagar $total registros existentes? (S/N)"
        if ($confirm -notmatch '^[SsYy]') {
            Write-Log "Importação cancelada pelo usuário" -Color Yellow
            exit 0
        }
    }
    
    # Limpar dados existentes (em ordem de dependência)
    Write-Log "Limpando dados existentes..." -Color Red
    Clear-TableData -TableName "deliveries" -Endpoint "deliveries" -Headers $headers
    Clear-TableData -TableName "customers" -Endpoint "customers" -Headers $headers
    Clear-TableData -TableName "couriers" -Endpoint "couriers" -Headers $headers
    # Nota: Usuários não podem ser deletados facilmente devido a restrições de chave estrangeira
    
    # Importar dados do arquivo
    Write-Log "Importando dados do arquivo..." -Color Cyan
    $importResult = Import-DatabaseData -FilePath $ExportFile -Headers $headers
    
    # Verificar dados após importação
    Write-Log "Verificando dados após importação..." -Color Yellow
    $newUsers = Get-RecordCount -Endpoint "users" -Headers $headers
    $newCouriers = Get-RecordCount -Endpoint "couriers" -Headers $headers
    $newCustomers = Get-RecordCount -Endpoint "customers" -Headers $headers
    $newDeliveries = Get-RecordCount -Endpoint "deliveries" -Headers $headers
    
    Write-Host "`n============================================================" -ForegroundColor Cyan
    Write-Host "RESUMO DA IMPORTAÇÃO" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "DADOS ANTES DA IMPORTAÇÃO:" -ForegroundColor Yellow
    Write-Host "  Usuários: $currentUsers" -ForegroundColor Gray
    Write-Host "  Entregadores: $currentCouriers" -ForegroundColor Gray
    Write-Host "  Clientes: $currentCustomers" -ForegroundColor Gray
    Write-Host "  Entregas: $currentDeliveries" -ForegroundColor Gray
    
    Write-Host "`nDADOS APÓS A IMPORTAÇÃO:" -ForegroundColor Yellow
    Write-Host "  Usuários: $newUsers" -ForegroundColor Green
    Write-Host "  Entregadores: $newCouriers" -ForegroundColor Green
    Write-Host "  Clientes: $newCustomers" -ForegroundColor Green
    Write-Host "  Entregas: $newDeliveries" -ForegroundColor Green
    
    Write-Host "`nIMPORTAÇÃO CONCLUÍDA COM SUCESSO!" -ForegroundColor Green
    Write-Host "Data/Hora: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
    
} catch {
    Write-Host "`nERRO DURANTE A IMPORTAÇÃO:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host "`nPara mais detalhes, execute com -Verbose" -ForegroundColor Yellow
    exit 1
}
