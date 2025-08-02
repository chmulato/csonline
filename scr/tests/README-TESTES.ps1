# README - Scripts de Teste de Endpoints CSOnline
# =====================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "SCRIPTS DE TESTE DE ENDPOINTS - CSONLINE" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host "`nSCRIPTS CRIADOS:" -ForegroundColor Yellow
Write-Host "================" -ForegroundColor Yellow

$scripts = @(
    @{ 
        File = "test-couriers.ps1"
        Description = "Testa endpoints de Couriers (/api/couriers)"
        Status = "Funcionando"
    },
    @{ 
        File = "test-users.ps1"
        Description = "Testa endpoints de Users (/api/users)"
        Status = "Parcial (individual funciona, lista com erro 500)"
    },
    @{ 
        File = "test-customers.ps1"
        Description = "Testa endpoints de Customers (/api/customers)"
        Status = "Erro 500 (problemas de serialização)"
    },
    @{ 
        File = "test-teams.ps1"
        Description = "Testa endpoints de Teams (/api/team)"
        Status = "Erro 500 (problemas de serialização)"
    },
    @{ 
        File = "test-deliveries.ps1"
        Description = "Testa endpoints de Deliveries (/api/deliveries)"
        Status = "Funcionando (sem dados)"
    },
    @{ 
        File = "test-sms.ps1"
        Description = "Testa endpoints de SMS (/api/sms)"
        Status = "Funcionando (sem dados)"
    },
    @{ 
        File = "test-login.ps1"
        Description = "Testa endpoint de Login (/api/login)"
        Status = "Erro 404 (endpoint não encontrado)"
    },
    @{ 
        File = "test-all-endpoints.ps1"
        Description = "Script master que executa todos os testes"
        Status = "Funcionando"
    },
    @{ 
        File = "health-check-endpoints.ps1"
        Description = "Verificação rápida de saúde de todos os endpoints"
        Status = "Funcionando"
    }
)

foreach ($script in $scripts) {
    Write-Host "`n$($script.File)" -ForegroundColor White
    Write-Host "   $($script.Description)" -ForegroundColor Gray
    Write-Host "   Status: $($script.Status)" -ForegroundColor Gray
}

Write-Host "`nCOMO USAR:" -ForegroundColor Yellow
Write-Host "==========" -ForegroundColor Yellow
Write-Host "Localização dos scripts: ./scr/tests/" -ForegroundColor Cyan

Write-Host "`n# Navegar para a pasta de testes:" -ForegroundColor Gray
Write-Host "cd scr/tests" -ForegroundColor White

Write-Host "`n# Verificar saúde geral dos endpoints:" -ForegroundColor Gray
Write-Host ".\health-check-endpoints.ps1" -ForegroundColor White

Write-Host "`n# Executar todos os testes:" -ForegroundColor Gray
Write-Host ".\test-all-endpoints.ps1" -ForegroundColor White

Write-Host "`n# Executar teste específico:" -ForegroundColor Gray
Write-Host ".\test-all-endpoints.ps1 -OnlyTest 'Couriers'" -ForegroundColor White
Write-Host ".\test-couriers.ps1" -ForegroundColor White

Write-Host "`n# Pular testes específicos:" -ForegroundColor Gray
Write-Host ".\test-all-endpoints.ps1 -SkipCustomers -SkipTeams" -ForegroundColor White

Write-Host "`n# Executar da raiz do projeto:" -ForegroundColor Gray
Write-Host ".\scr\tests\test-all-endpoints.ps1" -ForegroundColor White
Write-Host ".\scr\tests\health-check-endpoints.ps1" -ForegroundColor White

Write-Host "`nSTATUS ATUAL DOS ENDPOINTS:" -ForegroundColor Yellow
Write-Host "===========================" -ForegroundColor Yellow
Write-Host "/api/couriers          - Funcionando completamente" -ForegroundColor Green
Write-Host "/api/users            - Individual OK, lista com erro 500" -ForegroundColor Yellow
Write-Host "/api/customers         - Erro 500 (serialização)" -ForegroundColor Red
Write-Host "/api/team              - Erro 500 (serialização)" -ForegroundColor Red
Write-Host "/api/deliveries        - Funcionando (sem dados)" -ForegroundColor Green
Write-Host "/api/sms               - Funcionando (sem dados)" -ForegroundColor Green
Write-Host "/api/login             - Erro 404 (não encontrado)" -ForegroundColor Red

Write-Host "`nPROBLEMAS IDENTIFICADOS:" -ForegroundColor Yellow
Write-Host "========================" -ForegroundColor Yellow
Write-Host "1. Erro 500 em alguns endpoints indica problemas de:" -ForegroundColor Red
Write-Host "   - Serialização JSON (referências circulares)" -ForegroundColor Gray
Write-Host "   - Consultas JPA mal formadas" -ForegroundColor Gray
Write-Host "   - Mapeamento de entidades" -ForegroundColor Gray

Write-Host "`n2. Endpoint /api/login retorna 404:" -ForegroundColor Red
Write-Host "   - Verificar se o path está correto" -ForegroundColor Gray
Write-Host "   - Verificar se o controller está registrado" -ForegroundColor Gray

Write-Host "`nPROXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host "=================" -ForegroundColor Yellow
Write-Host "1. Verificar logs do WildFly para entender os erros 500" -ForegroundColor Gray
Write-Host "2. Adicionar @JsonManagedReference/@JsonBackReference onde necessário" -ForegroundColor Gray
Write-Host "3. Verificar mapeamentos JPA das entidades" -ForegroundColor Gray
Write-Host "4. Corrigir path do endpoint de login" -ForegroundColor Gray

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "Documentação criada em: $(Get-Date)" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
