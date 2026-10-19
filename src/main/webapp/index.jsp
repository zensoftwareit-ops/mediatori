<%@include file="header.jsp"%>

<div class="container-fluid">
	<div class="row vh-100 fix-height">

		<%@include file="sidebar.jsp"%>

		<div class="col bg-light">
			<div class="p-4">
				<div class="h1 font-weight-bold border-bottom">Situazione</div>
				<div class="row">
					<div class="col">
						<div class="py-1">
							Il tuo ultimo accesso: <b><s:date
									name="#session.admin.last_activity_date"
									format="dd/MM/yyyy HH:mm" /></b>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-sm-6 col-md-6 col-lg-4 col-xl-3">
						<div class="py-4">
							<div class="p-3 shadow bg-primary animated fadeInRight">
								<div class="h3">
									<a href="customers" class="text-white"><i
										class="fas fa-users"></i> Clienti</a>
								</div>
								<p class="text-white">
									<b><s:property value="%{countCustomers}" /></b> record<br>&nbsp;
								</p>
							</div>
						</div>
					</div>
					<div class="col-12 col-sm-6 col-md-6 col-lg-4 col-xl-3">
						<div class="py-4">
							<div class="p-3 shadow bg-primary animated fadeInRight fast">
								<div class="h3">
									<a href="sales" class="text-white"><i class="fas fa-coins"></i>
										Vendite</a>
								</div>
								<p class="text-white">
									<b><s:property value="%{countSales}" /></b> record<br>Anno
									di semina <b><s:property value="#session.sowing_year.year" /></b>
								</p>
							</div>
						</div>
					</div>
					<div class="col-12 col-sm-6 col-md-6 col-lg-4 col-xl-3">
						<div class="py-4">
							<div class="p-3 shadow bg-primary animated fadeInRight fast">
								<div class="h3">
									<a href="invoices" class="text-white"><i
										class="fas fa-coins"></i> Fatture</a>
								</div>
								<p class="text-white">
									<b><s:property value="%{countInvoices}" /></b> record<br>Anno
									fiscale <b><s:property value="#session.year.year" /></b>
								</p>
							</div>
						</div>
					</div>
					<div class="col-12 col-sm-6 col-md-6 col-lg-4 col-xl-3">
						<div class="py-4">
							<div class="p-3 shadow bg-primary animated fadeInRight faster">
								<div class="h3">
									<a href="samples_measures" class="text-white"><i
										class="fas fa-balance-scale"></i> Campioni</a>
								</div>
								<p class="text-white">
									<b><s:property value="%{countMeasures}" /></b> record<br>&nbsp;
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%@include file="footer.jsp"%>