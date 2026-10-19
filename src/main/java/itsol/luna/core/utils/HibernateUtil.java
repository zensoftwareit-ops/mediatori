package itsol.luna.core.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import itsol.luna.core.listeners.StartStopListener;
import itsol.luna.core.support.LogSupport;
 
public class HibernateUtil {
 
    private static final SessionFactory sessionFactory = buildSessionFactory();
 
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
        	
        	InputStream input = StartStopListener.class.getClassLoader().getResourceAsStream("app.properties");
        	Properties app = new Properties();
            app.load(input);
            
        	Properties HBR_PROPS = new Properties(); 
        	HBR_PROPS.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        	
        	
        	switch(app.getProperty("status")) { 
        	case "devel":
        		System.out.println(app.getProperty("db_hostname_devel") + app.getProperty("db_schema_devel") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		HBR_PROPS.put("hibernate.connection.url", app.getProperty("db_hostname_devel") + app.getProperty("db_schema_devel") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		HBR_PROPS.put("hibernate.connection.username", app.getProperty("db_username_devel"));
            	HBR_PROPS.put("hibernate.connection.password", app.getProperty("db_password_devel"));
        		break;
        	case "test":
        		HBR_PROPS.put("hibernate.connection.url", app.getProperty("db_hostname_test") + app.getProperty("db_schema_test") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		HBR_PROPS.put("hibernate.connection.username", app.getProperty("db_username_test"));
            	HBR_PROPS.put("hibernate.connection.password", app.getProperty("db_password_test"));
        		break;
        	case "prod":
        		HBR_PROPS.put("hibernate.connection.url", app.getProperty("db_hostname_prod") + app.getProperty("db_schema_prod") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        		HBR_PROPS.put("hibernate.connection.username", app.getProperty("db_username_prod"));
            	HBR_PROPS.put("hibernate.connection.password", app.getProperty("db_password_prod"));
        		break;
        	}

        	HBR_PROPS.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        	
        	HBR_PROPS.put("hibernate.connection.CharSet", "utf8");
        	HBR_PROPS.put("hibernate.connection.characterEncoding", "utf8");
        	HBR_PROPS.put("hibernate.connection.useUnicode", "utf8");
        	
        	HBR_PROPS.put("hibernate.connection.autocommit", "true");
        	
        	HBR_PROPS.put("hibernate.c3p0.min_size", "5");
        	HBR_PROPS.put("hibernate.c3p0.max_size", "20");
        	HBR_PROPS.put("hibernate.c3p0.acquire_increment", "5");
        	HBR_PROPS.put("hibernate.c3p0.idle_test_period", "1800");
        	HBR_PROPS.put("hibernate.c3p0.timeout", "1800");
        	HBR_PROPS.put("hibernate.c3p0.max_statements", "50");
        	HBR_PROPS.put("hibernate.current_session_context_class", "thread");
        	HBR_PROPS.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
        	HBR_PROPS.put("hibernate.show_sql", "false");
        	HBR_PROPS.put("javax.persistence.validation.mode", "none");
        	
        	Configuration cfg = new Configuration().configure();
        	cfg.setProperties(HBR_PROPS);
            return cfg.buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
 
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static boolean saveOrUpdate(Object o) {
    	
    	Session sx = sessionFactory.openSession();
    	boolean result = false;
    	

    	
    	try {
    		sx.beginTransaction();
    		sx.saveOrUpdate(o);
    		sx.getTransaction().commit();
    		result = true;
    	} catch(Exception ex) {
    		sx.getTransaction().rollback();
    		ex.printStackTrace();
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.SEVERE);
    	} finally {
    		sx.close();
    	}
    	
    	return result;
    }
    
    public static boolean delete(Object o) {
    	Session sx = sessionFactory.openSession();
    	boolean result = false;
    	
    	try {
    		sx.beginTransaction();
    		sx.delete(o);
    		sx.getTransaction().commit();
    		result = true;
    	} catch(Exception ex) {
    		sx.getTransaction().rollback();
    		ex.printStackTrace();
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.SEVERE);
    	} finally {
    		sx.close();
    	}
    	
    	return result;
    }
    

    
    public static void close() {
    	sessionFactory.close();
    }
 
}