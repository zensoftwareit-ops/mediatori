<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="col-12 col-sm-auto bg-primary shadow">
	<div class="py-4 pr-4">
		<ul
			class="list-unstyled position-relative position-sm-relative position-md-fixed"
			id="sidebar-menu">
			<li class="py-2"><a href="customers"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-users"></i> clienti</a></li>
			<li class="py-2"><a href="products"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-box-open"></i> prodotti</a></li>
			<li class="py-2"><a href="payments"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-box-open"></i> metodi di pagamento</a></li>
			<li class="py-2"><a href="sales"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-coins"></i> vendite</a></li>
			<li class="py-2"><a href="deadlines"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-exclamation"></i> scadenze</a></li>
			<li class="py-2"><a href="commissions"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-percentage"></i> provvigioni</a></li>
			<li class="py-2"><a href="invoices"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-file-invoice"></i> fatture</a></li>
			<li class="py-2"><a href="statistics"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-calculator"></i> statistiche</a></li>
			<li>&nbsp;</li>
			<li class="py-2"><a href="samples_measures"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-balance-scale"></i> Campioni</a></li>
			<li class="py-2"><a href="analisi"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-balance-scale"></i> Analisi</a></li>
			<li>&nbsp;</li>
			<li class="py-2"><a href="index"
				class="text-light text-uppercase font-weight-bold"><i
					class="fas fa-tachometer-alt"></i> situazione</a></li>
		</ul>
	</div>

	<div class="position-absolute" style="bottom: 10px;">
		<small class="text-muted">Luna v<s:property
				value="#application.app.getProperty('v')" /></small>
	</div>
</div>
