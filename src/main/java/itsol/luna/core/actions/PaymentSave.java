package itsol.luna.core.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Payment;
import itsol.luna.core.beans.PaymentInstallment;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.MySupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class PaymentSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3162613933234622462L;
	
	private Payment payment;

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	public String execute() {
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				//------------------------------ assegno pagamento all'utente
				try {
					if(this.payment.getAdmin().getId() == null) {
						this.payment.setAdmin(SessionSupport.getAdmin());
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'utente.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
						
				//------------------------------ salvo la commessa
				HibernateUtil.saveOrUpdate(this.payment);
				setResp("E' stato salvato il metodo di pagamento " + this.payment.getId() + ".");
				setResp_status_ok();
				
				//------------------------------ imposto action di ritorno
				try {
					switch(getAction_type()) {
					case "save":
						setRedirectAction("payments/manage/" + this.payment.getId());
						break;
					case "save_close":
						setRedirectAction("payments");
						break;
					default:
						setRedirectAction("payments");
						break;
					}
				} catch(Exception ex) {
					setRedirectAction("payments");

					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il settaggio dell'action di ritorno. Action settata su: sales.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				LogSupport.log(op() + "E' stato salvato il metodo di pagamento " + this.payment.getId() + ".", Level.INFO);
				
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
			SessionSupport.setPayments(MySupport.getPayments());
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
		
		//------------------------------ validazione ptag
		try {
			if(this.payment.getPtag() == null || this.payment.getPtag().isEmpty()) {
				addFieldError("payment.ptag", "Campo obbligatorio.");
			}
		} catch(Exception ex) {
			addFieldError("payment.ptag", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo ptag.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ validazione description
		try {
			if(this.payment.getDescription() == null || this.payment.getDescription().isEmpty()) {
				addFieldError("payment.description", "Campo obbligatorio.");
			}
		} catch(Exception ex) {
			addFieldError("payment.description", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo description.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ caricamento pinstallments
		try {
			
			List<PaymentInstallment> pinstallments = this.payment.getPinstallments();
			this.payment.setPinstallments(new ArrayList<PaymentInstallment>(0));
			int k = 0;
			

			for(int i = 0; i < pinstallments.size(); i++) {
				
				
				try {
					if(pinstallments.get(i).getDays() == null || pinstallments.get(i).getAmount_perc() == null) {
						System.out.println(i + " rimuovo");

					} else {
						System.out.println(i + " non rimuovo");
						pinstallments.get(i).setPayment(this.payment);
						pinstallments.get(i).setIdx(k);
						this.payment.getPinstallments().add(pinstallments.get(i));
						k++;
					}
				} catch(Exception ex) {
					System.out.println("rimuovo catch");
				}
			}
			
			this.payment.setInstallments(k);
			
		} catch(Exception ex) {
			addFieldError("payment.description", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento delle scadenze.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
	}

}
