# Script para configurar o DataSource JDBC do Tomcat 10+ (HSQLDB exemplo)
# Uso: pwsh ./config-tomcat10+-datasource.ps1

$tomcatLib = "server\apache-tomcat-10.1.43\lib"
$driverJar = "target\dependency\hsqldb-2.7.2.jar" # Ajuste o caminho se necessário
$contextFile = "server\apache-tomcat-10.1.43\conf\context.xml"

# Copia o driver JDBC para o Tomcat
if (!(Test-Path $driverJar)) {
    Write-Host "Driver JDBC não encontrado em $driverJar. Certifique-se de que o JAR está disponível."
    exit 1
}
Copy-Item $driverJar $tomcatLib -Force
Write-Host "Driver JDBC copiado para $tomcatLib"

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

Set-Content -Path $contextFile -Value $datasource -Encoding UTF8
Write-Host "Arquivo context.xml atualizado com DataSource para HSQLDB."

Write-Host "Reinicie o Tomcat para aplicar a configuração."
