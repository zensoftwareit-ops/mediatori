package itsol.luna.core.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MaintenanceMode implements Filter {

    /**
     * Default constructor. 
     */
    public MaintenanceMode() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		String path = ((HttpServletRequest) request).getRequestURI();
		
		
		if (path.contains("/html/")) {
			chain.doFilter(request, response);
		} else {
			ServletContext ctx = ((HttpServletRequest)request).getServletContext();
			
			
			
			if(ctx != null) {
				if(ctx.getAttribute("maintenance") != null) {
					if((boolean)ctx.getAttribute("maintenance")) {
						((HttpServletResponse)response).sendRedirect("html/maintenance.html");
					}
					
				}
			}

			
			

			// pass the request along the filter chain
			chain.doFilter(request, response);
		}
		

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
