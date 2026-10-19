package itsol.luna.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.utils.HibernateUtil;
import itsol.luna.core.beans.ContractTemplate;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Payment;
import itsol.luna.core.beans.Product;

public class MySupport {

	@SuppressWarnings("unchecked")
	public static List<Product> getProducts() {
		
		Session sx = HibernateUtil.getSessionFactory().openSession();
		List<Product> products = new ArrayList<Product>(0);
		
		try {
			if(SessionSupport.checkSessionLevel("admin|super")) {
				products = (List<Product>) sx.createQuery("FROM Product ORDER BY name").list();
			} else {
				Long id_admin = (Long)SessionSupport.getAdmin().getId();
				products = (List<Product>) sx.createQuery("FROM Product WHERE id_admin = :id_admin ORDER BY name").setParameter("id_admin", id_admin).list();
			}	
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log("MySupport - Si è verificato un errore durante l'estrazione della lista prodotti.", Level.INFO);
			LogSupport.log("MySupport - " + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}

		return products;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Customer> getCustomers() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		List<Customer> customers = new ArrayList<Customer>(0);
		
		try {
			if(SessionSupport.checkSessionLevel("admin|super")) {
				customers = (List<Customer>) sx.createQuery("FROM Customer").list();
			} else {
				Long id_admin = (Long)SessionSupport.getAdmin().getId();
				customers = (List<Customer>) sx.createQuery("FROM Customer WHERE id_admin = :id_admin").setParameter("id_admin", id_admin).list();	 
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log("MySupport - Si è verificato un errore durante l'estrazione della lista clienti.", Level.INFO);
			LogSupport.log("MySupport - " + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return customers;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Payment> getPayments() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		List<Payment> payments = new ArrayList<Payment>(0);
		
		try {
			if(SessionSupport.checkSessionLevel("admin|super")) {
				payments = (List<Payment>) sx.createQuery("FROM Payment").list();
			} else {
				Long id_admin = (Long)SessionSupport.getAdmin().getId();
				payments = (List<Payment>) sx.createQuery("FROM Payment WHERE id_admin = :id_admin").setParameter("id_admin", id_admin).list();	 
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log("MySupport - Si è verificato un errore durante l'estrazione della lista metodi di pagamento.", Level.INFO);
			LogSupport.log("MySupport - " + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return payments;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ContractTemplate> getContract_templates() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		List<ContractTemplate> contract_templates = new ArrayList<ContractTemplate>(0);
		
		try {
			if(SessionSupport.checkSessionLevel("admin|super")) {
				contract_templates = (List<ContractTemplate>) sx.createQuery("FROM ContractTemplate").list();
			} else {
				Long id_admin = (Long)SessionSupport.getAdmin().getId();
				contract_templates = (List<ContractTemplate>) sx.createQuery("FROM ContractTemplate WHERE id_admin = :id_admin").setParameter("id_admin", id_admin).list();	 
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log("MySupport - Si è verificato un errore durante l'estrazione della lista dei template contratto.", Level.INFO);
			LogSupport.log("MySupport - " + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return contract_templates;
	}
	


	
}
