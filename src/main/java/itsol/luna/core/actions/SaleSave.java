package itsol.luna.core.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.ContractTemplate;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Payment;
import itsol.luna.core.beans.PaymentInstallment;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SalePaymentInstallment;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleSave extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1114225925037183975L;
	
	
	//------------------------------ variabile sale
	private Sale sale;
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
	
	private Date exp_date_001;

	
	public Date getExp_date_001() {
		return exp_date_001;
	}
	public void setExp_date_001(Date exp_date_001) {
		this.exp_date_001 = exp_date_001;
	}
	
	private Integer oldNumber;
	
	
	public Integer getOldNumber() {
		return oldNumber;
	}
	public void setOldNumber(Integer oldNumber) {
		this.oldNumber = oldNumber;
	}
	public String execute() {
		
		String result = new String();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				//------------------------------ assegno commessa all'utente
				try {
					if(this.sale.getAdmin().getId() == null) {
						this.sale.setAdmin(SessionSupport.getAdmin());
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
					if(this.sale.getYear().getId() == null) {
						this.sale.setYear(SessionSupport.getSowingYear());
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la verifica dell'anno.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
				

				
				//------------------------------ se billed_mediation è nullo imposto NO
//				try {
//					if(this.sale.getBilled_buyer() == null || this.sale.getBilled_buyer().isEmpty()) {
//						this.sale.setBilled_buyer("NO");
//					}
//				} catch(Exception ex) {
//					if(ContextSupport.isDebug()) {
//						ex.printStackTrace();
//					}
//					LogSupport.log(op() + "Si è verificato un errore durante il settaggio di billed_buyer.", Level.INFO);
//					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
//				}
				
				//------------------------------ imposto numero della vendita
				try {
					if(this.sale.getNumber() == null) {
						this.sale.setNumber(SessionSupport.getAdmin().getNextSaleNumber());
						this.sale.setOtpSellerSent("NO");
						this.sale.setSignedSeller("NO");
						this.sale.setOtpBuyerSent("NO");
						this.sale.setSignedBuyer("NO");
					} else {
						if(this.sale.getNumber() != getOldNumber()) {
							SessionSupport.getAdmin().setNextSaleNumber(this.sale.getNumber());
						}
						
					}
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il settaggio del numero della vendita.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}
						
				//------------------------------ salvo la commessa
				HibernateUtil.saveOrUpdate(this.sale);
				setResp("E' stata salvata la vendita " + this.sale.getNumber() + ".");
				setResp_status_ok();
				
				//------------------------------ imposto action di ritorno
				try {
					switch(getAction_type()) {
					case "save":
						setRedirectAction("sales/manage/" + this.sale.getId());
						break;
					case "save_close":
						setRedirectAction("sales");
						break;
					case "save_close_modal":
						setRedirectAction("modal_close");
						break;
					default:
						setRedirectAction("sales");
						break;
					}
				} catch(Exception ex) {
					setRedirectAction("sales");

					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il settaggio dell'action di ritorno. Action settata su: sales.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				}

				LogSupport.log(op() + "E' stata salvata la vendita " + this.sale.getNumber() + ".", Level.INFO);
				
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
		
		return result;
		
	}

	@SuppressWarnings("unchecked")
	public void validate() {
		
		
		//------------------------------ validazione seller
		try {
			if(this.sale.getSeller().getId() == null) {
				addFieldError("sale.seller.id", "Campo obbligatorio.");
			}
		} catch(Exception ex) {
			addFieldError("sale.seller.id", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo seller.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ validazione buyer
		try {
			if(this.sale.getBuyer().getId() == null) {
				addFieldError("sale.buyer.id", "Campo obbligatorio.");
			}
		} catch(Exception ex) {
			addFieldError("sale.buyer.id", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo buyer.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ validazione sale_date
		try {
			if(this.sale.getSale_date() == null) {
				addFieldError("sale.sale_date", "Campo obbligatorio.");
			}
		} catch(Exception ex) {
			addFieldError("sale.sale_date", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo sale_date.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}

		//------------------------------ validazione invoice_deposit_amount
		try {
			if(this.sale.getInvoice_deposit_amount() == null || this.sale.getInvoice_deposit_amount() < 0) {
				this.sale.setInvoice_deposit_amount(0.00);
			}
		} catch(Exception ex) {
			this.sale.setInvoice_deposit_amount(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo invoice_deposit_amount. Settato a 0.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		//------------------------------ validazione invoice_balance_amount
		try {
			if(this.sale.getInvoice_balance_amount() == null || this.sale.getInvoice_balance_amount() < 0) {
				this.sale.setInvoice_balance_amount(0.00);
			}
		} catch(Exception ex) {
			this.sale.setInvoice_balance_amount(0.00);
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo invoice_balance_amount. Settato a 0.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}


		//------------------------------ controllo e caricamento products
		LogSupport.log(op() + "Caricamento dei prodotti.", Level.INFO);
		
		try {
			if(this.sale.getProduct().getProduct().getId() != null) {
				
				if(this.sale.getProduct().getAmount() == null || this.sale.getProduct().getAmount().isNaN()) {
					addFieldError("sale.product.amount", "Importo è obbligatorio - non può essere NaN.");
				}
				
				if(this.sale.getProduct().getProduct_amount() == null) {
					addFieldError("sale.product.product_amount", "Quantità è obbligatorio.");
				} else if (this.sale.getProduct().getProduct_amount() < 1) {
					addFieldError("sale.product.product_amount", "Quantità non può essere minore di 1.");
				}
				
				if(this.sale.getProduct().getTotal() == null || this.sale.getProduct().getTotal().isNaN()) {
					addFieldError("sale.product.total", "Totale è obbligatorio - non può essere NaN.");
				} 
				
				if(this.sale.getProduct().getVat() == null || this.sale.getProduct().getVat().isNaN()) {
					addFieldError("sale.product.vat", "Imposta è obbligatorio - non può essere NaN.");
				}
				
				this.sale.getProduct().setSale(this.sale);

			} 
		} catch(Exception ex) {
			
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento dei prodotti.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
	
		
		
		//------------------------------ validazione payment
		try {
			if(this.sale.getPayment().getId() == null) {
				addFieldError("sale.payment.id", "Campo obbligatorio.");
			} else {
				
				if(this.sale.getInstallments().isEmpty()) {
					List<PaymentInstallment> installments = new ArrayList<PaymentInstallment>(0);
					Session sx = HibernateUtil.getSessionFactory().openSession();
					
					try {
						installments = (List<PaymentInstallment>)sx.createQuery("FROM PaymentInstallment WHERE id_payment = :id").setParameter("id", this.sale.getPayment().getId()).list();
						
						long idx = 0;
						Iterator<PaymentInstallment> i = installments.iterator();
						while(i.hasNext()) {
							
							PaymentInstallment inst = i.next();
							SalePaymentInstallment pinst = new SalePaymentInstallment();
							
							if(this.sale.getPayment().getFixed_date().equals("SI")) {
								if(getExp_date_001() == null) {
									addFieldError("exp_date_001", "Campo obbligatorio.");
								} else {
									Calendar c = Calendar.getInstance();
									c.setTime(getExp_date_001());
									c.add(Calendar.DATE, inst.getDays());
									pinst.setExpire_date(c.getTime());
								}
							} else {
								Calendar c = Calendar.getInstance();
								c.setTime(this.sale.getSale_date());
								c.add(Calendar.DATE, inst.getDays());
								pinst.setExpire_date(c.getTime());
							}
							

							
							pinst.setAmount(this.sale.getProduct().getTotal() * (inst.getAmount_perc() / 100));
							
							pinst.setPayed("NO");
							
							pinst.setSale(this.sale);
							
							pinst.setIdx(idx);
							idx++;
							
							this.sale.getInstallments().add(pinst);
						}
					} catch(Exception ex) {
						if(ContextSupport.isDebug()) {
							ex.printStackTrace();
						}
						LogSupport.log(op() + "Si è verificato un errore durante la ricerca di payment.", Level.INFO);
						LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
					} finally {
						sx.close();
					}
					
					
					
				} else {
					
					
					
					Session sx = HibernateUtil.getSessionFactory().openSession();
					
					try {
						Payment p =(Payment)sx.createQuery("FROM Payment WHERE id = :id_payment").setParameter("id_payment", this.sale.getPayment().getId()).uniqueResult();
						
						List<SalePaymentInstallment> instlist = new ArrayList<SalePaymentInstallment>(0);
						
						Iterator<SalePaymentInstallment> i = this.sale.getInstallments().iterator();
						while(i.hasNext()) {
							SalePaymentInstallment pinst = i.next();
							try {
								if(pinst.getId() == null) {
									if(pinst.getAmount() != null && pinst.getExpire_date() != null) {
										instlist.add(pinst);
									} 
								} else {
									if(p.getFixed_date().equals("SI")) {
										if(getExp_date_001() == null) {
											addFieldError("exp_date_001", "Campo obbligatorio.");
										} else {
											Calendar c = Calendar.getInstance();
											c.setTime(getExp_date_001());
											c.add(Calendar.DATE, p.getPinstallments().get((int)pinst.getIdx()).getDays());
											pinst.setExpire_date(c.getTime());
										}
									} else {
//										Calendar c = Calendar.getInstance();
//										c.setTime(this.sale.getSale_date());
//										c.add(Calendar.DATE, p.getPinstallments().get((int)pinst.getIdx()).getDays());
//										pinst.setExpire_date(c.getTime());
									}
									

									
									instlist.add(pinst);
								}

							} catch(Exception ex) {
								ex.printStackTrace();
							}
							
						}
						
						System.out.println(instlist.size() + " -- " + p.getInstallments());
						if(instlist.size() <= p.getInstallments()) {
							for(int j = 0; j < instlist.size(); j++) {
								instlist.get(j).setAmount(this.sale.getProduct().getTotal() * (p.getPinstallments().get(j).getAmount_perc() / 100));
							}
						} 
						
						this.sale.setInstallments(instlist);
						
					} catch(Exception ex) {
						ex.printStackTrace();
					} finally {
						sx.close();
					}
					

				}
			}	
				
				
		} catch(Exception ex) {
			addFieldError("sale.payment.id", "Si è verificato un errore.");
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo payment.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		
		//validazione provvigioni
		try {
			
			if(this.getSale().getCommissions().get(0) != null) {
				if(this.getSale().getCommissions().get(0).getProduct_amount() == null) {
					this.getSale().getCommissions().remove(0);
				} else {
					
					if(this.getSale().getCommissions().get(0).getProduct_amount() > 0) {
						this.getSale().getCommissions().get(0).setSale(this.sale);
						this.getSale().getCommissions().get(0).setIdx(0);
						
						if(this.getSale().getCommissions().get(0).getBilled_buyer() == null) {
							this.getSale().getCommissions().get(0).setBilled_buyer("NO");
						} else if(this.getSale().getCommissions().get(0).getBilled_buyer().isEmpty()) {
							this.getSale().getCommissions().get(0).setBilled_buyer("NO");
						}
						
						if(this.getSale().getCommissions().get(0).getBilled_seller() == null) {
							this.getSale().getCommissions().get(0).setBilled_seller("NO");
						} else if(this.getSale().getCommissions().get(0).getBilled_seller().isEmpty()) {
							this.getSale().getCommissions().get(0).setBilled_seller("NO");
						}
					} else {
						if(this.getSale().getCommissions().get(0).getId() == null) {
							this.getSale().getCommissions().remove(0);
						}
					}
					
					
				}
			}
			
			
			if(this.getSale().getCommissions().get(1) != null) {
				if(this.getSale().getCommissions().get(1).getProduct_amount() == null) {
					this.getSale().getCommissions().remove(1);
				} else {
					
					if(this.getSale().getCommissions().get(1).getProduct_amount() > 0) {
						this.getSale().getCommissions().get(1).setSale(this.sale);
						this.getSale().getCommissions().get(1).setIdx(1);
						
						if(this.getSale().getCommissions().get(1).getBilled_buyer() == null) {
							this.getSale().getCommissions().get(1).setBilled_buyer("NO");
						} else if(this.getSale().getCommissions().get(1).getBilled_buyer().isEmpty()) {
							this.getSale().getCommissions().get(1).setBilled_buyer("NO");
						}
						
						if(this.getSale().getCommissions().get(1).getBilled_seller() == null) {
							this.getSale().getCommissions().get(1).setBilled_seller("NO");
						} else if(this.getSale().getCommissions().get(1).getBilled_seller().isEmpty()) {
							this.getSale().getCommissions().get(1).setBilled_seller("NO");
						}
					} else {
						if(this.getSale().getCommissions().get(1).getId() == null) {
							this.getSale().getCommissions().remove(1);
						}
						
					}
					
					
				}
			}


			

			
			
		} catch(Exception ex) {
			
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
			}
			LogSupport.log(op() + "Si è verificato un errore durante la validazione del campo commissions.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		}
		
		
	}
	
	public List<Product> getProducts() {
		return SessionSupport.getProducts();
	}

	public List<Customer> getCustomers() {
		return SessionSupport.getCustomers();
	}
	
	public List<Payment> getPayments() {
		return SessionSupport.getPayments();
	}
	
	public List<ContractTemplate> getContract_templates() {
		return SessionSupport.getContract_templates();
	}

}
