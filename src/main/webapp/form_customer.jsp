<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h3 font-weight-bold border-bottom">Anagrafica
					cliente</div>

				<div class="pt-2 pb-3" id="customers">

					<s:form action="customers/save" method="post" theme="simple">

						<div class="py-3">
							<a href="customers" role="button" class="btn btn-danger btn-sm">Chiudi</a>
							<s:submit type="button" name="action_type" value="save"
								cssClass="btn btn-warning btn-sm">Salva</s:submit>
							<s:submit type="button" name="action_type" value="save_close"
								cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>
						</div>

						<s:hidden name="customer.id" />
						<s:hidden name="customer.admin.id" />

						<div class="form-row">

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Dati
											dell'azienda</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.business_name" value="* Ragione Sociale"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.business_name"
												cssClass="form-control form-control-sm"
												placeholder="* Ragione Sociale" />
											<div>
												<s:fielderror fieldName="customer.business_name" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.vat_number" value="Partita IVA"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.vat_number"
												cssClass="form-control form-control-sm"
												placeholder="Partita IVA" />
											<div>
												<s:fielderror fieldName="customer.vat_number" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.referent" value="Referente"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.referent"
												cssClass="form-control form-control-sm"
												placeholder="Referente" />
											<div>
												<s:fielderror fieldName="customer.referent" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.tax_code" value="Codice fiscale"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.tax_code"
												cssClass="form-control form-control-sm"
												placeholder="Codice fiscale" />
											<div>
												<s:fielderror fieldName="customer.tax_code" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.internal_code" value="Codice interno *"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.internal_code"
												cssClass="form-control form-control-sm"
												placeholder="Codice interno *" />
											<div>
												<s:fielderror fieldName="customer.internal_code" />
											</div>
										</div>
									</div>
								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Indirizzo
											sede legale</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.legal_address" value="Via/piazza..."
													cssClass="sr-only" /></small>
											<s:textfield key="customer.legal_address"
												cssClass="form-control form-control-sm"
												placeholder="Via/piazza..." />
											<div>
												<s:fielderror fieldName="customer.legal_address" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.legal_zip_code" value="CAP"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.legal_zip_code"
												cssClass="form-control form-control-sm" placeholder="CAP" />
											<div>
												<s:fielderror fieldName="customer.legal_zip_code" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.legal_province" value="Provincia"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.legal_province"
												cssClass="form-control form-control-sm" maxlength="2"
												placeholder="Provincia" />
											<div>
												<s:fielderror fieldName="customer.legal_province" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.legal_city" value="Città" cssClass="sr-only" /></small>
											<s:textfield key="customer.legal_city"
												cssClass="form-control form-control-sm" placeholder="Città" />
											<div>
												<s:fielderror fieldName="customer.legal_city" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.legal_nation" value="Nazione"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.legal_nation"
												cssClass="form-control form-control-sm"
												placeholder="Nazione" />
											<div>
												<s:fielderror fieldName="customer.legal_nation" />
											</div>
										</div>
									</div>

								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Indirizzo
											secondario</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_address" value="Via/piazza..."
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_address"
												cssClass="form-control form-control-sm"
												placeholder="Via/piazza..." />
											<div>
												<s:fielderror fieldName="customer.secondary_address" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_zip_code" value="CAP"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_zip_code"
												cssClass="form-control form-control-sm" placeholder="CAP" />
											<div>
												<s:fielderror fieldName="customer.secondary_zip_code" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_province" value="Provincia"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_province"
												cssClass="form-control form-control-sm" maxlength="2"
												placeholder="Provincia" />
											<div>
												<s:fielderror fieldName="customer.secondary_province" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_city" value="Città"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_city"
												cssClass="form-control form-control-sm" placeholder="Città" />
											<div>
												<s:fielderror fieldName="customer.secondary_city" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_nation" value="Nazione"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_nation"
												cssClass="form-control form-control-sm"
												placeholder="Nazione" />
											<div>
												<s:fielderror fieldName="customer.secondary_nation" />
											</div>
										</div>
									</div>

								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Contatti
											aziendali</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.primary_phone" value="Telefono primario"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.primary_phone"
												cssClass="form-control form-control-sm"
												placeholder="Telefono primario" />
											<div>
												<s:fielderror fieldName="customer.primary_phone" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_phone" value="Telefono secondario"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_phone"
												cssClass="form-control form-control-sm"
												placeholder="Telefono secondario" />
											<div>
												<s:fielderror fieldName="customer.secondary_phone" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.primary_email"
													value="Indirizzo e-mail primario" cssClass="sr-only" /></small>
											<s:textfield key="customer.primary_email"
												cssClass="form-control form-control-sm"
												placeholder="Indirizzo e-mail primario" />
											<div>
												<s:fielderror fieldName="customer.primary_email" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.secondary_email"
													value="Indirizzo e-mail secondario" cssClass="sr-only" /></small>
											<s:textfield key="customer.secondary_email"
												cssClass="form-control form-control-sm"
												placeholder="Indirizzo e-mail secondario" />
											<div>
												<s:fielderror fieldName="customer.secondary_email" />
											</div>
										</div>
									</div>

								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Fatturazione</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.sdi_code" value="Codice univoco/PEC"
													cssClass="sr-only" /></small>
											<s:textfield key="customer.sdi_code"
												cssClass="form-control form-control-sm"
												placeholder="Codice univoco/PEC" />
											<div>
												<s:fielderror fieldName="customer.sdi_code" />
											</div>
										</div>
									</div>
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="customer.pec_email" value="PEC" cssClass="sr-only" /></small>
											<s:textfield key="customer.pec_email"
												cssClass="form-control form-control-sm" placeholder="PEC" />
											<div>
												<s:fielderror fieldName="customer.pec_email" />
											</div>
										</div>
									</div>

								</div>
							</div>

						</div>

						<a href="customers" role="button" class="btn btn-danger btn-sm">Chiudi</a>
						<s:submit type="button" name="action_type" value="save"
							cssClass="btn btn-warning btn-sm">Salva</s:submit>
						<s:submit type="button" name="action_type" value="save_close"
							cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>

					</s:form>
				</div>
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>