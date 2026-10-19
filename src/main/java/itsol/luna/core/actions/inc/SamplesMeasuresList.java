package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SamplesMeasuresList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2360939926560472009L;
	
	private Long id_customer;
	public Long getId_customer() {
		return id_customer;
	}
	public void setId_customer(Long id_customer) {
		this.id_customer = id_customer;
	}
	
	private Long id_product;
	public Long getId_product() {
		return id_product;
	}
	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}
	
	private String orderby;
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	
	private List<YieldMeasure> measures = new ArrayList<YieldMeasure>(0);
	public List<YieldMeasure> getMeasures() {
		return measures;
	}
	public void setMeasures(List<YieldMeasure> measures) {
		this.measures = measures;
	}
	
	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	private Double grains_average = 0.00;
	private Double global_average = 0.00;
	private Double defects_average_1 = 0.00;
	private Double defects_average_2 = 0.00;
	

	public Double getDefects_average_1() {
		return defects_average_1;
	}
	public void setDefects_average_1(Double defects_average_1) {
		this.defects_average_1 = defects_average_1;
	}
	public Double getDefects_average_2() {
		return defects_average_2;
	}
	public void setDefects_average_2(Double defects_average_2) {
		this.defects_average_2 = defects_average_2;
	}
	public Double getGrains_average() {
		return grains_average;
	}
	public void setGrains_average(Double grains_average) {
		this.grains_average = grains_average;
	}
	public Double getGlobal_average() {
		return global_average;
	}
	public void setGlobal_average(Double global_average) {
		this.global_average = global_average;
	}
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {
				
				try {
					this.customer = (Customer)sx.createQuery("FROM Customer WHERE id = :id_customer").setParameter("id_customer", getId_customer()).uniqueResult();
					this.product = (Product)sx.createQuery("FROM Product WHERE id = :id_product").setParameter("id_product", getId_product()).uniqueResult();
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca di cliente e/o prodotto.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} 
		
				if(SessionSupport.checkSessionLevel("admin|super")) {
					if(getOrderby() != null) {
						if(getOrderby().equals("customer")) {
							this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND c.id = :id_customer AND p.id = :id_product ORDER BY c.internal_code, ym.id, p.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
						} else {
							//this.measures = (List<YieldMeasure>)sx.createQuery("FROM YieldMeasure WHERE id_customer = :id_customer AND id_product = :id_product ORDER BY id").setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
							this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND c.id = :id_customer AND p.id = :id_product ORDER BY c.internal_code, ym.id, p.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
						}
					} else {
						//this.measures = (List<YieldMeasure>)sx.createQuery("FROM YieldMeasure WHERE id_customer = :id_customer AND id_product = :id_product ORDER BY id").setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
						this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND c.id = :id_customer AND p.id = :id_product ORDER BY c.internal_code, ym.id, p.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					}
					
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					
					String hql="";
					
					hql += "SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL)";
					
					if(getDate_from() != null) {
						hql += " AND DATE(ym.insert_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
					}
					
					if(getDate_to() != null) {
						hql += " AND DATE(ym.insert_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
					}
					
					hql += "ORDER BY c.internal_code, p.id, ym.id";

					this.measures = (List<YieldMeasure>)sx.createQuery(hql).setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					
//					if(getOrderby() != null) {
//						if(getOrderby().equals("customer")) {
//							this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL) ORDER BY c.internal_code, p.id, ym.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
//						} else {
//							this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL) ORDER BY c.internal_code, p.id, ym.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
//						}
//					} else {
//						this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL) ORDER BY c.internal_code, p.id, ym.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
//					}
					
				}
				
				try {
					if(this.measures.size() > 0) {

						
						Iterator<YieldMeasure> i = this.measures.iterator();
						while(i.hasNext()) {
							YieldMeasure ym = i.next();
							

							
							this.grains_average = this.grains_average + ym.getGrains_yield();
							this.global_average = this.global_average + ym.getGlobal_yield();
							this.defects_average_1 = this.defects_average_1 + ym.getDefects_1();
							this.defects_average_2 = this.defects_average_2 + ym.getDefects_2();
						}
						
						this.grains_average = this.grains_average / (double)this.measures.size();
						this.global_average = this.global_average / (double)this.measures.size();
						this.defects_average_1 = this.defects_average_1 / (double)this.measures.size();
						this.defects_average_2 = this.defects_average_2 / (double)this.measures.size();
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il calcolo delle medie.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}


				
				createPagination(this.measures.size());
				
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
