-- ============================================================
-- EXPORTAÇÃO COMPLETA DO BANCO CSONLINE HSQLDB
-- ============================================================
-- Data de exportação: 2025-08-07 20:17:50
-- Database: CSOnline HSQLDB
-- Gerado automaticamente pelo script export-database.ps1
-- ============================================================

-- ============================================================
-- TABELA: users (Usuários do sistema)
-- Registros: 8
-- ============================================================
-- Registro: users
-- address : 'Av. Paulista, 200'
-- email : 'empresa@cso.com'
-- email2 : NULL
-- id : 2
-- login : 'empresa'
-- mobile : '11888888888'
-- name : 'Empresa X'
-- password : 'empresa123'
-- role : 'BUSINESS'

-- Registro: users
-- address : 'Av. Brasil, 700'
-- email : 'empresay@cso.com'
-- email2 : 'contato@empresay.com'
-- id : 7
-- login : 'empresay'
-- mobile : '11333333333'
-- name : 'Empresa Y'
-- password : 'empresay789'
-- role : 'BUSINESS'

-- Registro: users
-- address : 'Rua das Flores, 300'
-- email : 'joao@cso.com'
-- email2 : NULL
-- id : 3
-- login : 'joao'
-- mobile : '11777777777'
-- name : 'Entregador JoÃ£o'
-- password : 'joao123'
-- role : 'COURIER'

-- Registro: users
-- address : 'Rua dos Pinheiros, 800'
-- email : 'lucas@cso.com'
-- email2 : NULL
-- id : 8
-- login : 'lucas'
-- mobile : '11222222222'
-- name : 'Entregador Lucas'
-- password : 'lucas789'
-- role : 'COURIER'

-- Registro: users
-- address : 'Rua das Laranjeiras, 500'
-- email : 'pedro@cso.com'
-- email2 : NULL
-- id : 5
-- login : 'pedro'
-- mobile : '11555555555'
-- name : 'Entregador Pedro'
-- password : 'pedro456'
-- role : 'COURIER'

-- Registro: users
-- address : 'Rua dos Abacaxis, 600'
-- email : 'ana@email.com'
-- email2 : NULL
-- id : 6
-- login : 'ana'
-- mobile : '11444444444'
-- name : 'Cliente Ana'
-- password : 'ana456'
-- role : 'CUSTOMER'

-- Registro: users
-- address : 'Rua dos LimÃµes, 400'
-- email : 'carlos@email.com'
-- email2 : NULL
-- id : 4
-- login : 'carlos'
-- mobile : '11666666666'
-- name : 'Cliente Carlos'
-- password : 'carlos123'
-- role : 'CUSTOMER'

-- Registro: users
-- address : 'Av. Santo Amaro, 900'
-- email : 'maria@email.com'
-- email2 : 'maria.contato@email.com'
-- id : 9
-- login : 'maria'
-- mobile : '11111111111'
-- name : 'Cliente Maria'
-- password : 'maria789'
-- role : 'CUSTOMER'


-- ============================================================
-- TABELA: customers (Clientes)
-- Registros: 2
-- ============================================================
-- Registro: customers
-- businessId : 2
-- factorCustomer : 1.2
-- id : 2
-- priceTable : 'TabelaB'
-- user : @{id=6; role=CUSTOMER; name=Cliente Ana; login=ana; password=ana456; email=ana@email.com; email2=; address=Rua dos Abacaxis, 600; mobile=11444444444}
-- userId : 6

-- Registro: customers
-- businessId : 7
-- factorCustomer : 1.3
-- id : 3
-- priceTable : 'TabelaC'
-- user : @{id=9; role=CUSTOMER; name=Cliente Maria; login=maria; password=maria789; email=maria@email.com; email2=maria.contato@email.com; address=Av. Santo Amaro, 900; mobile=11111111111}
-- userId : 9


-- ============================================================
-- TABELA: couriers (Entregadores)
-- Registros: 2
-- ============================================================
-- Registro: couriers
-- businessId : 2
-- factorCourier : 1.3
-- id : 2
-- userId : 5

-- Registro: couriers
-- businessId : 7
-- factorCourier : 1.4
-- id : 3
-- userId : 8


-- ============================================================
-- TABELA: sms (Mensagens SMS)
-- Registros: 2
-- ============================================================
-- Registro: sms
-- datetime : '2025-08-03 21:23:20.57651'
-- deliveryId : 2
-- id : 3
-- message : 'Saiu para entrega'
-- mobileFrom : '11555555555'
-- mobileTo : '11444444444'
-- piece : 1
-- type : 'INFO'

-- Registro: sms
-- datetime : '2025-08-03 21:23:20.596997'
-- deliveryId : 4
-- id : 5
-- message : 'Entrega frÃ¡gil a caminho'
-- mobileFrom : '11222222222'
-- mobileTo : '11111111111'
-- piece : 1
-- type : 'INFO'


-- ============================================================
-- TABELA: deliveries (Entregas)
-- Registros: 2
-- ============================================================
-- Registro: deliveries
-- additionalCost : 5
-- businessId : 2
-- completed : FALSE
-- contact : 'Maria Oliveira'
-- cost : 60
-- courierId : 2
-- customerId : 2
-- description : 'Entrega normal'
-- destination : 'Rua Oscar Freire, 300'
-- id : 2
-- km : '8'
-- received : FALSE
-- start : 'Av. Faria Lima, 200'
-- volume : '5 caixas'
-- weight : '20kg'

-- Registro: deliveries
-- additionalCost : 0
-- businessId : 7
-- completed : FALSE
-- contact : 'Ana Pereira'
-- cost : 45
-- courierId : 3
-- customerId : 3
-- description : 'Entrega frÃ¡gil'
-- destination : 'Av. AngÃ©lica, 400'
-- id : 4
-- km : '5'
-- received : TRUE
-- start : 'Rua ConsolaÃ§Ã£o, 800'
-- volume : '1 caixa'
-- weight : '2kg'


-- ============================================================
-- TABELA: teams (Equipes)
-- Registros: 2
-- ============================================================
-- Registro: teams
-- businessId : 2
-- courierId : 2
-- factorCourier : 1.3
-- id : 2

-- Registro: teams
-- businessId : 7
-- courierId : 3
-- factorCourier : 1.4
-- id : 3


-- ============================================================
-- RESUMO DA EXPORTAÇÃO
-- ============================================================-- users : 8 registros
-- customers : 2 registros
-- couriers : 2 registros
-- sms : 2 registros
-- deliveries : 2 registros
-- teams : 2 registros
-- TOTAL GERAL: 18 registros
-- ============================================================

