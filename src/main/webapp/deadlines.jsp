<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<s:date name="%{date_from}" format="dd/MM/yyyy" var="dfrom" />
		<s:date name="%{date_to}" format="dd/MM/yyyy" var="dto" />

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Scadenze</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="get" action="deadlines" theme="simple">
								<div class="form-group mr-1">
									<div>
										<sj:autocompleter key="id_customer" list="customers"
											listValue="business_name" listKey="id" />
									</div>
								</div>
								<div class="form-group mr-1">
									<sj:datepicker key="date_from" displayFormat="d/m/y"
										cssClass="form-control form-control-sm" placeholder="Dal..." />
								</div>
								<div class="form-group mr-1">
									<sj:datepicker key="date_to" displayFormat="d/m/y"
										cssClass="form-control form-control-sm" placeholder="Al..." />
								</div>
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-search"></i>
								</button>
							</s:form></li>
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="post" action="deadlines/print" theme="simple">
								<s:hidden key="id_customer" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:hidden key="customer_type" value="seller" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa venditori
								</button>
							</s:form></li>
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="post" action="deadlines/print" theme="simple">
								<s:hidden key="id_customer" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:hidden key="customer_type" value="buyer" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa acquirenti
								</button>
							</s:form></li>
					</ul>

				</div>

				<div id="accordion">

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-1">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-1" aria-expanded="true"
								aria-controls="card-1">
								<s:action name="deadlines/list" var="action">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="customer_type">seller</s:param>
								</s:action>
								Venditori -
								<s:property value="#action.sales.size()" />
							</button>
						</div>
						<div id="card-1" class="collapse" aria-labelledby="h-card-1"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="deadlines/list" executeResult="true">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="customer_type">seller</s:param>
								</s:action>
							</div>
						</div>
					</div>

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-2">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-2" aria-expanded="true"
								aria-controls="card-2">
								<s:action name="deadlines/list" var="action">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="customer_type">buyer</s:param>
								</s:action>
								Acquirenti -
								<s:property value="#action.sales.size()" />
							</button>
						</div>
						<div id="card-2" class="collapse show" aria-labelledby="h-card-2"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="deadlines/list" executeResult="true">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="customer_type">buyer</s:param>
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