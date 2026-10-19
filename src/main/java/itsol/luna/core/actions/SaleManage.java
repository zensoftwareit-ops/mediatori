package itsol.luna.core.actions;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.ContractTemplate;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Payment;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleManage extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2476326217542821785L;
	
	
	//------------------------------ variabile id_sale
	private Long id_sale;
	public Long getId_sale() {
		return id_sale;
	}
	public void setId_sale(Long id_sale) {
		this.id_sale = id_sale;
	}
	
	
	//------------------------------ variabile sale
	private Sale sale;
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
	public Date getExp_date_001() {
		return sale.getInstallments().get(0).getExpire_date();
	}

	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {

				this.sale = (Sale) sx.createQuery("FROM Sale WHERE id = :id_sale")
						.setParameter("id_sale", getId_sale())
						.uniqueResult();

				if(getScreen() != null) {
					result = SUCCESS + "_" + getScreen();
				} else {
					result = SUCCESS;
				}
				
			} else {
				result = SUCCESS_NOLOGIN;
			}
		} catch(Exception ex) {		
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore durante la ricerca del record");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante la ricerca del record", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return result;
		
	}
	
	public List<Product> getProducts() {
		return SessionSupport.getProducts();
	}

	public List<Customer> getCustomers() {
		return SessionSupport.getCustomers();
	}
	
	public List<Payment> getPayments() {
		return SessionSupport.getPayments();
	}
	
	public List<ContractTemplate> getContract_templates() {
		return SessionSupport.getContract_templates();
	}


}
