<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 TRANSITIONAL//EN">
<%!
public String getBrowserInfo(String Information) {
     String browsername = "";
     String browserversion = "";
     String browser = Information;
     if (browser.contains("MSIE")) {
         String subsString = browser.substring(browser.indexOf("MSIE"));
         String Info[] = (subsString.split(";")[0]).split(" ");
         browsername = "Internet Explorer";
         browserversion = Info[1];
      } else if (browser.contains("Firefox")) {
         String subsString = browser.substring(browser.indexOf("Firefox"));
         String Info[] = (subsString.split(" ")[0]).split("/");
         browsername = Info[0];
         browserversion = Info[1];
    } else if (browser.contains("Chrome")) {
         String subsString = browser.substring(browser.indexOf("Chrome"));
         String Info[] = (subsString.split(" ")[0]).split("/");
         browsername = Info[0];
         browserversion = Info[1];
    } else if (browser.contains("Opera")) {
         String subsString = browser.substring(browser.indexOf("Opera"));
         String Info[] = (subsString.split(" ")[0]).split("/");
         browsername = Info[0];
         browserversion = Info[1];
    } else if (browser.contains("Safari")) {
         String subsString = browser.substring(browser.indexOf("Safari"));
         String Info[] = (subsString.split(" ")[0]).split("/");
         browsername = Info[0];
         browserversion = Info[1];
    } 
    return browsername + "-" + browserversion;
}
public boolean checkVersionIE(String Information) {
	boolean isOK = false;
	String Info[] = this.getBrowserInfo(Information).split("-");
	if ((Info[1].equals("7.0")) || (Info[1].equals("8.0")) || (Info[1].equals("9.0"))) {
		isOK = true;	
	}
	return isOK;
}
%>
<%
	String ua = request.getHeader( "User-Agent" );
	boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
	boolean isMSIE = ( ua != null && ua.indexOf( "MSIE" ) != -1 && checkVersionIE(ua));
	boolean isChrome = ( ua != null && ua.indexOf( "Chrome/" ) != -1 );
	boolean isSafari = ( ua != null && ua.indexOf( "Safari/" ) != -1 );
	boolean isOpera = ( ua != null && ua.indexOf( "Opera/" ) != -1 );
	response.setHeader( "Vary", "User-Agent" );
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CSOnline</title>
<link type="text/css" rel="stylesheet" href="/resources/style.css" />
</head>
<body>
	<% if ( isFirefox ) { %>
	<jsp:forward page="/login.faces" />
	<% } else if ( isMSIE ) { %>
	<jsp:forward page="/login.faces" />
	<% } else if ( isChrome ) { %>
	<jsp:forward page="/login.faces" />
	<% } else if ( isSafari ) { %>
	<jsp:forward page="/login.faces" />
	<% } else if ( isOpera ) { %>
	<jsp:forward page="/login.faces" />
	<% } else { %>
	<table width="400px" border="0" align="left">
		<tr>
			<td colspan="5">
				<p class="fontes">
					Seu Navegador Web &eacute; o:
					<% out.println(getBrowserInfo(request.getHeader("User-Agent")));%>
				</p>
				<p class="fontes">Este sistema n&atilde;o &eacute;
					compat&iacute;vel com este navegador.</p>
				<p class="fontes">
					Utilize:<br> - Internet Explorer 8.0 ou 9.0,<br> -
					Firefox,<br> - Chrome,<br> - Opera, ou<br> - Safari.
				</p>
			</td>
		</tr>
		<tr>
			<td>
				<p class="fontes">
					<img src="images/firefox.jpg" alt="Firefox" width="50" height="50">
				</p>
			</td>
			<td>
				<p class="fontes">
					<img src="images/ie.jpg" alt="Internet Explorer" width="70"
						height="70">
				</p>
			</td>
			<td>
				<p class="fontes">
					<img src="images/chrome.jpg" alt="Chrome" width="50" height="50">
				</p>
			</td>
			<td>
				<p class="fontes">
					<img src="images/opera.jpg" alt="Opera" width="50" height="50">
				</p>
			</td>
			<td>
				<p class="fontes">
					<img src="images/safari.jpg" alt="Safira" width="50" height="57">
				</p>
			</td>
		</tr>
	</table>
	<% } %>
</body>
</html>
