# Script de teste para endpoints de SMS
# Base URL: http://localhost:8080/csonline/api

$baseUrl = "http://localhost:8080/csonline/api/sms"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - SMS" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Test 1: GET /api/sms (Listar todos os SMS)
Write-Host "`n1. Listando todos os SMS (GET /api/sms):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) SMS:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/sms/{id} (Buscar SMS por ID)
Write-Host "`n2. Buscando SMS por ID=1 (GET /api/sms/1):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -ContentType "application/json"
    Write-Host "Sucesso! SMS encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: GET /api/sms/delivery/{deliveryId} (Buscar SMS por entrega)
Write-Host "`n3. Buscando SMS por entrega ID=1 (GET /api/sms/delivery/1):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/delivery/1" -Method GET -ContentType "application/json"
    Write-Host "Sucesso! SMS da entrega encontrados:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: POST /api/sms (Criar novo SMS)
Write-Host "`n4. Criando novo SMS (POST /api/sms):" -ForegroundColor Green
$newSMS = @{
    phoneNumber = "11999888777"
    message = "SMS de teste enviado via API"
    status = "PENDING"
    delivery = @{
        id = 1
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newSMS -ContentType "application/json"
    Write-Host "Sucesso! SMS criado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdSMSId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdSMSId = $null
}

# Test 5: DELETE /api/sms/{id} (Deletar SMS)
if ($createdSMSId) {
    Write-Host "`n5. Deletando SMS ID=$createdSMSId (DELETE /api/sms/$createdSMSId):" -ForegroundColor Green
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdSMSId" -Method DELETE -ContentType "application/json"
        Write-Host "Sucesso! SMS deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE SMS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
