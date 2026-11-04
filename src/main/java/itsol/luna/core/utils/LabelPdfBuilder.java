package itsol.luna.core.utils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;

/** Generatore condiviso delle etichette verticali 10 x 15 cm. */
public final class LabelPdfBuilder {
	private static final float POINTS_PER_MM = 72f / 25.4f;
	private static final Rectangle LABEL_10X15 = new Rectangle(100f * POINTS_PER_MM, 150f * POINTS_PER_MM);

	private LabelPdfBuilder() { }

	public static byte[] sampleLabel(Customer customer, Product product, String average, Date date, String logoPath) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document(LABEL_10X15, 14f, 14f, 14f, 14f);
		PdfWriter.getInstance(document, output);
		document.open();
		addHeader(document, "CAMPIONE", logoPath);
		Font label = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f);
		Font value = FontFactory.getFont(FontFactory.HELVETICA, 12f);
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(18f);
		addBlock(table, "AGRICOLTORE", customer == null ? "" : clean(customer.getBusiness_name()), label, value);
		addBlock(table, "PRODOTTO", product == null ? "" : clean(product.getName()), label, value);
		addBlock(table, "MEDIA RESA GRANI", clean(average), label, value);
		addBlock(table, "DATA", date == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(date), label, value);
		document.add(table);
		document.close();
		return output.toByteArray();
	}

	public static byte[] analysisLabel(Customer customer, Product product, String logoPath) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document(LABEL_10X15, 12f, 12f, 12f, 12f);
		PdfWriter.getInstance(document, output);
		document.open();
		addHeader(document, "LABORATORIO ANALISI", logoPath);
		Font label = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f);
		Font value = FontFactory.getFont(FontFactory.HELVETICA, 9f);
		PdfPTable data = new PdfPTable(1);
		data.setWidthPercentage(100f);
		data.setSpacingBefore(4f);
		addCompactBlock(data, "AGRICOLTORE", customer == null ? "" : clean(customer.getBusiness_name()), label, value);
		addCompactBlock(data, "INDIRIZZO", customer == null ? "" : legalAddress(customer), label, value);
		addCompactBlock(data, "P. IVA / C.F.", customer == null ? "" : join("  -  ", clean(customer.getVat_number()), clean(customer.getTax_code())), label, value);
		addCompactBlock(data, "CODICE SDI", customer == null ? "" : clean(customer.getSdi_code()), label, value);
		addCompactBlock(data, "PEC", customer == null ? "" : clean(customer.getPec_email()), label, value);
		addCompactBlock(data, "PRODOTTO", product == null ? "" : clean(product.getName()), label, value);
		document.add(data);
		Paragraph request = new Paragraph("ANALISI RICHIESTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f));
		request.setSpacingBefore(5f);
		document.add(request);
		PdfPTable metals = new PdfPTable(1);
		metals.setWidthPercentage(100f);
		metals.setSpacingBefore(2f);
		addMetal(metals, "CADMIO");
		addMetal(metals, "ARSENICO");
		addMetal(metals, "NICHEL");
		document.add(metals);
		document.close();
		return output.toByteArray();
	}

	private static void addHeader(Document document, String title, String logoPath) throws Exception {
		PdfPTable header = new PdfPTable(1);
		header.setWidthPercentage(100f);
		if(logoPath != null && !logoPath.trim().isEmpty()) {
			Image logo = Image.getInstance(logoPath);
			logo.scaleToFit(210f, 48f);
			PdfPCell logoCell = new PdfPCell(logo, false);
			logoCell.setBorder(Rectangle.NO_BORDER);
			logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			logoCell.setPaddingBottom(8f);
			header.addCell(logoCell);
		}
		PdfPCell titleCell = new PdfPCell(new Phrase(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15f)));
		titleCell.setBorder(Rectangle.NO_BORDER);
		titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		titleCell.setPaddingBottom(5f);
		header.addCell(titleCell);
		document.add(header);
	}

	private static void addBlock(PdfPTable table, String name, String content, Font label, Font value) {
		PdfPCell cell = new PdfPCell();
		cell.setPadding(7f);
		cell.setPaddingBottom(10f);
		cell.addElement(new Phrase(name, label));
		Paragraph p = new Paragraph(content, value);
		p.setSpacingBefore(4f);
		cell.addElement(p);
		table.addCell(cell);
	}

	private static void addCompactBlock(PdfPTable table, String name, String content, Font label, Font value) {
		PdfPCell cell = new PdfPCell();
		cell.setPadding(2.5f);
		cell.addElement(new Phrase(name, label));
		Paragraph p = new Paragraph(content, value);
		p.setSpacingBefore(1f);
		cell.addElement(p);
		table.addCell(cell);
	}

	private static void addMetal(PdfPTable table, String metal) {
		PdfPCell cell = new PdfPCell(new Phrase(metal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f)));
		cell.setBackgroundColor(new BaseColor(242, 242, 242));
		cell.setPaddingTop(6f);
		cell.setPaddingBottom(12f);
		cell.setPaddingLeft(7f);
		table.addCell(cell);
	}

	private static String legalAddress(Customer customer) {
		String city = join(" ", clean(customer.getLegal_zip_code()), clean(customer.getLegal_city()));
		if(!clean(customer.getLegal_province()).isEmpty()) city += " (" + clean(customer.getLegal_province()) + ")";
		return join(", ", clean(customer.getLegal_address()), city, clean(customer.getLegal_nation()));
	}

	private static String join(String separator, String... values) {
		StringBuilder result = new StringBuilder();
		for(String value : values) {
			if(value == null || value.trim().isEmpty()) continue;
			if(result.length() > 0) result.append(separator);
			result.append(value.trim());
		}
		return result.toString();
	}

	private static String clean(Object value) {
		return value == null ? "" : value.toString().trim();
	}
}
