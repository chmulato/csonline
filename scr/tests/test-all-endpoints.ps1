# Script Master - Executa todos os testes de endpoints da aplicação CSOnline
# Este script executa todos os testes dos endpoints em sequência

param(
    [switch]$SkipCouriers,
    [switch]$SkipUsers,
    [switch]$SkipCustomers,
    [switch]$SkipTeams,
    [switch]$SkipDeliveries,
    [switch]$SkipSMS,
    [switch]$SkipLogin,
    [string]$OnlyTest = ""
)

$testScripts = @(
    @{ Name = "Couriers"; Script = "test-couriers.ps1"; Skip = $SkipCouriers },
    @{ Name = "Users"; Script = "test-users.ps1"; Skip = $SkipUsers },
    @{ Name = "Customers"; Script = "test-customers.ps1"; Skip = $SkipCustomers },
    @{ Name = "Teams"; Script = "test-teams.ps1"; Skip = $SkipTeams },
    @{ Name = "Deliveries"; Script = "test-deliveries.ps1"; Skip = $SkipDeliveries },
    @{ Name = "SMS"; Script = "test-sms.ps1"; Skip = $SkipSMS },
    @{ Name = "Login"; Script = "test-login.ps1"; Skip = $SkipLogin }
)

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "TESTE COMPLETO DE ENDPOINTS - CSONLINE API" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Data/Hora: $(Get-Date)" -ForegroundColor Cyan
Write-Host "Base URL: http://localhost:8080/csonline/api" -ForegroundColor Cyan

# Verificar se a aplicação está rodando
Write-Host "`nVerificando se a aplicação está rodando..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8080/csonline/api/couriers" -Method GET -TimeoutSec 5
    Write-Host "✓ Aplicação está rodando!" -ForegroundColor Green
} catch {
    Write-Host "✗ ERRO: Aplicação não está rodando ou não está acessível!" -ForegroundColor Red
    Write-Host "   Certifique-se de que a aplicação está rodando em http://localhost:8080/csonline" -ForegroundColor Red
    Write-Host "   Comando para iniciar: mvn wildfly:run" -ForegroundColor Yellow
    exit 1
}

$totalTests = 0
$passedTests = 0
$failedTests = 0
$skippedTests = 0

foreach ($test in $testScripts) {
    # Se especificado apenas um teste, executar apenas ele
    if ($OnlyTest -ne "" -and $test.Name -ne $OnlyTest) {
        continue
    }
    
    if ($test.Skip) {
        Write-Host "`n⏭️  PULANDO: $($test.Name)" -ForegroundColor Yellow
        $skippedTests++
        continue
    }
    
    Write-Host "`n🧪 EXECUTANDO: $($test.Name)" -ForegroundColor Cyan
    Write-Host "Script: $($test.Script)" -ForegroundColor Gray
    
    try {
        $scriptPath = Join-Path $PSScriptRoot $test.Script
        if (Test-Path $scriptPath) {
            & $scriptPath
            Write-Host "✓ $($test.Name) - CONCLUÍDO" -ForegroundColor Green
            $passedTests++
        } else {
            Write-Host "✗ $($test.Name) - ARQUIVO NÃO ENCONTRADO: $scriptPath" -ForegroundColor Red
            $failedTests++
        }
    } catch {
        Write-Host "✗ $($test.Name) - ERRO: $($_.Exception.Message)" -ForegroundColor Red
        $failedTests++
    }
    
    $totalTests++
    
    # Pausa entre testes
    if ($totalTests -lt $testScripts.Count) {
        Write-Host "`nAguardando 2 segundos antes do próximo teste..." -ForegroundColor Gray
        Start-Sleep -Seconds 2
    }
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "RESUMO DOS TESTES" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Total de testes executados: $totalTests" -ForegroundColor White
Write-Host "✓ Sucessos: $passedTests" -ForegroundColor Green
Write-Host "✗ Falhas: $failedTests" -ForegroundColor Red
Write-Host "⏭️  Pulados: $skippedTests" -ForegroundColor Yellow

if ($failedTests -eq 0 -and $totalTests -gt 0) {
    Write-Host "`n🎉 TODOS OS TESTES PASSARAM!" -ForegroundColor Green
} elseif ($failedTests -gt 0) {
    Write-Host "`n⚠️  ALGUNS TESTES FALHARAM!" -ForegroundColor Yellow
    Write-Host "Verifique os logs acima para mais detalhes." -ForegroundColor Yellow
}

Write-Host "`nTeste concluído em: $(Get-Date)" -ForegroundColor Cyan

# Exemplos de uso:
Write-Host "`n============================================================" -ForegroundColor DarkGray
Write-Host "EXEMPLOS DE USO:" -ForegroundColor DarkGray
Write-Host "============================================================" -ForegroundColor DarkGray
Write-Host "# Executar todos os testes:" -ForegroundColor DarkGray
Write-Host ".\test-all-endpoints.ps1" -ForegroundColor DarkGray
Write-Host "" -ForegroundColor DarkGray
Write-Host "# Executar apenas um teste específico:" -ForegroundColor DarkGray
Write-Host ".\test-all-endpoints.ps1 -OnlyTest 'Users'" -ForegroundColor DarkGray
Write-Host "" -ForegroundColor DarkGray
Write-Host "# Pular testes específicos:" -ForegroundColor DarkGray
Write-Host ".\test-all-endpoints.ps1 -SkipDeliveries -SkipSMS" -ForegroundColor DarkGray
