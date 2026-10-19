package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;

public class YieldMeasure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8684155535541654436L;
	
	private Long id;
	private Admin admin;
	private Customer customer;
	private Product product;
	public String getNote_1() {
		return note_1;
	}

	public void setNote_1(String note_1) {
		this.note_1 = note_1;
	}

	public String getNote_2() {
		return note_2;
	}

	public void setNote_2(String note_2) {
		this.note_2 = note_2;
	}

	private Double grains_yield;
	private Double global_yield;
	private String note_1;
	private String note_2;
	private Date insert_date;
	
	private SowingYear year;
	
	public SowingYear getYear() {
		return year;
	}

	public void setYear(SowingYear year) {
		this.year = year;
	}

	private Double thousand_grains;
	
	public Double getThousand_grains() {
		return thousand_grains;
	}

	public void setThousand_grains(Double thousand_grains) {
		this.thousand_grains = thousand_grains;
	}

	public Date getInsert_date() {
		return insert_date;
	}

	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}

	public YieldMeasure() {
		
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}



	public Double getGrains_yield() {
		return grains_yield;
	}

	public void setGrains_yield(Double grains_yield) {
		this.grains_yield = grains_yield;
	}

	public Double getGlobal_yield() {
		return global_yield;
	}

	public void setGlobal_yield(Double global_yield) {
		this.global_yield = global_yield;
	}
	
	private Double defects_1;
	private Double defects_2;
	public Double getDefects_1() {
		return defects_1;
	}

	public void setDefects_1(Double defects_1) {
		this.defects_1 = defects_1;
	}

	public Double getDefects_2() {
		return defects_2;
	}

	public void setDefects_2(Double defects_2) {
		this.defects_2 = defects_2;
	}


}
