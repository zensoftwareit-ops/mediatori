package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Payment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6288956117128828206L;
	
	private Long id;
	private String ptag;
	private String description;
	private Integer installments;
	private String fixed_date;
	
	public String getFixed_date() {
		return fixed_date;
	}

	public void setFixed_date(String fixed_date) {
		this.fixed_date = fixed_date;
	}

	private List<PaymentInstallment> pinstallments = new ArrayList<PaymentInstallment>(0);
	
	public List<PaymentInstallment> getPinstallments() {
		return pinstallments;
	}

	public void setPinstallments(List<PaymentInstallment> pinstallments) {
		this.pinstallments = pinstallments;
	}

	public Integer getInstallments() {
		return installments;
	}

	public void setInstallments(Integer installments) {
		this.installments = installments;
	}

	private Admin admin;

	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public Payment() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPtag() {
		return ptag;
	}

	public void setPtag(String ptag) {
		this.ptag = ptag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExpanded_description() {
		return ptag + " - " + description;
	}

}
