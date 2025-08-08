# Script para Visualização de Cobertura de Testes
# CSOnline Frontend - Vue.js

param(
    [switch]$Open,
    [switch]$Watch,
    [switch]$UI,
    [switch]$Summary
)

Write-Host "CSOnline Frontend - Coverage Analysis" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Função para executar testes com cobertura
function RunCoverage {
    Write-Host "Executando testes com cobertura..." -ForegroundColor Yellow
    npm run test:coverage
}

# Função para abrir relatório HTML
function OpenReport {
    $coverageFile = "coverage\index.html"
    if (Test-Path $coverageFile) {
        Write-Host "Abrindo relatório HTML..." -ForegroundColor Green
        Start-Process $coverageFile
    } else {
        Write-Host "Relatório não encontrado. Execute primeiro: npm run test:coverage" -ForegroundColor Red
    }
}

# Função para modo watch
function WatchCoverage {
    Write-Host "Executando testes em modo watch com cobertura..." -ForegroundColor Yellow
    npm run test:watch
}

# Função para UI interativa
function RunUI {
    Write-Host "Abrindo interface visual dos testes..." -ForegroundColor Yellow
    npm run test:coverage:ui
}

# Função para resumo rápido
function ShowSummary {
    Write-Host "Resumo de Cobertura Atual:" -ForegroundColor Green
    Write-Host ""
    
    if (Test-Path "coverage\coverage-final.json") {
        $coverage = Get-Content "coverage\coverage-final.json" | ConvertFrom-Json
        $total = $coverage.total
        
        Write-Host "Lines:      $($total.lines.pct)%" -ForegroundColor $(if($total.lines.pct -ge 70) {"Green"} else {"Red"})
        Write-Host "Functions:  $($total.functions.pct)%" -ForegroundColor $(if($total.functions.pct -ge 70) {"Green"} else {"Red"})
        Write-Host "Branches:   $($total.branches.pct)%" -ForegroundColor $(if($total.branches.pct -ge 70) {"Green"} else {"Red"})
        Write-Host "Statements: $($total.statements.pct)%" -ForegroundColor $(if($total.statements.pct -ge 70) {"Green"} else {"Red"})
        Write-Host ""
        Write-Host "Meta: 70% em todas as métricas" -ForegroundColor Blue
    } else {
        Write-Host "Dados de cobertura não encontrados. Execute primeiro: npm run test:coverage" -ForegroundColor Red
    }
}

# Menu interativo se nenhum parâmetro for fornecido
if (-not $Open -and -not $Watch -and -not $UI -and -not $Summary) {
    Write-Host "Selecione uma opção:" -ForegroundColor White
    Write-Host "1. Executar testes com cobertura" -ForegroundColor White
    Write-Host "2. Abrir relatório HTML" -ForegroundColor White
    Write-Host "3. Modo watch (contínuo)" -ForegroundColor White
    Write-Host "4. Interface visual (UI)" -ForegroundColor White
    Write-Host "5. Resumo rápido" -ForegroundColor White
    Write-Host "6. Sair" -ForegroundColor White
    Write-Host ""
    
    $choice = Read-Host "Digite sua escolha (1-6)"
    
    switch ($choice) {
        "1" { RunCoverage; OpenReport }
        "2" { OpenReport }
        "3" { WatchCoverage }
        "4" { RunUI }
        "5" { ShowSummary }
        "6" { exit }
        default { Write-Host "Opção inválida!" -ForegroundColor Red }
    }
} else {
    # Executar baseado nos parâmetros
    if ($Summary) { ShowSummary }
    if ($Watch) { WatchCoverage }
    if ($UI) { RunUI }
    if ($Open) { OpenReport }
    if (-not $Summary -and -not $Watch -and -not $UI -and -not $Open) {
        RunCoverage
        OpenReport
    }
}

Write-Host ""
Write-Host "Operação concluída!" -ForegroundColor Green
