<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h3 font-weight-bold border-bottom">Metodo di
					pagamento</div>

				<div class="pt-2 pb-3" id="form_payment">

					<s:form action="payments/save" method="post" theme="simple">

						<div class="py-3">
							<a href="payments" role="button" class="btn btn-danger btn-sm">Chiudi</a>
							<s:submit type="button" name="action_type" value="save"
								cssClass="btn btn-warning btn-sm">Salva</s:submit>
							<s:submit type="button" name="action_type" value="save_close"
								cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>
						</div>

						<s:hidden name="payment.id" />
						<s:hidden name="payment.admin.id" />

						<div class="form-row">

							<div class="form-group col-12 col-md-6 col-lg-6 col-xl-4">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Dati</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="payment.ptag" value="* Codice" cssClass="sr-only" /></small>
											<s:textfield key="payment.ptag"
												cssClass="form-control form-control-sm"
												placeholder="* Codice" />
											<div>
												<s:fielderror fieldName="payment.ptag" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="payment.description" value="* Descrizione"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.description"
												cssClass="form-control form-control-sm"
												placeholder="* Descrizione" />
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="payment.fixed_date" value="* Data manuale"
													cssClass="sr-only" /></small>
											<s:select key="payment.fixed_date"
												list="#{'NO':'NO', 'SI':'SI'}" id="fixed_date"
												cssClass="form-control form-control-sm" />
										</div>
									</div>

								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-6 col-xl-8">
								<div class="border bg-white shadow-sm rounded p-4 h-100"
									id="pinstallments">

									<small class="font-weight-bold"><label>Scadenze</label></small>

									<div class="form-row">
										<s:hidden key="payment.pinstallments[0].id" />
										<s:hidden key="payment.pinstallments[0].payment.id" />
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[0].days" value="* Giorni"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[0].days"
												id="pinst_days_0"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Giorni" />
											<div>
												<s:fielderror fieldName="payment.pinstallments[0].days" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[0].amount_perc" value="* Rata %"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[0].amount_perc"
												id="pinst_ap_0"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Rata %" />
											<div>
												<s:fielderror
													fieldName="payment.pinstallments[0].amount_perc" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<s:hidden key="payment.pinstallments[1].id" />
										<s:hidden key="payment.pinstallments[1].payment.id" />
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[1].days" value="* Giorni"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[1].days"
												id="pinst_days_1"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Giorni" />
											<div>
												<s:fielderror fieldName="payment.pinstallments[1].days" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[1].amount_perc" value="* Rata %"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[1].amount_perc"
												id="pinst_ap_1"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Rata %" />
											<div>
												<s:fielderror
													fieldName="payment.pinstallments[1].amount_perc" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<s:hidden key="payment.pinstallments[2].id" />
										<s:hidden key="payment.pinstallments[2].payment.id" />
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[2].days" value="* Giorni"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[2].days"
												id="pinst_days_2"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Giorni" />
											<div>
												<s:fielderror fieldName="payment.pinstallments[2].days" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[2].amount_perc" value="* Rata %"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[2].amount_perc"
												id="pinst_ap_2"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Rata %" />
											<div>
												<s:fielderror
													fieldName="payment.pinstallments[2].amount_perc" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<s:hidden key="payment.pinstallments[3].id" />
										<s:hidden key="payment.pinstallments[3].payment.id" />
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[3].days" value="* Giorni"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[3].days"
												id="pinst_days_3"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Giorni" />
											<div>
												<s:fielderror fieldName="payment.pinstallments[3].days" />
											</div>
										</div>
										<div class="form-group col-12 col-sm-12 col-md-6">
											<small class="font-weight-bold"><s:label
													for="payment.pinstallments[3].amount_perc" value="* Rata %"
													cssClass="sr-only" /></small>
											<s:textfield key="payment.pinstallments[3].amount_perc"
												id="pinst_ap_3"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Rata %" />
											<div>
												<s:fielderror
													fieldName="payment.pinstallments[3].amount_perc" />
											</div>
										</div>
									</div>



								</div>
							</div>

						</div>

						<a href="payments" role="button" class="btn btn-danger btn-sm">Chiudi</a>
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

