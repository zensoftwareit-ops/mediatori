package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.SowingYear;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class ChangeSowingYear extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6255788414632907897L;

	/**
	 * variabile id
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String execute() {

		String result = new String();

		try {

			Session sx = HibernateUtil.getSessionFactory().openSession();
			SowingYear year = null;

			try {
				year = (SowingYear) sx.createQuery("FROM SowingYear WHERE id = :id").setParameter("id", getId())
						.uniqueResult();
			} catch (Exception ex) {
				if (ContextSupport.isDebug()) {
					ex.printStackTrace();
				}
				LogSupport.log(op() + "Si è verificato un errore durante la ricerca dell'anno di semina.", Level.INFO);
				LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
						Level.SEVERE);
			} finally {
				sx.close();
			}

			if (year != null) {
				SessionSupport.setSowingYear(year);
				setResp("L'anno di semina è stato impostato su " + year.getYear() + ".");
				setResp_status_ok();
			} else {
				setResp("Non è stato trovato un anno di semina.");
				setResp_status_ko();
			}

			result = SUCCESS;

		} catch (Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore durante il cambio dell'anno di semina.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante il cambio dell'anno di semina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);

		}

		return result;

	}

}
