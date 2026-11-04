package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.Date;
import java.util.Calendar;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.Query;

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
	public Date getLabel_date() {
		return new Date();
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
		
				String hql = "SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year";
				boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
				if(filtraAdmin) hql += " AND ym.admin.id = :id_admin";
				if(getId_customer() != null) hql += " AND c.id = :id_customer";
				if(getId_product() != null) hql += " AND p.id = :id_product";
				if(getDate_from() != null) hql += " AND ym.insert_date >= :date_from";
				if(getDate_to() != null) hql += " AND ym.insert_date <= :date_to";
				hql += " ORDER BY c.internal_code, p.id, ym.id";
				Query query = sx.createQuery(hql).setParameter("id_year", SessionSupport.getSowingYear().getId());
				if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
				if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
				if(getId_product() != null) query.setParameter("id_product", getId_product());
				if(getDate_from() != null) query.setParameter("date_from", getDate_from());
				if(getDate_to() != null) query.setParameter("date_to", fineGiornata(getDate_to()));
				this.measures = (List<YieldMeasure>)query.list();
				
				try {
					if(this.measures.size() > 0) {
						int grainsCount = 0, globalCount = 0, defects1Count = 0, defects2Count = 0;
						Iterator<YieldMeasure> i = this.measures.iterator();
						while(i.hasNext()) {
							YieldMeasure ym = i.next();
							if(ym.getGrains_yield() != null) { this.grains_average += ym.getGrains_yield(); grainsCount++; }
							if(ym.getGlobal_yield() != null) { this.global_average += ym.getGlobal_yield(); globalCount++; }
							if(ym.getDefects_1() != null) { this.defects_average_1 += ym.getDefects_1(); defects1Count++; }
							if(ym.getDefects_2() != null) { this.defects_average_2 += ym.getDefects_2(); defects2Count++; }
						}
						if(grainsCount > 0) this.grains_average /= grainsCount;
						if(globalCount > 0) this.global_average /= globalCount;
						if(defects1Count > 0) this.defects_average_1 /= defects1Count;
						if(defects2Count > 0) this.defects_average_2 /= defects2Count;
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

	private Date fineGiornata(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

}
