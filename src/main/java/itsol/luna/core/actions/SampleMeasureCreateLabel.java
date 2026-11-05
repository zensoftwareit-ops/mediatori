package itsol.luna.core.actions;

import java.io.OutputStream;
import java.util.Date;
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

public class SampleMeasureCreateLabel extends MyActionSupport implements MyActionInterface {
	private static final long serialVersionUID = -3174090430662858720L;
	private String label_average;
	private String label_global_average;
	private String label_defects_average_1;
	private String label_defects_average_2;
	private Date label_date;
	public String getLabel_average() { return label_average; }
	public void setLabel_average(String label_average) { this.label_average = label_average; }
	public String getLabel_global_average() { return label_global_average; }
	public void setLabel_global_average(String label_global_average) { this.label_global_average = label_global_average; }
	public String getLabel_defects_average_1() { return label_defects_average_1; }
	public void setLabel_defects_average_1(String label_defects_average_1) { this.label_defects_average_1 = label_defects_average_1; }
	public String getLabel_defects_average_2() { return label_defects_average_2; }
	public void setLabel_defects_average_2(String label_defects_average_2) { this.label_defects_average_2 = label_defects_average_2; }
	public Date getLabel_date() { return label_date; }
	public void setLabel_date(Date label_date) { this.label_date = label_date; }

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
			byte[] pdf = LabelPdfBuilder.sampleLabel(customer, product, label_average, label_global_average,
					label_defects_average_1, label_defects_average_2,
					label_date == null ? new Date() : label_date, logoPath);
			writePdf(pdf, "Etichetta-campione.pdf");
			LogSupport.log(op() + "E' stata generata l'etichetta aggregata del campione.", Level.INFO);
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

	private void writePdf(byte[] pdf, String filename) throws Exception {
		HttpServletResponse response = ContextSupport.getResponse();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + filename);
		response.setContentLength(pdf.length);
		OutputStream os = response.getOutputStream();
		os.write(pdf);
		os.flush();
		os.close();
	}
}
