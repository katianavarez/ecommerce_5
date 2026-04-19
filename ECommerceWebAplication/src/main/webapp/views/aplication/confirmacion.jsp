<%-- 
    Document   : confirmacion
    Created on : 9 abr 2026, 4:12:45 a.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Confirmación de Compra — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/cart.css"/>
    </head>
    <body>
        <header class="site-header">
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo">
                    <div class="site-logo__wordmark">Vel<span>our</span></div>
                    <div class="site-logo__sub">Moda de Autor</div>
                </a>
            </div>
        </header>

        <main>
            <div class="confirm-page">
                <div class="confirm-icon">✓</div>
                <p class="confirm-order-id">Pedido #<c:out value="${pedido.numPedido}"/></p>
                <h1 class="confirm-title">¡Gracias por tu compra!</h1>
                <p class="confirm-text">
                    Recibirás un correo de confirmación en
                    <strong><c:out value="${pedido.usuario.correo}"/></strong>
                    con todos los detalles.
                </p>

                <div class="confirm-details">
                    <div class="confirm-row"><span>Fecha</span><span><c:out value="${pedido.fecha}"/></span></div>
                    <div class="confirm-row"><span>Cliente</span><span><c:out value="${pedido.usuario.nombre}"/></span></div>
                    <div class="confirm-row">
                        <span>Envío a</span>
                        <span>
                            <c:out value="${pedido.direccionEnvio.calle}"/>,
                            <c:out value="${pedido.direccionEnvio.ciudad}"/>
                        </span>
                    </div>
                    <div class="confirm-row">
                        <span>Método de pago</span>
                        <span><c:out value="${pedido.pago.metodo}"/></span>
                    </div>
                    <div class="confirm-row">
                        <span>Estado del pedido</span>
                        <span><c:out value="${pedido.estado}"/></span>
                    </div>
                    <div class="confirm-row" style="font-weight:600;">
                        <span>Total cobrado</span>
                        <span>$<fmt:formatNumber value="${pedido.total}" maxFractionDigits="0"/></span>
                    </div>
                </div>

                <!-- Artículos del pedido -->
                <c:if test="${not empty pedido.detalles}">
                    <div style="width:100%;max-width:480px;margin:var(--sp-6) 0;text-align:left;">
                        <h3 style="font-size:var(--fs-sm);font-weight:var(--fw-medium);margin-bottom:var(--sp-4);">Artículos</h3>
                        <c:forEach var="detalle" items="${pedido.detalles}">
                            <div style="display:flex;justify-content:space-between;padding:var(--sp-3) 0;border-bottom:1px solid var(--border-soft);font-size:var(--fs-sm);">
                                <span><c:out value="${detalle.producto.nombre}"/> × ${detalle.cantidad}</span>
                                <span>$<fmt:formatNumber value="${detalle.precioUnidad * detalle.cantidad}" maxFractionDigits="0"/></span>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>

                <div style="display:flex;flex-direction:column;gap:var(--sp-3);">
                    <a href="${pageContext.request.contextPath}/app/cuenta" class="btn btn--primary btn--full btn--lg">Ver mis pedidos</a>
                    <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--outline btn--full">Seguir comprando</a>
                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>
    </body>
</html>
