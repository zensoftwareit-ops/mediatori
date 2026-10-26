<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="row">
	<div class="col-12">
		<div class="py-4">
			<nav aria-label="Pagination">
				<ul class="pagination justify-content-center">
					<s:iterator value="%{paginator}" var="tpage">
						<s:if test="%{#tpage==page}">
							<li class="page-item active"><s:url value="%{action_name}"
									var="pageUrl">
									<s:param name="page" value="%{#tpage}" />
									<s:param name="s" value="%{s}" />
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="id_product" value="%{id_product}" />
									<s:param name="pagato" value="%{pagato}" />
									<s:param name="comune" value="%{comune}" />
									<s:iterator value="%{comuni_selezionati}" var="comuneSelezionato">
										<s:param name="comuni_selezionati" value="%{#comuneSelezionato}" />
									</s:iterator>
									<s:param name="fuori_limite" value="%{fuori_limite}" />
									<s:param name="date_from">
										<s:date name="date_from" format="dd/MM/yyyy"/>
									</s:param>
									<s:param name="date_to">
										<s:date name="date_to" format="dd/MM/yyyy"/>
									</s:param>
									<s:param name="billed_status" value="%{billed_status}" />
								</s:url> <s:a href="%{pageUrl}" cssClass="page-link">
									<s:property value="%{#tpage}" />
								</s:a></li>
						</s:if>
						<s:else>
							<li class="page-item"><s:url value="%{action_name}"
									var="pageUrl">
									<s:param name="page" value="%{#tpage}" />
									<s:param name="s" value="%{s}" />
									<s:param name="id_customer" value="%{id_customer}" />
									<s:param name="id_product" value="%{id_product}" />
									<s:param name="pagato" value="%{pagato}" />
									<s:param name="comune" value="%{comune}" />
									<s:iterator value="%{comuni_selezionati}" var="comuneSelezionato">
										<s:param name="comuni_selezionati" value="%{#comuneSelezionato}" />
									</s:iterator>
									<s:param name="fuori_limite" value="%{fuori_limite}" />
									<s:param name="date_from">
										<s:date name="date_from" format="dd/MM/yyyy"/>
									</s:param>
									<s:param name="date_to">
										<s:date name="date_to" format="dd/MM/yyyy"/>
									</s:param>
									<s:param name="billed_status" value="%{billed_status}" />
								</s:url> <s:a href="%{pageUrl}" cssClass="page-link">
									<s:property value="%{#tpage}" />
								</s:a></li>
						</s:else>
					</s:iterator>
				</ul>
			</nav>
		</div>
	</div>
</div>
