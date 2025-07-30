# Script para configurar SSL no Tomcat 10+ (autoassinado)
# Uso: pwsh ./config-ssl-tomcat10+.ps1

param(
    [string]$TomcatPath = "./server/apache-tomcat-10.1.43",
    [string]$KeystorePassword = "changeit",
    [string]$KeystoreFile = "keystore.p12",
    [string]$Alias = "tomcat"
)

$ErrorActionPreference = "Stop"

Write-Host "[INFO] Gerando keystore autoassinado..."
$keytool = "keytool"
$keystoreFullPath = Join-Path $TomcatPath $KeystoreFile

& $keytool -genkeypair -alias $Alias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keystoreFullPath -validity 365 -storepass $KeystorePassword -dname "CN=localhost, OU=Dev, O=Empresa, L=Cidade, S=Estado, C=BR"

if (!(Test-Path $keystoreFullPath)) {
    Write-Error "[ERRO] Keystore não foi criado: $keystoreFullPath"
    exit 1
}
Write-Host "[OK] Keystore criado em: $keystoreFullPath"

# Atualizar server.xml
$serverXml = Join-Path $TomcatPath "conf/server.xml"
if (!(Test-Path $serverXml)) {
    Write-Error "[ERRO] Não encontrado: $serverXml"
    exit 1
}

Write-Host "[INFO] Configurando conector SSL em $serverXml..."

# Remove conector SSL antigo, se existir
$content = Get-Content $serverXml
$content = $content -replace '(?s)<!--\s*\[SSL-CONNECTOR-START\].*?\[SSL-CONNECTOR-END\]\s*-->', ''

# Adiciona novo conector SSL
$sslConnector = @"
<!-- [SSL-CONNECTOR-START]
    Adicionado por config-ssl-tomcat10+.ps1 em $(Get-Date -Format 'yyyy-MM-dd HH:mm')
-->
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
           keystoreFile="$keystoreFullPath" keystorePass="$KeystorePassword" keystoreType="PKCS12"
           clientAuth="false" sslProtocol="TLS" />
<!-- [SSL-CONNECTOR-END] -->
"@

# Insere antes do fechamento de </Service>
$idx = ($content | Select-String -Pattern "</Service>" -SimpleMatch).LineNumber[0] - 1
if ($null -eq $idx) {
    Write-Error "[ERRO] Tag </Service> não encontrada em $serverXml"
    exit 1
}
$content = $content[0..$idx-1] + $sslConnector + $content[$idx..($content.Length-1)]

Set-Content -Path $serverXml -Value $content -Encoding UTF8

Write-Host "[OK] Conector SSL configurado na porta 8443."
Write-Host "[INFO] Reinicie o Tomcat para ativar o HTTPS:"
Write-Host "       pwsh ./stop-tomcat10+.ps1"
Write-Host "       pwsh ./start-tomcat10+.ps1"
Write-Host "[INFO] Acesse: https://localhost:8443/csonline/"
