package itsol.luna.core.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Analisi implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4124834374815654098L;
	private Long id;
	private Long id_admin;
	private Date dataAnalisi;
	private SowingYear year;
	public Long getId_admin() {
		return id_admin;
	}
	public void setId_admin(Long id_admin) {
		this.id_admin = id_admin;
	}
	public Date getDataAnalisi() {
		return dataAnalisi;
	}
	public void setDataAnalisi(Date dataAnalisi) {
		this.dataAnalisi = dataAnalisi;
	}
	public SowingYear getYear() {
		return year;
	}
	public void setYear(SowingYear year) {
		this.year = year;
	}
	private String nCampione;
	private Product prodotto;
	private Customer cliente;
	private String lotto;
	private String cadmio;
	private String arsenicoTot;
	private String arsenicoInorganico;
	private String nichel;
	private String altreAnalisi;
	private String note;
	private Double costo;
	
	private String pagato;
	
	private String cadmioFl;
	private String arsenicoFl;
	private String nichelFl;
	
	public String getCadmioFl() {
		return cadmioFl;
	}
	public void setCadmioFl(String cadmioFl) {
		this.cadmioFl = cadmioFl;
	}
	public String getArsenicoFl() {
		return arsenicoFl;
	}
	public void setArsenicoFl(String arsenicoFl) {
		this.arsenicoFl = arsenicoFl;
	}
	public String getNichelFl() {
		return nichelFl;
	}
	public void setNichelFl(String nichelFl) {
		this.nichelFl = nichelFl;
	}
	public boolean isNonValida() {
		return isCadmioFuoriLimite() || isArsenicoFuoriLimite() || isNichelFuoriLimite();
	}
	public String getEsito() {
		return isNonValida() ? "NON VALIDA" : "VALIDA";
	}
	public boolean isCadmioFuoriLimite() {
		return "SI".equalsIgnoreCase(cadmioFl) || superaLimite(cadmio, new BigDecimal("0.15"));
	}
	public boolean isArsenicoFuoriLimite() {
		return "SI".equalsIgnoreCase(arsenicoFl) || superaLimite(arsenicoInorganico, new BigDecimal("0.15"));
	}
	public boolean isNichelFuoriLimite() {
		return "SI".equalsIgnoreCase(nichelFl) || superaLimite(nichel, new BigDecimal("1.5"));
	}
	private boolean superaLimite(String valore, BigDecimal limite) {
		if(valore == null) return false;
		String normalizzato = valore.trim().replace(',', '.');
		boolean maggioreDi = normalizzato.startsWith(">");
		normalizzato = normalizzato.replaceFirst("^[<>]=?\\s*", "");
		try {
			int confronto = new BigDecimal(normalizzato).compareTo(limite);
			return confronto > 0 || (maggioreDi && confronto == 0);
		} catch(NumberFormatException ex) {
			return false;
		}
	}
	public String getPagato() {
		return pagato;
	}
	public void setPagato(String pagato) {
		this.pagato = pagato;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getnCampione() {
		return nCampione;
	}
	public void setnCampione(String nCampione) {
		this.nCampione = nCampione;
	}

	public Product getProdotto() {
		return prodotto;
	}
	public void setProdotto(Product prodotto) {
		this.prodotto = prodotto;
	}
	public Customer getCliente() {
		return cliente;
	}
	public void setCliente(Customer cliente) {
		this.cliente = cliente;
	}
	public String getLotto() {
		return lotto;
	}
	public void setLotto(String lotto) {
		this.lotto = lotto;
	}
	public String getCadmio() {
		return cadmio;
	}
	public void setCadmio(String cadmio) {
		this.cadmio = cadmio;
	}
	public String getArsenicoTot() {
		return arsenicoTot;
	}
	public void setArsenicoTot(String arsenicoTot) {
		this.arsenicoTot = arsenicoTot;
	}
	public String getArsenicoInorganico() {
		return arsenicoInorganico;
	}
	public void setArsenicoInorganico(String arsenicoInorganico) {
		this.arsenicoInorganico = arsenicoInorganico;
	}
	public String getNichel() {
		return nichel;
	}
	public void setNichel(String nichel) {
		this.nichel = nichel;
	}
	public String getAltreAnalisi() {
		return altreAnalisi;
	}
	public void setAltreAnalisi(String altreAnalisi) {
		this.altreAnalisi = altreAnalisi;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Double getCosto() {
		return costo;
	}
	public void setCosto(Double costo) {
		this.costo = costo;
	}
}
