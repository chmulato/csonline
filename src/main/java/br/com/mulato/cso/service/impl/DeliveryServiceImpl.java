package br.com.mulato.cso.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.SmsVO;
import br.com.mulato.cso.service.DeliveryService;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.SendEmail;
import br.com.mulato.cso.utils.SendSms;
import br.com.mulato.cso.utils.ToolUtils;

public class DeliveryServiceImpl
    implements DeliveryService
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(DeliveryServiceImpl.class);

	private SendSms sendSMSFromBusinessDelivery (final SmsVO sms) throws WebException
	{

		if ((sms.getTo() == null) || (sms.getTo().equals("")))
		{
			throw new WebException("Informe n鷐ero de celular para enviar!");
		}

		if ((sms.getFrom() == null) || (sms.getFrom().equals("")))
		{
			throw new WebException("Informe n鷐ero de celular de envio!");
		}

		if ((sms.getMessage() == null) || (sms.getMessage().equals("")))
		{
			throw new WebException("Informe mensagem de envio!");
		}

		if (new ToolUtils().validarNumero(sms.getTo()))
		{
			throw new WebException("N鷐ero de celular inv醠ido!");
		}

		return new SendSms(sms.getTo(), sms.getFrom(), sms.getMessage());

	}

	@SuppressWarnings("unused")
	private void sendMessageFromBusinessDelivery (final Integer deliveryId) throws WebException
	{

		final StringBuilder message = new StringBuilder();

		String subject = "";

		final DeliveryVO delivery = FactoryDAO.getInstancia().getDeliveryDAO().find(deliveryId);

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Neg贸cio da entrega n鉶 encontrado.");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Cliente da entrega n鉶 encontrado.");
		}

		if (delivery.getCourier() == null)
		{
			throw new WebException("Entregador n鉶 encontrado.");
		}

		if ((delivery.getReceived()) && (!delivery.getCompleted()))
		{

			final String emailCustomer = delivery.getCustomer().getEmail();
			final String emailCourier = delivery.getCourier().getEmail();

			subject = "Corrida No. " + delivery.getId() + " escalada para entrega!";

			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final String date = fmt.format(delivery.getDatetime());

			message.append("Empresa: 				").append(delivery.getBusiness().getName()).append("\r\n");
			message.append("Cliente: 				").append(delivery.getCustomer().getName()).append("\r\n");
			message.append("Nome do Entregador:		").append(delivery.getCourier().getName()).append("\r\n");
			message.append("Data/Hora: 				").append(date).append("\r\n");
			message.append("Endere鏾 base:		    ").append(delivery.getStart()).append("\r\n");
			message.append("Endere鏾 de entrega:    ").append(delivery.getDestination()).append("\r\n");
			message.append("Dist鈔cia: 	 			").append(delivery.getKm()).append(" Km\r\n");
			message.append("Contato: 	 			").append(delivery.getContact()).append("\r\n");
			message.append("Descri玢o: 	 			").append(delivery.getDescription()).append("\r\n");
			message.append("Additional:  			").append(delivery.getAdditionalCost()).append(" reais\r\n");
			message.append("Custo: 		 			").append(delivery.getCost()).append(" reais\r\n");

			// lista de emails de destino
			final String[] emailList01 = new String[2];

			emailList01[0] = emailCustomer;
			emailList01[1] = emailCourier;

			final SendEmail sendEmail = new SendEmail(emailList01, subject, message.toString());

		}

		if ((delivery.getReceived()) && (delivery.getCompleted()))
		{
			final String emailCustomer = delivery.getCustomer().getEmail();
			subject = "Corrida No. " + delivery.getId() + " finalizada. Encomenda entregue!";
			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final String date = fmt.format(delivery.getDatetime());
			message.append("Empresa: 				").append(delivery.getBusiness().getName()).append("\r\n");
			message.append("Cliente: 				").append(delivery.getCustomer().getName()).append("\r\n");
			message.append("Entregador:				").append(delivery.getCourier().getName()).append("\r\n");
			message.append("Data/Hora: 				").append(date).append("\r\n");
			message.append("Endere鏾 base:		    ").append(delivery.getStart()).append("\r\n");
			message.append("Endere鏾 de entrega:    ").append(delivery.getDestination()).append("\r\n");
			message.append("Dist鈔cia: 	 			").append(delivery.getKm()).append(" Km\r\n");
			message.append("Contato: 	 			").append(delivery.getContact()).append("\r\n");
			message.append("Descri玢o: 	 			").append(delivery.getDescription()).append("\r\n");
			message.append("Encomenda entregue.");
			// lista de emails de destino
			final String[] emailList02 = new String[2];
			emailList02[0] = emailCustomer;
			final SendEmail sendEmail = new SendEmail(emailList02, subject, message.toString());
		}
	}

	@SuppressWarnings("unused")
	private void sendMessageFromCustomerDelivery (final Integer deliveryId) throws WebException
	{

		final StringBuilder message = new StringBuilder();

		final DeliveryVO delivery = FactoryDAO.getInstancia().getDeliveryDAO().find(deliveryId);

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Neg?cio da entrega n鉶 encontrado.");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Cliente da entrega n鉶 encontrado.");
		}

		final String emailBusiness = delivery.getBusiness().getEmail();

		final String subject = "Agendar corrida No. " + delivery.getId() + ". Por favor, encaminhe um entregador e confirme valor!";

		final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final String date = fmt.format(delivery.getDatetime());

		message.append("Empresa: 				").append(delivery.getBusiness().getName()).append("\r\n");
		message.append("Cliente: 				").append(delivery.getCustomer().getName()).append("\r\n");
		message.append("Nome do Entregador:		" + "Escale um motoboy!" + "\r\n");
		message.append("Data/Hora: 				").append(date).append("\r\n");
		message.append("Endere鏾 base:		    ").append(delivery.getStart()).append("\r\n");
		message.append("Endere鏾 de entrega:    ").append(delivery.getDestination()).append("\r\n");
		message.append("Dist鈔cia: 	 			").append(delivery.getKm()).append(" Km\r\n");
		message.append("Contato: 	 			").append(delivery.getContact()).append("\r\n");
		message.append("Descri玢o: 	 			").append(delivery.getDescription()).append("\r\n");
		message.append("Adicional: 	 			").append(delivery.getAdditionalCost()).append(" reais (Valor a confirmar)\r\n");
		message.append("Custo: 		 			").append(delivery.getCost()).append(" reais (Valor a confirmar)\r\n");

		// lista de email de destino
		final String[] emailList03 = new String[1];

		emailList03[0] = emailBusiness;

		final SendEmail sendEmail = new SendEmail(emailList03, subject, message.toString());

	}

	@SuppressWarnings("unused")
	private void sendMessageFromCourierDelivery (final Integer deliveryId) throws WebException
	{

		final StringBuilder message = new StringBuilder();

		String subject = "";

		final DeliveryVO delivery = FactoryDAO.getInstancia().getDeliveryDAO().find(deliveryId);

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Neg贸cio da entrega n鉶 encontrado.");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Cliente da entrega n鉶 encontrado.");
		}

		if (delivery.getCourier() == null)
		{
			throw new WebException("Entregador n鉶 encontrado.");
		}

		final String emailBusiness = delivery.getBusiness().getEmail();
		final String emailCustomer = delivery.getCustomer().getEmail();

		if (delivery.getCompleted())
		{

			subject = "Corrida No. " + delivery.getId() + " finalizada. Encomenda entregue!";

			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			final String date = fmt.format(delivery.getDatetime());

			message.append("Empresa: 				").append(delivery.getBusiness().getName()).append("\r\n");
			message.append("Cliente: 				").append(delivery.getCustomer().getName()).append("\r\n");
			message.append("Entregador:				").append(delivery.getCourier().getName()).append("\r\n");
			message.append("Data/Hora: 				").append(date).append("\r\n");
			message.append("Endere鏾 base:		    ").append(delivery.getStart()).append("\r\n");
			message.append("Endere鏾 de entrega:    ").append(delivery.getDestination()).append("\r\n");
			message.append("Dist鈔cia: 	 			").append(delivery.getKm()).append(" Km\r\n");
			message.append("Contato: 	 			").append(delivery.getContact()).append("\r\n");
			message.append("Descri玢o: 	 			").append(delivery.getDescription()).append("\r\n");
			message.append("Encomenda entregue.");

			// lista de emails de destino
			final String[] emailList04 = new String[2];

			emailList04[0] = emailBusiness;
			emailList04[1] = emailCustomer;

			final SendEmail sendEmail = new SendEmail(emailList04, subject, message.toString());

		}
	}

	@Override
	public List<DeliveryVO> listAllDeliveryBusinessCompleted (final Integer idBusiness) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idBusiness == null)
		{
			throw new WebException("Informe id neg贸cio.");
		}

		if (idBusiness <= 0)
		{
			throw new WebException("Informe id neg贸cio!");
		}

		LOGGER.info("Listar todas as entregas completas do neg贸cio.");

		final BusinessVO business = new BusinessVO();
		business.setId(idBusiness);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryBusinessCompleted(business, true);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryBusinessNotCompleted (final Integer idBusiness) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idBusiness == null)
		{
			throw new WebException("Informe id neg贸cio.");
		}

		if (idBusiness <= 0)
		{
			throw new WebException("Informe id neg贸cio!");
		}

		LOGGER.info("Listar todas as entregas n鉶 completadas do neg贸cio.");

		final BusinessVO business = new BusinessVO();
		business.setId(idBusiness);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryBusinessCompleted(business, false);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCustomerCompleted (final Integer idCustomer) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idCustomer == null)
		{
			throw new WebException("Informe id cliente.");
		}

		if (idCustomer <= 0)
		{
			throw new WebException("Informe id cliente!");
		}

		LOGGER.info("Listar todas as entregas completas do cliente.");

		final CustomerVO customer = new CustomerVO();
		customer.setId(idCustomer);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCustomerCompleted(customer, true);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCustomerNotCompleted (final Integer idCustomer) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idCustomer == null)
		{
			throw new WebException("Informe id cliente.");
		}

		if (idCustomer <= 0)
		{
			throw new WebException("Informe id cliente!");
		}

		LOGGER.info("Listar todas as entregas n鉶 completadas do cliente.");

		final CustomerVO customer = new CustomerVO();
		customer.setId(idCustomer);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCustomerCompleted(customer, false);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCourierCompleted (final Integer idCourier) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idCourier == null)
		{
			throw new WebException("Informe id entregador.");
		}

		if (idCourier <= 0)
		{
			throw new WebException("Informe id entregador!");
		}

		LOGGER.info("Listar todas as entregas completas do entregador.");

		final CourierVO courier = new CourierVO();
		courier.setId(idCourier);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCourierCompleted(courier, true);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCourierNotCompleted (final Integer idCourier) throws WebException
	{

		List<DeliveryVO> list = null;

		if (idCourier == null)
		{
			throw new WebException("Informe id entregador.");
		}

		if (idCourier <= 0)
		{
			throw new WebException("Informe id entregador!");
		}

		LOGGER.info("Listar todas as entregas n鉶 completadas do entregador.");

		final CourierVO courier = new CourierVO();
		courier.setId(idCourier);

		try
		{
			list = FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCourierCompleted(courier, false);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public void saveDeliveryByBusiness (final DeliveryVO delivery, final boolean send_mail, final boolean send_sms) throws WebException
	{

		if (delivery == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getBusiness().getId() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getCustomer().getId() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getCourier() == null)
		{
			throw new WebException("Informe entregador!");
		}

		if (delivery.getCourier().getId() == null)
		{
			throw new WebException("Informe entregador!");
		}

		if (delivery.getContact() == null)
		{
			throw new WebException("Informe contato!");
		}

		if (delivery.getDescription() == null)
		{
			throw new WebException("Informe descri玢o da entrega!");
		}

		if (delivery.getKm() == null)
		{
			throw new WebException("Informe dist鈔cia!");
		}

		if (delivery.getCost() == null)
		{
			throw new WebException("Informe valor!");
		}

		if (send_sms)
		{

			if (delivery.getSendSMS() == null)
			{
				throw new WebException("Escreva a mensagem SMS!");
			}

		}

		try
		{

			if (delivery.getId() == null)
			{

				int deliveryId = 0;

				LOGGER.info("Salvar entrega pelo neg贸cio.");

				deliveryId = FactoryDAO.getInstancia().getDeliveryDAO().insert(delivery);

				if (send_mail)
				{

					sendMessageFromBusinessDelivery(deliveryId);

				}

				if (send_sms)
				{

					final CourierVO courier = FactoryDAO.getInstancia().getCourierDAO().find(delivery.getCourier().getId(), false);

					final SmsVO sms = delivery.getSendSMS();
					delivery.setId(deliveryId);
					sms.setDelivery(delivery);
					sms.setTo(courier.getMobile());
					sms.setFrom(InitProperties.getSmsMobile());
					delivery.setSendSMS(sms);

					sendSMSFromBusinessDelivery(delivery.getSendSMS());

					FactoryDAO.getInstancia().getSmsDAO().insert(delivery.getSendSMS());

				}

			}
			else
			{

				LOGGER.info("Atualizar entrega pelo neg贸cio.");

				FactoryDAO.getInstancia().getDeliveryDAO().update(delivery);

				if (send_mail)
				{

					sendMessageFromBusinessDelivery(delivery.getId());

				}

				if (send_sms)
				{

					final CourierVO courier = FactoryDAO.getInstancia().getCourierDAO().find(delivery.getCourier().getId(), false);

					final SmsVO sms = delivery.getSendSMS();
					sms.setDelivery(delivery);
					sms.setTo(courier.getMobile());
					sms.setFrom(InitProperties.getSmsMobile());
					delivery.setSendSMS(sms);

					sendSMSFromBusinessDelivery(delivery.getSendSMS());

					FactoryDAO.getInstancia().getSmsDAO().insert(delivery.getSendSMS());

				}

			}

		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}

	}

	@Override
	public void saveDeliveryByCustomer (final DeliveryVO delivery, final boolean send_mail) throws WebException
	{

		if (delivery == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getBusiness().getId() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getCustomer().getId() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getContact() == null)
		{
			throw new WebException("Informe contato!");
		}

		if (delivery.getDescription() == null)
		{
			throw new WebException("Informe descri玢o da entrega!");
		}

		if (delivery.getKm() == null)
		{
			throw new WebException("Informe dist鈔cia!");
		}

		if (delivery.getCost() == null)
		{
			throw new WebException("Informe valor!");
		}

		try
		{
			if (delivery.getId() == null)
			{
				int deliveryId = 0;
				LOGGER.info("Salvar entrega pelo cliente.");
				deliveryId = FactoryDAO.getInstancia().getDeliveryDAO().insert(delivery);
				if (send_mail)
				{
					sendMessageFromCustomerDelivery(deliveryId);
				}
			}
			else
			{
				LOGGER.info("Atualizar entrega pelo cliente.");
				FactoryDAO.getInstancia().getDeliveryDAO().update(delivery);
				if (send_mail)
				{
					sendMessageFromCustomerDelivery(delivery.getId());
				}
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public void saveDeliveryByCourier (final DeliveryVO delivery, final boolean send_mail) throws WebException
	{

		if (delivery == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (delivery.getId() == null)
		{
			throw new WebException("Informe id entrega!");
		}

		if (delivery.getId() <= 0)
		{
			throw new WebException("Informe id entrega!");
		}

		if (delivery.getBusiness() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getBusiness().getId() == null)
		{
			throw new WebException("Informe neg贸cio!");
		}

		if (delivery.getCustomer() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getCustomer().getId() == null)
		{
			throw new WebException("Informe cliente!");
		}

		if (delivery.getCourier() == null)
		{
			throw new WebException("Informe entregador!");
		}

		if (delivery.getCourier().getId() == null)
		{
			throw new WebException("Informe entregador!");
		}

		if (delivery.getContact() == null)
		{
			throw new WebException("Informe contato!");
		}

		if (delivery.getDescription() == null)
		{
			throw new WebException("Informe descri玢o da entrega!");
		}

		if (delivery.getKm() == null)
		{
			throw new WebException("Informe dist鈔cia!");
		}

		if (delivery.getCost() == null)
		{
			throw new WebException("Informe valor!");
		}

		try
		{
			LOGGER.info("Atualizar entrega pelo entregador.");
			FactoryDAO.getInstancia().getDeliveryDAO().update(delivery);
			if (send_mail)
			{
				sendMessageFromCourierDelivery(delivery.getId());
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}

	}

	@Override
	public DeliveryVO find (final Integer idDelivery) throws WebException
	{
		DeliveryVO delivery = null;
		if ((idDelivery == null) || (idDelivery <= 0))
		{
			throw new WebException("Informe id entrega.");
		}
		LOGGER.info("Buscar entrega.");
		try
		{
			delivery = FactoryDAO.getInstancia().getDeliveryDAO().find(idDelivery);
			if (delivery != null)
			{
				final List<SmsVO> listSMS = FactoryDAO.getInstancia().getSmsDAO().listAllSmsDelivery(idDelivery);
				if ((listSMS != null) && (listSMS.size() > 0))
				{
					delivery.setListSMS(listSMS);
				}
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return delivery;
	}

	@Override
	public void delete (final Integer idDelivery) throws WebException
	{

		if ((idDelivery == null) || (idDelivery <= 0))
		{
			throw new WebException("Informe id entrega.");
		}

		LOGGER.info("Deletar entrega.");

		try
		{
			FactoryDAO.getInstancia().getDeliveryDAO().delete(idDelivery);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public List<SmsVO> listAllDeliverySMS (final Integer idDelivery) throws WebException
	{
		List<SmsVO> list = null;
		if ((idDelivery == null) || (idDelivery.intValue() <= 0))
		{
			throw new WebException("Informe id entrega.");
		}
		LOGGER.info("Lista todas as mensagens da entrega.");
		try
		{
			list = FactoryDAO.getInstancia().getSmsDAO().listAllSmsDelivery(idDelivery);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public int saveSMS (final SmsVO sms) throws WebException
	{

		int count = 0;

		if (sms == null)
		{
			throw new WebException("Informe sms!");
		}

		if (sms.getDelivery() == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (sms.getDelivery().getId() == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (sms.getTo() == null)
		{
			throw new DAOException("Informe celular a receber mensagem!");
		}

		if (sms.getTo().equals(""))
		{
			throw new DAOException("Informe celular a receber mensagem!");
		}

		if (sms.getFrom() == null)
		{
			throw new DAOException("Informe celular de envio da mensagem!");
		}

		if (sms.getFrom().equals(""))
		{
			throw new DAOException("Informe celular de envio da mensagem!");
		}

		if ((sms.getType() != 'S') && (sms.getType() != 'R'))
		{
			throw new WebException("Informe sms de envio ['S'] ou resposta ['R']!");
		}

		if (sms.getMessage() == null)
		{
			throw new WebException("Informe mensagem do sms!");
		}

		if (sms.getMessage().equals(""))
		{
			throw new WebException("Informe mensagem do sms!");
		}

		LOGGER.info("Salvar mensagem sms.");

		try
		{
			count = FactoryDAO.getInstancia().getSmsDAO().insert(sms);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return count;
	}

	@Override
	public void saveSMSDelivery (final SmsVO sms) throws WebException
	{

		if (sms == null)
		{
			throw new WebException("Informe sms!");
		}

		if (sms.getId() == null)
		{
			throw new WebException("Informe id mensagem!");
		}

		if (sms.getId() <= 0)
		{
			throw new WebException("Informe id mensagem!");
		}

		if (sms.getDelivery() == null)
		{
			throw new WebException("Informe entrega!");
		}

		if (sms.getDelivery().getId() == null)
		{
			throw new WebException("Informe id entrega!");
		}

		if (sms.getDelivery().getId() <= 0)
		{
			throw new WebException("Informe id entrega!");
		}

		if (sms.getType() != 'R')
		{
			throw new WebException("Informe sms de resposta ['R']!");
		}

		LOGGER.info("Atualizar mensagem de entrega.");

		try
		{
			FactoryDAO.getInstancia().getSmsDAO().update(sms);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public SmsVO findSMS (final Integer idSms) throws WebException
	{

		SmsVO sms = null;

		if ((idSms == null) || (idSms <= 0))
		{
			throw new WebException("Informe id mensagem.");
		}

		LOGGER.info("Pesquisar mensagem sms.");

		try
		{
			sms = FactoryDAO.getInstancia().getSmsDAO().find(idSms);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return sms;
	}

	@Override
	public List<SmsVO> listAllCourierSMS (final CourierVO courier) throws WebException
	{
		List<SmsVO> list = null;

		if (courier == null)
		{
			throw new WebException("Informe entregador.");
		}

		if (courier.getId() == null)
		{
			throw new WebException("Informe id entregador.");
		}

		if (courier.getId() <= 0)
		{
			throw new WebException("Informe id entregador.");
		}

		if (courier.getMobile() == null)
		{
			throw new WebException("Informe celular do entregador.");
		}

		if (courier.getMobile().equals(""))
		{
			throw new WebException("Informe celular do entregador.");
		}

		LOGGER.info("Lista todas as mensagens do entregador.");

		try
		{
			list = FactoryDAO.getInstancia().getSmsDAO().listAllSmsMobile(courier.getMobile());
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

}
