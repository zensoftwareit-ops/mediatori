package itsol.luna.core.beans;

import java.io.Serializable;

public class Config implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2984367486827131623L;
	
	private Long id;
	private Year year;
	private SowingYear sowing_year;
	public SowingYear getSowing_year() {
		return sowing_year;
	}

	public void setSowing_year(SowingYear sowing_year) {
		this.sowing_year = sowing_year;
	}

	private Integer pagination;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Integer getPagination() {
		return pagination;
	}

	public void setPagination(Integer pagination) {
		this.pagination = pagination;
	}

}
