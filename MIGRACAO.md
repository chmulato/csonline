# Workflow de Migração para Jakarta EE 10

## Estratégia de Migração de Páginas JSF para Jakarta EE 10

Este documento descreve o workflow e checklist para migração das páginas JSF do CSOnline para Jakarta EE 10, focando na resolução dos problemas de CSS, gestão de temas e jQuery.

## Checklist de Migração por Página

### Estru### 22/07/2025 - 21:45 - Análise do Sistema de Gerenciamento de Temas

- [CONCLUÍDO] Identificado problema no seletor de temas na página login.xhtml
- [CONCLUÍDO] Análise do componente ThemeBean e sua interação com PrimeFaces
- [CONCLUÍDO] Análise do ThemeSwitcherBean e sua integração com PrimeFaces 14
- [IDENTIFICADO] Existem dois sistemas paralelos de gerenciamento de temas: ThemeBean e ThemeSwitcherBean
- [IDENTIFICADO] Login usa ThemeBean (#{themeMB}) enquanto theme.xhtml usa ThemeSwitcherBean (#{themeSwitcherBean})
- [IDENTIFICADO] ThemeBean lê configuração de web.xml e config.properties
- [IDENTIFICADO] ThemeSwitcherBean lê apenas de web.xml
- [ELABORADO] Plano de unificação dos dois componentes de tema
- [CRIADO] Código proposto para atualizar login.xhtml
- [PENDENTE] Unificação dos componentes de tema para usar apenas um gerenciador consistente
- [PENDENTE] Teste com diferentes temas disponíveis (nova-light, aristo, ui-lightness, etc.)Workflow

Para cada página (*.xhtml), seguir este processo em ordem:

1. **Análise Inicial**
   - [ ] Capturar screenshot da página antes da migração
   - [ ] Listar dependências de CSS, JS e componentes PrimeFaces
   - [ ] Documentar comportamento esperado

2. **Atualização de Namespaces e Imports**
   - [ ] Converter `xmlns:f="http://java.sun.com/jsf/core"` → `xmlns:f="jakarta.faces.core"`
   - [ ] Converter `xmlns:h="http://java.sun.com/jsf/html"` → `xmlns:h="jakarta.faces.html"`
   - [ ] Converter `xmlns:ui="http://java.sun.com/jsf/facelets"` → `xmlns:ui="jakarta.faces.facelets"`
   - [ ] Converter `xmlns:p="http://primefaces.org/ui"` → `xmlns:p="http://primefaces.org/ui"` (já compatível)

3. **Verificação de EL Expressions**
   - [ ] Substituir uso de palavras reservadas (`class`, `public`, etc.) por notação de array: `#{bean['class']}` em vez de `#{bean.class}`
   - [ ] Verificar uso de funções ou objetos implícitos que mudaram em Jakarta EE 10

4. **Testes de Renderização**
   - [ ] Verificar renderização inicial (sem interações)
   - [ ] Verificar Console do navegador para erros de JS/CSS
   - [ ] Capturar e documentar erros específicos

5. **Correções CSS/Temas**
   - [ ] Verificar caminhos dos recursos no `h:outputStylesheet` e `h:outputScript`
   - [ ] Confirmar se o tema PrimeFaces está sendo carregado
   - [ ] Adicionar atributos específicos para debug se necessário

6. **Verificação jQuery**
   - [ ] Confirmar carregamento do jQuery na ordem correta
   - [ ] Verificar conflitos com outros scripts
   - [ ] Testar funcionalidades JavaScript

7. **Pós-Migração**
   - [ ] Capturar screenshot da página após migração
   - [ ] Documentar mudanças necessárias
   - [ ] Marcar página como migrada no checklist geral

## Status das Páginas

```markdown
|------------------------------|--------------------|--------------------------------------------|---------------------------------------|-------------|
| Página                       | Status             | Problemas                                  | Solução                               | Verificação |
|------------------------------|--------------------|--------------------------------------------|---------------------------------------|-------------|
| login.xhtml                  | Testes Finais      | Namespace JSF, erros 404 em recursos, seletor de tema | Ajuste web.xml, namespace e ThemeBean | Em Teste    |
| logout.xhtml                 | Pendente           | -                                          | -                                     | -           |
| template.xhtml               | Em Migração        | Dependência de múltiplos recursos jQuery   | Reordenação dos scripts, atualização  | -           |
| theme.xhtml                  | Em Análise         | Incompatibilidade de ThemeBean vs ThemeSwitcherBean | Migração para gerenciador único de temas | Verificar PrimeFaces 14 |
| menu.xhtml                   | Pendente           | -                                          | -                                     | -           |
| business.xhtml               | Pendente           | -                                          | -                                     | -           |
| businesses.xhtml             | Pendente           | -                                          | -                                     | -           |
| change_password.xhtml        | Pendente           | -                                          | -                                     | -           |
| courier.xhtml                | Pendente           | -                                          | -                                     | -           |
| couriers.xhtml               | Pendente           | -                                          | -                                     | -           |
| customer.xhtml               | Pendente           | -                                          | -                                     | -           |
| customers.xhtml              | Pendente           | -                                          | -                                     | -           |
| deliveries.xhtml             | Pendente           | -                                          | -                                     | -           |
| deliveries_completed.xhtml   | Pendente           | -                                          | -                                     | -           |
| delivery.xhtml               | Pendente           | -                                          | -                                     | -           |
| delivery_courier.xhtml       | Pendente           | -                                          | -                                     | -           |
| delivery_customer.xhtml      | Pendente           | -                                          | -                                     | -           |
| delivery_view.xhtml          | Pendente           | -                                          | -                                     | -           |
| error.xhtml                  | Pendente           | -                                          | -                                     | -           |
| hello.xhtml                  | Pendente           | -                                          | -                                     | -           |
| messages.xhtml               | Pendente           | -                                          | -                                     | -           |
| pricetable.xhtml             | Pendente           | -                                          | -                                     | -           |
| pricetables.xhtml            | Pendente           | -                                          | -                                     | -           |
| resume.xhtml                 | Pendente           | -                                          | -                                     | -           |
| user.xhtml                   | Pendente           | -                                          | -                                     | -           |
| users.xhtml                  | Pendente           | -                                          | -                                     | -           |
|------------------------------|--------------------|--------------------------------------------|---------------------------------------|-------------|
```

## Soluções para Problemas Comuns

### 1. Erro 404 em Recursos PrimeFaces (core.js.xhtml, components.css.xhtml)

**Problema**: JSF tenta carregar recursos como `core.js.xhtml`, adicionando `.xhtml` indevidamente

**Soluções**:

1. Atualizar parâmetro de exclusão de recursos no `web.xml`:

```xml
<context-param>
<param-name>jakarta.faces.RESOURCE_EXCLUDES</param-name>
<param-value>.class .jsp .jspx .properties .groovy .css .js .png .jpg .jpeg .gif .ico .ttf .woff .woff2 .eot .svg</param-value>
</context-param>
```

2. Adicionar parâmetro para evitar sufixo `.xhtml` em recursos:

```xml
<context-param>
    <param-name>org.apache.myfaces.RESOURCE_SUFFIX</param-name>
    <param-value></param-value>
</context-param>
```

3. Configurar mapeamento para recursos do PrimeFaces:

```xml
<context-param>
    <param-name>primefaces.RESOURCE_MAPPINGS</param-name>
    <param-value>
        core.js=core.js
        validation.js=validation.js
        locale-pt.js=locale-pt.js
        components.css=components.css
        theme.css=theme.css
        primeicons.css=primeicons.css
    </param-value>
</context-param>
```

### 2. Problemas com Temas PrimeFaces

**Problema**: Tema PrimeFaces não carrega ou aparece sem estilo

**Soluções**:

1. Verificar se o tema está configurado corretamente no `web.xml`:

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>nova-light</param-value>
</context-param>
   ```

2. Garantir que a biblioteca do tema está disponível no classpath:

```xml
<dependency>
    <groupId>org.primefaces.themes</groupId>
    <artifactId>all-themes</artifactId>
    <version>1.0.10</version>
</dependency>
```

3. Adicionar referência direta ao tema na página se necessário:

```xml
<h:outputStylesheet library="primefaces-nova-light" name="theme.css" />
```

### 3. Problemas com jQuery

**Problema**: Funções jQuery não funcionam ou erro "$ is not defined"

**Soluções**:

1. Verificar ordem de carregamento (jQuery deve ser o primeiro):

```xml
<h:outputScript library="jquery" name="jquery-3.6.0.min.js" target="head" />
<!-- Scripts dependentes depois -->
<h:outputScript library="jquery" name="functions.js" target="body" />
```

2. Usar escopo seguro para evitar conflitos:

```javascript
(function($) {
    $(document).ready(function() {
        // Código jQuery aqui
    });
})(jQuery);
```

3. Verificar se jQuery está sendo carregado múltiplas vezes (via inspeção do navegador)

### 4. Problemas com EL Expressions

**Problema**: Erro "Property XXX not found" ou exceções relacionadas a EL

**Soluções**:

1. Substituir uso de palavras reservadas:

```xml
<!-- Incorreto -->
<h:outputText value="#{bean.class}" />

<!-- Correto -->
<h:outputText value="#{bean['class']}" />
```

2. Verificar objetos implícitos que mudaram de namespace:

```xml
<!-- Obsoleto -->
#{facesContext.externalContext.requestContextPath}

<!-- Preferível -->
#{request.contextPath}
```

## Processo de Teste e Validação

Para cada página, seguir este processo de validação:

1. **Teste Visual**:
   - Acessar a página diretamente via URL
   - Comparar com screenshot/design original
   - Verificar se todos os elementos estão sendo exibidos corretamente

2. **Inspeção de Recursos**:
   - Abrir Console do navegador (F12)
   - Verificar aba Network para recursos com status 404/403
   - Verificar Console para erros JavaScript

3. **Teste Funcional**:
   - Testar todas as interações: botões, links, formulários
   - Verificar validação de campos
   - Testar fluxo completo da funcionalidade

4. **Documentar Resultados**:
   - Atualizar status na tabela acima
   - Registrar soluções aplicadas
   - Documentar problemas remanescentes

## Estratégia de Deploy e Rollback

### Deploy

1. Atualizar apenas uma página por vez
2. Testar completamente antes de passar para próxima
3. Manter backup das páginas originais com extensão `.xhtml.bak`

### Rollback

1. Em caso de problema crítico, restaurar arquivo `.xhtml.bak`
2. Manter script para reverter múltiplas páginas se necessário

## Roteiro de Atualização Progressiva

### Fase 1: Páginas Core

- [  ] login.xhtml
- [  ] template.xhtml
- [  ] menu.xhtml
- [  ] theme.xhtml
- [  ] logout.xhtml

### Fase 2: Páginas de Cadastro

- [  ] user.xhtml
- [  ] users.xhtml
- [  ] business.xhtml
- [  ] businesses.xhtml
- [  ] customer.xhtml
- [  ] customers.xhtml
- [  ] courier.xhtml
- [  ] couriers.xhtml
- [  ] pricetable.xhtml
- [  ] pricetables.xhtml

### Fase 3: Páginas de Operação

- [  ] delivery.xhtml
- [  ] deliveries.xhtml
- [  ] delivery_courier.xhtml
- [  ] delivery_customer.xhtml
- [  ] delivery_view.xhtml
- [  ] deliveries_completed.xhtml

### Fase 4: Páginas Auxiliares

- [  ] change_password.xhtml
- [  ] error.xhtml
- [  ] messages.xhtml
- [  ] resume.xhtml
- [  ] hello.xhtml

## Log de Progresso

### 22/07/2025 - 17:08 - Início da Migração e Correção de Recursos

- [CONCLUÍDO] Iniciado processo de migração 
- [CONCLUÍDO] Identificado problema com recursos PrimeFaces na página login.xhtml
- [CONCLUÍDO] Diagnóstico: JSF tentando anexar extensão `.xhtml` aos recursos PrimeFaces
- [CONCLUÍDO] Ajustado parâmetro RESOURCE_EXCLUDES no web.xml
- [CONCLUÍDO] Adicionada configuração `org.apache.myfaces.RESOURCE_SUFFIX` para evitar sufixo .xhtml em recursos
- [CONCLUÍDO] Configurado mapeamento específico para recursos do PrimeFaces via `primefaces.RESOURCE_MAPPINGS`
- [CONCLUÍDO] Banco de dados H2 inicializado com sucesso (verificado nos logs)

### 22/07/2025 - 17:25 - Progresso na Página de Login

- [CONCLUÍDO] Criada estratégia de migração em fases, com prioridade para páginas core
- [CONCLUÍDO] Definido workflow com 7 passos para cada página
- [CONCLUÍDO] Estabelecido processo de teste e validação para confirmação
- [EM ANDAMENTO] Testes da página login.xhtml após ajustes nos recursos
- [PENDENTE] Verificação completa das EL Expressions na página login.xhtml
- [PENDENTE] Screenshot comparativo antes/depois da migração

### 22/07/2025 - 17:50 - Documentação e Próximos Passos

- [CONCLUÍDO] Documentado todos os problemas encontrados e soluções aplicadas
- [CONCLUÍDO] Criado documento de workflow com checklist detalhado
- [EM ANDAMENTO] Análise da página template.xhtml (dependência crítica)
- [PENDENTE] Corrigir referências CSS e JavaScript no template
- [PENDENTE] Migração completa das páginas core até 24/07/2025

### 22/07/2025 - 18:15 - Análise da Página Template

- [CONCLUÍDO] Inspeção do arquivo template.xhtml para identificar todos os componentes e recursos
- [CONCLUÍDO] Verificação dos scripts jQuery e dependências de carregamento
- [EM ANDAMENTO] Análise das inclusões de CSS e JS com prioridade para ordem de carregamento
- [PENDENTE] Testes de renderização após aplicação das correções de namespace
- [PLANEJADO] Refatoração dos componentes conforme necessário para Jakarta EE 10

### 22/07/2025 - 19:30 - Progresso na Migração de Namespaces

- [CONCLUÍDO] Verificação da página login.xhtml com novos recursos carregados
- [CONCLUÍDO] Validado carregamento de recursos PrimeFaces sem extensão .xhtml
- [EM ANDAMENTO] Atualização dos namespaces em template.xhtml de javax para jakarta
- [EM ANDAMENTO] Correção dos caminhos de recursos em login.xhtml
- [PENDENTE] Aplicação das mesmas correções em logout.xhtml e menu.xhtml

### 22/07/2025 - 21:00 - Testes e Validações

- [CONCLUÍDO] Verificação completa das EL Expressions na página login.xhtml
- [CONCLUÍDO] Identificação de padrões comuns de migração para reuso
- [EM ANDAMENTO] Testes de renderização da página login.xhtml com namespaces atualizados
- [EM ANDAMENTO] Correção de warnings específicos do MyFaces em log de console
- [PENDENTE] Validação do fluxo completo de login/logout com credenciais de teste

### 22/07/2025 - 21:45 - Correção da Gestão de Temas

- [CONCLUÍDO] Identificado problema no seletor de temas na página login.xhtml
- [CONCLUÍDO] Análise do componente ThemeBean e sua interação com PrimeFaces
- [IDENTIFICADO] Existem dois sistemas paralelos de gerenciamento de temas: ThemeBean e ThemeSwitcherBean
- [IDENTIFICADO] Login usa ThemeBean (#{themeMB}) enquanto theme.xhtml usa ThemeSwitcherBean (#{themeSwitcherBean})
- [PENDENTE] Unificação dos componentes de tema para usar apenas um gerenciador consistente
- [PENDENTE] Teste com diferentes temas disponíveis (nova-light, aristo, ui-lightness, etc.)

## Análise do Sistema de Temas

Durante a migração, foi identificado que o sistema possui dois componentes diferentes para gerenciamento de temas:

1. **ThemeBean** (`#{themeMB}`)
   - Utilizado na página `login.xhtml`
   - Implementado em `br.com.mulato.cso.view.beans.ThemeBean`
   - Usa `SelectItem` para lista de temas
   - Armazena tema padrão configurado em:
     - Parâmetro `themeDefault` no web.xml
     - Propriedade `client.theme_application` no config.properties

2. **ThemeSwitcherBean** (`#{themeSwitcherBean}`)
   - Utilizado na página `theme.xhtml`
   - Implementado em `br.com.mulato.cso.view.bean.ThemeSwitcherBean`
   - Usa classe interna `Theme` para lista de temas
   - Armazena tema padrão configurado em:
     - Parâmetro `themeDefault` no web.xml

### Inconsistências Identificadas

- Dois beans gerenciando temas separadamente
- Login utiliza `valueChangeListener="#{loginMB.changeTheme}"` que não está coordenado com ThemeSwitcherBean
- Configuração global do tema em `primefaces.THEME=nova-light` no web.xml pode conflitar com a seleção do usuário

### Solução Proposta

1. Unificar a gestão de temas em um único componente (ThemeSwitcherBean)
2. Atualizar login.xhtml para usar o mesmo componente que theme.xhtml
3. Garantir que a seleção de tema no login seja preservada na navegação
4. Verificar compatibilidade dos temas com PrimeFaces 14

### Código Proposto para login.xhtml

Substituir o trecho atual:

```xml
<h:outputText value="#{rotulo.theme}:" />
<p:selectOneMenu id="theme" label="#{rotulo.theme}"
    value="#{themeMB.theme}" valueChangeListener="#{loginMB.changeTheme}" onchange="this.form.submit();">
    <f:selectItems value="#{themeMB.themesList}" />
</p:selectOneMenu>
<p:spacer height="20px" />

<p:tooltip for="theme" value="#{rotulo.escolherTheme}" showEffect="fade" hideEffect="fade" />
```

Pelo código atualizado:

```xml
<h:outputText value="#{rotulo.theme}:" />
<p:selectOneMenu id="theme" label="#{rotulo.theme}"
    value="#{themeSwitcherBean.theme}" onchange="this.form.submit();">
    <f:selectItems value="#{themeSwitcherBean.themes}" />
</p:selectOneMenu>
<p:spacer height="20px" />

<p:tooltip for="theme" value="#{rotulo.escolherTheme}" showEffect="fade" hideEffect="fade" />
```

Adicionalmente, é necessário atualizar o `LoginController` para remover o método `changeTheme` e adaptar o código para usar diretamente o `ThemeSwitcherBean`.

### 23/07/2025 - 09:00 - Continuação das Correções de Template e Temas

- [PENDENTE] Finalização das alterações de namespace em template.xhtml
- [PENDENTE] Implementação da unificação dos componentes de tema
- [PENDENTE] Teste integrado do fluxo login → seleção de tema → redirecionamento
