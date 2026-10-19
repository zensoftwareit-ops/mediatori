package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class DeadlinesList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1779535902728365971L;
	
	//------------------------------ variabile sales
	private List<Sale> sales = new ArrayList<Sale>(0);
	public List<Sale> getSales() {
		return sales;
	}
	public void setSales(List<Sale> sales) {
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
			
			String q = "";
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					
					switch(getCustomer_type()) {
						case "seller":
							
							q = "SELECT DISTINCT s FROM Sale s";
							
							if(getId_customer() != null) {
								q += " INNER JOIN s.seller b";
							} 
							
							if(getDate_from() != null || getDate_to() != null) {
								q += " INNER JOIN s.installments inst";
							}
							
							q += " WHERE";
							
							if(getId_customer() != null) {
								q += " AND s.seller = b AND b.id = " + getId_customer();
							}
							
							if(getDate_from() != null) {
								q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
							}
							
							if(getDate_to() != null) {
								q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
							}
							
							q += " ORDER BY s.number DESC";
							
							break;
						case "buyer":
							
							q = "SELECT DISTINCT s FROM Sale s";
							
							if(getId_customer() != null) {
								q += " INNER JOIN s.buyer b";
							} 
							
							if(getDate_from() != null || getDate_to() != null) {
								q += " INNER JOIN s.installments inst";
							}
							
							q += " WHERE";
							
							if(getId_customer() != null) {
								q += " AND s.buyer = b AND b.id = " + getId_customer();
							}
							
							if(getDate_from() != null) {
								q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
							}
							
							if(getDate_to() != null) {
								q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
							}
							
							q += " ORDER BY s.number DESC";
							
							break;
					}
					
					this.sales = (List<Sale>) sx.createQuery(q).list();
					

				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					
					switch(getCustomer_type()) {
					case "seller":
						
						q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a";
						
						if(getId_customer() != null) {
							q += " INNER JOIN s.seller b";
						} 
						
						if(getDate_from() != null || getDate_to() != null) {
							q += " INNER JOIN s.installments inst";
						}
						
						q += " WHERE a.id = :id_admin";
						
						if(getId_customer() != null) {
							q += " AND s.seller = b AND b.id = " + getId_customer();
						}
						
						if(getDate_from() != null) {
							q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
						}
						
						if(getDate_to() != null) {
							q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
						}
						
						q += " ORDER BY s.number DESC";
						
						break;
					case "buyer":
						
						q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a";
						
						if(getId_customer() != null) {
							q += " INNER JOIN s.buyer b";
						} 
						
						if(getDate_from() != null || getDate_to() != null) {
							q += " INNER JOIN s.installments inst";
						}
						
						q += " WHERE a.id = :id_admin";
						
						if(getId_customer() != null) {
							q += " AND s.buyer = b AND b.id = " + getId_customer();
						}
						
						if(getDate_from() != null) {
							q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
						}
						
						if(getDate_to() != null) {
							q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
						}
						
						q += " ORDER BY s.number DESC";
						
						
						
						break;
					}

					if(getId_customer() != null || getDate_from() != null || getDate_to() != null) {
						this.sales = (List<Sale>) sx.createQuery(q).setParameter("id_admin", id_admin).list();
					}
					

					
					
				}
				
				createPagination(this.sales.size());
			
				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				//ex.printStackTrace();
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
