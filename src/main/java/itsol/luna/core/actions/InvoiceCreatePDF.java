package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.beans.InvoiceProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class InvoiceCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6102922835936220057L;
	
	private Long id_invoice;
	private Invoice invoice;
	
	String title = "";

	
	public class HeaderFooter extends PdfPageEventHelper {

		public void onStartPage(PdfWriter writer, Document document) {
			FontSelector selector = new FontSelector();
			Font f = FontFactory.getFont(FontFactory.HELVETICA, 7);
			selector.addFont(f);

			try {

				Phrase ph;

				PdfPTable table = new PdfPTable(3);
				table.setWidthPercentage(100);
				table.setSpacingAfter(15);

				ph = selector.process(title);
				PdfPCell cell1 = new PdfPCell(ph);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBorderWidthTop(0);
				cell1.setBorderWidthLeft(0);
				cell1.setBorderWidthRight(0);
				cell1.setBorderWidthBottom(0);
				table.addCell(cell1);

				PdfPCell cell2 = new PdfPCell();
				cell2.setBorderWidthTop(0);
				cell2.setBorderWidthLeft(0);
				cell2.setBorderWidthRight(0);
				cell2.setBorderWidthBottom(0);
				table.addCell(cell2);

				ph = selector.process("Pagina " + writer.getPageNumber());
				PdfPCell cell3 = new PdfPCell(ph);
				cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell3.setBorderWidthTop(0);
				cell3.setBorderWidthLeft(0);
				cell3.setBorderWidthRight(0);
				cell3.setBorderWidthBottom(0);
				table.addCell(cell3);

				document.add(table);

			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

		public void onEndPage(PdfWriter writer, Document document) {

		}

	}
	
	public String execute() {
		
		String result = new String();
		

		try {
			
			if(SessionSupport.checkSession()) {
				
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				try {
					if(SessionSupport.checkSessionLevel("admin|super")) {
						this.invoice = (Invoice)sx.createQuery("FROM Invoice WHERE id = :id_invoice").setParameter("id_invoice", getId_invoice()).uniqueResult();
					} else {
						Long id_admin = (Long)SessionSupport.getAdmin().getId();
						this.invoice = (Invoice)sx.createQuery("FROM Invoice WHERE id_admin = :id_admin AND id = :id_invoice").setParameter("id_admin", id_admin).setParameter("id_invoice", getId_invoice()).uniqueResult();
					}
				} catch(Exception ex) {
					
				} finally {
					sx.close();
				}
				
				if(this.invoice != null) {
					
					try {
						
						title = "Proforma N. " + this.invoice.getNumber() + " del " + new SimpleDateFormat("dd/MM/yyyy").format(this.invoice.getInvoice_date());
						title += "\nScadenza: " + new SimpleDateFormat("dd/MM/yyyy").format(this.invoice.getExpire_date());
						title += "\nCliente: " + this.invoice.getCustomer().getBusiness_name();
						
						Document document = new Document();
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						PdfWriter writer = PdfWriter.getInstance(document, output);

						FontSelector selector = new FontSelector();
						Font f = FontFactory.getFont(FontFactory.HELVETICA, 7);
						selector.addFont(f);

						document.setPageSize(PageSize.A4.rotate());

						HeaderFooter hf = new HeaderFooter();
						writer.setPageEvent(hf);

						document.open();
						
						Phrase ph;
						Double qta = 0.00;
						
						Iterator<InvoiceProduct> i = this.invoice.getProducts().iterator();
						
						while(i.hasNext()) {
							
							InvoiceProduct ip = i.next();
							
							PdfPTable table = new PdfPTable(
									new float[] { 5, 10, 25, 15, 8, 8, 8});
							table.setWidthPercentage(100);
							table.setSpacingAfter(15);

							ph = selector.process("N vendita");
							table.addCell(ph);

							ph = selector.process("Data vendita");
							table.addCell(ph);

							ph = selector.process("Venditore");
							table.addCell(ph);

							ph = selector.process("Prodotto");
							table.addCell(ph);

							ph = selector.process("Quantità");
							table.addCell(ph);
							
							ph = selector.process("Provv.");
							table.addCell(ph);

							ph = selector.process("Provv. Tot.");
							table.addCell(ph);
							
							ph = selector.process(ip.getSale().getNumber().toString());
							table.addCell(ph);
							
							ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(ip.getSale().getSale_date()));
							table.addCell(ph);
							
							ph = selector.process(ip.getSale().getSeller().getBusiness_name());
							table.addCell(ph);
							
							ph = selector.process(ip.getName());
							table.addCell(ph);
							
							ph = selector.process(ip.getProduct_amount().toString());
							table.addCell(ph);
							
							ph = selector.process(NumberFormat.getCurrencyInstance().format(ip.getCommission()));
							table.addCell(ph);
							
							ph = selector.process(NumberFormat.getCurrencyInstance().format(ip.getCommission_amount()));
							table.addCell(ph);
							
							qta = qta + ip.getProduct_amount();
							
							document.add(table);
							
						}
						
						PdfPTable table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("Totale peso (T)");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getNumberInstance().format(qta));
						table_end.addCell(ph);

						document.add(table_end);
						
						
						table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("Imponibile");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getCurrencyInstance().format(this.invoice.getTaxable_total()));
						table_end.addCell(ph);

						document.add(table_end);
						
						
						
						table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("IVA (22%)");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getCurrencyInstance().format(this.invoice.getVat_total()));
						table_end.addCell(ph);

						document.add(table_end);
						
						
						table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("Totale");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getCurrencyInstance().format(this.invoice.getTotal()));
						table_end.addCell(ph);

						document.add(table_end);
						
						
						
						table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("Ritenuta");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getCurrencyInstance().format(this.invoice.getHolding_tax()));
						table_end.addCell(ph);

						document.add(table_end);

						
						
						table_end = new PdfPTable(new float[] { 90, 10 });
						table_end.setWidthPercentage(100);
						table_end.setSpacingAfter(15);

						ph = selector.process("Totale (al netto ritenuta)");
						table_end.addCell(ph);

						ph = selector.process(NumberFormat.getCurrencyInstance().format(this.invoice.getTotal_without_holding()));
						table_end.addCell(ph);

						document.add(table_end);
						
						document.close();

						// ------------------------------ genero il pdf
						ContextSupport.getResponse().setContentType("application/pdf");
						ContextSupport.getResponse().addHeader("Content-Disposition",
								"attachment; filename=" + "Proforma_n_" + this.invoice.getNumber() + ".pdf");
						ContextSupport.getResponse().setContentLength(output.size());
						OutputStream os = ContextSupport.getResponse().getOutputStream();
						output.writeTo(os);
						os.flush();
						os.close();

						LogSupport.log(op() + "E' stato generato il pdf proforma.", Level.INFO);
						
						
						
						
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				
				
				

				
				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} 
		
		return result;
	}
	
	
	
	public Long getId_invoice() {
		return id_invoice;
	}
	public void setId_invoice(Long id_invoice) {
		this.id_invoice = id_invoice;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	


}
