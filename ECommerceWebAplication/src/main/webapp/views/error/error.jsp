<%-- 
    Document   : error
    Created on : 5 abr 2026, 6:45:46 p.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Error — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
    </head>
    <body style="display:flex;align-items:center;justify-content:center;min-height:100vh;flex-direction:column;gap:1rem;text-align:center;">
        <h1 style="font-size:4rem;font-family:var(--font-display,serif);">
            <c:choose>
                <c:when test="${pageContext.errorData.statusCode == 403}">403</c:when>
                <c:when test="${pageContext.errorData.statusCode == 404}">404</c:when>
                <c:otherwise>500</c:otherwise>
            </c:choose>
        </h1>
        <p style="color:#666;">
            <c:choose>
                <c:when test="${pageContext.errorData.statusCode == 403}">Acceso denegado.</c:when>
                <c:when test="${pageContext.errorData.statusCode == 404}">Página no encontrada.</c:when>
                <c:otherwise>Ocurrió un error interno. Por favor intenta de nuevo.</c:otherwise>
            </c:choose>
        </p>
        <a href="${pageContext.request.contextPath}/inicio" style="margin-top:1rem;text-decoration:underline;">← Volver al inicio</a>
    </body>
</html>
