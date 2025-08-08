# Script de teste para endpoints de Customers com JWT
# Versão: 2.0 - Suporte completo a JWT
# Base URL: http://localhost:8080/csonline/api

# Importar utilitário JWT
. "$PSScriptRoot\jwt-utility.ps1"

$baseUrl = "http://localhost:8080/csonline/api/customers"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - CUSTOMERS JWT" -ForegroundColor Yellow
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
$securityResult = Test-EndpointWithoutJWT -Url $baseUrl -Description "Customers endpoint"
if ($securityResult) {
    Write-Host "✅ Segurança OK: Endpoint protegido corretamente" -ForegroundColor Green
} else {
    Write-Host "❌ FALHA DE SEGURANÇA: Endpoint permite acesso sem JWT!" -ForegroundColor Red
}

# Test 1: GET /api/customers (Listar todos os clientes)
Write-Host "`n1. Listando todos os clientes (GET /api/customers):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) clientes:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/customers/{id} (Buscar cliente por ID)
Write-Host "`n2. Buscando cliente por ID=2 (GET /api/customers/2):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri "$baseUrl/2" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Cliente encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST /api/customers (Criar novo cliente)
Write-Host "`n3. Criando novo cliente (POST /api/customers):" -ForegroundColor Green
$newCustomer = @{
    name = "Empresa Teste"
    cnpj = "12345678000199"
    email = "empresa@teste.com"
    phone = "1133334444"
    address = "Rua Teste, 123"
    city = "São Paulo"
    state = "SP"
    zipCode = "01234567"
    active = $true
} | ConvertTo-Json

try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newCustomer -Headers $headers -ContentType "application/json"
    Write-Host "Sucesso! Cliente criado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdCustomerId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdCustomerId = $null
}

# Test 4: PUT /api/customers/{id} (Atualizar cliente)
if ($createdCustomerId) {
    Write-Host "`n4. Atualizando cliente ID=$createdCustomerId (PUT /api/customers/$createdCustomerId):" -ForegroundColor Green
    $updateCustomer = @{
        id = $createdCustomerId
        name = "Empresa Teste Atualizada"
        cnpj = "12345678000199"
        email = "empresa.nova@teste.com"
        phone = "1144445555"
        address = "Rua Nova, 456"
        city = "São Paulo"
        state = "SP"
        zipCode = "01234567"
        active = $true
    } | ConvertTo-Json

    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCustomerId" -Method PUT -Body $updateCustomer -Headers $headers -ContentType "application/json"
        Write-Host "Sucesso! Cliente atualizado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/customers/{id} (Deletar cliente)
if ($createdCustomerId) {
    Write-Host "`n5. Deletando cliente ID=$createdCustomerId (DELETE /api/customers/$createdCustomerId):" -ForegroundColor Green
    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCustomerId" -Method DELETE -Headers $headers -ContentType "application/json"
        Write-Host "Sucesso! Cliente deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE CUSTOMERS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
