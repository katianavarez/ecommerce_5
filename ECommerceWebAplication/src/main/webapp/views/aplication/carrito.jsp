<%-- 
    Document   : carrito
    Created on : 9 abr 2026, 3:56:26 a.m.
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
        <title>Carrito — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/cart.css"/>
    </head>
    <body>
        <header class="site-header">
            <div class="header-top">Envío gratis en compras mayores a $1,500 MXN &nbsp;·&nbsp;<a href="#">Ver beneficios →</a></div>
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo">
                    <div class="site-logo__wordmark">Vel<span>our</span></div>
                    <div class="site-logo__sub">Moda de Autor</div>
                </a>
                <nav class="main-nav">
                    <a href="${pageContext.request.contextPath}/inicio" class="main-nav__link">Inicio</a>
                    <a href="${pageContext.request.contextPath}/app/productos" class="main-nav__link">Colección</a>
                </nav>
                <div class="header-right">
                    <div class="header-actions">
                        <a href="${pageContext.request.contextPath}/app/cuenta" class="header-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                            </svg>
                        </a>
                        <a href="${pageContext.request.contextPath}/app/carrito" class="header-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>
                            </svg>
                            <c:if test="${cantidadItems > 0}">
                                <span class="cart-badge"><c:out value="${cantidadItems}"/></span>
                            </c:if>
                        </a>
                    </div>
                </div>
            </div>
        </header>

        <main>
            <div class="cart-layout">
                <div>
                    <h1 class="cart-title">Tu Carrito (<c:out value="${cantidadItems}"/>)</h1>

                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            <c:out value="${error}"/>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty carrito}">
                            <div style="text-align:center;padding:var(--sp-10);color:var(--text-muted);">
                                <p style="font-size:var(--fs-lg);margin-bottom:var(--sp-4);">Tu carrito está vacío</p>
                                <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--primary">Explorar colección</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cart-items">
                                <c:forEach var="item" items="${carrito}">
                                    <div class="cart-item">
                                        <a href="${pageContext.request.contextPath}/app/producto?id=${item.producto.id}" class="cart-item__img">
                                            <img src="${item.producto.imagenURL.startsWith('http') ? item.producto.imagenURL : pageContext.request.contextPath.concat('/').concat(item.producto.imagenURL)}"
                                                 alt="${item.producto.nombre}">
                                        </a>
                                        <div class="cart-item__info">
                                            <p class="cart-item__cat"><c:out value="${item.producto.categoria.nombre}"/></p>
                                            <h3 class="cart-item__name"><c:out value="${item.producto.nombre}"/></h3>
                                            <c:if test="${not empty item.talla}">
                                                <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:2px;">Talla: <strong><c:out value="${item.talla}"/></strong></p>
                                            </c:if>
                                            <p class="cart-item__price">$<fmt:formatNumber value="${item.precioUnidad}" maxFractionDigits="0"/></p>
                                        </div>
                                        <div class="cart-item__actions">
                                            <form method="POST" action="${pageContext.request.contextPath}/app/carrito" style="display:flex;gap:var(--sp-2);">
                                                <input type="hidden" name="accion" value="actualizar">
                                                <input type="hidden" name="productoId" value="${item.producto.id}">
                                                <input type="hidden" name="talla" value="${item.talla}">
                                                <div class="qty-control">
                                                    <button type="submit" name="cantidad" value="${item.cantidad - 1}" class="qty-btn">−</button>
                                                    <input class="qty-input" type="number" value="${item.cantidad}" readonly>
                                                    <button type="submit" name="cantidad" value="${item.cantidad + 1}" class="qty-btn">+</button>
                                                </div>
                                            </form>
                                            <form method="POST" action="${pageContext.request.contextPath}/app/carrito">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="productoId" value="${item.producto.id}">
                                                <input type="hidden" name="talla" value="${item.talla}">
                                                <button type="submit" class="cart-item__remove">Eliminar</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <div style="display:flex;gap:var(--sp-3);margin-top:var(--sp-6);">
                                <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--ghost btn--sm">← Seguir comprando</a>
                                <form method="POST" action="${pageContext.request.contextPath}/app/carrito">
                                    <input type="hidden" name="accion" value="vaciar">
                                    <button type="submit" class="btn btn--ghost btn--sm" style="color:var(--color-error);">Vaciar carrito</button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Resumen -->
                <c:if test="${not empty carrito}">
                    <div class="order-summary">
                        <h2 class="order-summary__title">Resumen del pedido</h2>
                        <div class="summary-lines">
                            <div class="summary-line">
                                <span>Subtotal (<c:out value="${cantidadItems}"/> artículos)</span>
                                <span>$<fmt:formatNumber value="${subtotal}" maxFractionDigits="0"/></span>
                            </div>
                            <div class="summary-line">
                                <span>Envío</span>
                                <c:choose>
                                    <c:when test="${subtotal >= 1500}">
                                        <span class="summary-line--free">Gratis</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span>$<fmt:formatNumber value="${costoEnvio}" maxFractionDigits="0"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="summary-line summary-line--total">
                                <span>Total</span>
                                <span>$<fmt:formatNumber value="${total}" maxFractionDigits="0"/></span>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/app/checkout" class="btn btn--gold btn--full btn--lg">Proceder al Pago</a>
                        <p style="text-align:center;font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-4);">
                            Pago seguro &nbsp;·&nbsp; Devoluciones gratuitas
                        </p>
                    </div>
                </c:if>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>
    </body>
</html>
