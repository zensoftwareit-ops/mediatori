<%@ taglib prefix="s" uri="/struts-tags"%>

<ul class="nav nav-pills">

						<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2"
								method="post" action="analisi/print" theme="simple" id="form-comm-buyer">
								<s:hidden key="id_customer" />
								<s:hidden key="id_product" />
								<s:hidden key="pagato" />
								<s:hidden key="comune" />
								<s:hidden key="fuori_limite" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa analisi
								</button>
							</s:form></li>
							</ul>

<div class="table-responsive">
	<table class="table table-sm table-hover">
		<thead>
			<tr>
				<th scope="col">Data</th>
				<th scope="col">Campione</th>
				<th scope="col">Cliente</th>
				<th scope="col">Riso</th>
				<th scope="col">Comune</th>
				<th scope="col">Cadmio</th>
				<th scope="col">Arsenico tot</th>
				<th scope="col">Arsenico inorg.</th>
				<th scope="col">Nichel</th>
				<th scope="col">Costo</th>
				<th scope="col">Pagato</th>
				<th scope="col">Esito</th>
				<th scope="col">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{analisi}" begin="%{begin}" end="%{end}">
				<s:if test="%{nonValida}"><tr class="table-danger"></s:if>
				<s:else><tr></s:else>
					<td><s:date name="dataAnalisi" format="dd/MM/yyyy" /></td>
					<th scope="row"><s:property value="%{nCampione}" /></th>
					<td><s:property value="%{cliente.business_name}" /></td>
					<td><s:property value="%{prodotto.name}" /></td>
					<td><s:property value="%{cliente.legal_city}" /></td>
					<td>
					<s:if test="%{cadmioFuoriLimite}">
						<span class="text-danger"><s:property value="%{cadmio}" /></span>
					</s:if>
					<s:else>
						<s:property value="%{cadmio}" />
					</s:else>
					</td>
					<td><s:property value="%{arsenicoTot}" /></td>
					<td>
					<s:if test="%{arsenicoFuoriLimite}">
						<span class="text-danger"><s:property value="%{arsenicoInorganico}" /></span>
					</s:if>
					<s:else>
						<s:property value="%{arsenicoInorganico}" />
					</s:else>
					</td>
					<td>
					<s:if test="%{nichelFuoriLimite}">
						<span class="text-danger"><s:property value="%{nichel}" /></span>
					</s:if>
					<s:else>
						<s:property value="%{nichel}" />
					</s:else>
					</td>
					<td class="text-right"><s:text name="format.money">
						<s:param value="%{costo}" />
					</s:text></td>
					<td><s:property value="%{pagato}" /></td>
					<td>
						<s:if test="%{nonValida}"><span class="badge badge-danger">NON VALIDA</span></s:if>
						<s:else><span class="badge badge-success">VALIDA</span></s:else>
					</td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="analisi/manage/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm"><i
									class="fas fa-pencil-alt"></i></a></li>
							<li class="nav-item m-1"><a
								href="analisi/delete/<s:property value="%{id}" />"
								onclick="return confirm('Eliminare definitivamente questa analisi?');"
								role="button" class="btn btn-danger btn-sm" title="Elimina"><i
									class="fas fa-trash"></i></a></li>
						</ul>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<s:if test="%{!riepilogoComuni.isEmpty()}">
	<div class="mt-4">
		<h5>Riepilogo per comune</h5>
		<div class="table-responsive">
			<table class="table table-sm table-bordered">
				<thead class="thead-light">
					<tr>
						<th>Comune</th><th>Analisi</th><th>Non valide</th>
						<th>Cadmio fuori limite</th><th>Arsenico fuori limite</th><th>Nichel fuori limite</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="%{riepilogoComuni}">
						<tr>
							<th><s:property value="%{comune}" /></th>
							<td><s:property value="%{totale}" /></td>
							<s:if test="%{nonValide > 0}"><td class="text-danger font-weight-bold"></s:if>
							<s:else><td></s:else>
								<s:property value="%{nonValide}" /> (<s:property value="%{percentualeNonValide}" />)
							</td>
							<s:if test="%{cadmioFuoriLimite > 0}"><td class="text-danger font-weight-bold"></s:if><s:else><td></s:else>
								<s:property value="%{cadmioFuoriLimite}" /> (<s:property value="%{percentualeCadmio}" />)</td>
							<s:if test="%{arsenicoFuoriLimite > 0}"><td class="text-danger font-weight-bold"></s:if><s:else><td></s:else>
								<s:property value="%{arsenicoFuoriLimite}" /> (<s:property value="%{percentualeArsenico}" />)</td>
							<s:if test="%{nichelFuoriLimite > 0}"><td class="text-danger font-weight-bold"></s:if><s:else><td></s:else>
								<s:property value="%{nichelFuoriLimite}" /> (<s:property value="%{percentualeNichel}" />)</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</s:if>

<%@include file="paginator.jsp"%>
