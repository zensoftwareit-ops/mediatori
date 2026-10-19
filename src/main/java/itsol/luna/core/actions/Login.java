package itsol.luna.core.actions;

import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Admin;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.MySupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class Login extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7776601155766536717L;
	
	private Admin admin;
	
	private String excp;

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
	        
	        this.admin = (Admin) sx.createQuery("FROM Admin WHERE username = :username AND password = :password")
	        		.setParameter("username", this.admin.getUsername())
	        		.setParameter("password", DigestUtils.sha256Hex(this.admin.getPassword()))
	        		.uniqueResult();
	        
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				this.excp = ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex);
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
			
		if(this.admin != null) {
				
			this.admin.setLast_activity_date(new Date());
			HibernateUtil.saveOrUpdate(this.admin);
				
			SessionSupport.getSession().setAttribute("logged_on", true);
			SessionSupport.getSession().setAttribute("admin", this.admin);
			SessionSupport.setYear(ContextSupport.getYear());
			SessionSupport.setSowingYear(ContextSupport.getSowingYear());
			
			SessionSupport.setCustomers(MySupport.getCustomers());
			SessionSupport.setProducts(MySupport.getProducts());
			SessionSupport.setPayments(MySupport.getPayments());
			SessionSupport.setContract_templates(MySupport.getContract_templates());
			
			LogSupport.log("L'utente " + this.admin.getFull_name() + " ha eseguito l'accesso.", Level.INFO);
				
			result = SUCCESS;
		} else {
			result = AUTH_ERROR;
		}
			
		return result;
		

	}
	
	public String getExcp() {
		return excp;
	}

}
