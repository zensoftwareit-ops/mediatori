package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class ProductDelete extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7639874689794667245L;
	
	/**
	 * variabile id_product
	 */
	private Long id_product;
	public Long getId_product() {
		return id_product;
	}
	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}
	
	/**
	 * variabile product
	 */
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public String execute() {
		String result = new String();
		
		try {
			if(SessionSupport.checkSession()) {
				
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				/**
				 * ricerca oggetto product
				 */
				try {
					this.product = (Product) sx.createQuery("FROM Product WHERE id = :id_product")
							.setParameter("id_product", getId_product())
							.uniqueResult();
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} finally {
					sx.close();
				}
				
				/**
				 * eliminazione se non nullo
				 */
				if(this.product != null) {
					Long id = this.product.getId();
					HibernateUtil.delete(this.product);
					LogSupport.log(op() + "E' stato eliminato il prodotto " + id + ".", Level.INFO);
					setResp("E' stato eliminato il prodotto " + id + ".");
					setResp_status_ok();
				} else {
					LogSupport.log(op() + "Nessuna eliminazione.", Level.INFO);
					setResp("Nessuna eliminazione.");
					setResp_status_ko();
				}
				
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
				setResp("Si è verificato un errore.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante l'eliminazione.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return result;
	}

}
