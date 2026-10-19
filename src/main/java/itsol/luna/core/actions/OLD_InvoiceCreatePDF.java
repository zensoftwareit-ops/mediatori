package itsol.luna.core.actions;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.beans.InvoiceProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class OLD_InvoiceCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6102922835936220057L;
	
	private Long id_invoice;
	private Invoice invoice;
	
	private List<String> files = new ArrayList<String>(0);
	
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();

		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.invoice = (Invoice)sx.createQuery("FROM Invoice WHERE id = :id_invoice").setParameter("id_invoice", getId_invoice()).uniqueResult();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.invoice = (Invoice)sx.createQuery("FROM Invoice WHERE id_admin = :id_admin AND id = :id_invoice").setParameter("id_admin", id_admin).setParameter("id_invoice", getId_invoice()).uniqueResult();
				}
				
				if(this.id_invoice != null) {
					
					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.ITALY);
					
					String filename = "F000" + this.invoice.getId().toString() + ".pdf";
					
					String src = ContextSupport.getUploadPath() + SessionSupport.getAdmin().getInvoice_template();
					String dest = ContextSupport.getUploadPath() + filename;
					
					PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
						
					PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
					Map<String, PdfFormField> fields = form.getFormFields();
					

					
					try {
						fields.get("acquirente").setValue(this.invoice.getCustomer().getBusiness_name() + " - " + this.invoice.getCustomer().getLegal_address() + " - " + this.invoice.getCustomer().getLegal_zip_code() + " " + this.invoice.getCustomer().getLegal_city() + " " + this.invoice.getCustomer().getLegal_province()).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("acquirente").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("n_fattura").setValue(this.invoice.getNumber().toString()).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("n_fattura").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("data_fattura").setValue(new SimpleDateFormat("dd/MM/yyyy").format(this.invoice.getInvoice_date())).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("data_fattura").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						if(this.invoice.getCustomer().getTax_code() != null) {
							fields.get("codice_fiscale").setValue(this.invoice.getCustomer().getTax_code()).setReadOnly(true);
						} else {
							fields.get("codice_fiscale").setReadOnly(true);
						}
					} catch(Exception ex) {
						try {
							fields.get("codice_fiscale").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						if(this.invoice.getCustomer().getVat_number() != null) {
							fields.get("partita_iva").setValue(this.invoice.getCustomer().getVat_number()).setReadOnly(true);
						} else {
							fields.get("partita_iva").setReadOnly(true);
						}
					} catch(Exception ex) {
						try {
							fields.get("partita_iva").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("pagamento").setValue("Rimessa diretta").setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("pagamento").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("banca").setValue("").setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("banca").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					
					
					try {
						int k = 1;
						Double qta = 0.00;
						Iterator<InvoiceProduct> i = this.invoice.getProducts().iterator();
						while(i.hasNext()) {
							InvoiceProduct ip = i.next();
							
							try {
								fields.get("descrizione_" + k).setValue("MEDIAZIONI").setReadOnly(true);
							} catch(Exception ex) {
								try {
									fields.get("descrizione_" + k).setReadOnly(true);
								} catch(Exception ex2) {}
							}
							
							try {
								fields.get("udm_" + k).setValue(ip.getUnit_of_measure()).setReadOnly(true);
							} catch(Exception ex) {
								try {
									fields.get("udm_" + k).setReadOnly(true);
								} catch(Exception ex2) {}
							}
							
							try {
								fields.get("qta_" + k).setValue(ip.getProduct_amount().toString()).setReadOnly(true);
								qta = qta + ip.getProduct_amount();
							} catch(Exception ex) {
								try {
									fields.get("qta_" + k).setReadOnly(true);
								} catch(Exception ex2) {}
							}
							
							try {
								fields.get("prezzo_" + k).setValue(formatter.format(ip.getCommission()).replace("€ ", "")).setReadOnly(true);
							} catch(Exception ex) {
								try {
									fields.get("prezzo_" + k).setReadOnly(true);
								} catch(Exception ex2) {}
							}
							
							try {
								fields.get("importo_" + k).setValue(formatter.format(ip.getCommission_amount()).replace("€ ", "")).setReadOnly(true);
							} catch(Exception ex) {
								try {
									fields.get("importo_" + k).setReadOnly(true);
								} catch(Exception ex2) {}
							}
							
							
							k++;
							
							
						}
						
						try {
							fields.get("udm_" + k).setValue("TOTALE").setReadOnly(true);
						} catch(Exception ex) {
							try {
								fields.get("udm_" + k).setReadOnly(true);
							} catch(Exception ex2) {}
						}
						System.out.println(qta);
						try {
							fields.get("qta_" + k).setValue(qta.toString()).setReadOnly(true);
						} catch(Exception ex) {
							try {
								fields.get("qta_" + k).setReadOnly(true);
							} catch(Exception ex2) {}
						}

						
						while(k <= 25) {
							fields.get("descrizione_" + k).setReadOnly(true);
							fields.get("udm_" + k).setReadOnly(true);
							fields.get("qta_" + k).setReadOnly(true);
							fields.get("prezzo_" + k).setReadOnly(true);
							fields.get("importo_" + k).setReadOnly(true);
							k++;
						}
						

						
						
					} catch(Exception ex) {
						
					}
					
				
					
					try {
						fields.get("imponibile").setValue(formatter.format(this.invoice.getTaxable_total()).replace("€ ", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("imponibile").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("iva").setValue(formatter.format(this.invoice.getVat_total()).replace("€ ", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("iva").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("totale").setValue(formatter.format(this.invoice.getTotal()).replace("€ ", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("totale").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("ritenuta").setValue(formatter.format(this.invoice.getHolding_tax()).replace("€ ", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("ritenuta").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					try {
						fields.get("netto_totale").setValue(formatter.format(this.invoice.getTotal_without_holding()).replace("€ ", "")).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("netto_totale").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					
					try {
						fields.get("data_scadenza").setValue(new SimpleDateFormat("dd/MM/yyyy").format(this.invoice.getExpire_date())).setReadOnly(true);
					} catch(Exception ex) {
						try {
							fields.get("data_scadenza").setReadOnly(true);
						} catch(Exception ex2) {}
					}
					
					
					pdfDoc.close();
					
					this.files.add(filename);
				}
				
				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			sx.close();
		}
		
		return result;
	}
	
	
	
	public Long getId_invoice() {
		return id_invoice;
	}
	public void setId_invoice(Long id_invoice) {
		this.id_invoice = id_invoice;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public List<String> getFiles() {
		return files;
	}

}
