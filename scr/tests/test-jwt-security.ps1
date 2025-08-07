# Script de teste de segurança JWT
# Versão: 1.0 - Teste abrangente de segurança
# Data: 7 de agosto de 2025

param(
    [string]$BaseUrl = "http://localhost:8080/csonline",
    [string]$Login = "empresa",
    [string]$Password = "empresa123",
    [switch]$Verbose
)

# Importar utilitário JWT
. "$PSScriptRoot\jwt-utility.ps1"

Write-Host "============================================================" -ForegroundColor Magenta
Write-Host "TESTE DE SEGURANÇA JWT - CSONLINE API" -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta

$results = @{
    PublicEndpoints = @()
    ProtectedEndpoints = @()
    SecurityTests = @()
    TotalTests = 0
    PassedTests = 0
    FailedTests = 0
}

# Endpoints a serem testados
$publicEndpoints = @(
    @{ Url = "$BaseUrl/api/login"; Method = "POST"; Body = '{"login":"empresa","password":"empresa123"}'; Description = "Login endpoint" }
)

$protectedEndpoints = @(
    @{ Url = "$BaseUrl/api/users"; Method = "GET"; Description = "Users list" },
    @{ Url = "$BaseUrl/api/couriers"; Method = "GET"; Description = "Couriers list" },
    @{ Url = "$BaseUrl/api/customers"; Method = "GET"; Description = "Customers list" },
    @{ Url = "$BaseUrl/api/deliveries"; Method = "GET"; Description = "Deliveries list" },
    @{ Url = "$BaseUrl/api/teams"; Method = "GET"; Description = "Teams list" },
    @{ Url = "$BaseUrl/api/sms"; Method = "GET"; Description = "SMS list" }
)

function Test-Endpoint {
    param(
        [string]$Url,
        [string]$Method = "GET",
        [string]$Body = $null,
        [hashtable]$Headers = @{},
        [string]$ExpectedStatus,
        [string]$Description
    )
    
    $results.TotalTests++
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            ErrorAction = "SilentlyContinue"
        }
        
        if ($Headers.Count -gt 0) {
            $params.Headers = $Headers
        }
        
        if ($Body) {
            $params.Body = $Body
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-WebRequest @params
        $actualStatus = $response.StatusCode
        
        if ($ExpectedStatus -and $actualStatus -ne $ExpectedStatus) {
            Write-Host "❌ FALHA: $Description" -ForegroundColor Red
            Write-Host "   Esperado: $ExpectedStatus, Recebido: $actualStatus" -ForegroundColor Red
            $results.FailedTests++
            return $false
        } else {
            Write-Host "✅ SUCESSO: $Description" -ForegroundColor Green
            Write-Host "   Status: $actualStatus" -ForegroundColor Gray
            $results.PassedTests++
            return $true
        }
    }
    catch {
        $actualStatus = $_.Exception.Response.StatusCode.value__
        
        if ($ExpectedStatus -and $actualStatus -eq $ExpectedStatus) {
            Write-Host "✅ SUCESSO: $Description" -ForegroundColor Green
            Write-Host "   Status: $actualStatus (esperado)" -ForegroundColor Gray
            $results.PassedTests++
            return $true
        } else {
            Write-Host "❌ FALHA: $Description" -ForegroundColor Red
            Write-Host "   Erro: $($_.Exception.Message)" -ForegroundColor Red
            $results.FailedTests++
            return $false
        }
    }
}

# Teste 1: Endpoints públicos devem funcionar sem JWT
Write-Host "`n📋 TESTE 1: ENDPOINTS PÚBLICOS (sem JWT)" -ForegroundColor Yellow
Write-Host "Verificando se endpoints públicos funcionam sem autenticação..." -ForegroundColor Gray

foreach ($endpoint in $publicEndpoints) {
    $result = Test-Endpoint -Url $endpoint.Url -Method $endpoint.Method -Body $endpoint.Body -ExpectedStatus 200 -Description $endpoint.Description
    $results.PublicEndpoints += @{ Endpoint = $endpoint.Description; Result = $result }
}

# Teste 2: Endpoints protegidos devem bloquear acesso sem JWT
Write-Host "`n🔒 TESTE 2: ENDPOINTS PROTEGIDOS (sem JWT - deve retornar 401)" -ForegroundColor Yellow
Write-Host "Verificando se endpoints protegidos bloqueiam acesso sem JWT..." -ForegroundColor Gray

foreach ($endpoint in $protectedEndpoints) {
    $result = Test-Endpoint -Url $endpoint.Url -Method $endpoint.Method -ExpectedStatus 401 -Description "$($endpoint.Description) (sem JWT)"
    $results.SecurityTests += @{ Endpoint = $endpoint.Description; Type = "No JWT"; Result = $result }
}

# Teste 3: Obter token JWT válido
Write-Host "`n🔑 TESTE 3: OBTENÇÃO DE TOKEN JWT" -ForegroundColor Yellow
$token = Get-JWTToken -Login $Login -Password $Password -BaseUrl $BaseUrl -Verbose:$Verbose

if (-not $token) {
    Write-Host "❌ CRÍTICO: Falha ao obter token JWT. Testes interrompidos." -ForegroundColor Red
    exit 1
}

Write-Host "✅ Token JWT obtido com sucesso!" -ForegroundColor Green

# Teste 4: Endpoints protegidos devem permitir acesso com JWT válido
Write-Host "`n🛡️ TESTE 4: ENDPOINTS PROTEGIDOS (com JWT válido)" -ForegroundColor Yellow
Write-Host "Verificando se endpoints protegidos permitem acesso com JWT válido..." -ForegroundColor Gray

$jwtHeaders = Get-JWTHeaders -Token $token

foreach ($endpoint in $protectedEndpoints) {
    $result = Test-Endpoint -Url $endpoint.Url -Method $endpoint.Method -Headers $jwtHeaders -ExpectedStatus 200 -Description "$($endpoint.Description) (com JWT)"
    $results.ProtectedEndpoints += @{ Endpoint = $endpoint.Description; Result = $result }
}

# Teste 5: Token inválido deve ser rejeitado
Write-Host "`n🚫 TESTE 5: TOKEN JWT INVÁLIDO" -ForegroundColor Yellow
Write-Host "Verificando se tokens inválidos são rejeitados..." -ForegroundColor Gray

$invalidHeaders = @{ "Authorization" = "Bearer token_invalido_123"; "Content-Type" = "application/json" }

foreach ($endpoint in $protectedEndpoints) {
    $result = Test-Endpoint -Url $endpoint.Url -Method $endpoint.Method -Headers $invalidHeaders -ExpectedStatus 401 -Description "$($endpoint.Description) (token inválido)"
    $results.SecurityTests += @{ Endpoint = $endpoint.Description; Type = "Invalid JWT"; Result = $result }
}

# Teste 6: Header malformado deve ser rejeitado
Write-Host "`n🔧 TESTE 6: HEADER AUTHORIZATION MALFORMADO" -ForegroundColor Yellow
Write-Host "Verificando se headers malformados são rejeitados..." -ForegroundColor Gray

$malformedHeaders = @{ "Authorization" = $token; "Content-Type" = "application/json" }  # Sem "Bearer"

$testEndpoint = $protectedEndpoints[0]
$result = Test-Endpoint -Url $testEndpoint.Url -Method $testEndpoint.Method -Headers $malformedHeaders -ExpectedStatus 401 -Description "$($testEndpoint.Description) (header malformado)"
$results.SecurityTests += @{ Endpoint = $testEndpoint.Description; Type = "Malformed Header"; Result = $result }

# Relatório final
Write-Host "`n============================================================" -ForegroundColor Magenta
Write-Host "RELATÓRIO FINAL DE SEGURANÇA JWT" -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta

Write-Host "`n📊 ESTATÍSTICAS:" -ForegroundColor Cyan
Write-Host "Total de testes: $($results.TotalTests)" -ForegroundColor White
Write-Host "Testes aprovados: $($results.PassedTests)" -ForegroundColor Green
Write-Host "Testes falharam: $($results.FailedTests)" -ForegroundColor Red
$successRate = [math]::Round(($results.PassedTests / $results.TotalTests) * 100, 2)
Write-Host "Taxa de sucesso: $successRate%" -ForegroundColor Cyan

Write-Host "`n📋 RESUMO POR CATEGORIA:" -ForegroundColor Cyan

# Endpoints públicos
$publicSuccess = ($results.PublicEndpoints | Where-Object { $_.Result }).Count
Write-Host "Endpoints públicos: $publicSuccess/$($results.PublicEndpoints.Count)" -ForegroundColor $(if ($publicSuccess -eq $results.PublicEndpoints.Count) { "Green" } else { "Red" })

# Endpoints protegidos
$protectedSuccess = ($results.ProtectedEndpoints | Where-Object { $_.Result }).Count
Write-Host "Endpoints protegidos (com JWT): $protectedSuccess/$($results.ProtectedEndpoints.Count)" -ForegroundColor $(if ($protectedSuccess -eq $results.ProtectedEndpoints.Count) { "Green" } else { "Red" })

# Testes de segurança
$securitySuccess = ($results.SecurityTests | Where-Object { $_.Result }).Count
Write-Host "Testes de segurança: $securitySuccess/$($results.SecurityTests.Count)" -ForegroundColor $(if ($securitySuccess -eq $results.SecurityTests.Count) { "Green" } else { "Red" })

if ($results.FailedTests -eq 0) {
    Write-Host "`n🎉 TODOS OS TESTES DE SEGURANÇA JWT PASSARAM!" -ForegroundColor Green
    Write-Host "✅ Sistema está seguro e funcionando corretamente." -ForegroundColor Green
} else {
    Write-Host "`n⚠️ ALGUNS TESTES FALHARAM - VERIFICAR SEGURANÇA!" -ForegroundColor Red
    Write-Host "❌ Revisar configurações de JWT e filtros de segurança." -ForegroundColor Red
}

Write-Host "`nTeste concluído em: $(Get-Date)" -ForegroundColor Gray
