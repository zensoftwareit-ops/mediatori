package itsol.luna.core.support;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogSupport {

	
	public static void log(String message, Level level) {  

	    try {  
	    	
		    Logger logger = Logger.getLogger("moonwalker");  
		    FileHandler log = new FileHandler(ContextSupport.getLogPath() + "default.log", true);  
		    
	        logger.addHandler(log);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        log.setFormatter(formatter);  
	        logger.log(level, message); 
	        
	        log.close();


	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } 

	}
}
