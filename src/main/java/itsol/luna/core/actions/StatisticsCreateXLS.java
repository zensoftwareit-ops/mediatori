package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class StatisticsCreateXLS extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2396107822072928400L;
	
	//------------------------------ variabile sales
		private List<Sale> sales = new ArrayList<Sale>(0);
		public List<Sale> getSales() {
			return sales;
		}
		public void setSales(List<Sale> sales) {
			this.sales = sales;
		}
		
		private String group;
		
		public String getGroup() {
			return group;
		}
		
		public void setGroup(String group) {
			this.group = group;
		}
		
		//------------------------------ variabile id_product
		private Long id_product;
		public Long getId_product() {
			return id_product;
		}
		public void setId_product(Long id_product) {
			this.id_product = id_product;
		}
		
		private Double total_qta = 0.00;
		private Double total_price = 0.00;
		private Double total_yield = 0.00;
		private Integer total_number = 0;
		private Double total_red = 0.00;
		private Double total_damaged = 0.00;
		
		private Double total_total_qta = 0.00;
		private Double total_total_price = 0.00;
		private Double total_total_yield = 0.00;
		private Double total_total_red = 0.00;
		private Double total_total_damaged = 0.00;
		
		String title = "";

		public class Stat {
			private Double total_qta = 0.00;
			private Double total_price = 0.00;
			private Double total_yield = 0.00;
			private Integer total_number = 0;
			private Double total_red = 0.00;
			private Double total_damaged = 0.00;
			private String product;
			
			public String getProduct() {
				return product;
			}
			public void setProduct(String product) {
				this.product = product;
			}
			public Double getTotal_qta() {
				return total_qta;
			}
			public void setTotal_qta(Double total_qta) {
				this.total_qta = total_qta;
			}
			public Double getTotal_price() {
				return total_price;
			}
			public void setTotal_price(Double total_price) {
				this.total_price = total_price;
			}
			public Double getTotal_yield() {
				return total_yield;
			}
			public void setTotal_yield(Double total_yield) {
				this.total_yield = total_yield;
			}
			public Integer getTotal_number() {
				return total_number;
			}
			public void setTotal_number(Integer total_number) {
				this.total_number = total_number;
			}
			public Double getTotal_red() {
				return total_red;
			}
			public void setTotal_red(Double total_red) {
				this.total_red = total_red;
			}
			public Double getTotal_damaged() {
				return total_damaged;
			}
			public void setTotal_damaged(Double total_damaged) {
				this.total_damaged = total_damaged;
			}
		}
		
		private List<Stat> stats = new ArrayList<Stat>(0);
		
		public String execute() {
			
			Session sx = HibernateUtil.getSessionFactory().openSession();
			title = "Elenco statistiche";
			
			try {

				if(SessionSupport.checkSession()) {
					
					if(SessionSupport.checkSessionLevel("admin|super")) {
													
					

					} else {
						
						
						this.sales = Sale.getStatistics(getId_product(), getId_customer(), getCustomer_type(), getGroup(), getDate_from(), getDate_to());
						
						if(getId_customer() != null && this.sales.size() > 0) {
							if(getCustomer_type().contentEquals("seller")) {
								title += " di " + this.sales.get(0).getSeller().getBusiness_name() + " (peso venduto)";
							} else if(getCustomer_type().contentEquals("buyer")) {
								title += " di " + this.sales.get(0).getBuyer().getBusiness_name() + " (peso acquistato)";
							}
						}
					}
					
					Workbook workbook = new XSSFWorkbook();
					Sheet sheet = workbook.createSheet(title);
					Row row = null;
					Cell cell = null;
					
					row = sheet.createRow(0);
					
					cell = row.createCell(0);
					cell.setCellValue(title);
					
					row = sheet.createRow(2);
					
					cell = row.createCell(0);
					cell.setCellValue("N. contratti");
					
					cell = row.createCell(1);
					cell.setCellValue("Prodotto");
					
					cell = row.createCell(2);
					cell.setCellValue("Tot. Quantità");
					
					cell = row.createCell(3);
					cell.setCellValue("Prezzo (m.p.)");
					
					cell = row.createCell(4);
					cell.setCellValue("Resa grani interi (m.p.)");
					
					cell = row.createCell(5);
					cell.setCellValue("Grani rossi (m.p.)");
					
					cell = row.createCell(6);
					cell.setCellValue("Grani danneggiati (m.p.)");
					
					cell = row.createCell(7);
					cell.setCellValue("% sul peso");
					
						Iterator<Product> ii = SessionSupport.getProducts().iterator();
						
						
						while(ii.hasNext()) {
							Product p = ii.next();
				

							total_number = 0;
							total_qta = 0.00;
							total_price = 0.00;
							total_yield = 0.00;
							total_red = 0.00;
							total_damaged = 0.00;
							
							
								
							Iterator<Sale> i = this.sales.iterator();
							while(i.hasNext()) {
								Sale s = i.next();
								SaleProduct sp = s.getProduct();
									
								if(sp.getProduct().getId().equals(p.getId())) {
										
									total_number++;
									total_qta = total_qta + sp.getProduct_amount();
									total_price = total_price + sp.getTotal();
										
									try {
										total_yield = total_yield + (sp.getGrains_yield() * sp.getProduct_amount());
									} catch(Exception ex) {}
										
									try {
										total_red = total_red + (sp.getD_red_grains() * sp.getProduct_amount());
									} catch(Exception ex) {}
										
									try {
										total_damaged = total_damaged + (sp.getD_damaged_grains() * sp.getProduct_amount());
									} catch(Exception ex) {}
										
									
								}
							}
							
							
								
								if(total_number > 0) {
									
									
									Stat s = new Stat();
									
									s.setTotal_number(total_number);
									s.setTotal_qta(total_qta);
									s.setTotal_price(total_price);
									s.setTotal_yield(total_yield);
									s.setTotal_red(total_red);
									s.setTotal_damaged(total_damaged);
									try {
										s.setProduct(p.getInternal_code() + " - " + p.getName());
									} catch(Exception ex) {
										s.setProduct(p.getName());
										
									}
									
									stats.add(s);
									
									total_total_qta = total_total_qta + total_qta;
									total_total_price = total_total_price + total_price;
									total_total_yield = total_total_yield + total_yield;
									total_total_red = total_total_red + total_red;
									total_total_damaged = total_total_damaged + total_damaged;
									

								}
							
							
							
							

						}
						
						Integer row_i = 3;
						Iterator<Stat> i = stats.iterator();
						while(i.hasNext()) {
							Stat s = i.next();
							
							try {
								row = sheet.createRow(row_i);
								
								cell = row.createCell(0);
								cell.setCellValue(s.getTotal_number());
							
								cell = row.createCell(1);
								cell.setCellValue(s.getProduct());
								
								cell = row.createCell(2);
								cell.setCellValue(s.getTotal_qta());
								
								cell = row.createCell(3);
								cell.setCellValue(s.getTotal_price() / s.getTotal_qta());
								
								cell = row.createCell(4);
								cell.setCellValue(s.getTotal_yield() / s.getTotal_qta());
								
								cell = row.createCell(5);
								cell.setCellValue(s.getTotal_red() / s.getTotal_qta());
								
								cell = row.createCell(6);
								cell.setCellValue(s.getTotal_damaged() / s.getTotal_qta());
								
								cell = row.createCell(7);
								cell.setCellValue((s.getTotal_qta() * 100) / total_total_qta);

								
								row_i++;
							} catch(Exception ex) {
								
							}
						}
						
						row = sheet.createRow(row_i);
						cell = row.createCell(2);
						cell.setCellValue(total_total_qta);
						
						cell = row.createCell(3);
						cell.setCellValue(total_total_price / total_total_qta);
						
						cell = row.createCell(4);
						cell.setCellValue(total_total_yield / total_total_qta);
						
						cell = row.createCell(5);
						cell.setCellValue(total_total_red / total_total_qta);
						
						cell = row.createCell(6);
						cell.setCellValue(total_total_damaged / total_total_qta);
						
						cell = row.createCell(7);
						cell.setCellValue(100);
						
						
						
						
						


					
					
					try {


						ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
				        workbook.write(outByteStream);
				        byte [] outArray = outByteStream.toByteArray();
				        ContextSupport.getResponse().setContentType("application/ms-excel");
				        ContextSupport.getResponse().setContentLength(outArray.length);
				        ContextSupport.getResponse().setHeader("Expires:", "0"); // eliminates browser caching
				        ContextSupport.getResponse().setHeader("Content-Disposition", "attachment; filename=Statistiche.xlsx");
				        OutputStream outStream = ContextSupport.getResponse().getOutputStream();
				        outStream.write(outArray);
				        outStream.flush();

				        workbook.close();
				        
				        LogSupport.log(op() + "E' stato generato il file xls della resa.", Level.INFO);


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
