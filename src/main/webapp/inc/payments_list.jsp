<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="table-responsive">
	<table class="table table-hover table-sm">
		<thead>
			<tr>
				<th scope="col">Codice</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Descrizione</th>
				<th scope="col">Rate</th>
				<th scope="col">Operazioni</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{payments}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{ptag}" /></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td><s:property value="%{description}" /></td>
					<td><s:property value="%{installments}" /></td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="payments/manage/<s:property value="%{id}" />"
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