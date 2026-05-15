<%-- 
    Document   : index
    Created on : 4 abr 2026, 7:06:46 p.m.
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
        <title>Velour — Moda Premium</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/styles/paginas/home.css"/>
    </head>
    <body>

        <!-- ─── HEADER ─── -->
        <header class="site-header">
            <div class="header-top">
                Envío gratis en compras mayores a $1,500 MXN &nbsp;·&nbsp;
                <a href="${pageContext.request.contextPath}/app/productos">Ver colección →</a>
            </div>
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo">
                    <div class="site-logo__wordmark">Vel<span>our</span></div>
                    <div class="site-logo__sub">Moda de Autor</div>
                </a>

                <nav class="main-nav">
                    <a href="${pageContext.request.contextPath}/inicio" class="main-nav__link main-nav__link--active">Inicio</a>
                    <a href="${pageContext.request.contextPath}/app/productos" class="main-nav__link">Colección</a>
                    <!--<a href="productos.html" class="main-nav__link">Novedades</a>-->
                    <!-- <a href="productos.html" class="main-nav__link">Sale</a>-->
                </nav>

                <div class="header-right">
                    <div class="header-actions">
                        <%-- Icono cuenta: si hay sesión activa → cuenta, si no → login --%>
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

                    <button class="header-burger" aria-label="Menú" id="burgerBtn">
                        <span></span><span></span><span></span>
                    </button>
                </div>

            </div> 

            <nav class="mobile-nav" id="mobileNav">
                <a href="${pageContext.request.contextPath}/inicio" class="mobile-nav__link">Inicio</a>
                <a href="${pageContext.request.contextPath}/app/productos" class="mobile-nav__link">Colección</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.clienteLogueado}">
                        <a href="${pageContext.request.contextPath}/app/cuenta" class="mobile-nav__link">Mi cuenta</a>
                        <a href="${pageContext.request.contextPath}/auth/logout" class="mobile-nav__link" data-action="logout">Cerrar sesión</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/auth/login" class="mobile-nav__link">Iniciar sesión</a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/app/carrito" class="mobile-nav__link">Carrito</a>
            </nav>
        </header>

        <!-- ─── HERO ─── -->
        <section class="hero">
            <!-- Imagen placeholder con gradiente simulando foto editorial -->
            <div class="hero__bg-img" style="background: linear-gradient(135deg, #2C2C3E 0%, #4A3728 50%, #1A1A2E 100%);"></div>
            <div class="hero__bg"></div>
            <div class="hero__content">
                <p class="hero__eyebrow">Nueva Colección SS 2025</p>
                <h1 class="hero__title">La elegancia <em>redefinida</em></h1>
                <p class="hero__subtitle">Prendas diseñadas para la mujer contemporánea. Cada pieza, una obra de arte portada con intención.</p>
                <div class="hero__actions">
                    <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--gold btn--lg">Explorar Colección</a>
                    <!--Modificar más adelante-->
                    <a href="${pageContext.request.contextPath}/inicio" class="btn btn--outline btn--lg" style="color:#fff; border-color:rgba(255,255,255,0.5);">Ver Novedades</a>
                </div>
            </div>
        </section>

        <!-- ─── CATEGORÍAS ─── -->
        <section class="categories-section">
            <div class="container">
                <div class="section-header">
                    <span class="section-header__eyebrow">Explora</span>
                    <h2 class="section-header__title">Categorías</h2>
                </div>
                <div class="categories-grid">
                    <%-- Solo mostrar categorías que tienen al menos 1 producto --%>
                    <c:set var="catConProductos" value="0"/>
                    <c:forEach var="cat" items="${categorias}" varStatus="status">
                        <c:if test="${not empty conteoCategorias[cat.id] and conteoCategorias[cat.id] > 0}">
                            <%-- Primera categoría con productos lleva la clase wide --%>
                            <c:set var="esPrimera" value="${catConProductos == 0}"/>
                            <a href="${pageContext.request.contextPath}/app/productos?categoriaId=${cat.id}"
                               class="cat-card ${esPrimera ? 'cat-card--wide' : ''}"
                               style="text-decoration:none;">
                                <img src="${pageContext.request.contextPath}/assets/img/categoria/${cat.slug}.png"
                                     alt="${cat.nombre}"
                                     title="Ruta: assets/img/categoria/${cat.slug}.png"
                                     onerror="this.src='${pageContext.request.contextPath}/assets/img/categoria/default.png';this.onerror=null;">
                                <div class="cat-card__overlay">
                                    <p class="cat-card__name"><c:out value="${cat.nombre}"/></p>
                                    <p class="cat-card__count">
                                        <c:out value="${conteoCategorias[cat.id]}"/> piezas
                                    </p>
                                </div>
                            </a>
                            <c:set var="catConProductos" value="${catConProductos + 1}"/>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </section>

        <!-- ─── DESTACADOS ─── -->
        <section class="featured-section">
            <div class="container">
                <div class="section-header">
                    <span class="section-header__eyebrow">Más Vendidos</span>
                    <h2 class="section-header__title">Destacados de la Temporada</h2>
                    <p class="section-header__subtitle">Piezas cuidadosamente seleccionadas que definen el estilo esta temporada.</p>
                </div>

                <div class="products-grid">
                    <c:choose>
                        <c:when test="${not empty topProductos}">
                            <c:forEach var="prod" items="${topProductos}">
                                <c:if test="${prod.activo}">
                                <a href="${pageContext.request.contextPath}/app/producto?id=${prod.id}"
                                   class="product-card" style="text-decoration:none;">
                                    <div class="product-card__img-wrap">
                                        <img src="${prod.imagenURL.startsWith('http') ? prod.imagenURL : pageContext.request.contextPath.concat('/').concat(prod.imagenURL)}"
                                             alt="${prod.nombre}">
                                    </div>
                                    <div class="product-card__body">
                                        <p class="product-card__cat"><c:out value="${prod.categoria.nombre}"/></p>
                                        <h3 class="product-card__name"><c:out value="${prod.nombre}"/></h3>
                                        <div class="product-card__price">
                                            <span class="product-card__price-current">
                                                $<fmt:formatNumber value="${prod.precio}" maxFractionDigits="0"/>
                                            </span>
                                        </div>
                                    </div>
                                </a>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p style="color:var(--text-muted);grid-column:1/-1;text-align:center;padding:var(--sp-8);">
                                Próximamente productos destacados.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="text-align:center; margin-top: var(--sp-10);">
                    <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--outline btn--lg">Ver toda la colección</a>
                </div>
            </div>
        </section>

        <!-- ─── PROMO BANNER ─── -->
        <div class="container">
            <div class="promo-banner">
                <div class="promo-banner__text">
                    <p class="promo-banner__eyebrow">Oferta Especial</p>
                    <h2 class="promo-banner__title">Sale de fin<br>de <strong>temporada</strong></h2>
                    <p class="promo-banner__sub">Hasta 40% de descuento en piezas seleccionadas de la colección anterior. Por tiempo limitado.</p>
                    <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--gold" style="margin-top: var(--sp-6);">Ver Descuentos</a>
                </div>
                <div style="width:280px; height:320px; border-radius:var(--r-md); background: linear-gradient(135deg, #C9A96E 0%, #1A1A2E 100%); flex-shrink:0;"></div>
            </div>
        </div>

        <!-- ─── NEWSLETTER ─── -->
        <section class="newsletter-section">
            <div class="section-header">
                <span class="section-header__eyebrow">Exclusivo</span>
                <h2 class="section-header__title">Únete al círculo Velour</h2>
                <p class="section-header__subtitle">Recibe acceso anticipado a nuevas colecciones, eventos privados y ofertas exclusivas para miembros.</p>
            </div>
            <form class="newsletter-form">
                <input class="form-control" type="email" placeholder="tu@correo.com">
                <button type="submit" class="btn btn--gold">Suscribirme</button>
            </form>
        </section>

        <!-- ─── FOOTER ─── -->
        <footer class="site-footer">
            <div class="footer-top">
                <div class="footer-brand">
                    <div class="footer-brand__logo">Vel<span>our</span></div>
                    <p class="footer-brand__text">Moda premium diseñada para la mujer contemporánea. Cada prenda es una declaración de elegancia y confianza.</p>
                    <div class="footer-social">
                        <a href="#" class="footer-social__link" aria-label="Instagram">In</a>
                        <a href="#" class="footer-social__link" aria-label="Facebook">Fb</a>
                        <a href="#" class="footer-social__link" aria-label="Pinterest">Pi</a>
                        <a href="#" class="footer-social__link" aria-label="TikTok">Tk</a>
                    </div>
                </div>

                <div class="footer-col">
                    <p class="footer-col__title">Tienda</p>
                    <div class="footer-col__links">
                        <a href="${pageContext.request.contextPath}/app/productos">Nueva Colección</a>
                        <a href="${pageContext.request.contextPath}/app/productos">Vestidos</a>
                        <a href="${pageContext.request.contextPath}/app/productos">Blazers</a>
                        <a href="${pageContext.request.contextPath}/app/productos">Conjuntos</a>
                        <a href="${pageContext.request.contextPath}/app/productos">Sale</a>
                    </div>
                </div>

                <div class="footer-col">
                    <p class="footer-col__title">Ayuda</p>
                    <div class="footer-col__links">
                        <a href="#">Guía de Tallas</a>
                        <a href="#">Envíos y Devoluciones</a>
                        <a href="#">Cuidado de Prendas</a>
                        <a href="#">Preguntas Frecuentes</a>
                        <a href="#">Contacto</a>
                    </div>
                </div>

                <div class="footer-col">
                    <p class="footer-col__title">Empresa</p>
                    <div class="footer-col__links">
                        <a href="#">Sobre Velour</a>
                        <a href="#">Sustentabilidad</a>
                        <a href="#">Trabaja con Nosotros</a>
                        <a href="#">Prensa</a>
                    </div>
                </div>
            </div>

            <div class="footer-bottom">
                <p>© 2025 Velour. Todos los derechos reservados.</p>
                <div class="footer-payments">
                    <span>VISA</span>
                    <span>MC</span>
                    <span>AMEX</span>
                    <span>OXXO</span>
                    <span>PayPal</span>
                </div>
            </div>
        </footer>

        <script type="module" src="${pageContext.request.contextPath}/assets/js/logout.js"></script>

        <script>
            const burger = document.getElementById('burgerBtn');
            const mobileNav = document.getElementById('mobileNav');
            burger.addEventListener('click', () => mobileNav.classList.toggle('is-open'));
        </script>
    </body>
</html>

