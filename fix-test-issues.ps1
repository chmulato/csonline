# Script de correção avançada para os testes
Write-Host "Iniciando correção avançada dos testes..." -ForegroundColor Green

$testDir = "c:\dev\csonline\src\test\java\com\caracore\cso"

# Padrões de correção mais precisos
$corrections = @{
    # Corrigir tipos de variáveis
    "UserService userService = new TestableUserService\(true\)" = "TestableUserService userService = new TestableUserService(true)"
    "TeamService teamService = new TestableTeamService\(true\)" = "TestableTeamService teamService = new TestableTeamService(true)"
    "CourierService courierService = new TestableCourierService\(true\)" = "TestableCourierService courierService = new TestableCourierService(true)"
    "CustomerService customerService = new TestableCustomerService\(true\)" = "TestableCustomerService customerService = new TestableCustomerService(true)"
    "DeliveryService deliveryService = new TestableDeliveryService\(true\)" = "TestableDeliveryService deliveryService = new TestableDeliveryService(true)"
    "PriceService priceService = new TestablePriceService\(true\)" = "TestablePriceService priceService = new TestablePriceService(true)"
    "SMSService smsService = new TestableSMSService\(true\)" = "TestableSMSService smsService = new TestableSMSService(true)"
    
    # Corrigir métodos que não existem
    "findAllByBusiness\(" = "findByBusiness("
    "\.delete\(" = ".deleteById("
    "TestTestJPAUtil" = "TestJPAUtil"
}

# Buscar todos os arquivos de teste Java
$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter "*.java" | Where-Object { $_.Name -like "*Test.java" }

Write-Host "Encontrados $($testFiles.Count) arquivos de teste para correção" -ForegroundColor Yellow

$processedFiles = 0
$modifiedFiles = 0

foreach ($file in $testFiles) {
    $processedFiles++
    Write-Host "[$processedFiles/$($testFiles.Count)] Corrigindo: $($file.Name)" -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    # Aplicar todas as correções
    foreach ($correction in $corrections.GetEnumerator()) {
        $content = $content -replace $correction.Key, $correction.Value
    }
    
    # Salvar o arquivo se foi modificado
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        $modifiedFiles++
        Write-Host "  ✓ Arquivo corrigido" -ForegroundColor Green
    } else {
        Write-Host "  - Nenhuma correção necessária" -ForegroundColor Gray
    }
}

Write-Host "`nCorreção avançada concluída!" -ForegroundColor Green
Write-Host "Arquivos processados: $processedFiles" -ForegroundColor White
Write-Host "Arquivos corrigidos: $modifiedFiles" -ForegroundColor White
