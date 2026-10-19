package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.Session;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SampleMeasureCreatePDF extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6822870425333002006L;
	
	private Long id_customer;
	public Long getId_customer() {
		return id_customer;
	}
	public void setId_customer(Long id_customer) {
		this.id_customer = id_customer;
	}
	
	private Long id_product;
	public Long getId_product() {
		return id_product;
	}
	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}
	
	private boolean col_customer;
	public boolean isCol_customer() {
		return col_customer;
	}
	public void setCol_customer(boolean col_customer) {
		this.col_customer = col_customer;
	}

	private List<YieldMeasure> measures = new ArrayList<YieldMeasure>(0);
	public List<YieldMeasure> getMeasures() {
		return measures;
	}
	public void setMeasures(List<YieldMeasure> measures) {
		this.measures = measures;
	}
	
	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	private Double grains_average = 0.00;
	private Double global_average = 0.00;
	private Double defects_average_1 = 0.00;
	private Double defects_average_2 = 0.00;
	private Double thousand_grains_average = 0.00;

	public Double getThousand_grains_average() {
		return thousand_grains_average;
	}
	public void setThousand_grains_average(Double thousand_grains_average) {
		this.thousand_grains_average = thousand_grains_average;
	}
	public Double getDefects_average_1() {
		return defects_average_1;
	}
	public void setDefects_average_1(Double defects_average_1) {
		this.defects_average_1 = defects_average_1;
	}
	public Double getDefects_average_2() {
		return defects_average_2;
	}
	public void setDefects_average_2(Double defects_average_2) {
		this.defects_average_2 = defects_average_2;
	}
	public Double getGrains_average() {
		return grains_average;
	}
	public void setGrains_average(Double grains_average) {
		this.grains_average = grains_average;
	}
	public Double getGlobal_average() {
		return global_average;
	}
	public void setGlobal_average(Double global_average) {
		this.global_average = global_average;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public String execute() {
		
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			if(SessionSupport.checkSession()) {
				
				try {
					this.customer = (Customer)sx.createQuery("FROM Customer WHERE id = :id_customer").setParameter("id_customer", getId_customer()).uniqueResult();
					this.product = (Product)sx.createQuery("FROM Product WHERE id = :id_product").setParameter("id_product", getId_product()).uniqueResult();
				} catch(Exception ex) {
					if(ContextSupport.isDebug()) {
						ex.printStackTrace();
					}
					LogSupport.log(op() + "Si è verificato un errore durante la ricerca di cliente e/o prodotto.", Level.INFO);
					LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
				} 
		
				if(SessionSupport.checkSessionLevel("admin|super")) {
					//this.measures = (List<YieldMeasure>)sx.createQuery("FROM YieldMeasure WHERE id_customer = :id_customer AND id_product = :id_product ORDER BY id").setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND c.id = :id_customer AND p.id = :id_product ORDER BY c.internal_code, ym.id, p.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					//this.measures = (List<YieldMeasure>)sx.createQuery("FROM YieldMeasure WHERE id_admin = :id_admin AND (id_customer = :id_customer OR :id_customer IS NULL) AND (id_product = :id_product OR :id_product IS NULL) ORDER BY id").setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					
					String hql="";
					if(!isCol_customer()) {
						
						
						hql += "SELECT ym FROM YieldMeasure ym INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL)";
						
						if(getDate_from() != null) {
							hql += " AND DATE(ym.insert_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
						}
						
						if(getDate_to() != null) {
							hql += " AND DATE(ym.insert_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
						}
						
						hql += "ORDER BY c.internal_code, p.id, ym.id";
						
						this.measures = (List<YieldMeasure>)sx.createQuery(hql).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
						
						//this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL) ORDER BY c.internal_code, p.id, ym.id").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					} else {
						
						
						hql += "SELECT ym FROM YieldMeasure ym INNER JOIN ym.admin a INNER JOIN ym.product p WHERE a.id = :id_admin AND (p.id = :id_product OR :id_product IS NULL)";
						
						if(getDate_from() != null) {
							hql += " AND DATE(ym.insert_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
						}
						
						if(getDate_to() != null) {
							hql += " AND DATE(ym.insert_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
						}
						
						hql += "ORDER BY p.id, ym.id";
						
						this.measures = (List<YieldMeasure>)sx.createQuery(hql).setParameter("id_admin", id_admin).setParameter("id_product", getId_product()).list();
						//this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (p.id = :id_product OR :id_product IS NULL) ORDER BY p.name").setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_product", getId_product()).list();
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
			
					document.open();
					
					String title = "Resa";
					
					if(getProduct() != null) {
						title += " del prodotto " + getProduct().getName();
					}
					
					if(getCustomer() != null) {
						title += " di " + getCustomer().getBusiness_name();
					}
					
					Phrase ph = selector.process(title);
					document.add(ph);
					
					PdfPTable table = new PdfPTable(new float[] { 10, 25, 25, 15, 8, 8, 8, 8, 15, 8 });
					table.setWidthPercentage(100);
					
					ph = selector.process("Data");
					table.addCell(ph);
					
					if(!isCol_customer()) {
						ph = selector.process("Cliente");
						table.addCell(ph);
					} else {
						ph = selector.process(" ");
						table.addCell(ph);
					}

					
					ph = selector.process("Prodotto");
					table.addCell(ph);
					
					ph = selector.process("Note");
					table.addCell(ph);
					
					ph = selector.process("Resa grani");
					table.addCell(ph);
					
					ph = selector.process("Resa globale");
					table.addCell(ph);
					
					ph = selector.process("Difetti 1");
					table.addCell(ph);
					
					ph = selector.process("Difetti 2");
					table.addCell(ph);
					
					ph = selector.process("Note");
					table.addCell(ph);
					
					ph = selector.process("1000 grani");
					table.addCell(ph);
					
					document.add(table);

					Long ex_id = null;
					Integer k = 0;
					
					Iterator<YieldMeasure> i = measures.iterator();
					while(i.hasNext()) {
						YieldMeasure ym = i.next();
						
						if(ym.getProduct().getId() != ex_id) {
							
							if(ex_id != null) {
								
								this.grains_average = this.grains_average / (double)k;
								this.global_average = this.global_average / (double)k;
								this.defects_average_1 = this.defects_average_1 / (double)k;
								this.defects_average_2 = this.defects_average_2 / (double)k;
								this.thousand_grains_average = this.thousand_grains_average / (double)k;
								
								table = new PdfPTable(new float[] { 10, 25, 25, 15, 8, 8, 8, 8, 15, 8 });
								table.setWidthPercentage(100);
								
								ph = selector.process("MEDIE");
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								ph = selector.process(NumberFormat.getNumberInstance().format(this.grains_average));
								table.addCell(ph);
								
								ph = selector.process(NumberFormat.getNumberInstance().format(this.global_average));
								table.addCell(ph);
								 
								ph = selector.process(NumberFormat.getNumberInstance().format(this.defects_average_1));
								table.addCell(ph);
								
								ph = selector.process(NumberFormat.getNumberInstance().format(this.defects_average_2));
								table.addCell(ph);
								
								ph = selector.process("");
								table.addCell(ph);
								
								ph = selector.process(NumberFormat.getNumberInstance().format(this.thousand_grains_average));
								table.addCell(ph);
								
								document.add(table);
								
								table = new PdfPTable(1);
								table.setWidthPercentage(100);
								
								ph = selector.process(" ");
								table.addCell(ph);
								
								document.add(table);
								
							}
							
							k = 1;
							this.grains_average = ym.getGrains_yield();
							this.global_average = ym.getGlobal_yield();
							this.defects_average_1 = ym.getDefects_1();
							this.defects_average_2 = ym.getDefects_2();
							this.thousand_grains_average = ym.getThousand_grains();
							
							ex_id = ym.getProduct().getId();
							
						} else {
							
							k++;
							this.grains_average = this.grains_average + ym.getGrains_yield();
							this.global_average = this.global_average + ym.getGlobal_yield();
							this.defects_average_1 = this.defects_average_1 + ym.getDefects_1();
							this.defects_average_2 = this.defects_average_2 + ym.getDefects_2();
							this.thousand_grains_average = this.thousand_grains_average + ym.getThousand_grains();
						}
						

						
						table = new PdfPTable(new float[] { 10, 25, 25, 15, 8, 8, 8, 8, 15, 8 });
						table.setWidthPercentage(100);
						
						
						
						//
						
						ph = selector.process(new SimpleDateFormat("dd/MM/yyyy").format(ym.getInsert_date()));
						table.addCell(ph);
						
						if(!isCol_customer()) {
							ph = selector.process(ym.getCustomer().getBusiness_name());
							table.addCell(ph);
						} else {
							ph = selector.process("");
							table.addCell(ph);
						}

						
						ph = selector.process(ym.getProduct().getName());
						table.addCell(ph);
						
						ph = selector.process(ym.getNote_1());
						table.addCell(ph);
						
				        ph = selector.process(NumberFormat.getNumberInstance().format(ym.getGrains_yield()));
				        table.addCell(ph);
				        
				        ph = selector.process(NumberFormat.getNumberInstance().format(ym.getGlobal_yield()));
				        table.addCell(ph);
				        
				        ph = selector.process(NumberFormat.getNumberInstance().format(ym.getDefects_1()));
				        table.addCell(ph);
				        
				        ph = selector.process(NumberFormat.getNumberInstance().format(ym.getDefects_2()));
				        table.addCell(ph);
				        
						ph = selector.process(ym.getNote_2());
						table.addCell(ph);
						
				        ph = selector.process(NumberFormat.getNumberInstance().format(ym.getThousand_grains()));
				        table.addCell(ph);
						
						document.add(table);
					}
					
					this.grains_average = this.grains_average / (double)k;
					this.global_average = this.global_average / (double)k;
					this.defects_average_1 = this.defects_average_1 / (double)k;
					this.defects_average_2 = this.defects_average_2 / (double)k;
					this.thousand_grains_average = this.thousand_grains_average / (double)k;
					
					table = new PdfPTable(new float[] { 10, 25, 25, 15, 8, 8, 8, 8, 15, 8 });
					table.setWidthPercentage(100);
					
					ph = selector.process("MEDIE");
					table.addCell(ph);
					
					ph = selector.process("");
					table.addCell(ph);
					
					ph = selector.process("");
					table.addCell(ph);
					
					ph = selector.process("");
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(this.grains_average));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(this.global_average));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(this.defects_average_1));
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(this.defects_average_2));
					table.addCell(ph);
					
					ph = selector.process("");
					table.addCell(ph);
					
					ph = selector.process(NumberFormat.getNumberInstance().format(this.thousand_grains_average));
					table.addCell(ph);
					
					document.add(table);
					
					document.close();
					
					//------------------------------ genero il pdf
		            ContextSupport.getResponse().setContentType("application/pdf");
		            ContextSupport.getResponse().addHeader("Content-Disposition", "attachment; filename=" + "Resa.pdf");
		            ContextSupport.getResponse().setContentLength(output.size());
		            OutputStream os = ContextSupport.getResponse().getOutputStream();
		            output.writeTo(os);
		            os.flush();
		            os.close();

		            
		            LogSupport.log(op() + "E' stato generato il pdf della resa.", Level.INFO);

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
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return null;
	}

}
