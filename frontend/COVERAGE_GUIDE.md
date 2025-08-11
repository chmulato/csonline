# Relatório de Cobertura de Testes - CSOnline Frontend

## Status Atual dos Testes (11 de Agosto 2025)

**Resultado dos Testes:** ✅ **566/567 testes passando (1 skipped)**  
**Arquivos de Teste:** 34 arquivos ativos (34 executados)  
**Tempo de Execução (run --coverage):** ~36s (inclui transformação + coleta)

### Resumo de Cobertura Global (Vitest / V8)

| Métrica | % Atual | Threshold Global | Status |
|---------|---------|------------------|--------|
| Statements | 74.62% | 70% | ✅ |
| Lines | 74.62% | 70% | ✅ |
| Branches | 78.92% | 70% | ✅ |
| Functions | 41.78% | 70% | ❌ (gargalo) |

> Observação: A baixa cobertura de **Functions** decorre principalmente de helpers/utilitários não invocados, fluxos de erro não exercitados e código legado/heurístico adicionado para compatibilidade de suites (ex.: modos "legacy" em componentes de gestão). O plano abaixo prioriza elevar Functions ≥ 60% rápido e ≥ 70% na próxima iteração.

### **Principais Suites de Teste Ativas (agrupadas)**

| Grupo / Suite (exemplos) | Qtde de Testes | Observações |
|--------------------------|----------------|-------------|
| Auth Store / auth.test | 21 | Cobertura alta de fluxos de login/logout e roles |
| MainLayout (simple + complex) | 18 (2 arquivos) | Renderização, dashboard, navegação, menu |
| PermissionGuard / debug-permission | 4 | Acesso por role e permission flags |
| Navigation simples | 5 | Rotas básicas e eventos back |
| Gestão de Usuários (fixed + utils + simple) | ~62 | CRUD, erros, navegação, edição |
| Gestão de Entregas (full + simplified + simple + fixed + utils) | ~69 | Status mapping, filtros, erros, modal |
| Gestão de SMS (legacy + simplified + simple) | ~46 | Filtros, templates, CRUD simulado |
| Gestão de Clientes (completo + simple) | 23 | CRUD, erros, dados vazios |
| Gestão de Preços (simple) | 7 | Estrutura e estados básicos |
| Gestão de Times (fixed + simple) | 6 | Carregamento inicial / erros mocks |
| Gestão de Entregadores (simple) | 8 | Placeholder inicial |
| Delivery / Team placeholder tests | 2 | Sanidade de montagem |
| API / permission guard auxiliares | 25+ | Testes de integração leve de endpoints mockados |

Total somado ≈ 566 (1 skipped) — alinhado com execução mais recente.

## Como Visualizar a Cobertura de Testes Vue.js

### **Comandos Disponíveis:**

```bash
# Executar testes com cobertura
npm run test:coverage

# Executar testes em modo watch com cobertura
npm run test:watch

# Abrir interface visual dos testes com cobertura
npm run test:coverage:ui
```

### **Arquivos de Relatório Gerados:**

- **`coverage/index.html`** - Relatório HTML interativo (RECOMENDADO)
- **`coverage/coverage-final.json`** - Dados JSON para integração
- **Console Output** - Resumo rápido no terminal

### **Métricas de Cobertura (definições)**
| Métrica | Descrição |
|---------|-----------|
| % Statements | Declarações executadas |
| % Branch | Ramificações (if/else/switch/?:) cobertas |
| % Functions | Funções/métodos invocados |
| % Lines | Linhas físicas executadas |

Threshold Global configurado: 70% (todos).  
Alerta ativo: Functions < 70%.

### **Estrutura do Relatório HTML:**

#### **Página Principal:**
- Resumo geral de cobertura
- Lista de todos os arquivos
- Status por diretório

#### **Detalhes por Arquivo:**
- Código fonte com destaque colorido:
  - Verde: Linhas cobertas
  - Vermelho: Linhas não cobertas
  - Amarelo: Ramificações parcialmente cobertas

### **Arquivos Excluídos da Cobertura:**

```javascript
exclude: [
  'node_modules/',
  'src/test/',
  '**/*.test.js',
  '**/*.spec.js',
  '**/coverage/**',
  '**/dist/**',
  '**/build/**'
]
```

### **Análise Atual da Cobertura (pontos de destaque)**

Cobertura forte em: Auth Store, Layout, Guards, fluxos principais de CRUD (Usuários, Entregas, SMS, Clientes) e status/filtros de entregas.

Lacunas principais:
1. Baixa invocação de helpers utilitários (formatters, mapeamentos de status alternativos, ramos de fallback).  
2. Caminhos de erro e branches negativos (especialmente delete/save com exceções em alguns componentes) parcialmente testados.  
3. Código legado/heurístico (ex.: detecção de modo legacy em SMS) sem teste direcionado para cada ramo.  
4. Funções relacionadas a confirmação via `window.alert` não totalmente cobertas (jsdom limita comportamento).  
5. Métodos internos que manipulam estados derivados (ex.: paginação, alguns filtros) não exercitados em cenários limítrofes (listas grandes, vazio + filtro simultâneo, duplicação).  

Arquivos com 0% / muito baixo (indicativo no relatório): provavelmente placeholders ou módulos não mais usados. Ação: revisar para remover, migrar para pasta excluída ou criar teste mínimo (smoke). 

Meta imediata: elevar Functions para ≥ 60% em 1ª onda (foco em 20–25 funções não testadas mais críticas) e ≥ 70% na 2ª onda.

### **Plano de Melhoria de Cobertura (Repriorizado)**

#### Fase 1 (Curto Prazo - elevar Functions ≥ 60%)
1. Criar testes unitários diretos para helpers isoláveis (importação direta) – status, formatters, filtros.  
2. Exercitar explicitamente cada branch de status em Delivery (pending/received/completed/canceled/desconhecido).  
3. Adicionar cenários de erro (promises rejeitadas) para save/delete em Customer, Courier, Price, Team, SMS (alguns já parcialmente).  
4. Mockar `window.alert` / `window.confirm` (ex.: `vi.spyOn(window, 'alert').mockImplementation(() => {})`) e cobrir fluxos condicionais.  
5. Testar heurística legacy vs simplified em SMS forçando ambos caminhos (injetar dataset/minimum fields).  

#### Fase 2 (Médio Prazo - Functions ≥ 70%, Branches ≥ 82%)
1. Adicionar testes de paginação/filtro combinados (limites: página vazia, mudança de filtro reseta página).  
2. Cobrir caminhos de rollback / resetForm para cada componente de gestão.  
3. Introduzir testes de integração leve (montar 2 componentes interagindo via eventos simulados).  
4. Validar consistência de stores adicionais (se criados) – incluir interceptors HTTP / token refresh (se aplicável).  

#### Fase 3 (Futuro - Robustez & Manutenibilidade)
1. E2E com Cypress/Playwright para fluxos críticos (login -> criar entrega -> enviar SMS).  
2. Medir mutação (Stryker) para reforçar qualidade, não só linha.  
3. Excluir definitivamente arquivos mortos ou marcar com `/* c8 ignore file */` quando justificável.  

#### Priorização de Arquivos (seletiva)
| Categoria | Ação | Objetivo |
|-----------|------|----------|
| Helpers de status / filtros | Testar cada branch | +5–8% Functions |
| SMS modo legacy heurístico | Forçar ambos caminhos | +2–3% Functions |
| Fluxos de erro CRUD | Rejeições explícitas | +5% Branches |
| Paginação / reset | Casos limite | +2% Lines |
| Placeholders 0% | Remover ou teste smoke | Limpar ruído |

### **Script de Análise Rápida**

```bash
# Executar cobertura e abrir relatório (Windows Powershell)
npm run test:coverage; start coverage/index.html

# (Linux/macOS)
npm run test:coverage && xdg-open coverage/index.html  # ou 'open' no macOS
```

### **Integração com CI/CD**

```yaml
# Exemplo para GitHub Actions
- name: Run tests with coverage
  run: npm run test:coverage
  
- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    file: ./coverage/coverage-final.json
```

### **Alertas de Qualidade**

#### **Boa Cobertura (>80%):**
- Código bem testado
- Baixo risco de bugs
- Manutenção segura

#### **Cobertura Média (60-80%):**
- Necessário mais testes
- Algumas áreas de risco
- Priorizar melhorias

#### **Baixa Cobertura (<60%):**
- Alto risco de bugs
- Testes insuficientes
- Refatoração perigosa

### **Monitoramento Contínuo**

```bash
# Executar testes em background
npm run test:watch

# Verificar cobertura após cada mudança
npm run test:coverage && echo "Cobertura atualizada!"
```

### **Relatórios Customizados**

Para relatórios específicos, edite `vite.config.js`:

```javascript
coverage: {
  reporter: ['text', 'json', 'html', 'lcov'],
  // Adicionar outros formatos conforme necessário
}
```

---

## **Próximos Passos (Roadmap Resumido)**

1. (Semana 1) Pacote Fase 1: adicionar testes de helpers/erros + mock alert/confirm.  
2. (Semana 2) Paginação & legacy SMS branches + limpeza de placeholders 0%.  
3. (Semana 3) Introduzir testes de integração (2 componentes) e gates CI (falhar se Functions < 55% inicialmente, subir gradualmente).  
4. (Semana 4) Iniciar setup E2E (Playwright) + pipeline Codecov / badge README.  
5. (Contínuo) Remover código morto e adicionar comentários `/* c8 ignore next */` apenas para casos inevitáveis (bloc de catch com log).  

Exemplo de gate incremental em package.json (script futuro):
```json
{
  "vitest": {
    "coverage": { "lines": 70, "functions": 55, "branches": 75, "statements": 70 }
  }
}
```

---

**Documentação atualizada em:** 11 de agosto de 2025  
**Status atual:** ✅ 566/567 testes passando (1 skipped)  
**Próxima revisão sugerida:** 18 de agosto de 2025  
**Meta de cobertura:** ≥ 70% global; Functions ≥ 60% (curto prazo) e ≥ 70% (meta)  
**Observação:** Consolidar redução de código morto antes de elevar gates definitivos (evita ruído artificial).
