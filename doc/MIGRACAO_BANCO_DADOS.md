# Migrações e Gerenciamento de Dados com Flyway

Este documento unificado descreve como o sistema CSOnline utiliza o Flyway para gerenciar migrações de banco de dados, incluindo a estrutura do esquema e os dados iniciais, executando sobre uma instância HSQLDB containerizada com Docker.

## Visão Geral

A partir de agosto de 2025, o CSOnline adotou o Flyway como solução para gerenciamento de migrações de banco de dados, **executando sobre uma instância HSQLDB containerizada com Docker**. Esta abordagem substitui o uso do arquivo `import.sql` tradicional, oferecendo:

1. **Controle de versão do esquema do banco de dados** - Similar ao controle de versão de código
2. **Atualizações incrementais consistentes** - Sem necessidade de recriar todo o banco
3. **Execução automática de migrações** - Durante a inicialização da aplicação
4. **Histórico completo de alterações** - Rastreabilidade de todas as mudanças
5. **Consistência entre ambientes** - Desenvolvimento, teste e produção seguem o mesmo processo
6. **Banco de dados containerizado** - HSQLDB executando em container Docker para isolamento e facilidade de setup

## Configuração do Banco de Dados com Docker

### Arquivo docker-compose.yml

O projeto utiliza Docker Compose para executar o banco de dados HSQLDB:

```yaml
version: '3.8'

services:
  hsqldb:
    image: datagrip/hsqldb:latest
    container_name: csonline-hsqldb
    ports:
      - "9001:9001"  # Porta padrão do HSQLDB
    volumes:
      - ./hsqldb-data:/data  # Persistência dos dados
    restart: unless-stopped
```

### Configurações de Conexão

As configurações de conexão estão definidas no arquivo `application.properties`:

```properties
# Configurações da conexão com o banco de dados
db.url=jdbc:hsqldb:hsql://localhost:9001/test
db.username=sa
db.password=
```

### Como Iniciar o Banco de Dados

1. **Iniciar o container HSQLDB:**
   ```powershell
   docker-compose up -d hsqldb
   ```

2. **Verificar se o container está rodando:**
   ```powershell
   docker ps
   ```

3. **Parar o container (quando necessário):**
   ```powershell
   docker-compose down
   ```

4. **Ver logs do container:**
   ```powershell
   docker logs csonline-hsqldb
   ```

### Persistência de Dados

Os dados do banco são persistidos no diretório `hsqldb-data/` na raiz do projeto, garantindo que as informações não sejam perdidas quando o container for reiniciado.

## Estrutura das Migrações

Os scripts de migração são armazenados no diretório `src/main/resources/db/migration` e seguem a convenção de nomenclatura:

```
V<versão>__<descrição>.sql
```

Por exemplo:
- `V1__Create_tables.sql`: Primeira migração que cria as tabelas
- `V2__Insert_initial_data.sql`: Segunda migração que insere dados iniciais

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

## Como as Migrações são Executadas

### Execução Automática
As migrações são executadas automaticamente durante a inicialização da aplicação através da classe `FlywayConfig`. Esta classe é um bean CDI singleton que:

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

# Validar os scripts de migração
mvn flyway:validate

# Reparar a tabela de histórico do Flyway
mvn flyway:repair
```

Para facilitar estas operações, o projeto inclui um script PowerShell `flyway-manage.ps1` que oferece um menu interativo para gerenciar migrações.

## Configuração

### Configurações do Flyway

As configurações do Flyway estão definidas no arquivo `application.properties`:

```properties
# Configurações do Flyway
flyway.enabled=true
flyway.locations=classpath:db/migration
flyway.baseline-on-migrate=true
flyway.baseline-version=0
flyway.validate-on-migrate=true

# Configurações da conexão com o banco de dados Docker
db.url=jdbc:hsqldb:hsql://localhost:9001/test
db.username=sa
db.password=
```

### Pré-requisitos

Antes de executar a aplicação, certifique-se de que:

1. **Docker e Docker Compose estão instalados**
2. **O container HSQLDB está rodando:**
   ```powershell
   docker-compose up -d hsqldb
   ```
3. **A porta 9001 está disponível** para conexão com o banco de dados

### Fluxo de Inicialização

1. Inicie o container HSQLDB com Docker Compose
2. Execute a aplicação (WildFly ou testes)
3. O Flyway conecta automaticamente ao banco containerizado
4. As migrações são executadas na inicialização da aplicação

## Boas Práticas para Migrações

1. **Nunca modifique um script de migração já aplicado** - Crie uma nova migração para fazer alterações
2. **Use transações nos scripts** - Para garantir consistência
3. **Mantenha os scripts idempotentes quando possível** - Use condicionais como `IF NOT EXISTS`
4. **Documente mudanças significativas** - Use comentários nos scripts
5. **Teste as migrações antes de implantá-las** - Use o comando `flyway:validate`

## Solução de Problemas

### Problemas com Docker e Banco de Dados

#### Container não inicia ou não conecta
1. Verifique se o Docker está rodando:
   ```powershell
   docker version
   ```

2. Verifique se a porta 9001 está livre:
   ```powershell
   netstat -an | findstr 9001
   ```

3. Reinicie o container:
   ```powershell
   docker-compose down
   docker-compose up -d hsqldb
   ```

4. Verifique os logs do container:
   ```powershell
   docker logs csonline-hsqldb
   ```

#### Limpar dados do banco (reset completo)
```powershell
# Parar o container
docker-compose down

# Remover dados persistidos
Remove-Item -Recurse -Force hsqldb-data

# Reiniciar o container
docker-compose up -d hsqldb
```

#### Acessar o banco de dados diretamente
```powershell
# Conectar ao container
docker exec -it csonline-hsqldb bash

# Ou usar um cliente HSQLDB externo conectando em:
# URL: jdbc:hsqldb:hsql://localhost:9001/test
# User: sa
# Password: (vazio)
```

### Erro "Migration checksum mismatch"

Este erro ocorre quando um script de migração já aplicado foi modificado. Soluções:

1. Se estiver em ambiente de desenvolvimento, use `mvn flyway:repair` para corrigir o checksum
2. Em produção, crie uma nova migração com as alterações desejadas

### Erro ao executar migrações

Verifique os logs da aplicação. Os erros mais comuns são:

1. **Container HSQLDB não está rodando** - Verifique com `docker ps`
2. **Sintaxe SQL inválida**
3. **Dependências entre tabelas não respeitadas** (chaves estrangeiras)
4. **Violações de restrições de unicidade**
5. **Problema de conectividade** - Verifique se a porta 9001 está acessível

## Exemplos de Cenários Simulados

Os dados iniciais inseridos pelo Flyway contemplam vários cenários de uso:

- Usuário ADMIN para administração geral do sistema.
- Empresa BUSINESS vinculada a múltiplos clientes e couriers, simulando ambiente corporativo.
- Dois clientes e dois couriers para testar entregas, autorizações e interações entre perfis.
- Entregas com diferentes status (pendente, concluída, recebida) e dados variados para validar regras de negócio.
- Tabela de preços diferenciada para clientes e tipos de veículos, permitindo testes de cálculo de custo.
- SMS simulando troca de mensagens entre courier e customer, útil para rastreamento e histórico.

Esses dados permitem validar cenários de autenticação, autorização, rastreamento, cálculo de preços e fluxo completo de entrega.

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

## Documentação dos Endpoints REST

Todos os dados simulados podem ser consultados e validados via endpoints REST documentados automaticamente pelo Swagger/OpenAPI.
- A especificação OpenAPI está disponível em `/api/openapi.json`.
- Para visualizar, utilize Swagger UI ou [Swagger Editor Online](https://editor.swagger.io/).

## Dicas para Testes

### Preparação do Ambiente
1. **Sempre inicie o container Docker primeiro:**
   ```powershell
   docker-compose up -d hsqldb
   ```

2. **Verifique a conectividade antes de executar testes:**
   ```powershell
   # Use os scripts de teste automatizados
   .\run-tests.ps1 -HealthCheck
   ```

### Estratégias de Teste
- Utilize os dados das migrações do Flyway para validar regras de negócio, persistência e integração entre camadas.
- Teste cenários de atualização, deleção e consulta por diferentes atributos e perfis de usuário.
- Simule operações de rastreamento e autorização usando os vínculos entre entidades (courier, customer, delivery).
- Para testes automatizados, utilize IDs altos para evitar conflitos com os dados iniciais inseridos pelas migrações.
- Expanda os scripts de migração conforme necessidade, adicionando novos scripts com versões superiores (V3, V4, etc.).
- Os dados simulam o fluxo completo do sistema, incluindo autenticação, autorização, entrega, rastreamento e histórico de mensagens.

### Reset do Ambiente de Teste
```powershell
# Script completo para reset do ambiente
docker-compose down
Remove-Item -Recurse -Force hsqldb-data -ErrorAction SilentlyContinue
docker-compose up -d hsqldb

# Aguardar o container inicializar (alguns segundos)
Start-Sleep -Seconds 5

# Executar testes para verificar se as migrações foram aplicadas
.\run-tests.ps1 -HealthCheck
```

## Evolução do Projeto

A evolução do CSOnline em termos de gerenciamento de dados passou por várias fases:

1. **Fase Inicial**: Uso do arquivo `import.sql` tradicional
2. **Migração para Flyway** (Agosto 2025): Implementação de migrações versionadas
3. **Containerização com Docker** (Agosto 2025): HSQLDB executando em container Docker

### Benefícios da Containerização

A migração para Docker trouxe benefícios adicionais:

- **Isolamento**: O banco de dados roda em ambiente isolado
- **Portabilidade**: Funciona igual em qualquer máquina com Docker
- **Facilidade de setup**: Um comando para ter o banco funcionando
- **Consistência**: Mesma versão do HSQLDB em todos os ambientes
- **Reset fácil**: Possibilidade de limpar dados facilmente
- **Desenvolvimento multiplataforma**: Windows, Linux e macOS

### Comandos Úteis para Desenvolvimento

```powershell
# Iniciar o ambiente completo
docker-compose up -d

# Ver status dos containers
docker ps

# Parar tudo
docker-compose down

# Reset completo (limpar dados)
docker-compose down && Remove-Item -Recurse -Force hsqldb-data && docker-compose up -d
```

Esta evolução representa um avanço significativo na maturidade técnica do projeto CSOnline, oferecendo maior robustez, rastreabilidade, facilidade de manutenção e portabilidade através da containerização.

Para mais detalhes sobre a migração do sistema antigo para o Flyway, consulte [MIGRACAO_IMPORT_SQL.md](MIGRACAO_IMPORT_SQL.md).
