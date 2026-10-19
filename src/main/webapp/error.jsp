<%@include file="header.jsp"%>

<div class="container">
	<div class="row vh-100 fix-height bg-light">
		<div class="col">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Si &egrave;
					verificato un errore :(</div>
				<br> <a href="#" onclick="history.back();"><i
					class="fas fa-chevron-left"></i> Torna indietro</a>
			</div>
			<div class="p-4">
				<s:property value="%{excp}" />
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>