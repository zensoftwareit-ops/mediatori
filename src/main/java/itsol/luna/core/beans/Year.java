package itsol.luna.core.beans;

import java.io.Serializable;

public class Year implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980816305906794136L;
	
	private Long id;
	private String year;
	
	public Year() {
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}

}
