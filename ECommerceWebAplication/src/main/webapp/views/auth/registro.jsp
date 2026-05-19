<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Crear Cuenta — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/auth.css"/>
    </head>
    <body>
        <main>
            <div class="auth-page">
                <div class="auth-visual">
                    <div class="auth-visual__img" style="background:linear-gradient(160deg,#2D5A2D 0%,#1A1A2E 100%);"></div>
                    <div class="auth-visual__content">
                        <div class="auth-visual__logo">Vel<span>our</span></div>
                        <p class="auth-visual__quote">"Únete al círculo de mujeres que visten con intención y elegancia."</p>
                        <p class="auth-visual__sub">— Velour Premium Members</p>
                    </div>
                </div>

                <div class="auth-form-side">
                    <div class="auth-form-box">
                        <a href="${pageContext.request.contextPath}/inicio"
                           style="display:block;margin-bottom:var(--sp-8);font-family:var(--font-display);font-size:var(--fs-2xl);font-weight:var(--fw-semibold);letter-spacing:0.14em;text-transform:uppercase;color:var(--color-primary);">
                            Vel<span style="color:var(--color-secondary);">our</span>
                        </a>

                        <h1 class="auth-form-box__title">Crear cuenta</h1>
                        <p class="auth-form-box__sub">Únete y disfruta de envíos exclusivos, acceso anticipado y más.</p>

                        <%-- Mensaje de error del servidor --%>
                        <c:if test="${not empty error}">
                            <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                ⚠ <c:out value="${error}"/>
                            </div>
                        </c:if>

                        <div id="registroError"
                             style="display:none;background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                        </div>

                        <form class="auth-form" method="POST"
                              action="${pageContext.request.contextPath}/auth/registro"
                              novalidate id="registroForm">

                            <div class="form-group">
                                <label class="form-label" for="nombre">Nombre completo *</label>
                                <input class="form-control" type="text" id="nombre" name="nombre"
                                       placeholder="Ana García"
                                       maxlength="100" value="<c:out value='${nombre}'/>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="correo">Correo electrónico *</label>
                                <input class="form-control" type="email" id="correo" name="correo"
                                       placeholder="ana@correo.com"
                                       maxlength="100" value="<c:out value='${correo}'/>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="telefono">Teléfono</label>
                                <input class="form-control" type="tel" id="telefono" name="telefono"
                                       placeholder="+52 55 0000 0000"
                                       maxlength="20" value="<c:out value='${telefono}'/>">
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="calle">Dirección (calle y número) *</label>
                                <input class="form-control" type="text" id="calle" name="calle"
                                       placeholder="Av. Ejemplo 123" maxlength="150"
                                       value="<c:out value='${calle}'/>" required>
                            </div>

                            <div class="form-row" style="display:flex;gap:var(--sp-3);">
                                <div class="form-group" style="flex:1;">
                                    <label class="form-label" for="ciudad">Ciudad *</label>
                                    <input class="form-control" type="text" id="ciudad" name="ciudad"
                                           placeholder="Cd. Obregón" maxlength="80"
                                           value="<c:out value='${ciudad}'/>" required>
                                </div>
                                <div class="form-group" style="flex:1;">
                                    <label class="form-label" for="estado">Estado *</label>
                                    <input class="form-control" type="text" id="estado" name="estado"
                                           placeholder="Sonora" maxlength="80"
                                           value="<c:out value='${estado}'/>" required>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="codigoPostal">Código postal *</label>
                                <input class="form-control" type="text" id="codigoPostal" name="codigoPostal"
                                       placeholder="85000" maxlength="10"
                                       value="<c:out value='${codigoPostal}'/>" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="contrasena"
                                       style="display:flex;justify-content:space-between;align-items:center;">
                                    Contraseña *
                                    <span id="pwdCounter"
                                          style="font-size:var(--fs-xs);color:var(--text-muted);font-weight:normal;letter-spacing:normal;text-transform:none;">
                                        0 / 8 mín.
                                    </span>
                                </label>
                                <input class="form-control" type="password" id="contrasena" name="contrasena"
                                       placeholder="Mínimo 8 caracteres"
                                       minlength="8" maxlength="50" required>
                                <p id="pwdHint"
                                   style="display:none;margin-top:4px;font-size:var(--fs-xs);color:var(--color-error);">
                                    La contraseña debe tener al menos 8 caracteres.
                                </p>
                            </div>

                            <button type="submit" class="btn btn--primary btn--full btn--lg">
                                Crear mi cuenta
                            </button>
                        </form>

                        <p class="auth-link">
                            ¿Ya tienes cuenta?
                            <a href="${pageContext.request.contextPath}/auth/login">Iniciar sesión</a>
                        </p>
                    </div>
                </div>
            </div>
        </main>

        <%-- Registro via Fetch API (Avance 4). Si el módulo falla, el form cae al servlet web. --%>
        <script type="module" src="${pageContext.request.contextPath}/assets/js/registro.js"></script>

        <script>
            // Contador de caracteres en tiempo real
            const pwdInput   = document.getElementById('contrasena');
            const pwdCounter = document.getElementById('pwdCounter');
            const pwdHint    = document.getElementById('pwdHint');

            pwdInput.addEventListener('input', () => {
                const len = pwdInput.value.length;
                pwdCounter.textContent = len + ' / 8 mín.';
                pwdCounter.style.color = len >= 8 ? 'var(--color-success, green)' : 'var(--text-muted)';
                pwdHint.style.display  = (len > 0 && len < 8) ? 'block' : 'none';
            });

            // Validación antes de enviar
            document.getElementById('registroForm').addEventListener('submit', function(e) {
                if (pwdInput.value.length < 8) {
                    e.preventDefault();
                    pwdHint.style.display = 'block';
                    pwdInput.focus();
                }
            });
        </script>
    </body>
</html>
