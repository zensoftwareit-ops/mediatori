<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<s:date name="%{date_from}" format="dd/MM/yyyy" var="dfrom" />
		<s:date name="%{date_to}" format="dd/MM/yyyy" var="dto" />

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Provvigioni</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="get" action="commissions" theme="simple">
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
								<div class="form-group mr-1">
									<s:select key="billed_status" list="#{'not_billed':'Da fatturare','billed':'Già fatturate','all':'Tutte' }" cssClass="form-control form-control-sm" />
								</div>
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-search"></i>
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
								<s:action name="commissions/list" var="action">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="billed_status" value="%{billed_status}"/>
									<s:param name="customer_type">seller</s:param>
								</s:action>
								Venditori -
								<s:property value="#action.sales.size()" />
							</button>
						</div>
						<div id="card-1" class="collapse" aria-labelledby="h-card-1"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="commissions/list" executeResult="true">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="billed_status" value="%{billed_status}"/>
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
								<s:action name="commissions/list" var="action">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="billed_status" value="%{billed_status}"/>
									<s:param name="customer_type">buyer</s:param>
								</s:action>
								Acquirenti -
								<s:property value="#action.sales.size()" />
							</button>
						</div>
						<div id="card-2" class="collapse show" aria-labelledby="h-card-2"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="commissions/list" executeResult="true">
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="date_from" value="%{#dfrom}" />
									<s:param name="date_to" value="%{#dto}" />
									<s:param name="billed_status" value="%{billed_status}"/>
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

<div class="modal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
     
      <div class="modal-body">
        <iframe id="modal-sale"></iframe>
      </div>
     
    </div>
  </div>
</div>

<%@include file="footer.jsp"%>