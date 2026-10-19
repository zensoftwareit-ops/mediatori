package itsol.luna.core.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.apache.commons.lang3.exception.ExceptionUtils;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;

public class SuperShowLog extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8846012832234601428L;
	
	private String excp;
	private String info;
	private String error;
	
	public String execute() {
		
		String result = new String();
		
		
		try {
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("super")) {
					
					   FileInputStream fstream = new FileInputStream(ContextSupport.getLogPath() + "default.log");
					   BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
					   String strLine;

					   while ((strLine = br.readLine()) != null) {
						   if(strLine != null) {
							   info = strLine + "\n" + info;
						   }
						   
					   }
					   fstream.close();
					   
					result = SUCCESS;
				} else {
					result = SUCCESS_NOGRANTS;
				}

			} else {
				result = SUCCESS_NOLOGIN;
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				this.excp = ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex);
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			
		}
		
		return result;
		
	}

	public String getInfo() {
		return info;
	}

	public String getError() {
		return error;
	}
	
	public String getExcp() {
		return excp;
	}

}
