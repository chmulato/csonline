# Script de Verificação de Saúde dos Endpoints
# Verifica rapidamente quais endpoints estão funcionando

$baseUrl = "http://localhost:8080/csonline/api"

$endpoints = @(
    @{ Name = "Couriers (List)"; Url = "$baseUrl/couriers"; Method = "GET" },
    @{ Name = "Couriers (ID=1)"; Url = "$baseUrl/couriers/1"; Method = "GET" },
    @{ Name = "Users (List)"; Url = "$baseUrl/users"; Method = "GET" },
    @{ Name = "Users (ID=1)"; Url = "$baseUrl/users/1"; Method = "GET" },
    @{ Name = "Customers (List)"; Url = "$baseUrl/customers"; Method = "GET" },
    @{ Name = "Customers (ID=1)"; Url = "$baseUrl/customers/1"; Method = "GET" },
    @{ Name = "Teams (List)"; Url = "$baseUrl/teams"; Method = "GET" },
    @{ Name = "Teams (ID=1)"; Url = "$baseUrl/teams/1"; Method = "GET" },
    @{ Name = "Deliveries (List)"; Url = "$baseUrl/deliveries"; Method = "GET" },
    @{ Name = "SMS (List)"; Url = "$baseUrl/sms"; Method = "GET" }
)

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "VERIFICAÇÃO DE SAÚDE DOS ENDPOINTS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Data/Hora: $(Get-Date)" -ForegroundColor Cyan

$working = 0
$broken = 0

foreach ($endpoint in $endpoints) {
    Write-Host "`nTestando: $($endpoint.Name)" -ForegroundColor Yellow
    Write-Host "URL: $($endpoint.Url)" -ForegroundColor Gray
    
    try {
        $response = Invoke-RestMethod -Uri $endpoint.Url -Method $endpoint.Method -TimeoutSec 10
        
        if ($response) {
            $count = if ($response.Count) { $response.Count } else { "1 item" }
            Write-Host "✓ FUNCIONANDO - $count registros" -ForegroundColor Green
            $working++
        } else {
            Write-Host "⚠️  VAZIO - Endpoint responde mas sem dados" -ForegroundColor Yellow
            $working++
        }
    } catch {
        $statusCode = "Desconhecido"
        if ($_.Exception.Response) {
            $statusCode = $_.Exception.Response.StatusCode
        }
        Write-Host "✗ ERRO - Status: $statusCode" -ForegroundColor Red
        Write-Host "  Detalhes: $($_.Exception.Message)" -ForegroundColor DarkRed
        $broken++
    }
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "RESUMO DA VERIFICAÇÃO" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "✓ Endpoints funcionando: $working" -ForegroundColor Green
Write-Host "✗ Endpoints com problema: $broken" -ForegroundColor Red

$total = $working + $broken
$successRate = if ($total -gt 0) { [math]::Round(($working / $total) * 100, 1) } else { 0 }
Write-Host "📊 Taxa de sucesso: $successRate%" -ForegroundColor $(if ($successRate -ge 80) { "Green" } elseif ($successRate -ge 50) { "Yellow" } else { "Red" })

if ($broken -gt 0) {
    Write-Host "`n💡 DICAS PARA RESOLVER PROBLEMAS:" -ForegroundColor Yellow
    Write-Host "1. Verifique os logs da aplicação em wildfly/standalone/log/" -ForegroundColor Gray
    Write-Host "2. Verifique se há problemas de serialização JSON nas entidades" -ForegroundColor Gray
    Write-Host "3. Verifique se as consultas JPA estão corretas" -ForegroundColor Gray
    Write-Host "4. Verifique se há referências circulares não tratadas" -ForegroundColor Gray
}

Write-Host "`nVerificação concluída em: $(Get-Date)" -ForegroundColor Cyan
