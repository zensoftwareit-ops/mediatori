package itsol.luna.core.actions;

import java.util.List;
import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;

public class AnalisiNew extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7759405343859118899L;
	private itsol.luna.core.beans.Analisi analisi;
	
public String execute() {
		
		String result = new String();
		
		try {
			if(SessionSupport.checkSession()) {
				this.analisi = new itsol.luna.core.beans.Analisi();
				this.analisi.setDataAnalisi(new Date());
				this.analisi.setCosto(86.00);
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
