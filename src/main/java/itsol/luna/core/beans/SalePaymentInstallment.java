package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;

public class SalePaymentInstallment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5549796226223807426L;
	
	private Long id;
	private Sale sale;
	private Date expire_date;
	private Double amount;
	private String payed;
	private long idx;

	public SalePaymentInstallment() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Date getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(Date expire_date) {
		this.expire_date = expire_date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	

}
