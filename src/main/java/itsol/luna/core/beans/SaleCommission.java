package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleCommission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6524811963097243151L;
	
	private Long id;
	private Sale sale;
	private long idx;
	private Invoice invoice_buyer;
	private Invoice invoice_seller;
	private String billed_buyer;
	private String billed_seller;
	private String billed_buyer_alt;
	private String billed_seller_alt;
	private Double seller_commission;
	private Double seller_commission_amount;
	private Double buyer_commission;
	private Double buyer_commission_amount;
	private Double product_amount;
	
	public SaleCommission() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public Invoice getInvoice_buyer() {
		return invoice_buyer;
	}

	public void setInvoice_buyer(Invoice invoice_buyer) {
		this.invoice_buyer = invoice_buyer;
	}

	public Invoice getInvoice_seller() {
		return invoice_seller;
	}

	public void setInvoice_seller(Invoice invoice_seller) {
		this.invoice_seller = invoice_seller;
	}

	public String getBilled_buyer() {
		return billed_buyer;
	}

	public void setBilled_buyer(String billed_buyer) {
		this.billed_buyer = billed_buyer;
	}

	public String getBilled_seller() {
		return billed_seller;
	}

	public void setBilled_seller(String billed_seller) {
		this.billed_seller = billed_seller;
	}

	public String getBilled_buyer_alt() {
		return billed_buyer_alt;
	}

	public void setBilled_buyer_alt(String billed_buyer_alt) {
		this.billed_buyer_alt = billed_buyer_alt;
	}

	public String getBilled_seller_alt() {
		return billed_seller_alt;
	}

	public void setBilled_seller_alt(String billed_seller_alt) {
		this.billed_seller_alt = billed_seller_alt;
	}

	public Double getSeller_commission() {
		return seller_commission;
	}

	public void setSeller_commission(Double seller_commission) {
		this.seller_commission = seller_commission;
	}

	public Double getSeller_commission_amount() {
		return seller_commission_amount;
	}

	public void setSeller_commission_amount(Double seller_commission_amount) {
		this.seller_commission_amount = seller_commission_amount;
	}

	public Double getBuyer_commission() {
		return buyer_commission;
	}

	public void setBuyer_commission(Double buyer_commission) {
		this.buyer_commission = buyer_commission;
	}

	public Double getBuyer_commission_amount() {
		return buyer_commission_amount;
	}

	public void setBuyer_commission_amount(Double buyer_commission_amount) {
		this.buyer_commission_amount = buyer_commission_amount;
	}

	public Double getProduct_amount() {
		return product_amount;
	}

	public void setProduct_amount(Double product_amount) {
		this.product_amount = product_amount;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SaleCommission> getCommissions(Long id_customer, String customer_type, String billed_status, Date date_from, Date date_to) {
		List<SaleCommission> sales = new ArrayList<SaleCommission>(0);
		Session sx = HibernateUtil.getSessionFactory().openSession();

		try {
			Long id_admin = (Long)SessionSupport.getAdmin().getId();
				
			String q = "SELECT sc FROM SaleCommission sc INNER JOIN sc.sale s INNER JOIN s.admin a";

			if (id_customer != null) {
				q += " INNER JOIN s." + customer_type +  " c";
			}

			q += " WHERE a.id = :id_admin";
			
			switch(billed_status) {
			case "not_billed":
				q += " AND sc.billed_" + customer_type + " = 'NO'";
				break;
			case "billed":
				q += " AND sc.billed_" + customer_type + " = 'SI'";
				break;
			case "all":
				q += " AND 1=1";
				break;
			}
			
			

			if (id_customer != null) {
				q += " AND s." + customer_type + " = c AND c.id = " + id_customer;
			}

			if (date_from != null) {
				q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(date_from, "yyyy-MM-dd")
						+ "'";
			}

			if (date_to != null) {
				q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(date_to, "yyyy-MM-dd")
						+ "'";
				
			}

			q += " ORDER BY s.sale_date DESC, s.number DESC";
			
			System.out.println(q);
			
			sales = (List<SaleCommission>) sx.createQuery(q).setParameter("id_admin", id_admin).list();
			
			System.out.println(sales.size());
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			sx.close();
		}
		
		return sales;
	}

}
