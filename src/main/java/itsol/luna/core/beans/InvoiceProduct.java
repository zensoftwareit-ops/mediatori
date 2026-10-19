package itsol.luna.core.beans;

import java.io.Serializable;

public class InvoiceProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3398850918118501979L;
	private Long id;
	private String name;
	
	private Double product_amount;
	private Double vat;
	private Double commission;
	private Double commission_amount;


	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Double getCommission_amount() {
		return commission_amount;
	}

	public void setCommission_amount(Double commission_amount) {
		this.commission_amount = commission_amount;
	}

	private long idx;
	private String unit_of_measure;
	
	private Invoice invoice;
	
	public InvoiceProduct() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getProduct_amount() {
		return product_amount;
	}

	public void setProduct_amount(Double product_amount) {
		this.product_amount = product_amount;
	}



	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}


	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public String getUnit_of_measure() {
		return unit_of_measure;
	}

	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	private Sale sale;


	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	

}
