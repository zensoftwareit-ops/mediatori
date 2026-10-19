<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="table-responsive">
	<table class="table table-sm table-hover">
		<thead>
			<tr>
				<th scope="col">#</th>
				<s:if test="%{#session.admin.level.matches('admin|super')}">
					<th scope="col">Utente</th>
				</s:if>
				<th scope="col">Nome</th>
				<th scope="col">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="%{products}" begin="%{begin}" end="%{end}">
				<tr>
					<th scope="row"><s:property value="%{internal_code}" /></th>
					<s:if test="%{#session.admin.level.matches('admin|super')}">
						<td><s:property value="%{admin.username}" /></td>
					</s:if>
					<td><s:property value="%{name}" /></td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="products/manage/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm"><i
									class="fas fa-pencil-alt"></i></a></li>
							<li class="nav-item m-1"><a
								href="products/delete/<s:property value="%{id}" />"
								role="button" class="btn btn-danger btn-sm delete-product"><i
									class="fas fa-trash"></i></a></li>
						</ul>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>