function setFile(src, target) {
	$('#' + target).val(src);
	$('#the-image').attr('src', 'GetImg?src=' + encodeURI(src));
	$('#media-library-modal').modal('hide');
}

$(document).ready(
		function() {
			
			var base_url = $('base').attr('href');
				
			var counter = parseInt($('#counter').val());
			
			if(counter == 0) {
				$('#col_' + counter).fadeIn('fast');
				counter++;
			}
			
			$('.load-window').fadeOut('fast'); 
			
			
			$('a:not(.no-loader)').click(
					function() {
						if($(this).attr('href') != "#" && $(this).attr('href') != undefined) {
							$('.load-window').fadeIn('fast');
						}
					});
			
			$('.fix-height').css('min-height', $(this).height() - $('header').height()).removeClass('vh-100');
			
			if($('#sidebar-menu').length) {
				var smenu = $('#sidebar-menu');
				var smenuw = $('#sidebar-menu').width();
				
				
				smenu.parent('div').css('min-width', smenuw + 10);
			}
			
			$('.numeric').keypress(
					function(e) {
						var t = this;
						var key = e.keyCode || e.charCode;
						if(key < 48 || key > 57) {
							if(key != 44) {
								if(key != 46) {
									e.preventDefault();
									
									if($(t).hasClass('negative') && key == 45) {
										$(t).val($(t).val() + '-');
									}
									
								} else {
									e.preventDefault();
									$(t).val($(t).val() + ',');
								}
							} 
						}
					});
			
			$('#show-media-library').click(
					function() {
						var t = this;
						$('#media-library-modal').find('#media-library-frame').attr('src', base_url + 'media-library?target=' + $(t).data('target'));
						$('#media-library-modal').modal('show');
					});


			$('.create-invoice').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per generare la fattura per questa vendita. Vuoi continuare?");
						
						if (r == true) {
						  $(t).parents('form').submit();
						} 
					});
					
			$('.delete-measure').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per eliminare questo campione. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
								$('.delete-customer').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per eliminare questo cliente. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
								$('.delete-product').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per eliminare questo prodotto. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
				$('.delete-sale').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per eliminare questa vendita. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
				$('.delete-invoice').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per eliminare questa fattura. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
				$('.send-mail').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per inviare questo contratto. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
				$('.send-otp').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per inviare il codice OTP per firmare il contratto. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});
					
				$('.invoice-payed').click(
					function(e) {
						e.preventDefault();
						
						var t = this;
						
						var r = confirm("Stai per segnalare questa fattura come pagata. Vuoi continuare?");
						
						if (r == true) {
						  location.href = $(t).attr('href');
						} 
					});

			
			

			
			
			


			
		
			
			//form contract
			
			if($('#form_contract').length) {
				
				var appendix;
				
				tinymce.init({
					selector: '.contract_appendix',
					menubar: false,
					init_instance_callback: function (editor) {
						appendix = editor;
					}
				});
				
				$.subscribe('contractTemplateChange', 
						function(event, data) {

							appendix.setContent($(data).val());

						});
				
			}
			
			
			
			//form contract template
			
			if($('#form_contract_template').length) {
				
				tinymce.init({
					selector: '.template',
					menubar: false,
					init_instance_callback: function (editor) {
			
					}
				});
				
			}
			
			
			
			
			//form customer
			
			if($('#form_customer').length) {

				
			}
			
			
			
			
			
			
			//form product
			
			if($('#form_product').length) {
			
				$('#the-image').attr('src', encodeURI($('#the-image').attr('src')));
			
			}
			
			
			
			//form sale
			
			if($('#form_sale').length) {
				
				var seller_id = $('input[name="sale.seller.id"]');
				var buyer_id = $('input[name="sale.buyer.id"]');

				if(seller_id.val() != '') {
					$.getJSON(base_url + 'GetCustomerJSON?id=' + seller_id.val(),
							function(json) {

								var str = json.business_name + '\n';
								str += json.legal_address + '\n';
								str += json.legal_zip_code + ' ' + json.legal_city + ' ' + json.legal_province + '\n';
								str += 'P. IVA ' + json.vat_number + '\n';
								$('#seller_info').val(str);

							});
				}
				
				if(buyer_id.val() != '') {
					$.getJSON(base_url + 'GetCustomerJSON?id=' + buyer_id.val(),
							function(json) {

								var str = json.business_name + '\n';
								str += json.legal_address + '\n';
								str += json.legal_zip_code + ' ' + json.legal_city + ' ' + json.legal_province + '\n';
								str += 'P. IVA ' + json.vat_number + '\n';
								$('#buyer_info').val(str);

							});
				}

				
				$('#col_amount').change(
						function() {
						
							var amount = parseFloat($('#col_amount').val().replace(',', '.'));
							
							var product_amount = parseFloat($('#col_product_amount').val().replace(',', '.'));
							var vat = parseFloat($('#col_vat').val().replace(',', '.'));
						
							var total = amount * product_amount;
							var taxable = total / (1 + (vat / 100));
							var vat_amount = taxable * (vat / 100);
	
							$('#col_total').val(total.toString().replace('.', ','));	
							$('#col_taxable').val(taxable.toString().replace('.', ','));	
							$('#col_vat_amount').val(vat_amount.toString().replace('.', ','));	

						});
				
				$('#col_product_amount').change(
						function() {
							
							var amount = parseFloat($('#col_amount').val().replace(',', '.'));
							var product_amount = parseFloat($('#col_product_amount').val().replace(',', '.'));
							var vat = parseFloat($('#col_vat').val().replace(',', '.'));
							
							var seller_commission = parseFloat($('#col_com_0_seller_commission').val().replace(',', '.'));
							var buyer_commission = parseFloat($('#col_com_0_buyer_commission').val().replace(',', '.'));

							var total = amount * product_amount;
							var taxable = total / (1 + (vat / 100));
							var vat_amount = taxable * (vat / 100);
							
							var seller_commission_amount = seller_commission * product_amount;
							var buyer_commission_amount = buyer_commission * product_amount;
							
							$('#col_total').val(total.toString().replace('.', ','));	
							$('#col_taxable').val(taxable.toString().replace('.', ','));	
							$('#col_vat_amount').val(vat_amount.toString().replace('.', ','));	
							$('#col_com_0_seller_commission_amount').val(seller_commission_amount.toString().replace('.', ','));
							$('#col_com_0_buyer_commission_amount').val(buyer_commission_amount.toString().replace('.', ','));
							$('#col_com_0_product_amount').val(product_amount.toString().replace('.', ','));
							
							$('#col_com_1_product_amount').val(0);
							$('#col_com_1_seller_commission_amount').val(0);
							$('#col_com_1_buyer_commission_amount').val(0);
							
						});
				

				
				$('#col_com_0_seller_commission').change(
						function() {
							
							var product_amount_0 = parseFloat($('#col_com_0_product_amount').val().replace(',', '.'));
							var product_amount_1 = parseFloat($('#col_com_1_product_amount').val().replace(',', '.'));
							
							var seller_commission = parseFloat($('#col_com_0_seller_commission').val().replace(',', '.'));

							var seller_commission_amount_0 = seller_commission * product_amount_0;
							var seller_commission_amount_1 = seller_commission * product_amount_1;
							
							$('#col_com_0_seller_commission_amount').val(seller_commission_amount_0.toString().replace('.', ','));	
							$('#col_com_1_seller_commission_amount').val(seller_commission_amount_1.toString().replace('.', ','));	
							
							$('#col_com_1_seller_commission').val(seller_commission.toString().replace('.', ','));	
							
						});
						
				$('#col_com_0_buyer_commission').change(
						function() {
							
							var product_amount_0 = parseFloat($('#col_com_0_product_amount').val().replace(',', '.'));
							var product_amount_1 = parseFloat($('#col_com_1_product_amount').val().replace(',', '.'));
							
							var buyer_commission = parseFloat($('#col_com_0_buyer_commission').val().replace(',', '.'));

							var buyer_commission_amount_0 = buyer_commission * product_amount_0;
							var buyer_commission_amount_1 = buyer_commission * product_amount_1;
							
							$('#col_com_0_buyer_commission_amount').val(buyer_commission_amount_0.toString().replace('.', ','));	
							$('#col_com_1_buyer_commission_amount').val(buyer_commission_amount_1.toString().replace('.', ','));	
							
							$('#col_com_1_buyer_commission').val(buyer_commission.toString().replace('.', ','));	
							
						});
						
						
				$('#col_com_1_product_amount').change(
						function() {
							
							
							var product_amount_0 = parseFloat($('#col_com_0_product_amount').val().replace(',', '.'));
							var product_amount_1 = parseFloat($('#col_com_1_product_amount').val().replace(',', '.'));
							
							product_amount_0 = product_amount_0 - product_amount_1;
							
							var seller_commission = parseFloat($('#col_com_0_seller_commission').val().replace(',', '.'));
							var buyer_commission = parseFloat($('#col_com_0_buyer_commission').val().replace(',', '.'));

							var seller_commission_amount_0 = seller_commission * product_amount_0;
							var buyer_commission_amount_0 = buyer_commission * product_amount_0;
							
							var seller_commission_amount_1 = seller_commission * product_amount_1;
							var buyer_commission_amount_1 = buyer_commission * product_amount_1;
							
						
							$('#col_com_0_seller_commission_amount').val(seller_commission_amount_0.toString().replace('.', ','));
							$('#col_com_0_buyer_commission_amount').val(buyer_commission_amount_0.toString().replace('.', ','));
							
							$('#col_com_1_seller_commission_amount').val(seller_commission_amount_1.toString().replace('.', ','));
							$('#col_com_1_buyer_commission_amount').val(buyer_commission_amount_1.toString().replace('.', ','));

							
							$('#col_com_0_product_amount').val(product_amount_0.toString().replace('.', ','));	
							$
							
						});

				
				$.subscribe('saleProductChange', 
						function(event, data) {
			    
							
					
							$.getJSON(base_url + 'GetProductJSON?id=' + $(data).val(),
									function(json) {
	
										var object = new Object();
										object.name = json.name;
										object.vat = json.vat;
										object.amount = 0;
										object.product_amount = 0;
										object.total = 0;
										object.taxable = 0;
										object.unit_of_measure = json.unit_of_measure;
										object.seller_commission = json.seller_commission.toString().replace('.', ',');
										object.buyer_commission = json.buyer_commission.toString().replace('.', ',');
										object.vat_amount = 0;
										object.seller_commission_amount = 0;
										object.buyer_commission_amount = 0;

										$('#col_name').val(object.name);
										$('#col_vat').val(object.vat);
										$('#col_amount').val(object.amount);
										$('#col_product_amount').val(object.product_amount);
										$('#col_total').val(object.total);	
										$('#col_taxable').val(object.taxable);	
										$('#col_unit_of_measure').val(object.unit_of_measure);	
										$('#col_vat_amount').val(object.vat_amount);
										
										$('#col_com_0_product_amount').val(object.product_amount);
										$('#col_com_0_seller_commission').val(object.seller_commission);	
										$('#col_com_0_buyer_commission').val(object.buyer_commission);		
										$('#col_com_0_seller_commission_amount').val(object.seller_commission_amount);	
										$('#col_com_0_buyer_commission_amount').val(object.buyer_commission_amount);	
										
										$('#col_com_1_product_amount').val(object.product_amount);
										$('#col_com_1_seller_commission').val(object.seller_commission);	
										$('#col_com_1_buyer_commission').val(object.buyer_commission);		
										$('#col_com_1_seller_commission_amount').val(object.seller_commission_amount);	
										$('#col_com_1_buyer_commission_amount').val(object.buyer_commission_amount);	
										


							});
						});
				
				$.subscribe('saleSellerChange', 
						function(event, data) {
					
							$.getJSON(base_url + 'GetCustomerJSON?id=' + $(data).val(),
									function(json) {

										var str = json.business_name + '\n';
										str += json.legal_address + '\n';
										str += json.legal_zip_code + ' ' + json.legal_city + ' ' + json.legal_province + '\n';
										str += 'P. IVA ' + json.vat_number + ' Tel. ' + json.primary_phone + ' - ' + json.secondary_phone + '\n';
										$('#seller_info').val(str);
										
//										var str2 = json.business_name + ' - ';
//										str2 += json.legal_address + ' ';
//										str2 += json.legal_zip_code + ' ' + json.legal_city + ' ' + json.legal_province + '\n';
//										$('#sales_save_sale_pickup_place').val(str2);


										$('#sales_save_sale_pickup_place').val(json.business_name);
										$('#sales_save_sale_pickup_address').val(json.legal_address);
										$('#sales_save_sale_pickup_city').val(json.legal_city);
										$('#sales_save_sale_pickup_province').val(json.legal_province);
										$('#sales_save_sale_pickup_zip_code').val(json.legal_zip_code);
										$('#sales_save_sale_pickup_phone').val(json.primary_phone + " " + json.secondary_phone);
										
										$('#sales_save_sale_sale_type').val('Campione');
										$('#sales_save_sale_pickup_time').val('Pronto');
										$('#sales_save_sale_pickup_way').val('Partenza');
										$('#sales_save_sale_packaging').val('Rinfusa');
										$('#sales_save_sale_mediation').val('D\'uso');

									});
							
						});
				
				$.subscribe('saleBuyerChange', 
						function(event, data) {
					
							$.getJSON(base_url + 'GetCustomerJSON?id=' + $(data).val(),
									function(json) {

										var str = json.business_name + '\n';
										str += json.legal_address + '\n';
										str += json.legal_zip_code + ' ' + json.legal_city + ' ' + json.legal_province + '\n';
										str += 'P. IVA ' + json.vat_number + ' Tel. ' + json.primary_phone + ' - ' + json.secondary_phone + '\n';
										$('#buyer_info').val(str);

									});
							
						});
						
				$.subscribe('salePaymentChange', 
						function(event, data) {
							
						$.getJSON(base_url + 'GetPaymentJSON?id=' + $(data).val(),
								function(json) {

							$('#fixed_date').val(json.fixed_date);
							if(json.fixed_date == 'SI') {
								$('#exp_date_001').show(0);
							} else {
								$('#exp_date_001').hide(0);
							}

								});
							

					

							
						});
				
				
				$('#form_sale').find('#show_hidden_invoice').click(
						function() {
							$('#hidden_invoice').show(0);
							$(this).hide(0);
						});
						
				$('#more_invoices').click(
					function() {
						var t = this;
						
						var inv = $(t).data('invoice');
						
						if(inv > 0 && inv <=2) {
							$('#invoice_extra' + inv).show(0);
							inv++;
							$(t).data('invoice', inv);
						}
							
					});
					
				$('#more_installments').click(
					function() {
						var t = this;
						
						var inst = $(t).data('installment');
						
						if(inst > 0 && inst <=9) {
							$('#inst-' + inst).show(0);
							inst++;
							$(t).data('installment', inst);
						}
							
					});
				
			}
			
			
			if($('#form_payment').length) {
				
				var form = $('#form_payment').find('form');
				
				if(form.find('#fixed_date').val() == 'SI') {
							$('#pinstallments').find('#pinst_days_1').prop('readonly', true);
							$('#pinstallments').find('#pinst_days_2').prop('readonly', true);
							$('#pinstallments').find('#pinst_days_3').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_0').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_1').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_2').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_3').prop('readonly', true);
				}
				
				form.find('#fixed_date').change(
					function() {
						var t = this;
						if($(t).val() == 'SI') {
							$('#pinstallments').find('#pinst_days_0').val(0);
							$('#pinstallments').find('#pinst_days_1').val('');
							$('#pinstallments').find('#pinst_days_2').val('');
							$('#pinstallments').find('#pinst_days_3').val('');
							$('#pinstallments').find('#pinst_ap_0').val(100);
							$('#pinstallments').find('#pinst_ap_1').val('');
							$('#pinstallments').find('#pinst_ap_2').val('');
							$('#pinstallments').find('#pinst_ap_3').val('');
							
							$('#pinstallments').find('#pinst_days_1').prop('readonly', true);
							$('#pinstallments').find('#pinst_days_2').prop('readonly', true);
							$('#pinstallments').find('#pinst_days_3').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_0').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_1').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_2').prop('readonly', true);
							$('#pinstallments').find('#pinst_ap_3').prop('readonly', true);
						} else {
							$('#pinstallments').find('input').prop('readonly', false);
						}
					}
				);
			}
			
			
			if($('#form_samples').length) {
				
				var form = $('#form_samples').find('form');
				
				$.subscribe('customerChange', 
						function(event, data) {
					
							form.submit();
							
						});
				
				$.subscribe('productChange', 
						function(event, data) {
					
							form.submit();
							
						});
				
			}
			

			
			if($('#form-invoice-buyer').length) {
				
				var form_inv_buyer = $('#form-invoice-buyer');
				
				form_inv_buyer.submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-buyer').each(
							function() {
								if($(this).is(':checked')) {
									form_inv_buyer.append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {
									var r = confirm("Stai per generare la fattura per le provvigioni selezionate. Vuoi continuare?");
						
									if (r == true) {
									
						 				form_inv_buyer.unbind('submit').submit();
									} 
								});
					});
				
			}
			
			if($('#form-invoice-seller').length) {
				
				var form_seller = $('#form-invoice-seller');
				
				form_seller.submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-seller').each(
							function() {
								if($(this).is(':checked')) {
									form_seller.append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {
									var r = confirm("Stai per generare la fattura per le provvigioni selezionate. Vuoi continuare?");
						
									if (r == true) {
						 				form_seller.unbind('submit').submit();
									} 
								});
					});
				
			}
			
			if($('#form-billed-buyer').length) {
				
				var form_billed_buyer = $('#form-billed-buyer');
				
				form_billed_buyer.submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-buyer').each(
							function() {
								if($(this).is(':checked')) {
									form_billed_buyer.append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {
									var r = confirm("Stai per escludere dalla fatturazione le provvigioni selezionate. Vuoi continuare?");
						
									if (r == true) {
						 				form_billed_buyer.unbind('submit').submit();
									} 
								});
					});
				
			}
			
			if($('#form-billed-seller').length) {
				
				var form_billed_seller = $('#form-billed-seller');
				
				//console.log(form_billed_seller.html());
				
				$('#form-billed-seller').submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-seller').each(
							function() {
								if($(this).is(':checked')) {
									$('#form-billed-seller').append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {
									//console.log($('#form-billed-seller').html());
									
									var r = confirm("Stai per escludere dalla fatturazione le provvigioni selezionate. Vuoi continuare?");
						
									if (r == true) {
						 				$('#form-billed-seller').unbind('submit').submit();
									} 
								});
					});
				
			}
			
			
			if($('#form-comm-buyer').length) {
				
				var form_buyer = $('#form-comm-buyer');
				
				form_buyer.submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-buyer').each(
							function() {
								if($(this).is(':checked')) {
									form_buyer.append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {

						 				form_buyer.unbind('submit').submit();
									
								});
					});
				
			}
			
			
			if($('#form-comm-seller').length) {
				
				var form_billed_seller = $('#form-comm-seller');
				
				form_billed_seller.submit(
					function(e) {
						e.preventDefault();
						
						$('.into-invoice-seller').each(
							function() {
								if($(this).is(':checked')) {
									form_billed_seller.append('<input type="hidden" name="commissions_ids" value="' + $(this).data('value') + '">');
								}	
							}).promise().done(
								function() {

						 				form_billed_seller.unbind('submit').submit();
									
								});
					});
				
			}
			
			
			if($('#check-all-seller').length) {
				var t = $('#check-all-seller');
				
				t.change(
					function() {
						if(t.is(':checked')) {
							$('.into-invoice-seller').prop('checked', true);
						} else {
							$('.into-invoice-seller').prop('checked', false);
						}
					});
			}
			
			if($('#check-all-buyer').length) {
				var t = $('#check-all-buyer');
				
				t.change(
					function() {
						if(t.is(':checked')) {
							$('.into-invoice-buyer').prop('checked', true);
						} else {
							$('.into-invoice-buyer').prop('checked', false);
						}
					});
			}
			



$('.sale-modal-btn').click(
	function(e) {
		e.preventDefault();
		
		var url = $(this).attr('href');
		
		$('#sale-frame').attr('src', url);
		
		$('#sale-modal').modal('show');
	}
);


$('.sign-modal-btn').click(
	function(e) {
		e.preventDefault();
		
		var url = $(this).attr('href');
		
		$('#sale-frame').attr('src', url);
		
		$('#sale-modal').modal('show');
	}
);
			

			
			
		});


var hheight = $('header').height();


$(window).scroll(
		function() {
				
			
			  if (window.pageYOffset > $('header').height() + 10) {
				    $('header').addClass("sticky shadow");
				    $('#sidebar-menu').css('top', hheight + 22.5);
				  } else {
					 $('header').removeClass("sticky shadow");
					 $('#sidebar-menu').css('top', 'initial');
				  }
		});