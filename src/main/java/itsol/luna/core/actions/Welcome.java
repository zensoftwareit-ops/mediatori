package itsol.luna.core.actions;



import java.util.logging.Level;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class Welcome extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2691398859817071457L;
	
	
	//------------------------------ variabile countCustomers
	private Long countCustomers;
	public Long getCountCustomers() {
		return countCustomers;
	}
	public void setCountCustomers(Long countCustomers) {
		this.countCustomers = countCustomers;
	}
	
	
	//------------------------------ variabile countSales
	private Long countSales;
	public Long getCountSales() {
		return countSales;
	}
	public void setCountSales(Long countSales) {
		this.countSales = countSales;
	}
	
	
	//------------------------------ variabile countInvoices
	private Long countInvoices;
	public Long getCountInvoices() {
		return countInvoices;
	}
	public void setCountInvoices(Long countInvoices) {
		this.countInvoices = countInvoices;
	}
	
	//------------------------------ variabile countMeasures
	private Long countMeasures;
	public Long getCountMeasures() {
		return countMeasures;
	}
	public void setCountMeasures(Long countMeasures) {
		this.countMeasures = countMeasures;
	}

	
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		

		
		try {

//			String host = ContextSupport.getRequest().getHeader("x-forwarded-host");
//			
//			if(host == null) {
//				host = "localhost";
//			} else if(host.isEmpty()) {
//				host = "localhost";
//			}
//			
//			Domain domain = (Domain)sx.createQuery("FROM Domain WHERE domain = :domain").setParameter("domain", host).uniqueResult();
//
//			
//			if(domain != null) {
//				SessionSupport.getSession().setAttribute("domain", domain);
//			}
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.countCustomers = (Long) sx.createQuery("SELECT COUNT(*) FROM Customer").uniqueResult();
					this.countSales = (Long) sx.createQuery("SELECT COUNT(*) FROM Sale WHERE id_year = :id_year").setParameter("id_year", SessionSupport.getSowingYear().getId()).uniqueResult();
					this.countInvoices = (Long) sx.createQuery("SELECT COUNT(*) FROM Invoice WHERE id_year = :id_year").setParameter("id_year", SessionSupport.getYear().getId()).uniqueResult();
					this.countMeasures = (Long) sx.createQuery("SELECT COUNT(*) FROM YieldMeasure WHERE id_year = :id_year").setParameter("id_year", SessionSupport.getSowingYear().getId()).uniqueResult();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.countCustomers = (Long) sx.createQuery("SELECT COUNT(*) FROM Customer WHERE id_admin = :id_admin").setParameter("id_admin", id_admin).uniqueResult();
					this.countSales = (Long) sx.createQuery("SELECT COUNT(*) FROM Sale WHERE id_year = :id_year AND id_admin = :id_admin").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).uniqueResult();
					this.countInvoices= (Long) sx.createQuery("SELECT COUNT(*) FROM Invoice WHERE id_year = :id_year AND id_admin = :id_admin").setParameter("id_year", SessionSupport.getYear().getId()).setParameter("id_admin", id_admin).uniqueResult();
					this.countMeasures = (Long) sx.createQuery("SELECT COUNT(*) FROM YieldMeasure WHERE id_year = :id_year AND id_admin = :id_admin").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).uniqueResult();
				}
				
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
