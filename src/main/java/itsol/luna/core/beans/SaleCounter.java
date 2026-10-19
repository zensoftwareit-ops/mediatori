package itsol.luna.core.beans;

import java.io.Serializable;

import itsol.luna.core.utils.HibernateUtil;

public class SaleCounter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4614945908317572923L;
	private Long id;
	private Integer number = 1;
	private SowingYear year;
	private Admin admin;
	
	public SaleCounter() {
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

	public SowingYear getYear() {
		return year;
	}

	public void setYear(SowingYear year) {
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
		
		try {
			HibernateUtil.saveOrUpdate(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		return number;
	}
	
	public void setNext(Integer next_number) {
		number = next_number;
		
		try {
			HibernateUtil.saveOrUpdate(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

}
