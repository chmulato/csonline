# Migração do import.sql para o Flyway

Este documento descreve o processo de migração da inicialização do banco de dados do CSOnline do método tradicional (import.sql) para o sistema de migrações do Flyway.

## Motivação

A migração do import.sql para o Flyway foi motivada pelas seguintes razões:

1. **Controle de versão do esquema**: O Flyway permite versionar o esquema do banco de dados e as alterações de dados
2. **Consistência entre ambientes**: Garante que todos os ambientes (desenvolvimento, teste, produção) tenham o mesmo esquema e dados iniciais
3. **Atualizações incrementais**: Permite adicionar novas tabelas ou dados sem precisar recriar todo o banco
4. **Rastreabilidade**: Histórico de mudanças no banco de dados fica armazenado na tabela `flyway_schema_history`
5. **Melhor gerenciamento de erros**: Evita problemas com scripts parcialmente executados

## Processo de Migração

O processo de migração envolveu os seguintes passos:

1. **Adição do Flyway como dependência**:
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
       <version>10.0.0</version>
   </dependency>
   ```

2. **Divisão do import.sql em scripts de migração**:
   - `V1__Create_tables.sql`: Contém os comandos DDL para criação das tabelas
   - `V2__Insert_initial_data.sql`: Contém os comandos DML para inserção dos dados iniciais

3. **Configuração da inicialização do Flyway**:
   - Criação da classe `FlywayConfig` para inicializar o Flyway na inicialização da aplicação
   - Configuração do `application.properties` com parâmetros do Flyway

4. **Atualização da configuração do JPA**:
   - Modificação do `persistence.xml` para desativar a geração automática de esquema
   - Remoção da referência ao arquivo import.sql

5. **Criação de ferramentas de suporte**:
   - Script PowerShell para facilitar a execução de comandos do Flyway
   - Documentação sobre o novo processo

## Comparação entre os métodos

### Método Antigo (import.sql)

**Vantagens**:
- Simplicidade: Um único arquivo com todos os comandos SQL
- Integração automática com JPA/Hibernate

**Desvantagens**:
- Sem controle de versão
- Difícil de manter em projetos grandes
- Problemas com scripts parcialmente executados
- Recria todo o banco a cada inicialização (desenvolvimento)

### Método Novo (Flyway)

**Vantagens**:
- Controle de versão do esquema e dados
- Scripts incrementais
- Rastreabilidade de mudanças
- Consistência entre ambientes
- Robustez contra falhas

**Desvantagens**:
- Configuração inicial mais complexa
- Necessidade de gerenciar dependências adicionais

## Impacto nas Ferramentas Existentes

### Testes Unitários
Os testes unitários que dependiam da inicialização automática do banco de dados continuam funcionando normalmente, pois o Flyway é inicializado antes da execução dos testes.

### Scripts de Deploy
Os scripts de deploy foram atualizados para incluir a execução das migrações do Flyway durante o processo de implantação.

### Documentação
A documentação foi atualizada para refletir a nova abordagem, incluindo:
- Atualização do IMPORT_SQL.md
- Criação do MIGRATIONS.md
- Atualização do README.md

## Conclusão

A migração para o Flyway representa um avanço significativo na gestão do banco de dados do CSOnline, oferecendo maior robustez, rastreabilidade e facilidade de manutenção. Embora tenha exigido um esforço inicial de configuração, os benefícios a longo prazo justificam plenamente esta mudança.

O processo de migração foi concluído com sucesso em agosto de 2025, e todos os ambientes estão agora utilizando o Flyway para gerenciar as migrações do banco de dados.
