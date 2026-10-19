<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="table-responsive">
	<table class="table table-hover table-sm">
		<thead>
			<tr>
				<th scope="col">#</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Cliente</th>
				<th scope="col">Data fattura</th>
				<th scope="col">Data scadenza</th>
				<th scope="col" class="text-right">Importo (&euro;)</th>
				<th scope="col" class="text-right">IVA (&euro;)</th>
				<th scope="col" class="text-right">Totale (&euro;)</th>
				<th scope="col">Pagata</th>
				<th scope="col">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{invoices}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{number}" /></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td width="25%"><s:property value="%{customer.business_name}" /></td>
					<td><s:date name="%{invoice_date}" format="dd/MM/yyyy" /></td>
					<td><s:date name="%{expire_date}" format="dd/MM/yyyy" /></td>
					<td class="text-right"><s:text name="format.money">
							<s:param value="%{taxable_total}" />
						</s:text></td>
					<td class="text-right"><s:text name="format.money">
							<s:param value="%{vat_total}" />
						</s:text></td>
					<td class="text-right"><s:text name="format.money">
							<s:param value="%{total}" />
						</s:text></td>
					<td>
						<s:form method="post" action="invoices/set-payed/%{id}" cssClass="form-inline m-1" theme="simple">
							<div class="form-group mr-1">
								<s:select key="payed" value="%{payed}" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
							</div>
							<s:submit type="button" cssClass="btn btn-primary btn-sm"><i class="fas fa-save"></i></s:submit>
						</s:form>
					</td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="invoices/create-pdf/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm no-loader"><i
									class="fas fa-file-pdf"></i> Fattura</a></li>
							<li class="nav-item m-1">
								<button type="button" class="btn btn-primary btn-sm"
									onclick="$('#details_<s:property value="%{id}" />').toggle()">
									<i class="fas fa-eye"></i> Dettaglio
								</button>
							</li>
							<li class="nav-item m-1"><a
								href="invoices/delete/<s:property value="%{id}" />?itype=<s:property value="%{customer_type}" />" role="button"
								class="btn btn-danger btn-sm delete-invoice"><i
									class="fas fa-trash"></i></a></li>
						</ul>
					</td>
				</tr>
				<tr style="display: none;" id="details_<s:property value="%{id}" />">
					<td></td>
					<td colspan="2">
						<table class="table table-sm">
							<thead>
								<tr>
									<th>Vendita</th>
									<th>Cliente</th>
									<th>Prodotto</th>
									<th></th>
								</tr>
							</thead>
							<s:iterator value="%{products}">
								<tr>
									<td><b><s:property value="%{sale.number}" /> <s:property
												value="%{sale.year.year}" /></b></td>
									<td><s:property value="%{sale.buyer.business_name}" /></td>
									<td><s:property value="%{name}" /></td>
									<td class="text-right"><s:text name="format.money">
											<s:param value="%{commission_amount}" />
										</s:text></td>
								</tr>
							</s:iterator>
						</table>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>