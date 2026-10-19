package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleCommission;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class CommissionCreateXLS extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9068964115271765302L;
	
	// ------------------------------ variabile sales
		private List<Sale> sales = new ArrayList<Sale>(0);

		public List<Sale> getSales() {
			return sales;
		}

		public void setSales(List<Sale> sales) {
			this.sales = sales;
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
		
		String title = "";
		
		@SuppressWarnings("unchecked")
		public String execute() {
			
			Session sx = HibernateUtil.getSessionFactory().openSession();
			title = "Elenco provvigioni";
			
			try {

				String q = "";

				if (SessionSupport.checkSession()) {

					if (SessionSupport.checkSessionLevel("admin|super")) {

						switch (getCustomer_type()) {
						case "seller":

							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.commissions sc";

							if (getId_customer() != null) {
								q += " INNER JOIN s.seller c";
							}

							switch(getBilled_status()) {
							case "not_billed":
								q += " WHERE sc.billed_seller = 'NO'";
								break;
							case "billed":
								q += " WHERE sc.billed_seller = 'SI'";
								break;
							case "all":
								q += " WHERE 1=1";
								break;
							}

							if (getId_customer() != null) {
								q += " AND s.seller = c AND c.id = " + getId_customer();
							}

							if (getDate_from() != null) {
								q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd")
										+ "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}

							if (getDate_to() != null) {
								q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd")
										+ "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}

							q += " ORDER BY s.sale_date DESC, s.number DESC";

							break;
						case "buyer":

							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.commissions sc";

							if (getId_customer() != null) {
								q += " INNER JOIN s.buyer c";
							}

							switch(getBilled_status()) {
							case "not_billed":
								q += " WHERE sc.billed_buyer = 'NO'";
								break;
							case "billed":
								q += " WHERE sc.billed_buyer = 'SI'";
								break;
							case "all":
								q += " WHERE 1=1";
								break;
							}

							if (getId_customer() != null) {
								q += " AND s.buyer = c AND c.id = " + getId_customer();
							}

							if (getDate_from() != null) {
								q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd")
										+ "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}

							if (getDate_to() != null) {
								q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd")
										+ "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}

							q += " ORDER BY s.sale_date DESC, s.number DESC";

							break;
						}

						this.sales = (List<Sale>) sx.createQuery(q).list();

					} else {
						Long id_admin = (Long) SessionSupport.getAdmin().getId();

						switch (getCustomer_type()) {
						case "seller":

							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a INNER JOIN s.commissions sc";

							if (getId_customer() != null) {
								q += " INNER JOIN s.seller c";
							}

							q += " WHERE a.id = :id_admin";
							
							switch(getBilled_status()) {
							case "not_billed":
								q += " AND sc.billed_seller = 'NO'";
								break;
							case "billed":
								q += " AND sc.billed_seller = 'SI'";
								break;
							case "all":
								q += " AND 1=1";
								break;
							}

							if (getId_customer() != null) {
								q += " AND s.seller = c AND c.id = " + getId_customer();
							}

							if (getDate_from() != null) {
								q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd")
										+ "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}

							if (getDate_to() != null) {
								q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd")
										+ "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}

							q += " ORDER BY s.sale_date DESC, s.number DESC";

							break;
						case "buyer":

							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a INNER JOIN s.commissions sc";

							if (getId_customer() != null) {
								q += " INNER JOIN s.buyer c";
							}

							q += " WHERE a.id = :id_admin";
							
							switch(getBilled_status()) {
							case "not_billed":
								q += " AND sc.billed_buyer = 'NO'";
								break;
							case "billed":
								q += " AND sc.billed_buyer = 'SI'";
								break;
							case "all":
								q += " AND 1=1";
								break;
							}

							if (getId_customer() != null) {
								q += " AND s.buyer = c AND c.id = " + getId_customer();
							}

							if (getDate_from() != null) {
								q += " AND DATE(s.sale_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd")
										+ "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}

							if (getDate_to() != null) {
								q += " AND DATE(s.sale_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd")
										+ "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}

							q += " ORDER BY s.sale_date DESC, s.number DESC";

							break;
						}

						this.sales = (List<Sale>) sx.createQuery(q).setParameter("id_admin", id_admin).list();

					}

					try {
						Workbook workbook = new XSSFWorkbook();
						Sheet sheet = workbook.createSheet(title);
						Row row = null;
						Cell cell = null;
						
						row = sheet.createRow(0);
						
						cell = row.createCell(0);
						cell.setCellValue(title);
						
						row = sheet.createRow(2);
						
						cell = row.createCell(0);
						cell.setCellValue("N");
						
						cell = row.createCell(1);
						cell.setCellValue("Data vendita");
						
						if (getCustomer_type().equals("buyer")) {
							cell = row.createCell(2);
							cell.setCellValue("Acquirente/venditore");
						} else if (getCustomer_type().equals("seller")) {
							cell = row.createCell(2);
							cell.setCellValue("Venditore/acquirente");
						}
						
						cell = row.createCell(3);
						cell.setCellValue("Prodotto");
						
						cell = row.createCell(4);
						cell.setCellValue("Quantità");
						
						cell = row.createCell(5);
						cell.setCellValue("Importo");
						
						cell = row.createCell(6);
						cell.setCellValue("Totale");
						
						cell = row.createCell(7);
						cell.setCellValue("Provv.");
						
						cell = row.createCell(8);
						cell.setCellValue("Provv. Tot.");
						
						cell = row.createCell(9);
						cell.setCellValue("N. Fatt.");
						
						cell = row.createCell(10);
						cell.setCellValue("Data");
						
						Integer row_i = 3;

						Iterator<Sale> i = this.sales.iterator();

						while (i.hasNext()) {

							Sale s = i.next();

							SaleProduct sp = s.getProduct();
							
							//
							
							row = sheet.createRow(row_i);

							cell = row.createCell(0);
							cell.setCellValue(s.getNumber().toString());
							
							cell = row.createCell(1);
							cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(s.getSale_date()));


							if (getCustomer_type().equals("buyer")) {
								cell = row.createCell(2);
								cell.setCellValue(s.getBuyer().getBusiness_name() + "\n" + s.getSeller().getBusiness_name());
							} else if (getCustomer_type().equals("seller")) {
								cell = row.createCell(2);
								cell.setCellValue(s.getSeller().getBusiness_name() + "\n" + s.getBuyer().getBusiness_name());
							}

							cell = row.createCell(3);
							cell.setCellValue(sp.getProduct().getName());

							qta = qta + sp.getProduct_amount();
							cell = row.createCell(4);
							cell.setCellValue(sp.getProduct_amount());

							cell = row.createCell(5);
							cell.setCellValue(sp.getAmount());

							cell = row.createCell(6);
							cell.setCellValue(sp.getTotal());


							if (getCustomer_type().equals("buyer")) {
								
								Double buyer_commission = 0.00;
								Double buyer_commission_amount = 0.00;
								
								
								Iterator<SaleCommission> ii = s.getCommissions().iterator();
								
								while(ii.hasNext()) {
									SaleCommission sc = ii.next();
									
									buyer_commission = buyer_commission + sc.getBuyer_commission();
									buyer_commission_amount = buyer_commission_amount + sc.getBuyer_commission_amount();
									
									if (sc.getBilled_buyer().contentEquals("SI")) {
										tot_billed = tot_billed + sc.getBuyer_commission_amount();
									} else if (sc.getBilled_buyer().contentEquals("NO")) {
										tot_not_billed = tot_not_billed + sc.getBuyer_commission_amount();
									}

								}
								
								cell = row.createCell(7);
								cell.setCellValue(buyer_commission);

								cell = row.createCell(8);
								cell.setCellValue(buyer_commission_amount);
								


							} else if (getCustomer_type().equals("seller")) {
								
								Double seller_commission = 0.00;
								Double seller_commission_amount = 0.00;
								
								
								Iterator<SaleCommission> ii = s.getCommissions().iterator();
								
								while(ii.hasNext()) {
									SaleCommission sc = ii.next();
									
									seller_commission = seller_commission + sc.getSeller_commission();
									seller_commission_amount = seller_commission_amount + sc.getSeller_commission_amount();
									
									if (sc.getBilled_seller().contentEquals("SI")) {
										tot_billed = tot_billed + sc.getSeller_commission_amount();
									} else if (sc.getBilled_seller().contentEquals("NO")) {
										tot_not_billed = tot_not_billed + sc.getSeller_commission_amount();
									}

								}
								
								cell = row.createCell(7);
								cell.setCellValue(seller_commission);

								cell = row.createCell(8);
								cell.setCellValue(seller_commission_amount);


							}
							
							try {
								
								String fatt = "";
								try {
									if(s.getInvoice_deposit_desc() != null) {
										if(!s.getInvoice_deposit_desc().isEmpty()) {
											fatt = s.getInvoice_deposit_desc() + " (acconto)\n";
										}
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_balance_desc() != null) {
										if(!s.getInvoice_balance_desc().isEmpty()) {
											fatt += s.getInvoice_balance_desc() + "\n";
										}
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_extra1_desc() != null) {
										if(!s.getInvoice_extra1_desc().isEmpty()) {
											fatt += s.getInvoice_extra1_desc() + "\n";
										}
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_extra2_desc() != null) {
										if(!s.getInvoice_extra2_desc().isEmpty()) {
											fatt += s.getInvoice_extra2_desc();
										}
									}
								} catch(Exception ex) {
									
								}

								cell = row.createCell(9);
								cell.setCellValue(fatt);
							} catch(Exception ex) {

							}
							
							
							
							try {
								
								String fatt = "";
								try {
									if(s.getInvoice_deposit_date() != null) {
										fatt = new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_deposit_date()) + "\n";
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_balance_date() != null) {
										fatt += new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_balance_date()) + "\n";
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_extra1_date() != null) {
										fatt += new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_extra1_date()) + "\n";
									}
								} catch(Exception ex) {
									
								}
								
								try {
									if(s.getInvoice_extra2_date() != null) {
										fatt += new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_extra2_date());
									}
								} catch(Exception ex) {
									
								}
								
								cell = row.createCell(10);
								cell.setCellValue(fatt);

							} catch(Exception ex) {

							}
							

							row_i++;

						}

						row = sheet.createRow(row_i);
						
						cell = row.createCell(4);
						cell.setCellValue(qta);
						
						row_i = row_i + 2;
						
						row = sheet.createRow(row_i);
				
						
						cell = row.createCell(0);
						cell.setCellValue("TOTALE PESO (T)");
						
						cell = row.createCell(1);
						cell.setCellValue(qta);
						
						row_i = row_i + 1;
						
						row = sheet.createRow(row_i);
						
						cell = row.createCell(0);
						cell.setCellValue("TOTALE FATTURATO");
						
						cell = row.createCell(1);
						cell.setCellValue(tot_billed);
						
						row_i = row_i + 1;
						
						row = sheet.createRow(row_i);
						
						cell = row.createCell(0);
						cell.setCellValue("TOTALE DA FATTURARE");
						
						cell = row.createCell(1);
						cell.setCellValue(tot_not_billed);
						
						
						
						ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
				        workbook.write(outByteStream);
				        byte [] outArray = outByteStream.toByteArray();
				        ContextSupport.getResponse().setContentType("application/ms-excel");
				        ContextSupport.getResponse().setContentLength(outArray.length);
				        ContextSupport.getResponse().setHeader("Expires:", "0"); // eliminates browser caching
				        ContextSupport.getResponse().setHeader("Content-Disposition", "attachment; filename=Commissioni.xlsx");
				        OutputStream outStream = ContextSupport.getResponse().getOutputStream();
				        outStream.write(outArray);
				        outStream.flush();

				        workbook.close();
				        
				        LogSupport.log(op() + "E' stato generato il file xls delle commissioni.", Level.INFO);

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
