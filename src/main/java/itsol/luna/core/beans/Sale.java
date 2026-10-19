package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class Sale implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5580581748347502754L;
	
	private Long id;
	private Admin admin;
	private Date insert_date;
	private Date update_date;
	private Date sale_date;
	private String sale_type;
	
	private String pickup_time;
	private String pickup_way;
	private String pickup_place;
	private String pickup_address;
	private String pickup_city;
	private String pickup_province;
	private String pickup_printing;
	private String pickup_zip_code;
	private String pickup_phone;

	private String carico_motrici;
	private String carico_al_coperto;
	private String disinfestato;
	
	private ContractTemplate contract_template;
	
	private String packaging;
	private String particular_conditions;
	private String mediation;	
	
	private Customer seller;
	private Customer buyer;
	
	private SowingYear year;
	
	private Payment payment;
	
	private String pickup_type;
	
	private SaleProduct product;	
	
	private List<SalePaymentInstallment> installments = new ArrayList<SalePaymentInstallment>(0);
	
	private List<SaleCommission> commissions = new ArrayList<SaleCommission>(0);

	public List<SaleCommission> getCommissions() {
		return commissions;
	}

	public void setCommissions(List<SaleCommission> commissions) {
		this.commissions = commissions;
	}

	private Integer number;

	private String invoice_deposit_desc;
	private Date invoice_deposit_date;
	private Double invoice_deposit_amount;
	
	private String invoice_balance_desc;
	private Date invoice_balance_date;
	private Double invoice_balance_amount;	
	
	private String invoice_extra1_desc;
	private Date invoice_extra1_date;
	private Double invoice_extra1_amount;	
	private String invoice_extra2_desc;
	private Date invoice_extra2_date;
	private Double invoice_extra2_amount;	
	
	private String alt_list;
	
	
	private String otpSeller;
	private String signedSeller;
	
	
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

	private Date signedSellerDate;
	
	private String otpBuyer;
	private String signedBuyer;
	private Date signedBuyerDate;
	
	private String otpSellerSent;
	private String otpBuyerSent;
	
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

	public Sale() {
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

	public String getSale_type() {
		return sale_type;
	}

	public void setSale_type(String sale_type) {
		this.sale_type = sale_type;
	}

	public String getPickup_time() {
		return pickup_time;
	}

	public void setPickup_time(String pickup_time) {
		this.pickup_time = pickup_time;
	}

	public String getPickup_way() {
		return pickup_way;
	}

	public void setPickup_way(String pickup_way) {
		this.pickup_way = pickup_way;
	}

	public String getPickup_place() {
		return pickup_place;
	}

	public void setPickup_place(String pickup_place) {
		this.pickup_place = pickup_place;
	}

	public String getPickup_address() {
		return pickup_address;
	}

	public void setPickup_address(String pickup_address) {
		this.pickup_address = pickup_address;
	}

	public String getPickup_city() {
		return pickup_city;
	}

	public void setPickup_city(String pickup_city) {
		this.pickup_city = pickup_city;
	}

	public String getPickup_province() {
		return pickup_province;
	}

	public void setPickup_province(String pickup_province) {
		this.pickup_province = pickup_province;
	}

	public String getPickup_printing() {
		return pickup_printing;
	}

	public void setPickup_printing(String pickup_printing) {
		this.pickup_printing = pickup_printing;
	}

	public String getPickup_zip_code() {
		return pickup_zip_code;
	}

	public void setPickup_zip_code(String pickup_zip_code) {
		this.pickup_zip_code = pickup_zip_code;
	}

	public String getPickup_phone() {
		return pickup_phone;
	}

	public void setPickup_phone(String pickup_phone) {
		this.pickup_phone = pickup_phone;
	}

	public String getCarico_motrici() {
		return carico_motrici;
	}

	public void setCarico_motrici(String carico_motrici) {
		this.carico_motrici = carico_motrici;
	}

	public String getCarico_al_coperto() {
		return carico_al_coperto;
	}

	public void setCarico_al_coperto(String carico_al_coperto) {
		this.carico_al_coperto = carico_al_coperto;
	}

	public String getDisinfestato() {
		return disinfestato;
	}

	public void setDisinfestato(String disinfestato) {
		this.disinfestato = disinfestato;
	}

	public ContractTemplate getContract_template() {
		return contract_template;
	}

	public void setContract_template(ContractTemplate contract_template) {
		this.contract_template = contract_template;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getParticular_conditions() {
		return particular_conditions;
	}

	public void setParticular_conditions(String particular_conditions) {
		this.particular_conditions = particular_conditions;
	}

	public String getMediation() {
		return mediation;
	}

	public void setMediation(String mediation) {
		this.mediation = mediation;
	}

	public Customer getSeller() {
		return seller;
	}

	public void setSeller(Customer seller) {
		this.seller = seller;
	}

	public Customer getBuyer() {
		return buyer;
	}

	public void setBuyer(Customer buyer) {
		this.buyer = buyer;
	}

	public SowingYear getYear() {
		return year;
	}

	public void setYear(SowingYear year) {
		this.year = year;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getPickup_type() {
		return pickup_type;
	}

	public void setPickup_type(String pickup_type) {
		this.pickup_type = pickup_type;
	}

	public SaleProduct getProduct() {
		return product;
	}

	public void setProduct(SaleProduct product) {
		this.product = product;
	}

	public List<SalePaymentInstallment> getInstallments() {
		return installments;
	}

	public void setInstallments(List<SalePaymentInstallment> installments) {
		this.installments = installments;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getInvoice_deposit_desc() {
		return invoice_deposit_desc;
	}

	public void setInvoice_deposit_desc(String invoice_deposit_desc) {
		this.invoice_deposit_desc = invoice_deposit_desc;
	}

	public Date getInvoice_deposit_date() {
		return invoice_deposit_date;
	}

	public void setInvoice_deposit_date(Date invoice_deposit_date) {
		this.invoice_deposit_date = invoice_deposit_date;
	}

	public Double getInvoice_deposit_amount() {
		return invoice_deposit_amount;
	}

	public void setInvoice_deposit_amount(Double invoice_deposit_amount) {
		this.invoice_deposit_amount = invoice_deposit_amount;
	}

	public String getInvoice_balance_desc() {
		return invoice_balance_desc;
	}

	public void setInvoice_balance_desc(String invoice_balance_desc) {
		this.invoice_balance_desc = invoice_balance_desc;
	}

	public Date getInvoice_balance_date() {
		return invoice_balance_date;
	}

	public void setInvoice_balance_date(Date invoice_balance_date) {
		this.invoice_balance_date = invoice_balance_date;
	}

	public Double getInvoice_balance_amount() {
		return invoice_balance_amount;
	}

	public void setInvoice_balance_amount(Double invoice_balance_amount) {
		this.invoice_balance_amount = invoice_balance_amount;
	}

	public String getInvoice_extra1_desc() {
		return invoice_extra1_desc;
	}

	public void setInvoice_extra1_desc(String invoice_extra1_desc) {
		this.invoice_extra1_desc = invoice_extra1_desc;
	}

	public Date getInvoice_extra1_date() {
		return invoice_extra1_date;
	}

	public void setInvoice_extra1_date(Date invoice_extra1_date) {
		this.invoice_extra1_date = invoice_extra1_date;
	}

	public Double getInvoice_extra1_amount() {
		return invoice_extra1_amount;
	}

	public void setInvoice_extra1_amount(Double invoice_extra1_amount) {
		this.invoice_extra1_amount = invoice_extra1_amount;
	}

	public String getInvoice_extra2_desc() {
		return invoice_extra2_desc;
	}

	public void setInvoice_extra2_desc(String invoice_extra2_desc) {
		this.invoice_extra2_desc = invoice_extra2_desc;
	}

	public Date getInvoice_extra2_date() {
		return invoice_extra2_date;
	}

	public void setInvoice_extra2_date(Date invoice_extra2_date) {
		this.invoice_extra2_date = invoice_extra2_date;
	}

	public Double getInvoice_extra2_amount() {
		return invoice_extra2_amount;
	}

	public void setInvoice_extra2_amount(Double invoice_extra2_amount) {
		this.invoice_extra2_amount = invoice_extra2_amount;
	}
	
	//methods
	
	public String getAlt_list() {
		return alt_list;
	}

	public void setAlt_list(String alt_list) {
		this.alt_list = alt_list;
	}

	@SuppressWarnings("unchecked")
	public static List<Sale> getStatistics(Long id_product, Long id_customer, String customer_type, String group, Date date_from, Date date_to) {
		List<Sale> sales = new ArrayList<Sale>(0);
		Session sx = HibernateUtil.getSessionFactory().openSession();

		try {
			Long id_admin = (Long)SessionSupport.getAdmin().getId();
			
			String q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a";
			if(id_product != null || group != null) {
				q += " INNER JOIN s.product p INNER JOIN p.product pr";
						
			}
			if(id_customer != null) {
				q += " INNER JOIN s." + customer_type + " AS c";
			}
			q+= " WHERE a.id = :id_admin";
			if(id_product != null) {
				q += " AND pr.id = " + id_product;
			} else if(group != null) {
				q += " AND pr.groups LIKE '" + group + "%'";
			}
			if(id_customer != null) {
				q += " AND c.id = " + id_customer;
			}
			if(date_from != null) {
				q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(date_from, "yyyy-MM-dd") + "'";
			}		
			if(date_to != null) {
				q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(date_to, "yyyy-MM-dd") + "'";
			}
			q += " ORDER BY s.sale_date DESC, s.number DESC";
			
			if(date_from != null || date_to != null) {
				sales = (List<Sale>) sx.createQuery(q).setParameter("id_admin", id_admin).list();
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			sx.close();
		}
		
		return sales;
	}
	
	

	
	
}
