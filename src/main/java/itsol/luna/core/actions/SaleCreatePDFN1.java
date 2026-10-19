package itsol.luna.core.actions;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.beans.SaleProduct;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleCreatePDFN1 extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2703559339272209449L;
	
	Font regular = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
	Font regular_bold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
	Font small = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL);
	Font small_bold = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD);
	Font console = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
	
	Document document;
	ByteArrayOutputStream output;
	PdfWriter writer;
	
	String title = "";
	
	private Long id_sale;
	private Sale sale;
	
	public Long getId_sale() {
		return id_sale;
	}
	public void setId_sale(Long id_sale) {
		this.id_sale = id_sale;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
	public class HeaderFooter extends PdfPageEventHelper {

		public void onStartPage(PdfWriter writer, Document document) {

			try {

				Path path1 = Paths.get(ServletActionContext.getServletContext().getResource("/images/logo_ghisoni.jpg").toURI());
				Image img1 = Image.getInstance(path1.toAbsolutePath().toString());

				PdfPTable table = new PdfPTable(new float[] { 1,4,1 });
				table.setTotalWidth(530);
				table.setLockedWidth(true);

				PdfPCell cell1 = new PdfPCell(img1, true);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				//cell1.setFixedHeight(110);
				cell1.setPaddingBottom(5);
				cell1.setPaddingTop(10);
				cell1.setBorderWidthTop(0);
				cell1.setBorderWidthLeft(0);
				cell1.setBorderWidthRight(0);
				cell1.setBorderWidthBottom(0);
				table.addCell(cell1);
				

				PdfPCell cell2 = new PdfPCell(new Phrase("Paolo Ghisoni - Via dei Mille n. 1 - 27011 Belgioioso (PV)\n"
						+ "Tel. 3386280139 Fax 0382970412\n"
						+ "Cod. Fisc. GHSPLA68P22G388W P. IVA 01654440187 - REA PV/200609", regular_bold));
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				//cell2.setFixedHeight(110);
				cell2.setPaddingBottom(5);
				cell2.setPaddingTop(10);
				cell2.setBorderWidthTop(0);
				cell2.setBorderWidthLeft(0);
				cell2.setBorderWidthRight(0);
				cell2.setBorderWidthBottom(0);
				table.addCell(cell2);

				
				Path path2 = Paths.get(ServletActionContext.getServletContext().getResource("/images/riceup2.png").toURI());
				Image img2 = Image.getInstance(path2.toAbsolutePath().toString());
				
				PdfPCell cell3 = new PdfPCell(img2, true);
				cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
				//cell3.setFixedHeight(110);
				cell3.setPaddingBottom(5);
				cell3.setPaddingTop(10);
				cell3.setBorderWidthTop(0);
				cell3.setBorderWidthLeft(0);
				cell3.setBorderWidthRight(0);
				cell3.setBorderWidthBottom(0);
				table.addCell(cell3);
				document.add(table);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void onEndPage(PdfWriter writer, Document document) {

		}

	}
	
	public String execute() {

		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id = :id_sale").setParameter("id_sale", getId_sale()).uniqueResult();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id_admin = :id_admin AND id = :id_sale").setParameter("id_admin", id_admin).setParameter("id_sale", getId_sale()).uniqueResult();
				}
				
				if(this.sale != null) {
					
					SaleProduct sp = sale.getProduct();

					String filename = "00" + this.sale.getNumber().toString() + " - " + this.sale.getSeller().getBusiness_name() + " - " + this.sale.getBuyer().getBusiness_name() + " - " + new SimpleDateFormat("dd-MM-yyyy").format(this.sale.getSale_date()) + ".pdf";
					filename = filename.replaceAll("'", "");
						
					//------------------------------ preparo documento 
					document = new Document();
					output = new ByteArrayOutputStream();
					writer = PdfWriter.getInstance(document, output);
					
					NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.ITALY);
					formatter.setMinimumFractionDigits(2);
					formatter.setMaximumFractionDigits(3);
					
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

					PdfPTable table;
					PdfPCell cell;
					Phrase p;
					Chunk c;
					
					HeaderFooter hf = new HeaderFooter();
					writer.setPageEvent(hf);
					
					document.open();
					
					//inizio
					
					table = new PdfPTable(1);
					table.setTotalWidth(530);
					table.setLockedWidth(true);
					table.setSpacingBefore(0);
					
					p = new Phrase();
					c = new Chunk("STABILITO\n", regular_bold);
					p.add(c);
					c = new Chunk("IN RIFERIMENTO ALLE CONDIZIONI GENERALI DI CONTRATTO PER LA COMPRAVENDITA DI RISONE APPROVATE DALLA CAMERA DI COMMERCIO DI PAVIA", regular);
					p.add(c);
					
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorderWidth(0);
					cell.setPaddingBottom(4);
					cell.setPaddingTop(4);
					
					table.addCell(cell);
					
					document.add(table);
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("N.", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getNumber().toString() + " REDATTO IN 3 COPIE");
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("VENDITORE:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add((sale.getSeller().getBusiness_name() + " - " + sale.getSeller().getLegal_address() + " - " + sale.getSeller().getLegal_zip_code() + " " + sale.getSeller().getLegal_city() + " " + sale.getSeller().getLegal_province() + " - P.IVA " + sale.getSeller().getVat_number() + " - Tel. " + sale.getSeller().getPrimary_phone() + " " + sale.getSeller().getSecondary_phone()).toUpperCase().replaceAll("NULL", "") + " - Cod. univoco " + sale.getSeller().getSdi_code());
						} catch(Exception ex) {}

						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("ACQUIRENTE:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add((sale.getBuyer().getBusiness_name() + " - " + sale.getBuyer().getLegal_address() + " - " + sale.getBuyer().getLegal_zip_code() + " " + sale.getBuyer().getLegal_city() + " " + sale.getBuyer().getLegal_province() + " - P.IVA " + sale.getBuyer().getVat_number() + " " + sale.getBuyer().getSecondary_phone()).toUpperCase().replaceAll("NULL", "") + " - Cod. univoco " + sale.getBuyer().getSdi_code());
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("INTERMEDIARIO:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("PAOLO GHISONI", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("MERCE:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getName().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}

						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("VENDITA A:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getSale_type().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					
					//
					try {
						String prezzo = new String();
						
						try {
							if(sale.getAlt_list() != null) {
								if(sale.getAlt_list().isBlank()) {
									prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("�", "");
									
									if(sp.getAmount_type().equals("iva")) {
										prezzo += " IVA INCLUSA";
									} else {
										prezzo += " IVA ESCLUSA";
									}
								} else {
									prezzo = sale.getAlt_list();
								}
								
							} else {
								prezzo = NumberFormat.getCurrencyInstance().format(sp.getAmount()).replaceAll("NULL", "").replaceAll("�", "");
								
								if(sp.getAmount_type().equals("iva")) {
									prezzo += " IVA INCLUSA";
								} else {
									prezzo += " IVA ESCLUSA";
								}
							}
						} catch(Exception ex) {}
						
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("PREZZO (�/Tonnellata):", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase(prezzo, regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						String quantita = new String();
						
						try {
							if(sp.getEnd_of_stock().contentEquals("true")) {
								quantita = NumberFormat.getNumberInstance().format(sp.getProduct_amount()) + "       FINE MAGAZZINO";
							} else {
								quantita = NumberFormat.getNumberInstance().format(sp.getProduct_amount());
							}
						} catch(Exception ex) {}

						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("QUANTITA' (Tonnellate):", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase(quantita.replaceAll("NULL", ""), regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("QUALITA':", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getQuality().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("TENORE DI UMIDITA':", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getHumidity().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("ANNO DEL RACCOLTO:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getHarvest_year().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { 1,1,1,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("RESA IN GRANI INTERI:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getGrains_yield().toString().toUpperCase().replaceAll("NULL", "")); 
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("RESA GLOBALE:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getGlobal_yield().toString().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
						
					//
					try {
						table = new PdfPTable(new float[] { 1,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("DIFETTI:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sp.getDefects().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)1.5,1,(float)1.5,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						p = new Phrase();
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("%", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("%", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)1.5,1,(float)1.5,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						p = new Phrase("GRANI ROSSI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_red_grains().toString().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("DISFORMITA' NATURALI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_malformations().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)1.5,1,(float)1.5,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						p = new Phrase("IMPURITA' VARIETALI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_impurity().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("GRANI GESSATI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_pinstriped_grains().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)1.5,1,(float)1.5,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						p = new Phrase("GRANI DANNEGGIATI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_damaged_grains().toString().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("GRANI DANNEGGIATI DA CALORE:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_heated_damaged_grains().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)1.5,1,(float)1.5,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						p = new Phrase("CORPI ESTRANEI COMMESTIBILI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_edible_extra().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase("CORPI ESTRANEI NON COMMESTIBILI:", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(small);
							p.add(sp.getD_non_edible_extra().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell.setPaddingBottom(2);
						cell.setPaddingTop(2);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
//					try {
//						table = new PdfPTable(new float[] { (float) 1.5,4 });
//						table.setTotalWidth(530);
//						table.setLockedWidth(true);
//						table.setSpacingBefore(15);
//						
//						p = new Phrase();
//						try {
//							p.setFont(regular_bold);
//							p.add(sale.getPickup_type().toUpperCase().replaceAll("NULL", ""));
//						} catch(Exception ex) {}
//						
//						cell = new PdfPCell(p);
//						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//						cell.setBorderWidth(0);
//						cell.setPaddingBottom(0);
//						cell.setPaddingTop(0);
//						
//						table.addCell(cell);
//						
//						p = new Phrase();
//						
//						cell = new PdfPCell(p);
//						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//						cell.setBorderWidth(0);
//						cell.setPaddingBottom(0);
//						cell.setPaddingTop(0);
//						
//						table.addCell(cell);
//						
//						document.add(table);
//					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("TEMPO " + sale.getPickup_type().toUpperCase().replaceAll("NULL", "") + ":", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getPickup_time().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("MODALITA' " + sale.getPickup_type().toUpperCase().replaceAll("NULL", "") + ":", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getPickup_way().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("LUOGO " + sale.getPickup_type().toUpperCase().replaceAll("NULL", "") + ":", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add((sale.getPickup_place() + " - " + sale.getPickup_address() + " " + sale.getPickup_city() + " " + sale.getPickup_province() + " - Tel. " + sale.getPickup_phone()).toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						String pagamento = new String();
						
						try {
							String dates = "";
							for(int i = 0; i < sale.getInstallments().size(); i++) {
								dates += "Scad. : " + format.format(sale.getInstallments().get(i).getExpire_date()) + " ";
							}
							
							pagamento = sale.getPayment().getDescription().toUpperCase().replaceAll("NULL", "") + " " + dates.toUpperCase().replaceAll("NULL", "");
							pagamento += " - AI SENSI E PER GLI EFFETTI DEL D. LGS. 198/21";
						} catch(Exception ex) {}
						
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("PAGAMENTO:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase(pagamento, regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("MEDIAZIONE:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getMediation().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("IMBALLAGGIO:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getPackaging().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float) 1.5,4 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("CONDIZIONI PARTICOLARI:", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(sale.getParticular_conditions().toUpperCase().replaceAll("NULL", ""));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { 1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("LABORATORIO PER L'ANALISI DELLE CARATTERISTICHE: E.N.R DI PAVIA", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}

//					try {
//						table = new PdfPTable(new float[] { 1 });
//						table.setTotalWidth(530);
//						table.setLockedWidth(true);
//						table.setSpacingBefore(15);
//						
//						p = new Phrase("NOTE: PER QUANTO NON PREVISTO SULLO STABILITO SI APPLICA IL DISPOSTO ART. 62 COMMA 1 DEL D.L. 24/01/2012 N° 1, CONVERTITO CON MODIFICAZIONI "
//								+ "DALLA LEGGE 24/03/2012 N° 27.", small);
//						
//						cell = new PdfPCell(p);
//						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//						cell.setBorderWidth(0);
//						cell.setPaddingBottom(0);
//						cell.setPaddingTop(0);
//						
//						table.addCell(cell);
//						
//						document.add(table);
//					} catch(Exception ex) {}
					

					
					try {
					table = new PdfPTable(new float[] { 1 });
					table.setTotalWidth(530);
					table.setLockedWidth(true);
					table.setSpacingBefore(15);
					
					p = new Phrase(" ", small);
					
					cell = new PdfPCell(p);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setBorderWidth(0);
					cell.setPaddingBottom(0);
					cell.setPaddingTop(0);
					
					table.addCell(cell);
					
					document.add(table);
				} catch(Exception ex) {}
					
					try {
						table = new PdfPTable(new float[] { 1,1,1,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("LUOGO", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("BELGIOIOSO", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("DATA", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(format.format(sale.getSale_date()));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)0.3,1,(float)0.6,1,(float)0.6,1,(float)0.3 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
			
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						if(sale.getSignedSeller().equals("SI")) {
							p = new Phrase("Firmato da " + sale.getSeller().getSigner() + " il " + format.format(sale.getSignedSellerDate()), console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						} else {
							p = new Phrase(" ", console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						}

						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						if(sale.getSignedBuyer().equals("SI")) {
							p = new Phrase("Firmato da " + sale.getBuyer().getSigner() + " il " + format.format(sale.getSignedBuyerDate()), console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						} else {
							p = new Phrase(" ", console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						}
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						
						try {
							Path path = Paths.get(ServletActionContext.getServletContext().getResource("/images/firma_ghisoni.jpg").toURI());
							Image img = Image.getInstance(path.toAbsolutePath().toString());						
							
							cell = new PdfPCell(img);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(0);
							cell.setPaddingTop(5);
							cell.setFixedHeight(40);
							
							table.addCell(cell);
						} catch(Exception ex) {}
						

						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)0.3,1,(float)0.6,1,(float)0.6,1,(float)0.3 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("IL VENDITORE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);

						table.addCell(cell);
						
						p = new Phrase("L'ACQUIRENTE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("IL MEDIATORE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
					
						
						document.add(table);
					} catch(Exception ex) {}
					
					try {

						table = new PdfPTable(new float[] { 1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(15);
						
						p = new Phrase("Ai sensi e per gli effetti di cui all'art. 1341, secondo comma, cod. ci. dichiariamo di approvare espressamente le clausole del presente stabilito in deroga alle condizioni generali di contratto che qui si intendono "
								+ "integralmente richiamate: Art. 5, clausola risolutiva espressa (art. 1454 cod. civ.); Art. 7, assunzioni dei rischi di viaggio; Art. 10, rifiuto di una consegna; Art. 11, arbitraggio tecnico; Art. 12, effetti dei ritardati "
								+ "pagamenti; Art. 13, clausola risolutiva espressa (art. 1454 cod. civ.); Art. 15, conciliazione ed arbitrato.", small);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					

					//
					try {
						table = new PdfPTable(new float[] { 1,1,1,1 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(5);
						
						p = new Phrase("LUOGO", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("BELGIOIOSO", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("DATA", regular_bold);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase();
						try {
							p.setFont(regular);
							p.add(format.format(sale.getSale_date()));
						} catch(Exception ex) {}
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)0.3,1,(float)0.6,1,(float)0.6,1,(float)0.3 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
			
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						if(sale.getSignedSeller().equals("SI")) {
							p = new Phrase("Firmato da " + sale.getSeller().getSigner() + " il " + format.format(sale.getSignedSellerDate()), console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						} else {
							p = new Phrase("", console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						}

						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						if(sale.getSignedBuyer().equals("SI")) {
							p = new Phrase("Firmato da " + sale.getBuyer().getSigner() + " il " + format.format(sale.getSignedBuyerDate()), console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						} else {
							p = new Phrase("", console);		
							cell = new PdfPCell(p);
							cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(2);
							cell.setPaddingTop(0);
						}
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						
						try {
							Path path = Paths.get(ServletActionContext.getServletContext().getResource("/images/firma_ghisoni.jpg").toURI());
							Image img = Image.getInstance(path.toAbsolutePath().toString());						
							
							cell = new PdfPCell(img);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBorderWidth(0);
							cell.setPaddingBottom(0);
							cell.setPaddingTop(5);
							cell.setFixedHeight(40);
							
							table.addCell(cell);
						} catch(Exception ex) {}
						

						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						
						
						document.add(table);
					} catch(Exception ex) {}
					
					//
					try {
						table = new PdfPTable(new float[] { (float)0.3,1,(float)0.6,1,(float)0.6,1,(float)0.3 });
						table.setTotalWidth(530);
						table.setLockedWidth(true);
						table.setSpacingBefore(0);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("IL VENDITORE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);

						table.addCell(cell);
						
						p = new Phrase("L'ACQUIRENTE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						p = new Phrase("IL MEDIATORE", regular);
						
						cell = new PdfPCell(p);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBorderWidth(0);
						cell.setBorderWidthTop(1);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
						cell = new PdfPCell();
						cell.setBorderWidth(0);
						cell.setPaddingBottom(0);
						cell.setPaddingTop(0);
						
						table.addCell(cell);
						
					
						
						document.add(table);
					} catch(Exception ex) {}
					
					
		
					
					//fine
	
					document.close();
					
					//------------------------------ genero il pdf
		            ContextSupport.getResponse().setContentType("application/pdf");
		            ContextSupport.getResponse().addHeader("Content-Disposition", "attachment; filename=" + filename);
		            ContextSupport.getResponse().setContentLength(output.size());
		            OutputStream os = ContextSupport.getResponse().getOutputStream();
		            output.writeTo(os);
		            os.flush();
		            os.close();

				}
				
				
			} 
			
		} catch(Exception ex) {
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			
		} finally {
			sx.close();
		}
		
		return null;
	}

}
