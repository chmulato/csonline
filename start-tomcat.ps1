# Script diagnóstico para JSF/Jakarta EE 10 no Tomcat 10
# Limpa, compila, deploya e executa validações básicas

$TomcatHome = "C:\dev\csonline\server\apache-tomcat-10.1.43"
$env:CATALINA_HOME = $TomcatHome

# 1. Copiar config desejada
Copy-Item "$TomcatHome\conf\context-h2.xml" "$TomcatHome\conf\context.xml" -Force
# Para PostgreSQL, troque para:
# Copy-Item "$TomcatHome\conf\context-postgres.xml" "$TomcatHome\conf\context.xml" -Force

# 2. Limpar Tomcat completamente (logs, work, temp, webapps exceto ROOT, conf antigo)
Write-Host "Limpando Tomcat completamente..."
Stop-Process -Name "java" -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Remove-Item "$TomcatHome\logs\*" -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\work" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\temp" -Recurse -Force -ErrorAction SilentlyContinue
# Remove todas as aplicações do webapps, exceto ROOT e remove WAR antigo explicitamente
Get-ChildItem "$TomcatHome\webapps" | Where-Object { $_.Name -ne "ROOT" } | ForEach-Object {
    Remove-Item $_.FullName -Recurse -Force -ErrorAction SilentlyContinue
}
# Remove WAR antigo explicitamente
if (Test-Path "$TomcatHome\webapps\csonline.war") {
    Remove-Item "$TomcatHome\webapps\csonline.war" -Force -ErrorAction SilentlyContinue
}
# Remove context.xml antigo
if (Test-Path "$TomcatHome\conf\context.xml") {
    Remove-Item "$TomcatHome\conf\context.xml" -Force -ErrorAction SilentlyContinue
}
Write-Host "Tomcat limpo."

# Pasta para logs de diagnóstico
$DiagLogDir = "c:\dev\csonline\diagnostico-logs"
if (!(Test-Path $DiagLogDir)) {
    New-Item -ItemType Directory -Path $DiagLogDir | Out-Null
}

# 3. Validar dependências e conflitos
Write-Host "Validando dependências do projeto..."
mvn dependency:tree | Out-File "$DiagLogDir\diagnostico-deps.txt"
Write-Host "Árvore de dependências salva em $DiagLogDir\diagnostico-deps.txt"


# 4. Compilar e gerar WAR
$mvnLogFile = "$DiagLogDir\maven-build.log"
$mvnResult = mvn clean package | Tee-Object -FilePath $mvnLogFile

# 5. Checar se WAR foi gerado
$warPath = "target\csonline.war"
if (!(Test-Path $warPath)) {
    Write-Host "ATENÇÃO: O arquivo $warPath não foi gerado. Tentando rodar 'mvn clean package' novamente..."
    $mvnResult2 = mvn clean package | Tee-Object -FilePath $mvnLogFile -Append
    if (!(Test-Path $warPath)) {
        Write-Host "ERRO: O arquivo $warPath ainda não foi gerado. Verifique o log do Maven em $mvnLogFile."
        exit 1
    }
}
Copy-Item $warPath "$TomcatHome\webapps\csonline.war" -Force

# 6. Iniciar Tomcat
Start-Process -FilePath "$TomcatHome\bin\startup.bat"
Write-Host "Tomcat 10 iniciado. Acesse http://localhost:8080/csonline"
Start-Sleep -Seconds 5
$logFile = "$TomcatHome\logs\catalina.$((Get-Date).ToString('yyyy-MM-dd')).log"
if (Test-Path $logFile) {
    Write-Host "--- Primeiras linhas do catalina.log ---"
    Get-Content $logFile -TotalCount 100
    $weldLines = Select-String -Path $logFile -Pattern "WELD|weld|CDI" | Select-Object -ExpandProperty Line
    if ($weldLines) {
        Write-Host "--- Linhas relacionadas ao Weld/CDI ---"
        $weldLines | ForEach-Object { Write-Host $_ }
        Write-Host "--- Fim Weld/CDI ---"
    }
    # Copiar catalina.log para pasta de diagnóstico
    Copy-Item $logFile "$DiagLogDir\catalina.log" -Force
    # Copiar outros logs relevantes do Tomcat
    Get-ChildItem "$TomcatHome\logs\*.log" | ForEach-Object {
        Copy-Item $_.FullName "$DiagLogDir\$($_.Name)" -Force
    }
}
else {
    Write-Host "Log catalina não encontrado: $logFile"
}
