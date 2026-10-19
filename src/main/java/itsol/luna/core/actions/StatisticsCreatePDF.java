package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
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

import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class StatisticsCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200989009528675860L;
	
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
					
					
					Iterator<Stat> i = stats.iterator();
					while(i.hasNext()) {
						Stat s = i.next();
						
						PdfPTable table = new PdfPTable(new float[] { 5, 10, 25, 25, 15, 15, 15, 15});
						table.setWidthPercentage(100);
						table.setSpacingAfter(15);
						
						ph = selector.process("N. contratti");
						table.addCell(ph);
						
						ph = selector.process("Prodotto");
						table.addCell(ph);
						
						ph = selector.process("Tot. Quantità");
						table.addCell(ph);
						
						ph = selector.process("Prezzo (m.p.)");
						table.addCell(ph);
						
						ph = selector.process("Resa grani interi (m.p.)");
						table.addCell(ph);
						
						ph = selector.process("Grani rossi (m.p.)");
						table.addCell(ph);
						
						ph = selector.process("Grani danneggiati (m.p.)");
						table.addCell(ph);
						
						ph = selector.process("% sul peso");
						table.addCell(ph);
						
						//
						
						ph = selector.process(s.getTotal_number().toString());
						table.addCell(ph);
						
						ph = selector.process(s.getProduct());
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getNumberInstance().format(s.getTotal_qta()));
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getCurrencyInstance().format(s.getTotal_price() / s.getTotal_qta()));
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getNumberInstance().format(s.getTotal_yield() / s.getTotal_qta()));
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getNumberInstance().format(s.getTotal_red() / s.getTotal_qta()));
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getNumberInstance().format(s.getTotal_damaged() / s.getTotal_qta()));
						table.addCell(ph);
						
						ph = selector.process(NumberFormat.getNumberInstance().format((s.getTotal_qta() * 100) / total_total_qta));
						table.addCell(ph);
						

						
						document.add(table);
					}
					
					
					PdfPTable table = new PdfPTable(new float[] { 5, 10, 25, 25, 15, 15, 15, 15});
					table.setWidthPercentage(100);
					table.setSpacingAfter(15);
					
					ph = selector.process("");
					table.addCell(ph);
					
					ph = selector.process("PESO TOTALE");
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(total_total_qta));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getCurrencyInstance().format(total_total_price / total_total_qta));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(total_total_yield / total_total_qta));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(total_total_red / total_total_qta));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(total_total_damaged / total_total_qta));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(100));
					table.addCell(ph);

					
					document.add(table);
					
					
					document.close();
					//------------------------------ genero il pdf
			        ContextSupport.getResponse().setContentType("application/pdf");
			        ContextSupport.getResponse().addHeader("Content-Disposition", "attachment; filename=" + "Statistiche.pdf");
			        ContextSupport.getResponse().setContentLength(output.size());
			        OutputStream os = ContextSupport.getResponse().getOutputStream();
			        output.writeTo(os);
			        os.flush();
			        os.close();
 
			        LogSupport.log(op() + "E' stato generato il pdf statistiche.", Level.INFO);

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
