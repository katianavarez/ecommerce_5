<%-- 
    Document   : productos
    Created on : 9 abr 2026, 3:48:47 a.m.
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
        <title>Colección — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/products.css"/>
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
                <form class="search-form" role="search" method="GET" action="${pageContext.request.contextPath}/app/productos">
                    <input class="search-form__input" type="search" name="busqueda"
                           placeholder="Buscar prendas..." value="${busqueda}">
                    <button class="search-form__btn" type="submit" aria-label="Buscar">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                        <circle cx="11" cy="11" r="7"/><path d="m21 21-4.35-4.35"/>
                        </svg>
                    </button>
                </form>
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
                    <button class="header-burger" aria-label="Menú" id="burgerBtn"><span></span><span></span><span></span></button>
                </div>
            </div>
            <nav class="mobile-nav" id="mobileNav">
                <a href="${pageContext.request.contextPath}/inicio" class="mobile-nav__link">Inicio</a>
                <a href="${pageContext.request.contextPath}/app/productos" class="mobile-nav__link">Colección</a>
                <a href="${pageContext.request.contextPath}/auth/login" class="mobile-nav__link">Mi cuenta</a>
                <a href="${pageContext.request.contextPath}/app/carrito" class="mobile-nav__link">Carrito</a>
            </nav>
        </header>

        <main>
            <div class="shop-layout">
                <!-- Sidebar filtros -->
                <%-- ── SIDEBAR FILTROS ─────────────────────────────── --%>
                <aside class="filters-sidebar">
                    <p class="filters-sidebar__title">Filtros</p>

                    <form id="formFiltros" method="GET" action="${pageContext.request.contextPath}/app/productos">

                        <%-- Categoría — checkboxes múltiples --%>
                        <div class="filter-group">
                            <p class="filter-group__label">Categoría <span>▾</span></p>
                            <div class="filter-options">
                                <c:forEach var="cat" items="${categorias}">
                                    <label class="filter-option">
                                        <input type="checkbox" name="categoriaId" value="${cat.id}"
                                               onchange="aplicarFiltros()"
                                               ${categoriasActivas.contains(cat.id) ? 'checked' : ''}>
                                        <c:out value="${cat.nombre}"/>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>

                        <hr class="divider">

                        <%-- Talla --%>
                        <div class="filter-group">
                            <p class="filter-group__label">Talla <span>▾</span></p>
                            <div class="filter-options" style="flex-direction:row;flex-wrap:wrap;gap:var(--sp-2);">
                                <c:forEach var="talla" items="${tallasEnum}">
                                    <label style="border:1.5px solid ${tallasActivas.contains(talla.name()) ? 'var(--color-secondary)' : 'var(--border-soft)'};padding:4px 10px;border-radius:var(--r-sm);font-size:var(--fs-xs);cursor:pointer;transition:border-color 0.15s;">
                                        <input type="checkbox" name="talla" value="${talla}"
                                               style="display:none;"
                                               onchange="aplicarFiltros()"
                                               ${tallasActivas.contains(talla.name()) ? 'checked' : ''}>
                                        <c:out value="${talla}"/>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>

                        <hr class="divider">

                        <%-- Color --%>
                        <div class="filter-group">
                            <p class="filter-group__label">Color <span>▾</span></p>
                            <div class="filter-colors" style="display:flex;flex-wrap:wrap;gap:var(--sp-2);">
                                <c:forEach var="col" items="${coloresEnum}">
                                    <div class="color-swatch ${colorActivo == col.name() ? 'color-swatch--active' : ''}"
                                         title="${col.nombre}"
                                         data-color="${col.name()}"
                                         onclick="seleccionarColorFiltro(this)"
                                         style="background:${col.hex};width:24px;height:24px;border-radius:50%;cursor:pointer;border:2px solid ${colorActivo == col.name() ? 'var(--color-secondary)' : 'transparent'};box-shadow:0 0 0 1px var(--border-soft);">
                                    </div>
                                </c:forEach>
                            </div>
                            <input type="hidden" name="color" id="colorFiltroInput" value="${colorActivo}">
                            <p id="colorFiltroLabel" style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-2);">
                                <c:choose>
                                    <c:when test="${not empty colorActivo}">Color: <strong>${colorActivo}</strong> — <a href="#" onclick="limpiarColor();return false;" style="color:var(--color-secondary);">× Quitar</a></c:when>
                                    <c:otherwise>Todos los colores</c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <hr class="divider">

                        <%-- Precio máximo --%>
                        <div class="filter-group">
                            <p class="filter-group__label">Precio máximo <span>▾</span></p>
                            <div class="price-range">
                                <input type="range" name="precioMax" id="sliderPrecio"
                                       min="0" max="${precioMaximo}"
                                       value="${not empty precioMaxActivo ? precioMaxActivo : precioMaximo}"
                                       oninput="actualizarPrecio(this.value)"
                                       onchange="aplicarFiltros()">
                                <div class="price-range__labels">
                                    <span>$0</span>
                                    <span id="precioLabel">
                                        $<fmt:formatNumber value="${not empty precioMaxActivo ? precioMaxActivo : precioMaximo}" maxFractionDigits="0"/>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <hr class="divider">

                        <%-- Disponibilidad --%>
                        <div class="filter-group">
                            <p class="filter-group__label">Disponibilidad <span>▾</span></p>
                            <div class="filter-options">
                                <label class="filter-option">
                                    <input type="checkbox" name="soloConStock" value="true"
                                           onchange="aplicarFiltros()"
                                           ${soloConStockActivo ? 'checked' : ''}>
                                    Solo en existencia
                                </label>
                            </div>
                        </div>

                    </form>

                    <button class="btn btn--outline btn--sm btn--full" style="margin-top:var(--sp-2);"
                            onclick="window.location='${pageContext.request.contextPath}/app/productos'">
                        Limpiar filtros
                    </button>
                </aside>

                <!-- Main -->
                <div class="shop-main">
                    <div class="shop-header">
                        <div>
                            <nav class="shop-breadcrumb">
                                <a href="${pageContext.request.contextPath}/inicio">Inicio</a>
                                <span class="shop-breadcrumb__sep">›</span>
                                <span>Colección</span>
                            </nav>
                            <h1 class="shop-title">
                                <c:choose>
                                    <c:when test="${not empty busqueda}">Resultados para: "<c:out value="${busqueda}"/>"</c:when>
                                    <c:otherwise>Toda la Colección</c:otherwise>
                                </c:choose>
                            </h1>
                        </div>
                    </div>

                    <!-- Grid de productos -->
                    <c:choose>
                        <c:when test="${empty productos}">
                            <div style="text-align:center;padding:var(--sp-12);color:var(--text-muted);">
                                <p style="font-size:var(--fs-lg);">No se encontraron productos</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="products-grid products-grid--3">
                                <c:forEach var="producto" items="${productos}">
                                    <a href="${pageContext.request.contextPath}/app/producto?id=${producto.id}"
                                       class="product-card" style="text-decoration:none;">
                                        <div class="product-card__img-wrap">
                                            <img src="${producto.imagenURL.startsWith('http') ? producto.imagenURL : pageContext.request.contextPath.concat('/').concat(producto.imagenURL)}"
                                                 alt="${producto.nombre}">
                                        </div>
                                        <div class="product-card__body">
                                            <p class="product-card__cat"><c:out value="${producto.categoria.nombre}"/></p>
                                            <h3 class="product-card__name"><c:out value="${producto.nombre}"/></h3>
                                            <div class="product-card__price" style="margin-top:var(--sp-2);">
                                                <span class="product-card__price-current">
                                                    $<fmt:formatNumber value="${producto.precio}" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Paginación -->
                    <c:if test="${totalProductos > 0}">
                        <div style="display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);margin-top:var(--sp-8);flex-wrap:wrap;">
                            <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                Mostrando
                                <strong><c:out value="${(paginaActual - 1) * 6 + 1}"/>&#8211;<c:out value="${paginaActual * 6 > totalProductos ? totalProductos : paginaActual * 6}"/></strong>
                                de <strong><c:out value="${totalProductos}"/></strong> productos
                            </p>
                            <c:if test="${totalPaginas > 1}">
                                <%-- Formulario base oculto con todos los filtros activos.
                                     Los botones de página envían este form con la página que corresponde. --%>
                                <form id="formPaginacion" method="GET"
                                      action="${pageContext.request.contextPath}/app/productos"
                                      style="display:none;">
                                    <input type="hidden" name="pagina" id="inputPagina" value="1">
                                    <c:if test="${not empty busqueda}">
                                        <input type="hidden" name="busqueda" value="${busqueda}">
                                    </c:if>
                                    <c:forEach var="catId" items="${categoriasActivas}">
                                        <input type="hidden" name="categoriaId" value="${catId}">
                                    </c:forEach>
                                    <c:forEach var="t" items="${tallasActivas}">
                                        <input type="hidden" name="talla" value="${t}">
                                    </c:forEach>
                                    <c:if test="${not empty colorActivo}">
                                        <input type="hidden" name="color" value="${colorActivo}">
                                    </c:if>
                                    <c:if test="${not empty precioMaxActivo}">
                                        <input type="hidden" name="precioMax" value="${precioMaxActivo}">
                                    </c:if>
                                    <c:if test="${soloConStockActivo}">
                                        <input type="hidden" name="soloConStock" value="true">
                                    </c:if>
                                </form>

                                <nav class="pagination" style="margin-top:0;flex-shrink:0;" aria-label="Paginación">
                                    <c:if test="${paginaActual > 1}">
                                        <button type="button" class="pagination__btn"
                                                onclick="irAPagina(${paginaActual - 1})">&#8249;</button>
                                    </c:if>
                                    <c:forEach var="i" begin="1" end="${totalPaginas}">
                                        <c:choose>
                                            <c:when test="${i == paginaActual}">
                                                <a class="pagination__btn pagination__btn--active" href="#">${i}</a>
                                            </c:when>
                                            <c:when test="${i == 1 || i == totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)}">
                                                <button type="button" class="pagination__btn"
                                                        onclick="irAPagina(${i})"><c:out value="${i}"/></button>
                                            </c:when>
                                            <c:when test="${i == paginaActual - 2 || i == paginaActual + 2}">
                                                <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                    <c:if test="${paginaActual < totalPaginas}">
                                        <button type="button" class="pagination__btn"
                                                onclick="irAPagina(${paginaActual + 1})">&#8250;</button>
                                    </c:if>
                                </nav>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>

        <script>
            function irAPagina(num) {
                document.getElementById('inputPagina').value = num;
                document.getElementById('formPaginacion').submit();
            }

            document.getElementById('burgerBtn').addEventListener('click', () =>
                document.getElementById('mobileNav').classList.toggle('is-open'));

            // ── Aplicar filtros automáticamente al cambiar checkboxes ──
            function aplicarFiltros() {
                document.getElementById('formFiltros').submit();
            }

            // ── Slider de precio ──────────────────────────────────────
            function actualizarPrecio(val) {
                const formatted = '$' + Math.round(val).toLocaleString('es-MX');
                document.getElementById('precioLabel').textContent = formatted;
            }

            // ── Filtro de color ───────────────────────────────────────
            function seleccionarColorFiltro(el) {
                const colorActual = document.getElementById('colorFiltroInput').value;
                const colorNuevo  = el.dataset.color;
                // Si se hace click en el mismo color activo, lo deselecciona
                if (colorActual === colorNuevo) {
                    limpiarColor();
                    return;
                }
                document.querySelectorAll('.color-swatch').forEach(s => {
                    s.style.border = '2px solid transparent';
                });
                el.style.border = '2px solid var(--color-secondary)';
                document.getElementById('colorFiltroInput').value = colorNuevo;
                aplicarFiltros();
            }

            function limpiarColor() {
                document.getElementById('colorFiltroInput').value = '';
                document.querySelectorAll('.color-swatch').forEach(s => {
                    s.style.border = '2px solid transparent';
                });
                aplicarFiltros();
            }

            // ── Resaltar tallas seleccionadas ─────────────────────────
            document.querySelectorAll('input[name="talla"]').forEach(cb => {
                const label = cb.closest('label');
                cb.addEventListener('change', () => {
                    label.style.borderColor = cb.checked ? 'var(--color-secondary)' : 'var(--border-soft)';
                });
            });
        </script>
    </body>
</html>
