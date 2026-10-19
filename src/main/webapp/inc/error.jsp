<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="container-fluid">
	<div class="row">
		<div class="col">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Si &egrave;
					verificato un errore :(</div>
			</div>
			<div class="p-4">
				<s:property value="%{excp}" />
			</div>
		</div>
	</div>
</div>
