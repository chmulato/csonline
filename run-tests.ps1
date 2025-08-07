# Script de conveniência para executar testes da aplicação CSOnline com JWT
# Versão: 2.0 - Suporte completo a JWT
# Este script permite executar os testes da raiz do projeto

param(
    [string]$TestType = "help",
    [string]$OnlyTest = "",
    [switch]$HealthCheck,
    [switch]$AllTests,
    [switch]$JWTSecurity,
    [switch]$Help,
    [switch]$Verbose,
    [string]$Login = "empresa",
    [string]$Password = "empresa123"
)

$testsPath = Join-Path $PSScriptRoot "scr\tests"

if ($Help -or ($TestType -eq "help" -and -not $JWTSecurity -and -not $HealthCheck -and -not $AllTests -and $OnlyTest -eq "")) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT DE CONVENIÊNCIA - TESTES CSONLINE JWT 2.0" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`n� NOVIDADE: AUTENTICAÇÃO JWT IMPLEMENTADA!" -ForegroundColor Green
    Write-Host "Todos os scripts agora trabalham com JWT automaticamente" -ForegroundColor Gray
    
    Write-Host "`n�📖 USO BÁSICO:" -ForegroundColor Yellow
    Write-Host "===============" -ForegroundColor Yellow
    Write-Host ".\run-tests.ps1 -JWTSecurity           # 🔒 Teste de segurança JWT (RECOMENDADO)" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -HealthCheck           # 🏥 Verificação rápida de saúde" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -AllTests              # 🚀 Executar todos os testes JWT" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -OnlyTest 'Users'      # 🎯 Teste específico" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -Help                  # 📚 Esta ajuda" -ForegroundColor White
    
    Write-Host "`n⚙️ USO AVANÇADO:" -ForegroundColor Yellow
    Write-Host "=================" -ForegroundColor Yellow
    Write-Host ".\run-tests.ps1 -AllTests -Verbose                    # Com logs detalhados" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -AllTests -Login 'admin' -Password 'admin123'" -ForegroundColor White
    Write-Host ".\run-tests.ps1 -JWTSecurity -Verbose                 # Segurança com detalhes" -ForegroundColor White
    
    Write-Host "`n📁 LOCALIZAÇÃO DOS SCRIPTS:" -ForegroundColor Yellow
    Write-Host "============================" -ForegroundColor Yellow
    Write-Host "$testsPath" -ForegroundColor Gray
    
    Write-Host "`n📋 SCRIPTS DISPONÍVEIS JWT 2.0:" -ForegroundColor Yellow
    Write-Host "=================================" -ForegroundColor Yellow
    Write-Host "🆕 jwt-utility.ps1                    # Funções auxiliares JWT" -ForegroundColor Green
    Write-Host "🆕 test-jwt-security.ps1              # Teste completo de segurança" -ForegroundColor Green
    Write-Host "🔄 test-login.ps1                     # Login com JWT" -ForegroundColor Cyan
    Write-Host "🔄 test-users.ps1                     # Usuários com JWT" -ForegroundColor Cyan
    Write-Host "🔄 test-couriers.ps1                  # Entregadores com JWT" -ForegroundColor Cyan
    Write-Host "   test-customers.ps1                 # Clientes com JWT" -ForegroundColor Gray
    Write-Host "   test-deliveries.ps1                # Entregas com JWT" -ForegroundColor Gray
    Write-Host "   test-teams.ps1                     # Equipes (404 - não implementado)" -ForegroundColor Yellow
    Write-Host "   test-sms.ps1                       # SMS com JWT" -ForegroundColor Gray
    Write-Host "🔄 test-all-endpoints.ps1             # Orquestrador principal" -ForegroundColor Cyan
    Write-Host "   health-check-endpoints.ps1         # Verificação de saúde" -ForegroundColor Gray
    
    Write-Host "`n🎯 RECOMENDAÇÕES:" -ForegroundColor Yellow
    Write-Host "=================" -ForegroundColor Yellow
    Write-Host "1. 🔒 Primeiro, execute: .\run-tests.ps1 -JWTSecurity" -ForegroundColor Green
    Write-Host "   Para validar a segurança JWT do sistema" -ForegroundColor Gray
    Write-Host "2. 🚀 Depois, execute: .\run-tests.ps1 -AllTests" -ForegroundColor Green  
    Write-Host "   Para testar todos os endpoints funcionais" -ForegroundColor Gray
    Write-Host "3. 🏥 Para verificação rápida: .\run-tests.ps1 -HealthCheck" -ForegroundColor Green
    Write-Host "   Para status básico sem autenticação" -ForegroundColor Gray
    
    exit 0
}

# Verificar se a pasta de testes existe
if (-not (Test-Path $testsPath)) {
    Write-Host "❌ ERRO: Pasta de testes não encontrada!" -ForegroundColor Red
    Write-Host "   Esperado: $testsPath" -ForegroundColor Gray
    exit 1
}

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "🧪 EXECUTANDO TESTES CSONLINE JWT 2.0" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "📍 Pasta de testes: $testsPath" -ForegroundColor Gray
Write-Host "🔐 Autenticação: JWT Bearer Token" -ForegroundColor Gray
Write-Host "👤 Login: $Login" -ForegroundColor Gray

# Navegar para a pasta de testes
Push-Location $testsPath

try {
    if ($JWTSecurity) {
        Write-Host "`n🔒 Executando teste completo de segurança JWT..." -ForegroundColor Yellow
        Write-Host "Este é o teste mais importante - valida toda a segurança JWT" -ForegroundColor Gray
        if ($Verbose) {
            & ".\test-jwt-security.ps1" -Verbose -Login $Login -Password $Password
        } else {
            & ".\test-jwt-security.ps1" -Login $Login -Password $Password
        }
    }
    elseif ($HealthCheck) {
        Write-Host "`n🏥 Executando verificação de saúde..." -ForegroundColor Yellow
        Write-Host "Verificação básica sem autenticação JWT" -ForegroundColor Gray
        & ".\health-check-endpoints.ps1"
    }
    elseif ($AllTests) {
        Write-Host "`n🚀 Executando todos os testes JWT..." -ForegroundColor Yellow
        Write-Host "Testes completos com autenticação automática" -ForegroundColor Gray
        $params = @("-Login", $Login, "-Password", $Password)
        if ($Verbose) { $params += "-Verbose" }
        & ".\test-all-endpoints.ps1" @params
    }
    elseif ($OnlyTest -ne "") {
        Write-Host "`n🎯 Executando teste específico: $OnlyTest" -ForegroundColor Yellow
        Write-Host "Teste individual com autenticação JWT" -ForegroundColor Gray
        $params = @("-OnlyTest", $OnlyTest, "-Login", $Login, "-Password", $Password)
        if ($Verbose) { $params += "-Verbose" }
        & ".\test-all-endpoints.ps1" @params
    }
    else {
        Write-Host "`n❓ Nenhuma opção especificada." -ForegroundColor Yellow
        Write-Host "🔒 Executando teste de segurança JWT por padrão..." -ForegroundColor Green
        Write-Host "Use -Help para ver todas as opções disponíveis" -ForegroundColor Gray
        & ".\test-jwt-security.ps1" -Login $Login -Password $Password
    }
} catch {
    Write-Host "`n❌ ERRO durante execução dos testes:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
} finally {
    # Voltar para a pasta original
    Pop-Location
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "✅ EXECUÇÃO DE TESTES CONCLUÍDA!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "📊 Para ver estatísticas detalhadas, use: -Verbose" -ForegroundColor Gray
Write-Host "📖 Documentação completa: ./doc/AUTENTICACAO_JWT.md" -ForegroundColor Gray
Write-Host "🔧 Scripts individuais: ./scr/tests/" -ForegroundColor Gray
