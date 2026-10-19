package itsol.luna.core.actions;

import java.util.ArrayList;
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

public class Analisi extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8706984980842090584L;
	
public String execute() {
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {
				saveActionName();
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
				setResp("Si è verificato un errore durante il caricamento della pagina.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} 
		
		return result;
	}

public List<Customer> getCustomers() {
	return SessionSupport.getCustomers();
}

@SuppressWarnings("unchecked")
public List<String> getComuni() {
	List<String> comuni = new ArrayList<String>(0);
	Session sx = HibernateUtil.getSessionFactory().openSession();
	try {
		String q = "SELECT DISTINCT c.legal_city FROM Analisi a INNER JOIN a.cliente c "
				+ "WHERE c.legal_city IS NOT NULL AND TRIM(c.legal_city) <> ''";
		if(!SessionSupport.checkSessionLevel("admin|super")) {
			q += " AND a.id_admin = :id_admin";
		}
		q += " ORDER BY c.legal_city";
		org.hibernate.Query query = sx.createQuery(q);
		if(!SessionSupport.checkSessionLevel("admin|super")) {
			query.setParameter("id_admin", SessionSupport.getAdmin().getId());
		}
		comuni = (List<String>) query.list();
	} catch(Exception ex) {
		LogSupport.log(op() + "Errore durante il caricamento dei comuni: " + ExceptionUtils.getMessage(ex), Level.SEVERE);
	} finally {
		sx.close();
	}
	return comuni;
}

}
