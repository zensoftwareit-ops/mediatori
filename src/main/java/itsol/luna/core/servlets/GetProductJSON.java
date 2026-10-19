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
import itsol.luna.core.beans.Product;

/**
 * Servlet implementation class GetProductJSON
 */
@WebServlet("/GetProductJSON")
public class GetProductJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProductJSON() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Long id_product = Long.parseLong(request.getParameter("id"));
		
		Session sx = HibernateUtil.getSessionFactory().openSession();
		Product product = (Product) sx.createQuery("FROM Product WHERE id = :id_product")
				.setParameter("id_product", id_product)
				.uniqueResult();
		sx.close();
		
		product.setAdmin(null);
		
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		response.getWriter().append(new Gson().toJson(product));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
