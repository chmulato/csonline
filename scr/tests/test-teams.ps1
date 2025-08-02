# Script de teste para endpoints de Teams
# Base URL: http://localhost:8080/csonline/api

$baseUrl = "http://localhost:8080/csonline/api/team"

Write-Host "=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE ENDPOINTS - TEAMS" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow

# Test 1: GET /api/team (Listar todos os times)
Write-Host "`n1. Listando todos os times (GET /api/team):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Encontrados $($response.Count) times:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/team/{id} (Buscar time por ID)
Write-Host "`n2. Buscando time por ID=1 (GET /api/team/1):" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -ContentType "application/json"
    Write-Host "Sucesso! Time encontrado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: POST /api/team (Criar novo time)
Write-Host "`n3. Criando novo time (POST /api/team):" -ForegroundColor Green
$newTeam = @{
    name = "Time Teste"
    description = "Time criado para teste"
    active = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newTeam -ContentType "application/json"
    Write-Host "Sucesso! Time criado:" -ForegroundColor Green
    $response | Format-Table -AutoSize
    $createdTeamId = $response.id
} catch {
    Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdTeamId = $null
}

# Test 4: PUT /api/team/{id} (Atualizar time)
if ($createdTeamId) {
    Write-Host "`n4. Atualizando time ID=$createdTeamId (PUT /api/team/$createdTeamId):" -ForegroundColor Green
    $updateTeam = @{
        id = $createdTeamId
        name = "Time Teste Atualizado"
        description = "Time atualizado para teste"
        active = $true
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdTeamId" -Method PUT -Body $updateTeam -ContentType "application/json"
        Write-Host "Sucesso! Time atualizado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/team/{id} (Deletar time)
if ($createdTeamId) {
    Write-Host "`n5. Deletando time ID=$createdTeamId (DELETE /api/team/$createdTeamId):" -ForegroundColor Green
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdTeamId" -Method DELETE -ContentType "application/json"
        Write-Host "Sucesso! Time deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE TEAMS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
