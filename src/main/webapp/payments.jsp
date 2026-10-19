<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Metodi di
					pagamento</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-3"><a
							class="nav-link btn btn-primary btn-sm mr-1" role="button"
							href="payments/new"><i class="fas fa-plus"></i> Nuovo</a></li>
					</ul>
				</div>
				>

				<div id="accordion">

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-1">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-1" aria-expanded="true"
								aria-controls="card-1">
								<s:action name="payments/list" var="action">
								</s:action>
								Tutti i dati -
								<s:property value="#action.payments.size()" />
							</button>
						</div>
						<div id="card-1" class="collapse show" aria-labelledby="h-card-1"
							data-parent="#accordion">
							<div class="card-body">
								<s:action name="payments/list" executeResult="true">
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