# Script final de correção completa dos testes
Write-Host "Executando correção final e completa dos testes..." -ForegroundColor Green

$testDir = "c:\dev\csonline\src\test\java\com\caracore\cso"

# Correções finais mais específicas
$finalCorrections = @{
    # Correções de tipos para variáveis de serviços
    "CourierService service = new TestableCourierService\(true\)" = "TestableCourierService service = new TestableCourierService(true)"
    "CustomerService service = new TestableCustomerService\(true\)" = "TestableCustomerService service = new TestableCustomerService(true)"
    "DeliveryService service = new TestableDeliveryService\(true\)" = "TestableDeliveryService service = new TestableDeliveryService(true)"
    "PriceService service = new TestablePriceService\(true\)" = "TestablePriceService service = new TestablePriceService(true)"
    "SMSService service = new TestableSMSService\(true\)" = "TestableSMSService service = new TestableSMSService(true)"
    "TeamService service = new TestableTeamService\(true\)" = "TestableTeamService service = new TestableTeamService(true)"
    "UserService service = new TestableUserService\(true\)" = "TestableUserService service = new TestableUserService(true)"
    
    # Outros padrões específicos
    "TeamService teamService = new TestableTeamService\(true\)" = "TestableTeamService teamService = new TestableTeamService(true)"
    "UserService userService = new TestableUserService\(true\)" = "TestableUserService userService = new TestableUserService(true)"
    
    # Correções de chamadas de métodos HTTP
    "\.deleteById\(\)" = ".delete()"
    
    # Corrigir referências a TestTestJPAUtil que são incorretas
    "TestTestJPAUtil\." = "TestJPAUtil."
}

# Buscar todos os arquivos de teste Java
$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter "*.java" | Where-Object { $_.Name -like "*Test.java" }

Write-Host "Encontrados $($testFiles.Count) arquivos de teste para correção final" -ForegroundColor Yellow

$processedFiles = 0
$modifiedFiles = 0

foreach ($file in $testFiles) {
    $processedFiles++
    Write-Host "[$processedFiles/$($testFiles.Count)] Corrigindo finalmente: $($file.Name)" -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    # Aplicar todas as correções finais
    foreach ($correction in $finalCorrections.GetEnumerator()) {
        $content = $content -replace $correction.Key, $correction.Value
    }
    
    # Salvar o arquivo se foi modificado
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        $modifiedFiles++
        Write-Host "  ✓ Arquivo corrigido finalmente" -ForegroundColor Green
    } else {
        Write-Host "  - Nenhuma correção final necessária" -ForegroundColor Gray
    }
}

Write-Host "`nCorreção final concluída!" -ForegroundColor Green
Write-Host "Arquivos processados: $processedFiles" -ForegroundColor White
Write-Host "Arquivos corrigidos: $modifiedFiles" -ForegroundColor White

# Executar teste para validar
Write-Host "`nExecutando testes para validar correção final..." -ForegroundColor Yellow
mvn test -Dtest="*Test" -DfailIfNoTests=false
