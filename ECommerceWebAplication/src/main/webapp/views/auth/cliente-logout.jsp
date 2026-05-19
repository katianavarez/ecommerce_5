<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Cerrando sesión — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
    </head>
    <body style="display:flex;align-items:center;justify-content:center;min-height:100vh;flex-direction:column;gap:1rem;text-align:center;">
        <p style="color:var(--text-muted);">Cerrando sesión…</p>
        <form id="logoutForm" method="POST" action="${pageContext.request.contextPath}/auth/logout">
            <noscript>
                <button type="submit" class="btn btn--primary">Confirmar cierre de sesión</button>
            </noscript>
        </form>
        <script>document.getElementById('logoutForm').submit();</script>
    </body>
</html>
