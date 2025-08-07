# Script de teste para endpoints de Teams
# Base URL: http://localhost:8080/csonline/api

param(
    [string]$Lo        Write-Host "✅ Sucesso! Time deletado." -ForegroundColor Green
    } catch {
        Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "✅ TESTE DE TEAMS CONCLUÍDO!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyanmin",
    [string]$Password = "admin123",
    [switch]$Verbose
)

# Importar utilitários JWT
. ".\jwt-utility.ps1"

$baseUrl = "http://localhost:8080/csonline/api/teams"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "🧪 TESTE DE ENDPOINTS - TEAMS JWT 2.0" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "🔐 Autenticação: JWT Bearer Token" -ForegroundColor Gray
Write-Host "👤 Login: $Login" -ForegroundColor Gray

# Obter token JWT
Write-Host "`n🔑 Obtendo token JWT..." -ForegroundColor Yellow
$token = Get-JWTToken -Login $Login -Password $Password
if (-not $token) {
    Write-Host "❌ ERRO: Falha na autenticação!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Token obtido com sucesso!" -ForegroundColor Green

# Test 1: GET /api/teams (Listar todos os times)
Write-Host "`n1️⃣ Listando todos os times (GET /api/teams):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "✅ Sucesso! Encontrados $($response.Count) times:" -ForegroundColor Green
    if ($Verbose -and $response.Count -gt 0) {
        $response | Format-Table -AutoSize
    } else {
        Write-Host "📊 Total: $($response.Count) times encontrados" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: GET /api/teams/{id} (Buscar time por ID)
Write-Host "`n2️⃣ Buscando time por ID=1 (GET /api/teams/1):" -ForegroundColor Green
try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "✅ Sucesso! Time encontrado:" -ForegroundColor Green
    if ($Verbose) {
        $response | Format-Table -AutoSize
    } else {
        Write-Host "📋 Time: $($response.name)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "⚠️ Time não encontrado ou erro: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Test 3: POST /api/teams (Criar novo time)
Write-Host "`n3️⃣ Criando novo time (POST /api/teams):" -ForegroundColor Green
$newTeam = @{
    name = "Time Teste JWT"
    description = "Time criado para teste JWT"
    active = $true
    businessId = 1  # ID do business
    courierId = 1   # ID do courier
} | ConvertTo-Json

try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $newTeam -Headers $headers -ContentType "application/json"
    Write-Host "✅ Sucesso! Time criado:" -ForegroundColor Green
    if ($Verbose) {
        $response | Format-Table -AutoSize
    } else {
        Write-Host "📋 Time criado: $($response.name) (ID: $($response.id))" -ForegroundColor Cyan
    }
    $createdTeamId = $response.id
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
    $createdTeamId = $null
}

# Test 4: PUT /api/teams/{id} (Atualizar time)
if ($createdTeamId) {
    Write-Host "`n4️⃣ Atualizando time ID=$createdTeamId (PUT /api/teams/$createdTeamId):" -ForegroundColor Green
    $updateTeam = @{
        id = $createdTeamId
        name = "Time Teste JWT Atualizado"
        description = "Time atualizado para teste JWT"
        active = $true
        businessId = 1
        courierId = 1
    } | ConvertTo-Json

    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdTeamId" -Method PUT -Body $updateTeam -Headers $headers -ContentType "application/json"
        Write-Host "✅ Sucesso! Time atualizado:" -ForegroundColor Green
        if ($Verbose) {
            $response | Format-Table -AutoSize
        } else {
            Write-Host "📋 Time atualizado: $($response.name)" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: DELETE /api/teams/{id} (Deletar time)
if ($createdTeamId) {
    Write-Host "`n5️⃣ Deletando time ID=$createdTeamId (DELETE /api/teams/$createdTeamId):" -ForegroundColor Green
    try {
        $headers = @{ "Authorization" = "Bearer $token" }
        $response = Invoke-RestMethod -Uri "$baseUrl/$createdTeamId" -Method DELETE -Headers $headers -ContentType "application/json"
        Write-Host "✅ Sucesso! Time deletado." -ForegroundColor Green
    } catch {
        Write-Host "Erro: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=======================================" -ForegroundColor Yellow
Write-Host "TESTE DE TEAMS CONCLUÍDO" -ForegroundColor Yellow
Write-Host "=======================================" -ForegroundColor Yellow
