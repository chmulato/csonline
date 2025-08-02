# Script de teste para endpoint de Login
# Base URL: http://localhost:8080/csonline/api

$baseUrl = "http://localhost:8080/csonline/api/login"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - LOGIN" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Test 1: POST /api/login (Fazer login - credenciais válidas)
Write-Host "`n1. Fazendo login com credenciais válidas (POST /api/login):" -ForegroundColor Green
$loginRequest = @{
    email = "admin@csonline.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $loginRequest -ContentType "application/json"
    Write-Host "Sucesso! Login realizado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: POST /api/login (Fazer login - credenciais inválidas)
Write-Host "`n2. Fazendo login com credenciais inválidas (POST /api/login):" -ForegroundColor Green
$invalidLoginRequest = @{
    email = "invalid@example.com"
    password = "wrongpassword"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $invalidLoginRequest -ContentType "application/json"
    Write-Host "Inesperado! Login realizado com credenciais inválidas:" -ForegroundColor Yellow
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Esperado! Login negado para credenciais inválidas: $($_.Exception.Message)" -ForegroundColor Green
}

# Test 3: POST /api/login (Teste com email vazio)
Write-Host "`n3. Fazendo login com email vazio (POST /api/login):" -ForegroundColor Green
$emptyEmailRequest = @{
    email = ""
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $emptyEmailRequest -ContentType "application/json"
    Write-Host "Inesperado! Login realizado com email vazio:" -ForegroundColor Yellow
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Esperado! Login negado para email vazio: $($_.Exception.Message)" -ForegroundColor Green
}

# Test 4: POST /api/login (Teste com password vazio)
Write-Host "`n4. Fazendo login com password vazio (POST /api/login):" -ForegroundColor Green
$emptyPasswordRequest = @{
    email = "admin@csonline.com"
    password = ""
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $emptyPasswordRequest -ContentType "application/json"
    Write-Host "Inesperado! Login realizado com password vazio:" -ForegroundColor Yellow
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Esperado! Login negado para password vazio: $($_.Exception.Message)" -ForegroundColor Green
}

# Test 5: POST /api/login (Teste com JSON malformado)
Write-Host "`n5. Fazendo login com JSON malformado (POST /api/login):" -ForegroundColor Green
$malformedJson = '{"email": "admin@csonline.com", "password": "admin123"'  # JSON incompleto

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $malformedJson -ContentType "application/json"
    Write-Host "Inesperado! Login realizado com JSON malformado:" -ForegroundColor Yellow
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Esperado! Login negado para JSON malformado: $($_.Exception.Message)" -ForegroundColor Green
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE LOGIN CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
