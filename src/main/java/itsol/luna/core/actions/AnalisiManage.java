package itsol.luna.core.actions;

import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class AnalisiManage extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2407240477717809600L;
	
	private Long id_analisi;
	private itsol.luna.core.beans.Analisi analisi;
	
public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {

				this.analisi = (itsol.luna.core.beans.Analisi) sx.createQuery("FROM Analisi WHERE id = :id_analisi")
						.setParameter("id_analisi", getId_analisi())
						.uniqueResult();
				
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
				setResp("Si è verificato un errore durante la ricerca del record.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante la ricerca del record.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return result;
		
	}

public Long getId_analisi() {
	return id_analisi;
}

public void setId_analisi(Long id_analisi) {
	this.id_analisi = id_analisi;
}

public itsol.luna.core.beans.Analisi getAnalisi() {
	return analisi;
}

public void setAnalisi(itsol.luna.core.beans.Analisi analisi) {
	this.analisi = analisi;
}

public List<Product> getProducts() {
	return SessionSupport.getProducts();
}

public List<Customer> getCustomers() {
	return SessionSupport.getCustomers();
}

}
