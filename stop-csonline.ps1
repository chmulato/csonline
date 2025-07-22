# Stop CSOnline - Jakarta EE 10 Application
# Encoding: UTF-8

Write-Host "=== Parando CSOnline - Jakarta EE 10 ===" -ForegroundColor Yellow

# Função para encontrar processos Java do CSOnline
function Get-CSOnlineProcesses {
    $processes = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
        $_.CommandLine -like "*jetty*" -or 
        $_.CommandLine -like "*csonline*" -or
        $_.CommandLine -like "*mvn*jetty:run*"
    }
    return $processes
}

# Verifica se há processos do CSOnline rodando
Write-Host "Verificando processos Java do CSOnline..." -ForegroundColor Cyan
$csonlineProcesses = Get-CSOnlineProcesses

if ($csonlineProcesses.Count -eq 0) {
    Write-Host "✅ Nenhum processo do CSOnline encontrado." -ForegroundColor Green
    exit 0
}

Write-Host "📋 Encontrados $($csonlineProcesses.Count) processo(s) do CSOnline:" -ForegroundColor Yellow
foreach ($proc in $csonlineProcesses) {
    Write-Host "   PID: $($proc.Id) | Memória: $([math]::Round($proc.WorkingSet64/1MB, 1)) MB" -ForegroundColor White
}

# Para os processos graciosamente
Write-Host "`n🛑 Parando processos do CSOnline..." -ForegroundColor Red
$stopCount = 0

foreach ($proc in $csonlineProcesses) {
    try {
        Write-Host "   Parando PID $($proc.Id)..." -ForegroundColor Yellow
        
        # Tenta parar graciosamente primeiro
        $proc.CloseMainWindow() | Out-Null
        Start-Sleep -Seconds 2
        
        # Se ainda estiver rodando, força a parada
        if (!$proc.HasExited) {
            $proc.Kill()
            Write-Host "   ⚠️  Processo $($proc.Id) foi forçado a parar." -ForegroundColor Orange
        } else {
            Write-Host "   ✅ Processo $($proc.Id) parou graciosamente." -ForegroundColor Green
        }
        
        $stopCount++
    }
    catch {
        Write-Host "   ❌ Erro ao parar processo $($proc.Id): $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Aguarda um momento e verifica se todos pararam
Start-Sleep -Seconds 3
$remainingProcesses = Get-CSOnlineProcesses

if ($remainingProcesses.Count -eq 0) {
    Write-Host "`n✅ Todos os processos do CSOnline foram parados com sucesso!" -ForegroundColor Green
    Write-Host "📊 Total de processos parados: $stopCount" -ForegroundColor Cyan
} else {
    Write-Host "`n⚠️  Ainda há $($remainingProcesses.Count) processo(s) rodando:" -ForegroundColor Yellow
    foreach ($proc in $remainingProcesses) {
        Write-Host "   PID: $($proc.Id) | Status: $($proc.ProcessName)" -ForegroundColor White
    }
    Write-Host "`n💡 Você pode tentar parar manualmente com:" -ForegroundColor Cyan
    Write-Host "   taskkill /F /PID <PID>" -ForegroundColor White
}

Write-Host "`n🏁 Script de parada concluído." -ForegroundColor Magenta
