<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">
		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Analisi</div>
				<div class="pt-2 pb-3">
					<div class="d-flex flex-wrap align-items-start">
						<a class="btn btn-primary btn-sm mr-3 mb-2" role="button" href="analisi/new">
							<i class="fas fa-plus"></i> Nuovo
						</a>
						<s:form cssClass="form-inline" method="get" action="analisi" theme="simple">
							<div class="form-group mr-1 mb-2">
								<sj:autocompleter key="id_customer" list="customers"
									listValue="business_name" listKey="id" placeholder="Cliente" />
							</div>
							<div class="form-group mr-1 mb-2">
								<sj:autocompleter key="id_product" list="products"
									listValue="name" listKey="id" placeholder="Riso" />
							</div>
							<div class="form-group dropdown mr-1 mb-2" id="analisiComuniDropdown">
								<button class="btn btn-light btn-sm border dropdown-toggle" type="button"
									id="analisiComuniButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									<span id="analisiComuniButtonText">Tutti i comuni</span>
								</button>
								<div class="dropdown-menu p-2 analisi-comuni-menu" aria-labelledby="analisiComuniButton"
									style="min-width: 260px;">
									<input type="text" id="analisiComuniSearch" class="form-control form-control-sm mb-2"
										placeholder="Cerca comune..." autocomplete="off" />
									<div id="analisiComuniOptions" style="max-height: 240px; overflow-y: auto;">
										<s:iterator value="comuni" var="comuneVoce">
											<label class="form-check mb-1 analisi-comune-opzione">
												<input class="form-check-input analisi-comune-checkbox" type="checkbox"
													name="comuni_selezionati" value='<s:property value="%{#comuneVoce}" />'
													<s:if test="%{comuniFiltro.contains(#comuneVoce)}">checked="checked"</s:if> />
												<span class="analisi-comune-label"><s:property value="%{#comuneVoce}" /></span>
											</label>
										</s:iterator>
									</div>
									<div class="dropdown-divider"></div>
									<button type="button" class="btn btn-link btn-sm p-0" id="analisiComuniClear">
										Deseleziona tutti
									</button>
								</div>
							</div>
							<div class="form-group mr-1 mb-2">
								<s:select name="fuori_limite"
									list="#{'':'Tutti gli esiti','TOTALE':'Totale solo fuori limite','CADMIO':'Solo cadmio fuori limite','ARSENICO':'Solo arsenico fuori limite','NICHEL':'Solo nichel fuori limite'}"
									cssClass="form-control form-control-sm" />
							</div>
							<div class="form-group mr-1 mb-2">
								<sj:datepicker key="date_from" displayFormat="d/m/y"
									cssClass="form-control form-control-sm" placeholder="Dal..." />
							</div>
							<div class="form-group mr-1 mb-2">
								<sj:datepicker key="date_to" displayFormat="d/m/y"
									cssClass="form-control form-control-sm" placeholder="Al..." />
							</div>
							<button type="submit" class="btn btn-primary btn-sm mb-2" title="Cerca">
								<i class="fas fa-search"></i>
							</button>
						</s:form>
					</div>
					<small class="text-muted">I dati mostrati appartengono all'annata di semina selezionata.</small>
				</div>

				<div class="card shadow-sm mb-1">
					<div class="card-header bg-white text-primary">Risultati analisi</div>
					<div class="card-body">
						<s:action name="analisi/list" executeResult="true">
							<s:param name="id_customer" value="%{id_customer}" />
							<s:param name="id_product" value="%{id_product}" />
							<s:param name="comuni_selezionati" value="%{comuni_selezionati}" />
							<s:param name="comune" value="%{comune}" />
							<s:param name="fuori_limite" value="%{fuori_limite}" />
							<s:param name="date_from" value="%{date_from}" />
							<s:param name="date_to" value="%{date_to}" />
						</s:action>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
$(function() {
	var $dropdown = $('#analisiComuniDropdown');
	var $search = $('#analisiComuniSearch');
	var $checkboxes = $('.analisi-comune-checkbox');

	function aggiornaTestoComuni() {
		var selezionati = $checkboxes.filter(':checked').length;
		$('#analisiComuniButtonText').text(selezionati > 0 ? 'Comuni selezionati: ' + selezionati : 'Tutti i comuni');
	}

	$('.analisi-comuni-menu').on('click', function(event) {
		event.stopPropagation();
	});

	$search.on('input', function() {
		var ricerca = $(this).val().toLowerCase().trim();
		$('.analisi-comune-opzione').each(function() {
			var testo = $(this).find('.analisi-comune-label').text().toLowerCase();
			$(this).toggle(testo.indexOf(ricerca) !== -1);
		});
	});

	$checkboxes.on('change', aggiornaTestoComuni);
	$('#analisiComuniClear').on('click', function() {
		$checkboxes.prop('checked', false);
		$search.val('').trigger('input');
		aggiornaTestoComuni();
	});
	$dropdown.on('shown.bs.dropdown', function() {
		$search.trigger('focus');
	});

	aggiornaTestoComuni();
});
</script>

<%@include file="footer.jsp"%>
