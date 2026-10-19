package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CustomerDelete extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4930458764164932575L;
	

	
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
				
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				/**
				 * ricerca oggetto customer
				 */
				try {
					this.customer = (Customer) sx.createQuery("FROM Customer WHERE id = :id_customer")
							.setParameter("id_customer", getId_customer())
							.uniqueResult();
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} finally {
					sx.close();
				}
				
				/**
				 * eliminazione se non nullo
				 */
				if(this.customer != null) {
					Long id = this.customer.getId();
					HibernateUtil.delete(this.customer);
					LogSupport.log(op() + "E' stato eliminato il cliente " + id + ".", Level.INFO);
					setResp("E' stato eliminato il cliente " + id + ".");
					setResp_status_ok();
				} else {
					LogSupport.log(op() + "Nessuna eliminazione.", Level.INFO);
					setResp("Nessuna eliminazione.");
					setResp_status_ko();
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
			LogSupport.log(op() + "Si è verificato un errore durante l'eliminazione.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			
			
		}
		
		return result;
	}

}
