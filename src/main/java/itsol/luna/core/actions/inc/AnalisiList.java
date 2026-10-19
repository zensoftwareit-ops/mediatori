package itsol.luna.core.actions.inc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.Query;

import itsol.luna.core.beans.Analisi;
import itsol.luna.core.beans.AnalisiComuneSummary;
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
	private List<AnalisiComuneSummary> riepilogoComuni = new ArrayList<AnalisiComuneSummary>(0);

	public List<Analisi> getAnalisi() {
		return analisi;
	}

	public void setAnalisi(List<Analisi> analisi) {
		this.analisi = analisi;
	}

	public List<AnalisiComuneSummary> getRiepilogoComuni() {
		return riepilogoComuni;
	}
	
	@SuppressWarnings("unchecked")
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				String q = "SELECT a FROM Analisi a INNER JOIN a.cliente c WHERE 1 = 1";
				boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
				if(filtraAdmin) q += " AND a.id_admin = :id_admin";
				if(getId_customer() != null) q += " AND c.id = :id_customer";
				if(getComune() != null && !getComune().trim().isEmpty()) q += " AND c.legal_city = :comune";
				if("SI".equals(getPagato()) || "NO".equals(getPagato())) q += " AND a.pagato = :pagato";
				q += " ORDER BY a.id DESC";

				Query query = sx.createQuery(q);
				if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
				if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
				if(getComune() != null && !getComune().trim().isEmpty()) query.setParameter("comune", getComune().trim());
				if("SI".equals(getPagato()) || "NO".equals(getPagato())) query.setParameter("pagato", getPagato());
				this.analisi = (List<Analisi>) query.list();
				creaRiepilogoComuni();
				
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

	private void creaRiepilogoComuni() {
		Map<String, AnalisiComuneSummary> riepilogo = new LinkedHashMap<String, AnalisiComuneSummary>();
		for(Analisi a : this.analisi) {
			String comune = a.getCliente() == null ? null : a.getCliente().getLegal_city();
			if(comune == null || comune.trim().isEmpty()) comune = "Comune non indicato";
			comune = comune.trim();
			AnalisiComuneSummary r = riepilogo.get(comune);
			if(r == null) {
				r = new AnalisiComuneSummary(comune);
				riepilogo.put(comune, r);
			}
			r.add(a);
		}
		this.riepilogoComuni = new ArrayList<AnalisiComuneSummary>(riepilogo.values());
	}

}
