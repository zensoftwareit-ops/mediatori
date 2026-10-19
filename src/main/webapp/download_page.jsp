<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col">
			<div class="p-4">
				<div class="h3 font-weight-bold border-bottom">Download</div>

				<div class="pt-2 pb-3" id="download">

					<s:iterator value="files">
						<div>
							<a href="DocDownload?filename=<s:property />"
								class="text-primary no-loader"><s:property /></a>
						</div>
					</s:iterator>

				</div>
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>