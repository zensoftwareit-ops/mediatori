package itsol.luna.core.actions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.beans.Seller;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SetBilledSeller extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3037338146390463013L;
	
	private Long id_seller;

	public Long getId_seller() {
		return id_seller;
	}

	public void setId_seller(Long id_seller) {
		this.id_seller = id_seller;
	}

	private Seller seller;

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	private Long[] commissions_ids;

	public Long[] getCommissions_ids() {
		return commissions_ids;
	}

	public void setCommissions_ids(Long[] commissions_ids) {
		this.commissions_ids = commissions_ids;
	}
	
	@SuppressWarnings("deprecation")
	public String execute() {
		String result = new String();

		try {
			if (SessionSupport.checkSession()) {

				Session sx = HibernateUtil.getSessionFactory().openSession();

				try {
					this.seller = (Seller) sx.createQuery("FROM Seller WHERE id = :id_seller")
							.setParameter("id_seller", getId_seller()).uniqueResult();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					sx.close();
				}

				if (this.seller != null) {


					if (getDate_from() == null) {
						setDate_from(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1970"));
					}

					if (getDate_to() == null) {
						setDate_to(new Date());
					}

					getDate_to().setHours(23);
					getDate_to().setMinutes(59);

					Iterator<Sale> i = this.seller.getSales().iterator();
					while (i.hasNext()) {

						Sale s = i.next();
						
						Iterator<SaleCommission> ii = s.getCommissions().iterator();
						
						while(ii.hasNext()) {
							
							SaleCommission sc = ii.next();
							
							if(Arrays.asList(getCommissions_ids()).contains(sc.getId())) {
								if (s.getSale_date().after(getDate_from()) && s.getSale_date().before(getDate_to())) {
									sc.setBilled_seller("SI");

								}
							}
							
						}
						

					}

					HibernateUtil.saveOrUpdate(this.seller);

					setResp("Le provvigioni (" + getCommissions_ids().toString() + ") sono state escluse dalla fatturazione.");
					setResp_status_ok();
					
					result = SUCCESS;

				} else {
					setResp("Il venditore è inesistente.");
					setResp_status_ko();
					result = ERROR;
				}

				
			} else {
				result = SUCCESS_NOLOGIN;
			}
		} catch (Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				result = ERROR_DEBUG;
			} else {
				setResp("Si è verificato un errore durante l'esclusione dalla fatturazione.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante l'esclusione dalla fatturazione.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);
		}

		return result;
	}

}
