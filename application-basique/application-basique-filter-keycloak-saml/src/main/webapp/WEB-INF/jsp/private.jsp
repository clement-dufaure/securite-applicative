<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Insert title here</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css" type="text/css" />
<link rel="icon" href="icone.ico" type='image/x-icon' />
</head>
<body>
	<h1>Page privée</h1>
	
	<c:if test="${secure}">
		<h2>Connexion sécurisé par HTTPS</h2>
	</c:if>
	<c:if test="${!secure}">
		<h2>Connexion en clair</h2>
	</c:if>

	<h2>
		Authentification par
		<c:out value="${typeAuthentification}" />
	</h2>

	<a href="?GLO=true">Se déconnecter</a>

	<p>
		Vous êtes
		<c:out value="${uid}" />
	</p>
	<p>
		idminefi :
		<c:out value="${idminefi}" />
	</p>
	<!--  <p>
		Vos rôles :
		<c:out value="${roles}" />
	</p>

	<p>
		La reponse du WS est :
		<c:out value="${reponseWS}"></c:out>
	</p>-->


</body>
</html>

