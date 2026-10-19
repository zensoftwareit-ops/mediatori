package itsol.luna.core.beans;

import java.io.Serializable;

public class ProductGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8247957701278442624L;
	
	private Long id;
	private String name;
	private String tag;
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public ProductGroup() {
		super();
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
	
	public String getListName() {
		String listName = "";
		
		if(this.status.equals("master")) {
			listName = this.name.toUpperCase();
		} else if(this.status.equals("slave")) {
			listName = "-- ".concat(this.name);
		}
		
		System.out.println(listName);
		
		return listName;
	}
	

}
