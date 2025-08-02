# AVISO: DOCUMENTO OBSOLETO

**Este documento foi fundido com o MIGRATIONS.md no novo documento [MIGRACAO_BANCO_DADOS.md](MIGRACAO_BANCO_DADOS.md).**

**Por favor, consulte o novo documento para obter informações atualizadas e completas sobre migrações e gerenciamento de dados no CSOnline.**

---

*O conteúdo abaixo é mantido apenas para referência histórica e pode estar desatualizado:*

## Exemplos de Cenários Simulados
- Usuário ADMIN para administração geral do sistema.
- Empresa BUSINESS vinculada a múltiplos clientes e couriers, simulando ambiente corporativo.
- Dois clientes e dois couriers para testar entregas, autorizações e interações entre perfis.
- Entregas com diferentes status (pendente, concluída, recebida) e dados variados para validar regras de negócio.
- Tabela de preços diferenciada para clientes e tipos de veículos, permitindo testes de cálculo de custo.
- SMS simulando troca de mensagens entre courier e customer, útil para rastreamento e histórico.

Esses dados permitem validar cenários de autenticação, autorização, rastreamento, cálculo de preços e fluxo completo de entrega.
  
## Documentação dos Endpoints REST
Todos os dados simulados podem ser consultados e validados via endpoints REST documentados automaticamente pelo Swagger/OpenAPI.
- A especificação OpenAPI está disponível em `/api/openapi.json`.
- Para visualizar, utilize Swagger UI ou [Swagger Editor Online](https://editor.swagger.io/).


## Exemplos de Consultas para Testes
```sql
-- Buscar entregas pendentes
SELECT * FROM delivery WHERE completed = FALSE;

-- Buscar todas as mensagens de uma entrega
SELECT * FROM sms WHERE iddelivery = 1;

-- Buscar couriers de uma empresa
SELECT * FROM courier WHERE idbusiness = 2;

-- Verificar status das migrações do Flyway
SELECT * FROM flyway_schema_history;
```

Essas consultas ajudam a validar o funcionamento das regras de negócio, a integração entre entidades e o status das migrações aplicadas.

# Dicas para Testes
- Utilize os dados das migrações do Flyway para validar regras de negócio, persistência e integração entre camadas.
- Teste cenários de atualização, deleção e consulta por diferentes atributos e perfis de usuário.
- Simule operações de rastreamento e autorização usando os vínculos entre entidades (courier, customer, delivery).
- Para testes automatizados, utilize IDs altos para evitar conflitos com os dados iniciais inseridos pelas migrações.
- Expanda os scripts de migração conforme necessidade, adicionando novos clientes, couriers, entregas ou preços para cenários avançados.
- Os dados simulam o fluxo completo do sistema, incluindo autenticação, autorização, entrega, rastreamento e histórico de mensagens.

# IMPORT_SQL.md

## Migrações de Banco de Dados com Flyway

A partir de agosto de 2025, o sistema CSOnline passou a utilizar o Flyway para gerenciar as migrações do banco de dados, incluindo a criação de tabelas e inserção de dados iniciais. Esta abordagem substitui o uso do arquivo `import.sql` tradicional, oferecendo melhor controle de versão e consistência nas atualizações do banco de dados.

### Estrutura de Migrações do Flyway

Os scripts de migração estão localizados em `src/main/resources/db/migration` e seguem a convenção de nomenclatura `V<versão>__<descrição>.sql`:

1. **V1__Create_tables.sql** - Cria a estrutura inicial do banco de dados
2. **V2__Insert_initial_data.sql** - Insere os dados iniciais em todas as tabelas

### Relação entre entidades:
- Usuários (app_user) são vinculados a clientes, couriers e empresas.
- Clientes e couriers pertencem a uma empresa (BUSINESS) e possuem fator/tabela de preço próprios.
- Entregas relacionam clientes, couriers e empresas, com status e dados variados.
- Tabela de preços define valores por cliente, veículo e localidade.
- SMS registra o histórico de mensagens entre courier e customer, vinculado à entrega.

### Exemplo de dados iniciais (V2__Insert_initial_data.sql):
```sql
-- Usuários iniciais
INSERT INTO app_user (id, role, name, login, password, email, email2, address, mobile) VALUES
  (1, 'ADMIN', 'Administrador', 'admin', 'admin123', 'admin@cso.com', NULL, 'Rua Central, 100', '11999999999'),
  (2, 'BUSINESS', 'Empresa X', 'empresa', 'empresa123', 'empresa@cso.com', NULL, 'Av. Paulista, 200', '11888888888'),
  (3, 'COURIER', 'Entregador João', 'joao', 'joao123', 'joao@cso.com', NULL, 'Rua das Flores, 300', '11777777777'),
  (4, 'CUSTOMER', 'Cliente Carlos', 'carlos', 'carlos123', 'carlos@email.com', NULL, 'Rua dos Limões, 400', '11666666666'),
  (5, 'COURIER', 'Entregador Pedro', 'pedro', 'pedro456', 'pedro@cso.com', NULL, 'Rua das Laranjeiras, 500', '11555555555'),
  (6, 'CUSTOMER', 'Cliente Ana', 'ana', 'ana456', 'ana@email.com', NULL, 'Rua dos Abacaxis, 600', '11444444444');

-- Clientes
INSERT INTO customer (id, idbusiness, iduser, factorCustomer, priceTable) VALUES
  (1, 2, 4, 1.1, 'TabelaA'),
  (2, 2, 6, 1.2, 'TabelaB');

-- Entregas
INSERT INTO delivery (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additionalCost, cost, received, completed, datatime) VALUES
  (1, 2, 1, 1, 'Av. Paulista, 1000', 'Rua Augusta, 500', 'João Silva', 'Entrega urgente', '10 caixas', '50kg', '15', 10.0, 100.0, TRUE, FALSE, CURRENT_TIMESTAMP),
  (2, 2, 2, 2, 'Av. Faria Lima, 200', 'Rua Oscar Freire, 300', 'Maria Oliveira', 'Entrega normal', '5 caixas', '20kg', '8', 5.0, 60.0, FALSE, FALSE, CURRENT_TIMESTAMP),
  (3, 2, 1, 2, 'Av. Rebouças, 500', 'Av. Brasil, 300', 'Carlos Santos', 'Entrega especial', '2 caixas', '5kg', '3', 2.0, 30.0, TRUE, TRUE, CURRENT_TIMESTAMP);
```

## Gerenciando Migrações

### Execução Automática
As migrações são executadas automaticamente durante a inicialização da aplicação através da classe `FlywayConfig`, um bean CDI singleton que:

1. É inicializado durante o startup da aplicação
2. Verifica se o Flyway está habilitado nas configurações
3. Conecta-se ao banco de dados usando o DataSource injetado
4. Executa as migrações pendentes

### Execução Manual
O projeto também suporta comandos Maven para executar operações do Flyway manualmente:

```bash
# Exibir informações sobre o estado das migrações
mvn flyway:info

# Executar migrações pendentes
mvn flyway:migrate

# Limpar o banco de dados (excluir todas as tabelas)
mvn flyway:clean
```

Para facilitar estas operações, o projeto inclui um script PowerShell `flyway-manage.ps1` que oferece um menu interativo para gerenciar migrações.

## Observações
- Os dados podem ser ajustados adicionando novos scripts de migração com versões superiores (V3, V4, etc.)
- Para evitar conflitos em testes automatizados, utilize IDs altos nos testes unitários.
- Consulte as regras de negócio em [REGRAS_DE_NEGOCIO.md](REGRAS_DE_NEGOCIO.md) para entender os vínculos entre entidades e restrições de acesso.
- Para mais detalhes sobre migrações, consulte [MIGRATIONS.md](MIGRATIONS.md).
- Os dados iniciais simulam cenários completos para validação da camada REST, serviços e regras de negócio.
