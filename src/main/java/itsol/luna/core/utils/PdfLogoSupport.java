package itsol.luna.core.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.struts2.ServletActionContext;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.support.SessionSupport;

/** Risolve e impagina i loghi documentali mantenendo lo storico dei contratti. */
public final class PdfLogoSupport {
	public static final String OLD_GHISONI_LOGO = "logo_ghisoni.jpg";
	public static final String NEW_GHISONI_LOGO = "logo_ghisoni_new.jpg";

	private PdfLogoSupport() { }

	public static String currentLogoPath() throws Exception {
		String logo = SessionSupport.getAdmin() == null ? null : SessionSupport.getAdmin().getDocumentLogo();
		return resourcePath(safeFileName(logo));
	}

	public static String contractLogoPath(Sale sale) throws Exception {
		String logo = sale == null ? null : sale.getContractLogo();
		return resourcePath(safeFileName(logo));
	}

	public static boolean usesNewContractLogo(Sale sale) {
		return sale != null && NEW_GHISONI_LOGO.equals(safeFileName(sale.getContractLogo()));
	}

	public static PdfPCell currentLogoCell(float maxWidth, float maxHeight) {
		PdfPCell cell = emptyLogoCell();
		try {
			Image logo = logoImage(currentLogoPath());
			logo.scaleToFit(maxWidth, maxHeight);
			logo.setAlignment(Element.ALIGN_CENTER);
			cell.addElement(logo);
		} catch(Exception ignored) { }
		return cell;
	}

	public static void addCurrentLogo(Document document, float maxWidth, float maxHeight) {
		try {
			Image logo = logoImage(currentLogoPath());
			logo.scaleToFit(maxWidth, maxHeight);
			logo.setAlignment(Element.ALIGN_CENTER);
			document.add(logo);
		} catch(Exception ignored) { }
	}

	public static Image logoImage(String path) throws Exception {
		return Image.getInstance(path);
	}

	private static PdfPCell emptyLogoCell() {
		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(0f);
		return cell;
	}

	private static String resourcePath(String fileName) throws Exception {
		Path path = Paths.get(ServletActionContext.getServletContext().getResource("/images/" + fileName).toURI());
		return path.toAbsolutePath().toString();
	}

	private static String safeFileName(String value) {
		if(value == null || value.trim().isEmpty()) return OLD_GHISONI_LOGO;
		String fileName = Paths.get(value.trim()).getFileName().toString();
		return fileName.matches("[A-Za-z0-9._-]+") ? fileName : OLD_GHISONI_LOGO;
	}
}
