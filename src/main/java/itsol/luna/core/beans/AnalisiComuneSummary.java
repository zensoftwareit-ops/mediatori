package itsol.luna.core.beans;

import java.io.Serializable;
import java.util.Locale;

public class AnalisiComuneSummary implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String comune;
	private int totale;
	private int nonValide;
	private int cadmioFuoriLimite;
	private int arsenicoFuoriLimite;
	private int nichelFuoriLimite;

	public AnalisiComuneSummary(String comune) {
		this.comune = comune;
	}

	public void add(Analisi analisi) {
		totale++;
		if(analisi.isNonValida()) nonValide++;
		if(analisi.isCadmioFuoriLimite()) cadmioFuoriLimite++;
		if(analisi.isArsenicoFuoriLimite()) arsenicoFuoriLimite++;
		if(analisi.isNichelFuoriLimite()) nichelFuoriLimite++;
	}

	private String percentuale(int valore) {
		return totale == 0 ? "0,0%" : String.format(Locale.ITALY, "%.1f%%", (valore * 100.0) / totale);
	}

	public String getComune() { return comune; }
	public int getTotale() { return totale; }
	public int getNonValide() { return nonValide; }
	public int getCadmioFuoriLimite() { return cadmioFuoriLimite; }
	public int getArsenicoFuoriLimite() { return arsenicoFuoriLimite; }
	public int getNichelFuoriLimite() { return nichelFuoriLimite; }
	public String getPercentualeNonValide() { return percentuale(nonValide); }
	public String getPercentualeCadmio() { return percentuale(cadmioFuoriLimite); }
	public String getPercentualeArsenico() { return percentuale(arsenicoFuoriLimite); }
	public String getPercentualeNichel() { return percentuale(nichelFuoriLimite); }
}
