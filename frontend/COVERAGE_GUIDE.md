# Relatório de Cobertura de Testes - CSOnline Frontend

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

### **Métricas de Cobertura:**

#### **Tipos de Métricas:**
- **% Statements**: Porcentagem de declarações executadas
- **% Branch**: Porcentagem de ramificações (if/else) testadas
- **% Functions**: Porcentagem de funções chamadas
- **% Lines**: Porcentagem de linhas executadas

#### **Thresholds Configurados:**
- **Mínimo Global**: 70% para todas as métricas
- **Alerta**: Quando qualquer métrica fica abaixo do threshold

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

### **Análise Atual da Cobertura:**

#### **Auth Store (src/stores/auth.js):**
- **90.6% Lines** - Excelente cobertura
- **70% Functions** - Todas as funções principais testadas
- **60.41% Branch** - Algumas condicionais não testadas
- **90.6% Statements** - Quase todas as declarações executadas

#### **MainLayout Component:**
- **Renderização básica testada**
- **Estados de autenticação cobertos**
- **Algumas interações específicas não testadas**

### **Recomendações para Melhorar Cobertura:**

#### **1. Cobertura de Branches (Condicionais):**
```bash
# Adicionar testes para cenários edge cases
# Exemplo: diferentes combinações de permissões
```

#### **2. Componentes Vue não testados:**
- `CourierManagement.vue`
- `CustomerManagement.vue`
- `UserManagement.vue`
- `DeliveryManagement.vue`

#### **3. Utilitários e Helpers:**
- Funções auxiliares do auth store
- Validadores de formulário
- Formatters

### **Script de Análise Rápida:**

```bash
# Ver apenas resumo
npm run test:coverage | grep -A 20 "Coverage report"

# Abrir relatório HTML automaticamente
start coverage/index.html  # Windows
open coverage/index.html   # macOS
xdg-open coverage/index.html  # Linux
```

### **Integração com CI/CD:**

```yaml
# Exemplo para GitHub Actions
- name: Run tests with coverage
  run: npm run test:coverage
  
- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    file: ./coverage/coverage-final.json
```

### **Alertas de Qualidade:**

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

### **Monitoramento Contínuo:**

```bash
# Executar testes em background
npm run test:watch

# Verificar cobertura após cada mudança
npm run test:coverage && echo "Cobertura atualizada!"
```

### **Relatórios Customizados:**

Para relatórios específicos, edite `vite.config.js`:

```javascript
coverage: {
  reporter: ['text', 'json', 'html', 'lcov'],
  // Adicionar outros formatos conforme necessário
}
```

---

## **Objetivo: Manter cobertura acima de 70% sempre!**
