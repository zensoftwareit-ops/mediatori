package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.MySupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CustomerSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6964700692177210310L;
	
	
	/**
	 * variabile customer
	 */
	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	/**
	 * metodo execute
	 */
	public String execute() {
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				/**
				 * assegno cliente all'utente
				 */
				try {
					if(this.customer.getAdmin().getId() == null) {
						this.customer.setAdmin(SessionSupport.getAdmin());
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'utente.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				
				/**
				 * imposto action di ritorno
				 */
				try {
					HibernateUtil.saveOrUpdate(this.customer);
					setResp("E' stato salvato il cliente " + this.customer.getId() + ".");
					setResp_status_ok();

					switch(getAction_type()) {
					case "save":
						setRedirectAction("customers/manage/" + this.customer.getId());
						break;
					case "save_close":
						setRedirectAction("customers");
						break;
					default:
						setRedirectAction("customers");
						break;
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore impostando la action di ritorno.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			SessionSupport.setCustomers(MySupport.getCustomers());
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante l'aggiornamento della lista.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} 
		
		return result;
		
	}
	
	public void validate() {
		
		if(this.customer.getBusiness_name().length() == 0) {
			addFieldError("customer.business_name", "Campo obbligatorio.");
		}
		
		if(this.customer.getInternal_code().length() == 0) {
			addFieldError("customer.internal_code", "Campo obbligatorio.");
		}
		
	}


}
