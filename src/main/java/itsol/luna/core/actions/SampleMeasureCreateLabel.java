package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SampleMeasureCreateLabel extends MyActionSupport implements MyActionInterface {

	private static final long serialVersionUID = -3174090430662858720L;
	private static final float POINTS_PER_MM = 72f / 25.4f;

	private Long id_measure;

	public Long getId_measure() {
		return id_measure;
	}

	public void setId_measure(Long id_measure) {
		this.id_measure = id_measure;
	}

	public String execute() {
		Session sx = HibernateUtil.getSessionFactory().openSession();

		try {
			if(!SessionSupport.checkSession()) {
				ContextSupport.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return null;
			}

			YieldMeasure measure;
			if(SessionSupport.checkSessionLevel("admin|super")) {
				measure = (YieldMeasure)sx.createQuery("FROM YieldMeasure WHERE id = :id_measure")
						.setParameter("id_measure", getId_measure())
						.uniqueResult();
			} else {
				measure = (YieldMeasure)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.admin a WHERE ym.id = :id_measure AND a.id = :id_admin")
						.setParameter("id_measure", getId_measure())
						.setParameter("id_admin", SessionSupport.getAdmin().getId())
						.uniqueResult();
			}

			if(measure == null) {
				ContextSupport.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}

			generaEtichetta(measure);
			LogSupport.log(op() + "E' stata generata l'etichetta del campione " + measure.getId() + ".", Level.INFO);
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la generazione dell'etichetta.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			try {
				if(!ContextSupport.getResponse().isCommitted()) {
					ContextSupport.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} catch(Exception ignored) {
				// La risposta potrebbe essere già stata inviata.
			}
		} finally {
			sx.close();
		}

		return null;
	}

	private void generaEtichetta(YieldMeasure measure) throws Exception {
		Rectangle formatoEtichetta = new Rectangle(100f * POINTS_PER_MM, 70f * POINTS_PER_MM);
		Document document = new Document(formatoEtichetta, 8f, 8f, 7f, 7f);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, output);

		Font testo = FontFactory.getFont(FontFactory.HELVETICA, 6.5f);
		Font etichetta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6.5f);
		Font titolo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f);
		Font codice = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f);

		document.open();

		PdfPTable intestazione = new PdfPTable(new float[] { 38f, 62f });
		intestazione.setWidthPercentage(100f);

		PdfPCell logoCell = new PdfPCell();
		logoCell.setBorder(Rectangle.NO_BORDER);
		logoCell.setPadding(0f);
		try {
			Path logoPath = Paths.get(ServletActionContext.getServletContext().getResource("/images/logo_ghisoni.jpg").toURI());
			Image logo = Image.getInstance(logoPath.toAbsolutePath().toString());
			logo.scaleToFit(95f, 30f);
			logoCell.addElement(logo);
		} catch(Exception ex) {
			LogSupport.log(op() + "Logo non disponibile per l'etichetta del campione.", Level.INFO);
		}
		intestazione.addCell(logoCell);

		PdfPCell titoloCell = new PdfPCell();
		titoloCell.setBorder(Rectangle.NO_BORDER);
		titoloCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		titoloCell.setPadding(0f);
		Paragraph titoloParagrafo = new Paragraph("CAMPIONE", titolo);
		titoloParagrafo.setAlignment(Element.ALIGN_RIGHT);
		titoloCell.addElement(titoloParagrafo);
		Paragraph codiceParagrafo = new Paragraph("N. " + measure.getId(), codice);
		codiceParagrafo.setAlignment(Element.ALIGN_RIGHT);
		titoloCell.addElement(codiceParagrafo);
		intestazione.addCell(titoloCell);
		document.add(intestazione);

		PdfPTable dati = new PdfPTable(new float[] { 24f, 76f });
		dati.setWidthPercentage(100f);
		dati.setSpacingBefore(3f);
		aggiungiRiga(dati, "Cliente", cliente(measure), etichetta, testo);
		aggiungiRiga(dati, "Prodotto", valore(measure.getProduct() == null ? null : measure.getProduct().getName()), etichetta, testo);
		aggiungiRiga(dati, "Data", data(measure), etichetta, testo);
		aggiungiRiga(dati, "Annata", valore(measure.getYear() == null ? null : measure.getYear().getYear()), etichetta, testo);
		aggiungiRiga(dati, "Note", unisciNote(measure), etichetta, testo);
		document.add(dati);

		PdfPTable misure = new PdfPTable(5);
		misure.setWidthPercentage(100f);
		misure.setSpacingBefore(4f);
		aggiungiIntestazione(misure, "Resa grani", etichetta);
		aggiungiIntestazione(misure, "Resa globale", etichetta);
		aggiungiIntestazione(misure, "Difetti 1", etichetta);
		aggiungiIntestazione(misure, "Difetti 2", etichetta);
		aggiungiIntestazione(misure, "1000 grani", etichetta);
		aggiungiValore(misure, numero(measure.getGrains_yield()), testo);
		aggiungiValore(misure, numero(measure.getGlobal_yield()), testo);
		aggiungiValore(misure, numero(measure.getDefects_1()), testo);
		aggiungiValore(misure, numero(measure.getDefects_2()), testo);
		aggiungiValore(misure, numero(measure.getThousand_grains()), testo);
		document.add(misure);

		document.close();

		HttpServletResponse response = ContextSupport.getResponse();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=Etichetta-campione-" + measure.getId() + ".pdf");
		response.setContentLength(output.size());
		OutputStream os = response.getOutputStream();
		output.writeTo(os);
		os.flush();
		os.close();
	}

	private void aggiungiRiga(PdfPTable table, String nome, String contenuto, Font fontEtichetta, Font fontTesto) {
		PdfPCell nomeCell = new PdfPCell(new Phrase(nome, fontEtichetta));
		PdfPCell valoreCell = new PdfPCell(new Phrase(contenuto, fontTesto));
		impostaCellaDati(nomeCell);
		impostaCellaDati(valoreCell);
		table.addCell(nomeCell);
		table.addCell(valoreCell);
	}

	private void impostaCellaDati(PdfPCell cell) {
		cell.setPaddingTop(1.5f);
		cell.setPaddingBottom(1.5f);
		cell.setPaddingLeft(2f);
		cell.setPaddingRight(2f);
	}

	private void aggiungiIntestazione(PdfPTable table, String contenuto, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(contenuto, font));
		cell.setBackgroundColor(new BaseColor(235, 235, 235));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(2f);
		table.addCell(cell);
	}

	private void aggiungiValore(PdfPTable table, String contenuto, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(contenuto, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(2f);
		table.addCell(cell);
	}

	private String cliente(YieldMeasure measure) {
		if(measure.getCustomer() == null) {
			return "";
		}
		String codiceCliente = valore(measure.getCustomer().getInternal_code());
		String ragioneSociale = valore(measure.getCustomer().getBusiness_name());
		if(codiceCliente.isEmpty()) {
			return ragioneSociale;
		}
		if(ragioneSociale.isEmpty()) {
			return codiceCliente;
		}
		return codiceCliente + " - " + ragioneSociale;
	}

	private String data(YieldMeasure measure) {
		if(measure.getInsert_date() == null) {
			return "";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(measure.getInsert_date());
	}

	private String unisciNote(YieldMeasure measure) {
		String note1 = valore(measure.getNote_1());
		String note2 = valore(measure.getNote_2());
		if(note1.isEmpty()) {
			return note2;
		}
		if(note2.isEmpty()) {
			return note1;
		}
		return note1 + " - " + note2;
	}

	private String numero(Double numero) {
		return numero == null ? "" : NumberFormat.getNumberInstance(Locale.ITALY).format(numero);
	}

	private String valore(Object valore) {
		return valore == null ? "" : valore.toString().trim();
	}
}
