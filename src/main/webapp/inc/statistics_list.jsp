<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>


<div class="table-responsive">
	<table class="table table-hover table-sm">
		<thead>
			<tr>
				<th scope="col">#</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Data vendita</th>
				<th scope="col">Prodotto</th>
				<th scope="col" class="text-right">Quantità</th>
				<th scope="col" class="text-right">Prezzo</th>
				<th scope="col" class="text-right">Resa grani interi</th>
			</tr>
		</thead>
		<tbody>
		
			<s:set var="total_qta" value="0" />
			<s:set var="total_price" value="0" />
			<s:set var="total_yield" value="0" />
			
			<s:iterator value="%{sales}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><a href="sales/manage/<s:property value="%{id}" />"><s:property value="%{number}" /></a></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td><s:date name="%{sale_date}" format="dd/MM/yyyy" /></td>
					<td width="25%"><s:property value="%{product.name}" /></td>
					<td class="text-right">
						<s:text name="format.number">
							<s:param value="%{product.product_amount}" />
						</s:text>
					</td>
					<td class="text-right">
						<s:text name="format.money">
							<s:param value="%{product.total}" />
						</s:text>
					</td>
					<td class="text-right">
						<s:text name="format.number">
							<s:param value="%{product.grains_yield}" />
						</s:text>
					</td>
					
					<s:set var="total_qta" value="%{#total_qta+product.product_amount}" />
					<s:set var="total_price" value="%{#total_price+product.total}" />
					<s:set var="total_yield" value="%{#total_yield+(product.grains_yield*product.product_amount)}" />

				</tr>
			</s:iterator>
		</tbody>
		<tbody>
			<tr>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<td colspan="4">TOTALE</td>
				</s:if>
				<s:else>
					<td colspan="3">TOTALE</td>
				</s:else>
				
				<td class="text-right">
					<s:text name="format.number">
						<s:param value="%{#total_qta}" />
					</s:text>
				</td>
				
				<td class="text-right">
					<s:text name="format.money">
						<s:param value="%{#total_price/#total_qta}" />
					</s:text>
				</td>
				
				<td class="text-right">
					<s:text name="format.number">
						<s:param value="%{#total_yield/#total_qta}" />
					</s:text>
				</td>
				
			</tr>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>