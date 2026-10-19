package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9039832455569565095L;
	private Long id;
	
	private Admin admin;
	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	private Year year;
	
	private Date insert_date;
	private Date update_date;
	private Date invoice_date;
	private Date expire_date;
	
	private String invoice_type;
	
	private String payed;
	
	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public Date getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}

	private Double total;
	private Double vat_total;
	private Double taxable_total;
	
	public Double getTaxable_total() {
		return taxable_total;
	}

	public void setTaxable_total(Double taxable_total) {
		this.taxable_total = taxable_total;
	}

	private List<InvoiceProduct> products = new ArrayList<InvoiceProduct>(0);
	//private List<InvoicePayment> payments = new ArrayList<InvoicePayment>(0);
	
	private String number;

	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Invoice() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getInsert_date() {
		return insert_date;
	}

	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}

	public Date getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}

	public Date getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getVat_total() {
		return vat_total;
	}

	public void setVat_total(Double vat_total) {
		this.vat_total = vat_total;
	}

	public List<InvoiceProduct> getProducts() {
		return products;
	}

	public void setProducts(List<InvoiceProduct> products) {
		this.products = products;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}
	
	public Double getHolding_tax() {
		return 0.23 * (0.5 * getTaxable_total());
	}
	
	public Double getTotal_without_holding() {
		return getTotal() - getHolding_tax();
	}

//	public List<InvoicePayment> getPayments() {
//		return payments;
//	}
//
//	public void setPayments(List<InvoicePayment> payments) {
//		this.payments = payments;
//	}
	

}
