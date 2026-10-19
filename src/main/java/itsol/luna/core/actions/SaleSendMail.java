package itsol.luna.core.actions;


import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;

import itsol.luna.core.beans.Sale;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.ContextSupport;
import itsol.luna.core.support.LogSupport;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SalePDFSupport;
import itsol.luna.core.support.SessionSupport;
import itsol.luna.core.utils.HibernateUtil;

public class SaleSendMail extends MyActionSupport implements MyActionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 238195400289594572L;
	
	private Long id_sale;
	private Sale sale;
	private String customer;
	
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
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
	
	public String execute() {
		
		String result = new String();
		Session sx = HibernateUtil.getSessionFactory().openSession();
		
		try {
			
			if(SessionSupport.checkSession()) {
				
				if(SessionSupport.checkSessionLevel("admin|super")) {
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id = :id_sale").setParameter("id_sale", getId_sale()).uniqueResult();
				} else {
					Long id_admin = (Long)SessionSupport.getAdmin().getId();
					this.sale = (Sale) sx.createQuery("FROM Sale WHERE id_admin = :id_admin AND id = :id_sale").setParameter("id_admin", id_admin).setParameter("id_sale", getId_sale()).uniqueResult();
				}
				
				//
				
				if(this.sale != null) {

					String filename = "00" + this.sale.getNumber().toString() + " - " + this.sale.getSeller().getBusiness_name() + " - " + this.sale.getBuyer().getBusiness_name() + " - " + new SimpleDateFormat("dd-MM-yyyy").format(this.sale.getSale_date()) + ".pdf";
						
					if(this.sale.getContract_template().getAction().equals("create-pdf")) {
						SalePDFSupport.create1(filename, this.sale);
					} else if(this.sale.getContract_template().getAction().equals("create-pdf-2")) {
						SalePDFSupport.create2(filename, this.sale);
					} else if(this.sale.getContract_template().getAction().equals("create-pdf-3")) {
						SalePDFSupport.create3(filename, this.sale);
					}
					
					try {
						
						File file = new File(ContextSupport.getUploadPath() + filename);
						
						Properties mailProp = new Properties();
						mailProp.put("mail.smtp.host", ContextSupport.app.getProperty("mail_smtp_host"));
						mailProp.put("mail.smtp.port", ContextSupport.app.getProperty("mail_smtp_port"));
						mailProp.put("mail.smtp.auth", "true");
						mailProp.put("mail.smtp.ssl.enable", "true");
						mailProp.put("mail.user", ContextSupport.app.getProperty("mail_username"));
						mailProp.put("mail.password", ContextSupport.app.getProperty("mail_password"));
						
				        Authenticator auth = new Authenticator() {
				            public PasswordAuthentication getPasswordAuthentication() {
				                return new PasswordAuthentication(
				                		ContextSupport.app.getProperty("mail_username"),
				                		ContextSupport.app.getProperty("mail_password"));
				            }
				        };
				        javax.mail.Session mailSession = javax.mail.Session.getInstance(mailProp, auth);
				        
				        Message msg = new MimeMessage(mailSession);

				        if(SessionSupport.getAdmin().getId() == 2) {
				        
				        try {
					        msg.setFrom(new InternetAddress("info@paolo-ghisoni.it"));
					        
					    
					        
					        if(getCustomer().equals("seller")) {
						        
						        if(!sale.getSeller().getSecondary_email().isEmpty()) {
						        	InternetAddress[] toAddresses = {new InternetAddress(sale.getSeller().getPrimary_email()), new InternetAddress(sale.getSeller().getSecondary_email())};
						        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
						        } else {
						        	InternetAddress[] toAddresses = {new InternetAddress(sale.getSeller().getPrimary_email())};
						        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
						        }
					        } else if(getCustomer().equals("buyer")) {
						        if(!sale.getBuyer().getSecondary_email().isEmpty()) {
						        	InternetAddress[] toAddresses = {new InternetAddress(sale.getBuyer().getPrimary_email()), new InternetAddress(sale.getBuyer().getSecondary_email())};
						        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
						        } else {
						        	InternetAddress[] toAddresses = {new InternetAddress(sale.getBuyer().getPrimary_email())};
						        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
						        }
					        }
					        
					        

					        
					        InternetAddress[] bccAddresses = { new InternetAddress("pgpghisoni@gmail.com") };

					        
					        msg.addRecipients(Message.RecipientType.BCC, bccAddresses);
					        
					        msg.setSubject("Contratto n. " + sale.getNumber() + " - " + sale.getSeller().getBusiness_name() + " - " + sale.getBuyer().getBusiness_name());
					        msg.setSentDate(new Date());
					        
					        MimeBodyPart messageBodyPart = new MimeBodyPart();
					        messageBodyPart.setContent("Cordiali saluti.<br>Paolo Ghisoni", "text/html");
					        
					        Multipart multipart = new MimeMultipart();
					        multipart.addBodyPart(messageBodyPart);
					        
					        MimeBodyPart attachPart = new MimeBodyPart();
					        try {
			                    attachPart.attachFile(file);
			                } catch (IOException ex) {
			                    ex.printStackTrace();
			                }
			 
			                multipart.addBodyPart(attachPart);
			                
			                msg.setContent(multipart);
			                
			                Transport.send(msg);
			                
			                setResp("Il messaggio è stato inviato.");
			                setResp_status_ok();
			                
				        } catch(Exception ex) {
				        	ex.printStackTrace();
				        	
			                setResp("Il messaggio NON è stato inviato.");
			                setResp_status_ko();
				        }

				        }
				        else if(SessionSupport.getAdmin().getId() == 3) {
					        try {
						        msg.setFrom(new InternetAddress("info@matteo-forte.it"));
						        
						    
						        
						        if(getCustomer().equals("seller")) {
							        
							        if(!sale.getSeller().getSecondary_email().isEmpty()) {
							        	InternetAddress[] toAddresses = {new InternetAddress(sale.getSeller().getPrimary_email()), new InternetAddress(sale.getSeller().getSecondary_email())};
							        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
							        } else {
							        	InternetAddress[] toAddresses = {new InternetAddress(sale.getSeller().getPrimary_email())};
							        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
							        }
						        } else if(getCustomer().equals("buyer")) {
							        if(!sale.getBuyer().getSecondary_email().isEmpty()) {
							        	InternetAddress[] toAddresses = {new InternetAddress(sale.getBuyer().getPrimary_email()), new InternetAddress(sale.getBuyer().getSecondary_email())};
							        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
							        } else {
							        	InternetAddress[] toAddresses = {new InternetAddress(sale.getBuyer().getPrimary_email())};
							        	msg.setRecipients(Message.RecipientType.TO, toAddresses);
							        }
						        }
						        
						        

//						        InternetAddress[] bccAddresses = { new InternetAddress("forte_matteo@virgilio.it") };
//
//						        
//						        msg.addRecipients(Message.RecipientType.BCC, bccAddresses);
						        
						        msg.setSubject("Contratto n. " + sale.getNumber() + " - " + sale.getSeller().getBusiness_name() + " - " + sale.getBuyer().getBusiness_name());
						        msg.setSentDate(new Date());
						        
						        MimeBodyPart messageBodyPart = new MimeBodyPart();
						        messageBodyPart.setContent("Per firmare questo contratto, fare clic sul seguente link: <a href=\"https://gestionale.matteo-forte.it/sales/selfsign/" + sale.getId() + "?customerType=" + getCustomer() + "\">Clicca qui</a><br><br>Cordiali saluti.<br>Matteo Forte", "text/html");
						        
						        Multipart multipart = new MimeMultipart();
						        multipart.addBodyPart(messageBodyPart);
						        
						        MimeBodyPart attachPart = new MimeBodyPart();
						        try {
				                    attachPart.attachFile(file);
				                } catch (IOException ex) {
				                    ex.printStackTrace();
				                }
				 
				                multipart.addBodyPart(attachPart);
				                
				                msg.setContent(multipart);
				                
				                Transport.send(msg);
				                
				                setResp("Il messaggio è stato inviato.");
				                setResp_status_ok();
				                
					        } catch(Exception ex) {
					        	ex.printStackTrace();
					        	
				                setResp("Il messaggio NON è stato inviato.");
				                setResp_status_ko();
					        }
				        }

				        
					} catch(Exception ex) {
						ex.printStackTrace();
					}

						
					

				}
				
				result = SUCCESS;
			} else {
				result = SUCCESS_NOLOGIN;
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			if(ContextSupport.isDebug()) {
				ex.printStackTrace();
				setExcp(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex));
			}
			LogSupport.log(ExceptionUtils.getMessage(ex), Level.INFO);
			LogSupport.log(ExceptionUtils.getMessage(ex) + "\n" + ExceptionUtils.getStackTrace(ex), Level.SEVERE);
			result = ERROR;
		} finally {
			sx.close();
		}
		
		return result;
	}
		

}
