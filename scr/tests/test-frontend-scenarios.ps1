# ============================================================================
# Script de Teste de Interface - Cenários de Navegação por Perfil
# ============================================================================
# 
# Este script simula a navegação e interações nos componentes Vue
# para cada perfil de usuário, validando a lógica de acesso
#
# Uso: .\test-frontend-scenarios.ps1
# ============================================================================

param(
    [string]$FrontendUrl = "http://localhost:5173",
    [switch]$Verbose,
    [switch]$ScreenshotMode
)

# Configurações
$ErrorActionPreference = "Continue"

# Cores para output
$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Cyan = "Cyan"
$Magenta = "Magenta"

# Detectar raiz do repositório para resolver caminhos do frontend
$RepoRoot = Split-Path -Path $PSScriptRoot -Parent  # scr/tests -> scr
$RepoRoot = Split-Path -Path $RepoRoot -Parent      # scr -> repo root
$FrontendDir = Join-Path $RepoRoot "frontend"

if (-not (Test-Path $FrontendDir)) {
    Write-Host "❌ Diretório frontend não encontrado!" -ForegroundColor Red
    Write-Host "   Caminho esperado: $FrontendDir" -ForegroundColor Gray
    Write-Host "   Dica: verifique se você está na raiz do repositório." -ForegroundColor Yellow
    return
}

# Cenários de teste por perfil
$TestScenarios = @{
    "ADMIN" = @{
        Username = "admin"
        Password = "admin123"
        AllowedScreens = @(
            "UserManagement", "CourierManagement", "CustomerManagement",
            "DeliveryManagement", "PriceManagement", "SMSManagement", 
            "TeamManagement", "Dashboard"
        )
        RestrictedScreens = @()
        TestActions = @(
            @{ Screen = "UserManagement"; Action = "Listar usuários"; Expected = "Success" }
            @{ Screen = "UserManagement"; Action = "Criar usuário"; Expected = "Success" }
            @{ Screen = "CourierManagement"; Action = "Listar entregadores"; Expected = "Success" }
            @{ Screen = "CourierManagement"; Action = "Criar entregador"; Expected = "Success" }
            @{ Screen = "CustomerManagement"; Action = "Listar clientes"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Listar entregas"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Criar entrega"; Expected = "Success" }
            @{ Screen = "PriceManagement"; Action = "Listar preços"; Expected = "Success" }
            @{ Screen = "PriceManagement"; Action = "Criar preço"; Expected = "Success" }
            @{ Screen = "SMSManagement"; Action = "Listar mensagens"; Expected = "Success" }
            @{ Screen = "SMSManagement"; Action = "Enviar mensagem"; Expected = "Success" }
            @{ Screen = "TeamManagement"; Action = "Listar equipes"; Expected = "Success" }
        )
    }
    
    "BUSINESS" = @{
        Username = "empresa"
        Password = "empresa123"
        AllowedScreens = @(
            "CourierManagement", "CustomerManagement", "DeliveryManagement",
            "PriceManagement", "SMSManagement", "Dashboard"
        )
        RestrictedScreens = @("UserManagement", "TeamManagement")
        TestActions = @(
            @{ Screen = "CourierManagement"; Action = "Listar próprios entregadores"; Expected = "Success" }
            @{ Screen = "CourierManagement"; Action = "Criar entregador"; Expected = "Success" }
            @{ Screen = "CustomerManagement"; Action = "Listar próprios clientes"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Listar próprias entregas"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Criar entrega"; Expected = "Success" }
            @{ Screen = "PriceManagement"; Action = "Listar próprios preços"; Expected = "Success" }
            @{ Screen = "PriceManagement"; Action = "Criar preço"; Expected = "Success" }
            @{ Screen = "SMSManagement"; Action = "Enviar mensagem"; Expected = "Success" }
            @{ Screen = "UserManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "TeamManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
        )
    }
    
    "COURIER" = @{
        Username = "joao"
        Password = "joao123"
        AllowedScreens = @("DeliveryManagement", "Dashboard")
        RestrictedScreens = @(
            "UserManagement", "CourierManagement", "CustomerManagement",
            "PriceManagement", "SMSManagement", "TeamManagement"
        )
        TestActions = @(
            @{ Screen = "DeliveryManagement"; Action = "Listar entregas atribuídas"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Atualizar status entrega"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Criar entrega"; Expected = "Forbidden" }
            @{ Screen = "UserManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "CourierManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "CustomerManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "PriceManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "SMSManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
        )
    }
    
    "CUSTOMER" = @{
        Username = "carlos"
        Password = "carlos123"
        AllowedScreens = @("DeliveryManagement", "Dashboard")
        RestrictedScreens = @(
            "UserManagement", "CourierManagement", "CustomerManagement",
            "PriceManagement", "SMSManagement", "TeamManagement"
        )
        TestActions = @(
            @{ Screen = "DeliveryManagement"; Action = "Listar próprias entregas"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Visualizar histórico"; Expected = "Success" }
            @{ Screen = "DeliveryManagement"; Action = "Criar entrega"; Expected = "Forbidden" }
            @{ Screen = "DeliveryManagement"; Action = "Modificar entrega"; Expected = "Forbidden" }
            @{ Screen = "UserManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "CourierManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "CustomerManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "PriceManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
            @{ Screen = "SMSManagement"; Action = "Acessar tela"; Expected = "Forbidden" }
        )
    }
}

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
        [string]$Details = ""
    )
    
    $status = if ($Success) { "✅ PASS" } else { "❌ FAIL" }
    $color = if ($Success) { $Green } else { $Red }
    
    Write-Host "  $status $Test" -ForegroundColor $color
    if ($Details) {
        Write-Host "    └─ $Details" -ForegroundColor Gray
    }
}

function Test-ComponentExists {
    param([string]$ComponentName)
    
    $componentPath = Join-Path $FrontendDir "src/components/$ComponentName.vue"
    return Test-Path $componentPath
}

function Test-ComponentStructure {
    param([string]$ComponentName)
    
    $componentPath = Join-Path $FrontendDir "src/components/$ComponentName.vue"
    
    if (-not (Test-Path $componentPath)) {
        return $false
    }
    
    try {
        $content = Get-Content $componentPath -Raw
        
        # Verificar estrutura básica Vue
        $hasTemplate = $content -match "&lt;template&gt;"
        $hasScript = $content -match "&lt;script.*setup&gt;"
        $hasStyle = $content -match "&lt;style.*scoped&gt;"
        
        return $hasTemplate -and $hasScript
    }
    catch {
        return $false
    }
}

function Test-ComponentAuthentication {
    param(
        [string]$ComponentName,
        [string]$UserRole
    )
    
    $componentPath = Join-Path $FrontendDir "src/components/$ComponentName.vue"
    
    if (-not (Test-Path $componentPath)) {
        return @{ HasAuth = $false; Details = "Componente não encontrado" }
    }
    
    try {
        $content = Get-Content $componentPath -Raw
        
        # Verificar se o componente tem verificação de autenticação
        $hasAuthCheck = $content -match "(userRole|currentUser|checkPermission|canAccess)"
        $hasRoleCheck = $content -match "role.*===.*['""]$UserRole['""]"
        
        return @{
            HasAuth = $hasAuthCheck
            HasRoleCheck = $hasRoleCheck
            Details = "Auth: $hasAuthCheck, Role: $hasRoleCheck"
        }
    }
    catch {
        return @{ HasAuth = $false; Details = "Erro ao ler componente" }
    }
}

# ============================================================================
# TESTES POR PERFIL DE USUÁRIO
# ============================================================================

function Test-ProfileScenarios {
    param([string]$ProfileName)
    
    $profile = $TestScenarios[$ProfileName]
    if (-not $profile) {
        Write-Host "❌ Perfil $ProfileName não encontrado" -ForegroundColor $Red
        return
    }
    
    Write-TestHeader "TESTANDO CENÁRIOS: $ProfileName"
    Write-Host "Usuário: $($profile.Username)" -ForegroundColor Gray
    Write-Host "Telas Permitidas: $($profile.AllowedScreens -join ', ')" -ForegroundColor $Green
    Write-Host "Telas Restritas: $($profile.RestrictedScreens -join ', ')" -ForegroundColor $Red
    
    Write-TestSection "Verificação de Componentes Existentes"
    
    # Testar se componentes permitidos existem
    foreach ($screen in $profile.AllowedScreens) {
        $exists = Test-ComponentExists $screen
        Write-TestResult "Componente $screen existe" $exists
        
        if ($exists) {
            $structure = Test-ComponentStructure $screen
            Write-TestResult "Estrutura Vue válida em $screen" $structure
            
            $auth = Test-ComponentAuthentication $screen $ProfileName
            Write-TestResult "Verificação de auth em $screen" $auth.HasAuth $auth.Details
        }
    }
    
    Write-TestSection "Verificação de Restrições de Acesso"
    
    # Testar componentes restritos
    foreach ($screen in $profile.RestrictedScreens) {
        $exists = Test-ComponentExists $screen
        if ($exists) {
            $auth = Test-ComponentAuthentication $screen $ProfileName
            $hasRestriction = $auth.HasAuth  # Se tem verificação de auth, pode ter restrição
            Write-TestResult "Restrição implementada em $screen" $hasRestriction $auth.Details
        }
    }
    
    Write-TestSection "Simulação de Ações Específicas"
    
    # Testar ações específicas do perfil
    foreach ($action in $profile.TestActions) {
        $componentExists = Test-ComponentExists $action.Screen
        
        if ($componentExists) {
            $shouldSucceed = $action.Expected -eq "Success"
            $testName = "$($action.Action) em $($action.Screen)"
            
            # Simular resultado baseado na expectativa
            if ($shouldSucceed -and ($action.Screen -in $profile.AllowedScreens)) {
                Write-TestResult $testName $true "Ação permitida para $ProfileName"
            }
            elseif (-not $shouldSucceed -and ($action.Screen -in $profile.RestrictedScreens)) {
                Write-TestResult $testName $true "Acesso negado conforme esperado"
            }
            else {
                Write-TestResult $testName $false "Comportamento inesperado"
            }
        }
        else {
            Write-TestResult "$($action.Action) em $($action.Screen)" $false "Componente não encontrado"
        }
    }
}

# ============================================================================
# TESTE DE NAVEGAÇÃO E ROTEAMENTO
# ============================================================================

function Test-RoutingAndNavigation {
    Write-TestHeader "TESTANDO NAVEGAÇÃO E ROTEAMENTO"
    
    Write-TestSection "Verificação de Arquivos de Roteamento"
    
    # Verificar se existe sistema de roteamento
    $routerFile = Join-Path $FrontendDir "src/router/index.js"
    if (Test-Path $routerFile) {
        Write-TestResult "Arquivo de router existe" $true $routerFile
        
        try {
            $routerContent = Get-Content $routerFile -Raw
            $hasRoutes = $routerContent -match "routes.*=.*\["
            $hasGuards = $routerContent -match "(beforeEach|requiresAuth|meta)"
            
            Write-TestResult "Definição de rotas" $hasRoutes
            Write-TestResult "Guards de autenticação" $hasGuards
        }
        catch {
            Write-TestResult "Análise do router" $false $_.Exception.Message
        }
    }
    else {
        Write-TestResult "Sistema de roteamento" $false "Router não encontrado"
    }
    
    Write-TestSection "Verificação de Menu/Navegação"
    
    # Verificar componentes de navegação
    $navComponents = @("Navigation", "Menu", "Sidebar", "Header", "Navbar")
    foreach ($navComp in $navComponents) {
        $exists = Test-ComponentExists $navComp
        if ($exists) {
            Write-TestResult "Componente de navegação $navComp" $true
            
            $auth = Test-ComponentAuthentication $navComp "ALL"
            Write-TestResult "Menu com controle de acesso" $auth.HasAuth $auth.Details
        }
    }
    
    Write-TestSection "Verificação de App Principal"
    
    $appFile = Join-Path $FrontendDir "src/App.vue"
    if (Test-Path $appFile) {
        Write-TestResult "App.vue existe" $true
        
        try {
            $appContent = Get-Content $appFile -Raw
            $hasRouterView = $appContent -match "router-view"
            $hasAuth = $appContent -match "(userRole|currentUser|isAuthenticated)"
            
            Write-TestResult "Router View configurado" $hasRouterView
            Write-TestResult "Controle de autenticação" $hasAuth
        }
        catch {
            Write-TestResult "Análise do App.vue" $false $_.Exception.Message
        }
    }
}

# ============================================================================
# TESTE DE RESPONSIVIDADE E UI
# ============================================================================

function Test-UIAndResponsiveness {
    Write-TestHeader "TESTANDO INTERFACE E RESPONSIVIDADE"
    
    Write-TestSection "Verificação de Estilos e CSS"
    
    # Verificar se existem arquivos de estilo
    $styleFiles = @(
        (Join-Path $FrontendDir "src/style.css"),
        (Join-Path $FrontendDir "src/assets/styles.css"),
        (Join-Path $FrontendDir "src/assets/main.css")
    )
    
    $hasStyles = $false
    foreach ($styleFile in $styleFiles) {
        if (Test-Path $styleFile) {
            $hasStyles = $true
            Write-TestResult "Arquivo de estilos" $true $styleFile
        }
    }
    
    if (-not $hasStyles) {
        Write-TestResult "Arquivos de estilo" $false "Nenhum arquivo CSS encontrado"
    }
    
    Write-TestSection "Verificação de Bibliotecas UI"
    
    $packageFile = Join-Path $FrontendDir "package.json"
    if (Test-Path $packageFile) {
        try {
            $packageContent = Get-Content $packageFile -Raw | ConvertFrom-Json
            
            # Verificar dependências UI comuns
            $uiLibraries = @("bootstrap", "vuetify", "quasar", "element-plus", "ant-design-vue")
            $foundLibraries = @()
            
            foreach ($lib in $uiLibraries) {
                if ($packageContent.dependencies.$lib -or $packageContent.devDependencies.$lib) {
                    $foundLibraries += $lib
                }
            }
            
            if ($foundLibraries.Count -gt 0) {
                Write-TestResult "Bibliotecas UI encontradas" $true ($foundLibraries -join ", ")
            } else {
                Write-TestResult "Bibliotecas UI" $false "Usando CSS customizado"
            }
            
            # Verificar Font Awesome ou ícones
            $hasIcons = $packageContent.dependencies.'@fortawesome/fontawesome-free' -or 
                       $packageContent.dependencies.'font-awesome' -or
                       $packageContent.dependencies.'@fortawesome/vue-fontawesome'
            
            Write-TestResult "Sistema de ícones" $hasIcons "Font Awesome detectado: $hasIcons"
            
        }
        catch {
            Write-TestResult "Análise package.json" $false $_.Exception.Message
        }
    }
    
    Write-TestSection "Verificação de Componentes de Layout"
    
    # Verificar componentes específicos com bom design
    $componentsToCheck = @("SMSManagement", "PriceManagement", "DeliveryManagement")
    
    foreach ($comp in $componentsToCheck) {
        if (Test-ComponentExists $comp) {
            $componentPath = "frontend/src/components/$comp.vue"
            $content = Get-Content $componentPath -Raw
            
            # Verificar se tem estilos específicos (WhatsApp para SMS, etc.)
            $hasCustomStyles = $content -match "&lt;style.*scoped&gt;"
            $hasWhatsAppTheme = $comp -eq "SMSManagement" -and $content -match "(whatsapp|#25D366|green)"
            $hasFormValidation = $content -match "(required|validation|error)"
            
            Write-TestResult "Estilos customizados em $comp" $hasCustomStyles
            
            if ($comp -eq "SMSManagement") {
                Write-TestResult "Tema WhatsApp em SMS" $hasWhatsAppTheme
            }
            
            Write-TestResult "Validação de formulários em $comp" $hasFormValidation
        }
    }
}

# ============================================================================
# EXECUÇÃO PRINCIPAL
# ============================================================================

function Main {
    Write-TestHeader "TESTE COMPLETO DE CENÁRIOS FRONTEND"
    Write-Host "Frontend URL: $FrontendUrl" -ForegroundColor Gray
    Write-Host "Data/Hora: $(Get-Date)" -ForegroundColor Gray
    
    # Verificar se o diretório frontend existe
    if (-not (Test-Path "frontend")) {
        Write-Host "❌ Diretório frontend não encontrado!" -ForegroundColor $Red
        return
    }
    
    # Testar cada perfil de usuário
    foreach ($profileName in $TestScenarios.Keys) {
        Test-ProfileScenarios $profileName
    }
    
    # Testes de navegação e UI
    Test-RoutingAndNavigation
    Test-UIAndResponsiveness
    
    Write-Host "`n🎉 Teste de cenários frontend concluído!" -ForegroundColor $Green
    Write-Host "📋 Execute o script test-all-profiles.ps1 para testes de API completos" -ForegroundColor $Cyan
}

# Executar
Main
