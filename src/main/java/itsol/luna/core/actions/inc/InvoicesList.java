package itsol.luna.core.actions.inc;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class InvoicesList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -126289229571353592L;
	
	//------------------------------ variabile invoices
	private List<Invoice> invoices;
	public List<Invoice> getInvoices() {
		return invoices;
	}
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}
	
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
	
	//------------------------------ variabile customer_type
	private String customer_type;
	public String getCustomer_type() {
		return customer_type;
	}
	public void setCustomer_type(String customer_type) {
		this.customer_type = customer_type;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {
				
				String q = "";
				
				if(SessionSupport.checkSessionLevel("admin|super")) {

						q = "SELECT i FROM Invoice i INNER JOIN i.year y";
						
						if(getId_customer() != null) {
							q += " INNER JOIN i.customer c";
						} 
						
						q += " WHERE y.id = :id_year AND i.invoice_type = '" + getCustomer_type() + "'";
						
						if(getId_customer() != null) {
							q += " AND i.customer = c AND c.id = " + getId_customer();
						}
						
						if(getDate_from() != null) {
							q += " AND DATE(i.invoice_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
						}
						
						if(getDate_to() != null) {
							q += " AND DATE(i.invoice_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
						}
						
						q += " ORDER BY i.invoice_date DESC, i.number DESC";
					
						this.invoices = (List<Invoice>) sx.createQuery(q).setParameter("id_year", SessionSupport.getYear().getId()).list();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					
					q = "SELECT i FROM Invoice i INNER JOIN i.year y INNER JOIN i.admin a";
					
					if(getId_customer() != null) {
						q += " INNER JOIN i.customer c";
					} 
					
					q += " WHERE y.id = :id_year AND i.invoice_type = '" + getCustomer_type() + "' AND a.id = :id_admin";
					
					if(getId_customer() != null) {
						q += " AND i.customer = c AND c.id = " + getId_customer();
					}
					
					if(getDate_from() != null) {
						q += " AND DATE(i.invoice_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
					}
					
					if(getDate_to() != null) {
						q += " AND DATE(i.invoice_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
					}
					
					q += " ORDER BY i.invoice_date DESC, i.number DESC";
					System.out.println(q);
					this.invoices = (List<Invoice>) sx.createQuery(q).setParameter("id_year", SessionSupport.getYear().getId()).setParameter("id_admin", id_admin).list();
				}
				
				createPagination(this.invoices.size());
				
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
