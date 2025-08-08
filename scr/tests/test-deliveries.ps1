# Script de teste para endpoints de Deliveries com JWT
# Versão: 2.0 - Suporte completo a JWT
# Base URL: http://localhost:8080/csonline/api

# Importar utilitário JWT
. "$PSScriptRoot\jwt-utility.ps1"

$baseUrl = "http://localhost:8080/csonline/api/deliveries"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - DELIVERIES JWT" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Obter token JWT
Write-Host "🔑 Obtendo token JWT..." -ForegroundColor Yellow
$token = Get-JWTToken -Login "empresa" -Password "empresa123" -BaseUrl "http://localhost:8080/csonline" -Verbose

if (-not $token) {
    Write-Host "❌ Falha ao obter token JWT. Abortando testes." -ForegroundColor Red
    exit 1
}

# Test 0: Teste de segurança - acesso sem JWT
Write-Host "`n0. Teste de segurança - tentativa de acesso sem JWT:" -ForegroundColor Yellow
$securityResult = Test-EndpointWithoutJWT -Url $baseUrl -Description "Deliveries endpoint"
if ($securityResult) {
    Write-Host "✅ Segurança OK: Endpoint protegido corretamente" -ForegroundColor Green
} else {
    Write-Host "❌ FALHA DE SEGURANÇA: Endpoint permite acesso sem JWT!" -ForegroundColor Red
}

# Test 1: GET /api/deliveries (Listar todas as entregas)
Write-Host "`n1. Listando todas as entregas (GET /api/deliveries):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Encontradas $($response.Count) entregas:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/deliveries/{id} (Buscar entrega por ID)
Write-Host "`n2. Buscando entrega por ID=1 (GET /api/deliveries/1):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Entrega encontrada:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST /api/deliveries (Criar nova entrega)
Write-Host "`n3. Criando nova entrega (POST /api/deliveries):" -ForegroundColor Green
$newDelivery = @{
    description = "Entrega de teste"
    originAddress = "Rua Origem, 123"
    destinationAddress = "Rua Destino, 456"
    deliveryDate = "2025-01-30T10:00:00"
    status = "PENDING"
    customer = @{
        id = 1
    }
    courier = @{
        id = 1
    }
} | ConvertTo-Json -Depth 3

try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newDelivery -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Entrega criada:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdDeliveryId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdDeliveryId = $null
}

# Test 4: PUT /api/deliveries/{id} (Atualizar entrega)
if ($createdDeliveryId) {
    Write-Host "`n4. Atualizando entrega ID=$createdDeliveryId (PUT /api/deliveries/$createdDeliveryId):" -ForegroundColor Green
    $updateDelivery = @{
        id = $createdDeliveryId
        description = "Entrega de teste atualizada"
        originAddress = "Rua Origem Nova, 789"
        destinationAddress = "Rua Destino Nova, 101"
        deliveryDate = "2025-01-30T14:00:00"
        status = "IN_TRANSIT"
        customer = @{
            id = 1
        }
        courier = @{
            id = 1
        }
    } | ConvertTo-Json -Depth 3

    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdDeliveryId" -Method PUT -Body $updateDelivery -Headers $headers -ContentType "application/json"
        Write-Host "Sucesso! Entrega atualizada." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/deliveries/{id} (Deletar entrega)
if ($createdDeliveryId) {
    Write-Host "`n5. Deletando entrega ID=$createdDeliveryId (DELETE /api/deliveries/$createdDeliveryId):" -ForegroundColor Green
    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdDeliveryId" -Method DELETE -Headers $headers -ContentType "application/json"
        Write-Host "Sucesso! Entrega deletada." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE DELIVERIES CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
