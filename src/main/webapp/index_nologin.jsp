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
<link rel="stylesheet" href="js/fullcalendar/core/main.css">
<link rel="stylesheet" href="js/fullcalendar/daygrid/main.css">
<link rel="stylesheet"
	href="css/custom.css?v=<s:property value="#application.app.getProperty('v')"/>">
<sj:head locale="it" jqueryui="true" />
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

	<div class="container">
		<div class="row justify-content-center vh-100">
			<div
				class="col-12 col-sm-10 col-md-6 col-lg-6 col-xl-4 align-self-center">

				<div class="row">
					<div class="col">
						<img class="img-fluid w-100 mb-4" src="images/LOGO_Luna.png">
					</div>
				</div>

				<s:form action="login" method="post" cssClass="form-horizontal"
					theme="simple">

					<div class="input-group mb-2">
						<div class="input-group-prepend">
							<div class="input-group-text">
								<i class="fas fa-user"></i>
							</div>
						</div>
						<s:textfield name="admin.username" cssClass="form-control"
							placeholder="* Nome utente" required="true" />
					</div>

					<div class="input-group mb-2">
						<div class="input-group-prepend">
							<div class="input-group-text">
								<i class="fas fa-key"></i>
							</div>
						</div>
						<s:password name="admin.password" cssClass="form-control"
							placeholder="* Password" required="true" />
					</div>

					<s:submit value="Accedi" cssClass="btn btn-primary w-100" />
				</s:form>
			</div>
		</div>
	</div>

	<%@include file="footer.jsp"%>