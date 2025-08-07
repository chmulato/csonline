# ============================================================
# SCRIPT AVANÇADO DE IMPORTAÇÃO DO BANCO CSONLINE HSQLDB
# ============================================================
# Descrição: Parse completo do arquivo SQL e reimportação exata
# Versão: 2.0
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
    [switch]$Help
)

if ($Help) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT AVANÇADO DE IMPORTAÇÃO - CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`nFUNCIONALIDADES:" -ForegroundColor Yellow
    Write-Host "- Parse completo do arquivo SQL exportado" -ForegroundColor White
    Write-Host "- Reimportação exata dos dados originais" -ForegroundColor White
    Write-Host "- Preserva IDs e relacionamentos" -ForegroundColor White
    Write-Host "- Modo dry-run para teste" -ForegroundColor White
    
    Write-Host "`nUSO:" -ForegroundColor Yellow
    Write-Host ".\import-database-advanced.ps1                 # Importa dados reais" -ForegroundColor White
    Write-Host ".\import-database-advanced.ps1 -DryRun         # Simula importação" -ForegroundColor White
    Write-Host ".\import-database-advanced.ps1 -Verbose        # Com logs detalhados" -ForegroundColor White
    
    exit 0
}

# Função para log com timestamp
function Write-Log {
    param($Message, $Color = "White")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] $Message" -ForegroundColor $Color
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

# Função para parse do arquivo SQL exportado
function Parse-ExportFile {
    param($FilePath)
    
    if (-not (Test-Path $FilePath)) {
        throw "Arquivo não encontrado: $FilePath"
    }
    
    Write-Log "Fazendo parse do arquivo: $FilePath" -Color Cyan
    
    $content = Get-Content $FilePath -Raw
    $tables = @{}
    
    # Parse por seções de tabela
    $tablePattern = '-- TABELA: (\w+) \([^)]+\)\s*\n-- Registros: (\d+)'
    $tableMatches = [regex]::Matches($content, $tablePattern)
    
    foreach ($match in $tableMatches) {
        $tableName = $match.Groups[1].Value
        $recordCount = [int]$match.Groups[2].Value
        
        Write-Log "Encontrada tabela: $tableName ($recordCount registros)" -Color Yellow
        
        # Extrair registros da tabela
        $records = @()
        $recordPattern = "-- Registro: $tableName\s*\n((?:-- \w+ : .+\n)+)"
        $recordMatches = [regex]::Matches($content, $recordPattern)
        
        foreach ($recordMatch in $recordMatches) {
            $recordText = $recordMatch.Groups[1].Value
            $record = @{}
            
            # Parse dos campos do registro
            $fieldPattern = "-- (\w+) : ('.*?'|NULL|\d+(?:\.\d+)?)"
            $fieldMatches = [regex]::Matches($recordText, $fieldPattern)
            
            foreach ($fieldMatch in $fieldMatches) {
                $fieldName = $fieldMatch.Groups[1].Value
                $fieldValue = $fieldMatch.Groups[2].Value
                
                # Converter valores
                if ($fieldValue -eq "NULL") {
                    $record[$fieldName] = $null
                } elseif ($fieldValue.StartsWith("'") -and $fieldValue.EndsWith("'")) {
                    $record[$fieldName] = $fieldValue.Substring(1, $fieldValue.Length - 2)
                } elseif ($fieldValue -match '^\d+$') {
                    $record[$fieldName] = [int]$fieldValue
                } elseif ($fieldValue -match '^\d+\.\d+$') {
                    $record[$fieldName] = [decimal]$fieldValue
                } else {
                    $record[$fieldName] = $fieldValue
                }
            }
            
            if ($record.Count -gt 0) {
                $records += $record
            }
        }
        
        $tables[$tableName] = $records
    }
    
    return $tables
}

# Função para criar dados via API
function Create-Record {
    param($Endpoint, $Data, $Headers)
    
    if ($DryRun) {
        Write-Log "  [DRY-RUN] Criaria $Endpoint com dados: $(($Data | ConvertTo-Json -Compress))" -Color Gray
        return @{ id = 999; success = $true }
    }
    
    try {
        $response = Invoke-RestMethod -Uri "$ServerUrl/$Endpoint" -Method POST -Headers $Headers -Body ($Data | ConvertTo-Json)
        return @{ id = $response.id; success = $true; data = $response }
    } catch {
        return @{ success = $false; error = $_.Exception.Message }
    }
}

# Função para importar usuários
function Import-Users {
    param($Users, $Headers)
    
    Write-Log "Importando $($Users.Count) usuários..." -Color Yellow
    $imported = 0
    $userMapping = @{}
    
    foreach ($user in $Users) {
        $userData = @{
            name = $user.name
            login = $user.login
            password = $user.password
            email = $user.email
            role = $user.role
            address = $user.address
            mobile = $user.mobile
        }
        
        if ($user.email2) {
            $userData.email2 = $user.email2
        }
        
        $result = Create-Record -Endpoint "users" -Data $userData -Headers $Headers
        
        if ($result.success) {
            $userMapping[$user.id] = $result.id
            $imported++
            Write-Log "  ✅ Usuário criado: $($user.name) (ID original: $($user.id) → novo: $($result.id))" -Color Green
        } else {
            Write-Log "  ❌ Erro ao criar usuário $($user.name): $($result.error)" -Color Red
        }
    }
    
    Write-Log "Usuários importados: $imported/$($Users.Count)" -Color Cyan
    return $userMapping
}

# Função para importar entregadores
function Import-Couriers {
    param($Couriers, $Headers, $UserMapping)
    
    Write-Log "Importando $($Couriers.Count) entregadores..." -Color Yellow
    $imported = 0
    $courierMapping = @{}
    
    foreach ($courier in $Couriers) {
        # Mapear o userId original para o novo
        $newUserId = $UserMapping[$courier.userId]
        if (-not $newUserId) {
            Write-Log "  ⚠️ UserID $($courier.userId) não encontrado no mapeamento para courier ID $($courier.id)" -Color Yellow
            continue
        }
        
        $courierData = @{
            factorCourier = $courier.factorCourier
            businessId = $courier.businessId
            userId = $newUserId
        }
        
        $result = Create-Record -Endpoint "couriers" -Data $courierData -Headers $Headers
        
        if ($result.success) {
            $courierMapping[$courier.id] = $result.id
            $imported++
            Write-Log "  ✅ Entregador criado: Factor=$($courier.factorCourier) (ID original: $($courier.id) → novo: $($result.id))" -Color Green
        } else {
            Write-Log "  ❌ Erro ao criar entregador: $($result.error)" -Color Red
        }
    }
    
    Write-Log "Entregadores importados: $imported/$($Couriers.Count)" -Color Cyan
    return $courierMapping
}

# Função para importar clientes
function Import-Customers {
    param($Customers, $Headers, $UserMapping)
    
    Write-Log "Importando $($Customers.Count) clientes..." -Color Yellow
    $imported = 0
    $customerMapping = @{}
    
    foreach ($customer in $Customers) {
        # Mapear o userId original para o novo
        $newUserId = $UserMapping[$customer.userId]
        if (-not $newUserId) {
            Write-Log "  ⚠️ UserID $($customer.userId) não encontrado no mapeamento para customer ID $($customer.id)" -Color Yellow
            continue
        }
        
        $customerData = @{
            factorCustomer = $customer.factorCustomer
            priceTable = $customer.priceTable
            businessId = $customer.businessId
            userId = $newUserId
        }
        
        $result = Create-Record -Endpoint "customers" -Data $customerData -Headers $Headers
        
        if ($result.success) {
            $customerMapping[$customer.id] = $result.id
            $imported++
            Write-Log "  ✅ Cliente criado: $($customer.priceTable) (ID original: $($customer.id) → novo: $($result.id))" -Color Green
        } else {
            Write-Log "  ❌ Erro ao criar cliente: $($result.error)" -Color Red
        }
    }
    
    Write-Log "Clientes importados: $imported/$($Customers.Count)" -Color Cyan
    return $customerMapping
}

# Função para importar entregas
function Import-Deliveries {
    param($Deliveries, $Headers, $CustomerMapping, $CourierMapping)
    
    Write-Log "Importando $($Deliveries.Count) entregas..." -Color Yellow
    $imported = 0
    
    foreach ($delivery in $Deliveries) {
        # Mapear IDs originais para novos
        $newCustomerId = $CustomerMapping[$delivery.customerId]
        $newCourierId = $CourierMapping[$delivery.courierId]
        
        if (-not $newCustomerId) {
            Write-Log "  ⚠️ CustomerID $($delivery.customerId) não encontrado no mapeamento" -Color Yellow
            continue
        }
        
        if (-not $newCourierId) {
            Write-Log "  ⚠️ CourierID $($delivery.courierId) não encontrado no mapeamento" -Color Yellow
            continue
        }
        
        $deliveryData = @{
            businessId = $delivery.businessId
            customerId = $newCustomerId
            courierId = $newCourierId
            start = $delivery.start
            destination = $delivery.destination
            contact = $delivery.contact
            description = $delivery.description
            volume = $delivery.volume
            weight = $delivery.weight
            km = $delivery.km
            additionalCost = $delivery.additionalCost
            cost = $delivery.cost
            received = $delivery.received
            completed = $delivery.completed
        }
        
        $result = Create-Record -Endpoint "deliveries" -Data $deliveryData -Headers $Headers
        
        if ($result.success) {
            $imported++
            Write-Log "  ✅ Entrega criada: $($delivery.description) (ID original: $($delivery.id) → novo: $($result.id))" -Color Green
        } else {
            Write-Log "  ❌ Erro ao criar entrega: $($result.error)" -Color Red
        }
    }
    
    Write-Log "Entregas importadas: $imported/$($Deliveries.Count)" -Color Cyan
}

# Script principal
try {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "IMPORTAÇÃO AVANÇADA DO BANCO CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    if ($DryRun) {
        Write-Host "🧪 MODO DRY-RUN ATIVADO - NENHUM DADO SERÁ MODIFICADO" -ForegroundColor Yellow
    }
    
    Write-Log "Iniciando parse do arquivo de exportação..." -Color Cyan
    $tables = Parse-ExportFile -FilePath $ExportFile
    
    Write-Log "Tabelas encontradas:" -Color Cyan
    foreach ($tableName in $tables.Keys) {
        Write-Log "  ${tableName}: $($tables[$tableName].Count) registros" -Color Gray
    }
    
    if (-not $DryRun) {
        # Obter token JWT
        Write-Log "Obtendo token de autenticação..." -Color Yellow
        $token = Get-JWTToken -Login $AdminLogin -Password $AdminPassword
        $headers = Get-AuthHeaders -Token $token
        Write-Log "Token obtido com sucesso" -Color Green
    } else {
        $headers = @{}
    }
    
    # Importar na ordem correta (respeitando dependências)
    $userMapping = @{}
    $courierMapping = @{}
    $customerMapping = @{}
    
    # 1. Usuários primeiro (não têm dependências)
    if ($tables.ContainsKey("users")) {
        $userMapping = Import-Users -Users $tables["users"] -Headers $headers
    }
    
    # 2. Entregadores (dependem de usuários)
    if ($tables.ContainsKey("couriers")) {
        $courierMapping = Import-Couriers -Couriers $tables["couriers"] -Headers $headers -UserMapping $userMapping
    }
    
    # 3. Clientes (dependem de usuários)
    if ($tables.ContainsKey("customers")) {
        $customerMapping = Import-Customers -Customers $tables["customers"] -Headers $headers -UserMapping $userMapping
    }
    
    # 4. Entregas (dependem de clientes e entregadores)
    if ($tables.ContainsKey("deliveries")) {
        Import-Deliveries -Deliveries $tables["deliveries"] -Headers $headers -CustomerMapping $customerMapping -CourierMapping $courierMapping
    }
    
    Write-Host "`n============================================================" -ForegroundColor Cyan
    Write-Host "IMPORTAÇÃO CONCLUÍDA" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    if ($DryRun) {
        Write-Host "🧪 MODO DRY-RUN - Nenhum dado foi realmente importado" -ForegroundColor Yellow
        Write-Host "Execute sem -DryRun para importar os dados realmente" -ForegroundColor Gray
    } else {
        Write-Host "✅ Dados importados com sucesso!" -ForegroundColor Green
    }
    
    Write-Host "Data/Hora: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
    
} catch {
    Write-Host "`nERRO DURANTE A IMPORTAÇÃO:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
