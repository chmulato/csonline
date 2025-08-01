# build-frontend.ps1
# Script para build e distribuição do front-end Vue dentro do WAR Java

param(
[string]$FrontendPath = "../frontend",
[string]$WebappPath = "../src/main/webapp"
)

Write-Host "[INFO] Etapa 1: Iniciando build do front-end Vue..."

Push-Location $FrontendPath

if (!(Test-Path "package.json")) {
    Write-Host "[ERRO] package.json não encontrado em $FrontendPath"
    Pop-Location
    exit 1
}
Write-Host "[OK] package.json encontrado."

npm install
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERRO] Falha ao instalar dependências NPM."
    Pop-Location
    exit 1
}
Write-Host "[OK] Dependências NPM instaladas."

npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERRO] Falha ao executar build do Vue."
    Pop-Location
    exit 1
}
Write-Host "[OK] Build do Vue concluído."

Pop-Location

Write-Host "[INFO] Etapa 2: Copiando arquivos gerados para $WebappPath..."

$distPath = Join-Path $FrontendPath "dist"
if (!(Test-Path $distPath)) {
    Write-Host "[ERRO] Pasta dist não encontrada após build."
    exit 1
}
Write-Host "[OK] Pasta dist encontrada."

# Remove arquivos antigos do webapp (exceto WEB-INF)
Get-ChildItem -Path $WebappPath -Exclude "WEB-INF" | Remove-Item -Recurse -Force

# Copia novos arquivos do dist para webapp
Copy-Item -Path "$distPath\*" -Destination $WebappPath -Recurse -Force
Write-Host "[OK] Arquivos do front-end copiados para $WebappPath."

Write-Host "[OK] Processo finalizado com sucesso. O front-end Vue está pronto para empacotamento no WAR."
