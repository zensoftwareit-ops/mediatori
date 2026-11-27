package itsol.luna.core.support;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;


import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;

public class SalePDFSupport {

	private static final String RISO_GALLO_VAT = "01407630183";
	private static final String RISO_GALLO_TEMPLATE = "/WEB-INF/templates/contratto_riso_gallo_2026.pdf";
	private static final String MUNDI_RISO_VAT = "01818090027";
	private static final String MUNDI_RISO_TEMPLATE = "/WEB-INF/templates/contratto_mundi_riso.pdf";
	private static final String VIAZZO_VAT = "01502060021";
	private static final String VIAZZO_TEMPLATE = "/WEB-INF/templates/contratto_viazzo_2026.pdf";
	private static final long GHISONI_ADMIN_ID = 2L;
	private static final long FORTE_ADMIN_ID = 3L;
	private static final String GHISONI_SIGNATURE = "/images/firma_ghisoni.jpg";
	private static final String FORTE_SIGNATURE = "/images/firma_forte.png";
	private static final String FORTE_RICE_TEMPLATE = "contratto_forte_01.pdf";

	/**
	 * Il contratto Riso Gallo e' globale e prevale sul modello scelto nella vendita.
	 * La P.IVA evita falsi positivi; la ragione sociale e' un fallback per le anagrafiche
	 * storiche nelle quali la P.IVA non era stata compilata.
	 */
	public static boolean isRisoGallo(Sale sale) {
		if(sale == null || sale.getBuyer() == null) {
			return false;
		}
		String vat = onlyDigits(sale.getBuyer().getVat_number());
		if(RISO_GALLO_VAT.equals(vat)) {
			return true;
		}
		String name = safe(sale.getBuyer().getBusiness_name()).toUpperCase(Locale.ITALY).replaceAll("[^A-Z0-9]", "");
		return name.contains("RISOGALLO");
	}

	/** Il modello Mundi Riso e' globale per tutti gli utenti del gestionale. */
	public static boolean isMundiRiso(Sale sale) {
		if(sale == null || sale.getBuyer() == null) {
			return false;
		}
		String vat = onlyDigits(sale.getBuyer().getVat_number());
		if(MUNDI_RISO_VAT.equals(vat)) {
			return true;
		}
		String name = safe(sale.getBuyer().getBusiness_name()).toUpperCase(Locale.ITALY).replaceAll("[^A-Z0-9]", "");
		return name.contains("MUNDIRISO");
	}

	/** Il modello Viazzo aggiornato e' globale per tutti gli utenti del gestionale. */
	public static boolean isViazzo(Sale sale) {
		if(sale == null || sale.getBuyer() == null) {
			return false;
		}
		String vat = onlyDigits(sale.getBuyer().getVat_number());
		if(VIAZZO_VAT.equals(vat)) {
			return true;
		}
		String name = safe(sale.getBuyer().getBusiness_name()).toUpperCase(Locale.ITALY).replaceAll("[^A-Z0-9]", "");
		return name.contains("RISOVIAZZO") || name.equals("VIAZZO") || name.startsWith("VIAZZO");
	}

	public static String createRisoGallo(String filename, Sale sale) {
		String dest = ContextSupport.getUploadPath() + filename;
		try(InputStream src = ServletActionContext.getServletContext().getResourceAsStream(RISO_GALLO_TEMPLATE);
			OutputStream out = new FileOutputStream(dest)) {
			if(src == null) {
				throw new IllegalStateException("Modello contratto Riso Gallo non trovato: " + RISO_GALLO_TEMPLATE);
			}
			writeRisoGallo(src, out, sale, mediatorSignature(sale));
		} catch(Exception ex) {
			logPdfError(ex);
		}
		return filename;
	}

	public static String createMundiRiso(String filename, Sale sale) {
		String dest = ContextSupport.getUploadPath() + filename;
		try(InputStream src = ServletActionContext.getServletContext().getResourceAsStream(MUNDI_RISO_TEMPLATE);
			OutputStream out = new FileOutputStream(dest)) {
			if(src == null) {
				throw new IllegalStateException("Modello contratto Mundi Riso non trovato: " + MUNDI_RISO_TEMPLATE);
			}
			writeMundiRiso(src, out, sale, mediatorSignature(sale));
		} catch(Exception ex) {
			logPdfError(ex);
		}
		return filename;
	}

	public static String createViazzo(String filename, Sale sale) {
		String dest = ContextSupport.getUploadPath() + filename;
		try(InputStream src = ServletActionContext.getServletContext().getResourceAsStream(VIAZZO_TEMPLATE);
			OutputStream out = new FileOutputStream(dest)) {
			if(src == null) {
				throw new IllegalStateException("Modello contratto Viazzo non trovato: " + VIAZZO_TEMPLATE);
			}
			writeViazzo(src, out, sale, mediatorSignature(sale));
		} catch(Exception ex) {
			logPdfError(ex);
		}
		return filename;
	}

	/** Metodo separato usato dalla verifica visuale del modello Viazzo. */
	public static void createViazzoFromTemplate(String template, String destination, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination)) {
			writeViazzo(src, out, sale, null);
		}
	}

	public static void createViazzoFromTemplate(String template, String destination, String signature, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination);
			InputStream signatureStream = new FileInputStream(signature)) {
			writeViazzo(src, out, sale, signatureStream.readAllBytes());
		}
	}

	/** Metodo separato usato dal test di impaginazione del modello Mundi. */
	public static void createMundiRisoFromTemplate(String template, String destination, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination)) {
			writeMundiRiso(src, out, sale, null);
		}
	}

	public static void createMundiRisoFromTemplate(String template, String destination, String signature, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination);
			InputStream signatureStream = new FileInputStream(signature)) {
			writeMundiRiso(src, out, sale, signatureStream.readAllBytes());
		}
	}

	/** Metodo separato usato anche dal test di impaginazione. */
	public static void createRisoGalloFromTemplate(String template, String destination, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination)) {
			writeRisoGallo(src, out, sale, null);
		}
	}

	/** Variante usata dalla verifica visuale del modello statico. */
	public static void createRisoGalloFromTemplate(String template, String destination, String signature, Sale sale) throws Exception {
		try(InputStream src = new FileInputStream(template); OutputStream out = new FileOutputStream(destination);
			InputStream signatureStream = new FileInputStream(signature)) {
			writeRisoGallo(src, out, sale, signatureStream.readAllBytes());
		}
	}

	private static void writeRisoGallo(InputStream source, OutputStream destination, Sale sale, byte[] mediatorSignature) throws Exception {
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(destination));
		try {
			PdfPage page = pdfDoc.getPage(1);
			PdfCanvas canvas = new PdfCanvas(page);
			PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			SaleProduct sp = sale.getProduct();

			// Intestazione del contratto.
			boxedText(canvas, bold, value(sale.getNumber()), 81.42f, 12.72f, 6, 757, 8);
			text(canvas, regular, safe(sale.getSeller() == null ? null : sale.getSeller().getLegal_city()), 218, 757, 190, 8);
			dateParts(canvas, regular, sale.getSale_date(), 458, 497, 531, 757);

			// Venditore.
			if(sale.getSeller() != null) {
				text(canvas, bold, safe(sale.getSeller().getBusiness_name()), 96, 716, 225, 8);
				text(canvas, regular, phones(sale.getSeller().getPrimary_phone(), sale.getSeller().getSecondary_phone()), 472, 716, 90, 7);
				text(canvas, regular, safe(sale.getSeller().getLegal_address()), 84, 698, 238, 8);
				text(canvas, regular, phones(sale.getSeller().getPrimary_phone(), sale.getSeller().getSecondary_phone()), 472, 698, 90, 7);
				text(canvas, regular, safe(sale.getSeller().getLegal_city()), 83, 680, 180, 8);
				text(canvas, regular, safe(sale.getSeller().getLegal_province()), 310, 680, 20, 8);
				String fiscal = taxIdentifier(sale.getSeller().getVat_number(), sale.getSeller().getTax_code());
				boxedText(canvas, regular, fiscal, 334.81f, 12.72f, 18, 662, 7);
				text(canvas, regular, safe(sale.getSeller().getPrimary_email()), 84, 662, 130, 7);
			}

			// Nel modello originale l'acquirente e' prestampato. Ripuliamo la riga
			// fiscale e la riscriviamo dai dati della vendita: prima P. IVA, poi C.F.
			// solo quando la P. IVA e' assente.
			whiteout(canvas, 335, 541, 228, 16);
			if(sale.getBuyer() != null) {
				String buyerVat = safe(sale.getBuyer().getVat_number());
				String buyerTax = taxIdentifier(buyerVat, sale.getBuyer().getTax_code());
				text(canvas, regular, buyerVat.isEmpty() ? "C.F." : "P. IVA", 454, 546, 24, 7);
				text(canvas, regular, buyerTax, 481, 545, 78, 8);
			}

			// Luogo di ritiro, solo se richiesto nella vendita.
			if(!"NO".equalsIgnoreCase(safe(sale.getPickup_printing()))) {
				text(canvas, regular, safe(sale.getPickup_place()), 145, 644, 176, 7);
				text(canvas, regular, safe(sale.getPickup_phone()), 472, 644, 90, 7);
				text(canvas, regular, safe(sale.getPickup_address()), 84, 626, 238, 8);
				text(canvas, regular, safe(sale.getPickup_phone()), 472, 626, 90, 7);
				text(canvas, regular, safe(sale.getPickup_city()), 83, 608, 180, 8);
				text(canvas, regular, safe(sale.getPickup_province()), 310, 608, 20, 8);
			}

			// Mediatore: le anagrafiche Admin contengono nome e cognome.
			if(sale.getAdmin() != null) {
				text(canvas, bold, safe(sale.getAdmin().getFull_name()), 96, 507, 225, 8);
			}

			// Merce e caratteristiche qualitative.
			if(sp != null) {
				text(canvas, bold, safe(sp.getName()), 145, 460, 112, 8);
				text(canvas, bold, quantityWithEndOfStock(sp), 357, 460, 83, 8);
				text(canvas, bold, value(sp.getGrains_yield()), 483, 460, 24, 8);
				text(canvas, bold, value(sp.getGlobal_yield()), 545, 460, 18, 8);
				dateParts(canvas, regular, sale.getSale_date(), 452, 487, 519, 446);

				text(canvas, regular, safe(sp.getHumidity()), 111, 409, 32, 8);
				text(canvas, regular, value(sp.getD_red_grains()), 270, 409, 35, 8);
				text(canvas, regular, value(sp.getD_damaged_grains()), 111, 395, 32, 8);
				text(canvas, regular, safe(sp.getD_malformations()), 270, 395, 35, 8);
				text(canvas, regular, safe(sp.getD_pinstriped_grains()), 111, 381, 32, 8);
				text(canvas, regular, safe(sp.getD_impurity()), 270, 381, 35, 8);
				text(canvas, regular, safe(sp.getQuality()), 94, 372, 185, 7);

				String price = !safe(sale.getAlt_list()).isEmpty() ? safe(sale.getAlt_list()) : number(sp.getAmount());
				text(canvas, bold, price, 123, 328, 142, 9);
				if("iva".equalsIgnoreCase(safe(sp.getAmount_type()))) {
					mark(canvas, 290, 340);
				} else {
					mark(canvas, 290, 318);
				}
			}

			text(canvas, bold, safe(sale.getMediation()), 123, 301, 142, 8);
			text(canvas, regular, paymentDates(sale), 438, 340, 126, 7);
			text(canvas, regular, safe(sale.getPickup_time()), 414, 274, 150, 8);
			if("SI".equalsIgnoreCase(safe(sale.getCarico_motrici()))) {
				mark(canvas, 463, 239);
			}
			if("SI".equalsIgnoreCase(safe(sale.getCarico_al_coperto()))) {
				mark(canvas, 539, 239);
			}
			text(canvas, regular, safe(sale.getParticular_conditions()), 34, 91, 525, 6.5f);

			// Le firme vengono mostrate solo dopo la verifica OTP, che imposta a SI
			// i rispettivi flag signedSeller/signedBuyer e memorizza data e ora.
			if("SI".equalsIgnoreCase(safe(sale.getSignedSeller())) && sale.getSignedSellerDate() != null
					&& sale.getSeller() != null) {
				String signature = "Firmato da " + sale.getSeller().getSigner() + " il " + dateTime(sale.getSignedSellerDate());
				text(canvas, regular, signature, 57, 174, 126, 6.5f);
				text(canvas, regular, signature, 57, 27, 126, 6.5f);
			}
			if("SI".equalsIgnoreCase(safe(sale.getSignedBuyer())) && sale.getSignedBuyerDate() != null
					&& sale.getBuyer() != null) {
				String signature = "Firmato da " + sale.getBuyer().getSigner() + " il " + dateTime(sale.getSignedBuyerDate());
				text(canvas, regular, signature, 412, 174, 126, 6.5f);
				text(canvas, regular, signature, 412, 27, 126, 6.5f);
			}
			if(mediatorSignature != null && mediatorSignature.length > 0) {
				ImageData signature = ImageDataFactory.create(mediatorSignature);
				if(isAdmin(sale, FORTE_ADMIN_ID)) {
					// Nei contratti Forte storici firma, dicitura e riga sono un unico blocco.
					canvas.addImage(signature, new Rectangle(235, 164, 126, 40), false);
					canvas.addImage(signature, new Rectangle(235, 13, 126, 40), false);
				} else {
					canvas.addImage(signature, new Rectangle(248, 171, 100, 16), false);
					canvas.addImage(signature, new Rectangle(248, 20, 100, 16), false);
				}
			}
		} finally {
			pdfDoc.close();
		}
	}

	private static void writeMundiRiso(InputStream source, OutputStream destination, Sale sale, byte[] mediatorSignature) throws Exception {
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(destination));
		try {
			PdfPage page = pdfDoc.getPage(1);
			PdfCanvas canvas = new PdfCanvas(page);
			PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			SaleProduct sp = sale.getProduct();

			// Intestazione: numero e data seguono esattamente le caselle del modello.
			boxedText(canvas, bold, value(sale.getNumber()), 89.55f, 12.55f, 5, 747, 8);
			boxedDate(canvas, regular, sale.getSale_date(), 442.2f, 12.56f, 747, 8);

			if(sale.getSeller() != null) {
				text(canvas, bold, safe(sale.getSeller().getBusiness_name()), 30, 684, 245, 8);
				text(canvas, regular, safe(sale.getSeller().getPrimary_phone()), 445, 684, 116, 7);
				text(canvas, regular, safe(sale.getSeller().getLegal_address()), 85, 666, 190, 8);
				text(canvas, regular, safe(sale.getSeller().getSecondary_phone()), 445, 666, 116, 7);
				boxedText(canvas, regular, safe(sale.getSeller().getLegal_zip_code()), 50.8f, 12.8f, 5, 649, 7);
				text(canvas, regular, cityAndProvince(sale.getSeller().getLegal_city(), sale.getSeller().getLegal_province()), 166, 649, 160, 8);
				boxedText(canvas, regular, taxIdentifier(sale.getSeller().getVat_number(), sale.getSeller().getTax_code()), 367.3f, 12.55f, 16, 649, 7);
				text(canvas, regular, firstNonEmpty(sale.getSeller().getPec_email(), sale.getSeller().getPrimary_email()), 51, 631, 148, 7);
			}

			if(!"NO".equalsIgnoreCase(safe(sale.getPickup_printing()))) {
				text(canvas, regular, safe(sale.getPickup_place()), 162, 607, 114, 7);
				text(canvas, regular, safe(sale.getPickup_phone()), 445, 607, 116, 7);
				text(canvas, regular, safe(sale.getPickup_address()), 85, 589, 190, 8);
				text(canvas, regular, safe(sale.getPickup_phone()), 445, 589, 116, 7);
				boxedText(canvas, regular, safe(sale.getPickup_zip_code()), 51.2f, 12.75f, 5, 572, 7);
				text(canvas, regular, cityAndProvince(sale.getPickup_city(), sale.getPickup_province()), 166, 572, 160, 8);
				if(sale.getSeller() != null) {
					boxedText(canvas, regular, taxIdentifier(sale.getSeller().getVat_number(), sale.getSeller().getTax_code()), 367.3f, 12.55f, 16, 572, 7);
				}
			}

			// L'anagrafica Mundi e' prestampata: l'identificativo fiscale viene
			// comunque riscritto dalla vendita, con priorita' alla P. IVA.
			whiteout(canvas, 329.5f, 505.6f, 238, 16.5f);
			if(sale.getBuyer() != null) {
				String buyerVat = safe(sale.getBuyer().getVat_number());
				text(canvas, regular, buyerVat.isEmpty() ? "C.F." : "P. IVA", 336, 509, 28, 7);
				boxedText(canvas, regular, taxIdentifier(buyerVat, sale.getBuyer().getTax_code()), 367.3f, 12.55f, 16, 509, 7);
			}

			if(sale.getAdmin() != null) {
				text(canvas, bold, safe(sale.getAdmin().getFull_name()), 118, 468, 155, 8);
			}

			if(sp != null) {
				text(canvas, bold, safe(sp.getName()), 147, 405, 165, 8);
				boxedText(canvas, bold, integerNumber(sp.getProduct_amount()), 342.8f, 12.35f, 4, 405, 7);
				if(isEndOfStock(sp)) {
					text(canvas, bold, "F.M.", 394, 405, 12, 4.5f);
				}
				boxedText(canvas, bold, value(sp.getGrains_yield()), 455.35f, 12.55f, 2, 405, 7);
				boxedText(canvas, bold, value(sp.getGlobal_yield()), 543.55f, 12.55f, 2, 405, 7);
				text(canvas, bold, safe(sp.getHarvest_year()), 280, 383, 33, 8);
				boxedDate(canvas, regular, sale.getSale_date(), 442.5f, 12.55f, 383, 7);

				boxedText(canvas, regular, safe(sp.getD_impurity()), 89.7f, 12.55f, 4, 346, 7);
				boxedText(canvas, regular, value(sp.getD_red_grains()), 202.7f, 12.7f, 4, 346, 7);
				boxedText(canvas, regular, value(sp.getD_damaged_grains()), 89.7f, 12.55f, 4, 328, 7);
				boxedText(canvas, regular, safe(sp.getD_malformations()), 202.7f, 12.7f, 4, 328, 7);
				boxedText(canvas, regular, safe(sp.getD_pinstriped_grains()), 89.7f, 12.55f, 4, 310, 7);

				String price = !safe(sale.getAlt_list()).isEmpty() ? safe(sale.getAlt_list()) : number(sp.getAmount());
				if(!boxedDecimal(canvas, bold, price, 250, 9)) {
					text(canvas, bold, price, 102, 250, 160, 8);
				}
				if("iva".equalsIgnoreCase(safe(sp.getAmount_type()))) {
					mark(canvas, 282, 267);
				} else {
					mark(canvas, 282, 243);
				}
			}

			if(!boxedDecimal(canvas, bold, safe(sale.getMediation()), 215, 8)) {
				text(canvas, bold, safe(sale.getMediation()), 102, 215, 160, 8);
			}
			text(canvas, regular, paymentDates(sale), 420, 255, 142, 7);
			text(canvas, regular, safe(sale.getPickup_time()), 390, 214, 172, 7);

			if("SI".equalsIgnoreCase(safe(sale.getSignedSeller())) && sale.getSignedSellerDate() != null
					&& sale.getSeller() != null) {
				String signature = "Firmato da " + sale.getSeller().getSigner() + " il " + dateTime(sale.getSignedSellerDate());
				text(canvas, regular, signature, 38, 145, 145, 6.5f);
				text(canvas, regular, signature, 65, 45, 112, 6.5f);
			}
			if("SI".equalsIgnoreCase(safe(sale.getSignedBuyer())) && sale.getSignedBuyerDate() != null
					&& sale.getBuyer() != null) {
				String signature = "Firmato da " + sale.getBuyer().getSigner() + " il " + dateTime(sale.getSignedBuyerDate());
				text(canvas, regular, signature, 412, 145, 145, 6.5f);
				text(canvas, regular, signature, 425, 45, 128, 6.5f);
			}
			if(mediatorSignature != null && mediatorSignature.length > 0) {
				ImageData signature = ImageDataFactory.create(mediatorSignature);
				if(isAdmin(sale, FORTE_ADMIN_ID)) {
					canvas.addImage(signature, new Rectangle(235, 137, 126, 32), false);
					canvas.addImage(signature, new Rectangle(235, 32, 126, 32), false);
				} else {
					canvas.addImage(signature, new Rectangle(248, 144, 100, 16), false);
					canvas.addImage(signature, new Rectangle(248, 44, 100, 16), false);
				}
			}
		} finally {
			pdfDoc.close();
		}
	}

	private static void writeViazzo(InputStream source, OutputStream destination, Sale sale, byte[] mediatorSignature) throws Exception {
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(destination));
		try {
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			SaleProduct sp = sale.getProduct();
			String sellerName = sale.getSeller() == null ? "" : safe(sale.getSeller().getBusiness_name());
			String sellerCity = sale.getSeller() == null ? "" : safe(sale.getSeller().getLegal_city());
			String sellerProvince = sale.getSeller() == null ? "" : safe(sale.getSeller().getLegal_province());
			String sellerAddress = sale.getSeller() == null ? "" : safe(sale.getSeller().getLegal_address());
			String sellerTax = sale.getSeller() == null ? "" : taxIdentifier(sale.getSeller().getVat_number(), sale.getSeller().getTax_code());
			String sellerPhones = sale.getSeller() == null ? "" : phones(sale.getSeller().getPrimary_phone(), sale.getSeller().getSecondary_phone());

			fillField(form, "Contratto n stipulato a il", value(sale.getNumber()), 9);
			fillField(form, "Stipulato A", sellerCity, 8);
			fillField(form, "Stipulato il", date(sale.getSale_date()), 8);

			fillField(form, "na del legale rappresentante Sig Nicola Viazzo  parte acquirente", sellerName, 9);
			fillField(form, "con sede legale in", sellerCity, 9);
			fillField(form, "undefined", sellerProvince, 8);
			fillField(form, "Via", sellerAddress, 9);
			fillField(form, "Cod Fisc e Partita IVA", sellerTax, 9);
			fillField(form, "RIF TELEFONICI", sellerPhones, 8);
			if(!"NO".equalsIgnoreCase(safe(sale.getPickup_printing()))) {
				fillField(form, "MITTENTE", pickupDetails(sale), 7);
			}

			if(sp != null) {
				fillField(form, "VARIETA", safe(sp.getName()), 9);
				fillField(form, "RACCOLTO", safe(sp.getHarvest_year()), 9);
				fillField(form, "ORIGINE", "ITALIA", 9);
				fillField(form, "Quantit\u00e0 TON", quantityWithEndOfStock(sp), 8);
				fillField(form, "UMIDITA.1", safe(sp.getHumidity()), 9);
				fillField(form, "RESA", yields(sp), 9);
				fillField(form, "DIFETTI", safe(sp.getDefects()), 9);
				fillField(form, "TON IVA compresa", viazzoPrice(sp, sale), 8);
			}

			fillField(form, "entro", safe(sale.getPickup_time()), 7);
			fillField(form, "Il pagamento avverr\u00e0 nel pieno rispetto dei modi e dei termini stabiliti dallart 4 del D Lgs n 1982021", paymentDates(sale), 7);
			fillField(form, "al quale la parte acquirente riconosce un compenso provvigionale di", sale.getAdmin() == null ? "" : safe(sale.getAdmin().getFull_name()), 8);
			// Viazzo applica sempre la provvigione concordata di 5 euro per tonnellata.
			fillField(form, "Euro", "5,00", 8);
			fillField(form, "DATA", date(sale.getSale_date()), 7);
			fillField(form, "l\u00ec", sellerCity, 7);

			if("SI".equalsIgnoreCase(safe(sale.getSignedBuyer())) && sale.getSignedBuyerDate() != null && sale.getBuyer() != null) {
				String signature = "Firmato da " + sale.getBuyer().getSigner() + " il " + date(sale.getSignedBuyerDate());
				fillField(form, "PARTE ACQUIRENTE", signature, 5.3f);
				fillField(form, "PARTE ACQUIRENTE_2", signature, 6.5f);
			}
			if("SI".equalsIgnoreCase(safe(sale.getSignedSeller())) && sale.getSignedSellerDate() != null && sale.getSeller() != null) {
				String signature = "Firmato da " + sale.getSeller().getSigner() + " il " + date(sale.getSignedSellerDate());
				fillField(form, "PARTE VENDITRICE", signature, 5.3f);
				fillField(form, "PARTE VENDITRICE_2", signature, 6.5f);
			}
			if(mediatorSignature == null || mediatorSignature.length == 0) {
				fillField(form, "IL MEDIATORE", sale.getAdmin() == null ? "" : safe(sale.getAdmin().getFull_name()), 7);
			}

			form.flattenFields();
			if(mediatorSignature != null && mediatorSignature.length > 0) {
				PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(1));
				ImageData signature = ImageDataFactory.create(mediatorSignature);
				if(isAdmin(sale, FORTE_ADMIN_ID)) {
					canvas.addImage(signature, new Rectangle(448, 78, 124, 22), false);
				} else {
					canvas.addImage(signature, new Rectangle(462, 82, 96, 16), false);
				}
			}
		} finally {
			pdfDoc.close();
		}
	}

	private static void fillField(PdfAcroForm form, String name, String value, float fontSize) {
		PdfFormField field = form.getField(name);
		if(field == null) return;
		field.setFontSize(fontSize);
		field.setValue(safe(value).replaceAll("(?i)null", "").trim());
		field.setReadOnly(true);
	}

	private static String pickupDetails(Sale sale) {
		StringBuilder result = new StringBuilder();
		appendPart(result, safe(sale.getPickup_place()));
		appendPart(result, safe(sale.getPickup_address()));
		appendPart(result, cityAndProvince(sale.getPickup_city(), sale.getPickup_province()));
		appendPart(result, safe(sale.getPickup_phone()));
		return result.toString();
	}

	private static void appendPart(StringBuilder value, String part) {
		if(safe(part).isEmpty()) return;
		if(value.length() > 0) value.append(" - ");
		value.append(part);
	}

	private static String yields(SaleProduct product) {
		String grains = value(product.getGrains_yield());
		String global = value(product.getGlobal_yield());
		if(grains.isEmpty()) return global;
		if(global.isEmpty()) return grains;
		return grains + " / " + global;
	}

	private static String viazzoPrice(SaleProduct product, Sale sale) {
		String price = !safe(sale.getAlt_list()).isEmpty() ? safe(sale.getAlt_list()) : number(product.getAmount());
		if(price.isEmpty()) return "";
		return price + ("iva".equalsIgnoreCase(safe(product.getAmount_type())) ? " IVA INCLUSA" : " IVA ESCLUSA");
	}

	private static void text(PdfCanvas canvas, PdfFont font, String text, float x, float y, float maxWidth, float size) {
		text = safe(text).replace('\n', ' ').replace('\r', ' ').trim();
		if(text.isEmpty()) return;
		float current = size;
		while(current > 5.2f && font.getWidth(text, current) > maxWidth) current -= 0.2f;
		while(text.length() > 1 && font.getWidth(text, current) > maxWidth) text = text.substring(0, text.length() - 1);
		canvas.beginText().setFontAndSize(font, current).moveText(x, y).showText(text).endText();
	}

	private static void boxedText(PdfCanvas canvas, PdfFont font, String text, float startX,
			float cellWidth, int cells, float y, float size) {
		text = safe(text).replaceAll("\\s+", "").toUpperCase(Locale.ITALY);
		int length = Math.min(text.length(), cells);
		for(int i = 0; i < length; i++) {
			String character = text.substring(i, i + 1);
			float width = font.getWidth(character, size);
			float x = startX + (i * cellWidth) + Math.max(0, (cellWidth - width) / 2f);
			text(canvas, font, character, x, y, cellWidth, size);
		}
	}

	private static void boxedDate(PdfCanvas canvas, PdfFont font, java.util.Date value,
			float startX, float cellWidth, float y, float size) {
		if(value == null) return;
		String digits = new SimpleDateFormat("ddMMyyyy").format(value);
		int[] cells = {0, 1, 3, 4, 6, 7, 8, 9};
		for(int i = 0; i < digits.length() && i < cells.length; i++) {
			boxedCharacter(canvas, font, digits.substring(i, i + 1), startX + cells[i] * cellWidth, cellWidth, y, size);
		}
	}

	private static boolean boxedDecimal(PdfCanvas canvas, PdfFont font, String value, float y, float size) {
		String normalized = safe(value).replaceAll("[^0-9,.-]", "");
		if(normalized.isEmpty()) return false;
		int comma = normalized.lastIndexOf(',');
		int dot = normalized.lastIndexOf('.');
		int separator = Math.max(comma, dot);
		String integerPart;
		String decimals;
		if(separator >= 0 && normalized.length() - separator - 1 <= 2) {
			integerPart = normalized.substring(0, separator).replaceAll("[^0-9]", "");
			decimals = normalized.substring(separator + 1).replaceAll("[^0-9]", "");
		} else {
			integerPart = normalized.replaceAll("[^0-9]", "");
			decimals = "";
		}
		if(integerPart.isEmpty()) integerPart = "0";
		while(decimals.length() < 2) decimals += "0";
		if(decimals.length() > 2) decimals = decimals.substring(0, 2);
		if(integerPart.length() > 4) integerPart = integerPart.substring(integerPart.length() - 4);

		float[] integerCenters = {114.8f, 140.2f, 165.1f, 190.5f};
		float[] decimalCenters = {228.2f, 253.2f};
		int offset = integerCenters.length - integerPart.length();
		for(int i = 0; i < integerPart.length(); i++) {
			centeredCharacter(canvas, font, integerPart.substring(i, i + 1), integerCenters[offset + i], y, size);
		}
		for(int i = 0; i < 2; i++) {
			centeredCharacter(canvas, font, decimals.substring(i, i + 1), decimalCenters[i], y, size);
		}
		return true;
	}

	private static void boxedCharacter(PdfCanvas canvas, PdfFont font, String character,
			float cellX, float cellWidth, float y, float size) {
		centeredCharacter(canvas, font, character, cellX + cellWidth / 2f, y, size);
	}

	private static void centeredCharacter(PdfCanvas canvas, PdfFont font, String character,
			float centerX, float y, float size) {
		float width = font.getWidth(character, size);
		text(canvas, font, character, centerX - width / 2f, y, width + 1, size);
	}

	private static void whiteout(PdfCanvas canvas, float x, float y, float width, float height) {
		canvas.saveState();
		canvas.setFillColor(ColorConstants.WHITE);
		canvas.rectangle(x, y, width, height);
		canvas.fill();
		canvas.restoreState();
	}

	private static void mark(PdfCanvas canvas, float x, float y) throws Exception {
		text(canvas, PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), "X", x, y, 10, 10);
	}

	private static void dateParts(PdfCanvas canvas, PdfFont font, java.util.Date value,
			float dayX, float monthX, float yearX, float y) {
		if(value == null) return;
		String[] parts = new SimpleDateFormat("dd/MM/yyyy").format(value).split("/");
		text(canvas, font, parts[0], dayX, y, 18, 8);
		text(canvas, font, parts[1], monthX, y, 18, 8);
		text(canvas, font, parts[2], yearX, y, 30, 8);
	}

	private static String paymentDates(Sale sale) {
		StringBuilder result = new StringBuilder();
		if(sale.getInstallments() != null) {
			for(int i = 0; i < sale.getInstallments().size(); i++) {
				if(sale.getInstallments().get(i).getExpire_date() != null) {
					if(result.length() > 0) result.append(" - ");
					result.append(date(sale.getInstallments().get(i).getExpire_date()));
				}
			}
		}
		if(result.length() == 0 && sale.getPayment() != null) result.append(safe(sale.getPayment().getDescription()));
		return result.toString();
	}

	private static String phones(String first, String second) {
		if(safe(first).isEmpty()) return safe(second);
		if(safe(second).isEmpty()) return safe(first);
		return safe(first) + " / " + safe(second);
	}

	private static String taxIdentifier(String vatNumber, String taxCode) {
		return !safe(vatNumber).isEmpty() ? vatNumber : taxCode;
	}

	private static String firstNonEmpty(String first, String second) {
		return !safe(first).isEmpty() ? first : second;
	}

	private static String cityAndProvince(String city, String province) {
		String result = safe(city);
		if(!safe(province).isEmpty()) result += (result.isEmpty() ? "" : " ") + "(" + safe(province) + ")";
		return result;
	}

	private static String integerNumber(Number value) {
		return value == null ? "" : Long.toString(Math.round(value.doubleValue()));
	}

	private static boolean isEndOfStock(SaleProduct product) {
		if(product == null) return false;
		String value = safe(product.getEnd_of_stock());
		return "true".equalsIgnoreCase(value) || "si".equalsIgnoreCase(value)
				|| "yes".equalsIgnoreCase(value) || "1".equals(value);
	}

	private static String quantityWithEndOfStock(SaleProduct product) {
		if(product == null || product.getProduct_amount() == null) return "";
		String quantity = NumberFormat.getNumberInstance(Locale.ITALY).format(product.getProduct_amount());
		return quantity + (isEndOfStock(product) ? " F.M." : "");
	}

	private static String number(Number number) {
		if(number == null) return "";
		NumberFormat format = NumberFormat.getNumberInstance(Locale.ITALY);
		format.setMaximumFractionDigits(3);
		format.setMinimumFractionDigits(0);
		return format.format(number);
	}

	private static String value(Object value) {
		return value == null ? "" : value.toString();
	}

	private static String date(java.util.Date date) {
		return date == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	private static String dateTime(java.util.Date date) {
		return date == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
	}

	private static String safe(String value) {
		return value == null ? "" : value.trim();
	}

	private static String onlyDigits(String value) {
		return safe(value).replaceAll("[^0-9]", "");
	}

	private static byte[] mediatorSignature(Sale sale) {
		String resource;
		if(isAdmin(sale, GHISONI_ADMIN_ID)) {
			resource = GHISONI_SIGNATURE;
		} else if(isAdmin(sale, FORTE_ADMIN_ID)) {
			resource = FORTE_SIGNATURE;
		} else {
			return null;
		}
		try(InputStream signature = ServletActionContext.getServletContext().getResourceAsStream(resource)) {
			return signature == null ? null : signature.readAllBytes();
		} catch(Exception ignored) {
			return null;
		}
	}

	private static boolean isAdmin(Sale sale, long id) {
		return sale != null && sale.getAdmin() != null && sale.getAdmin().getId() != null
				&& sale.getAdmin().getId().longValue() == id;
	}

	private static void logPdfError(Exception ex) {
		if(ContextSupport.isDebug()) ex.printStackTrace();
		LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
		LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
	}

	
	/**
	 * usato per: 
	 * contratto_ghisoni_01.pdf
	 * contratto_ghisoni_02.pdf
	 * contratto_forte_03.pdf
	 */
	public static String create1(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		if(isMundiRiso(sale)) return createMundiRiso(filename, sale);
		if(isViazzo(sale)) return createViazzo(filename, sale);
		
		try {
			String src = ContextSupport.getUploadPath() + sale.getContract_template().getFilename();	
			String dest = ContextSupport.getUploadPath() + filename;
			
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
				
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			Map<String, PdfFormField> fields = form.getFormFields();
			
			SaleProduct sp = sale.getProduct();
			
			
			try {
				fields.get("numero_contratto").setValue((sale.getNumber().toString() + " redatto in 3 copie").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("numero_contratto").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			
			try {
				fields.get("venditore").setValue((sale.getSeller().getBusiness_name() + " - " + sale.getSeller().getLegal_address() + " - " + sale.getSeller().getLegal_zip_code() + " " + sale.getSeller().getLegal_city() + " " + sale.getSeller().getLegal_province() + " - P.IVA " + sale.getSeller().getVat_number() + " - Tel. " + sale.getSeller().getPrimary_phone() + " " + sale.getSeller().getSecondary_phone()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente").setValue((sale.getBuyer().getBusiness_name() + " - " + sale.getBuyer().getLegal_address() + " - " + sale.getBuyer().getLegal_zip_code() + " " + sale.getBuyer().getLegal_city() + " " + sale.getBuyer().getLegal_province() + " - P.IVA " + sale.getBuyer().getVat_number() + " - Tel. " + sale.getBuyer().getPrimary_phone() + " " + sale.getBuyer().getSecondary_phone()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("intermediario").setValue((SessionSupport.getAdmin().getFirst_name() + " " + SessionSupport.getAdmin().getLast_name()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("intermediario").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("merce").setValue(sp.getName().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("merce").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("vendita_a").setValue(sale.getSale_type().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("vendita_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				
				String prezzo = new String();
				
				if(sale.getAlt_list() != null) {
					if(!sale.getAlt_list().isBlank()) {
						prezzo = sale.getAlt_list();
					} else {
						prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
						
						if(sp.getAmount_type().equals("iva")) {
							prezzo += " iva inclusa";
						} else {
							prezzo += " iva esclusa";
						}
					}
					
				} else {
					prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
					
					if(sp.getAmount_type().equals("iva")) {
						prezzo += " iva inclusa";
					} else {
						prezzo += " iva esclusa";
					}
				}

				
				fields.get("prezzo").setValue(prezzo.toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("prezzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			try {
				fields.get("quantita").setValue(quantityWithEndOfStock(sp).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				ex.printStackTrace();
				try {
					fields.get("quantita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("qualita").setValue(sp.getQuality().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("qualita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("umidita").setValue(sp.getHumidity().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("umidita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("anno_raccolto").setValue(sp.getHarvest_year().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("anno_raccolto").setReadOnly(true);
				} catch(Exception ex2) {}
				
			}
			
			try {
				fields.get("resa_grani_interi").setValue(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_grani_interi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("resa_globale").setValue(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_globale").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("difetti").setValue(sp.getDefects().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("difetti").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_red_grains() > 0) {
					fields.get("grani_rossi").setValue(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_rossi").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_rossi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("impurita_varietali").setValue(sp.getD_impurity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("impurita_varietali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_damaged_grains() > 0) {
					fields.get("grani_danneggiati").setValue(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_danneggiati").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("corpi_estranei_commestibili").setValue(sp.getD_edible_extra().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("corpi_estranei_commestibili").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("disformita_naturali").setValue(sp.getD_malformations().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("disformita_naturali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_gessati").setValue(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_gessati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_danneggiati_calore").setValue(sp.getD_heated_damaged_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati_calore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("corpi_estranei_non_commestibili").setValue(sp.getD_non_edible_extra().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("corpi_estranei_non_commestibili").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("ritiro_consegna").setValue(sale.getPickup_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("ritiro_consegna").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("tempo").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("tempo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("modalita").setValue(sale.getPickup_way().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("modalita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("luogo").setValue((sale.getPickup_place() + " - " + sale.getPickup_address() + " " + sale.getPickup_city() + " " + sale.getPickup_province() + " - Tel. " + sale.getPickup_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				String dates = "";
				for(int i = 0; i < sale.getInstallments().size(); i++) {
					dates += "Scad. : " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getInstallments().get(i).getExpire_date()) + " ";
				}
				//aggiungere le date successive di pagamenti
				fields.get("pagamento").setValue(sale.getPayment().getDescription().toUpperCase().replaceAll("NULL", "") + " " + dates.toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("pagamento").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("mediazione").setValue(sale.getMediation().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("mediazione").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("imballaggio").setValue(sale.getPackaging().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("imballaggio").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("condizioni_particolari").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				//fields.get("luogo_1").setValue("Belgioioso").setReadOnly(true);
				fields.get("luogo_1").setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("data_1").setValue(new SimpleDateFormat("dd/MM/yyyy").format(sale.getSale_date())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
							
			
			
			pdfDoc.close();
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return filename;
		
	}
	
	public static String create2(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		if(isMundiRiso(sale)) return createMundiRiso(filename, sale);
		if(isViazzo(sale)) return createViazzo(filename, sale);
		boolean forteRice = FORTE_RICE_TEMPLATE.equalsIgnoreCase(sale.getContract_template().getFilename());
		try (InputStream source = forteRice
				? ServletActionContext.getServletContext().getResourceAsStream("/WEB-INF/templates/" + FORTE_RICE_TEMPLATE)
				: new FileInputStream(ContextSupport.getUploadPath() + sale.getContract_template().getFilename())) {
			String dest = ContextSupport.getUploadPath() + filename;
			writeContract2(source, dest, sale, forteRice);
		} catch(Exception ex) {
			logPdfError(ex);
		}
		return filename;
	}

	static void writeContract2(InputStream source, String destination, Sale sale, boolean forteRice) throws Exception {
		try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), new PdfWriter(destination))) {
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			Map<String, PdfFormField> fields = form.getFormFields();
			if(forteRice) {
				PdfTextFormField number = (PdfTextFormField) fields.get("numero_contratto");
				number.setMaxLen(6);
				number.setComb(true);
				fields.get("acquirente").getWidgets().get(0)
						.setRectangle(new PdfArray(new Rectangle(88, 579, 363, 17)));
				placeForteRiceField(fields.get("resa_grani_interi"), new Rectangle(476, 458, 32, 18), 10, PdfFormField.ALIGN_RIGHT);
				placeForteRiceField(fields.get("resa_globale"), new Rectangle(535, 458, 25, 18), 10, PdfFormField.ALIGN_RIGHT);
				placeForteRiceField(fields.get("carico_motrici"), new Rectangle(460, 232, 13, 14), 10, PdfFormField.ALIGN_CENTER);
				placeForteRiceField(fields.get("carico_al_coperto"), new Rectangle(536, 232, 13, 14), 10, PdfFormField.ALIGN_CENTER);
				placeForteRiceField(fields.get("data_ritiro"), new Rectangle(409, 269, 151, 15), 9, PdfFormField.ALIGN_LEFT);
				placeForteRiceField(fields.get("luogo_ritiro"), new Rectangle(135, 638, 198, 17), 10, PdfFormField.ALIGN_LEFT);
				placeForteRiceField(fields.get("venditore_provincia"), new Rectangle(295, 675, 36, 17), 10, PdfFormField.ALIGN_LEFT);
				placeForteRiceField(fields.get("lrit_provincia"), new Rectangle(295, 602, 36, 17), 10, PdfFormField.ALIGN_LEFT);
			}
			
			SaleProduct sp = sale.getProduct();
			
			try {
				fields.get("numero_contratto").setValue((sale.getNumber().toString()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("numero_contratto").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			
			try {
				fields.get("venditore").setValue((sale.getSeller().getBusiness_name().toUpperCase().replaceAll("NULL", ""))).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_indirizzo").setValue((sale.getSeller().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_comune").setValue((sale.getSeller().getLegal_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_provincia").setValue((sale.getSeller().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel").setValue((sale.getSeller().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel_2").setValue((sale.getSeller().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				String tax = forteRice ? taxIdentifier(sale.getSeller().getVat_number(), sale.getSeller().getTax_code()) : sale.getSeller().getVat_number();
				fields.get("venditore_piva").setValue(safe(tax).toUpperCase(Locale.ITALY)).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_email").setValue((sale.getSeller().getPrimary_email()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_email").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			
			
			try {
				if(sale.getPickup_printing().contentEquals("SI")) {
					try {
						fields.get("luogo_ritiro").setValue((sale.getPickup_place()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("luogo_ritiro").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_indirizzo").setValue((sale.getPickup_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_indirizzo").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_comune").setValue((sale.getPickup_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_comune").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_cap").setValue((sale.getSeller().getLegal_zip_code())).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_cap").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_provincia").setValue((sale.getPickup_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_provincia").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_tel").setValue((sale.getSeller().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_tel").setReadOnly(true);
						} catch(Exception ex2) {}
					}
				}
			} catch(Exception ex) {
				try {
					fields.get("luogo_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_comune").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_cap").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("acquirente").setValue((sale.getBuyer().getBusiness_name()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_indirizzo").setValue((sale.getBuyer().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				String city = sale.getBuyer().getLegal_city();
				if(forteRice) {
					city = (safe(sale.getBuyer().getLegal_zip_code()) + " "
							+ cityAndProvince(city, sale.getBuyer().getLegal_province())).trim();
				}
				fields.get("acquirente_comune").setValue(safe(city).toUpperCase(Locale.ITALY)).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_provincia").setValue((sale.getBuyer().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel").setValue((sale.getBuyer().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel_2").setValue((sale.getBuyer().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(forteRice && safe(sale.getBuyer().getVat_number()).isEmpty()) {
					placeForteRiceField(fields.get("acquirente_piva"), new Rectangle(357, 542, 96, 17), 10, PdfFormField.ALIGN_LEFT);
					fields.get("acquirente_piva").setValue(safe(sale.getBuyer().getTax_code())).setReadOnly(true);
				} else {
					fields.get("acquirente_piva").setValue((sale.getBuyer().getVat_number()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("acquirente_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			
			try {
				fields.get("merce").setValue(sp.getName().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("merce").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("vendita_a").setValue(sale.getSale_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("vendita_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				
				String prezzo = new String();
				
				if(forteRice) {
					prezzo = firstNonEmpty(sale.getAlt_list(), sp.getAmount() == null ? ""
							: NumberFormat.getCurrencyInstance(Locale.ITALY).format(sp.getAmount()).replace("€", "").trim());
				} else if(sale.getAlt_list() != null) {
					if(!sale.getAlt_list().isBlank()) {
						prezzo = sale.getAlt_list();
					} else {
						prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
						
						if(sp.getAmount_type().equals("iva")) {
							prezzo += " iva inclusa";
						} else {
							prezzo += " iva esclusa";
						}
					}
					
				} else {
					prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
					
					if(sp.getAmount_type().equals("iva")) {
						prezzo += " iva inclusa";
					} else {
						prezzo += " iva esclusa";
					}
				}

				
				fields.get("prezzo").setValue(prezzo.toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("prezzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			


			
			try {
				fields.get("quantita").setValue(quantityWithEndOfStock(sp).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("quantita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("umidita").setValue(sp.getHumidity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("umidita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("anno_raccolto").setValue(sp.getHarvest_year().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("anno_raccolto").setReadOnly(true);
				} catch(Exception ex2) {}
				
			}
			
			try {
				fields.get("resa_grani_interi").setValue(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_grani_interi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("resa_globale").setValue(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_globale").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("difetti").setValue(sp.getDefects().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("difetti").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_red_grains() > 0) {
					fields.get("grani_rossi").setValue(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_rossi").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_rossi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("impurita_varietali").setValue(sp.getD_impurity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("impurita_varietali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_damaged_grains() > 0) {
					fields.get("grani_danneggiati").setValue(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_danneggiati").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("disformita_naturali").setValue(sp.getD_malformations().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("disformita_naturali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_gessati").setValue(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_gessati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("triciclazolo").setValue(sp.getTriciclazolo().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("triciclazolo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			

			
			try {
				fields.get("ritiro_consegna").setValue(sale.getPickup_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("ritiro_consegna").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("data_ritiro").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("modalita").setValue(sale.getPickup_way().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("modalita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("luogo").setValue(sale.getPickup_place().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(forteRice) {
					fillForteRiceField(fields.get("scadenza_pagamento"), paymentDates(sale),
							new Rectangle(436, 318, 123, 32), 9);
				} else {
					String dates = "";
					for(int i = 0; i < sale.getInstallments().size(); i++) {
						dates += new SimpleDateFormat("dd/MM/yyyy").format(sale.getInstallments().get(i).getExpire_date()) + " ";
					}
					if(dates.equals("") || sale.getPayment().getInstallments() == 0) {
						fields.get("scadenza_pagamento").setValue(sale.getPayment().getDescription()).setReadOnly(true);
					} else {
						fields.get("scadenza_pagamento").setValue(dates.toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					}
				}
			} catch(Exception ex) {
				try {
					fields.get("scadenza_pagamento").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("mediazione").setValue(sale.getMediation().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("mediazione").setReadOnly(true);
				} catch(Exception ex2) {}
			}

			
			try {
				if(forteRice) {
					fillForteRiceField(fields.get("condizioni_particolari"), sale.getParticular_conditions(),
							new Rectangle(34, 103, 526, 45), 9);
				} else {
					fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("condizioni_particolari").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("data_1").setValue(new SimpleDateFormat("dd/MM/yyyy").format(sale.getSale_date())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getCarico_motrici().equals("SI")) {
					fields.get("carico_motrici").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("carico_motrici").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getCarico_al_coperto().equals("SI")) {
					fields.get("carico_al_coperto").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("carico_al_coperto").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(!forteRice && sale.getDisinfestato().equals("SI")) {
					fields.get("disinfestato").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("disinfestato").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getProduct().getAmount_type().equals("iva")) {
					fields.get("iva_inclusa").setValue("X").setReadOnly(true);
				} else {
					fields.get("iva_esclusa").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("iva_inclusa").setReadOnly(true);
					fields.get("iva_esclusa").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				if(forteRice) {
					fillForteRiceSignature(fields.get("signed_seller"), sale.getSignedSeller(), sale.getSeller().getSigner(), sale.getSignedSellerDate());
				} else if(sale.getSignedSeller().equals("SI")) {
					fields.get("signed_seller").setValue("Firmato da " + sale.getSeller().getReferent() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedSellerDate())).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("signed_seller").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(forteRice) {
					fillForteRiceSignature(fields.get("signed_buyer"), sale.getSignedBuyer(), sale.getBuyer().getSigner(), sale.getSignedBuyerDate());
				} else if(sale.getSignedBuyer().equals("SI")) {
					fields.get("signed_buyer").setValue("Firmato da " + sale.getBuyer().getReferent() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedBuyerDate())).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("signed_buyer").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			if(forteRice) {
				if("SI".equalsIgnoreCase(safe(sale.getDisinfestato()))) {
					text(new PdfCanvas(pdfDoc.getPage(1)), PdfFontFactory.createFont(StandardFonts.HELVETICA),
							"Merce disinfestata", 110, 363, 170, 8);
				}
				for(PdfFormField field : fields.values()) {
					field.setReadOnly(true);
				}
			}
		}
	}

	private static void fillForteRiceField(PdfFormField field, String value, Rectangle bounds, float size) throws Exception {
		placeForteRiceField(field, bounds, size, PdfFormField.ALIGN_LEFT);
		field.setFieldFlag(PdfFormField.FF_MULTILINE);
		field.setValue(safe(value)).setReadOnly(true);
	}

	private static void fillForteRiceSignature(PdfFormField field, String signed, String signer, java.util.Date signedDate) throws Exception {
		if(!"SI".equalsIgnoreCase(safe(signed)) || signedDate == null) return;
		String signature = "Firmato da " + safe(signer) + " il " + date(signedDate);
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		float width = field.getWidgets().get(0).getRectangle().toRectangle().getWidth();
		field.setFieldFlag(PdfFormField.FF_MULTILINE, false);
		field.setFont(font);
		field.setFontSize(Math.min(8, (width - 6) / font.getWidth(signature, 1)));
		field.setValue(signature).setReadOnly(true);
	}

	private static void placeForteRiceField(PdfFormField field, Rectangle bounds, float size, int alignment) throws Exception {
		field.getWidgets().get(0).setRectangle(new PdfArray(bounds));
		field.setJustification(alignment);
		field.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA));
		field.setFontSize(size);
		field.setColor(ColorConstants.BLACK);
	}
	
	
	// CURTI
	public static String create3(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		if(isMundiRiso(sale)) return createMundiRiso(filename, sale);
		if(isViazzo(sale)) return createViazzo(filename, sale);
		
		
		try {
			String src = ContextSupport.getUploadPath() + sale.getContract_template().getFilename();	
			String dest = ContextSupport.getUploadPath() + filename;
			
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
				
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			Map<String, PdfFormField> fields = form.getFormFields();
			
			SaleProduct sp = sale.getProduct();
			
			try {
				fields.get("numero_contratto").setValue((sale.getNumber().toString()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("numero_contratto").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			
			try {
				fields.get("venditore").setValue((sale.getSeller().getBusiness_name().toUpperCase().replaceAll("NULL", ""))).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_indirizzo").setValue((sale.getSeller().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_cap").setValue((sale.getSeller().getLegal_zip_code()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_cap").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_comune").setValue((sale.getSeller().getLegal_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_provincia").setValue((sale.getSeller().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel").setValue((sale.getSeller().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel_2").setValue((sale.getSeller().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_piva").setValue((sale.getSeller().getVat_number()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_email").setValue((sale.getSeller().getPrimary_email()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_email").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getPickup_printing().contentEquals("SI")) {
					try {
						fields.get("luogo_ritiro").setValue((sale.getPickup_place()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("luogo_ritiro").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_indirizzo").setValue((sale.getPickup_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_indirizzo").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_comune").setValue((sale.getPickup_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_comune").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_cap").setValue((sale.getPickup_zip_code())).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_cap").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_provincia").setValue((sale.getPickup_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_provincia").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_tel").setValue((sale.getPickup_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_tel").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					

				}
			} catch(Exception ex) {
				try {
					fields.get("luogo_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_comune").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_cap").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_provincia").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			

			
			
			
			
			
			
			
			try {
				fields.get("acquirente").setValue((sale.getBuyer().getBusiness_name()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_indirizzo").setValue((sale.getBuyer().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_comune").setValue((sale.getBuyer().getLegal_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_provincia").setValue((sale.getBuyer().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel").setValue((sale.getBuyer().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel_2").setValue((sale.getBuyer().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_piva").setValue((sale.getBuyer().getVat_number()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			
			try {
				fields.get("merce").setValue(sp.getName().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("merce").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("vendita_a").setValue(sale.getSale_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("vendita_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				
				String prezzo = new String();
				
				if(sale.getAlt_list() != null) {
					if(!sale.getAlt_list().isBlank()) {
						prezzo = sale.getAlt_list();
					} else {
						prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
						

					}
					
				} else {
					prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
					

				}

				
				fields.get("prezzo").setValue(prezzo.toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("prezzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			


			
			try {
				fields.get("quantita").setValue(quantityWithEndOfStock(sp).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("quantita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("umidita").setValue(sp.getHumidity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("umidita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("anno_raccolto").setValue(sp.getHarvest_year().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("anno_raccolto").setReadOnly(true);
				} catch(Exception ex2) {}
				
			}
			
			try {
				fields.get("resa_grani_interi").setValue(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_grani_interi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("resa_globale").setValue(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_globale").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("difetti").setValue(sp.getDefects().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("difetti").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_red_grains() > 0) {
					fields.get("grani_rossi").setValue(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_rossi").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_rossi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("impurita_varietali").setValue(sp.getD_impurity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("impurita_varietali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_damaged_grains() > 0) {
					fields.get("grani_danneggiati").setValue(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_danneggiati").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("disformita_naturali").setValue(sp.getD_malformations().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("disformita_naturali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_gessati").setValue(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_gessati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("triciclazolo").setValue(sp.getTriciclazolo().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("triciclazolo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("ritiro_consegna").setValue(sale.getPickup_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("ritiro_consegna").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("tempo_ritiro").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("tempo_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("modalita").setValue(sale.getPickup_way().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("modalita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("luogo").setValue(sale.getPickup_place().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				String dates = "";
				for(int i = 0; i < sale.getInstallments().size(); i++) {
					dates += new SimpleDateFormat("dd/MM/yyyy").format(sale.getInstallments().get(i).getExpire_date()) + " ";
				}
				//aggiungere le date successive di pagamenti
				fields.get("scadenza_pagamento").setValue(dates.toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("scadenza_pagamento").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("mediazione").setValue(sale.getMediation().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("mediazione").setReadOnly(true);
				} catch(Exception ex2) {}
			}

			
			try {
				fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("condizioni_particolari").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
		
				fields.get("firma_v").setValue("Firmato da " + sale.getSeller().getSigner() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedSellerDate())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("firma_v").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("firma_a").setValue("Firmato da " + sale.getBuyer().getSigner() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedBuyerDate())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("firma_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("data_1").setValue(new SimpleDateFormat("dd/MM/yyyy").format(sale.getSale_date())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getCarico_motrici().equals("SI")) {
					fields.get("motrice").setValue("X").setReadOnly(true);
				} else {
					fields.get("autotreno").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("motrice").setReadOnly(true);
					fields.get("autotreno").setValue("X").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getProduct().getAmount_type().equals("iva")) {
					fields.get("iva_inclusa").setValue("X").setReadOnly(true);
				} else {
					fields.get("iva_esclusa").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("iva_inclusa").setReadOnly(true);
					fields.get("iva_esclusa").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			pdfDoc.close();
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return filename;
	}
	
	
	public static String create5(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		if(isMundiRiso(sale)) return createMundiRiso(filename, sale);
		if(isViazzo(sale)) return createViazzo(filename, sale);
		
		
		try {
			String src = ContextSupport.getUploadPath() + sale.getContract_template().getFilename();	
			String dest = ContextSupport.getUploadPath() + filename;
			
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
				
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			Map<String, PdfFormField> fields = form.getFormFields();
			
			SaleProduct sp = sale.getProduct();
			
			try {
				fields.get("numero_contratto").setValue((sale.getNumber().toString()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("numero_contratto").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			
			try {
				fields.get("venditore").setValue((sale.getSeller().getBusiness_name().toUpperCase().replaceAll("NULL", ""))).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_indirizzo").setValue((sale.getSeller().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_cap").setValue((sale.getSeller().getLegal_zip_code()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_cap").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_comune").setValue((sale.getSeller().getLegal_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_provincia").setValue((sale.getSeller().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel").setValue((sale.getSeller().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_tel_2").setValue((sale.getSeller().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_piva").setValue((sale.getSeller().getVat_number()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("venditore_email").setValue((sale.getSeller().getPrimary_email()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore_email").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getPickup_printing().contentEquals("SI")) {
					try {
						fields.get("luogo_ritiro").setValue((sale.getPickup_place()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("luogo_ritiro").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_indirizzo").setValue((sale.getPickup_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_indirizzo").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_comune").setValue((sale.getPickup_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_comune").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_cap").setValue((sale.getPickup_zip_code())).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_cap").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_provincia").setValue((sale.getPickup_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_provincia").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("lrit_tel").setValue(("Mittente: " + sale.getPickup_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("lrit_tel").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					

				}
			} catch(Exception ex) {
				try {
					fields.get("luogo_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_comune").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_cap").setReadOnly(true);
				} catch(Exception ex2) {}
				try {
					fields.get("lrit_provincia").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			

			
			
			
			
			
			
			
			try {
				fields.get("acquirente").setValue((sale.getBuyer().getBusiness_name()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_indirizzo").setValue((sale.getBuyer().getLegal_address()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_indirizzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_comune").setValue((sale.getBuyer().getLegal_city()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_comune").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_provincia").setValue((sale.getBuyer().getLegal_province()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_provincia").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel").setValue((sale.getBuyer().getPrimary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_tel_2").setValue((sale.getBuyer().getSecondary_phone()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_tel_2").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente_piva").setValue((sale.getBuyer().getVat_number()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente_piva").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			
			try {
				fields.get("merce").setValue(sp.getName().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("merce").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("vendita_a").setValue(sale.getSale_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("vendita_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				
				String prezzo = new String();
				
				if(sale.getAlt_list() != null) {
					if(!sale.getAlt_list().isBlank()) {
						prezzo = sale.getAlt_list();
					} else {
						prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
						

					}
					
				} else {
					prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
					

				}

				
				fields.get("prezzo").setValue(prezzo.toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("prezzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			


			
			try {
				fields.get("quantita").setValue(quantityWithEndOfStock(sp).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("quantita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("umidita").setValue(sp.getHumidity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("umidita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("anno_raccolto").setValue(sp.getHarvest_year().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("anno_raccolto").setReadOnly(true);
				} catch(Exception ex2) {}
				
			}
			
			try {
				fields.get("resa_grani_interi").setValue(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_grani_interi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("resa_globale").setValue(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_globale").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("difetti").setValue(sp.getDefects().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("difetti").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_red_grains() > 0) {
					fields.get("grani_rossi").setValue(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_rossi").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_rossi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("impurita_varietali").setValue(sp.getD_impurity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("impurita_varietali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_damaged_grains() > 0) {
					fields.get("grani_danneggiati").setValue(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_danneggiati").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("disformita_naturali").setValue(sp.getD_malformations().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("disformita_naturali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_gessati").setValue(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_gessati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("triciclazolo").setValue(sp.getTriciclazolo().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("triciclazolo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("ritiro_consegna").setValue(sale.getPickup_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("ritiro_consegna").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("tempo_ritiro").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("tempo_ritiro").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("modalita").setValue(sale.getPickup_way().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("modalita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("luogo").setValue(sale.getPickup_place().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				String dates = "";
				for(int i = 0; i < sale.getInstallments().size(); i++) {
					dates += new SimpleDateFormat("dd/MM/yyyy").format(sale.getInstallments().get(i).getExpire_date()) + " ";
				}
				//aggiungere le date successive di pagamenti
				fields.get("scadenza_pagamento").setValue(dates.toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("scadenza_pagamento").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("mediazione").setValue(sale.getMediation().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("mediazione").setReadOnly(true);
				} catch(Exception ex2) {}
			}

			
			try {
				fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("condizioni_particolari").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
		
				fields.get("firma_v").setValue("Firmato da " + sale.getSeller().getSigner() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedSellerDate())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("firma_v").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("firma_a").setValue("Firmato da " + sale.getBuyer().getSigner() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedBuyerDate())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("firma_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			try {
				fields.get("data_1").setValue(new SimpleDateFormat("dd/MM/yyyy").format(sale.getSale_date())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getCarico_motrici().equals("SI")) {
					fields.get("motrice").setValue("X").setReadOnly(true);
				} else {
					fields.get("autotreno").setValue("X").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("motrice").setReadOnly(true);
					fields.get("autotreno").setValue("X").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getProduct().getAmount_type().equals("iva")) {
					fields.get("iva").setValue("iva inclusa").setReadOnly(true);
				} else {
					fields.get("iva").setValue("+ iva").setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("iva").setReadOnly(true);
					
				} catch(Exception ex2) {}
			}
			
			pdfDoc.close();
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return filename;
	}
	
	
	
	public static String create4(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		if(isMundiRiso(sale)) return createMundiRiso(filename, sale);
		if(isViazzo(sale)) return createViazzo(filename, sale);
		
		try {
			String src = ContextSupport.getUploadPath() + sale.getContract_template().getFilename();	
			String dest = ContextSupport.getUploadPath() + filename;
			
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
				
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			Map<String, PdfFormField> fields = form.getFormFields();
			
			SaleProduct sp = sale.getProduct();
			
			
			try {
				fields.get("numero_contratto").setValue((sale.getNumber().toString() + " redatto in 3 copie").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("numero_contratto").setReadOnly(true);
				} catch(Exception ex2) {}

			}
			
			try {
				fields.get("venditore").setValue((sale.getSeller().getBusiness_name() + " - " + sale.getSeller().getLegal_address() + " - " + sale.getSeller().getLegal_zip_code() + " " + sale.getSeller().getLegal_city() + " " + sale.getSeller().getLegal_province() + " - P.IVA " + sale.getSeller().getVat_number() + " - Tel. " + sale.getSeller().getPrimary_phone()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("venditore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("acquirente").setValue((sale.getBuyer().getBusiness_name() + " - " + sale.getBuyer().getLegal_address() + " - " + sale.getBuyer().getLegal_zip_code() + " " + sale.getBuyer().getLegal_city() + " " + sale.getBuyer().getLegal_province() + " - P.IVA " + sale.getBuyer().getVat_number() + " - Tel. " + sale.getBuyer().getPrimary_phone()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("acquirente").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("intermediario").setValue((SessionSupport.getAdmin().getFirst_name() + " " + SessionSupport.getAdmin().getLast_name()).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("intermediario").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("merce").setValue(sp.getName().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("merce").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("vendita_a").setValue(sale.getSale_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("vendita_a").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				
				String prezzo = new String();
				
				if(sale.getAlt_list() != null) {
					if(!sale.getAlt_list().isBlank()) {
						prezzo = sale.getAlt_list();
					} else {
						prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
						
						if(sp.getAmount_type().equals("iva")) {
							prezzo += " iva inclusa";
						} else {
							prezzo += " iva esclusa";
						}
					}
					
				} else {
					prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("€", "");
					
					if(sp.getAmount_type().equals("iva")) {
						prezzo += " iva inclusa";
					} else {
						prezzo += " iva esclusa";
					}
				}

				
				fields.get("prezzo").setValue(prezzo.toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("prezzo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			

			


			
			try {
				fields.get("quantita").setValue(quantityWithEndOfStock(sp).toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("quantita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("qualita").setValue(sp.getQuality().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("qualita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("umidita").setValue(sp.getHumidity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("umidita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("anno_raccolto").setValue(sp.getHarvest_year().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("anno_raccolto").setReadOnly(true);
				} catch(Exception ex2) {}
				
			}
			
			try {
				fields.get("resa_grani_interi").setValue(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_grani_interi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("resa_globale").setValue(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("resa_globale").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("difetti").setValue(sp.getDefects().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("difetti").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_red_grains() > 0) {
					fields.get("grani_rossi").setValue(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_rossi").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_rossi").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("impurita_varietali").setValue(sp.getD_impurity().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("impurita_varietali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sp.getD_damaged_grains() > 0) {
					fields.get("grani_danneggiati").setValue(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("grani_danneggiati").setReadOnly(true);
				}
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("corpi_estranei_commestibili").setValue(sp.getD_edible_extra().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("corpi_estranei_commestibili").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("disformita_naturali").setValue(sp.getD_malformations().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("disformita_naturali").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_gessati").setValue(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_gessati").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("grani_danneggiati_calore").setValue(sp.getD_heated_damaged_grains().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("grani_danneggiati_calore").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("corpi_estranei_non_commestibili").setValue(sp.getD_non_edible_extra().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("corpi_estranei_non_commestibili").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("ritiro_consegna").setValue(sale.getPickup_type().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("ritiro_consegna").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getPickup_type().equals("Ritiro")) {
					fields.get("tempo_ritiro").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else if(sale.getPickup_type().equals("Consegna")) {
					fields.get("tempo_consegna").setValue(sale.getPickup_time().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("tempo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("modalita").setValue(sale.getPickup_way().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("modalita").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			
			
			try {
				if(sale.getPickup_type().equals("Ritiro")) {
					fields.get("luogo_ritiro").setValue((sale.getPickup_place() + " - " + sale.getPickup_address() + " " + sale.getPickup_city() + " " + sale.getPickup_province()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
				} else if(sale.getPickup_type().equals("Consegna")) {
					fields.get("luogo_consegna").setValue((sale.getPickup_place() + " - " + sale.getPickup_address() + " " + sale.getPickup_city() + " " + sale.getPickup_province()).toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("luogo").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {

				//aggiungere le date successive di pagamenti
				fields.get("pagamento").setValue(sale.getPayment().getDescription().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("pagamento").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("mediazione").setValue(sale.getMediation().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("mediazione").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("imballaggio").setValue(sale.getPackaging().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("imballaggio").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("condizioni_particolari").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				//fields.get("luogo_1").setValue("Belgioioso").setReadOnly(true);
				fields.get("luogo_1").setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("luogo_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				fields.get("data_1").setValue(new SimpleDateFormat("dd/MM/yyyy").format(sale.getSale_date())).setReadOnly(true);
			} catch(Exception ex) {
				try {
					fields.get("data_1").setReadOnly(true);
				} catch(Exception ex2) {}
			}
							
			
			
			pdfDoc.close();
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return filename;
		
	}
	
}
