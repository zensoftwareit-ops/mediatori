package itsol.luna.core.beans;

import java.io.Serializable;

import itsol.luna.core.utils.HibernateUtil;

public class InvoiceCounter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4898525596672314420L;
	private Long id;
	private Integer number;
	private Year year;
	private Admin admin;
	
	public InvoiceCounter() {
		super();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
	public Integer getNext() {
		number++;
		
		HibernateUtil.saveOrUpdate(this);
		
		return number;
	}

}
