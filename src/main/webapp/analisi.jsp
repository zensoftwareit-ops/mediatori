<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Analisi</div>

				<div class="pt-2 pb-3">
					<ul class="nav nav-pills">
						<li class="nav-item mr-4"><a
							class="nav-link btn btn-primary btn-sm" role="button"
							href="analisi/new"><i class="fas fa-plus"></i> Nuovo</a></li>
							<li class="nav-item mr-3">
							<s:form cssClass="form-inline" method="get" action="analisi" theme="simple">
								<div class="form-group mr-1">
									<div>
										<sj:autocompleter key="id_customer" list="customers" listValue="business_name" listKey="id" />
									</div>
								</div>
								<div class="form-group mr-1">
									<s:select key="pagato" list="#{'all':'Tutti','SI':'Pagato','NO':'Non pagato'}" cssClass="from-control form-control-sm" />
								</div>
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-search"></i>
								</button>
							</s:form>
						</li>
					</ul>
				</div>

				<div id="accordion">

					<div class="card shadow-sm mb-1">
						<div class="card-header bg-white p-1" id="h-card-1">
							<button class="btn btn-link text-primary" data-toggle="collapse"
								data-target="#card-1" aria-expanded="true"
								aria-controls="card-1">
								<s:action name="analisi/list" var="action">
									<s:param name="s" value="null" />
								</s:action>
								Tutti i dati -
								<s:property value="#action.analisi.size()" />
							</button>
						</div>
						<s:if test="%{s==null || s.isEmpty()}">
							<div id="card-1" class="collapse show" aria-labelledby="h-card-1"
								data-parent="#accordion">
								<div class="card-body">
									<s:action name="analisi/list" executeResult="true">
										<s:param name="s" value="null" />
									</s:action>
								</div>
							</div>
						</s:if>
						<s:else>
							<div id="card-1" class="collapse" aria-labelledby="h-card-1"
								data-parent="#accordion">
								<div class="card-body">
									<s:action name="analisi/list" executeResult="true">
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
								<s:action name="analisi/list" var="action">
									<s:param name="s" value="%{s}" />
								</s:action>
								Ricerca -
								<s:property value="#action.analisi.size()" />
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
									<s:action name="analisi/list" executeResult="true">
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