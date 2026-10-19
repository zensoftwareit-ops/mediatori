package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.Analisi;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class AnalisiCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033118516423577917L;

	private List<Analisi> analisi = new ArrayList<Analisi>(0);

	public List<Analisi> getAnalisi() {
		return analisi;
	}

	public void setAnalisi(List<Analisi> analisi) {
		this.analisi = analisi;
	}


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

	@SuppressWarnings("unchecked")
	public String execute() {

		Session sx = HibernateUtil.getSessionFactory().openSession();
		title = "Elenco analisi";


		try {

			if (SessionSupport.checkSession()) {

				if (SessionSupport.checkSessionLevel("admin|super")) {

				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();

					String q = "";
					q += "SELECT a FROM Analisi a";
					
					if(getId_customer() != null) {
						q += " INNER JOIN a.cliente c";
					} 
					
					q += " WHERE a.id_admin = :id_admin";
					
					if(getId_customer() != null) {
						q += " AND c.id = " + getId_customer();
					} 
					
					if(getPagato() != null)
					if(getPagato().equals("SI")) {
						q += " AND a.pagato = 'SI'";
					} else if(getPagato().equals("NO")) {
						q += " AND a.pagato = 'NO'";
					}
					
					q += " ORDER BY id DESC";
					
						this.analisi = (List<Analisi>) sx.createQuery(q).setParameter("id_admin", id_admin).list();
				}

				try {
					Document document = new Document();
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					PdfWriter writer = PdfWriter.getInstance(document, output);

					FontSelector selector = new FontSelector();
					Font f = FontFactory.getFont(FontFactory.HELVETICA, 7);
					
					selector.addFont(f);
					
					FontSelector selectorRed = new FontSelector();
					Font red = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.RED);
					selectorRed.addFont(red);

					document.setPageSize(PageSize.A4.rotate());

					HeaderFooter hf = new HeaderFooter();
					writer.setPageEvent(hf);

					document.open();

					Phrase ph;
					// document.add(ph);

					Iterator<Analisi> i = this.analisi.iterator();

					Double tot = 0.00;
					
					while (i.hasNext()) {

						Analisi a = i.next();
						
						PdfPTable table = new PdfPTable(
								new float[] { 20,20,10,10,10,10,10,10 });
						table.setWidthPercentage(100);
						table.setSpacingAfter(15);

						ph = selector.process("Campione");
						table.addCell(ph);

						ph = selector.process("Cliente");
						table.addCell(ph);

						ph = selector.process("Cadmio");
						table.addCell(ph);

						ph = selector.process("Arsenico tot");
						table.addCell(ph);

						ph = selector.process("Arsenico inorg.");
						table.addCell(ph);

						ph = selector.process("Nichel");
						table.addCell(ph);

						ph = selector.process("Costo");
						table.addCell(ph);

						ph = selector.process("Pagato");
						table.addCell(ph);
						

						
						ph = selector.process(a.getnCampione());
						table.addCell(ph);
						
						ph = selector.process(a.getCliente().getBusiness_name());
						table.addCell(ph);
						
						if("SI".equals(a.getCadmioFl()))
							ph = selectorRed.process(a.getCadmio());
						else
							ph = selector.process(a.getCadmio());
						
						table.addCell(ph);
						
						ph = selector.process(a.getArsenicoTot());
						table.addCell(ph);
						
						if("SI".equals(a.getArsenicoFl()))
							ph = selectorRed.process(a.getArsenicoInorganico());
						else
							ph = selector.process(a.getArsenicoInorganico());
							
						table.addCell(ph);

						if("SI".equals(a.getNichelFl()))
							ph = selectorRed.process(a.getNichel() == null ? "" : a.getNichel());
						else
							ph = selector.process(a.getNichel() == null ? "" : a.getNichel());

						table.addCell(ph);
						
						ph = selector.process(a.getCosto().toString());
						table.addCell(ph);
						
						ph = selector.process(a.getPagato());
						table.addCell(ph);
						
						document.add(table);
						
						///

						if(a.getPagato().equals("NO"))
							tot = tot+a.getCosto();

					}

					PdfPTable table = new PdfPTable(
							new float[] { 20,20,10,10,10,10,10 });
					table.setWidthPercentage(100);
					table.setSpacingAfter(15);
					
					ph = selector.process("");
					table.addCell(ph);

					ph = selector.process("");
					table.addCell(ph);

					ph = selector.process("");
					table.addCell(ph);

					ph = selector.process("");
					table.addCell(ph);

					ph = selector.process("TOT. DA PAGARE");
					table.addCell(ph);

					ph = selector.process(tot.toString());
					table.addCell(ph);

					ph = selector.process("");
					table.addCell(ph);
					
					document.add(table);

					document.close();

					// ------------------------------ genero il pdf
					ContextSupport.getResponse().setContentType("application/pdf");
					ContextSupport.getResponse().addHeader("Content-Disposition",
							"attachment; filename=" + "Provvigioni.pdf");
					ContextSupport.getResponse().setContentLength(output.size());
					OutputStream os = ContextSupport.getResponse().getOutputStream();
					output.writeTo(os);
					os.flush();
					os.close();

					LogSupport.log(op() + "E' stato generato il pdf provvigioni.", Level.INFO);

				} catch (Exception ex) {
					if (ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
							Level.SEVERE);
				}

			}

		} catch (Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));

			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);
		} finally {
			sx.close();
		}

		return null;
	}

}
