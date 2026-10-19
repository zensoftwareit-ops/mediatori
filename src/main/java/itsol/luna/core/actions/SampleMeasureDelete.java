package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SampleMeasureDelete extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id_measure;
	private YieldMeasure measure;
	public Long getId_measure() {
		return id_measure;
	}
	public void setId_measure(Long id_measure) {
		this.id_measure = id_measure;
	}
	public YieldMeasure getMeasure() {
		return measure;
	}
	public void setMeasure(YieldMeasure measure) {
		this.measure = measure;
	}
	
	public String execute() {
		String result = new String();
		
		try {
			if(SessionSupport.checkSession()) {
				
				Session sx = HibernateUtil.getSessionFactory().openSession();
				
				/**
				 * ricerca della commessa
				 */
				try {
					this.measure = (YieldMeasure) sx.createQuery("FROM YieldMeasure WHERE id = :id_measure")
							.setParameter("id_measure", getId_measure())
							.uniqueResult();
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
				if(this.measure != null) {
					Long id = this.measure.getId();
					HibernateUtil.delete(this.measure);
					LogSupport.log(op() + "E' stata eliminato il campione " + id + ".", Level.INFO);
					setResp("E' stata eliminato il campione " + id + ".");
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
