<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>

<s:if test="%{customer_type.equals('buyer')}">
	<ul class="nav nav-pills">

						<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2"
								method="post" action="commissions/print" theme="simple" id="form-comm-buyer">
								<s:hidden key="id_customer" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:hidden key="billed_status" />
								<s:hidden key="customer_type" value="buyer" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa acquirenti
								</button>
							</s:form></li>
													
<%-- 						<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2" --%>
<%-- 								method="post" action="commissions/xls" theme="simple" id="form-comm-buyer-excel"> --%>
<%-- 								<s:hidden key="id_customer" /> --%>
<%-- 								<s:hidden key="date_from" /> --%>
<%-- 								<s:hidden key="date_to" /> --%>
<%-- 								<s:hidden key="billed_status" /> --%>
<%-- 								<s:hidden key="customer_type" value="buyer" /> --%>
<!-- 								<button type="submit" class="btn btn-primary btn-sm mr-1"> -->
<!-- 									<i class="fab fa-excel"></i> Excel acquirenti -->
<!-- 								</button> -->
<%-- 							</s:form></li> --%>
		<li class="nav-item mr-3">
			<s:form method="post" action="invoices/from-buyer/%{id_customer}" theme="simple" cssClass="form-inline mb-2" id="form-invoice-buyer">
				<s:hidden key="date_from" />
				<s:hidden key="date_to" />
				<div class="form-group mx-1">
					<small class="font-weight-bold"><s:label for="expire_date" value="Scadenza" /></small>
					<sj:datepicker key="expire_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" value="today" />
				</div>
				<div class="form-group mx-1">
					<small class="font-weight-bold"><s:label for="no_vat" value="Esente IVA" /></small>
					<s:select key="no_vat" list="#{'NO':'NO','SI':'SI'}" cssClass="form-control form-control-sm" />
				</div>
				<div class="form-group mx-1">
					<small class="font-weight-bold"><s:label for="expire_date" value="Numero pers." /></small>
					<s:textfield key="custom_number" cssClass="form-control form-control-sm" />
				</div>
				<s:submit type="button" cssClass="btn btn-primary btn-sm"><i class="fas fa-share"></i> Unisci in fattura</s:submit>
			</s:form>
		</li>
		<li class="nav-item mr-3">
			<s:form method="post" action="invoices/set-billed-buyer/%{id_customer}" theme="simple" cssClass="form-inline mb-2" id="form-billed-buyer">
				<s:hidden key="date_from" />
				<s:hidden key="date_to" />
				<s:hidden key="id_customer" />
				<s:submit type="button" cssClass="btn btn-primary btn-sm"> <i class="fas fa-times"></i> Escludi da fatturazione</s:submit>
			</s:form>
		</li>
	</ul>


</s:if>
<s:elseif test="%{customer_type.equals('seller')}">
	<ul class="nav nav-pills">
			<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2"
								method="post" action="commissions/print" theme="simple" id="form-comm-seller">
								<s:hidden key="id_customer" />
								<s:hidden key="date_from" />
								<s:hidden key="date_to" />
								<s:hidden key="billed_status" />
								<s:hidden key="customer_type" value="seller" />
								<button type="submit" class="btn btn-primary btn-sm mr-1">
									<i class="fas fa-print"></i> Stampa venditori
								</button>
							</s:form></li>
<%-- 							<li class="nav-item mr-3"><s:form cssClass="form-inline mb-2" --%>
<%-- 								method="post" action="commissions/xls" theme="simple" id="form-comm-seller-excel"> --%>
<%-- 								<s:hidden key="id_customer" /> --%>
<%-- 								<s:hidden key="date_from" /> --%>
<%-- 								<s:hidden key="date_to" /> --%>
<%-- 								<s:hidden key="billed_status" /> --%>
<%-- 								<s:hidden key="customer_type" value="seller" /> --%>
<!-- 								<button type="submit" class="btn btn-primary btn-sm mr-1"> -->
<!-- 									<i class="fab fa-excel"></i> Excel venditori -->
<!-- 								</button> -->
<%-- 							</s:form></li> --%>
		<li class="nav-item mr-3">
			<s:form method="post" action="invoices/from-seller/%{id_customer}" theme="simple" cssClass="form-inline mb-2" id="form-invoice-seller">
				<s:hidden key="date_from" />
				<s:hidden key="date_to" />
				<div class="form-group mx-1">
					<small class="font-weight-bold"><s:label for="expire_date" value="* Scadenza" /></small>
					<sj:datepicker key="expire_date" displayFormat="d/m/y" cssClass="form-control form-control-sm" value="today" />
				</div>
				<s:submit type="button" cssClass="btn btn-primary btn-sm"><i class="fas fa-share"></i> Unisci in fattura</s:submit>
			</s:form>
		</li>
		<li class="nav-item mr-3">
			<s:form method="post" action="invoices/set-billed-seller/%{id_customer}" theme="simple" cssClass="form-inline mb-2" id="form-billed-seller">
				<s:hidden key="date_from" />
				<s:hidden key="date_to" />
				<s:hidden key="id_customer" />
				<s:submit type="button" cssClass="btn btn-primary btn-sm"><i class="fas fa-times"></i> Escludi da fatturazione</s:submit>
			</s:form>
		</li>
	</ul>
</s:elseif>


<div class="table-responsive">
	<table class="table table-hover table-sm">
		<thead>
			<tr>
				<th scope="col">#</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Data vendita</th>
				<th scope="col">Venditore</th>
				<th scope="col">Acquirente</th>
				<th scope="col" class="text-right">Provvigione</th>
				<th scope="col" class="text-right">Quantità</th>
				<th scope="col" class="text-right">Tot. provvigione</th>
				<th scope="col">
					
					<s:if test="%{customer_type.equals('buyer')}">
						<input type="checkbox" id="check-all-buyer" checked>
					</s:if>
					<s:elseif test="%{customer_type.equals('seller')}">
						<input type="checkbox" id="check-all-seller" checked>
					</s:elseif>
					Seleziona 
				</th>
			</tr>
		</thead>
		<tbody>
			<s:set var="total" value="0" />
			<s:set var="total_euro" value="0" />
			<s:iterator value="%{commissions}"  var="sc">
			
<%-- 				<s:iterator value="%{#s.commissions}" var="sc"> --%>
				<tr>
					<s:if test="%{customer_type.equals('buyer')}">
						<th scope="row"><a class="sale-modal-btn no-loader" href="sales/manage/<s:property value="#sc.sale.id" />?screen=modal"><s:property value="#sc.sale.number" /></a></th>
						<s:if test="%{#session.admin.level.matches('admin|super')}">
							<td><s:property value="%{#sc.sale.admin.username}" /></td>
						</s:if>
						<td><s:date name="#sc.sale.sale_date" format="dd/MM/yyyy" /></td>
						<td width="20%"><s:property value="#sc.sale.seller.business_name" /></td>
						<td width="20%"><s:property value="#sc.sale.buyer.business_name" /></td>
						<td class="text-right"><s:text name="format.money">
								<s:param value="#sc.buyer_commission" />
							</s:text></td>
						<td class="text-right">
							<s:text name="format.number">
								<s:param value="#sc.product_amount" />
							</s:text>
							
						</td>
						<td class="text-right"><s:text name="format.money">
								<s:param value="#sc.buyer_commission_amount" />
							</s:text></td>
						<td>
							<table>
								<tr>
								<s:if test="%{#sc.billed_buyer.equals('NO')}">
									<td>
										<input type="checkbox" class="into-invoice-buyer" data-value="<s:property value="#sc.id" />" checked>
									</td>
								</s:if>
								<s:elseif test="%{#sc.billed_buyer.equals('SI')}">
									<td>
										<b>Già fatturata/esclusa</b>
									</td>
								</s:elseif>
								</tr>

							</table>

						</td>
						<s:set var="total"
							value="%{#total+#sc.product_amount}" />
						<s:set var="total_euro"
							value="%{#total_euro+#sc.buyer_commission_amount}" />
					</s:if>
					<s:elseif test="%{customer_type.equals('seller')}">
						<th scope="row"><a class="sale-modal-btn no-loader" href="sales/manage/<s:property value="#sc.sale.id" />?screen=modal"><s:property value="#sc.sale.number" /></a></th>
						<s:if test="%{#session.admin.level.matches('admin|super')}">
							<td><s:property value="%{#sc.sale.admin.username}" /></td>
						</s:if>
						<td><s:date name="#sc.sale.sale_date" format="dd/MM/yyyy" /></td>
						<td width="20%"><s:property value="#sc.sale.seller.business_name" /></td>
						<td width="20%"><s:property value="#sc.sale.buyer.business_name" /></td>
						<td class="text-right"><s:text name="format.money">
								<s:param value="#sc.seller_commission" />
							</s:text></td>
							
						<td class="text-right">
							<s:text name="format.number">
								<s:param value="#sc.product_amount" />
							</s:text>
							
						</td>
						<td class="text-right"><s:text name="format.money">
								<s:param value="#sc.seller_commission_amount" />
							</s:text></td>
						<td>
							<table>
								<tr>
								<s:if test="%{#sc.billed_seller.equals('NO')}">
									<td>
										<input type="checkbox" class="into-invoice-seller" data-value="<s:property value="#sc.id" />" checked>
									</td>
								</s:if>
								<s:elseif test="%{#sc.billed_seller.equals('SI')}">
									<td>
										<b>Già fatturata/esclusa</b>
									</td>
								</s:elseif>
								</tr>
								
							</table>

						</td>
						<s:set var="total"
							value="%{#total+#sc.product_amount}" />
							<s:set var="total_euro"
							value="%{#total_euro+#sc.seller_commission_amount}" />
					</s:elseif>
				</tr>
<%-- 				</s:iterator> --%>

			</s:iterator>
		</tbody>
		<tbody>
			<tr>
				<td colspan="5">TOTALE</td>
				<td class="text-right"><s:text name="format.number">
						<s:param value="%{#total}" />
					</s:text></td>
				<td class="text-right"><s:text name="format.money">
						<s:param value="%{#total_euro}" />
					</s:text></td>
			</tr>
		</tbody>
	</table>
</div>



