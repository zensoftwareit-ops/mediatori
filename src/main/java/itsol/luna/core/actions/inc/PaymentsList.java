package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Payment;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class PaymentsList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3875169385762732716L;
	
	private List<Payment> payments = new ArrayList<Payment>(0);

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.payments = (List<Payment>) sx.createQuery("FROM Payment ORDER BY ptag").list();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.payments = (List<Payment>) sx.createQuery("FROM Payment WHERE id_admin = :id_admin ORDER BY ptag").setParameter("id_admin", id_admin).list();
				}
				
				createPagination(this.payments.size());
			
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
