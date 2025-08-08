# Script PowerShell para migrar testes automaticamente
# Substitui JPAUtil por TestJPAUtil em todos os arquivos de teste

Write-Host "🔄 Iniciando migração automática dos testes..." -ForegroundColor Green

# Diretório dos testes
$testDir = "src\test\java"

# Obter todos os arquivos .java
$javaFiles = Get-ChildItem -Path $testDir -Filter "*.java" -Recurse

Write-Host "📦 Substituindo imports em $($javaFiles.Count) arquivos..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # 1. Substituir imports
    $content = $content -replace 'import com\.caracore\.cso\.repository\.JPAUtil;', 'import com.caracore.cso.repository.TestJPAUtil;'
    
    # 2. Substituir chamadas do JPAUtil
    $content = $content -replace 'JPAUtil\.getEntityManager\(\)', 'TestJPAUtil.getEntityManager()'
    
    # 3. Substituir referências diretas  
    $content = $content -replace 'com\.caracore\.cso\.repository\.JPAUtil', 'com.caracore.cso.repository.TestJPAUtil'
    
    # 4. Substituir jakarta.persistence.EntityManager em lugar de imports específicos
    $content = $content -replace 'jakarta\.persistence\.EntityManager em = com\.caracore\.cso\.repository\.JPAUtil', 'jakarta.persistence.EntityManager em = com.caracore.cso.repository.TestJPAUtil'
    
    Set-Content $file.FullName -Value $content -NoNewline
}

Write-Host "✅ Migração concluída!" -ForegroundColor Green
Write-Host "📋 Resumo:" -ForegroundColor Cyan
Write-Host "   - $($javaFiles.Count) arquivos processados" -ForegroundColor White
Write-Host "   - JPAUtil → TestJPAUtil" -ForegroundColor White
Write-Host "📋 Próximos passos manuais:" -ForegroundColor Yellow
Write-Host "   1. Executar mvn test para validar" -ForegroundColor White
Write-Host "   2. Corrigir erros pontuais se necessário" -ForegroundColor White
