package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;


import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CommissionsList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7071304874463101872L;
	
	//------------------------------ variabile sales
//		private List<Sale> sales = new ArrayList<Sale>(0);
//		public List<Sale> getSales() {
//			return sales;
//		}
//		public void setSales(List<Sale> sales) {
//			this.sales = sales;
//		}
	
	private List<SaleCommission> commissions = new ArrayList<SaleCommission>(0);
		
		public List<SaleCommission> getCommissions() {
		return commissions;
	}
	public void setCommissions(List<SaleCommission> commissions) {
		this.commissions = commissions;
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
		
		private String billed_status;

		public String getBilled_status() {
		return billed_status;
	}

	public void setBilled_status(String billed_status) {
		this.billed_status = billed_status;
	}
		

		public String execute() {

			String result = new String();
			Session sx = HibernateUtil.getSessionFactory().openSession();
			
			try {

				
				if(SessionSupport.checkSession()) {
					
					if(SessionSupport.checkSessionLevel("admin|super")) {
						

						

					} else {
						
						
						if(getId_customer() != null || getDate_from() != null || getDate_to() != null) {
							this.commissions = SaleCommission.getCommissions(getId_customer(), getCustomer_type(), getBilled_status(), getDate_from(), getDate_to());
						}
						

						
						
					}
					
					//createPagination(this.commissions.size());
				
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
