package itsol.luna.core.actions;

import java.io.OutputStream;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;
import itsol.luna.core.utils.LabelPdfBuilder;
import itsol.luna.core.utils.PdfLogoSupport;

public class AnalisiLabelCreatePDF extends MyActionSupport implements MyActionInterface {
	private static final long serialVersionUID = 1L;
	public String execute() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		try {
			if(!SessionSupport.checkSession()) {
				ContextSupport.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return null;
			}
			Customer customer = (Customer)restrictedQuery(sx, "Customer", getId_customer()).uniqueResult();
			Product product = (Product)restrictedQuery(sx, "Product", getId_product()).uniqueResult();
			if(customer == null || product == null) {
				ContextSupport.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			String logoPath = PdfLogoSupport.currentLogoPath();
			byte[] pdf = LabelPdfBuilder.analysisLabel(customer, product, logoPath);
			HttpServletResponse response = ContextSupport.getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "inline; filename=Etichetta-laboratorio.pdf");
			response.setContentLength(pdf.length);
			OutputStream os = response.getOutputStream();
			os.write(pdf);
			os.flush();
			os.close();
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) ex.printStackTrace();
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			try { if(!ContextSupport.getResponse().isCommitted()) ContextSupport.getResponse().sendError(500); } catch(Exception ignored) { }
		} finally {
			sx.close();
		}
		return null;
	}

	private org.hibernate.Query restrictedQuery(Session sx, String entity, Long id) {
		String hql = "FROM " + entity + " WHERE id = :id";
		if(!SessionSupport.checkSessionLevel("admin|super")) hql += " AND admin.id = :id_admin";
		org.hibernate.Query query = sx.createQuery(hql).setParameter("id", id);
		if(!SessionSupport.checkSessionLevel("admin|super")) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
		return query;
	}
}
