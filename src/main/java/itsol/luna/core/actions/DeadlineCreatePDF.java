package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;

import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SalePaymentInstallment;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class DeadlineCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4137806011000674469L;
	
	//------------------------------ variabile sales
		private List<Sale> sales = new ArrayList<Sale>(0);
		public List<Sale> getSales() {
			return sales;
		}
		public void setSales(List<Sale> sales) {
			this.sales = sales;
		}
		
	
		

		
		private Double tot_payed = 0.00;
		private Double tot_not_payed = 0.00;
		
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
		
		@SuppressWarnings({ "unchecked"})
		public String execute() {

			Session sx = HibernateUtil.getSessionFactory().openSession();
			title = "Elenco scadenze pagamenti";
			
			
			try {
				
				String q = "";
				
				if(SessionSupport.checkSession()) {
					
					if(SessionSupport.checkSessionLevel("admin|super")) {
						
						switch(getCustomer_type()) {
							case "seller":
								
								q = "SELECT DISTINCT s FROM Sale s";
								
								if(getId_customer() != null) {
									q += " INNER JOIN s.seller b";
								} 
								
								if(getDate_from() != null || getDate_to() != null) {
									q += " INNER JOIN s.installments inst";
									
								}
								
								q += " WHERE";
								
								if(getId_customer() != null) {
									q += " AND s.seller = b AND b.id = " + getId_customer();
								}
								
								if(getDate_from() != null) {
									q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
									title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
								}
								
								if(getDate_to() != null) {
									q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
									title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
								}
								
								q += " ORDER BY s.number DESC";
								
								break;
							case "buyer":
								
								q = "SELECT DISTINCT s FROM Sale s";
								
								if(getId_customer() != null) {
									q += " INNER JOIN s.buyer b";
								} 
								
								if(getDate_from() != null || getDate_to() != null) {
									q += " INNER JOIN s.installments inst";
								}
								
								q += " WHERE";
								
								if(getId_customer() != null) {
									q += " AND s.buyer = b AND b.id = " + getId_customer();
								}
								
								if(getDate_from() != null) {
									q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
									title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
								}
								
								if(getDate_to() != null) {
									q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
									title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
								}
								
								q += " ORDER BY s.number DESC";
								
								break;
						}
						
						this.sales = (List<Sale>) sx.createQuery(q).list();
						

					} else {
						Long id_admin = (Long)SessionSupport.getAdmin().getId();
						
						switch(getCustomer_type()) {
						case "seller":
							
							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a";
							
							if(getId_customer() != null) {
								q += " INNER JOIN s.seller b";
							} 
							
							if(getDate_from() != null || getDate_to() != null) {
								q += " INNER JOIN s.installments inst";
							}
							
							q += " WHERE a.id = :id_admin";
							
							if(getId_customer() != null) {
								q += " AND s.seller = b AND b.id = " + getId_customer();
							}
							
							if(getDate_from() != null) {
								q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}
							
							if(getDate_to() != null) {
								q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}
							
							q += " ORDER BY s.number DESC";
							
							break;
						case "buyer":
							
							q = "SELECT DISTINCT s FROM Sale s INNER JOIN s.admin a";
							
							if(getId_customer() != null) {
								q += " INNER JOIN s.buyer b";
							} 
							
							if(getDate_from() != null || getDate_to() != null) {
								q += " INNER JOIN s.installments inst";
							}
							
							q += " WHERE a.id = :id_admin";
							
							if(getId_customer() != null) {
								q += " AND s.buyer = b AND b.id = " + getId_customer();
							}
							
							if(getDate_from() != null) {
								q += " AND DATE(inst.expire_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
								title += " dal " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_from());
							}
							
							if(getDate_to() != null) {
								q += " AND DATE(inst.expire_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
								title += " al " + new SimpleDateFormat("dd/MM/yyyy").format(getDate_to());
							}
							
							q += " ORDER BY s.number DESC";
							
							
							
							break;
						}
						
						this.sales = (List<Sale>) sx.createQuery(q).setParameter("id_admin", id_admin).list();

						

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
						//document.add(ph);
						
						Iterator<Sale> i = this.sales.iterator();
						
						while(i.hasNext()) {

							Sale s = i.next();
							

								SaleProduct sp = s.getProduct();
								
								PdfPTable table = new PdfPTable(new float[] { 5, 10, 25, 25, 15, 8, 8, 10, 25, 25 });
								table.setWidthPercentage(100);
								table.setSpacingAfter(15);
								
								ph = selector.process("N");
								table.addCell(ph);
								
								ph = selector.process("Data vendita");
								table.addCell(ph);
								
								ph = selector.process("Venditore");
								table.addCell(ph);
								
								ph = selector.process("Acquirente");
								table.addCell(ph);
								
								ph = selector.process("Prodotto");
								table.addCell(ph);
								
								ph = selector.process("Quantità");
								table.addCell(ph);
								
								ph = selector.process("Importo");
								table.addCell(ph);
								
								ph = selector.process("Totale");
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								//
								
								ph = selector.process(s.getNumber().toString());
						        table.addCell(ph);
						        
						        ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(s.getSale_date()));
						        table.addCell(ph);
						        
						        ph = selector.process(s.getSeller().getBusiness_name());
						        table.addCell(ph);
						        
						        ph = selector.process(s.getBuyer().getBusiness_name());
						        table.addCell(ph);
						        
						        ph = selector.process(sp.getProduct().getName());
						        table.addCell(ph);
						        
						        ph = selector.process(NumberFormat.getNumberInstance().format(sp.getProduct_amount()));
						        table.addCell(ph);
						        
						        ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getAmount()));
						        table.addCell(ph);
						        
						        ph = selector.process(NumberFormat.getCurrencyInstance().format(sp.getTotal()));
						        table.addCell(ph);

						        PdfPTable exp_table = new PdfPTable(new float[] { 100 });
						        exp_table.setWidthPercentage(100);
						        
						        ph = selector.process("Fattura");
						        exp_table.addCell(ph);
						        
						        try {
						        	
						        	try {
						        		if(s.getInvoice_deposit_desc() != null) {
						        			if(!s.getInvoice_deposit_desc().isEmpty()) {
						        				String fatt = s.getInvoice_deposit_desc();
						        				
									        	try {
									        		fatt += " del " + new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_deposit_date());
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	try {
									        		if(s.getInvoice_deposit_amount() != null) {
									        			if(s.getInvoice_deposit_amount() > 0) {
									        				fatt += " di " + NumberFormat.getCurrencyInstance().format(s.getInvoice_deposit_amount());
									        			}
									        		}
									        		
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	ph = selector.process(fatt);
												exp_table.addCell(ph);
						        			}
						        		}

						        	} catch(Exception ex) {
						        		ex.printStackTrace();
						        	}
						        	
						        	try {
						        		if(s.getInvoice_balance_desc() != null) {
						        			if(!s.getInvoice_balance_desc().isEmpty()) {
						        				String fatt = s.getInvoice_balance_desc();
						        				
									        	try {
									        		fatt += " del " + new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_balance_date());
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	try {
									        		if(s.getInvoice_balance_amount() != null) {
									        			if(s.getInvoice_balance_amount() > 0) {
									        				fatt += " di " + NumberFormat.getCurrencyInstance().format(s.getInvoice_balance_amount());
									        			}
									        		}
									        		
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	ph = selector.process(fatt);
												exp_table.addCell(ph);
						        			}
						        		}

						        	} catch(Exception ex) {
						        		ex.printStackTrace();
						        	}
						        	
						        	try {
						        		if(s.getInvoice_extra1_desc() != null) {
						        			if(!s.getInvoice_extra1_desc().isEmpty()) {
						        				String fatt = s.getInvoice_extra1_desc();
						        				
									        	try {
									        		fatt += " del " + new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_extra1_date());
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	try {
									        		if(s.getInvoice_extra1_amount() != null) {
									        			if(s.getInvoice_extra1_amount() > 0) {
									        				fatt += " di " + NumberFormat.getCurrencyInstance().format(s.getInvoice_extra1_amount());
									        			}
									        		}
									        		
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	ph = selector.process(fatt);
												exp_table.addCell(ph);
						        			}
						        		}

						        	} catch(Exception ex) {
						        		ex.printStackTrace();
						        	}
						        	
						        	try {
						        		if(s.getInvoice_extra2_desc() != null) {
						        			if(!s.getInvoice_extra2_desc().isEmpty()) {
						        				String fatt = s.getInvoice_extra2_desc();
						        				
									        	try {
									        		fatt += " del " + new SimpleDateFormat("dd/MM/yyyy").format(s.getInvoice_extra2_date());
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	try {
									        		if(s.getInvoice_extra2_amount() != null) {
									        			if(s.getInvoice_extra2_amount() > 0) {
									        				fatt += " di " + NumberFormat.getCurrencyInstance().format(s.getInvoice_extra2_amount());
									        			}
									        		}
									        		
									        	} catch(Exception ex) {
									        		ex.printStackTrace();
									        	}
									        	
									        	ph = selector.process(fatt);
												exp_table.addCell(ph);
						        			}
						        		}

						        	} catch(Exception ex) {
						        		ex.printStackTrace();
						        	}
						        	
						        	
									


						        } catch(Exception ex) {
						        	ex.printStackTrace();
						        } finally {

						        	PdfPCell exp_cell = new PdfPCell();
						        	exp_cell.setPadding(0);
						        	exp_cell.addElement(exp_table);
							        table.addCell(exp_cell);
						        }


						        exp_table = new PdfPTable(new float[] { 33,33,33 });
						        exp_table.setWidthPercentage(100);
						        
						        ph = selector.process("Scadenza");
						        exp_table.addCell(ph);
						        
						        ph = selector.process("Importo");
						        exp_table.addCell(ph);
						        
						        ph = selector.process("Pagata");
						        exp_table.addCell(ph);

						        
						        try {
						        	Iterator<SalePaymentInstallment> iii = s.getInstallments().iterator();
						        	
							        while(iii.hasNext()) {
							        	SalePaymentInstallment spi = iii.next();
							        	if(getDate_from() != null && getDate_to() != null) {
							        		if(getDate_from().compareTo(spi.getExpire_date()) <= 0 && getDate_to().compareTo(spi.getExpire_date()) >= 0) {
							        			
										        ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(spi.getExpire_date()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(NumberFormat.getCurrencyInstance().format(spi.getAmount()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(spi.getPayed());
										        exp_table.addCell(ph);
										        
										        if(spi.getPayed().contentEquals("SI")) {
										        	tot_payed = tot_payed + spi.getAmount();
										        } else if(spi.getPayed().contentEquals("NO")) {
										        	tot_not_payed = tot_not_payed + spi.getAmount();
										        }
							        		}
							        	} else if(getDate_from() != null) {
							        		if(getDate_from().compareTo(spi.getExpire_date()) <= 0) {
							        			
										        ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(spi.getExpire_date()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(NumberFormat.getCurrencyInstance().format(spi.getAmount()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(spi.getPayed());
										        exp_table.addCell(ph);
										        
										        if(spi.getPayed().contentEquals("SI")) {
										        	tot_payed = tot_payed + spi.getAmount();
										        } else if(spi.getPayed().contentEquals("NO")) {
										        	tot_not_payed = tot_not_payed + spi.getAmount();
										        }
							        		}
							        	} else if(getDate_to() != null) {
							        		if(getDate_to().compareTo(spi.getExpire_date()) >= 0) {
							        			
										        ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(spi.getExpire_date()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(NumberFormat.getCurrencyInstance().format(spi.getAmount()));
										        exp_table.addCell(ph);
										        
										        ph = selector.process(spi.getPayed());
										        exp_table.addCell(ph);
										        
										        if(spi.getPayed().contentEquals("SI")) {
										        	tot_payed = tot_payed + spi.getAmount();
										        } else if(spi.getPayed().contentEquals("NO")) {
										        	tot_not_payed = tot_not_payed + spi.getAmount();
										        }
							        		}
							        	} else {
									        ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(spi.getExpire_date()));
									        exp_table.addCell(ph);
									        
									        ph = selector.process(NumberFormat.getCurrencyInstance().format(spi.getAmount()));
									        exp_table.addCell(ph);
									        
									        ph = selector.process(spi.getPayed());
									        exp_table.addCell(ph);
									        
									        if(spi.getPayed().contentEquals("SI")) {
									        	tot_payed = tot_payed + spi.getAmount();
									        } else if(spi.getPayed().contentEquals("NO")) {
									        	tot_not_payed = tot_not_payed + spi.getAmount();
									        }
							        	}
							        	
							        	
							        }
						        } catch(Exception ex) {
						        	ex.printStackTrace();
						        } finally {
						        	PdfPCell exp_cell = new PdfPCell();
						        	exp_cell.setPadding(0);
						        	exp_cell.addElement(exp_table);
							        table.addCell(exp_cell);
						        }
						        
						        
								
						        document.add(table);

							

						}
						
						PdfPTable table_tot_payed = new PdfPTable(new float[] { 90, 10 });
						table_tot_payed.setWidthPercentage(100);
						table_tot_payed.setSpacingAfter(15);
						
						ph = selector.process("TOTALE PAGATO");
						table_tot_payed.addCell(ph);
						
						ph = selector.process(NumberFormat.getCurrencyInstance().format(tot_payed));
						table_tot_payed.addCell(ph);
						
						document.add(table_tot_payed);
						
						PdfPTable table_tot_not_payed = new PdfPTable(new float[] { 90, 10 });
						table_tot_not_payed.setWidthPercentage(100);
						table_tot_not_payed.setSpacingAfter(15);
						
						ph = selector.process("TOTALE NON PAGATO");
						table_tot_not_payed.addCell(ph);
						
						ph = selector.process(NumberFormat.getCurrencyInstance().format(tot_not_payed));
						table_tot_not_payed.addCell(ph);
						
						document.add(table_tot_not_payed);

						
						document.close();
						
						//------------------------------ genero il pdf
			            ContextSupport.getResponse().setContentType("application/pdf");
			            ContextSupport.getResponse().addHeader("Content-Disposition", "attachment; filename=" + "Scadenze.pdf");
			            ContextSupport.getResponse().setContentLength(output.size());
			            OutputStream os = ContextSupport.getResponse().getOutputStream();
			            output.writeTo(os);
			            os.flush();
			            os.close();

			            
			            LogSupport.log(op() + "E' stato generato il pdf scandenze.", Level.INFO);
						
					} catch(Exception ex) {
						if(ContextSupport.isDebug()) {
							ex.printStackTrace();
						}
						LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
						LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
					}
				
					
				} 
				
			} catch(Exception ex) {
				if(ContextSupport.isDebug()) {
					ex.printStackTrace();
					setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
				}
				LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
				LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			} finally {
				sx.close();
			}
			
			return null;
		}
		


}
