package itsol.luna.core.actions;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
		String result;
		setRedirectAction("analisi");

		try {
			if(SessionSupport.checkSession()) {
				impostaFuoriLimite();
				salvaAnalisi();
				setResp("E' stata salvata l'analisi " + this.analisi.getId() + ".");
				setResp_status_ok();
				if("save".equals(getAction_type())) {
					setRedirectAction("analisi/manage/" + this.analisi.getId());
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

	private void salvaAnalisi() {
		Session sx = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = sx.beginTransaction();
			Customer cliente = (Customer) sx.get(Customer.class, this.analisi.getCliente().getId());
			Product prodotto = (Product) sx.get(Product.class, this.analisi.getProdotto().getId());
			if(cliente == null || prodotto == null) {
				throw new IllegalArgumentException("Cliente o prodotto non valido.");
			}

			itsol.luna.core.beans.Analisi record;
			if(this.analisi.getId() == null) {
				record = new itsol.luna.core.beans.Analisi();
				record.setId_admin(SessionSupport.getAdmin().getId());
				record.setYear(SessionSupport.getSowingYear());
			} else {
				String hql = "FROM Analisi WHERE id = :id AND year.id = :id_sowing_year";
				boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
				if(filtraAdmin) hql += " AND id_admin = :id_admin";
				Query query = sx.createQuery(hql)
						.setParameter("id", this.analisi.getId())
						.setParameter("id_sowing_year", SessionSupport.getSowingYear().getId());
				if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
				record = (itsol.luna.core.beans.Analisi) query.uniqueResult();
				if(record == null) throw new SecurityException("Analisi non trovata o non autorizzata.");
			}

			copiaCampi(record, this.analisi);
			record.setCliente(cliente);
			record.setProdotto(prodotto);
			if(record.getDataAnalisi() == null) record.setDataAnalisi(new java.util.Date());
			this.analisi = record;
			impostaFuoriLimite();
			sx.saveOrUpdate(record);
			tx.commit();
		} catch(RuntimeException ex) {
			if(tx != null && tx.isActive()) tx.rollback();
			throw ex;
		} finally {
			sx.close();
		}
	}

	private void copiaCampi(itsol.luna.core.beans.Analisi destinazione, itsol.luna.core.beans.Analisi origine) {
		destinazione.setDataAnalisi(origine.getDataAnalisi());
		destinazione.setnCampione(origine.getnCampione());
		destinazione.setLotto(origine.getLotto());
		destinazione.setCadmio(origine.getCadmio());
		destinazione.setArsenicoTot(origine.getArsenicoTot());
		destinazione.setArsenicoInorganico(origine.getArsenicoInorganico());
		destinazione.setNichel(origine.getNichel());
		destinazione.setAltreAnalisi(origine.getAltreAnalisi());
		destinazione.setNote(origine.getNote());
		destinazione.setCosto(origine.getCosto());
		destinazione.setPagato(origine.getPagato());
	}
	
	public void validate() {

		impostaFuoriLimite();
		if(this.analisi == null || this.analisi.getDataAnalisi() == null) {
			addFieldError("analisi.dataAnalisi", "Data non indicata.");
		}
		
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
