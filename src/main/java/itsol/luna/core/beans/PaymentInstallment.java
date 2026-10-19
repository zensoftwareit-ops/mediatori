package itsol.luna.core.beans;

import java.io.Serializable;

public class PaymentInstallment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4560120726834369280L;
	
	private Long id;
	private Payment payment;
	private Integer days;
	private Double amount_perc;
	private long idx;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public PaymentInstallment() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Double getAmount_perc() {
		return amount_perc;
	}

	public void setAmount_perc(Double amount_perc) {
		this.amount_perc = amount_perc;
	}

}
