package itsol.luna.core.beans;

import java.io.Serializable;

public class SowingYear implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7408542216027687482L;
	
	private Long id;
	private String year;
	
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
	
	public SowingYear() {
		super();
	}

}
