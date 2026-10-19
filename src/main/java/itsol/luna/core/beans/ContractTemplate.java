package itsol.luna.core.beans;

import java.io.Serializable;

public class ContractTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6334501996387958817L;
	
	private Long id;
	private Admin admin;
	private String description;
	private String filename;
	private String action;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ContractTemplate() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
