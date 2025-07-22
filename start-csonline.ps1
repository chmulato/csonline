# Script para executar CSOnline com Jakarta EE 10 no Jetty 11
# Limpa, compila e executa o projeto atualizado

Write-Host "=== CSOnline - Jakarta EE 10 com Jetty 11 ===" -ForegroundColor Green

# 1. Parar processos Java relacionados ao projeto (mais seguro)
Write-Host "Verificando processos Java do CSOnline..." -ForegroundColor Yellow

# Buscar apenas processos Maven/Jetty relacionados ao projeto
$mvnProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
    try {
        $commandLine = (Get-WmiObject Win32_Process -Filter "ProcessId = $($_.Id)" -ErrorAction SilentlyContinue).CommandLine
        $commandLine -like "*jetty*" -or $commandLine -like "*maven*" -or $commandLine -like "*csonline*"
    } catch {
        $false
    }
}

if ($mvnProcesses) {
    Write-Host "Parando $($mvnProcesses.Count) processo(s) do CSOnline..." -ForegroundColor Yellow
    $mvnProcesses | ForEach-Object { Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue }
    Start-Sleep -Seconds 2
} else {
    Write-Host "Nenhum processo anterior do CSOnline encontrado." -ForegroundColor Gray
}

# 2. Limpar target para rebuild completo
Write-Host "Limpando target..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item "target" -Recurse -Force -ErrorAction SilentlyContinue
}

# 3. Compilar projeto
Write-Host "Compilando projeto Jakarta EE 10..." -ForegroundColor Yellow
$mvnResult = mvn clean compile

# 4. Verificar se compilação foi bem-sucedida
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Falha na compilação. Verifique os logs do Maven." -ForegroundColor Red
    exit 1
}

# 5. Verificar se porta 8080 está livre
$portInUse = netstat -an | Select-String "8080" | Select-String "LISTENING"
if ($portInUse) {
    Write-Host "AVISO: Porta 8080 em uso. Tentando liberar..." -ForegroundColor Yellow
    $processUsingPort = netstat -ano | Select-String "8080" | Select-String "LISTENING"
    if ($processUsingPort) {
        $pid = ($processUsingPort -split '\s+')[-1]
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
    }
}

# 6. Inicializar H2 Database e iniciar Jetty
Write-Host "Iniciando CSOnline com Jetty 11..." -ForegroundColor Green
Write-Host "URLs disponíveis:" -ForegroundColor Cyan
Write-Host "  http://localhost:8080/csonline/" -ForegroundColor White
Write-Host "  http://localhost:8080/csonline/login.xhtml" -ForegroundColor White
Write-Host ""
Write-Host "Usuário de teste H2:" -ForegroundColor Cyan
Write-Host "  Login: admin" -ForegroundColor White
Write-Host "  Senha: 123" -ForegroundColor White
Write-Host ""
Write-Host "Tecnologias carregadas:" -ForegroundColor Cyan
Write-Host "  ✅ Jakarta EE 10" -ForegroundColor Green
Write-Host "  ✅ PrimeFaces 14.0.0-jakarta" -ForegroundColor Green
Write-Host "  ✅ MyFaces 4.0.x" -ForegroundColor Green
Write-Host "  ✅ Weld 5.1.2.Final" -ForegroundColor Green
Write-Host "  ✅ H2 Database 2.3.232" -ForegroundColor Green
Write-Host "  ✅ Log4j 2.23.1" -ForegroundColor Green
Write-Host ""
Write-Host "Para parar o servidor, use Ctrl+C ou execute:" -ForegroundColor Cyan
Write-Host "  .\stop-csonline.ps1" -ForegroundColor White
Write-Host ""
Write-Host "Executando: mvn jetty:run" -ForegroundColor Yellow

# Executa Jetty (comando bloqueante)
mvn jetty:run
