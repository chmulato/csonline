# Documento Unificado de Testes - CSOnline

Data de atualização: 08/08/2025
Branch: `fix/backend-unit-tests`

---
## 1. Visão Geral
Este documento consolida todas as informações de testes do projeto (unitários, repositórios e endpoints) em um único local para facilitar acompanhamento de qualidade, evolução e próximos passos.

---
## 2. Ambiente e Infraestrutura
- Servidor de aplicação: WildFly 31.0.1.Final
- Banco de dados (testes): HSQLDB em memória (TestJPAUtil)
- Migrações: Flyway com reparo programático (flyway.repair + migrate)
- JPA Provider: EclipseLink
- Test Framework: JUnit 5
- Camada REST: Jersey
- Base de testes:
  - `BaseControllerJerseyTest`: ativa modo de teste e limpa DB antes de cada teste de controller.
  - `BaseServiceTest`: ativa modo de teste e limpa DB antes de cada teste de service.
- Modo Teste: Controlado por `System.setProperty("test.mode", "true")` com fallback reflexivo em `JPAUtil` para `TestJPAUtil`.

---
## 3. Status Global
| Categoria                | Total Passando | Observações Principais |
|--------------------------|----------------|------------------------|
| Testes Unitários/Serviço | 24             | 100% verdes na sessão atual |
| Testes de Endpoints (manuais/scripts) | 8 / 10        | 2 pendentes (ID individuais) |
| Repositórios ajustados   | 2              | 5 ainda usam `JPAUtil` direto |

---
## 4. Detalhamento Testes Unitários (24)
### 4.1 Infraestrutura / Base
- JpaInfrastructureTest (3/3)
- SimpleUserServiceTest (2/2)
- TeamServiceTestFixed (4/4)

### 4.2 Serviços de Negócio
- UserServiceTest (7/7)
- PriceServiceTest (1/1)
- SMSServiceTest (3/3)
- TeamServiceTest (4/4)

### 4.3 Repositórios (Completos)
- TeamRepositoryTest (3/3)
- UserRepositoryTest (1/1)

### 4.4 Repositórios Pendentes (erro JNDI / uso de JPAUtil)
- CourierRepositoryTest
- CustomerRepositoryTest
- DeliveryRepositoryTest
- PriceRepositoryTest
- SMSRepositoryTest

Ação requerida: migrar para `TestJPAUtil.getEntityManager()` ou aplicar mesmo padrão reflexivo já adotado.

---
## 5. Técnicas Aplicadas e Correções
### 5.1 Cascade Persistence
Problema: `CascadeType.PERSIST` tentando repersistir entidades já gerenciadas.
Solução: Padrão merge antes do persist em blocos transacionais controlados.

### 5.2 Integridade Referencial
Interceptação de mensagens de exceção (violação de FK) para lançar mensagem de domínio: "Não foi possível deletar. Existem registros vinculados." em serviços testáveis (`TestableUserService`, `TestableSMSService`).

### 5.3 Implementação de Métodos Faltantes
- `TestableSMSService.getDeliverySMSHistory`
- `TestableSMSService.sendDeliverySMS`

### 5.4 Consolidação de Setup
Removida duplicação de limpeza de banco em cada teste individual; centralizado nos base classes.

### 5.5 Padrão Dual Mode
`JPAUtil` detecta `test.mode` e invoca `TestJPAUtil` via reflexão, evitando acoplamento direto em código de produção.

---
## 6. Testes de Endpoints (Estado Atual)
Resumo: 8 de 10 endpoints de listagem e funcionalidades principais operacionais; problemas em consultas individuais.

### 6.1 Endpoints Funcionando (Lista)
- `/api/users` (GET) – lista completa
- `/api/couriers` (GET) – lista com IDs expostos via DTO/JsonProperty
- `/api/customers` (GET)
- `/api/team` (GET)
- `/api/sms` (GET)
- `/api/deliveries` (GET) – corrigido com DeliveryDTO (problemas de LocalDateTime eliminados)
- (Inclui massa representativa de `business`, `couriers`, `customers` e `deliveries`)

### 6.2 Problemas Identificados
1. `/api/team/{id}` retorna 404 para IDs existentes.
2. Endpoints individuais (`/api/users/{id}`, `/api/couriers/{id}`, `/api/customers/{id}`) retornam 200 com corpo vazio.
3. Apenas 2 de 4 deliveries listados (IDs filtrados) – investigar filtros / mapeamentos / consultas.

### 6.3 Causas Prováveis
- Métodos `findById` não convertendo corretamente a entidade (lazy/fechamento prematuro do EntityManager).
- Anotações de serialização ausentes nos retornos individuais.
- Possível lógica de exclusão lógica ou filtro JPQL inadvertido.

### 6.4 Ações Recomendadas
| Item | Ação | Prioridade |
|------|------|-----------|
| Team /{id} 404 | Validar rota e método no controller + service | Alta |
| User /{id} vazio | Inspecionar retorno null vs Optional | Alta |
| Courier /{id} vazio | Mapear query e lazy loads | Alta |
| Customer /{id} vazio | Ajustar fetch ou DTO | Alta |
| Deliveries filtradas | Verificar JPQL, cláusulas WHERE, constraints | Média |

---
## 7. Massa de Dados (Snapshot)
Resumo das tabelas principais usadas nos testes de endpoint (HSQLDB de desenvolvimento):

### app_user
IDs: ADMIN(1), BUSINESS(2,7), COURIER(3,5,8), CUSTOMER(4,6,9).

### courier
Relação business → courier via IDBUSINESS; fator de variação (FACTORCOURIER) presente.

### customer
Inclui fator (FACTORCUSTOMER) e tabela de preço (PRICETABLE).

### delivery
4 registros (apenas 2 retornados atualmente pelo endpoint) – investigar.

---
## 8. Fluxo de Execução de Testes
1. `mvn test` dispara testes unitários/repositório.
2. Base classes limpam DB a cada teste, evitando dependência de ordem.
3. Endpoint tests (manuais) podem ser validados via scripts PowerShell:
   - `scr/tests/test-all-endpoints.ps1`
   - Scripts específicos (`test-users.ps1`, `test-couriers.ps1`, etc.)

---
## 9. Checklist Consolidado
| Item | Status |
|------|--------|
| Infraestrutura TestJPAUtil | Concluído |
| Base de testes (service/controller) | Concluído |
| Tratamento cascade persistence | Concluído |
| Tratamento integridade referencial | Concluído |
| Implementação métodos SMS | Concluído |
| 24 testes verdes | Concluído |
| Repositórios migrar para TestJPAUtil | Pendente |
| Endpoints individuais corrigidos | Pendente |
| Filtro deliveries investigado | Pendente |
| Ampliação de cobertura de controllers | Pendente |

---
## 10. Próximos Passos Priorizados
1. Migrar 5 repositórios restantes para padrão de teste (evitar JNDI/lookup real).
2. Corrigir endpoints individuais (ajustar findById + serialização).
3. Revisar consultas Delivery e normalizar resultados.
4. Adicionar testes de integração para endpoints (JUnit + JerseyTest + dataset controlado).
5. Introduzir DTOs consistentes (padronizar saída e evitar loops Jackson).
6. Pipeline CI: executar testes + relatório (Jacoco / surefire) e health-check endpoints mockados.

---
## 11. Riscos & Mitigações
| Risco | Impacto | Mitigação |
|-------|---------|-----------|
| Dependência futura de ordem de teste | Flakiness | Limpeza central já mitiga |
| Divergência entre DTOs e entidades | Inconsistência de API | Definir contrato no pacote `dto` |
| Crescimento de massa de dados manual | Manutenção difícil | Fábricas de dados reutilizáveis / builders |
| Serialização LocalDateTime futura | Erros de parsing | Converter para ISO 8601 via Jackson Module ou DTO |

---
## 12. Referências Técnicas
- `JPAUtil` / `TestJPAUtil` (fallback reflexivo)
- `BaseControllerJerseyTest` / `BaseServiceTest`
- `TestDataFactory` / `TestDatabaseUtil`
- Testables: `TestableUserService`, `TestableSMSService`, etc.

---
## 13. Apêndice: Exemplo de Padrão Merge
```java
EntityManager em = TestJPAUtil.getEntityManager();
try {
    em.getTransaction().begin();
    business = em.merge(business);
    customerUser = em.merge(customerUser);
    customer.setBusiness(business);
    customer.setUser(customerUser);
    em.persist(customer);
    em.getTransaction().commit();
} finally {
    em.close();
}
```

---
## 14. Observações Finais
A base de testes atingiu estabilidade para evolução incremental. Focar agora em:
- Completar camada de repositórios
- Elevar cobertura de controllers e endpoints individuais
- Formalizar padrão de DTO e serialização

Documento único gerado para substituir visões fragmentadas anteriores.
