package itsol.luna.core.listeners;

import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.Session;

import itsol.luna.core.utils.HibernateUtil;
import itsol.luna.core.beans.Config;

/**
 * Application Lifecycle Listener implementation class StartStopListener
 *
 */
@WebListener
public class StartStopListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public StartStopListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent evt)  { 
         // TODO Auto-generated method stub
    	
    	HibernateUtil.close();
    	
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (Exception ex) {
                System.err.println(ex.getStackTrace());
            }

        }
        
        //ogSupport.log("Stopping web application --------> moonwalker_risicoltori", Level.INFO);
    	
    	System.out.println("Stopping web application.");
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */

	public void contextInitialized(ServletContextEvent evt)  { 
         // TODO Auto-generated method stub
    	
    	try {
    		
    		TimeZone.setDefault(TimeZone.getTimeZone("Europe/GMT"));
    		Locale.setDefault(Locale.ITALY);
    		
    		Class.forName("com.mysql.jdbc.Driver");
    		
    		InputStream input = StartStopListener.class.getClassLoader().getResourceAsStream("app.properties");
    		Properties app = new Properties();

            app.load(input);
    		
            evt.getServletContext().setAttribute("app", app);
            
    		Session sx = HibernateUtil.getSessionFactory().openSession();
    		
    		Config config = (Config) sx.createQuery("FROM Config").uniqueResult();   		
//    		List<Product> products = (List<Product>)sx.createQuery("FROM Product").list();
//    		List<Customer> customers = (List<Customer>)sx.createQuery("FROM Customer").list();
//    		List<Payment> payments = (List<Payment>)sx.createQuery("FROM Payment").list();
    		
    		sx.close();
        
    		evt.getServletContext().setAttribute("config", config);
//    		evt.getServletContext().setAttribute("products", products);
//    		evt.getServletContext().setAttribute("customers", customers);
//    		evt.getServletContext().setAttribute("payments", payments);
    		
    		//LogSupport.log("Starting web application --------> moonwalker_risicoltori", Level.INFO);
        
    		System.out.println("Starting web application.");
        
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}

    }
	
}
