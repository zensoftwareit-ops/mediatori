package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;

public class FastSale implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7143844359839598499L;
	
	private Long id;
	private Admin admin;
	private Date insert_date;
	private Date update_date;
	private Date sale_date;
	private Integer number;
	private ContractTemplate contract_template;
	
	private String otpSeller;
	private String signedSeller;
	private Date signedSellerDate;
	
	public String getOtpSeller() {
		return otpSeller;
	}

	public void setOtpSeller(String otpSeller) {
		this.otpSeller = otpSeller;
	}

	public String getSignedSeller() {
		return signedSeller;
	}

	public void setSignedSeller(String signedSeller) {
		this.signedSeller = signedSeller;
	}

	public Date getSignedSellerDate() {
		return signedSellerDate;
	}

	public void setSignedSellerDate(Date signedSellerDate) {
		this.signedSellerDate = signedSellerDate;
	}

	public String getOtpBuyer() {
		return otpBuyer;
	}

	public void setOtpBuyer(String otpBuyer) {
		this.otpBuyer = otpBuyer;
	}

	public String getSignedBuyer() {
		return signedBuyer;
	}

	public void setSignedBuyer(String signedBuyer) {
		this.signedBuyer = signedBuyer;
	}

	public Date getSignedBuyerDate() {
		return signedBuyerDate;
	}

	public void setSignedBuyerDate(Date signedBuyerDate) {
		this.signedBuyerDate = signedBuyerDate;
	}

	public String getOtpSellerSent() {
		return otpSellerSent;
	}

	public void setOtpSellerSent(String otpSellerSent) {
		this.otpSellerSent = otpSellerSent;
	}

	public String getOtpBuyerSent() {
		return otpBuyerSent;
	}

	public void setOtpBuyerSent(String otpBuyerSent) {
		this.otpBuyerSent = otpBuyerSent;
	}


	private String otpBuyer;
	private String signedBuyer;
	private Date signedBuyerDate;
	
	private String otpSellerSent;
	private String otpBuyerSent;
	
	public ContractTemplate getContract_template() {
		return contract_template;
	}

	public void setContract_template(ContractTemplate contract_template) {
		this.contract_template = contract_template;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}


	private FastCustomer seller;
	private FastCustomer buyer;
	
	private SowingYear year;
	
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

	public Date getSale_date() {
		return sale_date;
	}

	public void setSale_date(Date sale_date) {
		this.sale_date = sale_date;
	}

	

	public FastCustomer getSeller() {
		return seller;
	}

	public void setSeller(FastCustomer seller) {
		this.seller = seller;
	}

	public FastCustomer getBuyer() {
		return buyer;
	}

	public void setBuyer(FastCustomer buyer) {
		this.buyer = buyer;
	}

	public SowingYear getYear() {
		return year;
	}

	public void setYear(SowingYear year) {
		this.year = year;
	}



	public SaleProduct getProduct() {
		return product;
	}

	public void setProduct(SaleProduct product) {
		this.product = product;
	}

	
	private SaleProduct product;	
	


}
