package itsol.luna.core.actions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Invoice;
import itsol.luna.core.beans.InvoiceProduct;
import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.beans.Seller;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class InvoiceNewFromSeller extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3846907934441210426L;

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

	private Invoice invoice;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	private Date expire_date;

	public Date getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
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
					this.invoice = new Invoice();

					this.invoice.setAdmin(SessionSupport.getAdmin());
					this.invoice.setCustomer(this.seller.getCustomer());
					this.invoice.setYear(SessionSupport.getYear());
					this.invoice.setInvoice_date(new Date());
					this.invoice.setExpire_date(getExpire_date());

					int idx = 0;
					Double total = 0.00;
					Double taxable_total = 0.00;
					Double vat_total = 0.00;

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
									SaleProduct sp = s.getProduct();

									InvoiceProduct ip = new InvoiceProduct();
									ip.setName(sp.getName());

									ip.setProduct_amount(sc.getProduct_amount());
									ip.setVat(22.0);
									ip.setCommission(sc.getSeller_commission());
									ip.setCommission_amount(sc.getSeller_commission_amount());
									ip.setUnit_of_measure(sp.getUnit_of_measure());

									ip.setInvoice(this.invoice);

									ip.setIdx(idx);

									idx++;

									ip.setSale(s);

									this.invoice.getProducts().add(ip);

									taxable_total = taxable_total + ip.getCommission_amount();
									
									sc.setBilled_seller("SI");
									sc.setInvoice_seller(this.invoice);
								}
							}
						}



					}

					vat_total = taxable_total * 0.22;
					total = taxable_total + vat_total;

					this.invoice.setTotal(total);
					this.invoice.setTaxable_total(taxable_total);
					this.invoice.setVat_total(vat_total);
					this.invoice.setInvoice_type("seller");

					this.invoice.setNumber(SessionSupport.getAdmin().getNextInvoiceNumber().toString());

					HibernateUtil.saveOrUpdate(this.invoice);
					HibernateUtil.saveOrUpdate(this.seller);

					setResp("E' stata salvata la fattura N. " + this.invoice.getNumber() + ".");
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
				setResp("Si è verificato un errore durante la creazione della fattura.");
				setResp_status_ko();
				result = ERROR;
			}
			LogSupport.log(op() + "Si è verificato un errore durante la creazione della fattura.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);
		}

		return result;
	}

}
