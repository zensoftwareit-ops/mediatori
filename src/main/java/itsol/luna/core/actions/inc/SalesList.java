package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.FastSale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SalesList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7954064270197358289L;
	
	//------------------------------ variabile sales
	private List<FastSale> sales = new ArrayList<FastSale>(0);
	public List<FastSale> getSales() {
		return sales;
	}
	public void setSales(List<FastSale> sales) {
		this.sales = sales;
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
	
	//------------------------------ variabile number
	private Integer number;
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				String q = "";
				

				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					
					q += "SELECT s FROM FastSale s INNER JOIN s.year y";
					
					if(getId_customer() != null) {
						q += " INNER JOIN s.seller c INNER JOIN s.buyer cc";
					} 
					
					q += " WHERE y.id = :id_year";
					
					if(getId_customer() != null) {
						q += " AND ((s.seller = c AND c.id = " + getId_customer() + ") OR (s.buyer = cc AND cc.id = " + getId_customer() + "))";
					}
					
					if(getDate_from() != null) {
						q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
					}
					
					if(getDate_to() != null) {
						q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
					}
					
					if(getNumber() != null) {
						q += " AND s.number = " + getNumber();
					}
					
					q += " ORDER BY s.number DESC";
					

					this.sales = (List<FastSale>) sx.createQuery(q).setParameter("id_year", SessionSupport.getSowingYear().getId()).list();
					
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();

					q += "SELECT s FROM FastSale s INNER JOIN s.year y INNER JOIN s.admin a";
					
					if(getId_customer() != null) {
						q += " INNER JOIN s.seller c INNER JOIN s.buyer cc";
					} 
					
					q += " WHERE y.id = :id_year AND a.id = :id_admin";
					
					if(getId_customer() != null) {
						q += " AND ((s.seller = c AND c.id = " + getId_customer() + ") OR (s.buyer = cc AND cc.id = " + getId_customer() + "))";
					}
					
					if(getDate_from() != null) {
						q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
					}
					
					if(getDate_to() != null) {
						q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
					}
					
					if(getNumber() != null) {
						q += " AND s.number = " + getNumber();
					}
					
					q += " ORDER BY s.number DESC";
					
					this.sales = (List<FastSale>) sx.createQuery(q).setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).list();

				}
				
				createPagination(this.sales.size());
			
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
