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

import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.beans.YieldMeasure;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SampleMeasureCreateXLS extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9121578231345106684L;
	
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
	
	@SuppressWarnings("unchecked")
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
					this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.customer c INNER JOIN ym.product p WHERE c.id = :id_customer AND p.id = :id_product ORDER BY c.internal_code, ym.id, p.id").setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					//this.measures = (List<YieldMeasure>)sx.createQuery("FROM YieldMeasure WHERE id_admin = :id_admin AND (id_customer = :id_customer OR :id_customer IS NULL) AND (id_product = :id_product OR :id_product IS NULL) ORDER BY id").setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					//this.measures = (List<YieldMeasure>)sx.createQuery("SELECT ym FROM YieldMeasure ym INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL) ORDER BY c.internal_code, ym.id, p.id").setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
					
					String hql ="";
					hql += "SELECT ym FROM YieldMeasure ym INNER JOIN ym.year y INNER JOIN ym.admin a INNER JOIN ym.customer c INNER JOIN ym.product p WHERE y.id = :id_year AND a.id = :id_admin AND (c.id = :id_customer OR :id_customer IS NULL) AND (p.id = :id_product OR :id_product IS NULL)";
					
					if(getDate_from() != null) {
						hql += " AND DATE(ym.insert_date) >= '" + DateFormatUtils.format(getDate_from(), "yyyy-MM-dd") + "'";
					}
					
					if(getDate_to() != null) {
						hql += " AND DATE(ym.insert_date) <= '" + DateFormatUtils.format(getDate_to(), "yyyy-MM-dd") + "'";
					}
					
					if(!isCol_customer()) {
						hql += "ORDER BY c.internal_code, p.id, ym.id";
					} else {
						hql += "ORDER BY p.id, ym.id";
					}
					
					this.measures = (List<YieldMeasure>)sx.createQuery(hql).setParameter("id_year", SessionSupport.getSowingYear().getId()).setParameter("id_admin", id_admin).setParameter("id_customer", getId_customer()).setParameter("id_product", getId_product()).list();
				}
				
				try {
					String title = "Resa";
					
					if(getProduct() != null) {
						title += " del prodotto " + getProduct().getName();
					}
					
					if(getCustomer() != null) {
						title += " di " + getCustomer().getBusiness_name();
					}
					
					Workbook workbook = new XSSFWorkbook();
					Sheet sheet = workbook.createSheet(title);
					Row row = null;
					Cell cell = null;

					row = sheet.createRow(0);
					
					cell = row.createCell(0);
					cell.setCellValue("Data");
					
					if(!isCol_customer()) {
						cell = row.createCell(1);
						cell.setCellValue("Cliente");
					} else {
						cell = row.createCell(1);
						cell.setCellValue("");
					}

					cell = row.createCell(2);
					cell.setCellValue("Prodotto");

					cell = row.createCell(3);
					cell.setCellValue("Note");

					cell = row.createCell(4);
					cell.setCellValue("Resa grani");

					cell = row.createCell(5);
					cell.setCellValue("Resa globale");

					cell = row.createCell(6);
					cell.setCellValue("Difetti 1");

					cell = row.createCell(7);
					cell.setCellValue("Difetti 2");

					cell = row.createCell(8);
					cell.setCellValue("Note");

					cell = row.createCell(9);
					cell.setCellValue("1000 grani");
					
					Long ex_id = null;
					Integer k = 0;
					Integer row_i = 1;
					
					Iterator<YieldMeasure> i = measures.iterator();
					while(i.hasNext()) {
						YieldMeasure ym = i.next();
						
						if(ym.getProduct().getId() != ex_id) {
							if(ex_id != null) {
								this.grains_average = this.grains_average / (double)k;
								this.global_average = this.global_average / (double)k;
								this.defects_average_1 = this.defects_average_1 / (double)k;
								this.defects_average_2 = this.defects_average_2 / (double)k;
								
								row = sheet.createRow(row_i);
								cell = row.createCell(0);
								cell.setCellValue("MEDIE");

								cell = row.createCell(4);
								cell.setCellValue((double)this.grains_average);

								cell = row.createCell(5);
								cell.setCellValue((double)this.global_average);

								cell = row.createCell(6);
								cell.setCellValue((double)this.defects_average_1);

								cell = row.createCell(7);
								cell.setCellValue((double)this.defects_average_2);
								
								row_i++;

							}
							
							k = 1;
							this.grains_average = ym.getGrains_yield();
							this.global_average = ym.getGlobal_yield();
							this.defects_average_1 = ym.getDefects_1();
							this.defects_average_2 = ym.getDefects_2();
							
							ex_id = ym.getProduct().getId();
							
						} else {
							k++;
							this.grains_average = this.grains_average + ym.getGrains_yield();
							this.global_average = this.global_average + ym.getGlobal_yield();
							this.defects_average_1 = this.defects_average_1 + ym.getDefects_1();
							this.defects_average_2 = this.defects_average_2 + ym.getDefects_2();
						}
						
						row = sheet.createRow(row_i);
						cell = row.createCell(0);
						cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(ym.getInsert_date()));
						
						if(!isCol_customer()) {
							cell = row.createCell(1);
							cell.setCellValue(ym.getCustomer().getBusiness_name());
						} else {
							cell = row.createCell(1);
							cell.setCellValue("");
						}

						cell = row.createCell(2);
						cell.setCellValue(ym.getProduct().getName());

						cell = row.createCell(3);
						cell.setCellValue(ym.getNote_1());

						cell = row.createCell(4);
						cell.setCellValue((double)ym.getGrains_yield());

						cell = row.createCell(5);
						cell.setCellValue((double)ym.getGlobal_yield());

						cell = row.createCell(6);
						cell.setCellValue((double)ym.getDefects_1());

						cell = row.createCell(7);
						cell.setCellValue((double)ym.getDefects_2());

						cell = row.createCell(8);
						cell.setCellValue(ym.getNote_2());

						cell = row.createCell(9);
						cell.setCellValue((double)ym.getThousand_grains());
						
						row_i++;
						
					}

					this.grains_average = this.grains_average / (double)k;
					this.global_average = this.global_average / (double)k;
					this.defects_average_1 = this.defects_average_1 / (double)k;
					this.defects_average_2 = this.defects_average_2 / (double)k;
					
					row = sheet.createRow(row_i);
					cell = row.createCell(0);
					cell.setCellValue("MEDIE");

					cell = row.createCell(4);
					cell.setCellValue((double)this.grains_average);

					cell = row.createCell(5);
					cell.setCellValue((double)this.global_average);

					cell = row.createCell(6);
					cell.setCellValue((double)this.defects_average_1);

					cell = row.createCell(7);
					cell.setCellValue((double)this.defects_average_2);
					
					for(int ii = 0; ii < 10; ii++) {
						sheet.autoSizeColumn(ii);
					}
					
					ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			        workbook.write(outByteStream);
			        byte [] outArray = outByteStream.toByteArray();
			        ContextSupport.getResponse().setContentType("application/ms-excel");
			        ContextSupport.getResponse().setContentLength(outArray.length);
			        ContextSupport.getResponse().setHeader("Expires:", "0"); // eliminates browser caching
			        ContextSupport.getResponse().setHeader("Content-Disposition", "attachment; filename=Resa.xlsx");
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
			}
			LogSupport.log(op() + "Si è verificato un errore durante il caricamento della pagina.", Level.INFO);
			LogSupport.log(op() + ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
		} finally {
			sx.close();
		}
		
		return null;
	}

}
