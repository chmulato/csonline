
# Script para configurar SSL (HTTPS) autoassinado no WildFly 31
# Uso: pwsh ./config-ssl-wildfly-31.ps1

param(
    [string]$WildflyPath = "C:/dev/csonline/server/wildfly-31.0.1.Final",
    [string]$KeystorePassword = "changeit",
    [string]$KeystoreFile = "keystore.p12",
    [string]$Alias = "wildfly"
)

$ErrorActionPreference = "Stop"


Write-Host "[PASSO 1] Gerando keystore autoassinado..."
$keytool = "keytool"
$keystoreFullPath = Join-Path $WildflyPath $KeystoreFile

& $keytool -genkeypair -alias $Alias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keystoreFullPath -validity 365 -storepass $KeystorePassword -dname "CN=localhost, OU=Dev, O=Empresa, L=Cidade, S=Estado, C=BR"

if (!(Test-Path $keystoreFullPath)) {
    Write-Error "[ERRO] Keystore não foi criado: $keystoreFullPath"
    exit 1
}
Write-Host "[OK] Keystore criado em: $keystoreFullPath"

# Atualizar standalone.xml
Write-Host "[PASSO 2] Atualizando standalone.xml para configurar HTTPS..."
$standaloneXml = Join-Path $WildflyPath "standalone/configuration/standalone.xml"
if (!(Test-Path $standaloneXml)) {
    Write-Error "[ERRO] Não encontrado: $standaloneXml"
    exit 1
}

Write-Host "[INFO] Lendo e modificando $standaloneXml..."

# Lê o conteúdo do standalone.xml
[xml]$xml = Get-Content $standaloneXml

# Procura o security-realm ApplicationRealm
$applicationRealm = $xml.server.'management'.'security-realms'.'security-realm' | Where-Object { $_.name -eq 'ApplicationRealm' }
if ($null -eq $applicationRealm) {
    Write-Error "[ERRO] ApplicationRealm não encontrado em $standaloneXml"
    exit 1
}

# Adiciona ou atualiza o server-identity ssl
$serverIdentity = $applicationRealm.'server-identity'
if ($serverIdentity -eq $null) {
    $serverIdentity = $xml.CreateElement('server-identity')
    $applicationRealm.AppendChild($serverIdentity) | Out-Null
}
$ssl = $serverIdentity.ssl
if ($ssl -eq $null) {
    $ssl = $xml.CreateElement('ssl')
    $serverIdentity.AppendChild($ssl) | Out-Null
}
$ssl.SetAttribute('keystore-path', $keystoreFullPath)
$ssl.SetAttribute('keystore-password', $KeystorePassword)
$ssl.SetAttribute('alias', $Alias)
$ssl.SetAttribute('key-password', $KeystorePassword)
$ssl.SetAttribute('keystore-type', 'PKCS12')

# Salva o XML modificado
$xml.Save($standaloneXml)

Write-Host "[OK] HTTPS configurado no WildFly."
Write-Host "[INFO] Reinicie o servidor para ativar o SSL."
Write-Host "Acesse: https://localhost:8443/ (ajuste a porta se necessário)"
