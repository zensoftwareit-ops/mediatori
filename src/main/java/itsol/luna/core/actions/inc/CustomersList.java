package itsol.luna.core.actions.inc;

import java.util.List;
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

public class CustomersList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2300632295913760405L;
	
	//------------------------------ variabile customers
	private List<Customer> customers;
	public List<Customer> getCustomers() {
		return customers;
	}
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
		
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					if(getS() != null && !getS().isEmpty()) {
						this.customers = (List<Customer>) sx.createQuery("FROM Customer WHERE business_name LIKE '%" + getS() + "%' OR primary_email LIKE '%" + getS() + "%'").list();
					} else {
						this.customers = (List<Customer>) sx.createQuery("FROM Customer").list();
					}
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					if(getS() != null && !getS().isEmpty()) {
						this.customers = (List<Customer>) sx.createQuery("FROM Customer WHERE id_admin = :id_admin AND business_name LIKE '%" + getS() + "%' OR primary_email LIKE '%" + getS() + "%'").setParameter("id_admin", id_admin).list();
					} else {
						this.customers = (List<Customer>) sx.createQuery("FROM Customer WHERE id_admin = :id_admin").setParameter("id_admin", id_admin).list();
					}	 
				}

				createPagination(this.customers.size());
			
				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
		
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			sx.close();
		}
		
		return result;
	}

}
