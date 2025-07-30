# Script para configurar o DataSource JDBC do Tomcat 10+ (HSQLDB exemplo)
# Uso: pwsh ./config-tomcat10+-datasource.ps1

$tomcatLib = "server\apache-tomcat-10.1.43\lib"
$driverJar = "target\csonline-1.0-SNAPSHOT\WEB-INF\lib\hsqldb-2.7.2.jar" # Ajuste o caminho se necessário
$contextFile = "server\apache-tomcat-10.1.43\conf\context.xml"

# Copia o driver JDBC para o Tomcat
if (!(Test-Path $driverJar)) {
    Write-Host "[ERRO] Driver JDBC não encontrado em $driverJar. Certifique-se de que o JAR está disponível."
    exit 1
}
try {
    Copy-Item $driverJar $tomcatLib -Force -ErrorAction Stop
    Write-Host "[OK] Driver JDBC copiado para $tomcatLib"
} catch {
    Write-Host "[ERRO] Falha ao copiar o driver JDBC: $_"
    exit 1
}

# Cria ou atualiza o context.xml com o DataSource
$datasource = @"
<Context>
  <Resource name="jdbc/csonlineDS" auth="Container"
            type="javax.sql.DataSource"
            maxTotal="20" maxIdle="10" maxWaitMillis="-1"
            username="sa" password=""
            driverClassName="org.hsqldb.jdbcDriver"
            url="jdbc:hsqldb:mem:csonline;DB_CLOSE_DELAY=-1"/>
</Context>
"@

try {
    Set-Content -Path $contextFile -Value $datasource -Encoding UTF8 -ErrorAction Stop
    Write-Host "[OK] Arquivo context.xml atualizado com DataSource para HSQLDB."
    Write-Host "Reinicie o Tomcat para aplicar a configuração."
} catch {
    Write-Host "[ERRO] Falha ao atualizar o context.xml: $_"
    exit 1
}
