# CSOnline Frontend - Coverage Tools

Script PowerShell avançado para gerenciamento e visualização de cobertura de testes.

## Utilização

### Modo Interativo (Recomendado)
```powershell
.\coverage-tools.ps1
```

Apresenta menu com 8 opções:
1. **Teste rápido** - Executa apenas os 46 testes implementados
2. **Cobertura completa** - Executa todos os testes com relatório de cobertura
3. **Abrir relatório HTML** - Abre o relatório visual no navegador
4. **Modo watch** - Execução contínua (desenvolvimento)
5. **Interface visual (UI)** - Interface gráfica dos testes
6. **Resumo e status** - Visão geral do projeto
7. **Componente específico** - Teste individual por componente
8. **Sair**

### Modo Direto (Parâmetros)

#### Teste Rápido
```powershell
.\coverage-tools.ps1 -Quick
```
Executa os 46 testes atuais rapidamente (±5 segundos).

#### Resumo Completo
```powershell
.\coverage-tools.ps1 -Summary
```
Mostra status detalhado:
- ✅ 46/46 testes passando
- Lista de componentes testados
- Lista de componentes pendentes
- Métricas de cobertura

#### Componente Específico
```powershell
.\coverage-tools.ps1 -Component -Filter "auth"
.\coverage-tools.ps1 -Component -Filter "layout"
.\coverage-tools.ps1 -Component -Filter "permission"
.\coverage-tools.ps1 -Component -Filter "navigation"
```

#### Cobertura Completa
```powershell
.\coverage-tools.ps1 -All
```
Executa testes + gera relatório + abre no navegador.

#### Modo Watch
```powershell
.\coverage-tools.ps1 -Watch
```
Execução contínua para desenvolvimento.

## Status Atual (08/08/2025)

### ✅ Componentes Testados
- **Auth Store:** 21 testes (JWT, roles, permissões)
- **MainLayout:** 16 testes (navegação, responsividade)
- **PermissionGuard:** 3 testes (controle de acesso)
- **Navigation:** 6 testes (roteamento básico)

**Total: 46/46 testes passando (100%)**

### ❌ Componentes Pendentes
**Prioridade Alta:**
- CourierManagement.vue
- CustomerManagement.vue
- DeliveryManagement.vue

**Prioridade Média:**
- UserManagement.vue
- TeamManagement.vue

**Prioridade Baixa:**
- SMSManagement.vue
- PriceManagement.vue

## Arquivos Gerados

### Relatórios de Cobertura
- `coverage/index.html` - Relatório visual completo
- `coverage/coverage-final.json` - Dados JSON para integração
- `coverage/lcov.info` - Formato LCOV para CI/CD

### Logs de Teste
- Console output em tempo real
- Métricas de performance
- Alertas de erro/warning

## Métricas de Qualidade

**Meta Estabelecida:** 70% em todas as métricas

**Status Atual:**
- **Lines:** ~85%+ ✅
- **Functions:** ~90%+ ✅  
- **Branches:** ~75%+ ✅
- **Statements:** ~88%+ ✅

## Integração CI/CD

### GitHub Actions (Futuro)
```yaml
- name: Run Frontend Tests
  run: |
    cd frontend
    .\coverage-tools.ps1 -All
    
- name: Upload Coverage
  uses: codecov/codecov-action@v3
  with:
    file: ./frontend/coverage/coverage-final.json
```

## Troubleshooting

### Script não executa
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Testes falham
1. Verificar dependências: `npm install`
2. Limpar cache: `npm run clean` (se disponível)
3. Recriar node_modules: `rm -rf node_modules && npm install`

### Relatório não abre
- Verificar se arquivo existe: `coverage/index.html`
- Executar primeiro: `.\coverage-tools.ps1 -All`

## Desenvolvido para CSOnline
**Versão:** 2.0  
**Última atualização:** 08/08/2025  
**Compatibilidade:** Vue 3, Vitest, Windows PowerShell
