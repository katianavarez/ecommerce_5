<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Velour — Moda Premium</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/variables.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/base.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/layout.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/paginas/home.css" />
    </head>

    <body>
        <%@include file="/WEB-INF/jsp/fragments/header.jspf" %>

        <section class="hero">
            <div class="hero__bg-img" style="background: linear-gradient(135deg, #2C2C3E 0%, #4A3728 50%, #1A1A2E 100%);"></div>
            <div class="hero__bg"></div>
            <div class="hero__content">
                <p class="hero__eyebrow">Nueva Colección SS 2025</p>
                <h1 class="hero__title">La elegancia <em>redefinida</em></h1>
                <p class="hero__subtitle">Prendas diseñadas para la mujer contemporánea. Cada pieza, una obra de arte portada con intención.</p>
                <div class="hero__actions">
                    <a href="${pageContext.request.contextPath}/productos" class="btn btn--gold btn--lg">Explorar Colección</a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn--outline btn--lg" style="color:#fff; border-color:rgba(255,255,255,0.5);">Ver Novedades</a>
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

                    <div class="cat-card cat-card--wide">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/vestidos.png" alt="Vestidos">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Vestidos</p>
                            <p class="cat-card__count">24 piezas</p>
                        </div>
                    </div>

                    <div class="cat-card">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/blusas.png" alt="Blusas">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Blusas</p>
                            <p class="cat-card__count">18 piezas</p>
                        </div>
                    </div>

                    <div class="cat-card">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/conjuntoRojo.png" alt="Conjuntos">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Conjuntos</p>
                            <p class="cat-card__count">12 piezas</p>
                        </div>
                    </div>

                    <div class="cat-card">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/faldas.png" alt="Faldas">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Faldas</p>
                            <p class="cat-card__count">15 piezas</p>
                        </div>
                    </div>

                    <div class="cat-card">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/blazers.png" alt="Blazers">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Blazers</p>
                            <p class="cat-card__count">9 piezas</p>
                        </div>
                    </div>

                    <div class="cat-card">
                        <img src="${pageContext.request.contextPath}/assets/imgs/categoria/accesorios.png" alt="Accesorios">
                        <div class="cat-card__overlay">
                            <p class="cat-card__name">Accesorios</p>
                            <p class="cat-card__count">30 piezas</p>
                        </div>
                    </div>

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

                    <div class="product-card">
                        <div class="product-card__img-wrap">
                            <img src="${pageContext.request.contextPath}/assets/imgs/vestidos/vestidoAzulLargo.png" alt="Vestido Azul Largo">
                            <div class="product-card__badge">
                                <span class="badge badge--new">Nuevo</span>
                            </div>
                        </div>
                        <div class="product-card__body">
                            <p class="product-card__cat">Vestidos</p>
                            <h3 class="product-card__name">Vestido Azul Largo</h3>
                            <div class="product-card__price">
                                <span class="product-card__price-current">$2,890</span>
                            </div>
                        </div>
                    </div>

                    <div class="product-card">
                        <div class="product-card__img-wrap">
                            <img src="${pageContext.request.contextPath}/assets/imgs/blazers/blazerAzulRayas.png" alt="Blazer Estructurado Rayas">
                            <div class="product-card__badge">
                                <span class="badge badge--sale">–25%</span>
                            </div>
                        </div>
                        <div class="product-card__body">
                            <p class="product-card__cat">Blazers</p>
                            <h3 class="product-card__name">Blazer Estructurado Rayas</h3>
                            <div class="product-card__price">
                                <span class="product-card__price-current">$1,890</span>
                                <span class="product-card__price-original">$2,520</span>
                            </div>
                        </div>
                    </div>

                    <div class="product-card">
                        <div class="product-card__img-wrap">
                            <img src="${pageContext.request.contextPath}/assets/imgs/conjuntos/conjuntoNegroCasual.png" alt="Conjunto Negro Casual">
                        </div>
                        <div class="product-card__body">
                            <p class="product-card__cat">Conjuntos</p>
                            <h3 class="product-card__name">Conjunto Negro Casual</h3>
                            <div class="product-card__price">
                                <span class="product-card__price-current">$3,450</span>
                            </div>
                        </div>
                    </div>

                    <div class="product-card">
                        <div class="product-card__img-wrap">
                            <img src="${pageContext.request.contextPath}/assets/imgs/blusas/blusaCruzadaEstampadoGris.png" alt="Blusa Estampado Gris">
                            <div class="product-card__badge">
                                <span class="badge badge--new">Nuevo</span>
                            </div>
                        </div>
                        <div class="product-card__body">
                            <p class="product-card__cat">Blusas</p>
                            <h3 class="product-card__name">Blusa Estampado Gris</h3>
                            <div class="product-card__price">
                                <span class="product-card__price-current">$1,290</span>
                            </div>
                        </div>
                    </div>

                </div>

                <div style="text-align:center; margin-top: var(--sp-10);">
                    <a href="${pageContext.request.contextPath}/productos" class="btn btn--outline btn--lg">Ver toda la colección</a>
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
                    <a href="${pageContext.request.contextPath}/productos" class="btn btn--gold" style="margin-top: var(--sp-6);">Ver Descuentos</a>
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
        <%@include file="/WEB-INF/jsp/fragments/footer.jspf" %>

        <script>
            const burger = document.getElementById('burgerBtn');
            const mobileNav = document.getElementById('mobileNav');
            burger.addEventListener('click', () => mobileNav.classList.toggle('is-open'));
        </script>
    </body>
</html>
