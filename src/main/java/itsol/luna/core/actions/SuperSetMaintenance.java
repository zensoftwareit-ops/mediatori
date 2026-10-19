package itsol.luna.core.actions;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;

public class SuperSetMaintenance extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713735642813017236L;
	
	private Boolean value;

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
	
	public String execute() {
		
		String result = new String();
		
		try {
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("super")) {
					ContextSupport.setAttribute(value, "maintenance");
					result = SUCCESS;
				} else {
					result = SUCCESS_NOGRANTS;
				}
			} else {
				result = SUCCESS_NOLOGIN;
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			result = ERROR;
		}
		
		return result;
	}

}
