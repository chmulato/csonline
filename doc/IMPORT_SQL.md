# Gerenciamento de Dados Iniciais - CSOnline

O sistema CSOnline utiliza Flyway para gerenciamento de migrações de banco de dados e carga de dados iniciais. Este documento descreve a estrutura de dados de teste e consultas úteis para validação do sistema.

## Sistema de Migrações Flyway

O CSOnline utiliza Flyway 8.5.13 para controle de versão do banco de dados. As migrações estão localizadas em `src/main/resources/db/migration/` e são executadas automaticamente durante a inicialização da aplicação.

### Estrutura de Migrações Atual

1. **V1__Create_tables.sql** - Estrutura completa do banco de dados
2. **V2__Insert_initial_data.sql** - Dados iniciais para desenvolvimento e testes

### Dados de Teste Disponíveis

O sistema inclui dados simulados para validação completa das funcionalidades:

- **Usuário ADMIN**: Administração geral do sistema
- **Empresa BUSINESS**: Centro de distribuição com múltiplos clientes e couriers
- **2 Clientes**: Com diferentes fatores de preço e tabelas
- **2 Couriers**: Para testes de entrega e rastreamento
- **3 Entregas**: Com status variados (pendente, concluída, recebida)
- **Tabela de preços**: Diferenciada por cliente e tipo de veículo
- **Mensagens SMS**: Histórico de comunicação entre courier e customer

## Documentação dos Endpoints REST

Todos os dados podem ser consultados e validados via API REST documentada automaticamente:

- **API Swagger**: http://localhost:8080/csonline/swagger-ui/
- **Especificação OpenAPI**: http://localhost:8080/csonline/api/openapi.json
- **Editor Online**: [Swagger Editor](https://editor.swagger.io/)


## Consultas SQL para Validação

### Consultas Principais para Testes

```sql
-- Buscar entregas pendentes
SELECT d.*, c.name as courier_name, cu.name as customer_name 
FROM delivery d 
JOIN courier c ON d.idcourier = c.id 
JOIN customer cu ON d.idcustomer = cu.id 
WHERE d.completed = FALSE;

-- Buscar todas as mensagens de uma entrega
SELECT s.*, d.description as delivery_description 
FROM sms s 
JOIN delivery d ON s.iddelivery = d.id 
WHERE s.iddelivery = 1;

-- Buscar couriers de uma empresa
SELECT c.*, u.name, u.email, u.mobile 
FROM courier c 
JOIN app_user u ON c.iduser = u.id 
WHERE c.idbusiness = 2;

-- Verificar status das migrações do Flyway
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- Listar usuários por papel
SELECT role, COUNT(*) as total 
FROM app_user 
GROUP BY role;

-- Consultar tabela de preços por cliente
SELECT p.*, c.name as customer_name 
FROM price p 
JOIN customer c ON p.idcustomer = c.id 
ORDER BY c.name, p.locality;
```

### Consultas para Análise de Negócio

```sql
-- Entregas por status
SELECT 
    CASE 
        WHEN completed = TRUE THEN 'Concluída'
        WHEN received = TRUE THEN 'Recebida'
        ELSE 'Pendente'
    END as status,
    COUNT(*) as total,
    AVG(cost) as custo_medio
FROM delivery 
GROUP BY completed, received;

-- Couriers mais ativos
SELECT c.id, u.name, COUNT(d.id) as total_entregas, SUM(d.cost) as receita_total
FROM courier c
JOIN app_user u ON c.iduser = u.id
LEFT JOIN delivery d ON c.id = d.idcourier
GROUP BY c.id, u.name
ORDER BY total_entregas DESC;

-- Clientes por volume de entregas
SELECT cu.id, u.name, COUNT(d.id) as total_entregas, AVG(d.cost) as custo_medio
FROM customer cu
JOIN app_user u ON cu.iduser = u.id
LEFT JOIN delivery d ON cu.id = d.idcustomer
GROUP BY cu.id, u.name
ORDER BY total_entregas DESC;
```

## Guia de Testes e Validação

### Cenários de Teste Recomendados

1. **Autenticação e Autorização**
   - Teste login com diferentes perfis de usuário (ADMIN, BUSINESS, COURIER, CUSTOMER)
   - Valide restrições de acesso por papel
   - Verifique integração JWT para segurança

2. **Gestão de Entregas**
   - Crie novas entregas vinculando clientes e couriers
   - Teste mudanças de status (pendente → recebida → concluída)
   - Valide cálculos de custo baseados na tabela de preços

3. **Comunicação SMS**
   - Simule troca de mensagens entre courier e customer
   - Teste histórico de mensagens por entrega
   - Valide notificações automáticas

4. **Relatórios e Analytics**
   - Execute consultas de análise de negócio
   - Teste performance com múltiplas entregas
   - Valide integridade referencial entre tabelas

### Scripts de Teste Automatizados

O projeto inclui scripts PowerShell para testes automatizados:

```powershell
# Testar todos os endpoints
.\scr\tests\test-all-endpoints.ps1

# Testar módulo específico
.\scr\tests\test-couriers.ps1
.\scr\tests\test-customers.ps1
.\scr\tests\test-deliveries.ps1

# Health check geral
.\scr\tests\health-check-endpoints.ps1
```

### Boas Práticas para Testes

- **IDs Altos**: Use IDs ≥ 1000 em testes automatizados para evitar conflitos com dados iniciais
- **Cleanup**: Implemente limpeza de dados de teste após execução
- **Isolamento**: Cada teste deve ser independente e não afetar outros
- **Validação**: Sempre verifique tanto o status HTTP quanto o conteúdo da resposta
- **Performance**: Monitore tempo de resposta dos endpoints durante testes de carga

### Expandindo Dados de Teste

Para adicionar novos dados de teste, crie migrações incrementais:

```sql
-- Exemplo: V3__Add_test_data.sql
INSERT INTO app_user (id, role, name, login, password, email, mobile) VALUES
  (1001, 'CUSTOMER', 'Cliente Teste', 'teste', 'teste123', 'teste@email.com', '11123456789');

INSERT INTO customer (id, idbusiness, iduser, factorCustomer, priceTable) VALUES
  (1001, 2, 1001, 1.0, 'TabelaTeste');
```

## Integração com Sistema de Produção

### Configuração Atual

- **Ambiente**: Produção com WildFly 31.0.1.Final
- **Banco de Dados**: HSQLDB 2.7 com persistência em arquivo
- **URL Base**: http://localhost:8080/csonline/
- **Documentação API**: http://localhost:8080/csonline/swagger-ui/

### Monitoramento e Logs

- **Logs da Aplicação**: `logs/app.log`
- **Logs de Teste**: `logs/test.log`
- **Console WildFly**: http://localhost:9990/console
- **Métricas de Performance**: Disponíveis via JMX

## Referências Técnicas

- **Regras de Negócio**: [REGRAS_DE_NEGOCIO.md](REGRAS_DE_NEGOCIO.md)
- **Migrações Completas**: [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md)
- **Configuração WildFly**: [CONFIG_WILDFLY.md](CONFIG_WILDFLY.md)
- **Modelo de Dados**: [MODELO_DE_DADOS.md](MODELO_DE_DADOS.md)
- **Testes de Endpoints**: [TESTES_ENDPOINTS.md](TESTES_ENDPOINTS.md)
