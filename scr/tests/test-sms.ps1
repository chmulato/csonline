# Script de teste para endpoints de SMS com JWT
# Versão: 2.0 - Suporte completo a JWT
# Base URL: http://localhost:8080/csonline/api

# Importar utilitário JWT
. "$PSScriptRoot\jwt-utility.ps1"

$baseUrl = "http://localhost:8080/csonline/api/sms"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - SMS JWT" -ForegroundColor Yellow
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
$securityResult = Test-EndpointWithoutJWT -Url $baseUrl -Description "SMS endpoint"
if ($securityResult) {
    Write-Host "✅ Segurança OK: Endpoint protegido corretamente" -ForegroundColor Green
} else {
    Write-Host "❌ FALHA DE SEGURANÇA: Endpoint permite acesso sem JWT!" -ForegroundColor Red
}

# Test 1: GET /api/sms (Listar todos os SMS)
Write-Host "`n1. Listando todos os SMS (GET /api/sms):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) SMS:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/sms/{id} (Buscar SMS por ID)
Write-Host "`n2. Buscando SMS por ID=1 (GET /api/sms/1):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! SMS encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: GET /api/sms/delivery/{deliveryId} (Buscar SMS por entrega)
Write-Host "`n3. Buscando SMS por entrega ID=1 (GET /api/sms/delivery/1):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri "$baseUrl/delivery/1" -Method GET -Headers $headers -ContentType "application/json"
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
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newSMS -Headers $headers -ContentType "application/json"
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
