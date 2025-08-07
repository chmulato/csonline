# Script de teste para endpoints de Users com JWT
# Versão: 2.0 - Suporte completo a JWT
# Base URL: http://localhost:8080/csonline/api

# Importar utilitário JWT
. "$PSScriptRoot\jwt-utility.ps1"

$baseUrl = "http://localhost:8080/csonline/api/users"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - USERS JWT" -ForegroundColor Yellow
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
$securityResult = Test-EndpointWithoutJWT -Url $baseUrl -Description "Users endpoint"
if ($securityResult) {
    Write-Host "✅ Segurança OK: Endpoint protegido corretamente" -ForegroundColor Green
} else {
    Write-Host "❌ FALHA DE SEGURANÇA: Endpoint permite acesso sem JWT!" -ForegroundColor Red
}

# Test 1: GET /api/users (Listar todos os usuários)
Write-Host "`n1. Listando todos os usuários (GET /api/users):" -ForegroundColor Green
try {
    $headers = Get-JWTHeaders -Token $token
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers
    Write-Host "✅ Sucesso! Encontrados $($response.Count) usuários:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/users/{id} (Buscar usuário por ID)
Write-Host "`n2. Buscando usuário por ID=2 (GET /api/users/2):" -ForegroundColor Green
try {
    $headers = Get-JWTHeaders -Token $token
    $response = Invoke-RestMethod -Uri "$baseUrl/2" -Method GET -Headers $headers
    Write-Host "✅ Sucesso! Usuário encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST /api/users (Criar novo usuário)
Write-Host "`n3. Criando novo usuário (POST /api/users):" -ForegroundColor Green
$newUser = @{
    name = "Teste User"
    email = "teste@example.com"
    password = "123456"
    phone = "11999999999"
    role = "USER"
    active = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newUser -ContentType "application/json"
    Write-Host "Sucesso! Usuário criado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdUserId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdUserId = $null
}

# Test 4: PUT /api/users/{id} (Atualizar usuário)
if ($createdUserId) {
    Write-Host "`n4. Atualizando usuário ID=$createdUserId (PUT /api/users/$createdUserId):" -ForegroundColor Green
    $updateUser = @{
        id = $createdUserId
        name = "Teste User Atualizado"
        email = "teste.atualizado@example.com"
        password = "123456"
        phone = "11888888888"
        role = "USER"
        active = $true
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdUserId" -Method PUT -Body $updateUser -ContentType "application/json"
        Write-Host "Sucesso! Usuário atualizado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/users/{id} (Deletar usuário)
if ($createdUserId) {
    Write-Host "`n5. Deletando usuário ID=$createdUserId (DELETE /api/users/$createdUserId):" -ForegroundColor Green
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdUserId" -Method DELETE -ContentType "application/json"
        Write-Host "Sucesso! Usuário deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE USERS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
