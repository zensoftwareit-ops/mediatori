<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="table-responsive">
	<table class="table table-sm table-hover">
		<thead>
			<tr>
				<th scope="col">#</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Rag. Soc.</th>
				<th scope="col">Telefono</th>
				<th scope="col">E-mail</th>
				<th scope="col">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{customers}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{id}" /></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td><s:property value="%{business_name}" /></td>
					<td><a href="tel:<s:property value="%{primary_phone}" />"><s:property
								value="%{primary_phone}" /></a></td>
					<td><a href="mailto:<s:property value="%{primary_email}" />"><s:property
								value="%{primary_email}" /></a></td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="customers/manage/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm"><i
									class="fas fa-pencil-alt"></i></a></li>
							<li class="nav-item m-1"><a
								href="customers/pdf/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm"><i
									class="fas fa-print"></i></a></li>
							<li class="nav-item m-1"><a
								href="customers/delete/<s:property value="%{id}" />"
								role="button" class="btn btn-danger btn-sm delete-customer"><i
									class="fas fa-trash"></i></a></li>
						</ul>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>