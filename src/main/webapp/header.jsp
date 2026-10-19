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

	<header>
		<nav class="navbar navbar-expand-lg bg-white navbar-light">
			<a class="navbar-brand" href="#"> <img src="images/LOGO_Luna.png"
				height="30">
			</a>
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#navbarSupportedContent"
				aria-controls="navbarSupportedContent" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav mr-auto">
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						role="button" data-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"> File </a>
						<div class="dropdown-menu" aria-labelledby="navbarDropdown">
							<s:if test="%{#session.admin.level.matches('super')}">
								<a class="dropdown-item" href="show_log">System log</a>
								<a class="dropdown-item" href="set-maintenance?value=true">Set
									maintenance mode</a>
								<div class="dropdown-divider"></div>
							</s:if>
							<a class="dropdown-item" href="logout">Esci</a>
						</div></li>
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						role="button" data-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"> Anno fiscale <b>(<s:property
									value="#session.year.year" />)
						</b>
					</a>
						<div class="dropdown-menu" aria-labelledby="navbarDropdown">
							<s:iterator value="%{#session.admin.invoice_counters}">
								<a class="dropdown-item"
									href="change_year?id=<s:property value="%{year.id}" />"><s:property
										value="%{year.year}" /></a>
							</s:iterator>
						</div></li>
					<li class="nav-item dropdown"><a
						class="nav-link dropdown-toggle" href="#" id="navbarDropdown"
						role="button" data-toggle="dropdown" aria-haspopup="true"
						aria-expanded="false"> Anno di semina <b>(<s:property
									value="#session.sowing_year.year" />)
						</b>
					</a>
						<div class="dropdown-menu" aria-labelledby="navbarDropdown">
							<s:iterator value="%{#session.admin.sale_counters}">
								<a class="dropdown-item"
									href="change_sowing_year?id=<s:property value="%{year.id}" />"><s:property
										value="%{year.year}" /></a>
							</s:iterator>
						</div></li>
				</ul>
				<ul class="navbar-nav ml-auto">
					<li class="nav-item"><a href="#" class="nav-link"><i
							class="fas fa-user"></i> <s:property
								value="#session.admin.first_name" /> <s:property
								value="%{#session.admin.last_name}" /></a></li>
				</ul>
			</div>
		</nav>
	</header>