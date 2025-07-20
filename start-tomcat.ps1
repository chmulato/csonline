# Script diagnóstico para JSF/Jakarta EE 10 no Tomcat 10
# Limpa, compila, deploya e executa validações básicas

$TomcatHome = "C:\dev\csonline\server\apache-tomcat-10.1.43"
$env:CATALINA_HOME = $TomcatHome

# 1. Copiar config desejada
Copy-Item "$TomcatHome\conf\context-h2.xml" "$TomcatHome\conf\context.xml" -Force
# Para PostgreSQL, troque para:
# Copy-Item "$TomcatHome\conf\context-postgres.xml" "$TomcatHome\conf\context.xml" -Force

# 2. Limpar logs, WAR e pasta da aplicação
Remove-Item "$TomcatHome\logs\*" -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\webapps\csonline.war" -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\webapps\csonline" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\work" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\temp" -Recurse -Force -ErrorAction SilentlyContinue

# 3. Validar dependências e conflitos
Write-Host "Validando dependências do projeto..."
mvn dependency:tree | Out-File "diagnostico-deps.txt"
Write-Host "Árvore de dependências salva em diagnostico-deps.txt"

# 4. Compilar e gerar WAR
mvn clean package

# 5. Copiar WAR para Tomcat
Copy-Item "target\csonline.war" "$TomcatHome\webapps\csonline.war" -Force

 # 6. Iniciar Tomcat
 Start-Process -FilePath "$TomcatHome\bin\startup.bat"
 Write-Host "Tomcat 10 iniciado. Acesse http://localhost:8080/csonline"
 Start-Sleep -Seconds 5
 $logFile = "$TomcatHome\logs\catalina.$((Get-Date).ToString('yyyy-MM-dd')).log"
 if (Test-Path $logFile) {
     Write-Host "--- Primeiras linhas do catalina.log ---"
     Get-Content $logFile -TotalCount 100
     Write-Host "--- Fim do trecho ---"
    $weldLines = Select-String -Path $logFile -Pattern "WELD|weld|CDI" | Select-Object -ExpandProperty Line
    if ($weldLines) {
        Write-Host "--- Linhas relacionadas ao Weld/CDI ---"
        $weldLines | ForEach-Object { Write-Host $_ }
        Write-Host "--- Fim Weld/CDI ---"
    } else {
        Write-Host "Nenhuma linha Weld/CDI encontrada no catalina.log."
    }
 } else {
     Write-Host "Log catalina não encontrado: $logFile"
 }

# 7. Checklist de diagnóstico
Write-Host "Checklist rápido:"
Write-Host "- web.xml e faces-config.xml mínimos?"
Write-Host "- Weld (CDI) inicializa? (verifique logs)"
Write-Host "- META-INF/services correto nos jars Mojarra?"
Write-Host "- Página hello.xhtml mínima?"
Write-Host "- Log Mojarra detalhado ativado?"
Write-Host "- ResourceHandler instanciado? (verifique logs)"
Write-Host "- Teste com MyFaces se persistir erro."
