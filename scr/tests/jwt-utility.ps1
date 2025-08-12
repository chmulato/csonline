# Utilitário JWT para Scripts de Teste
# Versão: 2.0 - Suporte completo a JWT
# Data: 7 de agosto de 2025

param(
    [string]$Login = "admin",
    [string]$Password = "admin123",
    [string]$BaseUrl = "http://localhost:8080/csonline",
    [switch]$Verbose
)

if ($Login -ne 'admin') {
    Write-Host "⚠️ jwt-utility.ps1: Recomendado usar ADMIN para cobertura total. Recebido '$Login'." -ForegroundColor Yellow
}

# Função para obter token JWT
function Get-JWTToken {
    param(
        [string]$Login,
        [string]$Password,
        [string]$BaseUrl
    )
    
    try {
        $loginUrl = "$BaseUrl/api/login"
        $body = @{
            login = $Login
            password = $Password
        } | ConvertTo-Json
        
        if ($Verbose) {
            Write-Host "🔑 Obtendo token JWT..." -ForegroundColor Yellow
            Write-Host "URL: $loginUrl" -ForegroundColor Gray
            Write-Host "Login: $Login" -ForegroundColor Gray
        }
        
        $response = Invoke-WebRequest -Uri $loginUrl -Method POST -ContentType "application/json" -Body $body -ErrorAction Stop
        $json = $response.Content | ConvertFrom-Json
        
        if ($json.token) {
            if ($Verbose) {
                Write-Host "✅ Token obtido com sucesso!" -ForegroundColor Green
                Write-Host "Usuário: $($json.user.name) ($($json.user.role))" -ForegroundColor Cyan
                Write-Host "Expira em: $($json.expiresIn) segundos" -ForegroundColor Cyan
            }
            return $json.token
        } else {
            throw "Token não encontrado na resposta"
        }
    }
    catch {
        Write-Host "❌ Erro ao obter token JWT: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Função para criar headers com JWT
function Get-JWTHeaders {
    param([string]$Token)
    
    if (-not $Token) {
        Write-Host "❌ Token JWT não fornecido" -ForegroundColor Red
        return $null
    }
    
    return @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
}

# Função para testar endpoint com JWT
function Test-EndpointWithJWT {
    param(
        [string]$Url,
        [string]$Method = "GET",
        [string]$Body = $null,
        [string]$Token,
        [string]$Description = "Endpoint"
    )
    
    try {
        $headers = Get-JWTHeaders -Token $Token
        if (-not $headers) {
            return $false
        }
        
        if ($Verbose) {
            Write-Host "🧪 Testando: $Description" -ForegroundColor Yellow
            Write-Host "URL: $Url" -ForegroundColor Gray
            Write-Host "Method: $Method" -ForegroundColor Gray
        }
        
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $headers
            ErrorAction = "Stop"
        }
        
        if ($Body) {
            $params.Body = $Body
        }
        
        $response = Invoke-WebRequest @params
        
        if ($Verbose) {
            Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
            if ($response.Content.Length -lt 500) {
                Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
            } else {
                Write-Host "Response: [$(($response.Content | ConvertFrom-Json).Count) items]" -ForegroundColor Cyan
            }
        }
        
        return $true
    }
    catch {
        if ($Verbose) {
            Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
        }
        return $false
    }
}

# Função para testar endpoint sem JWT (deve retornar 401)
function Test-EndpointWithoutJWT {
    param(
        [string]$Url,
        [string]$Method = "GET",
        [string]$Description = "Endpoint"
    )
    
    try {
        if ($Verbose) {
            Write-Host "🔒 Testando segurança: $Description (sem JWT)" -ForegroundColor Yellow
        }
        
        $response = Invoke-WebRequest -Uri $Url -Method $Method -ErrorAction Stop
        
        if ($Verbose) {
            Write-Host "❌ FALHA DE SEGURANÇA: Endpoint permitiu acesso sem JWT!" -ForegroundColor Red
        }
        return $false
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 401) {
            if ($Verbose) {
                Write-Host "✅ Segurança OK: Retornou 401 Unauthorized" -ForegroundColor Green
            }
            return $true
        } else {
            if ($Verbose) {
                Write-Host "❌ Erro inesperado: $($_.Exception.Message)" -ForegroundColor Red
            }
            return $false
        }
    }
}

# Exportar funções se executado como módulo
if ($MyInvocation.InvocationName -eq "jwt-utility.ps1" -or $MyInvocation.InvocationName -like "*jwt-utility.ps1") {
    # Se executado diretamente, fazer um teste básico
    Write-Host "=== TESTE BÁSICO JWT UTILITY ===" -ForegroundColor Magenta
    
    $token = Get-JWTToken -Login $Login -Password $Password -BaseUrl $BaseUrl -Verbose:$Verbose
    
    if ($token) {
        Write-Host "✅ Token obtido: $($token.Substring(0,50))..." -ForegroundColor Green
        
        # Teste de endpoint protegido
        $testResult = Test-EndpointWithJWT -Url "$BaseUrl/api/users" -Token $token -Description "Users endpoint" -Verbose:$Verbose
        
        if ($testResult) {
            Write-Host "✅ Teste com JWT: SUCESSO" -ForegroundColor Green
        } else {
            Write-Host "❌ Teste com JWT: FALHA" -ForegroundColor Red
        }
        
        # Teste de segurança
        $securityTest = Test-EndpointWithoutJWT -Url "$BaseUrl/api/users" -Description "Users endpoint" -Verbose:$Verbose
        
        if ($securityTest) {
            Write-Host "✅ Teste de segurança: SUCESSO" -ForegroundColor Green
        } else {
            Write-Host "❌ Teste de segurança: FALHA" -ForegroundColor Red
        }
        
        # Retorna o token para uso em outros scripts
        return @{
            token = $token
        }
    } else {
        Write-Host "❌ Falha ao obter token JWT" -ForegroundColor Red
        exit 1
    }
}
