# Scripts de Teste de Endpoints - CSOnline

Esta pasta contém scripts PowerShell para testar todos os endpoints da API CSOnline.

## Scripts Disponíveis

| Script | Descrição | Status |
|--------|-----------|--------|
| `test-couriers.ps1` | Testa endpoints de Couriers (/api/couriers) | Funcionando |
| `test-users.ps1` | Testa endpoints de Users (/api/users) | Parcial |
| `test-customers.ps1` | Testa endpoints de Customers (/api/customers) | Erro 500 |
| `test-teams.ps1` | Testa endpoints de Teams (/api/team) | Erro 500 |
| `test-deliveries.ps1` | Testa endpoints de Deliveries (/api/deliveries) | Funcionando |
| `test-sms.ps1` | Testa endpoints de SMS (/api/sms) | Funcionando |
| `test-login.ps1` | Testa endpoint de Login (/api/login) | Erro 404 |
| `test-all-endpoints.ps1` | Script master que executa todos os testes | Funcionando |
| `health-check-endpoints.ps1` | Verificação rápida de saúde dos endpoints | Funcionando |

## Como Usar

### Da pasta de testes:
```powershell
cd scr/tests

# Verificar saúde dos endpoints
.\health-check-endpoints.ps1

# Executar todos os testes
.\test-all-endpoints.ps1

# Executar teste específico
.\test-couriers.ps1
.\test-all-endpoints.ps1 -OnlyTest 'Couriers'

# Pular testes específicos
.\test-all-endpoints.ps1 -SkipCustomers -SkipTeams
```

### Da raiz do projeto:
```powershell
# Script de conveniência
.\run-tests.ps1 -HealthCheck
.\run-tests.ps1
.\run-tests.ps1 -OnlyTest 'Couriers'

# Execução direta
.\scr\tests\health-check-endpoints.ps1
.\scr\tests\test-all-endpoints.ps1
```

## Status dos Endpoints

- **Funcionando**: /api/couriers, /api/deliveries, /api/sms
- **Parcial**: /api/users (individual OK, lista com erro 500)
- **Erro 500**: /api/customers, /api/team (problemas de serialização)
- **Erro 404**: /api/login (endpoint não encontrado)

## Pré-requisitos

- Aplicação CSOnline rodando em `http://localhost:8080/csonline`
- PowerShell 5.0 ou superior
- Módulo `Invoke-RestMethod` disponível

## Estrutura dos Testes

Cada script de teste inclui:
- Listagem de recursos (GET /api/resource)
- Busca por ID (GET /api/resource/{id})
- Criação (POST /api/resource)
- Atualização (PUT /api/resource/{id})
- Exclusão (DELETE /api/resource/{id})

## Troubleshooting

Para problemas com endpoints:
1. Verifique logs do WildFly em `wildfly/standalone/log/`
2. Verifique serialização JSON nas entidades
3. Verifique consultas JPA
4. Verifique referências circulares não tratadas
