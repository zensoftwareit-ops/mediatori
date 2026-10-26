package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
		if(getDate_from() != null) title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
		if(getDate_to() != null) title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
		if(!getComuniFiltro().isEmpty()) title += " - Comuni: " + String.join(", ", getComuniFiltro());


		try {

			if (SessionSupport.checkSession()) {

				this.analisi = eseguiQuery(sx);

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
					PdfPTable table = new PdfPTable(new float[] { 8,13,15,14,11,7,7,8,7,7,6,9 });
					table.setWidthPercentage(100);
					table.setSpacingAfter(12);
					String[] intestazioni = { "Data", "Campione", "Cliente", "Riso", "Comune", "Cadmio", "Arsenico tot", "Arsenico inorg.", "Nichel", "Costo", "Pagato", "Esito" };
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

	@SuppressWarnings("unchecked")
	private List<Analisi> eseguiQuery(Session sx) {
		List<String> comuniFiltro = getComuniFiltro();
		String q = "SELECT a FROM Analisi a INNER JOIN a.cliente c WHERE a.year.id = :id_sowing_year";
		boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
		if(filtraAdmin) q += " AND a.id_admin = :id_admin";
		if(getId_customer() != null) q += " AND c.id = :id_customer";
		if(getId_product() != null) q += " AND a.prodotto.id = :id_product";
		if(!comuniFiltro.isEmpty()) q += " AND c.legal_city IN (:comuni)";
		if(getDate_from() != null) q += " AND a.dataAnalisi >= :date_from";
		if(getDate_to() != null) q += " AND a.dataAnalisi <= :date_to";
		q += clausolaFuoriLimite();
		q += " ORDER BY UPPER(c.business_name) ASC, a.dataAnalisi ASC, a.id ASC";
		Query query = sx.createQuery(q);
		query.setParameter("id_sowing_year", SessionSupport.getSowingYear().getId());
		if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
		if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
		if(getId_product() != null) query.setParameter("id_product", getId_product());
		if(!comuniFiltro.isEmpty()) query.setParameterList("comuni", comuniFiltro);
		if(getDate_from() != null) query.setParameter("date_from", getDate_from());
		if(getDate_to() != null) query.setParameter("date_to", getDate_to());
		return (List<Analisi>) query.list();
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
