<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<!DOCTYPE html>

<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="java.net.URL" %>
<%! public URL fileURL;%>
	
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>Wavity Login</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<spring:theme code="standard.login.css.bootstrap" var="loginCssBootstrap" />
	    <spring:theme code="standard.login.css.form" var="loginCssForm" />
	    <spring:theme code="standard.login.css.animate" var="loginCssAnimate" />
	    <spring:theme code="standard.login.css.login" var="loginCssLogin" />
	    <spring:theme code="standard.login.css.stickyFooter" var="loginCssStickyFooter" />
		<link rel="stylesheet" href="<c:url value="${loginCssBootstrap}" />" />
		<link rel="stylesheet" href="<c:url value="${loginCssForm}" />" />
		<link rel="stylesheet" href="<c:url value="${loginCssAnimate}" />" />
		<link rel="stylesheet" href="<c:url value="${loginCssLogin}" />" />
		<link rel="stylesheet" href="<c:url value="${loginCssStickyFooter}" />" />
		
		<spring:theme code="cas.login.javascript.require" var="loginJsRequire" />
		<script type="text/javascript" src="<c:url value="${loginJsRequire}" />"></script>
		<script>
			require(['themes/wavity/res/index'], function() {
				require(['jquery', '../wavity/js/init/logininit'], function($, LoginInit){
					$(function() {
						var errorCode = "null";
						LoginInit(errorCode);
						console.log("Wavity login is ready.");
					});
				});
			});
		</script>
	</head>
	<body role="application" class="bodyLayout">
		<%
	    String serviceUrl = null;
	    if (request.getHeader("referer") != null) {
	        serviceUrl = request.getHeader("referer");
	    }
	    %>

		<c:set var="serviceUrl" value="<%=serviceUrl%>"/>
        <c:if test="${not empty serviceUrl}">
            <c:set var="string1" value="${serviceUrl}" />
            <c:set var="string2" value="${fn:split(string1, '//')}" />
            <c:set var="string3" value="${fn:split(string2[1], '/')}" />
            <c:set var="string4" value="${fn:split(string3[0], '.')}" />
            <c:set var="string5" value="${fn:split(string2[2], '?')}" />
            
            <c:set var="appName" value="${string5[0]}" />
            <c:set var="tenantName" value="${string4[0]}" />
        </c:if>

		<%
		String formActionUrl = "";
		// using getAttribute allows us to get the orginal url out of the page when a forward has taken place.
		String queryString = "?"+request.getAttribute("javax.servlet.forward.query_string");
		String requestURI = ""+request.getAttribute("javax.servlet.forward.request_uri");
		if(requestURI == "null") {
			// using getAttribute allows us to get the orginal url out of the page when a include has taken place.
			queryString = "?"+request.getAttribute("javax.servlet.include.query_string");
			requestURI = ""+request.getAttribute("javax.servlet.include.request_uri");
		}
		if(requestURI == "null") {
			queryString = "?"+request.getQueryString();
			requestURI = request.getRequestURI();
		}
		if(queryString.equals("?null")) queryString = "";
		
		formActionUrl = requestURI+queryString;
		%>

		<spring:theme code="standard.login.app.logo" var="defaultAppLogo" />
		<spring:theme code="standard.login.tenant.logo" var="defaultTenantLogo" />
		<input type="hidden" name="defaultAppLogo" value="${defaultAppLogo}" />
		<input type="hidden" name="defaultTenantLogo" value="${defaultTenantLogo}" />
		
		<input type="hidden" name="appName" value="${appName}" />
		<input type="hidden" name="tenantName" value="${tenantName}" />
		<input type="hidden" name="tenantLogoUrl" value="${largeLogo}" />
		<input type="hidden" name="loginTicket" value="${loginTicket}" />
		<input type="hidden" name="flowExecutionKey" value="${flowExecutionKey}" />
		<input type="hidden" name="formActionUrl" value="<%=formActionUrl %>" />
		
		
		<header role="banner" id="ot-header" class="header">
			<!-- header region -->
		</header>
		<main role="main" id="ot-main" class="main">

		</main>

		<footer role="contentinfo" id="ot-footer" class="footer">
			<!-- footer region -->
		</footer>

	</body>
</html>