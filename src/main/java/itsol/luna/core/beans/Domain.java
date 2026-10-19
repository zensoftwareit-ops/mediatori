package itsol.luna.core.beans;

import java.io.Serializable;

public class Domain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6323478901505254950L;
	
	private Long id;
	private String domain;
	private String url;
	private String res_path;
	
	public Domain() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRes_path() {
		return res_path;
	}

	public void setRes_path(String res_path) {
		this.res_path = res_path;
	}

}
