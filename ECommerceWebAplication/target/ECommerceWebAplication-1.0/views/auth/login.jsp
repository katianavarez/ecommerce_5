<%-- 
    Document   : login
    Created on : 5 abr 2026, 7:53:13 p.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Iniciar Sesión — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/auth.css"/>
    </head>
    <body>
        <main>
            <div class="auth-page">
                <div class="auth-visual">
                    <div class="auth-visual__img" style="background:linear-gradient(160deg,#1A1A2E 0%,#2C1810 60%,#1A1A2E 100%);"></div>
                    <div class="auth-visual__content">
                        <div class="auth-visual__logo">Vel<span>our</span></div>
                        <p class="auth-visual__quote">"La moda es una forma de expresar quién eres sin decir una sola palabra."</p>
                        <p class="auth-visual__sub">— Colección SS 2025</p>
                    </div>
                </div>
                <div class="auth-form-side">
                    <div class="auth-form-box">
                        <a href="${pageContext.request.contextPath}/inicio" style="display:block;margin-bottom:var(--sp-8);font-family:var(--font-display);font-size:var(--fs-2xl);font-weight:var(--fw-semibold);letter-spacing:0.14em;text-transform:uppercase;color:var(--color-primary);">
                            Vel<span style="color:var(--color-secondary);">our</span>
                        </a>
                        <h1 class="auth-form-box__title">Bienvenida de vuelta</h1>
                        <p class="auth-form-box__sub">Inicia sesión para acceder a tu cuenta y pedidos.</p>

                        <c:if test="${not empty param.registered}">
                            <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                ✓ Cuenta creada exitosamente. Ya puedes iniciar sesión.
                            </div>
                        </c:if>

                        <c:if test="${not empty error}">
                            <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                <c:out value="${error}"/>
                            </div>
                        </c:if>

                        <form class="auth-form" method="POST" action="${pageContext.request.contextPath}/auth/login">
                            <c:if test="${not empty param.redirect}">
                                <input type="hidden" name="redirect" value="<c:out value='${param.redirect}'/>">
                            </c:if>
                            <div class="form-group">
                                <label class="form-label" for="correo">Correo electrónico</label>
                                <input class="form-control" type="email" id="correo" name="correo"
                                       placeholder="ana@correo.com" value="<c:out value='${correo}'/>" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="contrasena">Contraseña</label>
                                <input class="form-control" type="password" id="contrasena" name="contrasena"
                                       placeholder="••••••••" required>
                            </div>
                            <button type="submit" class="btn btn--primary btn--full btn--lg">Iniciar sesión</button>
                        </form>

                        <p class="auth-link">
                            ¿No tienes cuenta? <a href="${pageContext.request.contextPath}/auth/registro">Crear cuenta</a>
                        </p>
                        <p class="auth-link" style="margin-top:var(--sp-2);">
                            ¿Eres administrador? <a href="${pageContext.request.contextPath}/admin/login">Acceso admin</a>
                        </p>
                    </div>
                </div>
            </div>
        </main>
    </body>
</html>
