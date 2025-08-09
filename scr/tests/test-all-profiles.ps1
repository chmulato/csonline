# ============================================================================
# Script de Teste Completo CSOnline - Todos os Perfis de Usuário
# ============================================================================
# 
# Este script testa todas as funcionalidades do sistema para cada perfil:
# - ADMIN: Acesso total a todas as funcionalidades
# - BUSINESS: Gestão de entregas, entregadores, clientes e preços da empresa
# - COURIER: Visualização de entregas atribuídas, atualização de status
# - CUSTOMER: Visualização de entregas próprias, histórico
#
# Uso: .\test-all-profiles.ps1
# ============================================================================

param(
    [string]$BaseUrl = "http://localhost:8080/csonline/api",
    [string]$FrontendUrl = "http://localhost:5173",
    [switch]$Verbose,
    [switch]$GenerateReport
)

# Configurações
$ErrorActionPreference = "Continue"
$ProgressPreference = "SilentlyContinue"

# Cores para output
$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Cyan = "Cyan"
$Magenta = "Magenta"

# Variáveis globais
# Use uma lista mutável em escopo de script para evitar erros de op_Addition e conflitos de escopo
$script:TestResults = New-Object System.Collections.ArrayList
$CurrentUser = $null
$AuthToken = $null

# ============================================================================
# FUNÇÕES AUXILIARES
# ============================================================================

function Write-TestHeader {
    param([string]$Title)
    Write-Host "`n$('=' * 80)" -ForegroundColor $Cyan
    Write-Host " $Title" -ForegroundColor $Cyan
    Write-Host "$('=' * 80)" -ForegroundColor $Cyan
}

function Write-TestSection {
    param([string]$Section)
    Write-Host "`n$('-' * 60)" -ForegroundColor $Yellow
    Write-Host " $Section" -ForegroundColor $Yellow
    Write-Host "$('-' * 60)" -ForegroundColor $Yellow
}

function Write-TestResult {
    param(
        [string]$Test,
        [bool]$Success,
        [string]$Details = "",
        [string]$Profile = ""
    )
    
    $status = if ($Success) { "✅ PASS" } else { "❌ FAIL" }
    $color = if ($Success) { $Green } else { $Red }
    
    Write-Host "  $status $Test" -ForegroundColor $color
    if ($Details) {
        Write-Host "    └─ $Details" -ForegroundColor Gray
    }
    
    # Armazenar resultado (sempre como lista mutável no escopo do script)
    $entry = [PSCustomObject]@{
        Profile = $Profile
        Test = $Test
        Success = $Success
        Details = $Details
        Timestamp = (Get-Date)
    }
    [void]$script:TestResults.Add($entry)
}

function Invoke-ApiRequest {
    param(
        [string]$Endpoint,
        [string]$Method = "GET",
        [hashtable]$Headers = @{},
        [object]$Body = $null
    )
    
    try {
        $uri = "$BaseUrl$Endpoint"
        $requestHeaders = @{ "Accept" = "application/json" }
        
        if ($AuthToken) {
            $requestHeaders["Authorization"] = "Bearer $AuthToken"
        }
        
        foreach ($key in $Headers.Keys) {
            $requestHeaders[$key] = $Headers[$key]
        }
        
        $params = @{
            Uri = $uri
            Method = $Method
            Headers = $requestHeaders
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
            $params.ContentType = "application/json"
        }
        
        if ($Verbose) {
            Write-Host "    API: $Method $uri" -ForegroundColor Gray
        }
        
        return Invoke-RestMethod @params
    }
    catch {
        if ($Verbose) {
            Write-Host "    API ERROR: $($_.Exception.Message)" -ForegroundColor Red
        }
        throw
    }
}

function Test-Login {
    param(
        [string]$Username,
        [string]$Password,
        [string]$ExpectedRole
    )
    
    try {
        $loginData = @{
            login = $Username
            password = $Password
        }
        
        $response = Invoke-ApiRequest -Endpoint "/login" -Method "POST" -Body $loginData
        
        if ($response.token -and $response.role -eq $ExpectedRole) {
            $global:AuthToken = $response.token
            $global:CurrentUser = $response
            Write-TestResult "Login como $Username ($ExpectedRole)" $true "Token obtido, role validado" $ExpectedRole
            return $true
        } else {
            Write-TestResult "Login como $Username ($ExpectedRole)" $false "Resposta inválida ou role incorreto" $ExpectedRole
            return $false
        }
    }
    catch {
        Write-TestResult "Login como $Username ($ExpectedRole)" $false $_.Exception.Message $ExpectedRole
        return $false
    }
}

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Endpoint,
        [string]$Method = "GET",
        [object]$Body = $null,
        [bool]$ShouldSucceed = $true,
        [string]$Profile = ""
    )
    
    try {
        $response = Invoke-ApiRequest -Endpoint $Endpoint -Method $Method -Body $Body
        
        if ($ShouldSucceed) {
            Write-TestResult $Name $true "Dados retornados: $($response.Count) items" $Profile
        } else {
            Write-TestResult $Name $false "Deveria ter falhado mas funcionou" $Profile
        }
        
        return $response
    }
    catch {
        if ($ShouldSucceed) {
            Write-TestResult $Name $false $_.Exception.Message $Profile
        } else {
            Write-TestResult $Name $true "Acesso negado conforme esperado" $Profile
        }
        return $null
    }
}

# ============================================================================
# TESTES POR PERFIL
# ============================================================================

function Test-AdminProfile {
    Write-TestHeader "TESTANDO PERFIL: ADMINISTRADOR"
    
    if (-not (Test-Login "admin" "admin123" "ADMIN")) {
        return
    }
    
    Write-TestSection "Gestão de Usuários"
    Test-Endpoint "Listar usuários" "/users" -Profile "ADMIN"
    Test-Endpoint "Buscar usuário específico" "/users/1" -Profile "ADMIN"
    
    Write-TestSection "Gestão de Entregadores"
    Test-Endpoint "Listar entregadores" "/couriers" -Profile "ADMIN"
    Test-Endpoint "Buscar entregador específico" "/couriers/1" -Profile "ADMIN"
    
    Write-TestSection "Gestão de Clientes"
    Test-Endpoint "Listar clientes" "/customers" -Profile "ADMIN"
    Test-Endpoint "Buscar cliente específico" "/customers/1" -Profile "ADMIN"
    
    Write-TestSection "Gestão de Equipes"
    Test-Endpoint "Listar equipes" "/teams" -Profile "ADMIN"
    Test-Endpoint "Buscar equipe específica" "/teams/1" -Profile "ADMIN"
    
    Write-TestSection "Gestão de Entregas"
    Test-Endpoint "Listar entregas" "/deliveries" -Profile "ADMIN"
    Test-Endpoint "Buscar entrega específica" "/deliveries/1" -Profile "ADMIN"
    
    Write-TestSection "Gestão de Preços"
    Test-Endpoint "Listar preços" "/prices" -Profile "ADMIN"
    
    Write-TestSection "Gestão de SMS/WhatsApp"
    Test-Endpoint "Listar mensagens SMS" "/sms" -Profile "ADMIN"
    
    Write-TestSection "Operações de Criação (Admin)"
    $newUser = @{
        role = "COURIER"
        name = "Teste Admin Courier"
        login = "test_admin_courier"
        password = "test123"
        email = "test@admin.com"
        mobile = "11000000000"
    }
    Test-Endpoint "Criar usuário" "/users" "POST" $newUser -Profile "ADMIN"
}

function Test-BusinessProfile {
    Write-TestHeader "TESTANDO PERFIL: EMPRESA"
    
    if (-not (Test-Login "empresa" "empresa123" "BUSINESS")) {
        return
    }
    
    Write-TestSection "Visualização Permitida (Business)"
    Test-Endpoint "Listar próprios entregadores" "/couriers" -Profile "BUSINESS"
    Test-Endpoint "Listar próprios clientes" "/customers" -Profile "BUSINESS"
    Test-Endpoint "Listar próprias entregas" "/deliveries" -Profile "BUSINESS"
    Test-Endpoint "Listar próprios preços" "/prices" -Profile "BUSINESS"
    Test-Endpoint "Listar mensagens SMS" "/sms" -Profile "BUSINESS"
    
    Write-TestSection "Gestão de Entregadores (Business)"
    $newCourier = @{
        businessId = 2
        factorCourier = 1.0
        user = @{
            role = "COURIER"
            name = "Entregador Business Test"
            login = "courier_business_test"
            password = "test123"
            email = "courier.business@test.com"
            mobile = "11000000001"
        }
    }
    Test-Endpoint "Criar entregador" "/couriers" "POST" $newCourier -Profile "BUSINESS"
    
    Write-TestSection "Gestão de Preços (Business)"
    $newPrice = @{
        tableName = "Tabela Business Test"
        customer = @{ id = 1 }
        business = @{ id = 2 }
        vehicle = "Moto"
        local = "São Paulo - Teste"
        price = 25.50
    }
    Test-Endpoint "Criar preço" "/prices" "POST" $newPrice -Profile "BUSINESS"
    
    Write-TestSection "Acessos Restritos (Business)"
    Test-Endpoint "Listar todos usuários" "/users" -ShouldSucceed $false -Profile "BUSINESS"
    Test-Endpoint "Listar todas equipes" "/teams" -ShouldSucceed $false -Profile "BUSINESS"
}

function Test-CourierProfile {
    Write-TestHeader "TESTANDO PERFIL: ENTREGADOR"
    
    if (-not (Test-Login "joao" "joao123" "COURIER")) {
        return
    }
    
    Write-TestSection "Visualização Permitida (Courier)"
    Test-Endpoint "Listar entregas atribuídas" "/deliveries" -Profile "COURIER"
    Test-Endpoint "Visualizar perfil próprio" "/users/$($CurrentUser.id)" -Profile "COURIER"
    
    Write-TestSection "Operações Permitidas (Courier)"
    # Teste de atualização de status de entrega (se entrega existir)
    $deliveries = Test-Endpoint "Verificar entregas disponíveis" "/deliveries" -Profile "COURIER"
    if ($deliveries -and $deliveries.Count -gt 0) {
        $deliveryId = $deliveries[0].id
        $updateData = @{
            status = "EM_TRANSITO"
            observacao = "Saindo para entrega - Teste Courier"
        }
        Test-Endpoint "Atualizar status entrega" "/deliveries/$deliveryId" "PUT" $updateData -Profile "COURIER"
    }
    
    Write-TestSection "Acessos Restritos (Courier)"
    Test-Endpoint "Listar usuários" "/users" -ShouldSucceed $false -Profile "COURIER"
    Test-Endpoint "Listar clientes" "/customers" -ShouldSucceed $false -Profile "COURIER"
    Test-Endpoint "Listar preços" "/prices" -ShouldSucceed $false -Profile "COURIER"
    Test-Endpoint "Criar entregador" "/couriers" "POST" @{test="data"} -ShouldSucceed $false -Profile "COURIER"
}

function Test-CustomerProfile {
    Write-TestHeader "TESTANDO PERFIL: CLIENTE"
    
    if (-not (Test-Login "carlos" "carlos123" "CUSTOMER")) {
        return
    }
    
    Write-TestSection "Visualização Permitida (Customer)"
    Test-Endpoint "Listar próprias entregas" "/deliveries" -Profile "CUSTOMER"
    Test-Endpoint "Visualizar perfil próprio" "/users/$($CurrentUser.id)" -Profile "CUSTOMER"
    
    Write-TestSection "Acessos Restritos (Customer)"
    Test-Endpoint "Listar usuários" "/users" -ShouldSucceed $false -Profile "CUSTOMER"
    Test-Endpoint "Listar entregadores" "/couriers" -ShouldSucceed $false -Profile "CUSTOMER"
    Test-Endpoint "Listar clientes" "/customers" -ShouldSucceed $false -Profile "CUSTOMER"
    Test-Endpoint "Listar preços" "/prices" -ShouldSucceed $false -Profile "CUSTOMER"
    Test-Endpoint "Listar mensagens SMS" "/sms" -ShouldSucceed $false -Profile "CUSTOMER"
    Test-Endpoint "Criar entrega" "/deliveries" "POST" @{test="data"} -ShouldSucceed $false -Profile "CUSTOMER"
}

# ============================================================================
# TESTES DE FRONTEND
# ============================================================================

function Test-FrontendPages {
    Write-TestHeader "TESTANDO PÁGINAS FRONTEND"
    
    try {
        Write-TestSection "Verificação de Conectividade"
        $frontendResponse = Invoke-WebRequest -Uri $FrontendUrl -UseBasicParsing -TimeoutSec 10
        Write-TestResult "Frontend acessível" ($frontendResponse.StatusCode -eq 200) "Status: $($frontendResponse.StatusCode)" "FRONTEND"
        
        Write-TestSection "Verificação de Recursos Estáticos"
        # Verificar se os componentes Vue existem
        $components = @(
            "Login", "UserManagement", "CourierManagement", 
            "CustomerManagement", "DeliveryManagement", 
            "PriceManagement", "SMSManagement", "TeamManagement"
        )
        
        foreach ($component in $components) {
            $componentExists = Test-Path "frontend/src/components/$component.vue"
            Write-TestResult "Componente $component existe" $componentExists "" "FRONTEND"
        }
        
    }
    catch {
        Write-TestResult "Frontend acessível" $false $_.Exception.Message "FRONTEND"
    }
}

# ============================================================================
# TESTES DE INTEGRAÇÃO
# ============================================================================

function Test-Integration {
    Write-TestHeader "TESTANDO INTEGRAÇÃO COMPLETA"
    
    Write-TestSection "Fluxo Completo: Empresa → Entregador → Entrega"
    
    # 1. Login como empresa
    if (Test-Login "empresa" "empresa123" "BUSINESS") {
        
        # 2. Criar um entregador
        $courier = @{
            businessId = 2
            factorCourier = 1.2
            user = @{
                role = "COURIER"
                name = "Integration Test Courier"
                login = "integration_courier"
                password = "test123"
                email = "integration@test.com"
                mobile = "11999999998"
            }
        }
        $courierResponse = Test-Endpoint "Criar entregador (Integração)" "/couriers" "POST" $courier -Profile "INTEGRATION"
        
        # 3. Criar uma entrega
        if ($courierResponse) {
            $delivery = @{
                businessId = 2
                customerId = 1
                courierId = $courierResponse.id
                start = "Av. Paulista, 1000"
                destination = "Rua Augusta, 500"
                contact = "João Silva"
                description = "Entrega de integração"
                volume = "1 caixa"
                weight = "2kg"
                km = "10"
                cost = 50.0
            }
            $deliveryResponse = Test-Endpoint "Criar entrega (Integração)" "/deliveries" "POST" $delivery -Profile "INTEGRATION"
            
            # 4. Login como entregador e atualizar status
            if ($deliveryResponse -and (Test-Login "integration_courier" "test123" "COURIER")) {
                $statusUpdate = @{
                    status = "EM_TRANSITO"
                    observacao = "Teste de integração - entregador"
                }
                Test-Endpoint "Atualizar status (Integração)" "/deliveries/$($deliveryResponse.id)" "PUT" $statusUpdate -Profile "INTEGRATION"
            }
        }
    }
}

# ============================================================================
# RELATÓRIO FINAL
# ============================================================================

function Generate-TestReport {
    Write-TestHeader "RELATÓRIO FINAL DE TESTES"
    
    $totalTests = $TestResults.Count
    $passedTests = ($TestResults | Where-Object { $_.Success }).Count
    $failedTests = $totalTests - $passedTests
    $successRate = if ($totalTests -gt 0) { [math]::Round(($passedTests / $totalTests) * 100, 2) } else { 0 }
    
    Write-Host "`nRESUMO GERAL:" -ForegroundColor $Magenta
    Write-Host "  Total de Testes: $totalTests" -ForegroundColor White
    Write-Host "  Sucessos: $passedTests" -ForegroundColor $Green
    Write-Host "  Falhas: $failedTests" -ForegroundColor $Red
    Write-Host "  Taxa de Sucesso: $successRate%" -ForegroundColor $(if ($successRate -gt 80) { $Green } else { $Red })
    
    Write-Host "`nRESUMO POR PERFIL:" -ForegroundColor $Magenta
    $TestResults | Group-Object Profile | ForEach-Object {
        $profileTotal = $_.Count
        $profilePassed = ($_.Group | Where-Object { $_.Success }).Count
        $profileRate = if ($profileTotal -gt 0) { [math]::Round(($profilePassed / $profileTotal) * 100, 2) } else { 0 }
        
        Write-Host "  $($_.Name): $profilePassed/$profileTotal ($profileRate%)" -ForegroundColor $(if ($profileRate -gt 80) { $Green } else { $Yellow })
    }
    
    if ($failedTests -gt 0) {
        Write-Host "`nTESTES QUE FALHARAM:" -ForegroundColor $Red
        $TestResults | Where-Object { -not $_.Success } | ForEach-Object {
            Write-Host "  ❌ [$($_.Profile)] $($_.Test)" -ForegroundColor $Red
            if ($_.Details) {
                Write-Host "     └─ $($_.Details)" -ForegroundColor Gray
            }
        }
    }
    
    if ($GenerateReport) {
        $reportFile = "test-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').csv"
        $TestResults | Export-Csv -Path $reportFile -NoTypeInformation
        Write-Host "`nRelatório detalhado salvo em: $reportFile" -ForegroundColor $Cyan
    }
}

# ============================================================================
# EXECUÇÃO PRINCIPAL
# ============================================================================

function Main {
    Write-TestHeader "INICIANDO TESTES COMPLETOS DO CSONLINE"
    Write-Host "Backend: $BaseUrl" -ForegroundColor Gray
    Write-Host "Frontend: $FrontendUrl" -ForegroundColor Gray
    Write-Host "Horário: $(Get-Date)" -ForegroundColor Gray
    
    # Verificar conectividade básica
    try {
        # Utilizar página pública da aplicação para checagem (evita 401 em endpoint protegido)
        $baseAppUrl = if ($BaseUrl -match "/api$") { $BaseUrl -replace "/api$", "" } else { $BaseUrl }
        $response = Invoke-WebRequest -Uri $baseAppUrl -Method GET -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 400) {
            Write-Host "✅ Backend está acessível" -ForegroundColor $Green
        } else {
            Write-Host "❌ Backend respondeu com status: $($response.StatusCode)" -ForegroundColor $Red
            return
        }
    }
    catch {
        Write-Host "❌ Backend não está acessível: $($_.Exception.Message)" -ForegroundColor $Red
        Write-Host "   Certifique-se que o WildFly está rodando em $baseAppUrl" -ForegroundColor Yellow
        return
    }
    
    # Executar testes por perfil
    Test-AdminProfile
    Test-BusinessProfile
    Test-CourierProfile
    Test-CustomerProfile
    
    # Testes de frontend
    Test-FrontendPages
    
    # Testes de integração
    Test-Integration
    
    # Gerar relatório
    Generate-TestReport
    
    Write-Host "`n🎉 Testes concluídos!" -ForegroundColor $Green
}

# Executar script principal
Main
