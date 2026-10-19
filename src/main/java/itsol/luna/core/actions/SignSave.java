package itsol.luna.core.actions;

import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SignSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4103547204079968455L;
	
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
	
	private String otp;
	
	 public String getOtp() {
			return otp;
		}
		public void setOtp(String otp) {
			this.otp = otp;
		}
	
	public String execute() {
		String result = new String();
		
		
		try {
			
			setRedirectAction("modal_close");
			
//			if(SessionSupport.checkSession()) {

				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				try {
					
					String q = "FROM Sale WHERE id = :id_sale";
					
					if(getCustomerType().equals("seller")) {
						q += " AND otpSeller = :otp AND otpSellerSent = 'SI'"; 
					}
					
					if(getCustomerType().equals("buyer")) {
						q += " AND otpBuyer = :otp AND otpBuyerSent = 'SI'"; 
					}
					
					
					this.sale = (Sale) sx.createQuery(q)
							.setParameter("id_sale", getId_sale())
							.setParameter("otp", getOtp())
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
					if(getCustomerType().equals("seller")) {
						this.sale.setSignedSeller("SI");
						this.sale.setSignedSellerDate(new Date());
						HibernateUtil.saveOrUpdate(this.sale);
							
						setResp("E' stato applicata la FIRMA VENDITORE per la vendita " + this.sale.getNumber() + ".");
						setResp_status_ok();
					
					} else if(getCustomerType().equals("buyer")) {
						
						this.sale.setSignedBuyer("SI");
						this.sale.setSignedBuyerDate(new Date());
						HibernateUtil.saveOrUpdate(this.sale);
						
						setResp("E' stato applicata la FIRMA ACQUIRENTE per la vendita " + this.sale.getNumber() + ".");
						setResp_status_ok();

					} else {
						setResp("OTP non trovato.");
						setResp_status_ko();
					}
					
				} else {
					setResp("Vendita non trovata.");
					setResp_status_ko();
				}

				result = SUCCESS;
				
			
//			} else {
//				result = SUCCESS_NOLOGIN;
//			}
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

}
