# AVISO: DOCUMENTO OBSOLETO

**Este documento foi fundido com o IMPORT_SQL.md no novo documento [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md).**

**Por favor, consulte o novo documento para obter informações atualizadas e completas sobre migrações e gerenciamento de dados no CSOnline.**

---

*O conteúdo abaixo é mantido apenas para referência histórica e pode estar desatualizado:*

# Migrações de Banco de Dados com Flyway

Este documento descreve como o sistema CSOnline utiliza o Flyway para gerenciar migrações de banco de dados.

## Visão Geral

O Flyway é uma ferramenta de migração de banco de dados que nos permite controlar versões do esquema do banco de dados de forma similar ao controle de versão de código. As principais vantagens são:

1. Controle de versão do esquema do banco de dados
2. Atualizações incrementais consistentes
3. Execução automática de migrações na inicialização da aplicação
4. Histórico de migrações aplicadas

## Estrutura das Migrações

Os scripts de migração são armazenados no diretório `src/main/resources/db/migration` e seguem a convenção de nomenclatura:

```
V<versão>__<descrição>.sql
```

Por exemplo:
- `V1__Create_tables.sql`: Primeira migração que cria as tabelas
- `V2__Insert_initial_data.sql`: Segunda migração que insere dados iniciais

## Como as Migrações são Executadas

As migrações são executadas automaticamente durante a inicialização da aplicação através da classe `FlywayConfig`. Esta classe é um bean CDI singleton que:

1. É inicializado durante o startup da aplicação
2. Verifica se o Flyway está habilitado nas configurações
3. Conecta-se ao banco de dados usando o DataSource injetado
4. Executa as migrações pendentes

## Comandos Maven para Flyway

O projeto também suporta comandos Maven para executar operações do Flyway manualmente:

```bash
# Exibir informações sobre o estado das migrações
mvn flyway:info

# Executar migrações pendentes
mvn flyway:migrate

# Limpar o banco de dados (excluir todas as tabelas)
mvn flyway:clean

# Validar os scripts de migração
mvn flyway:validate

# Reparar a tabela de histórico do Flyway
mvn flyway:repair
```

## Configuração

As configurações do Flyway estão definidas no arquivo `application.properties`:

```properties
# Configurações do Flyway
flyway.enabled=true
flyway.locations=classpath:db/migration
flyway.baseline-on-migrate=true
flyway.baseline-version=0
flyway.validate-on-migrate=true
```

## Boas Práticas para Migrações

1. **Nunca modifique um script de migração já aplicado** - Crie uma nova migração para fazer alterações
2. **Use transações nos scripts** - Para garantir consistência
3. **Mantenha os scripts idempotentes quando possível** - Use condicionais como `IF NOT EXISTS`
4. **Documente mudanças significativas** - Use comentários nos scripts
5. **Teste as migrações antes de implantá-las** - Use o comando `flyway:validate`

## Solução de Problemas

### Erro "Migration checksum mismatch"

Este erro ocorre quando um script de migração já aplicado foi modificado. Soluções:

1. Se estiver em ambiente de desenvolvimento, use `mvn flyway:repair` para corrigir o checksum
2. Em produção, crie uma nova migração com as alterações desejadas

### Erro ao executar migrações

Verifique os logs da aplicação. Os erros mais comuns são:

1. Sintaxe SQL inválida
2. Dependências entre tabelas não respeitadas (chaves estrangeiras)
3. Violações de restrições de unicidade

## Migrações Existentes

### V1__Create_tables.sql
Cria a estrutura inicial do banco de dados, incluindo as tabelas:
- app_user
- customer
- courier
- team
- price
- delivery
- sms

### V2__Insert_initial_data.sql
Insere os dados iniciais para todas as tabelas, incluindo:
- Usuários administrativos
- Empresas
- Entregadores
- Clientes
- Tabelas de preço
- Entregas de exemplo
