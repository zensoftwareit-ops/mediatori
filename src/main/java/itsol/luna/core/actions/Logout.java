package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;

public class Logout extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -387947745425661572L;
	
	private String excp;
	
	public String execute() {
		
		String result = new String();
		
		try {
			
			LogSupport.log("L'utente " + SessionSupport.getAdmin().getFull_name() + " si è disconnesso.", Level.INFO);
			
			SessionSupport.getSession().removeAttribute("logged_on");
			SessionSupport.getSession().removeAttribute("admin");
			
			result = SUCCESS;
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				this.excp = ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex);
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		}
		
		return result;

	}
	
	public String getExcp() {
		return excp;
	}

}
