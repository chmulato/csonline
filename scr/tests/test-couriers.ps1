# Script de teste para endpoints de Couriers
# Base URL: http://localhost:8080/csonline/api

$baseUrl = "http://localhost:8080/csonline/api/couriers"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - COURIERS" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Test 1: GET /api/couriers (Listar todos os entregadores)
Write-Host "`n1. Listando todos os entregadores (GET /api/couriers):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) entregadores:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/couriers/{id} (Buscar entregador por ID)
Write-Host "`n2. Buscando entregador por ID=1 (GET /api/couriers/1):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Entregador encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST /api/couriers (Criar novo entregador)
Write-Host "`n3. Criando novo entregador (POST /api/couriers):" -ForegroundColor Green
$newCourier = @{
    factorCourier = 1.5
    business = @{
        id = 1
        name = "Entregador Teste"
        email = "entregador.teste@example.com"
        password = "123456"
        phone = "11888777666"
        role = "COURIER"
        active = $true
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newCourier -ContentType "application/json"
    Write-Host "Sucesso! Entregador criado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdCourierId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdCourierId = $null
}

# Test 4: PUT /api/couriers/{id} (Atualizar entregador)
if ($createdCourierId) {
    Write-Host "`n4. Atualizando entregador ID=$createdCourierId (PUT /api/couriers/$createdCourierId):" -ForegroundColor Green
    $updateCourier = @{
        id = $createdCourierId
        factorCourier = 1.8
        business = @{
            id = 1
            name = "Entregador Teste Atualizado"
            email = "entregador.atualizado@example.com"
            password = "123456"
            phone = "11999888777"
            role = "COURIER"
            active = $true
        }
    } | ConvertTo-Json -Depth 3

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCourierId" -Method PUT -Body $updateCourier -ContentType "application/json"
        Write-Host "Sucesso! Entregador atualizado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/couriers/{id} (Deletar entregador)
if ($createdCourierId) {
    Write-Host "`n5. Deletando entregador ID=$createdCourierId (DELETE /api/couriers/$createdCourierId):" -ForegroundColor Green
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCourierId" -Method DELETE -ContentType "application/json"
        Write-Host "Sucesso! Entregador deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE COURIERS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
