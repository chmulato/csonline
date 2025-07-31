<#
Script de teste para o endpoint /couriers da aplicação CSOnline

O que faz:
- Realiza uma requisição GET para http://localhost:8080/couriers
- Exibe o status HTTP, tempo de resposta e o conteúdo retornado (em JSON)
- Trata e exibe erros de forma amigável

Como usar:
1. Certifique-se de que a aplicação está rodando localmente em http://localhost:8080
2. Execute este script no PowerShell
3. Siga as instruções na tela
#>


Write-Host "[INFO] Certifique-se de que a aplicação está rodando localmente em http://localhost:8080/csonline"
Read-Host "Pressione ENTER para continuar após iniciar a aplicação"

$endpoint = Read-Host "Informe o endpoint para testar (ex: /couriers)"
if (-not $endpoint.StartsWith("/")) { $endpoint = "/$endpoint" }
$uri = "http://localhost:8080/csonline$endpoint"
Write-Host "[INFO] Testando requisição GET em: $uri"

$start = Get-Date
try {
    Write-Host "[INFO] Enviando requisição..."
    $response = Invoke-RestMethod -Uri $uri -Method Get -ErrorAction Stop
    $status = "200 OK"
    Write-Host "[OK] Requisição bem-sucedida."
} catch {
    $status = $_.Exception.Response.StatusCode.value__ + " " + $_.Exception.Response.StatusDescription
    $response = $_.Exception.Message
    Write-Host "[ERRO] Falha na requisição: $status"
}
$end = Get-Date
$elapsed = ($end - $start).TotalMilliseconds

Write-Host "[INFO] Status HTTP: $status"
Write-Host "[INFO] Tempo de resposta: $elapsed ms"
Write-Host "[INFO] Resposta recebida:"
$response | ConvertTo-Json -Depth 5
