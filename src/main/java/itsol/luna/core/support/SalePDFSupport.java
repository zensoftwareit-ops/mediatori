package itsol.luna.core.support;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;


import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;

public class SalePDFSupport {

	
	/**
	 * usato per: 
	 * contratto_ghisoni_01.pdf
	 * contratto_ghisoni_02.pdf
	 * contratto_forte_03.pdf
	 */
	public static String create1(String filename, Sale sale) {
		
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
