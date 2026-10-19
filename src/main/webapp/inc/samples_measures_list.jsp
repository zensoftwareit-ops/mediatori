<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>

<div class="table-responsive">
	<table class="table table-hover table-sm">
		<thead>
			<tr>
				<th scope="col">Data misurazione</th>
				<th scope="col"><a
					href="samples_measures?id_customer=<s:property value="%{id_customer}" />&id_product=<s:property value="%{id_product}" />&orderby=customer">Cliente</a></th>
				<th scope="col">Prodotto</th>
				<th scope="col">Note</th>
				<th scope="col">Resa grani</th>
				<th scope="col">Resa globale</th>
				<th scope="col">Difetti 1</th>
				<th scope="col">Difetti 2</th>
				<th scope="col">Note</th>
				<th scope="col">1000 grani</th>
				<th scope="col" colspan="3">&nbsp;</th>
			</tr>
		</thead>
		<tbody>

			<tr>
				<s:form action="samples_measures/save" method="post" theme="simple">
					<s:hidden key="measure.admin.id" />
					<s:hidden key="measure.year.id" />
					<s:hidden key="measure.customer.id" value="%{id_customer}" />
					<s:hidden key="measure.product.id" value="%{id_product}" />
					<td><sj:datepicker key="measure.insert_date"
							displayFormat="d/m/y" cssClass="form-control form-control-sm" />
					</td>
					<td><s:property value="customer.internal_code" /> <s:fielderror
							fieldName="measure.customer.id" /></td>
					<td><s:property value="product.name" /> <s:fielderror
							fieldName="measure.product.id" /></td>
					<td><s:textfield key="measure.note_1"
							cssClass="form-control form-control-sm" /></td>
					<td><s:textfield key="measure.grains_yield"
							cssClass="form-control form-control-sm numeric" /></td>
					<td><s:textfield key="measure.global_yield"
							cssClass="form-control form-control-sm numeric" /></td>
					<td><s:textfield key="measure.defects_1"
							cssClass="form-control form-control-sm numeric" /></td>
					<td><s:textfield key="measure.defects_2"
							cssClass="form-control form-control-sm numeric" /></td>
					<td><s:textfield key="measure.note_2"
							cssClass="form-control form-control-sm" /></td>
					<td><s:textfield key="measure.thousand_grains"
							cssClass="form-control form-control-sm numeric" /></td>
					<td><s:submit type="button" cssClass="btn btn-primary btn-sm">
							<i class="fas fa-save"></i>
						</s:submit></td>
					<td colspan="2"></td>
				</s:form>
			</tr>

			<s:iterator value="%{measures}" begin="%{begin}" end="%{end}">
				<tr>
					<s:form action="samples_measures/save" method="post" theme="simple">
						<s:hidden key="measure.id" value="%{id}" />
						<s:hidden key="measure.admin.id" value="%{admin.id}" />
						<s:hidden key="measure.year.id" value="%{year.id}" />
						<s:hidden key="measure.customer.id" value="%{customer.id}" />
						<s:hidden key="measure.product.id" value="%{product.id}" />

						<td><sj:datepicker key="measure.insert_date"
								value="%{insert_date}" displayFormat="d/m/y"
								cssClass="form-control form-control-sm" /></td>
						<td><s:property value="%{customer.internal_code}" /></td>
						<td><s:property value="%{product.name}" /></td>
						<td><s:textfield key="measure.note_1" value="%{note_1}"
								cssClass="form-control form-control-sm" /></td>
						<td><s:textfield key="measure.grains_yield"
								value="%{grains_yield}"
								cssClass="form-control form-control-sm numeric" /></td>
						<td><s:textfield key="measure.global_yield"
								value="%{global_yield}"
								cssClass="form-control form-control-sm numeric" /></td>
						<td><s:textfield key="measure.defects_1" value="%{defects_1}"
								cssClass="form-control form-control-sm numeric" /></td>
						<td><s:textfield key="measure.defects_2" value="%{defects_2}"
								cssClass="form-control form-control-sm numeric" /></td>
						<td><s:textfield key="measure.note_2" value="%{note_2}"
								cssClass="form-control form-control-sm" /></td>
						<td><s:textfield key="measure.thousand_grains"
								value="%{thousand_grains}"
								cssClass="form-control form-control-sm numeric" /></td>
						<td><s:submit type="button" cssClass="btn btn-primary btn-sm">
								<i class="fas fa-save"></i>
							</s:submit></td>
						<td><a
							href="samples_measures/label/<s:property value="%{id}" />"
							target="_blank" title="Stampa etichetta"
							role="button" class="btn btn-secondary btn-sm no-loader"><i
								class="fas fa-print"></i></a></td>
						<td><a
							href="samples_measures/delete/<s:property value="%{id}" />"
							role="button" class="btn btn-danger btn-sm delete-measure"><i
								class="fas fa-trash"></i></a></td>
					</s:form>
				</tr>
			</s:iterator>

			<tr>
				<td colspan="3">MEDIA</td>
				<td></td>
				<td><b><s:text name="format.number">
							<s:param value="grains_average" />
						</s:text></b></td>
				<td><b><s:text name="format.number">
							<s:param value="global_average" />
						</s:text></b></td>
				<td><b><s:text name="format.number">
							<s:param value="defects_average_1" />
						</s:text></b></td>
				<td><b><s:text name="format.number">
							<s:param value="defects_average_2" />
						</s:text></b></td>
				<td></td>
			</tr>

		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>
