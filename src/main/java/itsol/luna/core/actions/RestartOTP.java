package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class RestartOTP extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5007474059027483398L;
	
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
					

					
					if(getCustomerType().equals("seller") && sale.getOtpSeller() != null) {
						
						sale.setOtpSellerSent("NO");

					} else if(getCustomerType().equals("buyer") && sale.getOtpBuyer() != null) {
						
						sale.setOtpBuyerSent("NO");
						
					} else {
						setResp("OTP non trovato.");
						setResp_status_ko();
					}
						
					HibernateUtil.saveOrUpdate(sale);
					
					
					
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
}
