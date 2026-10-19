package itsol.luna.core.actions;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;


public class Sales extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7448857609650747047L;
	
	//------------------------------ variabile id_customer
	private Long id_customer;
	public Long getId_customer() {
		return id_customer;
	}
	public void setId_customer(Long id_customer) {
		this.id_customer = id_customer;
	}

	//------------------------------ variabile date_from
	private Date date_from;
	public Date getDate_from() {
		return date_from;
	}
	public void setDate_from(Date date_from) {
		this.date_from = date_from;
	}
	
	//------------------------------ variabile date_to
	private Date date_to;
	public Date getDate_to() {
		return date_to;
	}
	public void setDate_to(Date date_to) {
		this.date_to = date_to;
	}
	
	//------------------------------ variabile number
	private Integer number;
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

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


}
