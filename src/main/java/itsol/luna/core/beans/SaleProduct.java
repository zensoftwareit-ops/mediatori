package itsol.luna.core.beans;

import java.io.Serializable;

public class SaleProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6044189688496756094L;
	
	private Long id_sale;

	private String name;
	private Double product_amount;
	private Double amount;
	private Double total;
	private Double taxable;
	private Double vat;
	private Double vat_amount;
	private String unit_of_measure;

	private Double product_amount_2;
	private Double total_2;
	private Double taxable_2;
	private Double vat_amount_2;

	private String end_of_stock;

	private String quality;
	private String humidity;
	private Integer grains_yield;
	private Integer global_yield;
	private String defects;
	
	private Double d_red_grains;
	private String d_impurity;
	private Double d_damaged_grains;
	private String d_edible_extra;
	private String d_malformations;
	private String d_pinstriped_grains;
	private String d_heated_damaged_grains;
	private String d_non_edible_extra;
	private String triciclazolo;
	private String harvest_year;
	
	private String amount_type;
	
	private Sale sale = new Sale();
	private Product product = new Product();
	
	public SaleProduct() {
		super();
	}

	public Long getId_sale() {
		return id_sale;
	}

	public void setId_sale(Long id_sale) {
		this.id_sale = id_sale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getProduct_amount() {
		return product_amount;
	}

	public void setProduct_amount(Double product_amount) {
		this.product_amount = product_amount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getTaxable() {
		return taxable;
	}

	public void setTaxable(Double taxable) {
		this.taxable = taxable;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public Double getVat_amount() {
		return vat_amount;
	}

	public void setVat_amount(Double vat_amount) {
		this.vat_amount = vat_amount;
	}

	public String getUnit_of_measure() {
		return unit_of_measure;
	}

	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}

	public Double getProduct_amount_2() {
		return product_amount_2;
	}

	public void setProduct_amount_2(Double product_amount_2) {
		this.product_amount_2 = product_amount_2;
	}

	public Double getTotal_2() {
		return total_2;
	}

	public void setTotal_2(Double total_2) {
		this.total_2 = total_2;
	}

	public Double getTaxable_2() {
		return taxable_2;
	}

	public void setTaxable_2(Double taxable_2) {
		this.taxable_2 = taxable_2;
	}

	public Double getVat_amount_2() {
		return vat_amount_2;
	}

	public void setVat_amount_2(Double vat_amount_2) {
		this.vat_amount_2 = vat_amount_2;
	}

	public String getEnd_of_stock() {
		return end_of_stock;
	}

	public void setEnd_of_stock(String end_of_stock) {
		this.end_of_stock = end_of_stock;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public Integer getGrains_yield() {
		return grains_yield;
	}

	public void setGrains_yield(Integer grains_yield) {
		this.grains_yield = grains_yield;
	}

	public Integer getGlobal_yield() {
		return global_yield;
	}

	public void setGlobal_yield(Integer global_yield) {
		this.global_yield = global_yield;
	}

	public String getDefects() {
		return defects;
	}

	public void setDefects(String defects) {
		this.defects = defects;
	}

	public Double getD_red_grains() {
		return d_red_grains;
	}

	public void setD_red_grains(Double d_red_grains) {
		this.d_red_grains = d_red_grains;
	}

	public String getD_impurity() {
		return d_impurity;
	}

	public void setD_impurity(String d_impurity) {
		this.d_impurity = d_impurity;
	}

	public Double getD_damaged_grains() {
		return d_damaged_grains;
	}

	public void setD_damaged_grains(Double d_damaged_grains) {
		this.d_damaged_grains = d_damaged_grains;
	}

	public String getD_edible_extra() {
		return d_edible_extra;
	}

	public void setD_edible_extra(String d_edible_extra) {
		this.d_edible_extra = d_edible_extra;
	}

	public String getD_malformations() {
		return d_malformations;
	}

	public void setD_malformations(String d_malformations) {
		this.d_malformations = d_malformations;
	}

	public String getD_pinstriped_grains() {
		return d_pinstriped_grains;
	}

	public void setD_pinstriped_grains(String d_pinstriped_grains) {
		this.d_pinstriped_grains = d_pinstriped_grains;
	}

	public String getD_heated_damaged_grains() {
		return d_heated_damaged_grains;
	}

	public void setD_heated_damaged_grains(String d_heated_damaged_grains) {
		this.d_heated_damaged_grains = d_heated_damaged_grains;
	}

	public String getD_non_edible_extra() {
		return d_non_edible_extra;
	}

	public void setD_non_edible_extra(String d_non_edible_extra) {
		this.d_non_edible_extra = d_non_edible_extra;
	}

	public String getTriciclazolo() {
		return triciclazolo;
	}

	public void setTriciclazolo(String triciclazolo) {
		this.triciclazolo = triciclazolo;
	}

	public String getHarvest_year() {
		return harvest_year;
	}

	public void setHarvest_year(String harvest_year) {
		this.harvest_year = harvest_year;
	}

	public String getAmount_type() {
		return amount_type;
	}

	public void setAmount_type(String amount_type) {
		this.amount_type = amount_type;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	

}
