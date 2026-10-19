package itsol.luna.core.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class InvoiceDelete extends MyActionSupport implements MyActionInterface {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2421757453291062525L;
	
	private Long id_invoice;
	public Long getId_invoice() {
		return id_invoice;
	}
	public void setId_invoice(Long id_invoice) {
		this.id_invoice = id_invoice;
	}
	
	private Invoice invoice;
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	private String itype;
	public String getItype() {
		return itype;
	}
	public void setItype(String itype) {
		this.itype = itype;
	}
	
	private List<SaleCommission> commissions = new ArrayList<SaleCommission>(0);
	
	@SuppressWarnings("unchecked")
	public String execute() {
		String result = new String();
		
		try {
			if(SessionSupport.checkSession()) {
				
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				/**
				 * ricerca oggetto invoice
				 */
				try {
					this.invoice = (Invoice) sx.createQuery("FROM Invoice WHERE id = :id_invoice")
							.setParameter("id_invoice", getId_invoice())
							.uniqueResult();
					if(getItype().equals("seller")) {
						this.commissions = (List<SaleCommission>)sx.createQuery("SELECT sc FROM SaleCommission sc INNER JOIN sc.invoice_seller i WHERE sc.invoice_seller = i AND i.id = :id_invoice").setParameter("id_invoice", this.invoice.getId()).list();
						Iterator<SaleCommission> i = this.commissions.iterator();
						while(i.hasNext()) {
							SaleCommission commission = i.next();
							commission.setInvoice_seller(null);
							HibernateUtil.saveOrUpdate(commission);
						}
					} else if(getItype().equals("buyer")) {
						this.commissions = (List<SaleCommission>)sx.createQuery("SELECT sc FROM SaleCommission sc INNER JOIN sc.invoice_buyer i WHERE sc.invoice_buyer = i AND i.id = :id_invoice").setParameter("id_invoice", this.invoice.getId()).list();
						Iterator<SaleCommission> i = this.commissions.iterator();
						while(i.hasNext()) {
							SaleCommission commission = i.next();
							commission.setInvoice_buyer(null);
							HibernateUtil.saveOrUpdate(commission);
						}
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} finally {
					sx.close();
				}
				
				/**
				 * eliminazione se non nullo
				 */
				if(this.invoice != null) {
					String number = this.invoice.getNumber();
					HibernateUtil.delete(this.invoice);
					LogSupport.log(op() + "E' stata eliminata la fattura " + number + ".", Level.INFO);
					setResp("E' stata eliminata la fattura " + number + ".");
					setResp_status_ok();
				} else {
					LogSupport.log(op() + "Nessuna eliminazione.", Level.INFO);
					setResp("Nessuna eliminazione.");
					setResp_status_ko();
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
			LogSupport.log(op() + "Si è verificato un errore durante l'eliminazione.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		return result;
	}
	

}
