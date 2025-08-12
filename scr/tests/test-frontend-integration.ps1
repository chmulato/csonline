param(
    [string]$BaseUrl = "http://localhost:8080/csonline",
    [string]$Login = "admin",
    [string]$Password = "admin123",
    [switch]$Verbose,
    [switch]$SkipUsers,
    [switch]$SkipCouriers,
    [switch]$SkipCustomers,
    [switch]$SkipDeliveries,
    [switch]$SkipPrices,
    [switch]$SkipTeams,
    [switch]$SkipSMS
)

$ErrorActionPreference = 'Stop'
$Green='Green'; $Red='Red'; $Yellow='Yellow'; $Cyan='Cyan'; $Gray='Gray'

Write-Host "============================================================" -ForegroundColor $Cyan
Write-Host "TESTE INTEGRADO FRONTEND ↔ BACKEND" -ForegroundColor $Cyan
Write-Host "Base: $BaseUrl" -ForegroundColor $Cyan
Write-Host "Data/Hora: $(Get-Date)" -ForegroundColor $Cyan
Write-Host "============================================================" -ForegroundColor $Cyan

# Força uso de admin (se outro login for passado, avisa e substitui)
if ($Login -ne 'admin') {
    Write-Host "⚠️  Este script requer perfil ADMIN. Substituindo Login='$Login' por 'admin'." -ForegroundColor $Yellow
    $Login = 'admin'
    if ($Password -ne 'admin123') { $Password = 'admin123' }
}

# Carrega utilitário JWT (funções apenas)
. "$PSScriptRoot/jwt-utility.ps1"

$token = Get-JWTToken -Login $Login -Password $Password -BaseUrl $BaseUrl
if (-not $token) { Write-Host "❌ Não foi possível obter token JWT" -ForegroundColor $Red; exit 1 }
$headers = Get-JWTHeaders -Token $token
Write-Host "✓ Autenticado como ADMIN" -ForegroundColor $Green

function Test-EndpointBasic {
    param(
        [string]$Name,
        [string]$ListPath,
        [string]$SinglePath,
        [ScriptBlock]$ShapeCheck,
        [switch]$Skip,
        [switch]$AllowEmpty
    )
    if ($Skip) { Write-Host "⏭️  PULANDO $Name" -ForegroundColor $Yellow; return }
    Write-Host "\n🧪 $Name" -ForegroundColor $Cyan
    try {
        $listUrl = "$BaseUrl/api$ListPath"
        $resp = Invoke-RestMethod -Uri $listUrl -Headers $headers -Method GET -ErrorAction Stop
        if (-not $resp) {
            if ($AllowEmpty) {
                Write-Host "  ⚠️ Lista vazia em $Name" -ForegroundColor $Yellow
                return
            } else { throw "Resposta vazia" }
        }
        $count = ($resp | Measure-Object).Count
        Write-Host "  ✓ Lista retornou $count registros" -ForegroundColor $Green
        $first = $resp | Select-Object -First 1
        if ($first) {
            if ($ShapeCheck) { & $ShapeCheck -Item $first }
            if ($SinglePath -and $first.id) {
                $singleUrl = "$BaseUrl/api$SinglePath/$($first.id)"
                $one = Invoke-RestMethod -Uri $singleUrl -Headers $headers -Method GET -ErrorAction Stop
                if ($one.id -ne $first.id) { throw "Inconsistência ID (lista=$($first.id) detalhe=$($one.id))" }
                Write-Host "  ✓ GET por ID ($($one.id)) consistente" -ForegroundColor $Green
            }
        } else {
            Write-Host "  ⚠️ Lista vazia - nada para validar shape" -ForegroundColor $Yellow
        }
    } catch {
    $errMsg = $_.Exception.Message
    Write-Host "  ❌ Falha em ${Name}: ${errMsg}" -ForegroundColor $Red
    }
}

# Shape check helpers (validam campos usados nos componentes)
$checkUser = { param($Item) if(-not ($Item.name -and $Item.login -and $Item.role)){ throw "Campos básicos ausentes em user" } else { Write-Host "  ✓ User shape OK" -ForegroundColor $Green } }
$checkCourier = { param($Item) if(-not $Item.factorCourier){ Write-Host "  ⚠️ factorCourier ausente" -ForegroundColor $Yellow } if(-not $Item.user -and -not $Item.email){ Write-Host "  ⚠️ dados user/email não presentes" -ForegroundColor $Yellow } else { Write-Host "  ✓ Courier shape mínimo" -ForegroundColor $Green } }
$checkCustomer = { param($Item) if(-not $Item.user){ Write-Host "  ⚠️ customer.user ausente" -ForegroundColor $Yellow } else { Write-Host "  ✓ Customer shape OK" -ForegroundColor $Green } }
$checkDelivery = { param($Item) if(-not ($Item.businessId -or $Item.business) ){ Write-Host "  ⚠️ businessId ausente" -ForegroundColor $Yellow } else { Write-Host "  ✓ Delivery shape básico" -ForegroundColor $Green } }
$checkPrice = { param($Item) if(-not $Item.vehicle){ Write-Host "  ⚠️ price.vehicle ausente" -ForegroundColor $Yellow } else { Write-Host "  ✓ Price shape OK" -ForegroundColor $Green } }
$checkTeam = { param($Item) if(-not ($Item.businessId -and $Item.courierId)){ throw "Team sem businessId/courierId" } else { Write-Host "  ✓ Team shape OK" -ForegroundColor $Green } }
$checkSMS = { param($Item) if(-not $Item.type){ Write-Host "  ⚠️ sms.type ausente" -ForegroundColor $Yellow } else { Write-Host "  ✓ SMS shape OK" -ForegroundColor $Green } }

# Execuções
Test-EndpointBasic -Name "USERS" -ListPath "/users" -SinglePath "/users" -ShapeCheck $checkUser -Skip:$SkipUsers
Test-EndpointBasic -Name "COURIERS" -ListPath "/couriers" -SinglePath "/couriers" -ShapeCheck $checkCourier -Skip:$SkipCouriers
Test-EndpointBasic -Name "CUSTOMERS" -ListPath "/customers" -SinglePath "/customers" -ShapeCheck $checkCustomer -Skip:$SkipCustomers
Test-EndpointBasic -Name "DELIVERIES" -ListPath "/deliveries" -SinglePath "/deliveries" -ShapeCheck $checkDelivery -Skip:$SkipDeliveries
Test-EndpointBasic -Name "PRICES" -ListPath "/prices" -SinglePath "/prices" -ShapeCheck $checkPrice -Skip:$SkipPrices
function Seed-TeamIfEmpty {
    if ($SkipTeams) { return }
    try {
        $teamsResp = Invoke-RestMethod -Uri "$BaseUrl/api/teams" -Headers $headers -Method GET -ErrorAction Stop
        if (-not $teamsResp -or $teamsResp.Count -eq 0) {
            Write-Host "  ↻ Seed: criando equipes (business + couriers)..." -ForegroundColor $Yellow
            # Buscar usuários e filtrar business
            $users = Invoke-RestMethod -Uri "$BaseUrl/api/users" -Headers $headers -Method GET -ErrorAction Stop
            $businessUser = $users | Where-Object { $_.role -eq 'BUSINESS' } | Select-Object -First 1
            if (-not $businessUser) { Write-Host "  ❌ Nenhum usuário BUSINESS encontrado" -ForegroundColor $Red; return }
            # Buscar couriers
            $couriersResp = Invoke-RestMethod -Uri "$BaseUrl/api/couriers" -Headers $headers -Method GET -ErrorAction Stop
            if (-not $couriersResp -or $couriersResp.Count -eq 0) { Write-Host "  ❌ Nenhum courier disponível para seed" -ForegroundColor $Red; return }
            $createdCount = 0
            foreach ($c in $couriersResp) {
                try {
                    $payload = @{ businessId = $businessUser.id; courierId = $c.id; factorCourier = ($c.factorCourier ? $c.factorCourier : 1.0) } | ConvertTo-Json
                    $create = Invoke-RestMethod -Uri "$BaseUrl/api/teams" -Headers $headers -Method POST -Body $payload -ContentType 'application/json' -ErrorAction Stop
                    Write-Host "    ✓ Team criado business=$($businessUser.id) courier=$($c.id) id=$($create.id)" -ForegroundColor $Green
                    $createdCount++
                } catch {
                    Write-Host "    ⚠️ Falha ao criar team para courier $($c.id): $($_.Exception.Message)" -ForegroundColor $Yellow
                }
            }
            if ($createdCount -gt 0) { Write-Host "  ✓ Seed de Teams concluído ($createdCount criados)" -ForegroundColor $Green }
        }
    } catch {
        Write-Host "  ❌ Erro ao semear Teams: $($_.Exception.Message)" -ForegroundColor $Red
    }
}

Seed-TeamIfEmpty
Test-EndpointBasic -Name "TEAMS" -ListPath "/teams" -SinglePath "/teams" -ShapeCheck $checkTeam -Skip:$SkipTeams -AllowEmpty
Test-EndpointBasic -Name "SMS" -ListPath "/sms" -SinglePath "/sms" -ShapeCheck $checkSMS -Skip:$SkipSMS

Write-Host "\n✅ Integração básica concluída." -ForegroundColor $Green
Write-Host "Use -Verbose para detalhes adicionais e switches -Skip* para pular módulos." -ForegroundColor $Gray
