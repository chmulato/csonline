package br.com.mulato.cso.service;

import java.io.Serializable;
import java.util.List;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.SmsVO;

public interface DeliveryService extends Serializable {

	public List<DeliveryVO> listAllDeliveryBusinessCompleted (Integer idBusiness) throws WebException;

	public List<DeliveryVO> listAllDeliveryBusinessNotCompleted (Integer idBusiness) throws WebException;

	public List<DeliveryVO> listAllDeliveryCustomerCompleted (Integer idCustomer) throws WebException;

	public List<DeliveryVO> listAllDeliveryCustomerNotCompleted (Integer idCustomer) throws WebException;

	public List<DeliveryVO> listAllDeliveryCourierCompleted (Integer idCourier) throws WebException;

	public List<DeliveryVO> listAllDeliveryCourierNotCompleted (Integer idCourier) throws WebException;

	public List<SmsVO> listAllCourierSMS (CourierVO courier) throws WebException;

	public List<SmsVO> listAllDeliverySMS (Integer idDelivery) throws WebException;

	public void saveDeliveryByBusiness (DeliveryVO delivery, boolean send_mail, boolean send_sms) throws WebException;

	public void saveDeliveryByCustomer (DeliveryVO delivery, boolean send_mail) throws WebException;

	public void saveDeliveryByCourier (DeliveryVO delivery, boolean send_mail) throws WebException;

	public void saveSMSDelivery (SmsVO sms) throws WebException;

	public int saveSMS (SmsVO sms) throws WebException;

	public DeliveryVO find (Integer idDelivery) throws WebException;

	public SmsVO findSMS (Integer idSms) throws WebException;

	public void delete (Integer idDelivery) throws WebException;

}
