<%-- 
    Document   : index
    Created on : 19-mar-2012, 19:25:56
    Author     : USUARIO
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.UUID"%>
<%@page import="model.Servicio"%>
<%@page import="model.Factura"%>
<%@page import="model.BilleteVendido"%>
<%@page import="model.Ruta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="keywords" content="" />
<meta name="description" content="" />
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<script src="javascripts/1286136086-jquery.js"></script>
<script src="javascripts/1291523190-jpaginate.js"></script>
<script>
            $(document).ready(function() {
                $("#billetes").jPaginate({items:1, previous:"Anterior", next:"Siguente", cookies:false});
            });
</script>
<title>I love traveling</title>
<link href="/TiendaBilletesAutobus/style.css" rel="stylesheet" type="text/css" media="screen" />
</head>
<body>
    <jsp:useBean id="estaciones" scope="application" class="java.util.ArrayList" />
    <jsp:useBean id="servicios" scope="application" class="java.util.HashMap" />
    <jsp:useBean id="billeteGestion" scope="session" class="model.BilleteVendido" />
    <jsp:useBean id="msg" scope="request" class="java.lang.String" />
<div id="wrapper">
	<div id="header-wrapper">
    <div id="topmenu"><p>Compre y gestione billetes de autobús de forma rápida y sencilla   </p></div>
		<div id="header">
			<div id="logo">
				<h1>&nbsp;</h1>
			</div>
			<div id="menu">
				<ul>
					<li><a href="/TiendaBilletesAutobus/">Homepage</a></li>
					<li><a href="/TiendaBilletesAutobus/go?to=busqueda">comprar billete</a></li>
					<li class="current_page_item"><a href="/TiendaBilletesAutobus/go?to=gestion">gestionar billete adquirido</a></li>
					<li><a href="/TiendaBilletesAutobus/go?to=solicitudClubBus">solicitar tarjeta club-bus</a></li>
                    <li><a href="#">consulta de ofertas</a></li>
				</ul>
			</div>
		</div>
	</div>
	<!-- end #header -->
	<div id="page">
		<div id="page-bgtop">
			<div id="page-bgbtm">
				<div id="content">
				  <div class="post" align="center">
                      <h2 class="title">Cancelar billete</h2>
                      <%if(msg != null && !msg.equals("")) {
                          if(msg.contains("!")) {%>
                              <p style="color: red;"><%=msg%></p>
                          <%} else {%>
                              <p style="color: #2EB902;"><%=msg%></p>
                          <%}
                      }%>
                      <%HashMap<UUID,Servicio> serviciosC = (HashMap<UUID,Servicio>) servicios;%>
                      <p>Seguro que desea cancelar el billete (localizador: <%=billeteGestion.getLocalizador()%>)</p></br></br>
                      <p></p>
                      <p>
                      <a href="javascript:history.back()" style="color: #2EB902; font-weight: bold;">VOLVER</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                      <a href="/TiendaBilletesAutobus/go?to=cancelacion" style="color: blue; font-weight: bold;">CANCELAR BILLETE</a>
                      </p>
				  </div>
				</div>
				<!-- end #content -->
				<div id="sidebar">
					<ul>
                            <h3>Buscador de Billetes</h3>
                            <form method="post" action="/TiendaBilletesAutobus/go">
                                <input name="to" value="busqueda" type="hidden">
                                <li>Origen<select name="origen">
                                    <%ArrayList<String> estacionesString = (ArrayList<String>) estaciones;
                                    for(String estacion : estacionesString){%>
                                        <option><%=estacion%></option>
                                    <%}%>
                                </select></li>
                                <li>Destino<select name="destino">
                                    <%for(String estacion : estacionesString){%>
                                        <option><%=estacion%></option>
                                    <%}%>
                                </select></li>
                                <li>Ida <input type="Radio" name="modo" value="i" checked>&nbsp;&nbsp;Ida y vuelta <input type="Radio" name="modo" value="iv"></li>
                                <li><input type="checkbox" name="sinfecha" value="sinfecha"> Consulta sin fecha</li>
                                <li>Fecha de salida:<br/> Dia: <select name="day"><option>-</option><%for(int i=1;i<=31;i++){%><option><%=i%></option><%}%></select>&nbsp;&nbsp;Mes: <select name="month"><option>-</option><%for(int i=1;i<=12;i++) {%><option><%=i%></option><%}%></select>&nbsp;&nbsp;A&ntildeo: <select name="year">
                                    <option>-</option><%for(int i=2012;i<2015;i++) {%><option><%=i%></option><%}%></select></li>
                                <li>Fecha de regreso:<br/> Dia: <select name="dayVuelta"><option>-</option><%for(int i=1;i<=31;i++){%><option><%=i%></option><%}%></select>&nbsp;&nbsp;Mes: <select name="monthVuelta"><option>-</option><%for(int i=1;i<=12;i++) {%><option><%=i%></option><%}%></select>&nbsp;&nbsp;A&ntildeo: <select name="yearVuelta">
                                    <option>-</option><%for(int i=2012;i<2015;i++) {%><option><%=i%></option><%}%></select></li>
                                <li>Plazas<select name="numBilletes"><%for(int i=1;i<10;i++) {%><option><%=i%></option><%}%></select></li>
                                <li>Club-Bus: <input name="clubBus" value="" type="text" /></li>
                                <li>NIF<input name="nif" value="" type="text" /></li>
                                <span><button style="margin-left: 83px;">Buscar</button></span><%--img src="cssimages/carts.gif" alt="" width="16" height="24" class="carts" /--%>
                            </form>
							<%--p> formulario de compra de billete</p--%>
				  </ul>
							<h2>Todo ventajas</h2>
							<ul>
								<li><a href="#">Obten puntos por billete</a></li>
								<li><a href="#">Nuevas ofertas cada dia</a></li>
								<li><a href="#">Descuentos acumulables</a></li>
								<li><a href="#">Acceso a billetes VIP</a></li>
								<li><a href="#">No haga colas!</a></li>
								<li><a href="#">El mejor servicio de Europa</a></li>
							</ul>
                </div>
				<!-- end #sidebar -->
				<div style="clear: both;">&nbsp;</div>
			</div>
		</div>
	</div>
	<!-- end #page -->
</div>
<div id="footer">
	<p>Copyright (c) 2012 ilovetraveling.com. All rights reserved.  <a href=""></a></p>
</div>
<!-- end #footer -->
</body>
</html>

