<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">

				<div class="h1 font-weight-bold border-bottom">Log</div>

				<div class="pt-2 pb-3">
					<textarea class="form-control form-control-sm" rows="20" readonly><s:property
							value="info" /></textarea>
				</div>

			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>