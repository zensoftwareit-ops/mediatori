package itsol.luna.core.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

public class MyActionSupport extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * gestione eccezioni
	 */
	private String excp;
	public String getExcp() {
		return excp;
	}
	public void setExcp(String excp) {
		this.excp = excp;
	}
	
	
	/**
	 * gestione paginazione
	 */
	private Integer pages = 1;
	public Integer getPages() {
		return pages;
	}
	public void setPages(Integer pages) {
		this.pages = pages;
	}
	
	private Integer page = 1;
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	
	private Integer begin;
	public Integer getBegin() {
		return begin;
	}
	public void setBegin(Integer begin) {
		this.begin = begin;
	}
	
	private Integer end;
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	
	private List<Integer> paginator = new ArrayList<Integer>(0);
	public List<Integer> getPaginator() {
		
		for(int i = 1; i <= this.pages; i++) {
			paginator.add(i);
		}
		
		return paginator;
	}
	
	public void createPagination(int size) {
		Integer max = size;
		
		Integer pagination = ContextSupport.getConfig().getPagination();
		
		setPages((int) Math.ceil((double)max / pagination));
		
		setEnd((getPage() * pagination) - 1);
		setBegin(getEnd() - (pagination - 1));
		
		if(getEnd() >= max) {
			setEnd(max - 1);
		}
	}
	
	public String op() {
		String op = new String();
		try {
			op =  this.getClass().getSimpleName() + " - OP#" + SessionSupport.getAdmin().getId() + " - ";
		} catch(Exception ex) {
			op = this.getClass().getSimpleName() + " - NOOP - ";
		}
		return op;
	}

	
	private String command;
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	private String resp;
	public String getResp() {
		return resp;
	}
	public void setResp(String resp) {
		this.resp = resp;
	}
	
	private String resp_status = "none";
	public String getResp_status() {
		return resp_status;
	}
	public void setResp_status(String resp_status) {
		this.resp_status = resp_status;
	}

	public void setResp_status_ok() {
		this.resp_status = "text-success";
	}
	
	public void setResp_status_ko() {
		this.resp_status = "text-danger";
	}

	private String action_type;
	public String getAction_type() {
		return action_type;
	}
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	
	private String redirectAction;
	public String getRedirectAction() {
		return redirectAction;
	}
	public void setRedirectAction(String redirectAction) {
		this.redirectAction = redirectAction;
	}
	
	private String action_name;
	public String getAction_name() {
		return action_name;
	}
	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}
	
	public void saveActionName() {
		this.action_name = ContextSupport.getActionName();
	}
	
	//------------------------------ variabile s (ricerca)
	private String s;
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	
	//------------------------------ variabile id_customer
	private Long id_customer;
	public Long getId_customer() {
		return id_customer;
	}
	public void setId_customer(Long id_customer) {
		this.id_customer = id_customer;
	}

	//------------------------------ variabile id_product
	private Long id_product;
	public Long getId_product() {
		return id_product;
	}
	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}

	//------------------------------ filtro esito analisi
	private String fuori_limite;
	public String getFuori_limite() {
		return fuori_limite;
	}
	public void setFuori_limite(String fuori_limite) {
		this.fuori_limite = fuori_limite;
	}

	//------------------------------ variabile date_from
	private Date date_from;
	public Date getDate_from() {
		return date_from;
	}
	public void setDate_from(Date date_from) {
		this.date_from = date_from;
	}
	
	//------------------------------ variabile date_to
	private Date date_to;
	public Date getDate_to() {
		return date_to;
	}
	public void setDate_to(Date date_to) {
		this.date_to = date_to;
	}
	
	//------------------------------ variabile customer_type
	private String customer_type;
	public String getCustomer_type() {
		return customer_type;
	}
	public void setCustomer_type(String customer_type) {
		this.customer_type = customer_type;
	}
	
	//------------------------------ variabile screen
	private String screen;
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	
	private String pagato;
	public String getPagato() {
		return pagato;
	}
	public void setPagato(String pagato) {
		this.pagato = pagato;
	}

	//------------------------------ comune della sede legale del cliente
	private String comune;
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}

}
