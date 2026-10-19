package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.Query;

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
import itsol.luna.core.beans.AnalisiComuneSummary;
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

				String q = "SELECT a FROM Analisi a INNER JOIN a.cliente c WHERE 1 = 1";
				boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
				if(filtraAdmin) q += " AND a.id_admin = :id_admin";
				if(getId_customer() != null) q += " AND c.id = :id_customer";
				if(getComune() != null && !getComune().trim().isEmpty()) q += " AND c.legal_city = :comune";
				if("SI".equals(getPagato()) || "NO".equals(getPagato())) q += " AND a.pagato = :pagato";
				q += " ORDER BY a.id DESC";
				Query query = sx.createQuery(q);
				if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
				if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
				if(getComune() != null && !getComune().trim().isEmpty()) query.setParameter("comune", getComune().trim());
				if("SI".equals(getPagato()) || "NO".equals(getPagato())) query.setParameter("pagato", getPagato());
				this.analisi = (List<Analisi>) query.list();

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

					Double tot = 0.00;
					PdfPTable table = new PdfPTable(new float[] { 16,18,13,8,8,9,8,8,7,10 });
					table.setWidthPercentage(100);
					table.setSpacingAfter(12);
					String[] intestazioni = { "Campione", "Cliente", "Comune", "Cadmio", "Arsenico tot", "Arsenico inorg.", "Nichel", "Costo", "Pagato", "Esito" };
					for(String intestazione : intestazioni) table.addCell(selector.process(intestazione));
					for(Analisi a : this.analisi) {
						table.addCell(selector.process(valore(a.getnCampione())));
						table.addCell(selector.process(a.getCliente() == null ? "" : valore(a.getCliente().getBusiness_name())));
						table.addCell(selector.process(a.getCliente() == null ? "" : valore(a.getCliente().getLegal_city())));
						table.addCell(a.isCadmioFuoriLimite() ? selectorRed.process(valore(a.getCadmio())) : selector.process(valore(a.getCadmio())));
						table.addCell(selector.process(valore(a.getArsenicoTot())));
						table.addCell(a.isArsenicoFuoriLimite() ? selectorRed.process(valore(a.getArsenicoInorganico())) : selector.process(valore(a.getArsenicoInorganico())));
						table.addCell(a.isNichelFuoriLimite() ? selectorRed.process(valore(a.getNichel())) : selector.process(valore(a.getNichel())));
						table.addCell(selector.process(valore(a.getCosto())));
						table.addCell(selector.process(valore(a.getPagato())));
						PdfPCell esito = new PdfPCell(new Phrase(a.getEsito(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, a.isNonValida() ? BaseColor.WHITE : BaseColor.BLACK)));
						if(a.isNonValida()) esito.setBackgroundColor(BaseColor.RED);
						table.addCell(esito);
						if("NO".equals(a.getPagato()) && a.getCosto() != null) tot += a.getCosto();
					}
					document.add(table);

					PdfPTable totale = new PdfPTable(new float[] { 80,20 });
					totale.setWidthPercentage(100);
					totale.setSpacingAfter(15);
					totale.addCell(selector.process("TOT. DA PAGARE"));
					totale.addCell(selector.process(tot.toString()));
					document.add(totale);

					Map<String, AnalisiComuneSummary> riepilogo = creaRiepilogoComuni();
					if(!riepilogo.isEmpty()) {
						document.add(new Phrase("Riepilogo per comune", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
						PdfPTable comuni = new PdfPTable(new float[] { 22,10,17,17,17,17 });
						comuni.setWidthPercentage(100);
						comuni.setSpacingBefore(6);
						String[] titoli = { "Comune", "Analisi", "Non valide", "Cadmio fuori limite", "Arsenico fuori limite", "Nichel fuori limite" };
						for(String t : titoli) comuni.addCell(selector.process(t));
						for(AnalisiComuneSummary r : riepilogo.values()) {
							comuni.addCell(selector.process(r.getComune()));
							comuni.addCell(selector.process(String.valueOf(r.getTotale())));
							comuni.addCell(selector.process(r.getNonValide() + " (" + r.getPercentualeNonValide() + ")"));
							comuni.addCell(selector.process(r.getCadmioFuoriLimite() + " (" + r.getPercentualeCadmio() + ")"));
							comuni.addCell(selector.process(r.getArsenicoFuoriLimite() + " (" + r.getPercentualeArsenico() + ")"));
							comuni.addCell(selector.process(r.getNichelFuoriLimite() + " (" + r.getPercentualeNichel() + ")"));
						}
						document.add(comuni);
					}

					document.close();

					// ------------------------------ genero il pdf
					ContextSupport.getResponse().setContentType("application/pdf");
					ContextSupport.getResponse().addHeader("Content-Disposition",
							"attachment; filename=" + "Analisi.pdf");
					ContextSupport.getResponse().setContentLength(output.size());
					OutputStream os = ContextSupport.getResponse().getOutputStream();
					output.writeTo(os);
					os.flush();
					os.close();

					LogSupport.log(op() + "E' stato generato il pdf analisi.", Level.INFO);

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

	private String valore(Object value) {
		return value == null ? "" : value.toString();
	}

	private Map<String, AnalisiComuneSummary> creaRiepilogoComuni() {
		Map<String, AnalisiComuneSummary> riepilogo = new TreeMap<String, AnalisiComuneSummary>(String.CASE_INSENSITIVE_ORDER);
		for(Analisi a : this.analisi) {
			String comune = a.getCliente() == null ? null : a.getCliente().getLegal_city();
			if(comune == null || comune.trim().isEmpty()) comune = "Comune non indicato";
			comune = comune.trim();
			AnalisiComuneSummary r = riepilogo.get(comune);
			if(r == null) {
				r = new AnalisiComuneSummary(comune);
				riepilogo.put(comune, r);
			}
			r.add(a);
		}
		return riepilogo;
	}

}
