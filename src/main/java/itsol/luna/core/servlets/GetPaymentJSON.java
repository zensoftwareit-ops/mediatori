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
import itsol.luna.core.beans.Payment;

/**
 * Servlet implementation class GetPaymentJSON
 */
@WebServlet("/GetPaymentJSON")
public class GetPaymentJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPaymentJSON() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Long id_payment = Long.parseLong(request.getParameter("id"));
		
		Session sx = HibernateUtil.getSessionFactory().openSession();
		Payment payment = (Payment) sx.createQuery("FROM Payment WHERE id = :id_payment")
				.setParameter("id_payment", id_payment)
				.uniqueResult();
		sx.close();
		
		payment.setAdmin(null);
		payment.setPinstallments(null);
		
		response.setCharacterEncoding("UTF8");
		response.setContentType("application/json");
		response.getWriter().append(new Gson().toJson(payment));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
