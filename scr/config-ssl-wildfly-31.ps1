
# Script para configurar SSL (HTTPS) autoassinado no WildFly 31
# Uso: pwsh ./config-ssl-wildfly-31.ps1


# Caminhos relativos à raiz do projeto
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition

param(
    [string]$WildflyPath = (Join-Path $root "server/wildfly-31.0.1.Final"),
    [string]$KeystorePassword = "changeit",
    [string]$KeystoreFile = "keystore.p12",
    [string]$Alias = "wildfly"
)

$ErrorActionPreference = "Stop"



Write-Host "[PASSO 1] Gerando keystore autoassinado..."
$keytool = "keytool"
$keystoreFullPath = Join-Path $WildflyPath $KeystoreFile

Write-Host "[DEBUG] Executando keytool para gerar o keystore..."
$keytoolOutput = & $keytool -genkeypair -alias $Alias -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore $keystoreFullPath -validity 365 -storepass $KeystorePassword -dname "CN=localhost, OU=Dev, O=Empresa, L=Cidade, S=Estado, C=BR" 2>&1
if ($LASTEXITCODE -eq 0 -and (Test-Path $keystoreFullPath)) {
    Write-Host "[OK] Keystore criado em: $keystoreFullPath"
} else {
    Write-Host "[ERRO] Falha ao criar o keystore. Saída do keytool:"
    $keytoolOutput | ForEach-Object { Write-Host $_ }
    exit 1
}
Write-Host "--------------------------------------------------"

# Atualizar standalone.xml

Write-Host "[PASSO 2] Atualizando standalone.xml para configurar HTTPS..."
$standaloneXml = Join-Path $WildflyPath "standalone/configuration/standalone.xml"
if (!(Test-Path $standaloneXml)) {
    Write-Host "[ERRO] Não encontrado: $standaloneXml"
    exit 1
}
Write-Host "[OK] standalone.xml localizado em: $standaloneXml"
Write-Host "[INFO] Lendo e modificando $standaloneXml..."


# Lê o conteúdo do standalone.xml
[xml]$xml = Get-Content $standaloneXml


# Procura o security-realm ApplicationRealm
$applicationRealm = $xml.server.'management'.'security-realms'.'security-realm' | Where-Object { $_.name -eq 'ApplicationRealm' }
if ($null -eq $applicationRealm) {
    Write-Host "[ERRO] ApplicationRealm não encontrado em $standaloneXml"
    exit 1
}


# Adiciona ou atualiza o server-identity ssl
$serverIdentity = $applicationRealm.'server-identity'
if ($serverIdentity -eq $null) {
    $serverIdentity = $xml.CreateElement('server-identity')
    $applicationRealm.AppendChild($serverIdentity) | Out-Null
    Write-Host "[INFO] Criado elemento server-identity."
}
$ssl = $serverIdentity.ssl
if ($ssl -eq $null) {
    $ssl = $xml.CreateElement('ssl')
    $serverIdentity.AppendChild($ssl) | Out-Null
    Write-Host "[INFO] Criado elemento ssl."
} else {
    Write-Host "[INFO] Atualizando elemento ssl existente."
}
$ssl.SetAttribute('keystore-path', $keystoreFullPath)
$ssl.SetAttribute('keystore-password', $KeystorePassword)
$ssl.SetAttribute('alias', $Alias)
$ssl.SetAttribute('key-password', $KeystorePassword)
$ssl.SetAttribute('keystore-type', 'PKCS12')


# Salva o XML modificado
$xml.Save($standaloneXml)
if ($?) {
    Write-Host "[OK] HTTPS configurado no WildFly."
    Write-Host "[INFO] Reinicie o servidor para ativar o SSL."
    Write-Host "Acesse: https://localhost:8443/ (ajuste a porta se necessário)"
} else {
    Write-Host "[ERRO] Falha ao salvar o standalone.xml."
    exit 1
}
