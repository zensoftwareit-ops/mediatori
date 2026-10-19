package itsol.luna.core.actions;

import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SamplesMeasureSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3355639159616043529L;
	
	private YieldMeasure measure;
	public YieldMeasure getMeasure() {
		return measure;
	}
	public void setMeasure(YieldMeasure measure) {
		this.measure = measure;
	}
	
	public String execute() {
		
		String result = new String();
		
		try {
			
			//------------------------------ assegno commessa all'utente
			try {
				if(this.measure.getAdmin().getId() == null) {
					this.measure.setAdmin(SessionSupport.getAdmin());
				}
			} catch(Exception ex) {
				if(ContextSupport.isDebug()) {
					ex.printStackTrace();
				}
				LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'utente.", Level.INFO);
				LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			}
			
			//------------------------------ assegno anno alla vendita
			try {
				if(this.measure.getYear().getId() == null) {
					this.measure.setYear(SessionSupport.getSowingYear());
				}
			} catch(Exception ex) {
				if(ContextSupport.isDebug()) {
					ex.printStackTrace();
				}
				LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'anno.", Level.INFO);
				LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			}
			
			HibernateUtil.saveOrUpdate(this.measure);
			setResp("Salvataggio eseguito correttamente!");
			setResp_status_ok();
			
			setRedirectAction("samples_measures");
			
			LogSupport.log(op() + "E' stata salvato il campione " + this.measure.getId() + ".", Level.INFO);
			
			result = SUCCESS;
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(op() + "Si è verificato un errore.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		}
		
		return result;
		
	}
	
	public void validate() {
		
		//------------------------------ validazione customer
		try {
			if(this.measure.getCustomer().getId() == null) {
				addFieldError("measure.customer.id", "Cliente non selezionato.");
				System.out.println("Cliente non selezionato.");
			}
		} catch(Exception ex) {
			addFieldError("measure.customer.id", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo customer.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ validazione product
		try {
			if(this.measure.getProduct().getId() == null) {
				addFieldError("measure.product.id", "Prodotto non selezionato.");
			}
		} catch(Exception ex) {
			addFieldError("measure.product.id", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo product.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			if(this.measure.getGrains_yield() == null || this.measure.getGrains_yield().isNaN()) {
				this.measure.setGrains_yield(0.00);
			}
		} catch(Exception ex) {
			
			this.measure.setGrains_yield(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo grains_yield. Settato a 0.00.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			if(this.measure.getGlobal_yield() == null || this.measure.getGlobal_yield().isNaN()) {
				this.measure.setGlobal_yield(0.00);
			}
		} catch(Exception ex) {
			
			this.measure.setGlobal_yield(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo global_yield. Settato a 0.00.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			if(this.measure.getDefects_1() == null || this.measure.getDefects_1().isNaN()) {
				this.measure.setDefects_1(0.00);
			}
		} catch(Exception ex) {
			
			this.measure.setDefects_1(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo defects_1. Settato a 0.00.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			if(this.measure.getDefects_2() == null || this.measure.getDefects_2().isNaN()) {
				this.measure.setDefects_2(0.00);
			}
		} catch(Exception ex) {
			
			this.measure.setDefects_2(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo defects_2. Settato a 0.00.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		try {
			if(this.measure.getThousand_grains() == null || this.measure.getThousand_grains().isNaN()) {
				this.measure.setThousand_grains(0.00);
			}
		} catch(Exception ex) {
			
			this.measure.setThousand_grains(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo thousand_grains. Settato a 0.00.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
	}
	
	public Long getId_customer() {
		return getMeasure().getCustomer().getId();
	}
	
	public Long getId_product() {
		return getMeasure().getProduct().getId();
	}

}
