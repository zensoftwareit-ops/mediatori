package itsol.luna.core.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;


import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.ProductGroup;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class ProductManage extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5969208174472078329L;
	
	
	//------------------------------ variabile id_product
	private Long id_product;
	public Long getId_product() {
		return id_product;
	}
	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}
	
	
	//------------------------------ variabile product
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {

				this.product = (Product) sx.createQuery("FROM Product WHERE id = :id_product")
						.setParameter("id_product", getId_product())
						.uniqueResult();
				
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
				setResp("Si è verificato un errore durante la ricerca del record.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante la ricerca del record.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductGroup> getGroups() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		List<ProductGroup> groups = new ArrayList<ProductGroup>(0);
		
		try {
			groups = (List<ProductGroup>) sx.createQuery("FROM ProductGroup ORDER BY tag").list();
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log("MySupport - Si è verificato un errore durante l'estrazione della lista gruppi.", Level.INFO);
			LogSupport.log("MySupport - " + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return groups;
	}
	

}
