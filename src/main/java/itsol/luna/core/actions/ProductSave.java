package itsol.luna.core.actions;


import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;

import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.MySupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class ProductSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782622535478429282L;
	
	
	//------------------------------ variabile product
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	public String execute() { 
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				//------------------------------ assegno prodotto all'utente
				try {
					if(this.product.getAdmin().getId() == null) {
						this.product.setAdmin(SessionSupport.getAdmin());
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'utente.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				//------------------------------ salvo il prodotto
				HibernateUtil.saveOrUpdate(this.product);
				setResp("E' stato salvato il prodotto " + this.product.getId() + ".");
				setResp_status_ok();
				
				//------------------------------ imposto action di ritorno
				try {
					switch(getAction_type()) {
					case "save":
						setRedirectAction("products/manage/" + this.product.getId());
						break;
					case "save_close":
						setRedirectAction("products");
						break;
					default:
						setRedirectAction("products");
						break;
					}				
				} catch(Exception ex) {
					setRedirectAction("products");

					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il settaggio dell'action di ritorno. Action settata su: sales.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
				
				LogSupport.log(op() + "E' stato salvato il prodotto " + this.product.getId() + ".", Level.INFO);
				
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
			SessionSupport.setProducts(MySupport.getProducts());
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante l'aggiornamento della lista.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} 
		

		
		return result;

	}
	
	public void validate() {
		
		if(this.product.getName().length() == 0) {
			addFieldError("product.name", "Campo obbligatorio.");
		}
		
		if(this.product.getUnit_of_measure().length() == 0) {
			addFieldError("product.unit_of_measure", "Campo obbligatorio.");
		}
		
		if(this.product.getVat() == null) {
			addFieldError("product.vat", "Campo obbligatorio.");
		} else if(this.product.getVat() == 0) {
			addFieldError("product.taxable", "Questo valore non pu� essere 0.");
		}
		
		if(this.product.getInternal_code().length() == 0) {
			this.product.setInternal_code(null);
		}
		
		if(this.product.getDescription().length() == 0) {
			this.product.setDescription(null);
		}
		
		if(this.product.getBuyer_commission() == null) {
			addFieldError("product.buyer_commission", "Campo obbligatorio.");
		} 
		
		if(this.product.getSeller_commission() == null) {
			addFieldError("product.seller_commission", "Campo obbligatorio.");
		} 
		
		if(this.product.getContract_template() == null) {
			this.product.setContract_template("contratto_ghisoni_01.pdf");
		} 
		
	}



}
