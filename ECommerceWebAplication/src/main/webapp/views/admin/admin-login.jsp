<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Acceso Admin — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/auth.css"/>
    </head>
    <body>
        <main>
            <div class="auth-page">
                <div class="auth-visual">
                    <div class="auth-visual__img" style="background:linear-gradient(160deg,#1A1A2E 0%,#0d0d1a 100%);"></div>
                    <div class="auth-visual__content">
                        <div class="auth-visual__logo">Vel<span>our</span></div>
                        <p class="auth-visual__quote">Panel de Administración</p>
                        <p class="auth-visual__sub">Acceso exclusivo para administradores</p>
                    </div>
                </div>
                <div class="auth-form-side">
                    <div class="auth-form-box">
                        <a href="${pageContext.request.contextPath}/inicio" style="display:block;margin-bottom:var(--sp-8);font-family:var(--font-display);font-size:var(--fs-2xl);font-weight:var(--fw-semibold);letter-spacing:0.14em;text-transform:uppercase;color:var(--color-primary);">
                            Vel<span style="color:var(--color-secondary);">our</span>
                        </a>
                        <h1 class="auth-form-box__title">Acceso Administrador</h1>
                        <p class="auth-form-box__sub">Ingresa con tus credenciales de administrador.</p>

                        <c:if test="${not empty error}">
                            <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                <c:out value="${error}"/>
                            </div>
                        </c:if>

                        <form class="auth-form" method="POST" action="${pageContext.request.contextPath}/admin/login">
                            <div class="form-group">
                                <label class="form-label" for="correo">Correo electrónico</label>
                                <input class="form-control" type="email" id="correo" name="correo"
                                       placeholder="admin@velour.com" value="<c:out value='${correo}'/>" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="contrasena">Contraseña</label>
                                <input class="form-control" type="password" id="contrasena" name="contrasena"
                                       placeholder="••••••••" required>
                            </div>
                            <button type="submit" class="btn btn--primary btn--full btn--lg">Acceder al Panel</button>
                        </form>

                        <p class="auth-link">
                            <a href="${pageContext.request.contextPath}/auth/login">← Volver a la tienda</a>
                        </p>
                    </div>
                </div>
            </div>
        </main>
    </body>
</html>
