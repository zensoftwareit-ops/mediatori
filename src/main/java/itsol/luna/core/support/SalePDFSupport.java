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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;


import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;

public class SalePDFSupport {

	private static final String RISO_GALLO_VAT = "01407630183";
	private static final String RISO_GALLO_TEMPLATE = "/WEB-INF/templates/contratto_riso_gallo_2026.pdf";
	private static final String MEDIATOR_SIGNATURE = "/images/firma_ghisoni.jpg";

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

	public static String createRisoGallo(String filename, Sale sale) {
		String dest = ContextSupport.getUploadPath() + filename;
		try(InputStream src = ServletActionContext.getServletContext().getResourceAsStream(RISO_GALLO_TEMPLATE);
			OutputStream out = new FileOutputStream(dest)) {
			if(src == null) {
				throw new IllegalStateException("Modello contratto Riso Gallo non trovato: " + RISO_GALLO_TEMPLATE);
			}
			writeRisoGallo(src, out, sale, mediatorSignature());
		} catch(Exception ex) {
			logPdfError(ex);
		}
		return filename;
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
			text(canvas, bold, value(sale.getNumber()), 84, 757, 50, 8);
			text(canvas, regular, safe(sale.getSeller() == null ? null : sale.getSeller().getLegal_city()), 218, 757, 190, 8);
			dateParts(canvas, regular, sale.getSale_date(), 458, 497, 531, 757);

			// Venditore.
			if(sale.getSeller() != null) {
				text(canvas, bold, safe(sale.getSeller().getBusiness_name()), 96, 716, 225, 8);
				text(canvas, regular, safe(sale.getSeller().getInternal_code()), 337, 716, 105, 7);
				text(canvas, regular, phones(sale.getSeller().getPrimary_phone(), sale.getSeller().getSecondary_phone()), 472, 716, 90, 7);
				text(canvas, regular, safe(sale.getSeller().getLegal_address()), 84, 698, 238, 8);
				text(canvas, regular, phones(sale.getSeller().getPrimary_phone(), sale.getSeller().getSecondary_phone()), 472, 698, 90, 7);
				text(canvas, regular, safe(sale.getSeller().getLegal_city()), 83, 680, 180, 8);
				text(canvas, regular, safe(sale.getSeller().getLegal_province()), 310, 680, 20, 8);
				String fiscal = firstNonBlank(sale.getSeller().getTax_code(), sale.getSeller().getVat_number());
				text(canvas, regular, safe(fiscal), 390, 680, 165, 8);
				text(canvas, regular, safe(sale.getSeller().getPrimary_email()), 84, 662, 130, 7);
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
				text(canvas, bold, safe(sp.getName()), 107, 460, 155, 8);
				text(canvas, bold, number(sp.getProduct_amount()), 357, 460, 83, 8);
				text(canvas, bold, value(sp.getGrains_yield()), 464, 460, 42, 8);
				text(canvas, bold, value(sp.getGlobal_yield()), 529, 460, 42, 8);
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
					mark(canvas, 290, 346);
				} else {
					mark(canvas, 290, 324);
				}
			}

			text(canvas, bold, safe(sale.getMediation()), 123, 301, 142, 8);
			text(canvas, regular, paymentDates(sale), 438, 312, 126, 7);
			text(canvas, regular, safe(sale.getPickup_time()), 414, 274, 150, 8);
			if("SI".equalsIgnoreCase(safe(sale.getCarico_motrici()))) {
				mark(canvas, 463, 239);
			}
			if("SI".equalsIgnoreCase(safe(sale.getCarico_al_coperto()))) {
				mark(canvas, 553, 239);
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
				canvas.addImage(signature, new Rectangle(248, 171, 100, 16), false);
				canvas.addImage(signature, new Rectangle(248, 20, 100, 16), false);
			}
		} finally {
			pdfDoc.close();
		}
	}

	private static void text(PdfCanvas canvas, PdfFont font, String text, float x, float y, float maxWidth, float size) {
		text = safe(text).replace('\n', ' ').replace('\r', ' ').trim();
		if(text.isEmpty()) return;
		float current = size;
		while(current > 5.2f && font.getWidth(text, current) > maxWidth) current -= 0.2f;
		while(text.length() > 1 && font.getWidth(text, current) > maxWidth) text = text.substring(0, text.length() - 1);
		canvas.beginText().setFontAndSize(font, current).moveText(x, y).showText(text).endText();
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

	private static String firstNonBlank(String first, String second) {
		return !safe(first).isEmpty() ? first : second;
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

	private static byte[] mediatorSignature() {
		try(InputStream signature = ServletActionContext.getServletContext().getResourceAsStream(MEDIATOR_SIGNATURE)) {
			return signature == null ? null : signature.readAllBytes();
		} catch(Exception ignored) {
			return null;
		}
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
				if(sp.getEnd_of_stock().contentEquals("true")) {
					fields.get("quantita").setValue((NumberFormat.getNumberInstance().format(sp.getProduct_amount()) + "       FINE MAGAZZINO").toUpperCase().replaceAll("NULL", "").replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("quantita").setValue(NumberFormat.getNumberInstance().format(sp.getProduct_amount()).replaceAll("NULL", "")).setReadOnly(true);
				}
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
				if(sp.getEnd_of_stock().contentEquals("true")) {
					fields.get("quantita").setValue((NumberFormat.getNumberInstance().format(sp.getProduct_amount())+ " F. M.").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("quantita").setValue(NumberFormat.getNumberInstance().format(sp.getProduct_amount())).setReadOnly(true);
				}
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
				String dates = "";
				for(int i = 0; i < sale.getInstallments().size(); i++) {
					dates += new SimpleDateFormat("dd/MM/yyyy").format(sale.getInstallments().get(i).getExpire_date()) + " ";
				}
				//aggiungere le date successive di pagamenti
				if(dates.equals("") || sale.getPayment().getInstallments() == 0) {
					fields.get("scadenza_pagamento").setValue(sale.getPayment().getDescription()).setReadOnly(true);
				} else {
					fields.get("scadenza_pagamento").setValue(dates.toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
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
				fields.get("condizioni_particolari").setValue(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
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
				if(sale.getDisinfestato().equals("SI")) {
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
				if(sale.getSignedSeller().equals("SI")) {
					fields.get("signed_seller").setValue("Firmato da " + sale.getSeller().getReferent() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedSellerDate())).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("signed_seller").setReadOnly(true);
				} catch(Exception ex2) {}
			}
			
			try {
				if(sale.getSignedBuyer().equals("SI")) {
					fields.get("signed_buyer").setValue("Firmato da " + sale.getBuyer().getReferent() + " il " + new SimpleDateFormat("dd/MM/yyyy").format(sale.getSignedBuyerDate())).setReadOnly(true);
				}
				
			} catch(Exception ex) {
				try {
					fields.get("signed_buyer").setReadOnly(true);
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
	
	
	// CURTI
	public static String create3(String filename, Sale sale) {
		if(isRisoGallo(sale)) return createRisoGallo(filename, sale);
		
		
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
				if(sp.getEnd_of_stock().contentEquals("true")) {
					fields.get("quantita").setValue((NumberFormat.getNumberInstance().format(sp.getProduct_amount()) + " F. M.").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("quantita").setValue(NumberFormat.getNumberInstance().format(sp.getProduct_amount())).setReadOnly(true);
				}
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
				if(sp.getEnd_of_stock().contentEquals("true")) {
					fields.get("quantita").setValue((NumberFormat.getNumberInstance().format(sp.getProduct_amount()) + " F. M.").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("quantita").setValue(NumberFormat.getNumberInstance().format(sp.getProduct_amount())).setReadOnly(true);
				}
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
				if(sp.getEnd_of_stock().contentEquals("true")) {
					fields.get("quantita").setValue((NumberFormat.getNumberInstance().format(sp.getProduct_amount()) + "       FINE MAGAZZINO").toUpperCase().replaceAll("NULL", "")).setReadOnly(true);
				} else {
					fields.get("quantita").setValue(NumberFormat.getNumberInstance().format(sp.getProduct_amount())).setReadOnly(true);
				}
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
