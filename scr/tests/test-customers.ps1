# Script de teste para endpoints de Customers
# Base URL: http://localhost:8080/csonline/api

$baseUrl = "http://localhost:8080/csonline/api/customers"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - CUSTOMERS" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Test 1: GET /api/customers (Listar todos os clientes)
Write-Host "`n1. Listando todos os clientes (GET /api/customers):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) clientes:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/customers/{id} (Buscar cliente por ID)
Write-Host "`n2. Buscando cliente por ID=2 (GET /api/customers/2):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/2" -Method GET -ContentType "application/json"
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
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newCustomer -ContentType "application/json"
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
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCustomerId" -Method PUT -Body $updateCustomer -ContentType "application/json"
        Write-Host "Sucesso! Cliente atualizado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/customers/{id} (Deletar cliente)
if ($createdCustomerId) {
    Write-Host "`n5. Deletando cliente ID=$createdCustomerId (DELETE /api/customers/$createdCustomerId):" -ForegroundColor Green
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdCustomerId" -Method DELETE -ContentType "application/json"
        Write-Host "Sucesso! Cliente deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE CUSTOMERS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
