package itsol.luna.core.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;

/**
 * Servlet implementation class GetImg
 */
@WebServlet("/GetImg")
public class GetImg extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetImg() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
String src = (String)request.getParameter("src");
		
		if(src != null) {
			if(src.length() > 0) {
				
				try {
					response.setContentType("image");  
				    ServletOutputStream out;  
				    out = response.getOutputStream();  
				    FileInputStream fin = new FileInputStream(src);  
				      
				    BufferedInputStream bin = new BufferedInputStream(fin);  
				    BufferedOutputStream bout = new BufferedOutputStream(out);  
				    int ch =0;  
				    while((ch=bin.read())!=-1)  
				    {  
				    bout.write(ch);  
				    }  
				      
				    bin.close();  
				    fin.close();  
				    bout.close();  
				    out.close();  
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
					LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
