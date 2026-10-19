<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h3 font-weight-bold border-bottom">Anagrafica
					prodotto</div>

				<div class="pt-2 pb-3" id="form_product">

					<s:form action="products/save" method="post" theme="simple">

						<div class="py-3">
							<a href="products" role="button" class="btn btn-danger btn-sm">Chiudi</a>
							<s:submit type="button" name="action_type" value="save"
								cssClass="btn btn-warning btn-sm">Salva</s:submit>
							<s:submit type="button" name="action_type" value="save_close"
								cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>
						</div>

						<s:hidden name="product.id" />
						<s:hidden name="product.admin.id" />

						<div class="form-row">

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Dati
											anagrafici</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.name" value="* Nome" cssClass="sr-only" /></small>
											<s:textfield key="product.name"
												cssClass="form-control form-control-sm" placeholder="* Nome" />
											<div>
												<s:fielderror fieldName="product.name" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.internal_code" value="Codice interno"
													cssClass="sr-only" /></small>
											<s:textfield key="product.internal_code"
												cssClass="form-control form-control-sm"
												placeholder="Codice interno" />
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.description" value="Descrizione"
													cssClass="sr-only" /></small>
											<s:textarea key="product.description"
												cssClass="form-control form-control-sm product_template"
												placeholder="Descrizione" />
										</div>
									</div>
									
									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.groups" value="Gruppo merceologico"
													cssClass="sr-only" /></small>
<%-- 											<s:select key="product.category" list="#{'RISONE':'RISONE','RISO':'RISO','MAIS_SECCO':'MAIS SECCO','MAIS_VERDE':'MAIS VERDE','SOIA_SECCA':'SOIA SECCA','SOIA_VERDE':'SOIA VERDE','GRANO_TENERO':'GRANO TENERO','GRANO_DURO':'GRANO DURO','ALTRO':'ALTRO'}" cssClass="form-control form-control-sm" /> --%>
											<s:select key="product.groups" list="groups" listKey="tag" listValue="listName" cssClass="form-control form-control-sm" />
										</div>
									</div>

								</div>
							</div>

							<div class="form-group col-12 col-md-6 col-lg-4 col-xl-3">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Dati
											commerciali</label></small>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.vat" value="* IVA (%)" cssClass="sr-only" /></small>
											<s:textfield key="product.vat"
												cssClass="form-control form-control-sm numeric"
												placeholder="* IVA (%)" />
											<div>
												<s:fielderror fieldName="product.vat" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.unit_of_measure"
													value="* Unit&agrave; di misura" cssClass="sr-only" /></small>
											<s:select key="product.unit_of_measure"
												list="#{'T':'(T) Tonnellate'}"
												cssClass="form-control form-control-sm" />
											<div>
												<s:fielderror fieldName="product.unit_of_measure" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.buyer_commission" value="* Provv. Acquirente"
													cssClass="sr-only" /></small>
											<s:textfield key="product.buyer_commission"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Provv. Acquirente" />
											<div>
												<s:fielderror fieldName="product.buyer_commission" />
											</div>
										</div>
									</div>

									<div class="form-row">
										<div class="form-group col-12">
											<small class="font-weight-bold"><s:label
													for="product.seller_commission" value="* Provv. Venditore"
													cssClass="sr-only" /></small>
											<s:textfield key="product.seller_commission"
												cssClass="form-control form-control-sm numeric"
												placeholder="* Provv. Venditore" />
											<div>
												<s:fielderror fieldName="product.seller_commission" />
											</div>
										</div>
									</div>

								</div>
							</div>

						</div>

						<a href="products" role="button" class="btn btn-danger btn-sm">Chiudi</a>
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

