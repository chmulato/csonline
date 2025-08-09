# ============================================================================
# Teste Rápido - Verificação Essencial dos Endpoints por Perfil
# ============================================================================
# 
# Este script faz uma verificação rápida dos endpoints principais
# para cada perfil de usuário para validação imediata
#
# Uso: .\quick-test.ps1
# ============================================================================

param(
    [string]$BaseUrl = "http://localhost:8080/csonline/api"
)

$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Cyan = "Cyan"

function Test-QuickLogin {
    param($Username, $Password, $Role)
    
    try {
        $body = @{ login = $Username; password = $Password } | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "$BaseUrl/login" -Method POST -Body $body -ContentType "application/json"
        
        if ($response.token -and $response.role -eq $Role) {
            Write-Host "✅ $Username ($Role)" -ForegroundColor $Green
            return $response.token
        } else {
            Write-Host "❌ $Username ($Role) - Token ou role inválido" -ForegroundColor $Red
            return $null
        }
    }
    catch {
        Write-Host "❌ $Username ($Role) - $($_.Exception.Message)" -ForegroundColor $Red
        return $null
    }
}

function Test-QuickEndpoint {
    param($Name, $Endpoint, $Token, $ShouldWork = $true)
    
    try {
        $headers = @{ "Authorization" = "Bearer $Token"; "Accept" = "application/json" }
        $response = Invoke-RestMethod -Uri "$BaseUrl$Endpoint" -Headers $headers
        
        if ($ShouldWork) {
            Write-Host "  ✅ $Name" -ForegroundColor $Green
        } else {
            Write-Host "  ⚠️  $Name (deveria falhar mas funcionou)" -ForegroundColor $Yellow
        }
    }
    catch {
        if ($ShouldWork) {
            Write-Host "  ❌ $Name - $($_.Exception.Message)" -ForegroundColor $Red
        } else {
            Write-Host "  ✅ $Name (acesso negado conforme esperado)" -ForegroundColor $Green
        }
    }
}

Write-Host "🚀 TESTE RÁPIDO CSONLINE" -ForegroundColor $Cyan
Write-Host "=========================" -ForegroundColor $Cyan

# Teste Admin
Write-Host "`n👤 ADMIN:" -ForegroundColor $Yellow
$adminToken = Test-QuickLogin "admin" "admin123" "ADMIN"
if ($adminToken) {
    Test-QuickEndpoint "Usuários" "/users" $adminToken
    Test-QuickEndpoint "Entregadores" "/couriers" $adminToken
    Test-QuickEndpoint "Clientes" "/customers" $adminToken
    Test-QuickEndpoint "Entregas" "/deliveries" $adminToken
    Test-QuickEndpoint "Preços" "/prices" $adminToken
    Test-QuickEndpoint "SMS" "/sms" $adminToken
    Test-QuickEndpoint "Equipes" "/teams" $adminToken
}

# Teste Business
Write-Host "`n🏢 BUSINESS:" -ForegroundColor $Yellow
$businessToken = Test-QuickLogin "empresa" "empresa123" "BUSINESS"
if ($businessToken) {
    Test-QuickEndpoint "Entregadores" "/couriers" $businessToken
    Test-QuickEndpoint "Clientes" "/customers" $businessToken
    Test-QuickEndpoint "Entregas" "/deliveries" $businessToken
    Test-QuickEndpoint "Preços" "/prices" $businessToken
    Test-QuickEndpoint "SMS" "/sms" $businessToken
    Test-QuickEndpoint "Usuários (negado)" "/users" $businessToken $false
    Test-QuickEndpoint "Equipes (negado)" "/teams" $businessToken $false
}

# Teste Courier
Write-Host "`n🚴 COURIER:" -ForegroundColor $Yellow
$courierToken = Test-QuickLogin "joao" "joao123" "COURIER"
if ($courierToken) {
    Test-QuickEndpoint "Entregas" "/deliveries" $courierToken
    Test-QuickEndpoint "Usuários (negado)" "/users" $courierToken $false
    Test-QuickEndpoint "Clientes (negado)" "/customers" $courierToken $false
    Test-QuickEndpoint "Preços (negado)" "/prices" $courierToken $false
    Test-QuickEndpoint "SMS (negado)" "/sms" $courierToken $false
}

# Teste Customer
Write-Host "`n🛒 CUSTOMER:" -ForegroundColor $Yellow
$customerToken = Test-QuickLogin "carlos" "carlos123" "CUSTOMER"
if ($customerToken) {
    Test-QuickEndpoint "Entregas" "/deliveries" $customerToken
    Test-QuickEndpoint "Usuários (negado)" "/users" $customerToken $false
    Test-QuickEndpoint "Entregadores (negado)" "/couriers" $customerToken $false
    Test-QuickEndpoint "Clientes (negado)" "/customers" $customerToken $false
    Test-QuickEndpoint "Preços (negado)" "/prices" $customerToken $false
    Test-QuickEndpoint "SMS (negado)" "/sms" $customerToken $false
}

Write-Host "`n✨ Teste rápido concluído!" -ForegroundColor $Cyan
Write-Host "Para testes completos, execute: .\test-all-profiles.ps1" -ForegroundColor Gray
