package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Analisi;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class AnalisiList extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8547937100675044360L;
	
	private List<Analisi> analisi = new ArrayList<Analisi>(0);

	public List<Analisi> getAnalisi() {
		return analisi;
	}

	public void setAnalisi(List<Analisi> analisi) {
		this.analisi = analisi;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
						this.analisi = (List<Analisi>) sx.createQuery("FROM Analisi ORDER BY nCampione DESC").list();
					
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();

					String q = "";
					q += "SELECT a FROM Analisi a";
					
					if(getId_customer() != null) {
						q += " INNER JOIN a.cliente c";
					} 
					
					q += " WHERE a.id_admin = :id_admin";
					
					if(getId_customer() != null) {
						q += " AND c.id = " + getId_customer();
					} 
					
					if(getPagato() != null)
					if(getPagato().equals("SI")) {
						q += " AND a.pagato = 'SI'";
					} else if(getPagato().equals("NO")) {
						q += " AND a.pagato = 'NO'";
					}
					
					q += " ORDER BY id DESC";
					
						this.analisi = (List<Analisi>) sx.createQuery(q).setParameter("id_admin", id_admin).list();
					
					
				}	
				
				createPagination(this.analisi.size());

				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
		
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			sx.close();
		}
		
		return result;
	}

}
