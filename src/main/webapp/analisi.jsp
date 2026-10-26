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
							<div class="form-group mr-1 mb-2">
								<s:select name="comuni_selezionati" list="comuni" multiple="true" size="3"
									cssClass="form-control form-control-sm" title="Ctrl+clic per selezionare più comuni" />
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

<%@include file="footer.jsp"%>
