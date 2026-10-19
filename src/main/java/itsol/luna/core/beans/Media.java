package itsol.luna.core.beans;

import java.io.Serializable;

public class Media implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9022835292925635448L;
	private Long id;
	private String filename;
	private String category;
	
	public Media() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	

}
