package br.com.mulato.cso.dry;

import br.com.mulato.cso.exception.DAOException;

public interface InterfaceSQL
{
	
	// ########### TRANSACTION PARAMETERS #################### //
	
	public final boolean TRANSACTION_ENABLE = true;
	
	public final boolean TRANSACTION_DISABLE = false;
	
	public void setTransaction_active(boolean  enable) throws DAOException;
	
	// ########### ADMINISTRATOR PROFILE'S QUERIES ########### //
	// PERFIL DO ADMINISTRADOR
	// selecionar login com a senha;
	// USER
	// selecionar todos os usuários;
	// selecionar um usuário pelo id; (visualizar)
	// BUSINESS
	// selecionar todos os negócios;
	// selecionar um negócio pelo id;
	// atualizar um negócio pelo id;
	// verificar se têm entrega, se têm cliente e se têm motoboy, se OK=0 deletar negócio.
	// Inserir um negócio;
	// obs.: o perfil administrador não inclui cliente, motoboy e nem entrega. Também não pode visualizar
	// estas abas

	public static final String SELECT_USER_BY_LOGIN_AND_PASSWORD = "select u.* from user u where u.login=? and u.password=?";

	public static final String SELECT_USER_BY_ID = "select u.* from user u where u.id=?";

	public static final String SELECT_USER_BY_LOGIN = "select u.* from user u where u.login=?";

	public static final String SELECT_ALL_USER = "select u.* from user u order by u.role, u.name";

	// SELECT ALL BUSINESS
	public static final String SELECT_ALL_BUSINESS = "select u.* from user u where u.role='BUSINESS' order by u.name";

	// SELECT ALL COURIER
	public static final String SELECT_ALL_COURIER = "select u.*, t.* from user as u inner join team as t on u.id=t.idcourier";

	public static final String SELECT_BUSINESS_BY_ID = "select u.* from user u where u.id=?";

	public static final String SELECT_DELIVERY_BUSINESS_BY_IDDELIVERY = "select u.*, d.* from user as u" +
	    " inner join delivery as d on u.id=d.idbusiness where d.id=?";

	public static final String SELECT_ALL_DELIVERY_NOT_COMPLETED = "select * from delivery where (received='1' and completed='0') order by datatime";

	public static final String UPDATE_BUSINESS = "update user set" +
	    " name=?, email=?, email2=?, address=?, mobile=? where (role='BUSINESS' and id=?)";

	public static final String COUNT_DELIVERY_BUSINESS = "select count(id) from delivery where idbusiness=?";

	public static final String COUNT_CUSTOMER_BUSINESS = "select count(id) from customer where idbusiness=?";

	public static final String COUNT_COURIER_BUSINESS = "select count(id) from team where idbusiness=?";

	public static final String DELETE_BUSINESS_BY_ID = "delete from user where (role='BUSINESS' and id=?)";

	public static final String INSERT_BUSINESS = "insert into user" + " (id, role, name, login, password, email, email2, address, mobile)" +
	    "  values (?,?,?,?,?,?,?,?,?)";

	public static final String UPDATE_LOGIN_PASSWORD = "update user set password=? where login=?";

	// ########### BUSINESS PROFILE'S QUERIES ########### //
	// PERFIL DO NEGÃ“CIO
	// selecionar login com a senha;
	// BUSINESS
	// selecionar um negócio pelo id;
	// atualizar um negócio pelo id;
	// obs.: não é permitido remover do negócio.
	// CUSTOMER
	// incluir um cliente do negócio;
	// atualizar um cliente pelo id;
	// selecionar todos os clientes do negócio;
	// selecionar o cliente do negócio pelo id;
	// verificar se têm entrega, se OK=0 deletar o cliente.
	// COURIER
	// incluir um entregador do negócio;
	// selecionar todos os entregadores do negócio;
	// atualizar um entregador do negócio pelo id;
	// selecionar o entregador do negócio pelo id;
	// verificar se o entregador têm entregas realizadas, se OK=0 deletar o entregador do negócio.
	// DELIVERY
	// incluir uma entrega do negócio selecionando um entregador;
	// selecionar todos as entregas do negócio;
	// selecionar uma entrega do negócio pelo id;
	// atualizar a entrega do negócio pelo id;
	// se a entrega não estiver completa é possí­vel deleta-la.

	// BUSINESS MANAGEMENT - IDNUSINESS IS KNOW
	public static final String GET_LAST_ID_ON_USER_TABLE = "select max(id) from user";

	// INSERT A CUSTOMER BUSINESS
	public static final String INSERT_CUSTOMER_BUSINESS_01 = "insert into user" +
	    " (id, role, name, login, password, email, email2, address, mobile)" + "  values (?,?,?,?,?,?,?,?,?)";

	public static final String INSERT_CUSTOMER_BUSINESS_02 = "insert into customer" +
	    " (id, idbusiness, idcustomer, factor_customer, price_table) values (?,?,?,?,?)";

	// UPDATE A CUSTOMER BUSINESS
	// ID=IDCUSTOMER
	public static final String UPDATE_CUSTOMER_01 = "update user set" +
	    " name=?, email=?, email2=?, address=?, mobile=? where (role='CUSTOMER' and id=?)";

	// IDBUSINESS=IDDBUSINESS AND IDCUSTOMER=IDCUSTOMER
	public static final String UPDATE_CUSTOMER_02 = "update customer set" + " factor_customer=?, price_table=? where (idbusiness=? and idcustomer=?)";

	// SELECT ALL CUSTOMER BUSINESS
	public static final String SELECT_ALL_CUSTOMER_BUSINESS = "select u.*, cu.*" + " from user as u inner join customer as cu" +
	    " on u.id=cu.idcustomer" + " where cu.idbusiness=?"; // ID=IDBUSINESS

	// SELECT CUSTOMER BUSINESS BY IDCUSTOMER
	public static final String SELECT_CUSTOMER_BUSINESS_BY_IDCUSTOMER = "select u.*, cu.*" + " from user as u inner join customeras cu" +
	    " on u.id=cu.idbusiness" + " where cu.idcustomer=?"; // ID=IDCUSTOMER

	// DELETE CUSTOMER BUSINESS
	// CHECK: IS THERE A DELIVERY?
	// ID=IDCUSTOMER
	public static final String COUNT_DELIVERY_CUSTOMER = "select count(id) from delivery where idcustomer=?";

	// AND, DELETE CUSTOMER BUSINESS USING 02 STEPS
	// ID=IDCUSTOMER
	public static final String DELETE_CUSTOMER_BY_ID_01 = "delete from customer where idcustomer=?";

	// ID=IDCUSTOMER
	public static final String DELETE_CUSTOMER_BY_ID_02 = "delete from user where (role='CUSTOMER' and id=?)";

	// COURIER MANAGEMENT - IBNUSINESS IS KNOW
	// INSERT A COURIER BUSINESS
	public static final String INSERT_COURIER_01 = "insert into user" + " (id, role, name, login, password, email, email2, address, mobile)" +
	    "  values (?,?,?,?,?,?,?,?,?)";

	public static final String INSERT_COURIER_02 = "insert into team" + " (id, idbusiness, idcourier, factor_courier) values (?,?,?,?)";

	// UPDATE A COURIER BUSINESS
	// ID=IDCOURIER
	public static final String UPDATE_COURIER_01 = "update user set" +
	    " name=?, email=?, email2=?, address=?, mobile=? where (role='COURIER' and id=?)";

	// IDBUSINESS=IDDBUSINESS AND IDCOURIER=IDCCOURIER
	public static final String UPDATE_COURIER_02 = "update team set factor_courier=? where (idbusiness=? and idcourier=?)";

	public static final String UPDATE_COURIER_03 = "update user set" +
	    " name=?, password=?, email=?, email2=?, address=?, mobile=? where (role='COURIER' and id=?)";

	// SELECT ALL COURIER BUSINESS
	// IDBUSINESS=IDDBUSINESS
	public static final String SELECT_ALL_COURIER_BUSINESS = "select u.*, t.*" + " from user as u inner join team as t" +
	    " on	u.id=t.idcourier where t.idbusiness=?";

	// SELECT COURIER BUSINESS BY IDCOURIER
	// IDCOURIER=IDCOURIER
	public static final String SELECT_COURIER_BUSINESS_BY_IDCOURIER = "select u.*, t.*" + " from user as u inner join team as t" +
	    " on u.id=t.idbusiness where t.idcourier=?";

	// DELETE COURIER BUSINESS
	// CHECK: IS THERE A DELIVERY?
	public static final String COUNT_DELIVERY_COURIER_BY_IDCOURIER = "select count(id) from delivery where idcourier=?";

	// AND, DELETE COURIER BUSINESS USING 02 STEPS
	public static final String DELETE_COURIER_BY_ID_01 = "delete from team where idcourier=?";

	// ID=IDCOURIER
	public static final String DELETE_COURIER_BY_ID_02 = "delete from user where (role='COURIER' and id=?)";

	// BUSINESS MANAGEMENT - GET LAST ID CUSTOMER TABLE
	public static final String GET_LAST_ID_ON_CUSTOMER_TABLE = "select max(id) from customer";

	// BUSINESS MANAGEMENT - GET LAST ID TEAM TABLE
	public static final String GET_LAST_ID_ON_TEAM_TABLE = "select max(id) from team";

	public static final String INSERT_DELIVERY = "insert into delivery" +
	    " (id, idbusiness, idcustomer, idcourier, start, destination, contact, description, volume, weight, km, additional_cost, cost, received, completed)" +
	    "  values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	// SELECT ALL DELIVERY BUSINESS COMPLETED
	public static final String SELECT_ALL_DELIVERY_BUSINESS_COMPLETED = "select * from delivery where (idbusiness=? AND completed='1')";

	// SELECT ALL DELIVERY BUSINESS NOT COMPLETED
	public static final String SELECT_ALL_DELIVERY_BUSINESS_NOT_COMPLETED = "select * from delivery where (idbusiness=? and completed='0')";

	// SELECT DELIVERY BY ID
	public static final String SELECT_DELIVERY_BY_ID = "select * from delivery where id=?";

	// UPDATE DELIVERY BY ID
	// ID=ID DELIVERY
	public static final String UPDATE_DELIVERY_BY_ID = "update delivery set" +
	    " idbusiness=?, idcustomer=?, idcourier=?, start=?, destination=?, contact=?, description=?," +
	    " volume=?, weight=?, km=?, additional_cost=?, cost=?, received=?, completed=? where id=?";

	// AND, DELETE DELIVERY USING 02 STEPS
	// SELECT DELIVERY BY ID
	public static final String SELECT_DELIVERY_NOT_RECEIVED = "select received from delivery where id=?";

	// ID=ID DELIVERY
	public static final String DELETE_DELIVERY_BY_ID = "delete from delivery where id=?";

	// ########### CUSTOMER PROFILE'S QUERIES ########### //
	// PERFIL DO CLIENTE
	// selecionar login com a senha;
	// CUSTOMER
	// selecionar um cliente pelo id;
	// atualizar um cliente pelo id;
	// obs.: não é permitido remover o cliente;
	// DELIVERY
	// incluir uma entrega do negócio;
	// selecionar todas as entregas do cliente não completas;
	// selecionar todas as entregas do cliente completas;
	// atualizar as entregas do cliente não recebidas;
	// deletar as entregas do cliente não recebidas.

	// CUSTOMER MANAGEMENT - IBCUSTOMER IS KNOW
	// SELECT CUSTOMER BY IDCUSTOMER
	// ID=IDCUSTOMER
	public static final String SELECT_CUSTOMER_BY_ID = "select u.*, cu.* from user u inner join customer cu" + " on u.id=cu.idcustomer where u.id=?";

	// CUSTOMER MANAGEMENT - ID IS KNOW
	public static final String GET_LAST_ID_ON_DELIVERY_TABLE = "select max(id) from delivery";

	// SELECT ALL DELIVERY CUSTOMER NOT COMPLETED
	// IDCUSTOMER=IDCUSTOMER
	public static final String SELECT_ALL_DELIVERY_CUSTOMER_NOT_COMPLETED = "select * from delivery where (idcustomer=? and completed='0')";

	// SELECT ALL DELIVERY CUSTOMER COMPLETED
	// IDCUSTOMER=IDCUSTOMER
	public static final String SELECT_ALL_DELIVERY_CUSTOMER_COMPLETED = "select * from delivery where (idcustomer=? and completed='1')";

	// ########### COURIER PROFILE'S QUERIES ########### //
	// PERFIL DO MOTOBOY
	// selecionar login com a senha;
	// COURIER
	// selecionar um motoboy pelo id; (visualizar)
	// DELIVERY
	// selecionar todas as entregas do motoboy não completas;
	// selecionar todas as entregas do motoboy completas;
	// atualizar a entrega do motoboy pelo id e não completas;
	// selecionar a entrega do motoboy pelo id.

	// COURIER MANAGEMENT - IBCOURIER IS KNOW
	// SELECT COURIER BY ID
	// ID=IDCOURIER
	public static final String SELECT_COURIER_BY_ID = "select u.*, t.*" + " from user as u inner join team as t" +
	    " on u.id=t.idcourier where u.id=?";

	// SELECT ALL DELIVERY COURIER NOT COMPLETED
	// IDCOURIER=IDCOURIER
	public static final String SELECT_ALL_DELIVERY_COURIER_NOT_COMPLETED = "select * fromdelivery where (idcourier=? and completed='0')";

	// SELECT ALL DELIVERY COURIER COMPLETED
	// IDCOURIER=IDCOURIER
	public static final String SELECT_ALL_DELIVERY_COURIER_COMPLETED = "select * from delivery where (idcourier=? and completed='1')";

	// INSERT PRICE VALUES
	public static final String INSERT_PRICE = "insert into price" + " (id, idbusiness, table_name, vehicle, local, price) values (?,?,?,?,?,?)";

	// GET LAST ID PRICE TABLE
	public static final String GET_LAST_ID_ON_PRICE_TABLE = "select max(id) from price";

	// UPDATE PRICE VALUES BY ID
	public static final String UPDATE_PRICE_BY_ID = "update price set" + " idbusiness=?, table_name=?, vehicle=?, local=?, price=? where id=?";

	// SELECT PRICE BY ID
	public static final String SELECT_PRICE_BY_ID = "select * from price where id=?";

	// SELECT PRICE BY OTHER PRICE
	public static final String SELECT_PRICE_EQUAL = "select * from price where (idbusiness=? and table_name=? and vehicle=? and local=?)";

	// SELECT PRICE BY OTHER PRICE AND ID
	public static final String SELECT_PRICE_EQUAL_OTHER_ID = "select * from price where (id<>? and idbusiness=? and table_name=? and vehicle=? and local=?)";

	// SELECT PRICE BY IDCUSTOMER
	public static final String SELECT_CUSTOMER_VALUES = "select * from price where table_name=? order by vehicle, local";

	// SELECT PRICE BY IDBUSINESS AND PRICE TABLE
	public static final String SELECT_BUSINESS_VALUES_BY_PRICE_TABLE = "select * from price where (idbusiness=? and table_name=?) order by vehicle, local";

	// SELECT PRICE BY IDBUSINESS
	public static final String SELECT_BUSINESS_VALUES = "select * from price where idbusiness=? order by table_name, vehicle, local";

	// DELETE PRICE BY ID
	public static final String DELETE_PRICE_BY_ID = "delete from price where id=?";

	// DELETE PRICE OF BUSINESS BY IDBUSINESS
	public static final String DELETE_BUSINESS_VALUES = "delete from price where idbusiness=?";

	// DELETE PRICE OF CUSTOMER BY IDCUSTOMER
	public static final String DELETE_CUSTOMER_VALUES = "delete from price where idcustomer=?";

	// GET LAST ID SMS TABLE
	public static final String GET_LAST_ID_ON_SMS_TABLE = "select max(id) from sms";

	// INSERT SMS SENDER
	public static final String INSERT_SMS_SENT = "insert into sms" +
	    " (id, idelivery, piece, type, mobile_to, mobile_from, message) values (?,?,?,'S',?,?,?)";

	// INSERT SMS RECEIVER
	public static final String INSERT_SMS_RECEIVED = "insert into sms" +
	    " (id, iddelivery, piece, type, mobile_to, mobile_from, message) values (?,?,?,'R',?,?,?)";

	// UPDATE SMS RECEIVER
	public static final String UPDATE_SMS_RECEIVED = "update sms set iddelivery=? where (id=? and type='R')";

	// SELECT SMS BY ID
	public static final String SELECT_SMS_BY_ID = "select * from sms where id=?";

	// SELECT SMS BY IDDELIVERY
	public static final String SELECT_SMS_DELIVERY = "select * from sms where iddelivery=? order by type, piece";

	// SELECT 10 SMS BY IDDELIVERY
	public static final String SELECT_SMS_DELIVERY_BY_MOBILE_FROM = "select * from sms where mobile_from=? order by desc datetime limit 10";

	// SELECT SMS BY MOBILE NUMBER
	public static final String SELECT_SMS_MOBILE = "select * from sms" + " where (mobile_from=? or mobile_to=?)" + " order by datetime desc limit 10";

}
