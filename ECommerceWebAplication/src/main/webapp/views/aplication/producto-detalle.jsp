<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="producto-id" content="<c:out value='${productoId}'/>">
        <%@ include file="/WEB-INF/jspf/jwt-meta.jspf" %>
        <title>Producto — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/product-detail.css"/>
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
                    <a href="${pageContext.request.contextPath}/app/productos" class="main-nav__link main-nav__link--active">Colección</a>
                </nav>
                <div class="header-right">
                    <div class="header-actions">
                        <c:choose>
                            <c:when test="${not empty sessionScope.clienteLogueado}">
                                <a href="${pageContext.request.contextPath}/app/cuenta" class="header-icon" aria-label="Mi cuenta">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/auth/login" class="header-icon" aria-label="Iniciar sesión">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                                    </svg>
                                </a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/app/carrito" class="header-icon" aria-label="Carrito">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>
                            </svg>
                            <c:set var="totalCarrito" value="0"/>
                            <c:forEach var="_item" items="${sessionScope.carrito}">
                                <c:set var="totalCarrito" value="${totalCarrito + _item.cantidad}"/>
                            </c:forEach>
                            <c:if test="${totalCarrito > 0}">
                                <span class="cart-badge"><c:out value="${totalCarrito}"/></span>
                            </c:if>
                        </a>
                    </div>
                    <button class="header-burger" id="burgerBtn"><span></span><span></span><span></span></button>
                </div>
            </div>
        </header>

        <main>
            <div class="container" style="padding-top:var(--sp-5);">
                <nav class="shop-breadcrumb">
                    <a href="${pageContext.request.contextPath}/inicio">Inicio</a>
                    <span class="shop-breadcrumb__sep">›</span>
                    <a href="${pageContext.request.contextPath}/app/productos">Colección</a>
                    <span class="shop-breadcrumb__sep">›</span>
                    <span id="breadcrumbNombre">Cargando…</span>
                </nav>
            </div>

            <%-- Spinner / mensaje de carga --%>
            <div id="loadingState" style="text-align:center;padding:var(--sp-10);color:var(--text-muted);">
                <p>Cargando producto…</p>
            </div>

            <%-- Mensaje de error si falla la carga --%>
            <div id="errorState" style="display:none;text-align:center;padding:var(--sp-10);color:var(--color-error);">
                <p id="errorMessage">No se pudo cargar el producto.</p>
                <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--primary" style="margin-top:var(--sp-4);">
                    Volver a la colección
                </a>
            </div>

            <%-- Contenedor principal del detalle: lo llena el JS --%>
            <div class="detail-layout" id="detailLayout" style="display:none;">
                <div class="product-gallery">
                    <div class="gallery-thumbs"></div>
                    <div class="gallery-main">
                        <img src="" alt="" id="mainImage">
                    </div>
                </div>
                <div class="product-info" id="productInfo">
                    <%-- El JS inyecta categoría, nombre, rating, precio, descripción,
                         selector de talla, stock y form de carrito --%>
                </div>
            </div>

            <%-- Tab de reseñas: contenedor que el JS llena --%>
            <div class="product-tabs" id="resenasTabs" style="display:none;">
                <div class="tabs-nav">
                    <button class="tab-btn tab-btn--active" data-tab="resenas">
                        Reseñas (<span id="totalResenasCount">0</span>)
                    </button>
                </div>
                <div class="tab-content tab-content--active" id="tab-resenas">
                    <div id="resenasContainer"></div>
                    <hr style="margin:var(--sp-6) 0;border:none;border-top:1px solid var(--border-soft);">
                    <div id="formResenaContainer"></div>
                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>

        <script type="module" src="${pageContext.request.contextPath}/assets/js/producto-detalle.js"></script>
        <script type="module" src="${pageContext.request.contextPath}/assets/js/header-badge.js"></script>
    </body>
</html>
