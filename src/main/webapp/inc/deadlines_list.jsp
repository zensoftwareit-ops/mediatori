<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>

<style>
.ui-datepicker-trigger {
	display: none;
}
</style>

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
				<th scope="col">Fatture</th>
				<th scope="col">Scadenze</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{sales}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{number}" /></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td><s:date name="%{sale_date}" format="dd/MM/yyyy" /></td>
					<td width="25%"><s:property value="%{seller.business_name}" /></td>
					<td width="25%"><s:property value="%{buyer.business_name}" /></td>
					<td>
						<ul>
						<s:if test="%{!invoice_deposit_desc.isEmpty() || invoice_deposit_date!=null || invoice_deposit_amount>0}">
							<li>
							<div>
								Fattura (acconto) 
								<s:property value="%{invoice_deposit_desc}" />
								del
								<s:date format="dd/MM/yyyy" name="%{invoice_deposit_date}" />
								di
								<s:text name="format.money">
									<s:param value="%{invoice_deposit_amount}" />
								</s:text>
							</div>
							</li>
						</s:if>
						
						<s:if test="%{!invoice_balance_desc.isEmpty() || invoice_balance_date!=null || invoice_balance_amount>0}">
							<li>
							<div>
								Fattura
								<s:property value="%{invoice_balance_desc}" />
								del
								<s:date format="dd/MM/yyyy" name="%{invoice_balance_date}" />
								di
								<s:text name="format.money">
									<s:param value="%{invoice_balance_amount}" />
								</s:text>
							</div>
							</li>
						</s:if>
						
						<s:if test="%{!invoice_extra1_desc.isEmpty() || invoice_extra1_date!=null || invoice_extra1_amount>0}">
							<li>
							<div>
								Fattura (ulteriore) 
								<s:property value="%{invoice_extra1_desc}" />
								del
								<s:date format="dd/MM/yyyy" name="%{invoice_extra1_date}" />
								di
								<s:text name="format.money">
									<s:param value="%{invoice_extra1_amount}" />
								</s:text>
							</div>
							</li>
						</s:if>
						
						<s:if test="%{!invoice_extra2_desc.isEmpty() || invoice_extra2_date!=null || invoice_extra2_amount>0}">
							<li>
							<div>
								Fattura (ulteriore) 
								<s:property value="%{invoice_extra2_desc}" />
								del
								<s:date format="dd/MM/yyyy" name="%{invoice_extra2_date}" />
								di
								<s:text name="format.money">
									<s:param value="%{invoice_extra2_amount}" />
								</s:text>
							</div>
							</li>
						</s:if>
						</ul>
					</td>
					<td><s:iterator value="%{installments}" var="inst">
							<s:if test="%{date_from!=null && date_to!=null}">
<%-- 								<s:if test="%{date_to.compareTo(#inst.expire_date)>=0 && date_from.compareTo(#inst.expire_date)<=0}"> --%>
								<s:if test="%{(date_to.after(#inst.expire_date) || date_to.compareTo(#inst.expire_date)==0) && (date_from.before(#inst.expire_date) || date_from.compareTo(#inst.expire_date)==0)}">
									<div>
										<s:if test="%{#inst.payed.equals('SI')}">
											<span class="text-success"><i class="fas fa-circle"></i></span>
										</s:if>
										<s:else>
											<span class="text-danger"><i class="fas fa-circle"></i></span>
										</s:else>
										<b><s:date name="#inst.expire_date" format="dd/MM/yyyy" /></b>
										(
										<s:text name="format.money">
											<s:param value="#inst.amount" />
										</s:text>
										)
									</div>
								</s:if>
							</s:if>
							<s:elseif test="%{date_from!=null}">
<%-- 								<s:if test="%{date_from.compareTo(#inst.expire_date)<=0}"> --%>
								<s:if test="date_from.before(#inst.expire_date) || date_from.compareTo(#inst.expire_date)==0">
									<div>
										<s:if test="%{#inst.payed.equals('SI')}">
											<span class="text-success"><i class="fas fa-circle"></i></span>
										</s:if>
										<s:else>
											<span class="text-danger"><i class="fas fa-circle"></i></span>
										</s:else>
										<b><s:date name="#inst.expire_date" format="dd/MM/yyyy" /></b>
										(
										<s:text name="format.money">
											<s:param value="#inst.amount" />
										</s:text>
										)
									</div>
								</s:if>
							</s:elseif>
							<s:elseif test="%{date_to!=null}">
<%-- 								<s:if test="%{date_to.compareTo(#inst.expire_date)>=0}"> --%>
								<s:if test="date_to.after(#inst.expire_date) || date_to.compareTo(#inst.expire_date)==0">
									<div>
										<s:if test="%{#inst.payed.equals('SI')}">
											<span class="text-success"><i class="fas fa-circle"></i></span>
										</s:if>
										<s:else>
											<span class="text-danger"><i class="fas fa-circle"></i></span>
										</s:else>
										<b><s:date name="#inst.expire_date" format="dd/MM/yyyy" /></b>
										(
										<s:text name="format.money">
											<s:param value="#inst.amount" />
										</s:text>
										)
									</div>
								</s:if>
							</s:elseif>

							<s:else>
								<div>
									<s:if test="%{#inst.payed.equals('SI')}">
										<span class="text-success"><i class="fas fa-circle"></i></span>
									</s:if>
									<s:else>
										<span class="text-danger"><i class="fas fa-circle"></i></span>
									</s:else>
									<b><s:date name="#inst.expire_date" format="dd/MM/yyyy" /></b>
									(
									<s:text name="format.money">
										<s:param value="#inst.amount" />
									</s:text>
									)
								</div>
							</s:else>
						</s:iterator></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>