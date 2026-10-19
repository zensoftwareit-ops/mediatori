package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import itsol.luna.core.support.SessionSupport;

public class Admin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9170334700164693487L;

	private Long id;
	private String first_name;
	private String last_name;
	private String username;
	private String password;
	private Date insert_date;
	private Date update_date;
	private Date last_activity_date;
	private String level;
	private String smsEnabled;
	private String smsApiKey;

	
	public String getSmsApiKey() {
		return smsApiKey;
	}

	public void setSmsApiKey(String smsApiKey) {
		this.smsApiKey = smsApiKey;
	}

	private Set<SaleCounter> sale_counters = new HashSet<SaleCounter>(0);
	private Set<InvoiceCounter> invoice_counters = new HashSet<InvoiceCounter>(0);
	

	private String invoice_template;


	public String getInvoice_template() {
		return invoice_template;
	}

	public void setInvoice_template(String invoice_template) {
		this.invoice_template = invoice_template;
	}

	public Set<SaleCounter> getSale_counters() {
		return sale_counters;
	}

	public void setSale_counters(Set<SaleCounter> sale_counters) {
		this.sale_counters = sale_counters;
	}

	public Set<InvoiceCounter> getInvoice_counters() {
		return invoice_counters;
	}

	public void setInvoice_counters(Set<InvoiceCounter> invoice_counters) {
		this.invoice_counters = invoice_counters;
	}

	public Admin() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Date getLast_activity_date() {
		return last_activity_date;
	}

	public void setLast_activity_date(Date last_activity_date) {
		this.last_activity_date = last_activity_date;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getFull_name() {
		return this.first_name +  " " + this.last_name;
	}
	
	public Integer getNextSaleNumber() {
		
		Integer number = -1;
		
		try {
			Iterator<SaleCounter> i = sale_counters.iterator();
			while(i.hasNext()) {
				SaleCounter sc = i.next();
				if(sc.getYear().getId().equals(SessionSupport.getSowingYear().getId())) {
					number = sc.getNext();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return number;
		
	}
	
	public void setNextSaleNumber(Integer number) {
		try {
			Iterator<SaleCounter> i = sale_counters.iterator();
			while(i.hasNext()) {
				SaleCounter sc = i.next();
				if(sc.getYear().getId().equals(SessionSupport.getSowingYear().getId())) {
					sc.setNext(number);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Integer getNextInvoiceNumber() {
		
		Integer number = -1;
		
		try {
			Iterator<InvoiceCounter> i = invoice_counters.iterator();
			while(i.hasNext()) {
				InvoiceCounter ic = i.next();
				if(ic.getYear().getId().equals(SessionSupport.getYear().getId())) {
					number = ic.getNext();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return number;
		
	}

	public String getSmsEnabled() {
		return smsEnabled;
	}

	public void setSmsEnabled(String smsEnabled) {
		this.smsEnabled = smsEnabled;
	}

}
