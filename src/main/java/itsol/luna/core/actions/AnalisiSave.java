package itsol.luna.core.actions;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.MySupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class AnalisiSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4473671133389080614L;
	private static final BigDecimal LIMITE_CADMIO = new BigDecimal("0.15");
	private static final BigDecimal LIMITE_ARSENICO = new BigDecimal("0.15");
	private static final BigDecimal LIMITE_NICHEL = new BigDecimal("1.5");
	
	private itsol.luna.core.beans.Analisi analisi;

	public itsol.luna.core.beans.Analisi getAnalisi() {
		return analisi;
	}

	public void setAnalisi(itsol.luna.core.beans.Analisi analisi) {
		this.analisi = analisi;
	}
	
public String execute() {
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {

				impostaFuoriLimite();
				
				/**
				 * assegno cliente all'utente
				 */
				try {
					if(this.analisi.getId_admin() == null) {
						this.analisi.setId_admin(SessionSupport.getAdmin().getId());
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'utente.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				
				/**
				 * imposto action di ritorno
				 */
				try {
					HibernateUtil.saveOrUpdate(this.analisi);
					setResp("E' stato salvato il analisi " + this.analisi.getId() + ".");
					setResp_status_ok();

					switch(getAction_type()) {
					case "save":
						setRedirectAction("analisi/manage/" + this.analisi.getId());
						break;
					case "save_close":
						setRedirectAction("analisi");
						break;
					default:
						setRedirectAction("analisi");
						break;
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore impostando la action di ritorno.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			SessionSupport.setCustomers(MySupport.getCustomers());
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante l'aggiornamento della lista.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} 
		
		return result;
		
	}
	
	public void validate() {

		impostaFuoriLimite();
		
		//------------------------------ validazione customer
				try {
					if(this.analisi.getCliente().getId() == null) {
						addFieldError("analisi.cliente.id", "Cliente non selezionato.");
						System.out.println("Cliente non selezionato.");
					}
				} catch(Exception ex) {
					addFieldError("analisi.cliente.id", "Si è verificato un errore.");
					
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo customer.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
				
				//------------------------------ validazione product
				try {
					if(this.analisi.getProdotto().getId() == null) {
						addFieldError("analisi.prodotto.id", "Prodotto non selezionato.");
					}
				} catch(Exception ex) {
					addFieldError("analisi.prodotto.id", "Si è verificato un errore.");
					
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo product.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
		
	}

	private void impostaFuoriLimite() {
		if(this.analisi == null) {
			return;
		}

		this.analisi.setCadmioFl(fuoriLimite(this.analisi.getCadmio(), LIMITE_CADMIO));
		this.analisi.setArsenicoFl(fuoriLimite(this.analisi.getArsenicoInorganico(), LIMITE_ARSENICO));
		this.analisi.setNichelFl(fuoriLimite(this.analisi.getNichel(), LIMITE_NICHEL));
	}

	private String fuoriLimite(String valore, BigDecimal limite) {
		if(valore == null) {
			return "NO";
		}

		String normalizzato = valore.trim().replace(',', '.');
		boolean maggioreDi = normalizzato.startsWith(">");
		normalizzato = normalizzato.replaceFirst("^[<>]=?\\s*", "");

		try {
			BigDecimal numero = new BigDecimal(normalizzato);
			int confronto = numero.compareTo(limite);
			return (confronto > 0 || (maggioreDi && confronto == 0)) ? "SI" : "NO";
		} catch(NumberFormatException ex) {
			return "NO";
		}
	}
	
	public List<Product> getProducts() {
		return SessionSupport.getProducts();
	}

	public List<Customer> getCustomers() {
		return SessionSupport.getCustomers();
	}

}
