<%@ taglib prefix="s" uri="/struts-tags"%>

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
				<th scope="col">Prodotto</th>
				<th scope="col">Operazioni</th>
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
					<td width="20%"><s:property value="%{seller.business_name}" /></td>
					<td width="20%"><s:property value="%{buyer.business_name}" /></td>
					<td width="10%"><s:property value="%{product.name}" /></td>
					<td>
						<ul class="nav nav-pills">
							<li class="nav-item m-1"><a
								href="sales/manage/<s:property value="%{id}" />" role="button"
								class="btn btn-primary btn-sm"><i class="fas fa-pencil-alt"></i></a>
							</li>
							<li class="nav-item m-1"><a
								href="sales/<s:property value="%{contract_template.action}" />/<s:property value="%{id}" />"
								role="button" class="btn btn-primary btn-sm no-loader"><i
									class="fas fa-file-pdf"></i> Contratto</a></li>
							<li class="nav-item m-1"><a
								href="sales/send-mail/<s:property value="%{id}" />?customer=seller"
								role="button" class="btn btn-primary btn-sm no-loader send-mail"><i
									class="fas fa-envelope"></i> Invia al venditore</a></li>
							<li class="nav-item m-1"><a
								href="sales/send-mail/<s:property value="%{id}" />?customer=buyer"
								role="button" class="btn btn-primary btn-sm no-loader send-mail"><i
									class="fas fa-envelope"></i> Invia all'acquirente</a></li>

							<li class="nav-item m-1"><a
								href="sales/delete/<s:property value="%{id}" />" role="button"
								class="btn btn-danger btn-sm delete-sale"><i
									class="fas fa-trash"></i></a></li>

						</ul>
						<s:if test="%{#session.admin.smsEnabled.equals('SI')}">
							<ul class="nav nav-pills">
								<li class="nav-item m-1">
									<s:if test="%{signedSeller.equals('SI')}">
										<a href="#" role="button" class="btn btn-outline-success btn-sm disabled" ><i class="fas fa-user-check"></i> Firmato dal venditore</a>
									</s:if>
									<s:else>
										<s:if test="%{otpSellerSent.equals('SI')}">
											<a href="sales/sign/<s:property value="%{id}" />?customerType=seller"
											role="button" class="btn btn-secondary btn-sm no-loader sign-modal-btn"><i
											class="fas fa-signature"></i> FIRMA venditore</a>
											<a href="sales/sign-restart/<s:property value="%{id}" />?customerType=seller" role="button" class="btn btn-danger btn-sm">Reset vend.</a>
										</s:if>
										<s:else>
											<a href="sales/send-otp/<s:property value="%{id}" />?customerType=seller&tel="
											role="button" class="btn btn-primary btn-sm no-loader send-otp"><i
											class="fas fa-sms"></i> Invia OTP venditore</a>
										</s:else>
									</s:else>
								</li>
								<li class="nav-item m-1">
									<s:if test="%{signedBuyer.equals('SI')}">
										<a href="#" role="button" class="btn btn-outline-success btn-sm disabled" ><i class="fas fa-user-check"></i> Firmato dall'acquirente</a>
									</s:if>
									<s:else>
										<s:if test="%{otpBuyerSent.equals('SI')}">
											<a href="sales/sign/<s:property value="%{id}" />?customerType=buyer"
											role="button" class="btn btn-secondary btn-sm no-loader sign-modal-btn"><i
											class="fas fa-signature"></i> FIRMA acquirente</a>
											<a href="sales/sign-restart/<s:property value="%{id}" />?customerType=buyer" role="button" class="btn btn-danger btn-sm">Reset acq.</a>
										</s:if>
										<s:else>
											<a href="sales/send-otp/<s:property value="%{id}" />?customerType=buyer&tel="
											role="button" class="btn btn-primary btn-sm no-loader send-otp"><i
											class="fas fa-sms"></i> Invia OTP acquirente</a>
										</s:else>
									</s:else>
								</li>
							</ul>
						</s:if>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

<%@include file="paginator.jsp"%>