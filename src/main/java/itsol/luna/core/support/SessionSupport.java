package itsol.luna.core.support;

import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;

import itsol.luna.core.beans.Admin;
import itsol.luna.core.beans.ContractTemplate;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Domain;
import itsol.luna.core.beans.Payment;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.SowingYear;
import itsol.luna.core.beans.Year;


public class SessionSupport {

	
	public static Boolean checkSession() {
		
		try {
			HttpSession session = getSession();
			
			if(session != null) {
				if(session.getAttribute("logged_on") != null && (boolean)session.getAttribute("logged_on") == true) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			return false;
		}

	}
	
	public static Boolean checkSessionLevel(String level) {
		if(checkSession()) {
			HttpSession session = getSession();
			return ((Admin) session.getAttribute("admin")).getLevel().matches(level);
		} else {
			return false;
		}
	}
	
	public static Admin getAdmin() {
		if(checkSession()) {
			HttpSession session = getSession();
			return (Admin) session.getAttribute("admin");
		} else {
			return null;
		}
	}
	
	public static HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}
	
	public static void setYear(Year year) {
		ServletActionContext.getRequest().getSession().setAttribute("year", year);
	}
	
	public static Year getYear() {
		if(checkSession()) {
			HttpSession session = getSession();
			return (Year) session.getAttribute("year");
		} else {
			return null;
		}
	}
	
	public static void setSowingYear(SowingYear year) {
		ServletActionContext.getRequest().getSession().setAttribute("sowing_year", year);
	}
	
	public static SowingYear getSowingYear() {
		if(checkSession()) {
			HttpSession session = getSession();
			return (SowingYear) session.getAttribute("sowing_year");
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Product> getProducts() {
		HttpSession session = getSession();
		return (List<Product>) session.getAttribute("products");
	}
	
	public static void setProducts(List<Product> products) {
		ServletActionContext.getRequest().getSession().setAttribute("products", products);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Customer> getCustomers() {
		HttpSession session = getSession();
		return (List<Customer>) session.getAttribute("customers");
	}
	
	public static void setCustomers(List<Customer> customers) {
		ServletActionContext.getRequest().getSession().setAttribute("customers", customers);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Payment> getPayments() {
		HttpSession session = getSession();
		return (List<Payment>) session.getAttribute("payments");
	}
	
	public static void setPayments(List<Payment> payments) {
		ServletActionContext.getRequest().getSession().setAttribute("payments", payments);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ContractTemplate> getContract_templates() {
		HttpSession session = getSession();
		return (List<ContractTemplate>) session.getAttribute("contract_templates");
	}
	
	public static void setContract_templates(List<ContractTemplate> contract_templates) {
		ServletActionContext.getRequest().getSession().setAttribute("contract_templates", contract_templates);
	}
	
	public static Domain getDomain() {
		HttpSession session = getSession();
		return (Domain) session.getAttribute("domain");
	}
	
	public static void setDomain(Domain domain) {
		ServletActionContext.getRequest().getSession().setAttribute("domain", domain);
	}

}
