package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8538327169935360358L;

	private Long id;
	private Date insert_date;
	private Date update_date;
	private Admin admin;
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	private String business_name;
	private String vat_number;
	private String legal_address;
	private String secondary_address;
	private String legal_zip_code;
	private String secondary_zip_code;
	private String legal_province;
	private String secondary_province;
	private String legal_city;
	private String secondary_city;
	private String legal_nation;
	private String secondary_nation;
	private String primary_email;
	private String pec_email;
	private String sdi_code;
	private String extra;
	private String primary_phone;
	private String internal_code;
	private String tax_code;
	private String secondary_phone;
	private String fax;
	private String referent;
	private String secondary_email;

	public String getSecondary_email() {
		return secondary_email;
	}

	public void setSecondary_email(String secondary_email) {
		this.secondary_email = secondary_email;
	}

	public Customer() {

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


	public String getBusiness_name() {
		return business_name;
	}

	public void setBusiness_name(String business_name) {
		this.business_name = business_name;
	}

	public String getVat_number() {
		return vat_number;
	}

	public void setVat_number(String vat_number) {
		this.vat_number = vat_number;
	}

	public String getLegal_address() {
		return legal_address;
	}

	public void setLegal_address(String legal_address) {
		this.legal_address = legal_address;
	}

	public String getSecondary_address() {
		return secondary_address;
	}

	public void setSecondary_address(String secondary_address) {
		this.secondary_address = secondary_address;
	}

	public String getLegal_zip_code() {
		return legal_zip_code;
	}

	public void setLegal_zip_code(String legal_zip_code) {
		this.legal_zip_code = legal_zip_code;
	}

	public String getSecondary_zip_code() {
		return secondary_zip_code;
	}

	public void setSecondary_zip_code(String secondary_zip_code) {
		this.secondary_zip_code = secondary_zip_code;
	}

	public String getLegal_province() {
		return legal_province;
	}

	public void setLegal_province(String legal_province) {
		this.legal_province = legal_province;
	}

	public String getSecondary_province() {
		return secondary_province;
	}

	public void setSecondary_province(String secondary_province) {
		this.secondary_province = secondary_province;
	}

	public String getLegal_city() {
		return legal_city;
	}

	public void setLegal_city(String legal_city) {
		this.legal_city = legal_city;
	}

	public String getSecondary_city() {
		return secondary_city;
	}

	public void setSecondary_city(String secondary_city) {
		this.secondary_city = secondary_city;
	}

	public String getLegal_nation() {
		return legal_nation;
	}

	public void setLegal_nation(String legal_nation) {
		this.legal_nation = legal_nation;
	}

	public String getSecondary_nation() {
		return secondary_nation;
	}

	public void setSecondary_nation(String secondary_nation) {
		this.secondary_nation = secondary_nation;
	}

	public String getPrimary_email() {
		return primary_email;
	}

	public void setPrimary_email(String primary_email) {
		this.primary_email = primary_email;
	}

	public String getPec_email() {
		return pec_email;
	}

	public void setPec_email(String pec_email) {
		this.pec_email = pec_email;
	}

	public String getSdi_code() {
		return sdi_code;
	}

	public void setSdi_code(String sdi_code) {
		this.sdi_code = sdi_code;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getPrimary_phone() {
		return primary_phone;
	}

	public void setPrimary_phone(String primary_phone) {
		this.primary_phone = primary_phone;
	}

	public String getInternal_code() {
		return internal_code;
	}

	public void setInternal_code(String internal_code) {
		this.internal_code = internal_code;
	}

	public String getTax_code() {
		return tax_code;
	}

	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}

	public String getSecondary_phone() {
		return secondary_phone;
	}

	public void setSecondary_phone(String secondary_phone) {
		this.secondary_phone = secondary_phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getReferent() {
		return referent;
	}

	public void setReferent(String referent) {
		this.referent = referent;
	}
	
	public String getSigner() {
		String signer = "";
		if(this.getReferent() != null) {
			if(!this.getReferent().isEmpty() && !this.getReferent().isBlank()) {
				signer = this.getReferent();
			} else {
				signer = this.getBusiness_name();
			}
		} else {
			signer = this.getBusiness_name();
		}
		return signer;
	}
}
