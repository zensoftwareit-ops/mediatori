package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class InvoiceSetPayed extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1925541503520864548L;
	
	private Long id_invoice;
	
	private Invoice invoice;

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
	
	private String payed;
	
	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	public String execute() {
		String result = new String();
		
		try {
			if (SessionSupport.checkSession()) {
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				try {
					this.invoice = (Invoice)sx.createQuery("FROM Invoice WHERE id = :id_invoice").setParameter("id_invoice", getId_invoice()).uniqueResult();
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
					sx.close();
				}
				
				if(this.invoice != null) {
					this.invoice.setPayed(getPayed());
					System.out.println(this.invoice.getPayed());
					HibernateUtil.saveOrUpdate(this.invoice);
					
					setResp("E' stato aggiornato lo stato di pagamento della fattura n. " + this.invoice.getNumber() + ".");
					setResp_status_ok();
					
					result = SUCCESS;
				} else {
					setResp("La fattura è inesistente.");
					setResp_status_ko();
					result = ERROR;
				}
				
			} else {
				result = SUCCESS_NOLOGIN;
			}

		} catch(Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore durante il settaggio del campo payed.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante il settaggio del campo payed.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);
		}
		
		return result;
	}

}
