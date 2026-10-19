package itsol.luna.core.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import itsol.luna.core.beans.Domain;
import itsol.luna.core.utils.HibernateUtil;

/**
 * Servlet Filter implementation class DomainValidation
 */

public class DomainValidation implements Filter {

    /**
     * Default constructor. 
     */
    public DomainValidation() {
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
			
		
		try {
			
			Domain domain = (Domain)((HttpServletRequest)request).getSession().getAttribute("domain");
			
			if(domain == null) {
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				try {
					String host = ((HttpServletRequest)request).getHeader("x-forwarded-host");
					
					if(host == null) {
						host = "localhost";
					} else if(host.isEmpty()) {
						host = "localhost";
					}
					
					domain = (Domain)sx.createQuery("FROM Domain WHERE domain = :domain").setParameter("domain", host).uniqueResult();

					
					if(domain != null) {
						((HttpServletRequest)request).getSession().setAttribute("domain", domain);
					} else {
						((HttpServletResponse)response).sendRedirect("html/invalid_domain.html");
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
					sx.close();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
