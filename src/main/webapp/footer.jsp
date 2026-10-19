<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="modal fade" id="media-library-modal" tabindex="-1"
	role="dialog" aria-labelledby="media-library-modal" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg"
		role="document">
		<div class="modal-content">
			<div class="modal-header">
				<div class="h5">Libreria dei media</div>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="embed-responsive embed-responsive-16by9">
					<iframe class="embed-responsive-item" id="media-library-frame"
						src=""></iframe>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="documents-folder-modal" tabindex="-1"
	role="dialog" aria-labelledby="documents-folder-modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg"
		role="document">
		<div class="modal-content">
			<div class="modal-header">
				<div class="h5">Cartella documenti</div>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="embed-responsive embed-responsive-16by9">
					<iframe class="embed-responsive-item" id="documents-folder-frame"
						src=""></iframe>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="sale-modal" tabindex="-1"
	role="dialog" aria-labelledby="sale-modal"
	aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg"
		role="document">
		<div class="modal-content">
			<div class="modal-header">
        		<button type="button" class="close" data-dismiss="modal" aria-label="Close">
          		<span aria-hidden="true">&times;</span>
        		</button>
      		</div>
			<div class="modal-body">
				<div class="embed-responsive embed-responsive-16by9">
					<iframe class="embed-responsive-item" id="sale-frame"
						src=""></iframe>
				</div>
			</div>
		</div>
	</div>
</div>

<s:if test="%{!resp_status.equals('none')}">

	<div class="toast"
		style="position: fixed; bottom: 5px; right: 5px; z-index: 999999;"
		data-delay="5000">
		<div class="toast-header">
			<strong class="mr-auto">Informazioni</strong>
			<button type="button" class="ml-2 mb-1 close" data-dismiss="toast"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
		</div>
		<div class="toast-body">
			<span class="<s:property value="resp_status"/>"><b><s:property
						value="resp" /></b></span>
		</div>
	</div>
</s:if>

<script src="js/bootstrap.bundle.min.js"></script>
<script src="js/tinymce/tinymce.min.js"></script>
<script
	src="js/custom.js?v=<s:property value="#application.app.getProperty('v')"/>"></script>

<s:if test="%{!resp_status.equals('none')}">
	<script>
  $(document).ready(
		  function() {
			  $('.toast').toast('show');
		  });
  </script>
</s:if>

<script> 
window.onunload= function() {};
</script>

</body>
</html>