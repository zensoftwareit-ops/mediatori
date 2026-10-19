package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.hibernate.Session;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CustomerCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5483678218481596566L;
	
	Font regular = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
	Font regular_bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	Font small = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
	Font small_bold = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	
	Document document;
	ByteArrayOutputStream output;
	PdfWriter writer;
	
	String title = "";
	
	private Long id_customer;

	public Long getId_customer() {
		return id_customer;
	}

	public void setId_customer(Long id_customer) {
		this.id_customer = id_customer;
	}
	
	public String execute() {

		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				Customer c = (Customer)sx.createQuery("FROM Customer WHERE id = :id_customer").setParameter("id_customer", getId_customer()).uniqueResult();
				
				if(c != null) {
					String filename = c.getBusiness_name()+".pdf";
					filename = filename.replaceAll("'", "");
						
					//------------------------------ preparo documento 
					document = new Document();
					output = new ByteArrayOutputStream();
					writer = PdfWriter.getInstance(document, output);

					PdfPTable table;
					PdfPCell cell;
					Phrase p;
							
					document.open();
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("ANAGRAFICA: ", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add((c.getBusiness_name() + " - " + c.getLegal_address() + " - " + c.getLegal_zip_code() + " " + c.getLegal_city() + " " + c.getLegal_province() + " - P.IVA " + c.getVat_number() + " - Tel. " + c.getPrimary_phone() + " " + c.getSecondary_phone() + " -  Codice SDI: " + c.getSdi_code()).toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}

						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					
					document.close();
					
					//------------------------------ genero il pdf
		            ContextSupport.getResponse().setContentType("application/pdf");
		            ContextSupport.getResponse().addHeader("Content-Disposition", "attachment; filename=" + filename);
		            ContextSupport.getResponse().setContentLength(output.size());
		            OutputStream os = ContextSupport.getResponse().getOutputStream();
		            output.writeTo(os);
		            os.flush();
		            os.close();
				}
			}
		
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			sx.close();
		}
	
		return null;
	}

}
