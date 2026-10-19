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

				<div class="h3 font-weight-bold border-bottom">Firma il contratto n. <s:property value="%{id_sale}" /></div>
				<div class="h2 text-success text-center">OK</div>
				
				<div class="pt-2 pb-3">
					<a href="https://gestionale.matteo-forte.it/sales/<s:property value="%{sale.contract_template.action}" />/<s:property value="%{sale.id}" />?noSession=YES">Fai clic qui per scaricare il contratto firmato</a>
				</div>

			</div>
		</div>
	</div>
</div>

<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/tinymce/tinymce.min.js"></script>
<script
	src="js/custom.js?v=<s:property value="#application.app.getProperty('v')"/>"></script>



</body>
</html>
