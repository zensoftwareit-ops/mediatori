<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>
<html lang="it-IT">
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<base href="<s:property value="#session.domain.url"/>">
<title>Luna 1.0 build <s:property
		value="#application.app.getProperty('v')" /></title>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/all.min.css">
<link rel="stylesheet" href="css/animate.css">
<link rel="stylesheet"
	href="css/custom.css?v=<s:property value="#application.app.getProperty('v')"/>">
<sj:head locale="it" jqueryui="true" jquerytheme="moonwalker"
	customBasepath="%{context}css/jqueryui" />
	<style>
.ui-datepicker-trigger {
	display: none;
}
</style>
</head>
<body>

	<div class="load-window bg-light">
		<div class="container">
			<div class="row vh-100 justify-content-center">
				<div class="col-10 col-lg-4 col-xl-3 align-self-center">
					<div class="progress">
						<div
							class="progress-bar progress-bar-striped progress-bar-animated bg-primary"
							role="progressbar" aria-valuenow="100" aria-valuemin="0"
							aria-valuemax="100" style="width: 100%"></div>
					</div>
				</div>
			</div>
		</div>
	</div>



<div class="container-fluid vh-100">
	<div class="row justify-content-center vh-100">

			<div class="col-12 col-sm-6 col-md-6 col-lg-4 align-self-center  bg-light border">
			<div class="p-4">

					<div class="h3 font-weight-bold border-bottom">Firma il contratto n. <s:property value="%{sale.number}" /></div>


				<div class="pt-2 pb-3">
				
					<div class="row" id="getotprow">
						<div class="col-12">
							<input type="text" id="telnumber" class="form-control form-control-sm" placeholder="Numero di telefono (senza +39)">
							<a id="getotp" href="#" data-id-sale="<s:property value="%{id_sale}" />" data-customer-type="<s:property value="%{customerType}" />" class="pt-3 d-block">Fai clic qui per ricevere un codice OTP vai sms sul tuo cellulare.</a>
						</div>
					</div>

					<s:form action="sales/selfsign-save" method="post" theme="simple" id="formsign" cssClass="d-none">

						<s:hidden key="id_sale" />
						<s:hidden key="customerType" />

						<div class="form-row">

							<div class="form-group col-12 ">



								<div class="form-row">
									<div class="form-group col-12">
										<s:textfield key="otp" cssClass="form-control form-control-sm" placeholder="Codice OTP" required="required"/>
									</div>
								</div>

							</div>

						</div>
						
						<s:submit type="button" name="action_type" value="save_close_modal" cssClass="btn btn-success btn-sm">Firma documento</s:submit>

					</s:form>
				</div>

			</div>
		</div>
	</div>
</div>

<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/tinymce/tinymce.min.js"></script>
<script
	src="js/custom.js?v=<s:property value="#application.app.getProperty('v')"/>"></script>

<script>
$(document).ready(
		function() {
			
			$('#getotp').click(
					function(e) {
						e.preventDefault();
						$('.load-window').fadeIn('fast');
						var t = $(this);
						$.ajax({
							url: 'https://gestionale.matteo-forte.it/sales/send-otp/' + t.data('idSale') + '?customerType=' + t.data('customerType') + '&tel=' + $('#telnumber').val(),
							success: function() {$('.load-window').fadeOut('fast'); $('#getotprow').hide();$('#formsign').removeClass('d-none');}
						});
					});
			
		});
</script>

</body>
</html>
