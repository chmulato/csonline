# Script para migrar todos os testes para usar serviços testáveis
Write-Host "Iniciando migração dos testes para serviços testáveis..." -ForegroundColor Green

$testDir = "c:\dev\csonline\src\test\java\com\caracore\cso"

# Lista de serviços e suas respectivas classes testáveis
$serviceMappings = @{
    "new UserService()" = "new TestableUserService(true)"
    "new TeamService()" = "new TestableTeamService(true)"
    "new CourierService()" = "new TestableCourierService(true)"
    "new CustomerService()" = "new TestableCustomerService(true)"
    "new DeliveryService()" = "new TestableDeliveryService(true)"
    "new PriceService()" = "new TestablePriceService(true)"
    "new SMSService()" = "new TestableSMSService(true)"
}

# Importações que devem ser adicionadas
$testableImports = @(
    "import com.caracore.cso.service.TestableUserService;",
    "import com.caracore.cso.service.TestableTeamService;",
    "import com.caracore.cso.service.TestableCourierService;",
    "import com.caracore.cso.service.TestableCustomerService;",
    "import com.caracore.cso.service.TestableDeliveryService;",
    "import com.caracore.cso.service.TestablePriceService;",
    "import com.caracore.cso.service.TestableSMSService;"
)

# Buscar todos os arquivos de teste Java
$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter "*.java" | Where-Object { $_.Name -like "*Test.java" }

Write-Host "Encontrados $($testFiles.Count) arquivos de teste" -ForegroundColor Yellow

$processedFiles = 0
$modifiedFiles = 0

foreach ($file in $testFiles) {
    $processedFiles++
    Write-Host "[$processedFiles/$($testFiles.Count)] Processando: $($file.Name)" -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    $needsModification = $false
    
    # Verificar se o arquivo precisa de modificação
    foreach ($oldService in $serviceMappings.Keys) {
        if ($content -match [regex]::Escape($oldService)) {
            $needsModification = $true
            break
        }
    }
    
    if (-not $needsModification) {
        Write-Host "  ✓ Não precisa modificação" -ForegroundColor Green
        continue
    }
    
    # Aplicar substituições
    foreach ($mapping in $serviceMappings.GetEnumerator()) {
        $content = $content -replace [regex]::Escape($mapping.Key), $mapping.Value
    }
    
    # Adicionar imports necessários
    $lines = $content -split "`n"
    $packageIndex = -1
    $lastImportIndex = -1
    
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match "^package ") {
            $packageIndex = $i
        }
        if ($lines[$i] -match "^import ") {
            $lastImportIndex = $i
        }
    }
    
    # Inserir imports após o último import existente
    if ($lastImportIndex -ge 0) {
        $insertIndex = $lastImportIndex + 1
        $newLines = @()
        $newLines += $lines[0..$lastImportIndex]
        
        foreach ($import in $testableImports) {
            if ($content -notmatch [regex]::Escape($import)) {
                $newLines += $import
            }
        }
        
        $newLines += $lines[($lastImportIndex + 1)..($lines.Count - 1)]
        $content = $newLines -join "`n"
    }
    
    # Salvar o arquivo modificado
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        $modifiedFiles++
        Write-Host "  ✓ Arquivo modificado com sucesso" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ Nenhuma alteração necessária" -ForegroundColor Yellow
    }
}

Write-Host "`nMigração concluída!" -ForegroundColor Green
Write-Host "Arquivos processados: $processedFiles" -ForegroundColor White
Write-Host "Arquivos modificados: $modifiedFiles" -ForegroundColor White

# Executar teste para validar
Write-Host "`nExecutando testes para validar migração..." -ForegroundColor Yellow
Set-Location "c:\dev\csonline"
mvn test -Dtest="*Test" -DfailIfNoTests=false
