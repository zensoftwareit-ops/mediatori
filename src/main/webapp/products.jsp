<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Prodotti</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-4"><a
							class="nav-link btn btn-primary btn-sm" role="button"
							href="products/new"><i class="fas fa-plus"></i> Nuovo</a></li>
						<li class="nav-item mr-3"><s:form cssClass="form-inline"
								method="get" action="products" theme="simple">
								<div class="form-row">
									<div class="form-group">
										<s:textfield type="search" key="s"
											cssClass="form-control form-control-sm mr-1"
											placeholder="Cerca..." />
									</div>
								</div>
								<s:submit type="button" cssClass="btn btn-primary btn-sm">
									<i class="fas fa-search"></i>
								</s:submit>
							</s:form></li>
					</ul>
				</div>

				<div id="accordion">

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-1">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-1" aria-expanded="true"
								aria-controls="card-1">
								<s:action name="products/list" var="action">
									<s:param name="s" value="null" />
								</s:action>
								Tutti i dati -
								<s:property value="#action.products.size()" />
							</button>
						</div>
						<s:if test="%{s==null || s.isEmpty()}">
							<div id="card-1" class="collapse show" aria-labelledby="h-card-1"
								data-parent="#accordion">
								<div class="card-body">
									<s:action name="products/list" executeResult="true">
										<s:param name="s" value="null" />
									</s:action>
								</div>
							</div>
						</s:if>
						<s:else>
							<div id="card-1" class="collapse" aria-labelledby="h-card-1"
								data-parent="#accordion">
								<div class="card-body">
									<s:action name="products/list" executeResult="true">
										<s:param name="s" value="null" />
									</s:action>
								</div>
							</div>
						</s:else>

					</div>

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-2">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-2" aria-expanded="true"
								aria-controls="card-2">
								<s:action name="products/list" var="action">
									<s:param name="s" value="%{s}" />
								</s:action>
								Ricerca -
								<s:property value="#action.products.size()" />
							</button>
						</div>
						<s:if test="%{s==null || s.isEmpty()}">
							<div id="card-2" class="collapse" aria-labelledby="h-card-2"
								data-parent="#accordion_products">
								<div class="card-body">
									<s:action name="products/list" executeResult="true">
										<s:param name="s" value="%{s}" />
									</s:action>
								</div>
							</div>
						</s:if>
						<s:else>
							<div id="card-2" class="collapse show" aria-labelledby="h-card-2"
								data-parent="#accordion_products">
								<div class="card-body">
									<s:action name="products/list" executeResult="true">
										<s:param name="s" value="%{s}" />
									</s:action>
								</div>
							</div>
						</s:else>
					</div>

				</div>

			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>