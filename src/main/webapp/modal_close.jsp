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

<script src="js/custom.js?v=<s:property value="#application.app.getProperty('v')"/>"></script>

<script>
$(document).ready(
		function() {
			parent.location.reload();
		});
</script>

</body>
</html>