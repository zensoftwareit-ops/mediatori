package itsol.luna.core.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.google.gson.Gson;

import itsol.luna.core.utils.HibernateUtil;
import itsol.luna.core.beans.Customer;

/**
 * Servlet implementation class GetProductJSON
 */
@WebServlet("/GetCustomerJSON")
public class GetCustomerJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetCustomerJSON() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Long id_customer = Long.parseLong(request.getParameter("id"));
		
		Session sx = HibernateUtil.getSessionFactory().openSession();
		Customer customer = (Customer) sx.createQuery("FROM Customer WHERE id = :id_customer")
				.setParameter("id_customer", id_customer)
				.uniqueResult();
		sx.close();
		
		customer.setAdmin(null);
		
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		response.getWriter().append(new Gson().toJson(customer));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
