package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class AnalisiDelete extends MyActionSupport implements MyActionInterface {

	private static final long serialVersionUID = 1L;
	private Long id_analisi;

	public Long getId_analisi() { return id_analisi; }
	public void setId_analisi(Long id_analisi) { this.id_analisi = id_analisi; }

	public String execute() {
		if(!SessionSupport.checkSession()) return SUCCESS_NOLOGIN;
		Session sx = HibernateUtil.getSessionFactory().openSession();
		try {
			String hql = "FROM Analisi WHERE id = :id_analisi AND year.id = :id_sowing_year";
			boolean filtraAdmin = !SessionSupport.checkSessionLevel("admin|super");
			if(filtraAdmin) hql += " AND id_admin = :id_admin";
			Query query = sx.createQuery(hql).setParameter("id_analisi", id_analisi)
					.setParameter("id_sowing_year", SessionSupport.getSowingYear().getId());
			if(filtraAdmin) query.setParameter("id_admin", SessionSupport.getAdmin().getId());
			itsol.luna.core.beans.Analisi analisi = (itsol.luna.core.beans.Analisi) query.uniqueResult();
			if(analisi == null) {
				setResp("Analisi non trovata o non autorizzata.");
				setResp_status_ko();
				return ERROR;
			}
			Long id = analisi.getId();
			sx.close();
			sx = null;
			HibernateUtil.delete(analisi);
			setResp("E' stata eliminata l'analisi " + id + ".");
			setResp_status_ok();
			LogSupport.log(op() + "E' stata eliminata l'analisi " + id + ".", Level.INFO);
			return SUCCESS;
		} catch(Exception ex) {
			setResp("Si è verificato un errore durante l'eliminazione dell'analisi.");
			setResp_status_ko();
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			if(ContextSupport.isDebug()) {
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				return ERROR_DEBUG;
			}
			return ERROR;
		} finally {
			if(sx != null && sx.isOpen()) sx.close();
		}
	}
}
