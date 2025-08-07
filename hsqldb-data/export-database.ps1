# Script para extrair todos os dados do banco HSQLDB do CSOnline
# Versão: 1.0
# Autor: CSOnline Team
# Data: 2025-08-07

param(
    [string]$OutputPath = "C:\dev\csonline\hsqldb-data\database-export.sql",
    [string]$DatabaseUrl = "jdbc:hsqldb:hsql://localhost:9001/csonline",
    [string]$Username = "sa",
    [string]$Password = "",
    [switch]$Verbose,
    [switch]$Help
)

if ($Help) {
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "SCRIPT DE EXPORTAÇÃO DE DADOS - CSONLINE HSQLDB" -ForegroundColor Cyan
    Write-Host "============================================================" -ForegroundColor Cyan
    
    Write-Host "`nDESCRIÇÃO:" -ForegroundColor Yellow
    Write-Host "Este script extrai todos os dados do banco HSQLDB do CSOnline" -ForegroundColor Gray
    Write-Host "e gera um arquivo SQL com todos os dados das tabelas." -ForegroundColor Gray
    
    Write-Host "`nUSO:" -ForegroundColor Yellow
    Write-Host ".\export-database.ps1                           # Exportação padrão" -ForegroundColor White
    Write-Host ".\export-database.ps1 -Verbose                 # Com logs detalhados" -ForegroundColor White
    Write-Host ".\export-database.ps1 -OutputPath 'C:\temp\export.sql'" -ForegroundColor White
    Write-Host ".\export-database.ps1 -Help                    # Esta ajuda" -ForegroundColor White
    
    Write-Host "`nPARÂMETROS:" -ForegroundColor Yellow
    Write-Host "-OutputPath  : Caminho do arquivo de saída (padrão: database-export.sql)" -ForegroundColor Gray
    Write-Host "-DatabaseUrl : URL do banco HSQLDB" -ForegroundColor Gray
    Write-Host "-Username    : Usuário do banco (padrão: sa)" -ForegroundColor Gray
    Write-Host "-Password    : Senha do banco (padrão: vazio)" -ForegroundColor Gray
    Write-Host "-Verbose     : Exibir logs detalhados" -ForegroundColor Gray
    Write-Host "-Help        : Exibir esta ajuda" -ForegroundColor Gray
    
    exit 0
}

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "EXPORTAÇÃO DE DADOS - CSONLINE HSQLDB" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Data/Hora: $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')" -ForegroundColor Gray
Write-Host "Arquivo de saída: $OutputPath" -ForegroundColor Gray

# Função para obter dados via API REST (método alternativo usando a aplicação)
function Get-DataViaAPI {
    param(
        [string]$BaseUrl = "http://localhost:8080/csonline/api",
        [string]$Login = "empresa",
        [string]$Password = "empresa123"
    )
    
    try {
        Write-Host "`nObtendo token JWT..." -ForegroundColor Yellow
        
        $loginData = @{
            login = $Login
            password = $Password
        } | ConvertTo-Json
        
        $tokenResponse = Invoke-RestMethod -Uri "$BaseUrl/login" -Method POST -ContentType "application/json" -Body $loginData
        $token = $tokenResponse.token
        
        if (-not $token) {
            throw "Falha ao obter token JWT"
        }
        
        $headers = @{ "Authorization" = "Bearer $token" }
        
        Write-Host "Token obtido com sucesso!" -ForegroundColor Green
        
        # Estrutura para armazenar todos os dados
        $allData = @{
            exportDate = Get-Date -Format 'yyyy-MM-dd HH:mm:ss'
            database = "CSOnline HSQLDB"
            tables = @{}
        }
        
        # Lista de endpoints para extrair dados
        $endpoints = @(
            @{ name = "users"; endpoint = "/users"; description = "Usuários do sistema" },
            @{ name = "customers"; endpoint = "/customers"; description = "Clientes" },
            @{ name = "couriers"; endpoint = "/couriers"; description = "Entregadores" },
            @{ name = "deliveries"; endpoint = "/deliveries"; description = "Entregas" },
            @{ name = "teams"; endpoint = "/teams"; description = "Equipes" },
            @{ name = "sms"; endpoint = "/sms"; description = "Mensagens SMS" }
        )
        
        Write-Host "`nExtraindo dados das tabelas..." -ForegroundColor Yellow
        
        foreach ($ep in $endpoints) {
            try {
                Write-Host "- Extraindo $($ep.description)..." -ForegroundColor Gray
                
                $data = Invoke-RestMethod -Uri "$BaseUrl$($ep.endpoint)" -Method GET -Headers $headers
                $allData.tables[$ep.name] = @{
                    description = $ep.description
                    count = if ($data -is [array]) { $data.Count } else { if ($data) { 1 } else { 0 } }
                    data = $data
                }
                
                Write-Host "  ✅ $($allData.tables[$ep.name].count) registros extraídos" -ForegroundColor Green
                
            } catch {
                Write-Host "  ❌ Erro ao extrair $($ep.description): $($_.Exception.Message)" -ForegroundColor Red
                $allData.tables[$ep.name] = @{
                    description = $ep.description
                    count = 0
                    error = $_.Exception.Message
                    data = @()
                }
            }
        }
        
        return $allData
        
    } catch {
        Write-Host "❌ Erro ao conectar com a API: $($_.Exception.Message)" -ForegroundColor Red
        throw
    }
}

# Função para gerar SQL a partir dos dados
function Generate-SQLFromData {
    param($data, $outputPath)
    
    $sqlContent = @"
-- ============================================================
-- EXPORTAÇÃO COMPLETA DO BANCO CSONLINE HSQLDB
-- ============================================================
-- Data de exportação: $($data.exportDate)
-- Database: $($data.database)
-- Gerado automaticamente pelo script export-database.ps1
-- ============================================================

"@

    foreach ($tableName in $data.tables.Keys) {
        $table = $data.tables[$tableName]
        
        $sqlContent += @"

-- ============================================================
-- TABELA: $tableName ($($table.description))
-- Registros: $($table.count)
-- ============================================================

"@
        
        if ($table.count -gt 0 -and $table.data) {
            $records = if ($table.data -is [array]) { $table.data } else { @($table.data) }
            
            foreach ($record in $records) {
                $sqlContent += "-- Registro: $tableName`n"
                
                # Converter objeto para comentários SQL legíveis
                $properties = $record | Get-Member -MemberType Properties | Select-Object -ExpandProperty Name
                
                foreach ($prop in $properties) {
                    $value = $record.$prop
                    if ($value -is [string]) {
                        $value = "'$($value.Replace("'", "''"))'"
                    } elseif ($value -eq $null) {
                        $value = "NULL"
                    } elseif ($value -is [bool]) {
                        $value = if ($value) { "TRUE" } else { "FALSE" }
                    }
                    
                    $sqlContent += "-- $prop : $value`n"
                }
                
                $sqlContent += "`n"
            }
        } else {
            $sqlContent += "-- Nenhum registro encontrado ou erro na extração`n"
            if ($table.error) {
                $sqlContent += "-- Erro: $($table.error)`n"
            }
        }
    }
    
    $sqlContent += @"

-- ============================================================
-- RESUMO DA EXPORTAÇÃO
-- ============================================================
"@
    
    $totalRecords = 0
    foreach ($tableName in $data.tables.Keys) {
        $table = $data.tables[$tableName]
        $sqlContent += "-- $tableName : $($table.count) registros`n"
        $totalRecords += $table.count
    }
    
    $sqlContent += "-- TOTAL GERAL: $totalRecords registros`n"
    $sqlContent += "-- ============================================================`n"
    
    # Salvar arquivo
    $sqlContent | Out-File -FilePath $outputPath -Encoding UTF8
    
    return $totalRecords
}

# Execução principal
try {
    Write-Host "`nIniciando exportação dos dados..." -ForegroundColor Yellow
    
    # Tentar conexão via API REST
    Write-Host "`nTentando conexão via API REST..." -ForegroundColor Cyan
    $data = Get-DataViaAPI
    
    Write-Host "`nGerando arquivo SQL..." -ForegroundColor Yellow
    $totalRecords = Generate-SQLFromData -data $data -outputPath $OutputPath
    
    Write-Host "`n============================================================" -ForegroundColor Cyan
    Write-Host "EXPORTAÇÃO CONCLUÍDA COM SUCESSO!" -ForegroundColor Green
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host "Arquivo gerado: $OutputPath" -ForegroundColor White
    Write-Host "Total de registros: $totalRecords" -ForegroundColor White
    Write-Host "Tamanho do arquivo: $([math]::Round((Get-Item $OutputPath).Length / 1KB, 2)) KB" -ForegroundColor White
    
    if ($Verbose) {
        Write-Host "`nResumo por tabela:" -ForegroundColor Yellow
        foreach ($tableName in $data.tables.Keys | Sort-Object) {
            $table = $data.tables[$tableName]
            Write-Host "- $tableName : $($table.count) registros" -ForegroundColor Gray
        }
    }
    
} catch {
    Write-Host "`n❌ ERRO durante a exportação:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
