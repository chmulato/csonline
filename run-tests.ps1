# Script de conveniência para executar testes da aplicação CSOnline
# Este script permite executar os testes da raiz do projeto

param(
    [string]$TestType = "all",
    [string]$OnlyTest = "",
    [switch]$HealthCheck,
    [switch]$Help
)

$testsPath = Join-Path $PSScriptRoot "scr\tests"

if ($Help) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT DE CONVENIÊNCIA - TESTES CSONLINE" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`n📖 USO:" -ForegroundColor Yellow
    Write-Host "=======" -ForegroundColor Yellow
    Write-Host ".\run-tests.ps1 -HealthCheck          # Verificação rápida de saúde" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -AllTests             # Executar todos os testes" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -OnlyTest 'Couriers'  # Teste específico" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -Help                 # Esta ajuda" -ForegroundColor White
    
    Write-Host "`n📁 LOCALIZAÇÃO DOS SCRIPTS:" -ForegroundColor Yellow
    Write-Host "============================" -ForegroundColor Yellow
    Write-Host "$testsPath" -ForegroundColor Gray
    
    Write-Host "`n📋 SCRIPTS DISPONÍVEIS:" -ForegroundColor Yellow
    Write-Host "========================" -ForegroundColor Yellow
    Write-Host "• test-couriers.ps1" -ForegroundColor Gray
    Write-Host "• test-users.ps1" -ForegroundColor Gray
    Write-Host "• test-customers.ps1" -ForegroundColor Gray
    Write-Host "• test-teams.ps1" -ForegroundColor Gray
    Write-Host "• test-deliveries.ps1" -ForegroundColor Gray
    Write-Host "• test-sms.ps1" -ForegroundColor Gray
    Write-Host "• test-login.ps1" -ForegroundColor Gray
    Write-Host "• test-all-endpoints.ps1" -ForegroundColor Gray
    Write-Host "• health-check-endpoints.ps1" -ForegroundColor Gray
    
    exit 0
}

# Verificar se a pasta de testes existe
if (-not (Test-Path $testsPath)) {
    Write-Host "❌ ERRO: Pasta de testes não encontrada!" -ForegroundColor Red
    Write-Host "   Esperado: $testsPath" -ForegroundColor Gray
    exit 1
}

Write-Host "🧪 EXECUTANDO TESTES CSONLINE" -ForegroundColor Cyan
Write-Host "Pasta de testes: $testsPath" -ForegroundColor Gray

# Navegar para a pasta de testes
Push-Location $testsPath

try {
    if ($HealthCheck) {
        Write-Host "`n🔍 Executando verificação de saúde..." -ForegroundColor Yellow
        & ".\health-check-endpoints.ps1"
    }
    elseif ($AllTests) {
        Write-Host "`n🚀 Executando todos os testes..." -ForegroundColor Yellow
        & ".\test-all-endpoints.ps1"
    }
    elseif ($OnlyTest -ne "") {
        Write-Host "`n🎯 Executando teste específico: $OnlyTest" -ForegroundColor Yellow
        & ".\test-all-endpoints.ps1" -OnlyTest $OnlyTest
    }
    else {
        Write-Host "`n❓ Nenhuma opção especificada. Use -Help para ver as opções." -ForegroundColor Yellow
        Write-Host "Executando verificação de saúde por padrão..." -ForegroundColor Gray
        & ".\health-check-endpoints.ps1"
    }
} finally {
    # Voltar para a pasta original
    Pop-Location
}

Write-Host "`n✅ Execução concluída!" -ForegroundColor Green
