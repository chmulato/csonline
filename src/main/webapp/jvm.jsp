<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@ page import="java.lang.management.*"%>
<%@ page import="java.util.*"%>

<%--
    Document   : JVM Memory Viewer
    Created on : 06/12/2016, 10:28:23
    Author     : Christian Vladimir Uhdre Mulato
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 TRANSITIONAL//EN" "HTTP://WWW.W3.ORG/TR/HTML4/LOOSE.DTD">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SaqueNET - JVM Memory View</title>
</head>
<body
	style="font-family: verdana; font-size: 10px; margin: 10px; padding: 10px;">
	<h4>
		JVM Memory View | Status at
		<%= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())%>
		<%if (request.getParameter("gc") != null) {%>
		(After running the garbage collector)<%}%>
	</h4>
	<hr />
	<div style="text-align: right;">
		<a href="jvm.jsp">Refresh</a> <a href="jvm.jsp?gc=true">Run
			Garbage Colector</a>
	</div>
	<br />
	<br />
	<%
                    //roda gc se solicitado
                    if (request.getParameter("gc") != null) {
                        System.gc();
                    }

                    //CONFIGURAÇÕES
                    //CORES
                    //String full = "#B6D1FE";
                    String full = "blue";
                    //String normal = "#BFFDBA";
                    String normal = "lightgreen";
                    String atencao = "#FFE793";
                    
                    //String perigoso = "#FF9893";
                    String perigoso = "red";
                    String document_background_color = "#F8F8F8";
                    //TAMANHO DO GRAFICO
                    int pixelProporcao = 2;
                    int altura = 25;
        %>


	<div>
		Memory MXBean

		<div
			style="border: 1px solid black;padding: 10px;background-color: <%=document_background_color%>;">

			<strong>Heap Memory</strong> <br />Usage:
			<%=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()%><br />
			<br />
			<%
                
                            //calcula o máximo e o usado(%) do maxima para exibir graficamente os valores
                            long maxHM = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax(); //isto é 100%
                            long usedHM = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed(); //isto é % de uso
                            //calcula quanto %é o uso de maximo
                            long porcentagemHM = (usedHM * 100) / maxHM;
                            String cor = "";
                            if (porcentagemHM <= 30) {
                                cor = normal;
                            } else if (porcentagemHM <= 70) {
                                cor = atencao;
                            } else if (porcentagemHM <= 100) {
                                cor = perigoso;
                            }

                %>
			Max (<%=(maxHM / 1024) / 1024%>MB = 100%):
			<div
				style="background-color: <%=full%>;width: <%=100 * pixelProporcao%>px;height: <%=altura%>px;"></div>
			Used (<%=(usedHM / 1024) / 1024%>MB =
			<%=porcentagemHM%>%):
			<div
				style="background-color: <%=cor%>;width: <%=porcentagemHM * pixelProporcao%>px;height: <%=altura%>px;"></div>
			<br /> <strong>Non-Heap Memory</strong> <br />Usage:
			<%=ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()%><br />
			<br />
			<%
                            //calcula o máximo e o usado(%) do maxima para exibir graficamente os valores
                            long maxNHM = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax(); //isto é 100%
                            long usedNHM = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed(); //isto é % de uso
                            //calcula quanto %é o uso de maximo
                            long porcentagemNHM = (usedNHM * 100) / maxNHM;
                            cor = "";
                            if (porcentagemNHM <= 30) {
                                cor = normal;
                            } else if (porcentagemNHM <= 70) {
                                cor = atencao;
                            } else if (porcentagemNHM <= 100) {
                                cor = perigoso;
                            }

                %>
			Max (<%=(maxNHM / 1024) / 1024%>MB = 100%):
			<div
				style="background-color: <%=full%>;width: <%=100 * pixelProporcao%>px;height: <%=altura%>px;"></div>
			Used (<%=(usedNHM / 1024) / 1024%>MB =
			<%=porcentagemNHM %>%):
			<div
				style="background-color: <%=cor%>;width: <%=porcentagemNHM * pixelProporcao%>px;height: <%=altura%>px;"></div>
		</div>

		<br /> Memory Pool MXBeans
		<div
			style="border: 1px solid black;padding: 10px;background-color: <%=document_background_color%>;">
			<%
                			for (MemoryPoolMXBean item :  ManagementFactory.getMemoryPoolMXBeans()) {
                %>

			<strong><%= item.getName()%></strong><br /> Type:
			<%= item.getType()%><br /> Usage:
			<%= item.getUsage()%><br /> Peak Usage:
			<%= item.getPeakUsage()%><br /> Collection Usage:
			<%= item.getCollectionUsage()%>
			<br /> <br />
			<%
	                             //calcula o máximo e o usado(%) do maxima para exibir graficamente os valores
	                             long maxLoop = item.getUsage().getMax(); //isto é 100%
	                             long usedLoop = item.getUsage().getUsed(); //isto é % de uso
	                             //calcula quanto %é o uso de maximo
	                             long porcentagemLoop = (usedLoop * 100) / maxLoop;
	                             cor = "";
	                             if (porcentagemLoop <= 30) {
	                                 cor = normal;
	                             } else if (porcentagemLoop <= 70) {
	                                 cor = atencao;
	                             } else if (porcentagemLoop <= 100) {
	                                 cor = perigoso;
	                             }

                %>
			Max (<%=(maxLoop / 1024) / 1024%>MB = 100%):
			<div
				style="background-color: <%=full%>;width: <%=100 * pixelProporcao%>px;height: <%=altura%>px;"></div>
			Used (<%=(usedLoop / 1024) / 1024%>MB =
			<%=porcentagemLoop%>%):
			<div
				style="background-color: <%=cor%>;width: <%=porcentagemLoop * pixelProporcao%>px;height: <%=altura%>px;"></div>
			<br />
			<br />
			<%
                            }
                %>
		</div>

	</div>
	<br />
	<div style="text-align: right;">Christian Vladimir Uhdre Mulato
		(chmulato@hotmail.com) ® 2016 - all rights reserved</div>

</body>
</html>






