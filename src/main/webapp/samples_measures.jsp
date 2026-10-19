<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>
		
		<s:date name="%{date_from}" format="dd/MM/yyyy" var="dfrom" />
		<s:date name="%{date_to}" format="dd/MM/yyyy" var="dto" />

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Campioni</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="post" action="samples_measures/print" theme="simple">
								<s:hidden key="id_customer" />
								<s:hidden key="id_product" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:checkbox key="col_customer" fieldValue="true" value="false"
									label="Nascondi colonna Cliente" />
								<small class="mr-1">&nbsp;Nascondi colonna Cliente</small>
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa PDF
								</button>
							</s:form></li>
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="post" action="samples_measures/xls" theme="simple">
								<s:hidden key="id_customer" />
								<s:hidden key="id_product" />
																<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:checkbox key="col_customer" fieldValue="true" value="false"
									label="Nascondi colonna Cliente" />
								<small class="mr-1">&nbsp;Nascondi colonna Cliente</small>
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fab-excel"></i> Scarica Excel
								</button>
							</s:form></li>
					</ul>
				</div>

				<div class="pt-2 pb-3" id="form_samples">
					<s:form action="samples_measures" method="get" theme="simple">
						<div class="form-row">
							<div class="form-group col-12 col-sm-12 col-md-6">
								<small class="font-weight-bold"><s:label
										for="id_customer" value="Cliente" /></small>
								<div>
									<sj:autocompleter key="id_customer" list="customers"
										listValue="business_name" listKey="id"
										onChangeTopics="customerChange" />
								</div>
							</div>
							<div class="form-group col-12 col-sm-12 col-md-6">
								<small class="font-weight-bold"><s:label
										for="id_product" value="Prodotto" /></small>
								<div>
									<sj:autocompleter key="id_product" list="products"
										listValue="name" listKey="id" onChangeTopics="productChange" />
								</div>
							</div>
								<div class="form-group mr-1">
									<sj:datepicker key="date_from" displayFormat="d/m/y" cssClass="form-control form-control-sm" placeholder="Dal..." />
								</div>
								<div class="form-group mr-1">
									<sj:datepicker key="date_to" displayFormat="d/m/y" cssClass="form-control form-control-sm" placeholder="Al..." />
								</div>
													<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-search"></i>
								</button>
						</div>
					</s:form>
				</div>

				<div id="accordion">

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-1">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-1" aria-expanded="true"
								aria-controls="card-1">
								<s:action name="samples_measures/list" var="action">
									<s:param name="id_customer" value="%{id_customer}"></s:param>
									<s:param name="id_product" value="%{id_product}"></s:param>
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
								</s:action>
								Tutti i dati -
								<s:property value="#action.measures.size()" />
							</button>
						</div>
						<div id="card-1" class="collapse show" aria-labelledby="h-card-1"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="samples_measures/list" executeResult="true">
									<s:param name="id_customer" value="%{id_customer}"></s:param>
									<s:param name="id_product" value="%{id_product}"></s:param>
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
								</s:action>
							</div>
						</div>

					</div>

				</div>

			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>