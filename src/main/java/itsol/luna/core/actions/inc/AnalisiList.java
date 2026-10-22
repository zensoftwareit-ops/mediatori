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
				
				// Il riepilogo usa la popolazione completa dei filtri anagrafici, così le
				// percentuali restano significative anche quando si visualizzano solo i fuori limite.
				List<Analisi> baseRiepilogo = eseguiQuery(sx, false);
				this.analisi = eseguiQuery(sx, true);
				creaRiepilogoComuni(baseRiepilogo);
				
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

	@SuppressWarnings("unchecked")
	private List<Analisi> eseguiQuery(Session sx, boolean applicaFiltroFuoriLimite) {
		String q = "SELECT a FROM Analisi a INNER JOIN a.cliente c WHERE a.year.id = :id_sowing_year";
		boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
		if(filtraAdmin) q += " AND a.id_admin = :id_admin";
		if(getId_customer() != null) q += " AND c.id = :id_customer";
		if(getId_product() != null) q += " AND a.prodotto.id = :id_product";
		if(getComune() != null && !getComune().trim().isEmpty()) q += " AND c.legal_city = :comune";
		if(getDate_from() != null) q += " AND a.dataAnalisi >= :date_from";
		if(getDate_to() != null) q += " AND a.dataAnalisi <= :date_to";
		if("SI".equals(getPagato()) || "NO".equals(getPagato())) q += " AND a.pagato = :pagato";
		if(applicaFiltroFuoriLimite) q += clausolaFuoriLimite();
		q += " ORDER BY a.dataAnalisi DESC, a.id DESC";

		Query query = sx.createQuery(q);
		query.setParameter("id_sowing_year", SessionSupport.getSowingYear().getId());
		if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
		if(getId_customer() != null) query.setParameter("id_customer", getId_customer());
		if(getId_product() != null) query.setParameter("id_product", getId_product());
		if(getComune() != null && !getComune().trim().isEmpty()) query.setParameter("comune", getComune().trim());
		if(getDate_from() != null) query.setParameter("date_from", getDate_from());
		if(getDate_to() != null) query.setParameter("date_to", getDate_to());
		if("SI".equals(getPagato()) || "NO".equals(getPagato())) query.setParameter("pagato", getPagato());
		return (List<Analisi>) query.list();
	}

	private String clausolaFuoriLimite() {
		if(getFuori_limite() == null) return "";
		String filtro = getFuori_limite().trim().toUpperCase();
		if("TOTALE".equals(filtro)) return " AND (UPPER(a.cadmioFl) = 'SI' OR UPPER(a.arsenicoFl) = 'SI' OR UPPER(a.nichelFl) = 'SI')";
		if("CADMIO".equals(filtro)) return " AND UPPER(a.cadmioFl) = 'SI'";
		if("ARSENICO".equals(filtro)) return " AND UPPER(a.arsenicoFl) = 'SI'";
		if("NICHEL".equals(filtro)) return " AND UPPER(a.nichelFl) = 'SI'";
		return "";
	}

	private void creaRiepilogoComuni(List<Analisi> dati) {
		Map<String, AnalisiComuneSummary> riepilogo = new LinkedHashMap<String, AnalisiComuneSummary>();
		for(Analisi a : dati) {
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
