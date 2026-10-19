<%@ taglib prefix="s" uri="/struts-tags"%>

<ul class="nav nav-pills">

						<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2"
								method="post" action="analisi/print" theme="simple" id="form-comm-buyer">
								<s:hidden key="id_customer" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa analisi
								</button>
							</s:form></li>
							</ul>

<div class="table-responsive">
	<table class="table table-sm table-hover">
		<thead>
			<tr>
				<th scope="col">Campione</th>
				<th scope="col">Cliente</th>
				<th scope="col">Cadmio</th>
				<th scope="col">Arsenico tot</th>
				<th scope="col">Arsenico inorg.</th>
				<th scope="col">Nichel</th>
				<th scope="col">Costo</th>
				<th scope="col">Pagato</th>
				<th scope="col">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{analisi}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{nCampione}" /></th>
					<td><s:property value="%{cliente.business_name}" /></td>
					<td>
					<s:if test="%{cadmioFl != null && cadmioFl.equals('SI')}">
						<span class="text-danger"><s:property value="%{cadmio}" /></span>
					</s:if>
					<s:else>
						<s:property value="%{cadmio}" />
					</s:else>
					</td>
					<td><s:property value="%{arsenicoTot}" /></td>
					<td>
					<s:if test="%{arsenicoFl != null && arsenicoFl.equals('SI')}">
						<span class="text-danger"><s:property value="%{arsenicoInorganico}" /></span>
					</s:if>
					<s:else>
						<s:property value="%{arsenicoInorganico}" />
					</s:else>
					</td>
					<td>
					<s:if test="%{nichelFl != null && nichelFl.equals('SI')}">
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
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="analisi/manage/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm"><i
									class="fas fa-pencil-alt"></i></a></li>
							
						</ul>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>
