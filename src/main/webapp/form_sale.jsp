<%@include file="header.jsp"%>

<style>
.ui-datepicker-trigger {
	display: none;
}
</style>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">

				<s:if test="%{sale==null}">
					<div class="h3 font-weight-bold border-bottom">Nuova vendita</div>
				</s:if>
				<s:else>
					<div class="h3 font-weight-bold border-bottom">
						Vendita #
						<s:property value="sale.number" />
					</div>
				</s:else>

				<div class="pt-2 pb-3" id="form_sale">

					<s:form action="sales/save" method="post" theme="simple">

						<div class="py-3">
							<a href="sales" role="button" class="btn btn-danger btn-sm">Chiudi</a>
							<s:submit type="button" name="action_type" value="save" cssClass="btn btn-warning btn-sm">Salva</s:submit>
							<s:submit type="button" name="action_type" value="save_close" cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>
						</div>

						<s:hidden key="sale.id" />
						<s:hidden key="sale.admin.id" />
						<s:hidden key="sale.year.id" />
						
						<s:hidden key="sale.otpSeller" />
						<s:hidden key="sale.otpSellerSent" />
						<s:hidden key="sale.signedSeller" />
						<s:hidden key="sale.signedSellerDate" />
						
						<s:hidden key="sale.otpBuyer" />
						<s:hidden key="sale.otpBuyerSent" />
						<s:hidden key="sale.signedBuyer" />
						<s:hidden key="sale.signedBuyerDate" />

						
						
						<s:if test="%{sale.id!=null}">
							<div class="form-row">
								<div class="form-group col-12">
									<div class="border bg-white shadow-sm rounded p-4 h-100">
										<div class="form-row">
											<div class="form-group col-12 col-md-4">
												<s:label for="sale.number" value="Numero contratto" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.number" cssClass="form-control form-control-sm" placeholder="Numero contratto" />
												<div>
													<s:fielderror fieldName="sale.number" />
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</s:if>


						<div class="form-row">

							<div class="form-group col-12 col-md-6">

								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="form-row">
										<div class="form-group col-12">
											<s:label for="sale.seller.id" value="* Venditore" cssClass="font-weight-bold small text-primary" />
											<div>
												<sj:autocompleter key="sale.seller.id" list="customers" listValue="business_name" listKey="id" onChangeTopics="saleSellerChange" />
											</div>
											<div>
												<s:fielderror fieldName="sale.seller.id" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<textarea class="form-control form-control-sm" id="seller_info" rows="6" readonly></textarea>
										</div>
									</div>

								</div>

							</div>

							<div class="form-group col-12 col-md-6">

								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="form-row">
										<div class="form-group col-12">
											<s:label for="sale.buyer.id" value="* Acquirente" cssClass="font-weight-bold small text-primary" />
											<div>
												<sj:autocompleter key="sale.buyer.id" list="customers" listValue="business_name" listKey="id" onChangeTopics="saleBuyerChange" />
											</div>
											<div>
												<s:fielderror fieldName="sale.buyer.id" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<textarea class="form-control form-control-sm" id="buyer_info" rows="6" readonly></textarea>
										</div>
									</div>

								</div>

							</div>

						</div>

						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">
								
									<div class="h4 text-primary">Contratto</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.sale_type" value="Vendita a" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.sale_type" cssClass="form-control form-control-sm" placeholder="Vendita a" />
											<div>
												<s:fielderror fieldName="sale.sale_type" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.sale_date" value="* Data della vendita" cssClass="font-weight-bold small text-primary" />
											<sj:datepicker key="sale.sale_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.sale_date" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.contract_template.id" value="* Formato del contratto" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.contract_template.id" list="contract_templates" listKey="id" listValue="description" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.sale_date" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_type" value="Ritiro/consegna" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.pickup_type" list="#{'Ritiro':'Ritiro','Consegna':'Consegna'}" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.pickup_type" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_time" value="Tempo di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_time" cssClass="form-control form-control-sm" placeholder="Tempo di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_time" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_way" value="Modalità di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_way" cssClass="form-control form-control-sm" placeholder="Modalità di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_way" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_printing" value="Stampa luogo ritiro" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.pickup_printing" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.pickup_printing" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_place" value="Luogo di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_place" cssClass="form-control form-control-sm" placeholder="Luogo di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_place" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_address" value="Indirizzo di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_address" cssClass="form-control form-control-sm" placeholder="Indirizzo di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_address" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_city" value="Città di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_city" cssClass="form-control form-control-sm" placeholder="Città di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_city" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_province" value="Provincia di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_province" cssClass="form-control form-control-sm" placeholder="Provincia di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_province" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_zip_code" value="CAP di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_zip_code" cssClass="form-control form-control-sm" placeholder="CAP di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_zip_code" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.pickup_phone" value="Telefono di ritiro" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.pickup_phone" cssClass="form-control form-control-sm" placeholder="Telefono di ritiro" />
											<div>
												<s:fielderror fieldName="sale.pickup_phone" />
											</div>
										</div>

									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.packaging" value="Imballaggio" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.packaging" cssClass="form-control form-control-sm" placeholder="Imballaggio" />
											<div>
												<s:fielderror fieldName="sale.packaging" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.mediation" value="Mediazione" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.mediation" cssClass="form-control form-control-sm" placeholder="Mediazione" />
											<div>
												<s:fielderror fieldName="sale.mediation" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<s:hidden key="sale.payment.fixed_date" id="fixed_date" />
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.payment.id" value="Condizioni di pagamento" cssClass="font-weight-bold small text-primary" />
											<div>
												<sj:autocompleter key="sale.payment.id" list="payments" listValue="expanded_description" listKey="id" onChangeTopics="salePaymentChange" />
											</div>
											<div>
												<s:fielderror fieldName="sale.payment.id" />
											</div>
										</div>

										<s:if test="%{sale.payment.fixed_date.equals('SI')}">
											<div class="form-group col-12 col-sm-12 col-md-4" id="exp_date_001">
												<s:label for="exp_date_001" value="* Scadenza" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="exp_date_001" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="exp_date_001" />
												</div>
											</div>
										</s:if>
										<s:else>
											<div class="form-group col-12 col-sm-12 col-md-4" style="display: none;" id="exp_date_001">
												<s:label for="exp_date_001" value="Scadenza" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="exp_date_001" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="exp_date_001" />
												</div>
											</div>
										</s:else>

									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.particular_conditions" value="Condizioni particolari" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.particular_conditions" cssClass="form-control form-control-sm" placeholder="Condizioni particolari" />
											<div>
												<s:fielderror fieldName="sale.particular_conditions" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.carico_motrici" value="Carico motrici" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.carico_motrici" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.carico_motrici" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.carico_al_coperto" value="Carico al coperto" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.carico_al_coperto" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.carico_al_coperto" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.disinfestato" value="Disinfestato" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.disinfestato" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.disinfestato" />
											</div>
										</div>
									</div>

								</div>
							</div>
						</div>



						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="h4 text-primary">Merce</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-4">
										
											<s:hidden key="sale.product.id_sale" />
											<s:hidden key="sale.product.name" id="col_name" />
											<s:hidden key="sale.product.unit_of_measure" id="col_unit_of_measure" />
											<s:hidden key="sale.product.vat" id="col_vat" />
											
											<s:label for="sale.product.product.id" value="Prodotto" cssClass="font-weight-bold small text-primary" />
											<sj:autocompleter key="sale.product.product.id" list="products" listValue="name" listKey="id" onChangeTopics="saleProductChange" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-3">
											<s:label for="sale.product.harvest_year" value="Anno del raccolto" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.harvest_year" id="col_harvest_year" cssClass="form-control form-control-sm numeric" placeholder="Anno del raccolto" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.product.amount_type" value="Ivato si/no" cssClass="font-weight-bold small text-primary" />
											<s:select key="sale.product.amount_type" list="#{'iva':'Ivato','noiva':'Non ivato'}" cssClass="form-control form-control-sm numeric" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-3">
											<s:label for="sale.product.end_of_stock" value="Fine mag." cssClass="font-weight-bold small text-primary" />
											<s:checkbox key="sale.product.end_of_stock" id="col_end_of_stock" />
										</div>

									</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-1">
											<s:label for="sale.product.amount" value="* Importo" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.amount" id="col_amount" cssClass="form-control form-control-sm numeric" placeholder="* Importo" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-1">
											<s:label for="sale.product.product_amount" value="* Quantità" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.product_amount" id="col_product_amount" cssClass="form-control form-control-sm numeric" placeholder="* Quantità" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.product.total" value="* Totale" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.total" id="col_total" cssClass="form-control form-control-sm numeric" placeholder="* Totale" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-1">
											<s:label for="sale.product.vat_amount" value="* IVA" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.vat_amount" id="col_vat_amount" cssClass="form-control form-control-sm numeric" placeholder="* IVA" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.product.taxable" value="* Imponibile" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.taxable" id="col_taxable" cssClass="form-control form-control-sm numeric" placeholder="* Imponibile" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.alt_list" value="Listino alternativo" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.alt_list" id="col_alt_list" cssClass="form-control form-control-sm" placeholder="Listino alternativo" />
										</div>
									</div>
									

								</div>
							</div>
						</div>
						
						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="h4 text-primary">Provvigioni</div>
									
									<div class="text-primary">Primo ritiro</div>
									
									<div class="form-row">
									
										<s:hidden key="sale.commissions[0].id" />
										<s:hidden key="sale.commissions[0].idx" />
										<s:hidden key="sale.commissions[0].sale.id" />
									
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">										
											<s:label for="sale.commissions[0].product_amount" value="Quantità" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[0].product_amount" id="col_com_0_product_amount" cssClass="form-control form-control-sm numeric" placeholder="Quantità" readonly="true" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[0].seller_commission" value="Provv. Venditore" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[0].seller_commission" id="col_com_0_seller_commission" cssClass="form-control form-control-sm numeric" placeholder="Provv. Venditore" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[0].buyer_commission" value="Provv. Acquirente" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[0].buyer_commission" id="col_com_0_buyer_commission" cssClass="form-control form-control-sm numeric" placeholder="Provv. Acquirente" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[0].seller_commission_amount" value="Tot. Venditore" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[0].seller_commission_amount" id="col_com_0_seller_commission_amount" cssClass="form-control form-control-sm numeric" placeholder="Tot. Venditore" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[0].buyer_commission_amount" value="Tot. Acquirente" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[0].buyer_commission_amount" id="col_com_0_buyer_commission_amount" cssClass="form-control form-control-sm numeric" placeholder="Tot. Acquirente" />
										</div>
									</div>
									
									<div class="text-primary">Secondo ritiro (eventuale)</div>
									
									<div class="form-row">
									
										<s:hidden key="sale.commissions[1].id" />
										<s:hidden key="sale.commissions[1].idx" />
										<s:hidden key="sale.commissions[1].sale.id" />
									
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">										
											<s:label for="sale.commissions[1].product_amount" value="Quantità" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[1].product_amount" id="col_com_1_product_amount" cssClass="form-control form-control-sm numeric" placeholder="Quantità" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[1].seller_commission" value="Provv. Venditore" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[1].seller_commission" value="%{sale.commissions[0].seller_commission}" id="col_com_1_seller_commission" cssClass="form-control form-control-sm numeric" placeholder="Provv. Venditore" readonly="true" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[1].buyer_commission" value="Provv. Acquirente" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[1].buyer_commission" value="%{sale.commissions[0].buyer_commission}" id="col_com_1_buyer_commission" cssClass="form-control form-control-sm numeric" placeholder="Provv. Acquirente" readonly="true" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[1].seller_commission_amount" value="Tot. Venditore" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[1].seller_commission_amount" id="col_com_1_seller_commission_amount" cssClass="form-control form-control-sm numeric" placeholder="Tot. Venditore" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6 col-lg-2">
											<s:label for="sale.commissions[1].buyer_commission_amount" value="Tot. Acquirente" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.commissions[1].buyer_commission_amount" id="col_com_1_buyer_commission_amount" cssClass="form-control form-control-sm numeric" placeholder="Tot. Acquirente" />
										</div>
									</div>
									
								</div>
							</div>
						</div>
						
						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="h4 text-primary">Qualità</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.quality" value="Qualità" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.quality" id="col_quality" cssClass="form-control form-control-sm" placeholder="Qualità" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.humidity" value="Tenore di umidità" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.humidity" id="col_humidity" cssClass="form-control form-control-sm" placeholder="Tenore di umidità" />
										</div>
									</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.grains_yield" value="Resa in grani interi" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.grains_yield" id="col_grains_yield" cssClass="form-control form-control-sm numeric" placeholder="Resa in grani interi" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.global_yield" value="Resa globale" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.global_yield" id="col_global_yield" cssClass="form-control form-control-sm numeric" placeholder="Resa globale" />
										</div>
									</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.defects" value="Difetti" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.defects" id="col_defects" cssClass="form-control form-control-sm" placeholder="Difetti" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<s:label for="sale.product.triciclazolo" value="Triciclazolo" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.triciclazolo" id="col_triciclazolo" cssClass="form-control form-control-sm" placeholder="Triciclazolo" />
										</div>
									</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_red_grains" value="Grani rossi" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_red_grains" id="col_d_red_grains" cssClass="form-control form-control-sm numeric" placeholder="Grani rossi" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_impurity" value="Impurità varietali" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_impurity" id="col_d_impurity" cssClass="form-control form-control-sm numeric" placeholder="Impurità varietali" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_malformations" value="Malformazioni" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_malformations" id="col_d_malformations" cssClass="form-control form-control-sm numeric" placeholder="Malformazioni" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_pinstriped_grains" value="Grani gessati" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_pinstriped_grains" id="col_d_pinstriped_grains" cssClass="form-control form-control-sm numeric" placeholder="Grani gessati" />
										</div>
									</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_damaged_grains" value="Grani danneggiati" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_damaged_grains" id="col_d_damaged_grains" cssClass="form-control form-control-sm numeric" placeholder="Grani danneggiati" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_heated_damaged_grains" value="Grani danneggiati dal calore" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_heated_damaged_grains" id="col_d_heated_damaged_grains" cssClass="form-control form-control-sm numeric" placeholder="Grani danneggiati dal calore" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_edible_extra" value="Corpi esterni commestibili" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_edible_extra" id="col_d_edible_extra" cssClass="form-control form-control-sm numeric" placeholder="Corpi esterni commestibili" />
										</div>
										<div class="form-group col-12 col-sm-12 col-md-3">
											<s:label for="sale.product.d_non_edible_extra" value="Corpi esterni non commestibli" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.product.d_non_edible_extra" id="col_d_non_edible_extra" cssClass="form-control form-control-sm numeric" placeholder="Corpi esterni non commestibli" />
										</div>
									</div>
									
								</div>
							</div>
						</div>

						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="h4 text-primary">Pagamenti</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_deposit_desc" value="Fattura (acconto)" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.invoice_deposit_desc" cssClass="form-control form-control-sm" placeholder="Fattura (acconto)" />
											<div>
												<s:fielderror fieldName="sale.invoice_deposit_desc" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_deposit_date" value="del" cssClass="font-weight-bold small text-primary" />
											<sj:datepicker key="sale.invoice_deposit_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.invoice_deposit_date" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_deposit_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.invoice_deposit_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
											<div>
												<s:fielderror fieldName="sale.invoice_deposit_amount" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_balance_desc" value="Fattura" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.invoice_balance_desc" cssClass="form-control form-control-sm" placeholder="Fattura" />
											<div>
												<s:fielderror fieldName="sale.invoice_balance_desc" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_balance_date" value="del" cssClass="font-weight-bold small text-primary" />
											<sj:datepicker key="sale.invoice_balance_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="sale.invoice_balance_date" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-4">
											<s:label for="sale.invoice_balance_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
											<s:textfield key="sale.invoice_balance_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
											<div>
												<s:fielderror fieldName="sale.invoice_balance_amount" />
											</div>
										</div>
									</div>
									
									<s:set var="more_invoices" value="2" />
									
									<s:if test="%{sale.invoice_extra1_desc.isEmpty()}">
										<div class="form-row" style="display:none;" id="invoice_extra1">
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_desc" value="Fattura ulteriore" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra1_desc" cssClass="form-control form-control-sm" placeholder="Fattura" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_desc" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_date" value="del" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.invoice_extra1_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra1_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_amount" />
												</div>
											</div>
										</div>
									
									</s:if>
									<s:else>
									
										<div class="form-row" id="invoice_extra1">
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_desc" value="Fattura ulteriore" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra1_desc" cssClass="form-control form-control-sm" placeholder="Fattura" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_desc" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_date" value="del" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.invoice_extra1_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra1_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra1_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra1_amount" />
												</div>
											</div>
										</div>
									
										<s:set var="more_invoices" value="#more_invoice-1" />
									
									</s:else>

									<s:if test="%{sale.invoice_extra2_desc.isEmpty()}">
										<div class="form-row" style="display:none;" id="invoice_extra2">
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_desc" value="Fattura ulteriore" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra2_desc" cssClass="form-control form-control-sm" placeholder="Fattura" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_desc" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_date" value="del" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.invoice_extra2_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra2_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_amount" />
												</div>
											</div>
										</div>
									</s:if>
									<s:else>
										<div class="form-row" id="invoice_extra2">
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_desc" value="Fattura ulteriore" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra2_desc" cssClass="form-control form-control-sm" placeholder="Fattura" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_desc" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_date" value="del" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.invoice_extra2_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.invoice_extra2_amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.invoice_extra2_amount" cssClass="form-control form-control-sm numeric" placeholder="Importo" />
												<div>
													<s:fielderror fieldName="sale.invoice_extra2_amount" />
												</div>
											</div>
										</div>
									
										<s:set var="more_invoices" value="#more_invoice-1" />

									</s:else>
									
									<s:if test="%{#more_invoices==1}">
										<button type="button" class="btn btn-primary btn-sm" id="more_invoices" data-invoice="2"><i class="fas fa-plus"></i></button>
									</s:if>
									<s:elseif test="%{#more_invoices==2}">
										<button type="button" class="btn btn-primary btn-sm" id="more_invoices" data-invoice="1"><i class="fas fa-plus"></i></button>
									</s:elseif>
									
								</div>
							</div>
						</div>
						
						<s:if test="%{sale.id!=null}">
						
						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">
									
									<div class="h4 text-primary">Scadenze</div>


									<s:iterator value="sale.installments" var="inst">
									
										<div class="form-row">
										
											<s:hidden key="sale.installments[%{#inst.idx}].id" />
											<s:hidden key="sale.installments[%{#inst.idx}].idx" />
											<s:hidden key="sale.installments[%{#inst.idx}].sale.id" />
										
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#inst.idx}].expire_date" value="Scadenza %{#inst.idx+1}" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.installments[%{#inst.idx}].expire_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#inst.idx}].expire_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#inst.idx}].amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.installments[%{#inst.idx}].amount" cssClass="form-control form-control-sm numeric" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#inst.idx}].amount" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#inst.idx}].payed" value="Pagato" cssClass="font-weight-bold small text-primary" />
												<s:select key="sale.installments[%{#inst.idx}].payed" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#inst.idx}].payed" />
												</div>
											</div>
										</div>
										
										<s:set var="counter" value="#inst.idx+1" />

									</s:iterator>
									
									<s:set var="bkcounter" value="%{counter}" /> 
									
									<s:iterator begin="#counter" end="9">

										<div class="form-row" id="inst-<s:property value="%{#counter}" />" style="display:none;">
										
											<s:hidden key="sale.installments[%{#counter}].id" />
											<s:hidden key="sale.installments[%{#counter}].idx" value="%{#counter}" />
											<s:hidden key="sale.installments[%{#counter}].sale.id" value="%{sale.id}" />
										
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#counter}].expire_date" value="Scadenza %{#counter+1}" cssClass="font-weight-bold small text-primary" />
												<sj:datepicker key="sale.installments[%{#counter}].expire_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#counter}].expire_date" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#counter}].amount" value="Importo" cssClass="font-weight-bold small text-primary" />
												<s:textfield key="sale.installments[%{#counter}].amount" cssClass="form-control form-control-sm numeric" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#counter}].amount" />
												</div>
											</div>
											<div class="form-group col-12 col-sm-12 col-md-4">
												<s:label for="sale.installments[%{#counter}].payed" value="Pagato" cssClass="font-weight-bold small text-primary" />
												<s:select key="sale.installments[%{#counter}].payed" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
												<div>
													<s:fielderror fieldName="sale.installments[%{#counter}].payed" />
												</div>
											</div>
										</div>
										
										<s:set var="counter" value="#counter+1" />
										
									</s:iterator>
									
									<s:if test="%{#bkcounter<=10}">
										<button type="button" class="btn btn-primary btn-sm" id="more_installments" data-installment="<s:property value="%{#bkcounter}" />"><i class="fas fa-plus"></i></button>
									</s:if>
									
								</div>
							</div>
						</div>
						
						</s:if>
						
						<div class="form-row">
							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<div class="h4 text-primary">Fatture</div>
									
									<div class="form-row">
										<div class="form-group col-12 col-sm-12 col-md-6">
											<div class="h5 text-primary">Fattura al venditore</div>
											
											<s:if test="%{sale.commissions[0]!=null}">
												<div class="form-row">
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[0].invoice_seller!=null}">
															<s:label for="sale.commissions[0].billed_seller" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[0].billed_seller" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" readonly="true"/>
															<div><s:fielderror fieldName="sale.commissions[0].billed_seller" /></div>
														</s:if>
														<s:else>
															<s:label for="sale.commissions[0].billed_seller" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[0].billed_seller" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
															<div><s:fielderror fieldName="sale.commissions[0].billed_seller" /></div>
														</s:else>
													</div>
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[0].invoice_seller!=null}">
															<s:hidden key="sale.commissions[0].invoice_seller.id" />
															<s:label for="sale.commissions[0].invoice_seller.number" value="Fattura n." cssClass="font-weight-bold small text-primary" />
															<s:textfield key="sale.commissions[0].invoice_seller.number" cssClass="form-control form-control-sm" readonly="true" />
														</s:if>
														<s:else>
															<s:if test="%{sale.commissions[0].billed_seller.equals('SI')}">
																<s:label for="sale.commissions[0].billed_seller_alt" value="Note esclusione" cssClass="font-weight-bold small text-primary" />
																<s:textfield key="sale.commissions[0].billed_seller_alt" cssClass="form-control form-control-sm" />
																<div><s:fielderror fieldName="sale.commissions[0].billed_seller_alt" /></div>
															</s:if>
														</s:else>
													</div>
												</div>
											</s:if>

										</div>
										
										<div class="form-group col-12 col-sm-12 col-md-6">
											<div class="h5 text-primary">Fattura all'acquirente</div>
											
											<div class="form-row">
												<div class="form-group col-6">
													<s:if test="%{sale.commissions[0].invoice_buyer!=null}">
														<s:label for="sale.commissions[0].billed_buyer" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
														<s:select key="sale.commissions[0].billed_buyer" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" readonly="true"/>
														<div><s:fielderror fieldName="sale.commissions[0].billed_buyer" /></div>
													</s:if>
													<s:else>
														<s:label for="sale.commissions[0].billed_buyer" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
														<s:select key="sale.commissions[0].billed_buyer" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
														<div><s:fielderror fieldName="sale.commissions[0].billed_buyer" /></div>
													</s:else>
												</div>
												<div class="form-group col-6">
													<s:if test="%{sale.commissions[0].invoice_buyer!=null}">
														<s:hidden key="sale.commissions[0].invoice_buyer.id" />
														<s:label for="sale.commissions[0].invoice_buyer.number" value="Fattura n." cssClass="font-weight-bold small text-primary" />
														<s:textfield key="sale.commissions[0].invoice_buyer.number" cssClass="form-control form-control-sm" readonly="true" />
													</s:if>
													<s:else>
														<s:if test="%{sale.commissions[0].billed_buyer.equals('SI')}">
															<s:label for="sale.commissions[0].billed_buyer_alt" value="Note esclusione" cssClass="font-weight-bold small text-primary" />
															<s:textfield key="sale.commissions[0].billed_buyer_alt" cssClass="form-control form-control-sm" />
															<div><s:fielderror fieldName="sale.commissions[0].billed_buyer_alt" /></div>
														</s:if>
													</s:else>
												</div>
											</div>
										</div>
										
									</div>
									
									<s:if test="%{sale.commissions[1]!=null}">
										<div class="form-row">
											<div class="form-group col-12 col-sm-12 col-md-6">
												<div class="h5 text-primary">Ulteriore fattura al venditore</div>

												<div class="form-row">
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[1].invoice_seller!=null}">
															<s:label for="sale.commissions[1].billed_seller" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[1].billed_seller" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" readonly="true"/>
															<div><s:fielderror fieldName="sale.commissions[1].billed_seller" /></div>
														</s:if>
														<s:else>
															<s:label for="sale.commissions[1].billed_seller" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[1].billed_seller" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
															<div><s:fielderror fieldName="sale.commissions[1].billed_seller" /></div>
														</s:else>
													</div>
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[1].invoice_seller!=null}">
															<s:hidden key="sale.commissions[1].invoice_seller.id" />
															<s:label for="sale.commissions[1].invoice_seller.number" value="Fattura n." cssClass="font-weight-bold small text-primary" />
															<s:textfield key="sale.commissions[1].invoice_seller.number" cssClass="form-control form-control-sm" readonly="true" />
														</s:if>
														<s:else>
															<s:if test="%{sale.commissions[1].billed_seller.equals('SI')}">
																<s:label for="sale.commissions[1].billed_seller_alt" value="Note esclusione" cssClass="font-weight-bold small text-primary" />
																<s:textfield key="sale.commissions[1].billed_seller_alt" cssClass="form-control form-control-sm" />
																<div><s:fielderror fieldName="sale.commissions[1].billed_seller_alt" /></div>
															</s:if>
														</s:else>
													</div>
												</div>
											</div>
										
											<div class="form-group col-12 col-sm-12 col-md-6">
												<div class="h5 text-primary">Ulteriore fattura all'acquirente</div>
											
												<div class="form-row">
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[1].invoice_buyer!=null}">
															<s:label for="sale.commissions[1].billed_buyer" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[1].billed_buyer" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" readonly="true"/>
															<div><s:fielderror fieldName="sale.commissions[1].billed_buyer" /></div>
														</s:if>
														<s:else>
															<s:label for="sale.commissions[1].billed_buyer" value="Emessa/esclusa" cssClass="font-weight-bold small text-primary" />
															<s:select key="sale.commissions[1].billed_buyer" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
															<div><s:fielderror fieldName="sale.commissions[1].billed_buyer" /></div>
														</s:else>
													</div>
													<div class="form-group col-6">
														<s:if test="%{sale.commissions[1].invoice_buyer!=null}">
															<s:hidden key="sale.commissions[1].invoice_buyer.id" />
															<s:label for="sale.commissions[1].invoice_buyer.number" value="Fattura n." cssClass="font-weight-bold small text-primary" />
															<s:textfield key="sale.commissions[1].invoice_buyer.number" cssClass="form-control form-control-sm" readonly="true" />
														</s:if>
														<s:else>
															<s:if test="%{sale.commissions[1].billed_buyer.equals('SI')}">
																<s:label for="sale.commissions[1].billed_buyer_alt" value="Note esclusione" cssClass="font-weight-bold small text-primary" />
																<s:textfield key="sale.commissions[1].billed_buyer_alt" cssClass="form-control form-control-sm" />
																<div><s:fielderror fieldName="sale.commissions[1].billed_buyer_alt" /></div>
															</s:if>
														</s:else>
													</div>
												</div>
											</div>
										
										</div>
									</s:if>
								</div>
							</div>
						</div>

						<a href="sales" role="button" class="btn btn-danger btn-sm">Chiudi</a>
						<s:submit type="button" name="action_type" value="save" cssClass="btn btn-warning btn-sm">Salva</s:submit>
						<s:submit type="button" name="action_type" value="save_close" cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>

					</s:form>
				</div>

			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>
