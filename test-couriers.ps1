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


Write-Host "Certifique-se de que a aplicação está rodando localmente em http://localhost:8080/csonline"
Read-Host "Pressione ENTER para continuar após iniciar a aplicação"

$endpoint = Read-Host "Informe o endpoint para testar (ex: /couriers)"
if (-not $endpoint.StartsWith("/")) { $endpoint = "/$endpoint" }
$uri = "http://localhost:8080/csonline$endpoint"
Write-Host "Testando GET $uri"

$start = Get-Date
try {
    $response = Invoke-RestMethod -Uri $uri -Method Get -ErrorAction Stop
    $status = "200 OK"
} catch {
    $status = $_.Exception.Response.StatusCode.value__ + " " + $_.Exception.Response.StatusDescription
    $response = $_.Exception.Message
}
$end = Get-Date
$elapsed = ($end - $start).TotalMilliseconds

Write-Host "Status HTTP: $status"
Write-Host "Tempo de resposta: $elapsed ms"
Write-Host "Resposta:"
$response | ConvertTo-Json -Depth 5
