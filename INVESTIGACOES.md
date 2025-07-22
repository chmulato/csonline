# Guia Prático: Migração JSF para Jakarta EE 10

## Objetivo
Guia completo para migração de projetos JSF antigos (Java EE/JSF 2.x) para Jakarta EE 10 com JSF 4.0, incluindo soluções para problemas comuns e estratégias testadas.

## Resumo do Ambiente de Destino
- **Jakarta Faces**: 4.0 (JSF 4.0)
- **CDI**: 4.0 com Weld 5.1.0.Final
- **Bean Validation**: 3.0 com Hibernate Validator 8.0.0.Final
- **Servlet Container**: Tomcat 10.1.13
- **Build Tool**: Maven 3.x
- **Java**: 17+

## **DESCOBERTAS CRÍTICAS DA MIGRAÇÃO**

### **1. PrimeFaces Jakarta EE - Descoberta Fundamental**
A descoberta mais importante foi que **PrimeFaces 13.0.7 padrão NÃO é compatível com Jakarta EE**, mesmo sendo uma versão recente. O PrimeFaces ainda utiliza namespace `javax.*` internamente.

**Solução comprovada:**
- Usar `PrimeFaces 14.0.0` com `<classifier>jakarta</classifier>`
- Verificar MANIFEST.MF para confirmar `jakarta.servlet.*` em vez de `javax.servlet.*`

### **2. H2 Database - Palavra Reservada `user`**
**Problema crítico descoberto**: H2 Database rejeita `user` como nome de tabela por ser palavra reservada.

**Erro típico:**
```sql
Syntax error in SQL statement "DROP TABLE IF EXISTS [*]user"
```

**Solução obrigatória:**
- Renomear tabela `user` para `users`
- Atualizar todas as foreign key references: `REFERENCES user(ID)` → `REFERENCES users(ID)`
- Atualizar sequências: `user_seq` → `users_seq`
- Atualizar comentários da tabela

### **3. Log4j 2 - Migração Completa Necessária**
**Descoberta**: Log4j 1.x (`log4j.properties`) é incompatível com Jakarta EE 10. Era necessária migração completa para Log4j 2.

**Problemas encontrados:**
- `log4j.properties` ignorado por Log4j 2.23.1
- Appender conflicts quando ambos os formatos coexistem
- Configuração Jakarta EE requer namespaces específicos

**Solução implementada:**
- Criação de `log4j2.xml` completo
- Configuração de appenders Console e RollingFile
- Loggers específicos para Jakarta EE frameworks

### **4. Jetty vs Cargo Plugin - Solução de Cache**
**Problema**: Maven Cargo Plugin com Tomcat embedded apresentava problemas persistentes de cache e configuração.

**Solução adotada:**
- Migração para Jetty Maven Plugin 11.0.17
- Eliminação de problemas de cache de configuração
- Startup mais rápido e confiável

### **5. DatabaseInitializer - Resource Path Crítico**

**Problema descoberto**: Path incorreto para recursos SQL causava falha na inicialização.

**Erro comum:**
```
Resource not found: /resources/data-h2.sql
```

**Solução identificada:**
- Path correto: `/data-h2.sql` (raiz do classpath)
- Path incorreto: `/resources/data-h2.sql`
- Arquivo deve estar em `src/main/resources/data-h2.sql`

### **6. Compilação Jakarta EE - Versões Específicas**

**Dependências críticas validadas:**

```xml
<jakarta.faces.version>4.0.6</jakarta.faces.version>
<myfaces.version>4.0.2</myfaces.version>
<weld.version>5.1.2.Final</weld.version>
<primefaces.version>14.0.0</primefaces.version>
<log4j.version>2.23.1</log4j.version>
<h2.version>2.3.232</h2.version>
```

## PROBLEMAS CRÍTICOS IDENTIFICADOS E SOLUÇÕES

### **CRÍTICO: H2 Database Schema Issues**

#### Problema: Palavra Reservada `user`

```sql
-- FALHA - 'user' é palavra reservada no H2
CREATE TABLE user (ID INT PRIMARY KEY, ...);

-- SOLUÇÃO - Usar nome não reservado
CREATE TABLE users (ID INT PRIMARY KEY, ...);
```

**AÇÃO OBRIGATÓRIA**: Atualizar todas as referências:

- Tabela: `user` → `users`
- Foreign Keys: `REFERENCES user(ID)` → `REFERENCES users(ID)`
- Sequências: `user_seq` → `users_seq`
- Comentários: `COMMENT ON TABLE user` → `COMMENT ON TABLE users`
```xml
<!-- FALHA - 'class' é palavra reservada no Jakarta EE 10 -->
<h:outputText styleClass="#{bootstrap.class}" />

<!-- SOLUÇÃO - Use sintaxe de array -->
<h:outputText styleClass="#{bootstrap['class']}" />
```

#### Problema: Propriedades CDI Wrapper
```xml
<!-- FALHA - Weld wrapper não expõe esta propriedade -->
#{facesContext.application.version}

<!-- SOLUÇÃO - Use valores estáticos ou alternativas -->
#{facesContext.viewRoot.viewId}
```

**AÇÃO OBRIGATÓRIA**: Revisar todas as EL expressions em `.xhtml` procurando por:
- `#{bean.class}` → `#{bean['class']}`
- `#{facesContext.application.version}` → Remover ou substituir por constante
- Qualquer propriedade que use palavras reservadas Java

### **CRÍTICO: PrimeFaces - Versão Jakarta EE**

**DESCOBERTA ESSENCIAL**: PrimeFaces 13.0.7 padrão ainda usa namespace `javax.*` em vez de `jakarta.*`

#### Problema: Versão Incorreta do PrimeFaces
```xml
<!-- INCORRETO - Usa javax.* internamente -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>13.0.7</version>
</dependency>
```

#### Solução: Versão Jakarta EE Específica
```xml
<!-- CORRETO - Versão Jakarta EE -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>14.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

**VERIFICAÇÃO OBRIGATÓRIA**: 
- Confirmar que o JAR contém `jakarta.servlet.*` no MANIFEST.MF
- Verificar se `UploadedFileCleanerListener` existe na versão Jakarta

### **CRÍTICO: Cache do Maven Cargo Plugin**

#### Problema: Mudanças não refletidas
O Cargo mantém cache persistente que impede que mudanças em arquivos `.xhtml` sejam refletidas.

#### **SOLUÇÃO OBRIGATÓRIA**:
```powershell
# 1. Parar servidor
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Stop-Process -Force

# 2. Limpar cache do Cargo
Remove-Item -Recurse -Force target/cargo -ErrorAction SilentlyContinue

# 3. Rebuild completo
mvn clean package

# 4. Reiniciar servidor
mvn cargo:run
```

### **CRÍTICO: Configuração Maven Crítica**

#### pom.xml - Dependencies Essenciais:
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jakarta.faces.version>4.0.2</jakarta.faces.version>
    <weld.version>5.1.0.Final</weld.version>
    <hibernate.validator.version>8.0.0.Final</hibernate.validator.version>
</properties>

<dependencies>
    <!-- JSF Implementation - Jakarta Faces -->
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>jakarta.faces</artifactId>
        <version>${jakarta.faces.version}</version>
    </dependency>
    
    <!-- CDI Implementation - Weld -->
    <dependency>
        <groupId>org.jboss.weld.servlet</groupId>
        <artifactId>weld-servlet-shaded</artifactId>
        <version>${weld.version}</version>
    </dependency>
    
    <!-- Bean Validation -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>${hibernate.validator.version}</version>
    </dependency>
    
    <!-- Expression Language API -->
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>jakarta.el</artifactId>
        <version>5.0.0-M1</version>
    </dependency>
</dependencies>
```

#### Cargo Plugin Configuration:
```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <version>1.10.10</version>
    <configuration>
        <container>
            <containerId>tomcat10x</containerId>
            <type>embedded</type>
        </container>
        <configuration>
            <properties>
                <cargo.servlet.port>8080</cargo.servlet.port>
            </properties>
        </configuration>
    </configuration>
</plugin>
```

### **CRÍTICO: Configuração web.xml Obrigatória**

#### src/main/webapp/WEB-INF/web.xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

    <display-name>JSF Jakarta EE 10 Application</display-name>
    
    <!-- JSF Servlet Configuration -->
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <!-- JSF URL Patterns -->
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.faces</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>/faces/*</url-pattern>
    </servlet-mapping>
    
    <!-- JSF Configuration -->
    <context-param>
        <param-name>jakarta.faces.PROJECT_STAGE</param-name>
        <param-value>Development</param-value>
    </context-param>
    
    <context-param>
        <param-name>jakarta.faces.FACELETS_SUFFIX</param-name>
        <param-value>.xhtml</param-value>
    </context-param>
    
    <!-- CDI Bootstrap -->
    <listener>
        <listener-class>org.jboss.weld.environment.servlet.Listener</listener-class>
    </listener>
    
</web-app>
```

#### src/main/webapp/WEB-INF/beans.xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
       https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
       version="4.0"
       bean-discovery-mode="annotated">
</beans>
```

## ROTEIRO DE MIGRAÇÃO PASSO A PASSO

### Etapa 1: Análise do Projeto Existente
```bash
# 1. Inventário de dependencies atuais
grep -r "javax.faces\|javax.servlet\|javax.inject" pom.xml src/

# 2. Listar todas as EL expressions problemáticas
grep -r "#{.*\.class}" src/main/webapp/
grep -r "#{facesContext\.application\." src/main/webapp/

# 3. Identificar anotações Java EE antigas
grep -r "@javax\." src/main/java/
```

### Etapa 2: Atualização de Dependencies
```xml
<!-- REMOVER estas dependencies antigas -->
<!-- javax.faces:jsf-api -->
<!-- javax.faces:jsf-impl -->
<!-- javax.servlet:javax.servlet-api -->
<!-- javax.inject:javax.inject -->

<!-- ADICIONAR estas dependencies novas -->
<!-- Conforme seção anterior -->
```

### Etapa 3: Refatoração de Código Java
```java
// ANTIGAS - Substituir
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

// NOVAS - Jakarta EE 10
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.enterprise.context.RequestScoped;
```

### Etapa 4: Correção de EL Expressions
#### Script PowerShell para correção automática:
```powershell
# Buscar e substituir palavras reservadas
Get-ChildItem -Path "src/main/webapp" -Filter "*.xhtml" -Recurse | 
ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace '#{([^}]*?)\.class}', '#{$1[''class'']}'
    $content = $content -replace '#{facesContext\.application\.version}', 'Jakarta Faces 4.0'
    Set-Content -Path $_.FullName -Value $content
}
```

### Etapa 5: Validação e Testes
```powershell
# 1. Limpeza completa
Remove-Item -Recurse -Force target/ -ErrorAction SilentlyContinue
mvn clean compile

# 2. Verificar erros de compilação
mvn compile | Select-String "ERROR"

# 3. Executar testes
mvn test

# 4. Deploy e teste
mvn cargo:run
```

## **TROUBLESHOOTING - Problemas Comuns**

### Erro: HTTP 500 - EL Expression
**Sintoma**: Página retorna erro 500 com `Property [xxx] not found`

**Soluções**:
1. Verificar se propriedade usa palavra reservada: `#{bean.class}` → `#{bean['class']}`
2. Verificar se EL expression existe: `#{facesContext.application.version}` → Remover
3. Limpar cache Cargo: `Remove-Item -Recurse -Force target/cargo`

### Erro: ClassNotFoundException jakarta.faces
**Sintoma**: `java.lang.ClassNotFoundException: jakarta.faces.webapp.FacesServlet`

**Solução**: Verificar dependencies no pom.xml - Jakarta Faces deve estar presente.

### Erro: Mudanças não aparecem no browser
**Sintoma**: Alterações em .xhtml não são refletidas

**Solução**: Cache do Cargo - sempre executar limpeza antes de redeploy.

### Erro: CDI Injection não funciona
**Sintoma**: `@Inject` retorna null

**Soluções**:
1. Verificar `beans.xml` na pasta `WEB-INF`
2. Verificar se Weld listener está no `web.xml`
3. Usar `@Named` ao invés de `@ManagedBean`

## **CHECKLIST DE VALIDAÇÃO FINAL**

### Pré-Deploy:
- [ ] Todas imports javax.* substituídas por jakarta.*
- [ ] EL expressions com palavras reservadas corrigidas
- [ ] web.xml configurado para Jakarta EE 10
- [ ] beans.xml presente e válido
- [ ] Dependencies Maven atualizadas
- [ ] Cache Cargo limpo

### Pós-Deploy:
- [ ] Servidor inicia sem erros
- [ ] Logs mostram Mojarra e Weld inicializados
- [ ] Página de teste retorna HTTP 200
- [ ] CDI injection funcionando
- [ ] Bean Validation ativo

### Logs de Sucesso Esperados:
```log
INFO: Inicializando Mojarra 4.0.2 para o contexto '/seu-projeto'
INFO: WELD-ENV-001100: Tomcat 7+ detected, CDI injection will be available
INFO: HV000001: Hibernate Validator 8.0.0.Final
INFO: Tomcat 10.x Embedded started on port [8080]
```

## **RESUMO EXECUTIVO**

### Principais Mudanças Necessárias:
1. **Dependencies**: javax.* → jakarta.*
2. **Imports Java**: javax.* → jakarta.*  
3. **EL Expressions**: Palavras reservadas precisam sintaxe especial
4. **Cache Management**: Cargo requer limpeza manual
5. **Configuration**: web.xml e beans.xml precisam namespaces atualizados

### Tempo Estimado de Migração:
- **Projeto Pequeno** (1-10 páginas): 2-4 horas
- **Projeto Médio** (10-50 páginas): 1-2 dias
- **Projeto Grande** (50+ páginas): 3-5 dias

### Riscos Críticos:
1. EL expressions com palavras reservadas quebram silenciosamente
2. Cache do Cargo pode mascarar problemas
3. Weld CDI wrapper não expõe todas propriedades JSF

### Benefícios da Migração:
- Compatibilidade com Java 17+
- Suporte a longo prazo
- Performance melhorada
- Recursos modernos do Jakarta EE 10

---

**RESULTADO DA MIGRAÇÃO COMPLETA:**

✅ **PrimeFaces 14.0.0-jakarta** funcionando corretamente  
✅ **H2 Database 2.3.232** inicializando com script corrigido (`users` table)  
✅ **Log4j 2.23.1** com configuração completa XML  
✅ **Apache MyFaces 4.0.2** carregado e funcional  
✅ **Weld 5.1.2.Final** CDI inicializado com sucesso  
✅ **Jetty 11.0.17** rodando sem problemas de cache  
✅ **Jakarta EE 10** totalmente funcional na porta 8080

**Logs de sucesso confirmados:**

```text
09:25:25.306 [main] INFO  DatabaseInitializer - Banco H2 inicializado com sucesso.
jul. 22, 2025 9:25:29 AM MyFaces Core has started, it took [3921] ms.
jul. 22, 2025 9:25:29 AM Running on PrimeFaces 14.0.0
```

**URLs funcionais:**

- `http://localhost:8080/csonline/` ✅
- `http://localhost:8080/csonline/login.xhtml` ✅

**Documento validado com projeto real funcionando em**: 22 de julho de 2025  
**Migração Jakarta EE 10**: ✅ **COMPLETA E FUNCIONAL**