package itsol.luna.core.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SalePDFSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleCreatePDF3 extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -411642756982941826L;
	
	private Long id_sale;
	private Sale sale;
	
	private List<String> files = new ArrayList<String>(0);
	
	public String execute() {

		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id = :id_sale").setParameter("id_sale", getId_sale()).uniqueResult();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id_admin = :id_admin AND id = :id_sale").setParameter("id_admin", id_admin).setParameter("id_sale", getId_sale()).uniqueResult();
				}
				
				//
				
				if(this.sale != null) {
						
					
						String filename = "00" + this.sale.getNumber().toString() + " - " + this.sale.getSeller().getBusiness_name() + " - " + this.sale.getBuyer().getBusiness_name() + " - " + new SimpleDateFormat("dd-MM-yyyy").format(this.sale.getSale_date()) + ".pdf";
						filename = filename.replaceAll("'", "");
						
						SalePDFSupport.create3(filename, this.sale);
						
						this.files.add(filename);


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
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			sx.close();
		}
		
		return result;
	}

	
	public Long getId_sale() {
		return id_sale;
	}
	public void setId_sale(Long id_sale) {
		this.id_sale = id_sale;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public List<String> getFiles() {
		return files;
	}
	
	
	


}
