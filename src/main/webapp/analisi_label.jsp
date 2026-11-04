<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">
		<%@include file="sidebar.jsp"%>
		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Etichetta laboratorio analisi</div>
				<div class="card shadow-sm mt-3">
					<div class="card-body">
						<p class="text-muted">Genera un'etichetta verticale 10x15 cm con l'anagrafica completa dell'agricoltore, il prodotto e le analisi richieste.</p>
						<s:form action="analisi/etichetta/print" method="post" theme="simple" target="_blank">
							<div class="form-group">
								<label class="font-weight-bold" for="id_customer">Agricoltore</label>
								<sj:autocompleter key="id_customer" list="customers" listValue="business_name" listKey="id" placeholder="Cerca agricoltore..." />
							</div>
							<div class="form-group">
								<label class="font-weight-bold" for="id_product">Prodotto</label>
								<sj:autocompleter key="id_product" list="products" listValue="name" listKey="id" placeholder="Cerca prodotto..." />
							</div>
							<div class="alert alert-light border">Metalli riportati: <strong>Cadmio, Arsenico, Nichel</strong>. I valori restano vuoti per il laboratorio.</div>
							<button type="submit" class="btn btn-primary no-loader"><i class="fas fa-print"></i> Stampa etichetta 10x15</button>
							<a href="analisi" class="btn btn-danger">Chiudi</a>
						</s:form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>
