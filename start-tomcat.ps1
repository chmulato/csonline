# Script para iniciar o Tomcat 10 e subir a aplicação local
# Defina o diretório do Tomcat 10
$TomcatHome = "C:\dev\csonline\server\apache-tomcat-10.1.43"

# Defina a variável de ambiente CATALINA_HOME
$env:CATALINA_HOME = $TomcatHome

# (Opcional) Copie o arquivo de configuração desejado para context.xml
# Para H2:
Copy-Item "$TomcatHome\conf\context-h2.xml" "$TomcatHome\conf\context.xml" -Force
# Para PostgreSQL, troque para:
# Copy-Item "$TomcatHome\conf\context-postgres.xml" "$TomcatHome\conf\context.xml" -Force

# Apague os logs antigos do Tomcat
Remove-Item "$TomcatHome\logs\*" -Force -ErrorAction SilentlyContinue

# Apague o WAR antigo e a pasta da aplicação
Remove-Item "$TomcatHome\webapps\csonline.war" -Force -ErrorAction SilentlyContinue
Remove-Item "$TomcatHome\webapps\csonline" -Recurse -Force -ErrorAction SilentlyContinue

# Gere o WAR da aplicação
mvn clean package

# Copie o WAR para a pasta webapps do Tomcat 10
Copy-Item "target\csonline.war" "$TomcatHome\webapps\csonline.war" -Force

# Inicie o Tomcat 10
Start-Process -FilePath "$TomcatHome\bin\startup.bat"

Write-Host "Tomcat 10 iniciado. Acesse http://localhost:8080/csonline"
