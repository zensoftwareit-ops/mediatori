package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1698975000222242541L;

	private Long id;
	private Date insert_date;
	private Date update_date;
	private String name;
	private Admin admin;
	private String internal_code;
	private String description;
	private Double vat;
	private String unit_of_measure;
	private Double buyer_commission;
	private Double seller_commission;
	private String groups;
	private String contract_template;

	public Product() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getInsert_date() {
		return insert_date;
	}

	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}

	public Date getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public String getInternal_code() {
		return internal_code;
	}

	public void setInternal_code(String internal_code) {
		this.internal_code = internal_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public String getUnit_of_measure() {
		return unit_of_measure;
	}

	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}

	public Double getBuyer_commission() {
		return buyer_commission;
	}

	public void setBuyer_commission(Double buyer_commission) {
		this.buyer_commission = buyer_commission;
	}

	public Double getSeller_commission() {
		return seller_commission;
	}

	public void setSeller_commission(Double seller_commission) {
		this.seller_commission = seller_commission;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getContract_template() {
		return contract_template;
	}

	public void setContract_template(String contract_template) {
		this.contract_template = contract_template;
	}


	
}
