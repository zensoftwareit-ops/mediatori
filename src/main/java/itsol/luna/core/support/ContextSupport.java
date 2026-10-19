package itsol.luna.core.support;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;

import itsol.luna.core.beans.Config;
import itsol.luna.core.beans.SowingYear;
import itsol.luna.core.beans.Year;
import itsol.luna.core.beans.Domain;



public class ContextSupport {
	
	
	public static Properties app;
	
	static {
		try {
			app = (Properties) ServletActionContext.getServletContext().getAttribute("app");
		} catch(Exception ex) {
    		InputStream input = ContextSupport.class.getClassLoader().getResourceAsStream("app.properties");
    		app = new Properties();
            try {
				app.load(input);
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}
	}
	
	
	public static String getVersion() {
		return app.getProperty("v");
	}
	
	public static String getBaseUrl() {
		
		String base_url = new String();
		
//		switch(app.getProperty("status")) {
//		case "devel":
//			base_url = app.getProperty("url_devel");
//			break;
//		case "test":
//			base_url = app.getProperty("url_test");
//			break;
//		case "prod":
//			base_url = app.getProperty("url_prod");
//			break;
//		}
		
		base_url = ((Domain) ServletActionContext.getRequest().getSession().getAttribute("domain")).getUrl();
		
		return base_url;
		
		
//		if(Long.parseLong(app.getProperty("published")) == 1) {
//			return app.getProperty("url_prod");
//		} else {
//			return app.getProperty("url_local");
//		}
	}
	
	public static Boolean isDebug() {
		if(Long.parseLong(app.getProperty("debug")) == 1) {
			return true;
		} else {
			return false;
		}
	}
	
//	public static Boolean isPublished() {
//		if(Long.parseLong(app.getProperty("published")) == 1) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
	public static String getUploadPath() {
		
		String upload_path = new String();
		
//		switch(app.getProperty("status")) {
//		case "devel":
//			upload_path = app.getProperty("upload_path_devel");
//			break;
//		case "test":
//			upload_path = app.getProperty("upload_path_test");
//			break;
//		case "prod":
//			upload_path = app.getProperty("upload_path_prod");
//			break;
//		}
		
		upload_path = ((Domain) ServletActionContext.getRequest().getSession().getAttribute("domain")).getRes_path() + "uploads/";
		
		return upload_path;
		
//		if(Long.parseLong(app.getProperty("published")) == 1) {
//			return app.getProperty("upload_path_prod");
//		} else {
//			return app.getProperty("upload_path_devel");
//		}
	}
	
	public static String getLogPath() {
		
		String log_path = new String();
		
//		switch(app.getProperty("status")) {
//		case "devel":
//			log_path = app.getProperty("log_path_devel");
//			break;
//		case "test":
//			log_path = app.getProperty("log_path_test");
//			break;
//		case "prod":
//			log_path = app.getProperty("log_path_prod");
//			break;
//		}
		
		log_path = ((Domain) ServletActionContext.getRequest().getSession().getAttribute("domain")).getRes_path() + "logs/";
		
		return log_path;
		
//		if(Long.parseLong(app.getProperty("published")) == 1) {
//			return app.getProperty("log_path_prod");
//		} else {
//			return app.getProperty("log_path_devel");
//		}
	}
	
	public static Config getConfig() {
		return (Config) ServletActionContext.getServletContext().getAttribute("config");
	}
	
	public static void setConfig(Config config) {
		ServletActionContext.getServletContext().setAttribute("config", config);
	}
	
	public static Year getYear() {
		return ((Config) ServletActionContext.getServletContext().getAttribute("config")).getYear();
	}
	
	public static String thisYear() {
		return ((Config) ServletActionContext.getServletContext().getAttribute("config")).getYear().getYear();
	}
	
	public static SowingYear getSowingYear() {
		return ((Config) ServletActionContext.getServletContext().getAttribute("config")).getSowing_year();
	}
	
	public static void setAttribute(Object o, String key) {
		ServletActionContext.getServletContext().setAttribute(key, o);
	}
	
//	@SuppressWarnings("unchecked")
//	public static List<Product> getProducts() {
//		return (List<Product>) ServletActionContext.getServletContext().getAttribute("products");
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<Customer> getCustomers() {
//		return (List<Customer>) ServletActionContext.getServletContext().getAttribute("customers");
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<Payment> getPayments() {
//		return (List<Payment>) ServletActionContext.getServletContext().getAttribute("payments");
//	}
	
	public static HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
	
	public static HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
	
	public static String getActionName() {
		return ActionContext.getContext().getName();
	}
	

	
	
}
