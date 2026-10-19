package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CommissionCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033118516423577917L;

	private List<SaleCommission> commissions = new ArrayList<SaleCommission>(0);

	public List<SaleCommission> getCommissions() {
		return commissions;
	}

	public void setCommissions(List<SaleCommission> commissions) {
		this.commissions = commissions;
	}

	private String billed_status;

	public String getBilled_status() {
		return billed_status;
	}

	public void setBilled_status(String billed_status) {
		this.billed_status = billed_status;
	}

	private Double tot_billed = 0.00;
	private Double tot_not_billed = 0.00;
	private Double qta = 0.00;

	private Long[] commissions_ids;

	public Long[] getCommissions_ids() {
		return commissions_ids;
	}

	public void setCommissions_ids(Long[] commissions_ids) {
		this.commissions_ids = commissions_ids;
	}

	String title = "";

	public class HeaderFooter extends PdfPageEventHelper {

		public void onStartPage(PdfWriter writer, Document document) {
			FontSelector selector = new FontSelector();
			Font f = FontFactory.getFont(FontFactory.HELVETICA, 7);
			selector.addFont(f);

			try {

				Phrase ph;

				PdfPTable table = new PdfPTable(3);
				table.setWidthPercentage(100);
				table.setSpacingAfter(15);

				ph = selector.process(title);
				PdfPCell cell1 = new PdfPCell(ph);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBorderWidthTop(0);
				cell1.setBorderWidthLeft(0);
				cell1.setBorderWidthRight(0);
				cell1.setBorderWidthBottom(0);
				table.addCell(cell1);

				PdfPCell cell2 = new PdfPCell();
				cell2.setBorderWidthTop(0);
				cell2.setBorderWidthLeft(0);
				cell2.setBorderWidthRight(0);
				cell2.setBorderWidthBottom(0);
				table.addCell(cell2);

				ph = selector.process("Pagina " + writer.getPageNumber());
				PdfPCell cell3 = new PdfPCell(ph);
				cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell3.setBorderWidthTop(0);
				cell3.setBorderWidthLeft(0);
				cell3.setBorderWidthRight(0);
				cell3.setBorderWidthBottom(0);
				table.addCell(cell3);

				document.add(table);

			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

		public void onEndPage(PdfWriter writer, Document document) {

		}

	}

	public String execute() {

		Session sx = HibernateUtil.getSessionFactory().openSession();
		title = "Elenco provvigioni";
		
		if (getDate_from() != null) {
			title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
		}
		
		if (getDate_to() != null) {
			title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
		}

		try {

			if (SessionSupport.checkSession()) {

				if (SessionSupport.checkSessionLevel("admin|super")) {

				} else {
					this.commissions = SaleCommission.getCommissions(getId_customer(), getCustomer_type(),
							getBilled_status(), getDate_from(), getDate_to());
				}

				try {
					Document document = new Document();
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					PdfWriter writer = PdfWriter.getInstance(document, output);

					FontSelector selector = new FontSelector();
					Font f = FontFactory.getFont(FontFactory.HELVETICA, 7);
					selector.addFont(f);

					document.setPageSize(PageSize.A4.rotate());

					HeaderFooter hf = new HeaderFooter();
					writer.setPageEvent(hf);

					document.open();

					Phrase ph;
					// document.add(ph);

					Iterator<SaleCommission> i = this.commissions.iterator();

					while (i.hasNext()) {

						SaleCommission sc = i.next();
						Sale s = sc.getSale();

						if (Arrays.asList(getCommissions_ids()).contains(sc.getId())) {

							SaleProduct sp = sc.getSale().getProduct();

							if (getCustomer_type().equals("buyer")) {

								PdfPTable table = new PdfPTable(
										new float[] { 5, 10, 25, 15, 8, 8, 8, 8, 8, 10, 10, 10 });
								table.setWidthPercentage(100);
								table.setSpacingAfter(15);

								ph = selector.process("N");
								table.addCell(ph);

								ph = selector.process("Data vendita");
								table.addCell(ph);

								ph = selector.process("Acquirente/venditore");
								table.addCell(ph);

								ph = selector.process("Prodotto");
								table.addCell(ph);

								ph = selector.process("Quantità");
								table.addCell(ph);

								ph = selector.process("Importo");
								table.addCell(ph);

								ph = selector.process("Totale");
								table.addCell(ph);

								ph = selector.process("Ritirato");
								table.addCell(ph);

								ph = selector.process("Provv.");
								table.addCell(ph);

								ph = selector.process("Provv. Tot.");
								table.addCell(ph);

								ph = selector.process("N. Fatt.");
								table.addCell(ph);

								ph = selector.process("Data");
								table.addCell(ph);

								//

								ph = selector.process(s.getNumber().toString());
								table.addCell(ph);

								ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(s.getSale_date()));
								table.addCell(ph);

								ph = selector.process(
										s.getBuyer().getBusiness_name() + "\n" + s.getSeller().getBusiness_name());
								table.addCell(ph);

								ph = selector.process(sp.getProduct().getName());
								table.addCell(ph);

								ph = selector.process(NumberFormat.getNumberInstance().format(sp.getProduct_amount()));
								table.addCell(ph);

								ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getAmount()));
								table.addCell(ph);

								ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getTotal()));
								table.addCell(ph);

								qta = qta + sc.getProduct_amount();
								ph = selector.process(NumberFormat.getNumberInstance().format(sc.getProduct_amount()));
								table.addCell(ph);

								ph = selector
										.process(NumberFormat.getCurrencyInstance().format(sc.getBuyer_commission()));
								table.addCell(ph);

								ph = selector.process(
										NumberFormat.getCurrencyInstance().format(sc.getBuyer_commission_amount()));
								table.addCell(ph);

								if (sc.getBilled_buyer().contentEquals("SI")) {
									tot_billed = tot_billed + sc.getBuyer_commission_amount();
								} else if (sc.getBilled_buyer().contentEquals("NO")) {
									tot_not_billed = tot_not_billed + sc.getBuyer_commission_amount();
								}

								try {

									String fatt = "";
									try {
										if (s.getInvoice_deposit_desc() != null) {
											if (!s.getInvoice_deposit_desc().isEmpty()) {
												fatt = s.getInvoice_deposit_desc() + " (acconto)\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_balance_desc() != null) {
											if (!s.getInvoice_balance_desc().isEmpty()) {
												fatt += s.getInvoice_balance_desc() + "\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra1_desc() != null) {
											if (!s.getInvoice_extra1_desc().isEmpty()) {
												fatt += s.getInvoice_extra1_desc() + "\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra2_desc() != null) {
											if (!s.getInvoice_extra2_desc().isEmpty()) {
												fatt += s.getInvoice_extra2_desc();
											}
										}
									} catch (Exception ex) {

									}

									ph = selector.process(fatt);
									table.addCell(ph);
								} catch (Exception ex) {
									ph = selector.process("");
									table.addCell(ph);
								}

								try {

									String fatt = "";
									try {
										if (s.getInvoice_deposit_date() != null) {
											fatt = new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_deposit_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_balance_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_balance_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra1_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_extra1_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra2_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_extra2_date());
										}
									} catch (Exception ex) {

									}

									ph = selector.process(fatt);
									table.addCell(ph);

								} catch (Exception ex) {
									ph = selector.process("");
									table.addCell(ph);
								}

								document.add(table);

							} else if (getCustomer_type().equals("seller")) {

								PdfPTable table = new PdfPTable(new float[] { 5, 10, 25, 15, 8, 8, 8, 8, 10, 10, 10 });
								table.setWidthPercentage(100);
								table.setSpacingAfter(15);

								ph = selector.process("N");
								table.addCell(ph);

								ph = selector.process("Data vendita");
								table.addCell(ph);

								ph = selector.process("Venditore/acquirente");
								table.addCell(ph);

								ph = selector.process("Prodotto");
								table.addCell(ph);

								ph = selector.process("Quantità");
								table.addCell(ph);

								ph = selector.process("Importo");
								table.addCell(ph);

								ph = selector.process("Totale");
								table.addCell(ph);

								ph = selector.process("Provv.");
								table.addCell(ph);

								ph = selector.process("Provv. Tot.");
								table.addCell(ph);

								ph = selector.process("N. Fatt.");
								table.addCell(ph);

								ph = selector.process("Data");
								table.addCell(ph);

								//

								ph = selector.process(s.getNumber().toString());
								table.addCell(ph);

								ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(s.getSale_date()));
								table.addCell(ph);

								ph = selector.process(
										s.getSeller().getBusiness_name() + "\n" + s.getBuyer().getBusiness_name());
								table.addCell(ph);

								ph = selector.process(sp.getProduct().getName());
								table.addCell(ph);

								qta = qta + sp.getProduct_amount();
								ph = selector.process(NumberFormat.getNumberInstance().format(sp.getProduct_amount()));
								table.addCell(ph);

								ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getAmount()));
								table.addCell(ph);

								ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getTotal()));
								table.addCell(ph);

								Double seller_commission = 0.00;
								Double seller_commission_amount = 0.00;

								seller_commission = seller_commission + sc.getSeller_commission();
								seller_commission_amount = seller_commission_amount + sc.getSeller_commission_amount();

								if (sc.getBilled_seller().contentEquals("SI")) {
									tot_billed = tot_billed + sc.getSeller_commission_amount();
								} else if (sc.getBilled_seller().contentEquals("NO")) {
									tot_not_billed = tot_not_billed + sc.getSeller_commission_amount();
								}

								ph = selector.process(NumberFormat.getCurrencyInstance().format(seller_commission));
								table.addCell(ph);

								ph = selector
										.process(NumberFormat.getCurrencyInstance().format(seller_commission_amount));
								table.addCell(ph);

								try {

									String fatt = "";
									try {
										if (s.getInvoice_deposit_desc() != null) {
											if (!s.getInvoice_deposit_desc().isEmpty()) {
												fatt = s.getInvoice_deposit_desc() + " (acconto)\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_balance_desc() != null) {
											if (!s.getInvoice_balance_desc().isEmpty()) {
												fatt += s.getInvoice_balance_desc() + "\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra1_desc() != null) {
											if (!s.getInvoice_extra1_desc().isEmpty()) {
												fatt += s.getInvoice_extra1_desc() + "\n";
											}
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra2_desc() != null) {
											if (!s.getInvoice_extra2_desc().isEmpty()) {
												fatt += s.getInvoice_extra2_desc();
											}
										}
									} catch (Exception ex) {

									}

									ph = selector.process(fatt);
									table.addCell(ph);
								} catch (Exception ex) {
									ph = selector.process("");
									table.addCell(ph);
								}

								try {

									String fatt = "";
									try {
										if (s.getInvoice_deposit_date() != null) {
											fatt = new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_deposit_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_balance_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_balance_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra1_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_extra1_date()) + "\n";
										}
									} catch (Exception ex) {

									}

									try {
										if (s.getInvoice_extra2_date() != null) {
											fatt += new SimpleDateFormat("dd/MM/yyyy")
													.format(s.getInvoice_extra2_date());
										}
									} catch (Exception ex) {

									}

									ph = selector.process(fatt);
									table.addCell(ph);

								} catch (Exception ex) {
									ph = selector.process("");
									table.addCell(ph);
								}

								document.add(table);
							}
						}

					}

					PdfPTable table_qta = new PdfPTable(new float[] { 90, 10 });
					table_qta.setWidthPercentage(100);
					table_qta.setSpacingAfter(15);

					ph = selector.process("TOTALE PESO (T)");
					table_qta.addCell(ph);

					ph = selector.process(NumberFormat.getNumberInstance().format(qta));
					table_qta.addCell(ph);

					document.add(table_qta);

					PdfPTable table_tot_billed = new PdfPTable(new float[] { 90, 10 });
					table_tot_billed.setWidthPercentage(100);
					table_tot_billed.setSpacingAfter(15);

					ph = selector.process("TOTALE FATTURATO");
					table_tot_billed.addCell(ph);

					ph = selector.process(NumberFormat.getCurrencyInstance().format(tot_billed));
					table_tot_billed.addCell(ph);

					document.add(table_tot_billed);

					PdfPTable table_tot_not_billed = new PdfPTable(new float[] { 90, 10 });
					table_tot_not_billed.setWidthPercentage(100);
					table_tot_not_billed.setSpacingAfter(15);

					ph = selector.process("TOTALE DA FATTURARE");
					table_tot_not_billed.addCell(ph);

					ph = selector.process(NumberFormat.getCurrencyInstance().format(tot_not_billed));
					table_tot_not_billed.addCell(ph);

					document.add(table_tot_not_billed);

					document.close();

					// ------------------------------ genero il pdf
					ContextSupport.getResponse().setContentType("application/pdf");
					ContextSupport.getResponse().addHeader("Content-Disposition",
							"attachment; filename=" + "Provvigioni.pdf");
					ContextSupport.getResponse().setContentLength(output.size());
					OutputStream os = ContextSupport.getResponse().getOutputStream();
					output.writeTo(os);
					os.flush();
					os.close();

					LogSupport.log(op() + "E' stato generato il pdf provvigioni.", Level.INFO);

				} catch (Exception ex) {
					if (ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
							Level.SEVERE);
				}

			}

		} catch (Exception ex) {
			if (ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));

			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex),
					Level.SEVERE);
		} finally {
			sx.close();
		}

		return null;
	}

}
