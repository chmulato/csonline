# Script para aplicar anotações de autorização em todos os controllers
# 
# Regras de negócio implementadas:
# - ADMIN: Acesso total a tudo
# - BUSINESS: Acesso a recursos da própria empresa
# - COURIER: Acesso apenas a entregas próprias
# - CUSTOMER: Acesso apenas a entregas próprias (visualização)

# Mapeamento de endpoints e permissões
$ControllerPermissions = @{
    "UserController" = @{
        "GET /" = @("ADMIN")
        "GET /{id}" = @("ADMIN", "BUSINESS")
        "POST /" = @("ADMIN")
        "PUT /{id}" = @("ADMIN", "BUSINESS")
        "DELETE /{id}" = @("ADMIN")
    }
    
    "CourierController" = @{
        "GET /" = @("ADMIN", "BUSINESS")
        "GET /{id}" = @("ADMIN", "BUSINESS", "COURIER")
        "POST /" = @("ADMIN", "BUSINESS")
        "PUT /{id}" = @("ADMIN", "BUSINESS")
        "DELETE /{id}" = @("ADMIN", "BUSINESS")
    }
    
    "CustomerController" = @{
        "GET /" = @("ADMIN", "BUSINESS")
        "GET /{id}" = @("ADMIN", "BUSINESS", "CUSTOMER")
        "POST /" = @("ADMIN", "BUSINESS")
        "PUT /{id}" = @("ADMIN", "BUSINESS")
        "DELETE /{id}" = @("ADMIN", "BUSINESS")
    }
    
    "DeliveryController" = @{
        "GET /" = @("ADMIN", "BUSINESS", "COURIER", "CUSTOMER")
        "GET /{id}" = @("ADMIN", "BUSINESS", "COURIER", "CUSTOMER")
        "POST /" = @("ADMIN", "BUSINESS")
        "PUT /{id}" = @("ADMIN", "BUSINESS", "COURIER")
        "DELETE /{id}" = @("ADMIN", "BUSINESS")
    }
    
    "PriceController" = @{
        "GET /" = @("ADMIN", "BUSINESS")
        "GET /{id}" = @("ADMIN", "BUSINESS")
        "POST /" = @("ADMIN", "BUSINESS")
        "PUT /{id}" = @("ADMIN", "BUSINESS")
        "DELETE /{id}" = @("ADMIN", "BUSINESS")
    }
    
    "SMSController" = @{
        "GET /" = @("ADMIN", "BUSINESS")
        "GET /{id}" = @("ADMIN", "BUSINESS")
        "POST /" = @("ADMIN", "BUSINESS")
        "PUT /{id}" = @("ADMIN", "BUSINESS")
        "DELETE /{id}" = @("ADMIN", "BUSINESS")
    }
    
    "TeamController" = @{
        "GET /" = @("ADMIN")
        "GET /{id}" = @("ADMIN")
        "POST /" = @("ADMIN")
        "PUT /{id}" = @("ADMIN")
        "DELETE /{id}" = @("ADMIN")
    }
    
    "LoginController" = @{
        "POST /login" = @() # Acesso público
    }
}

Write-Host "📋 Mapeamento de Permissões por Controller" -ForegroundColor Cyan
Write-Host "=" * 60

foreach ($controller in $ControllerPermissions.Keys) {
    Write-Host "`n🎯 $controller" -ForegroundColor Yellow
    
    foreach ($endpoint in $ControllerPermissions[$controller].Keys) {
        $roles = $ControllerPermissions[$controller][$endpoint]
        $rolesText = if ($roles.Count -eq 0) { "🌐 Público" } else { "🔒 " + ($roles -join ", ") }
        Write-Host "  $endpoint → $rolesText" -ForegroundColor Gray
    }
}

Write-Host "`n✅ As anotações devem ser aplicadas manualmente nos controllers Java" -ForegroundColor Green
Write-Host "Exemplo:" -ForegroundColor Gray
Write-Host '@RolesAllowed({"ADMIN", "BUSINESS"})' -ForegroundColor Yellow
Write-Host "public List<Courier> getAll() { ... }" -ForegroundColor Gray
