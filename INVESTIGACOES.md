
# Guia Prático: Migração JSF para Jakarta EE 10

## Objetivo

Guia objetivo para migrar projetos JSF antigos (Java EE/JSF 2.x) para Jakarta EE 10 (JSF 4.0), com soluções diretas para problemas reais.

## Ambiente de Destino

- **Jakarta Faces**: 4.0 (JSF 4.0)
- **CDI**: 4.0 (Weld 5.1.0.Final)
- **Bean Validation**: 3.0 (Hibernate Validator 8.0.0.Final)
- **Servlet Container**: Tomcat 10.1.13
- **Build Tool**: Maven 3.x
- **Java**: 11

---

## Descobertas Críticas da Migração


### 1. PrimeFaces Jakarta EE

**Problema:** PrimeFaces 13.0.7 padrão NÃO é compatível com Jakarta EE (usa `javax.*`).

**Solução:**

- Usar `PrimeFaces 14.0.0` com `<classifier>jakarta</classifier>`
- Verificar MANIFEST.MF para garantir `jakarta.servlet.*`


### 2. H2 Database: Palavra Reservada `user`

**Problema:** H2 não aceita `user` como nome de tabela.

**Solução:**

- Renomear tabela para `users`
- Atualizar todas as referências (FK, sequências, comentários)


### 3. Log4j 2

**Problema:** Log4j 1.x não funciona com Jakarta EE 10.

**Solução:**

- Migrar para `log4j2.xml` completo
- Configurar appenders Console/RollingFile


### 4. Jetty vs Cargo Plugin

**Problema:** Cargo Plugin com Tomcat embedded tem problemas de cache.

**Solução:**

- Usar Jetty Maven Plugin 11.0.17


### 5. DatabaseInitializer: Resource Path

**Problema:** Caminho incorreto para SQL impede inicialização.

**Solução:**

- Usar `/data-h2.sql` na raiz do classpath


### 6. Recursos JSF/PrimeFaces

**Problema:** JSF tenta processar CSS/JS/imagens como XHTML (404).

**Solução:**

- Configurar `jakarta.faces.RESOURCE_EXCLUDES` corretamente no web.xml

**AÇÃO OBRIGATÓRIA**: Configurar corretamente as exclusões de recursos para evitar que JSF processe arquivos estáticos como páginas XHTML.


### 7. Sistema de Temas Duplicado

**Problema:** Dois beans de tema (ThemeBean e ThemeSwitcherBean) causam inconsistência.

**Solução:**

- Unificar gestão de temas em ThemeSwitcherBean
- Atualizar login.xhtml para usar ThemeSwitcherBean


### 8. Scripts PowerShell

**Solução:** Scripts para start/stop do servidor Jetty (Windows).

- start-csonline.ps1: inicialização, logs, limpeza automática
- stop-csonline.ps1: encerramento seguro


### 9. Compilação Jakarta EE

**Dependências validadas:**

```xml
<jakarta.faces.version>4.0.8</jakarta.faces.version>
<mojarra.version>4.0.8</mojarra.version>
<weld.version>5.1.2.Final</weld.version>
<primefaces.version>14.0.0</primefaces.version>
<log4j.version>2.23.1</log4j.version>
<h2.version>2.3.232</h2.version>
```

## Problemas Críticos Identificados e Soluções

### H2 Database: Palavra Reservada

```sql
-- FALHA
CREATE TABLE user (...); -- 'user' é reservada
-- SOLUÇÃO
CREATE TABLE users (...);
```

**Ação obrigatória:** Atualizar todas as referências (FK, sequências, comentários)

### EL Expressions: Palavras Reservadas

```xml
<!-- FALHA -->
<h:outputText styleClass="#{bootstrap.class}" />
<!-- SOLUÇÃO -->
<h:outputText styleClass="#{bootstrap['class']}" />
```

**Ação obrigatória:** Corrigir todas as EL expressions problemáticas


### PrimeFaces: Versão Correta

```xml
<!-- INCORRETO -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>13.0.7</version>
</dependency>
<!-- CORRETO -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>14.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

**Verificação obrigatória:** Confirmar MANIFEST.MF com `jakarta.servlet.*`


### Cache do Cargo Plugin

**Problema:** Mudanças em .xhtml não refletem por cache do Cargo.
**Solução obrigatória:**

```powershell
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Stop-Process -Force
Remove-Item -Recurse -Force target/cargo -ErrorAction SilentlyContinue
mvn clean package
mvn cargo:run
```


### Maven: Configuração Essencial

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jakarta.faces.version>4.0.8</jakarta.faces.version>
    <weld.version>5.1.0.Final</weld.version>
    <hibernate.validator.version>8.0.0.Final</hibernate.validator.version>
</properties>
...existing code...
```


### Cargo Plugin: Configuração

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <version>1.10.10</version>
    ...existing code...
</plugin>
```


### web.xml e beans.xml: Configuração

```xml
<!-- web.xml -->
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" ... version="6.0">
    ...existing code...
</web-app>
<!-- beans.xml -->
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee" ... version="4.0" bean-discovery-mode="annotated" />
```


## Roteiro de Migração Passo a Passo


### 1. Análise do Projeto

```bash
grep -r "javax.faces\|javax.servlet\|javax.inject" pom.xml src/
grep -r "#{.*\.class}" src/main/webapp/
grep -r "@javax\." src/main/java/
```


### 2. Atualização de Dependencies

Remover dependências antigas (javax.*) e adicionar novas (jakarta.*).


### 3. Refatoração de Código Java

Substituir imports javax.* por jakarta.*


### 4. Correção de EL Expressions

Script PowerShell para correção automática:

```powershell
Get-ChildItem -Path "src/main/webapp" -Filter "*.xhtml" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace '#{([^}]*?)\.class}', '#{$1[''class'']}'
    $content = $content -replace '#{facesContext\.application\.version}', 'Jakarta Faces 4.0'
    Set-Content -Path $_.FullName -Value $content
}
```


### 5. Validação e Testes

```powershell
Remove-Item -Recurse -Force target/ -ErrorAction SilentlyContinue
mvn clean compile
mvn compile | Select-String "ERROR"
mvn test
mvn cargo:run
```

## Troubleshooting: Problemas Comuns


### Erro: HTTP 500 - EL Expression

**Sintoma:** Property [xxx] not found
**Soluções:**

1. Corrigir uso de palavras reservadas: `#{bean.class}` → `#{bean['class']}`
2. Remover ELs inexistentes: `#{facesContext.application.version}`
3. Limpar cache do Cargo


### Erro: ClassNotFoundException jakarta.faces

**Sintoma:** `java.lang.ClassNotFoundException: jakarta.faces.webapp.FacesServlet`
**Solução:** Verificar dependências no pom.xml


### Erro: Mudanças não aparecem no browser

**Sintoma:** Alterações em .xhtml não refletem
**Solução:** Limpar cache do Cargo antes do redeploy


### Erro: CDI Injection não funciona

**Sintoma:** `@Inject` retorna null
**Soluções:**

1. Verificar beans.xml em WEB-INF
2. Verificar Weld listener no web.xml
3. Usar @Named ao invés de @ManagedBean


## Checklist de Validação Final

### Pré-Deploy

- [ ] Imports javax.* substituídas por jakarta.*
- [ ] EL expressions corrigidas
- [ ] web.xml atualizado
- [ ] beans.xml válido
- [ ] Dependências Maven atualizadas
- [ ] Cache Cargo limpo

### Pós-Deploy

- [ ] Servidor inicia sem erros
- [ ] Logs Mojarra/Weld OK
- [ ] Página retorna HTTP 200
- [ ] CDI funcionando
- [ ] Bean Validation ativo

### Logs de Sucesso Esperados

```log
INFO: Inicializando Eclipse Mojarra 4.0.8 para o contexto '/seu-projeto'
INFO: WELD-ENV-001100: Tomcat 7+ detected, CDI injection will be available
INFO: HV000001: Hibernate Validator 8.0.0.Final
INFO: Tomcat 10.x Embedded started on port [8080]
```

jul. 22, 2025 10:48:22 AM Eclipse Mojarra JSF implementation started successfully.
jul. 22, 2025 10:48:22 AM Running on PrimeFaces 14.0.0

---

## Resumo Executivo

### Principais Mudanças

1. Dependencies: javax.* → jakarta.*
2. Imports Java: javax.* → jakarta.*
3. EL Expressions: corrigidas
4. Cache: limpeza manual do Cargo
5. Configuração: web.xml e beans.xml atualizados

### Tempo Estimado de Migração

- Projeto pequeno (1-10 páginas): 2-4h
- Projeto médio (10-50 páginas): 1-2 dias
- Projeto grande (50+ páginas): 3-5 dias

### Riscos Críticos

1. EL expressions com palavras reservadas quebram silenciosamente
2. Cache do Cargo pode mascarar problemas
3. Weld CDI wrapper não expõe todas propriedades JSF


### Benefícios

- Compatibilidade com Java 11
- Suporte a longo prazo
- Performance melhorada
- Recursos modernos do Jakarta EE 10

---

## Resultado da Migração

- PrimeFaces 14.0.0-jakarta funcionando corretamente
- H2 Database 2.3.232 inicializando com tabela `users`
- Log4j 2.23.1 com configuração XML
- Eclipse Mojarra 4.0.8 carregado e funcional
- Weld 5.1.2.Final CDI inicializado
- Jetty 11.0.17 rodando sem problemas de cache
- Jakarta EE 10 funcional na porta 8080
- web.xml simplificado
- Recursos customizados removidos (CSS, jQuery)
- Scripts PowerShell para gerenciamento automatizado

### Logs de sucesso confirmados

```text
INFO  DatabaseInitializer - Banco H2 inicializado com sucesso.
Eclipse Mojarra JSF implementation started successfully.
Running on PrimeFaces 14.0.0
INFO  Weld - WELD-ENV-001008: Initialize Weld using ServletContainerInitializer
[INFO] Started Server@...{STARTING}[11.0.17,sto=0]
```

### URLs funcionais

- http://localhost:8080/csonline/
- http://localhost:8080/csonline/login.xhtml

### Arquivos criados/atualizados

- log4j2.xml
- data-h2.sql
- DatabaseInitializer.java
- beans.xml
- start-csonline.ps1 / stop-csonline.ps1
- pom.xml, web.xml, faces-config.xml
- ThemeBean.java, ThemeSwitcherBean.java
- login.xhtml, theme.xhtml, template.xhtml
- MIGRACAO.md
- Imports Java e namespaces XHTML

### Configurações críticas

- Recursos JSF: jakarta.faces.RESOURCE_EXCLUDES
- PrimeFaces: tema nova-light
- H2 Database: tabela users
- Log4j 2: rotação de arquivos
- CDI/Weld: inicialização automática
- Jetty: plugin Maven

---

## Migração Jakarta EE 10: Completa, Otimizada e Validada

### Principais conquistas

1. Jakarta EE 10 completo
2. PrimeFaces 14.0.0-jakarta funcionando
3. Recursos JSF resolvidos
4. H2 Database funcional
5. Scripts PowerShell para start/stop
6. Jetty 11.0.17 sem cache
7. Log4j 2.23.1 estruturado
8. Eclipse Mojarra estável

### Estado final

- Servidor: Jetty 11.0.17 (dev) / Tomcat 10.1.x (prod)
- URLs: http://localhost:8080/csonline/ e /login.xhtml
- Banco: H2 2.3.232
- Interface: PrimeFaces 14.0.0-jakarta, tema nova-light
- CDI: Weld 5.1.2.Final
- Logging: Log4j 2.23.1
- JSF: Eclipse Mojarra 4.0.8

### Tempo real de migração

- Total: ~10 horas (projeto médio)

### Lições aprendidas

1. PrimeFaces: usar apenas 14.0.0-jakarta
2. JSF: Mojarra mais estável
3. H2: evitar palavra reservada user
4. Jetty superior ao Cargo
5. Scripts PowerShell essenciais
6. web.xml minimal
7. PrimeFaces supre interface, recursos customizados são redundantes

### Resultado técnico validado

```text
PrimeFaces 14.0.0-jakarta carregando temas corretamente
Eclipse Mojarra 4.0.8 processando XHTML sem erros
Weld 5.1.2.Final injetando dependencies
H2 Database inicializando automaticamente
Log4j 2.23.1 gerando logs estruturados
Jetty 11.0.17 servindo aplicação
web.xml simplificado
Recursos customizados removidos
Uso exclusivo de PrimeFaces
Configuração minimal e otimizada
```
