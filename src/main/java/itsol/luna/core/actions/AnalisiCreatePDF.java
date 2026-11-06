package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
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
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;
import itsol.luna.core.utils.PdfLogoSupport;

public class AnalisiCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033118516423577917L;

	private List<Analisi> analisi = new ArrayList<Analisi>(0);
	private List<Analisi> analisiRiepilogo = new ArrayList<Analisi>(0);

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

				PdfPCell cell2 = PdfLogoSupport.currentLogoCell(105f, 30f);
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
		if(getDate_from() != null) title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
		Date dataFineEffettiva = getDate_to() != null ? fineGiornata(getDate_to()) : (getDate_from() != null ? new Date() : null);
		if(dataFineEffettiva != null) title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(dataFineEffettiva);
		if(!getComuniFiltro().isEmpty()) title += " - Comuni: " + String.join(", ", getComuniFiltro());
		if(!getGruppiFiltro().isEmpty()) title += " - Gruppi: " + String.join(", ", getGruppiFiltro());


		try {

			if (SessionSupport.checkSession()) {

				// Il riepilogo percentuale usa tutte le analisi che rispettano i filtri
				// anagrafici e temporali, prima dell'eventuale filtro "solo fuori limite".
				this.analisiRiepilogo = eseguiQuery(sx, false);
				this.analisi = eseguiQuery(sx, true);

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

					PdfPTable table = new PdfPTable(new float[] { 8,13,17,15,12,7,7,8,7,9 });
					table.setWidthPercentage(100);
					table.setSpacingAfter(12);
					String[] intestazioni = { "Data", "Campione", "Cliente", "Riso", "Comune", "Cadmio", "Arsenico tot", "Arsenico inorg.", "Nichel", "Esito" };
					for(String intestazione : intestazioni) table.addCell(selector.process(intestazione));
					for(Analisi a : this.analisi) {
						table.addCell(selector.process(a.getDataAnalisi() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(a.getDataAnalisi())));
						table.addCell(selector.process(valore(a.getnCampione())));
						table.addCell(selector.process(a.getCliente() == null ? "" : valore(a.getCliente().getBusiness_name())));
						table.addCell(selector.process(a.getProdotto() == null ? "" : valore(a.getProdotto().getName())));
						table.addCell(selector.process(a.getCliente() == null ? "" : valore(a.getCliente().getLegal_city())));
						table.addCell(a.isCadmioFuoriLimite() ? selectorRed.process(valore(a.getCadmio())) : selector.process(valore(a.getCadmio())));
						table.addCell(selector.process(valore(a.getArsenicoTot())));
						table.addCell(a.isArsenicoFuoriLimite() ? selectorRed.process(valore(a.getArsenicoInorganico())) : selector.process(valore(a.getArsenicoInorganico())));
						table.addCell(a.isNichelFuoriLimite() ? selectorRed.process(valore(a.getNichel())) : selector.process(valore(a.getNichel())));
						PdfPCell esito = new PdfPCell(new Phrase(a.getEsito(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, a.isNonValida() ? BaseColor.WHITE : BaseColor.BLACK)));
						if(a.isNonValida()) esito.setBackgroundColor(BaseColor.RED);
						table.addCell(esito);
					}
					document.add(table);

					aggiungiRiepilogoTotale(document, selector);

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

	private void aggiungiRiepilogoTotale(Document document, FontSelector selector) throws DocumentException {
		int totale = analisiRiepilogo.size();
		int nonValide = 0, cadmio = 0, arsenico = 0, nichel = 0;
		for(Analisi a : analisiRiepilogo) {
			if(a.isNonValida()) nonValide++;
			if(a.isCadmioFuoriLimite()) cadmio++;
			if(a.isArsenicoFuoriLimite()) arsenico++;
			if(a.isNichelFuoriLimite()) nichel++;
		}
		int valide = totale - nonValide;
		document.add(new Phrase("Riepilogo totale", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
		PdfPTable riepilogo = new PdfPTable(new float[] { 12,18,18,18,18,18 });
		riepilogo.setWidthPercentage(100);
		riepilogo.setSpacingBefore(6);
		String[] titoli = { "Analisi", "Valide", "Non valide", "Cadmio fuori limite", "Arsenico fuori limite", "Nichel fuori limite" };
		for(String t : titoli) riepilogo.addCell(selector.process(t));
		riepilogo.addCell(selector.process(String.valueOf(totale)));
		riepilogo.addCell(selector.process(valide + " (" + percentuale(valide, totale) + ")"));
		riepilogo.addCell(selector.process(nonValide + " (" + percentuale(nonValide, totale) + ")"));
		riepilogo.addCell(selector.process(cadmio + " (" + percentuale(cadmio, totale) + ")"));
		riepilogo.addCell(selector.process(arsenico + " (" + percentuale(arsenico, totale) + ")"));
		riepilogo.addCell(selector.process(nichel + " (" + percentuale(nichel, totale) + ")"));
		document.add(riepilogo);
	}

	private String percentuale(int valore, int totale) {
		return totale == 0 ? "0,00%" : String.format(java.util.Locale.ITALY, "%.2f%%", valore * 100d / totale);
	}

	@SuppressWarnings("unchecked")
	private List<Analisi> eseguiQuery(Session sx, boolean applicaFiltroFuoriLimite) {
		List<String> comuniFiltro = getComuniFiltro();
		List<String> gruppiFiltro = getGruppiFiltro();
		boolean filtraAnnata = getDate_from() == null && getDate_to() == null;
		String q = "SELECT a FROM Analisi a INNER JOIN a.cliente c INNER JOIN a.prodotto p WHERE 1 = 1";
		if(filtraAnnata) q += " AND a.year.id = :id_sowing_year";
		boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
		if(filtraAdmin) q += " AND a.id_admin = :id_admin";
		if(getId_customer() != null) q += " AND c.id = :id_customer";
		if(getId_product() != null) q += " AND p.id = :id_product";
		if(!comuniFiltro.isEmpty()) q += " AND c.legal_city IN (:comuni)";
		if(!gruppiFiltro.isEmpty()) {
			q += " AND (";
			for(int i = 0; i < gruppiFiltro.size(); i++) {
				if(i > 0) q += " OR ";
				q += "UPPER(p.groups) LIKE :gruppo" + i;
			}
			q += ")";
		}
		if(getDate_from() != null) q += " AND a.dataAnalisi >= :date_from";
		Date dataFineEffettiva = getDate_to() != null ? fineGiornata(getDate_to()) : (getDate_from() != null ? new Date() : null);
		if(dataFineEffettiva != null) q += " AND a.dataAnalisi <= :date_to";
		if(applicaFiltroFuoriLimite) q += clausolaFuoriLimite();
		q += " ORDER BY UPPER(c.business_name) ASC, a.dataAnalisi ASC, a.id ASC";
		Query query = sx.createQuery(q);
		if(filtraAnnata) query.setParameter("id_sowing_year", SessionSupport.getSowingYear().getId());
		if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
		if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
		if(getId_product() != null) query.setParameter("id_product", getId_product());
		if(!comuniFiltro.isEmpty()) query.setParameterList("comuni", comuniFiltro);
		for(int i = 0; i < gruppiFiltro.size(); i++) query.setParameter("gruppo" + i, gruppiFiltro.get(i).toUpperCase() + "%");
		if(getDate_from() != null) query.setParameter("date_from", getDate_from());
		if(dataFineEffettiva != null) query.setParameter("date_to", dataFineEffettiva);
		return (List<Analisi>) query.list();
	}

	private Date fineGiornata(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	private String clausolaFuoriLimite() {
		if(getFuori_limite() == null) return "";
		String filtro = getFuori_limite().trim().toUpperCase();
		if("TOTALE".equals(filtro)) return " AND (UPPER(a.cadmioFl) = 'SI' OR UPPER(a.arsenicoFl) = 'SI' OR UPPER(a.nichelFl) = 'SI')";
		if("CADMIO".equals(filtro)) return " AND UPPER(a.cadmioFl) = 'SI'";
		if("ARSENICO".equals(filtro)) return " AND UPPER(a.arsenicoFl) = 'SI'";
		if("NICHEL".equals(filtro)) return " AND UPPER(a.nichelFl) = 'SI'";
		return "";
	}

}
