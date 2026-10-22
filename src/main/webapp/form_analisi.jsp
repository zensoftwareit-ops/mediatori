<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h3 font-weight-bold border-bottom">Analisi</div>

				<div class="pt-2 pb-3" id="form_analisi">

					<s:form action="analisi/save" method="post" theme="simple">

						<div class="py-3">
							<a href="analisi" role="button" class="btn btn-danger btn-sm">Chiudi</a>
							<s:submit type="button" name="action_type" value="save"
								cssClass="btn btn-warning btn-sm">Salva</s:submit>
							<s:submit type="button" name="action_type" value="save_close"
								cssClass="btn btn-success btn-sm">Salva e chiudi</s:submit>
						</div>

						<s:hidden name="analisi.id" />

						<div class="form-row">

							<div class="form-group col-12">
								<div class="border bg-white shadow-sm rounded p-4 h-100">

									<small class="font-weight-bold"><label>Dati
											</label></small>

									<div class="form-row">
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.dataAnalisi" value="* Data analisi" /></small>
											<sj:datepicker key="analisi.dataAnalisi" displayFormat="d/m/y"
												cssClass="form-control form-control-sm" />
											<div><s:fielderror fieldName="analisi.dataAnalisi" /></div>
										</div>
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.cliente.id" value="* Cliente" /></small>
																			<div>
									<sj:autocompleter key="analisi.cliente.id" list="customers"
										listValue="business_name" listKey="id"
										/>
								</div>
											<div>
												<s:fielderror fieldName="analisi.cliente.id" />
											</div>
										</div>
									
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.prodotto.id" value="* Prodotto"
													 /></small>
												 <div>
													<s:select key="analisi.prodotto.id" list="products"
								listValue="name" listKey="id" headerKey="" headerValue="Seleziona prodotto"
								cssClass="form-control form-control-sm" /></div>
																					<div>
										<s:fielderror fieldName="analisi.prodotto.id" />
											</div>
										</div>
									
										<div class="form-group col-6">
											<small class="font-weight-bold"><s:label
													for="analisi.nCampione" value="Campione"
													/></small>
											<s:textfield key="analisi.nCampione"
												cssClass="form-control form-control-sm"
												placeholder="Campione" />
										</div>
									
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.lotto" value="Lotto"
													/></small>
											<s:textfield key="analisi.lotto"
												cssClass="form-control form-control-sm"
												placeholder="Lotto" />
										</div>
									
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.cadmio" value="Cadmio"
													/></small>
											<s:textfield key="analisi.cadmio"
											id="analisi_cadmio"
												cssClass="form-control form-control-sm"
												placeholder="Cadmio" />
										</div>
										
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.cadmioFl" value="Cadmio fuori limite"
													/></small>
										<s:select key="analisi.cadmioFl" id="analisi_cadmioFl" list="#{'NO':'NO','SI':'SI'}" disabled="true" cssClass="form-control form-control-sm" />
										</div>
								
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.arsenicoTot" value="Arsenico tot."
													/></small>
											<s:textfield key="analisi.arsenicoTot"
												cssClass="form-control form-control-sm"
												placeholder="Arsenico tot." />
										</div>
								
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.arsenicoInorganico" value="Arsenico inorg."
													/></small>
											<s:textfield key="analisi.arsenicoInorganico"
											id="analisi_arsenicoInorganico"
												cssClass="form-control form-control-sm"
												placeholder="Arsenico inorg." />
										</div>
										
										<div class="form-group col-4">
											<small class="font-weight-bold"><s:label
													for="analisi.arsenicoFl" value="Arsenico inorg. fuori limite"
													/></small>
										<s:select key="analisi.arsenicoFl" id="analisi_arsenicoFl" list="#{'NO':'NO','SI':'SI'}" disabled="true" cssClass="form-control form-control-sm" />
										</div>

									<div class="form-group col-4">
										<small class="font-weight-bold"><s:label
												for="analisi.nichel" value="Nichel"
												/></small>
										<s:textfield key="analisi.nichel"
											id="analisi_nichel"
											cssClass="form-control form-control-sm"
											placeholder="Nichel" />
									</div>

									<div class="form-group col-4">
										<small class="font-weight-bold"><s:label
												for="analisi.nichelFl" value="Nichel fuori limite"
												/></small>
										<s:select key="analisi.nichelFl" id="analisi_nichelFl" list="#{'NO':'NO','SI':'SI'}" disabled="true" cssClass="form-control form-control-sm" />
									</div>
									
										<div class="form-group col-6">
											<small class="font-weight-bold"><s:label
													for="analisi.altreAnalisi" value="Altre analisi"
													/></small>
											<s:textfield key="analisi.altreAnalisi"
												cssClass="form-control form-control-sm"
												placeholder="Altre analisi" />
										</div>
									
									
										<div class="form-group col-6">
											<small class="font-weight-bold"><s:label
													for="analisi.note" value="Note"
													/></small>
											<s:textfield key="analisi.note"
												cssClass="form-control form-control-sm"
												placeholder="Note" />
										</div>
									
										<div class="form-group col-6">
											<small class="font-weight-bold"><s:label
													for="analisi.costo" value="Costo"
													/></small>
											<s:textfield key="analisi.costo"
												value="%{86.00}" readonly="true"
												cssClass="form-control form-control-sm numeric"
												placeholder="Costo" />
										</div>
										
										<div class="form-group col-6">
											<small class="font-weight-bold"><s:label
													for="analisi.pagato" value="Pagato"
													/></small>
											<s:select key="analisi.pagato"
												cssClass="form-control form-control-sm"
												list="#{'NO':'NO','SI':'SI'}" />
										</div>
									</div>


								</div>
							</div>

							

						</div>

						<a href="analisi" role="button" class="btn btn-danger btn-sm">Chiudi</a>
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

<script>
(function() {
	function valoreNumerico(valore) {
		var testo = (valore || '').trim().replace(',', '.');
		var maggioreDi = testo.charAt(0) === '>';
		testo = testo.replace(/^[<>]=?\s*/, '');
		var numero = Number(testo);
		return {
			valido: testo !== '' && Number.isFinite(numero),
			numero: numero,
			maggioreDi: maggioreDi
		};
	}

	function aggiornaFuoriLimite(campoValore, campoFlag, limite) {
		var input = document.getElementById(campoValore);
		var flag = document.getElementById(campoFlag);
		if (!input || !flag) {
			return;
		}

		var valore = valoreNumerico(input.value);
		flag.value = valore.valido && (valore.numero > limite || (valore.maggioreDi && valore.numero === limite)) ? 'SI' : 'NO';
	}

	var controlli = [
		['analisi_cadmio', 'analisi_cadmioFl', 0.15],
		['analisi_arsenicoInorganico', 'analisi_arsenicoFl', 0.15],
		['analisi_nichel', 'analisi_nichelFl', 1.5]
	];

	controlli.forEach(function(controllo) {
		var input = document.getElementById(controllo[0]);
		if (input) {
			input.addEventListener('input', function() {
				aggiornaFuoriLimite(controllo[0], controllo[1], controllo[2]);
			});
			aggiornaFuoriLimite(controllo[0], controllo[1], controllo[2]);
		}
	});
})();
</script>

<%@include file="footer.jsp"%>

