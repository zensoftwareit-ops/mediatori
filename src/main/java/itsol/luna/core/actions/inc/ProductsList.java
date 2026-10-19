package itsol.luna.core.actions.inc;

import java.util.List;
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

public class ProductsList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 21249284521947954L;
	
	//------------------------------ variabile products
	private List<Product> products;
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					if(getS() != null && !getS().isEmpty()) {
						this.products = (List<Product>) sx.createQuery("FROM Product WHERE name LIKE '%" + getS() + "%' OR internal_code LIKE '%" + getS() + "%' ORDER BY name").list();
					} else {
						this.products = (List<Product>) sx.createQuery("FROM Product ORDER BY name").list();
					}
					
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					
					if(getS() != null && !getS().isEmpty()) {
						this.products = (List<Product>) sx.createQuery("SELECT p FROM Product p INNER JOIN p.admin a WHERE a.id = :id_admin AND (p.name LIKE '%" + getS() + "%' OR p.internal_code LIKE '%" + getS() + "%') ORDER BY p.name").setParameter("id_admin", id_admin).list();
					} else {
						this.products = (List<Product>) sx.createQuery("SELECT p FROM Product p INNER JOIN p.admin a WHERE a.id = :id_admin ORDER BY p.name").setParameter("id_admin", id_admin).list();
					}
					
				}	
				
				createPagination(this.products.size());

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
