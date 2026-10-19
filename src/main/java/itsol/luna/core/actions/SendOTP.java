package itsol.luna.core.actions;

import java.util.Optional;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.utils.HibernateUtil;
import pl.smsapi.OAuthClient;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.api.action.sms.SMSSend;
import pl.smsapi.api.response.MessageResponse;
import pl.smsapi.api.response.StatusResponse;
import pl.smsapi.exception.SmsapiException;
import pl.smsapi.proxy.ProxyNative;

public class SendOTP extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1974549228310028297L;
	
	private String tel;
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}

	private Sale sale;
	private Long id_sale;
	private String customerType;
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	public Long getId_sale() {
		return id_sale;
	}
	public void setId_sale(Long id_sale) {
		this.id_sale = id_sale;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public String execute() {
		String result = new String();
		
		
		try {


				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				try {
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id = :id_sale")
							.setParameter("id_sale", getId_sale())
							.uniqueResult();
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
						setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
						result = ERROR_DEBUG;
					} else {
						setResp("Si è verificato un errore durante la ricerca del record");
						setResp_status_ko();
						result = ERROR;
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca del record", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} finally {
					sx.close();
				}


				if(sale != null) {
					
					String key = sale.getAdmin().getSmsApiKey();
					
					if(getCustomerType().equals("seller") && sale.getOtpSeller() != null) {
						
						if(getTel().isBlank())
							setTel(sale.getSeller().getPrimary_phone());
						
						if(sendSms(getTel(), sale.getOtpSeller(), sale.getNumber().toString(), key)) {
							
							this.sale.setOtpSellerSent("SI");
							HibernateUtil.saveOrUpdate(this.sale);
							
							setResp("E' stato inviato il codice OTP VENDITORE per la vendita " + this.sale.getNumber() + ".");
							setResp_status_ok();
						} else {
							setResp("Errore durante l'invio di OTP VENDITORE");
							setResp_status_ko();
						}
					} else if(getCustomerType().equals("buyer") && sale.getOtpBuyer() != null) {
						
						if(getTel().isBlank())
							setTel(sale.getBuyer().getPrimary_phone());
						
						if(sendSms(getTel(), sale.getOtpBuyer(), sale.getNumber().toString(), key)) {
							
							this.sale.setOtpBuyerSent("SI");
							HibernateUtil.saveOrUpdate(this.sale);
							
							setResp("E' stato inviato il codice OTP ACQUIRENTE per la vendita " + this.sale.getNumber() + ".");
							setResp_status_ok();
						} else {
							setResp("Errore durante l'invio di OTP ACQUIRENTE");
							setResp_status_ko();
						}
					} else {
						setResp("OTP non trovato.");
						setResp_status_ko();
					}
						
					
					
					
					
				} else {
					setResp("Vendita non trovata.");
					setResp_status_ko();
				}

				result = SUCCESS;
				
			
		} catch(Exception ex) {		
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore generico");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore generico", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} 
		return result;
	}
	
	private boolean sendSms(String tel, String otp, String number, String key) {
		boolean res = false;
		
		try {
            String oauthToken = key;
            OAuthClient client = new OAuthClient(oauthToken);
            ProxyNative proxy = new ProxyNative("https://api.smsapi.com/");

            SmsFactory smsApi = new SmsFactory(client, proxy);
            
            String msg = "Il tuo codice per firmare il contratto n. " + number + ": " + otp;

            tel = tel.replace("+39","").replaceAll("[^\\d.]", "");
            System.out.println(tel+msg);
            
            SMSSend action = smsApi.actionSend()
                    .setTo("+39" + tel)
                    .setText(msg);

            StatusResponse result = action.execute();

            Optional<MessageResponse> status = result.getList().stream().findFirst();
            if (status.isEmpty()) {
                throw new RuntimeException();
            }
            
            res = true;

        } catch (SmsapiException e) {
            System.out.println("Exception: " + e.getMessage());
        }
		return res;
	}

}
