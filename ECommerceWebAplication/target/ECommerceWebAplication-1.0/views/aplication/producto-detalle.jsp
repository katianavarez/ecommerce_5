<%-- 
    Document   : producto-detalle
    Created on : 9 abr 2026, 4:04:25 a.m.
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
        <title>${producto.nombre} — Velour</title>
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
                        <a href="${pageContext.request.contextPath}/app/cuenta" class="header-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                            </svg>
                        </a>
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
                    <span><c:out value="${producto.nombre}"/></span>
                </nav>
            </div>

            <div class="detail-layout">
                <!-- Galería -->
                <div class="product-gallery">
                    <%-- Columna de miniaturas (reserva el espacio de 80px del grid) --%>
                    <div class="gallery-thumbs"></div>

                    <div class="gallery-main">
                        <img src="${producto.imagenURL.startsWith('http') ? producto.imagenURL : pageContext.request.contextPath.concat('/').concat(producto.imagenURL)}"
                             alt="${producto.nombre}" id="mainImage">
                    </div>
                </div>

                <!-- Info -->
                <div class="product-info">
                    <p class="product-info__category">
                        <c:out value="${producto.categoria.nombre}"/>
                    </p>
                    <h1 class="product-info__name"><c:out value="${producto.nombre}"/></h1>

                    <c:if test="${promedio > 0}">
                        <div class="product-info__rating">
                            <div class="stars">
                                <%-- Mostrar estrellas según promedio redondeado --%>
                                <c:choose>
                                    <c:when test="${promedio >= 4.5}">★★★★★</c:when>
                                    <c:when test="${promedio >= 3.5}">★★★★☆</c:when>
                                    <c:when test="${promedio >= 2.5}">★★★☆☆</c:when>
                                    <c:when test="${promedio >= 1.5}">★★☆☆☆</c:when>
                                    <c:otherwise>★☆☆☆☆</c:otherwise>
                                </c:choose>
                            </div>
                            <span class="rating-count">
                                <fmt:formatNumber value="${promedio}" maxFractionDigits="1"/> · ${totalResenas} reseñas
                            </span>
                        </div>
                    </c:if>

                    <div class="product-info__price">
                        <span class="price-current">$<fmt:formatNumber value="${producto.precio}" maxFractionDigits="0"/></span>
                    </div>

                    <p class="product-info__shipping">
                        <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                        Envío gratis — llega en 3–5 días hábiles
                    </p>

                    <hr class="product-info__divider">

                    <p style="font-size:var(--fs-sm);color:var(--text-muted);line-height:1.8;">
                        <c:out value="${producto.descripcion}"/>
                    </p>

                    <%-- Selector de talla interactivo --%>
                    <c:if test="${not empty producto.tallasDisponibles}">
                        <div class="size-selector" style="margin-top:var(--sp-4);">
                            <p class="size-selector__title">
                                Talla: <strong id="tallaSeleccionadaLabel" style="color:var(--color-secondary);">—</strong>
                                <span id="tallaHint" style="font-size:var(--fs-xs);color:var(--color-error);display:none;margin-left:8px;">
                                    Selecciona una talla
                                </span>
                            </p>
                            <div class="size-options">
                                <c:forEach var="talla" items="${producto.tallasDisponibles}">
                                    <%-- Buscar la cantidad de esta talla en la lista de stock --%>
                                    <c:set var="cantidadTalla" value="0"/>
                                    <c:forEach var="st" items="${stockPorTallaList}">
                                        <c:if test="${st.talla == talla}">
                                            <c:set var="cantidadTalla" value="${st.cantidad}"/>
                                        </c:if>
                                    </c:forEach>
                                    <c:choose>
                                        <c:when test="${cantidadTalla <= 0}">
                                            <%-- Sin stock: botón deshabilitado con línea cruzada --%>
                                            <button type="button"
                                                    class="size-btn size-btn--disabled"
                                                    disabled
                                                    title="Sin stock">
                                                <c:out value="${talla}"/>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <%-- Con stock: botón activo --%>
                                            <button type="button"
                                                    class="size-btn"
                                                    onclick="seleccionarTalla(this, '<c:out value="${talla}"/>')">
                                                <c:out value="${talla}"/>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>

                    <%-- Stock con color según disponibilidad --%>
                    <c:choose>
                        <c:when test="${producto.stock == 0}">
                            <p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-error);">Agotado</p>
                        </c:when>
                        <c:when test="${producto.stock <= 5}">
                            <p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-warning);">
                                ¡Solo <c:out value="${producto.stock}"/> disponibles!
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-success, green);">En existencia</p>
                        </c:otherwise>
                    </c:choose>

                    <%-- Formulario agregar al carrito — con validación de talla --%>
                    <c:if test="${producto.stock > 0}">
                        <form method="POST" action="${pageContext.request.contextPath}/app/carrito"
                              id="formCarrito" onsubmit="return validarTalla()">
                            <input type="hidden" name="accion" value="agregar">
                            <input type="hidden" name="productoId" value="${producto.id}">
                            <%-- Campo talla: solo se envía si el producto tiene tallas disponibles --%>
                            <c:if test="${not empty producto.tallasDisponibles}">
                                <input type="hidden" name="talla" id="tallaInput" value="">
                            </c:if>
                            <div class="product-actions" style="margin-top:var(--sp-5);">
                                <div class="qty-control">
                                    <button type="button" class="qty-btn" onclick="changeQty(-1)">−</button>
                                    <input class="qty-input" type="number" name="cantidad" value="1"
                                           min="1" max="${producto.stock}" id="qtyInput" readonly>
                                    <button type="button" class="qty-btn" onclick="changeQty(1)">+</button>
                                </div>
                                <button type="submit" class="btn btn--gold btn--lg" style="flex:1;">
                                    Agregar al Carrito
                                </button>
                            </div>
                        </form>
                    </c:if>
                </div>
            </div>

            <!-- Reseñas -->
            <div class="product-tabs">
                <div class="tabs-nav">
                    <button class="tab-btn tab-btn--active" data-tab="resenas">
                        Reseñas (<c:out value="${totalResenas}"/>)
                    </button>
                </div>
                <div class="tab-content tab-content--active" id="tab-resenas">

                    <%-- Mensaje de éxito tras publicar reseña --%>
                    <c:if test="${not empty param.resenaOk}">
                        <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                            ✓ Tu reseña fue publicada correctamente. ¡Gracias!
                        </div>
                    </c:if>
                    <c:if test="${not empty errorResena}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                            ⚠ <c:out value="${errorResena}"/>
                        </div>
                    </c:if>

                    <%-- Reseñas propias del usuario logueado (todas las suyas) --%>
                    <c:if test="${not empty resenasUsuario}">
                        <p style="font-size:var(--fs-xs);letter-spacing:0.1em;text-transform:uppercase;color:var(--color-secondary);font-weight:var(--fw-medium);margin-bottom:var(--sp-3);">
                            Tus reseñas
                        </p>
                        <div class="reviews-list" style="margin-bottom:var(--sp-6);">
                            <c:forEach var="resena" items="${resenasUsuario}">
                                <div class="review-card" style="border-left:2px solid var(--color-secondary);">
                                    <div class="review-card__header">
                                        <div>
                                            <p class="review-author"><c:out value="${resena.usuario.nombre}"/></p>
                                            <div class="stars">
                                                <c:forEach begin="1" end="${resena.calificacion}">★</c:forEach>
                                            </div>
                                        </div>
                                        <p class="review-date"><c:out value="${resena.fecha}"/></p>
                                    </div>
                                    <p class="review-text"><c:out value="${resena.comentario}"/></p>
                                </div>
                            </c:forEach>
                        </div>
                        <c:if test="${not empty resenasOtros}">
                            <hr style="margin-bottom:var(--sp-5);border:none;border-top:1px solid var(--border-soft);">
                        </c:if>
                    </c:if>

                    <%-- Reseñas de otros usuarios (últimas 5) --%>
                    <c:choose>
                        <c:when test="${empty resenasOtros and empty resenasUsuario}">
                            <p style="color:var(--text-muted);margin-bottom:var(--sp-6);">Aún no hay reseñas para este producto.</p>
                        </c:when>
                        <c:when test="${not empty resenasOtros}">
                            <c:if test="${not empty resenasUsuario}">
                                <p style="font-size:var(--fs-xs);letter-spacing:0.1em;text-transform:uppercase;color:var(--text-muted);font-weight:var(--fw-medium);margin-bottom:var(--sp-3);">
                                    Reseñas recientes
                                </p>
                            </c:if>
                            <div class="reviews-list" style="margin-bottom:var(--sp-6);">
                                <c:forEach var="resena" items="${resenasOtros}">
                                    <div class="review-card">
                                        <div class="review-card__header">
                                            <div>
                                                <p class="review-author"><c:out value="${resena.usuario.nombre}"/></p>
                                                <div class="stars">
                                                    <c:forEach begin="1" end="${resena.calificacion}">★</c:forEach>
                                                </div>
                                            </div>
                                            <p class="review-date"><c:out value="${resena.fecha}"/></p>
                                        </div>
                                        <p class="review-text"><c:out value="${resena.comentario}"/></p>
                                    </div>
                                </c:forEach>
                            </div>
                            <%-- Indicador si hay más reseñas que no se muestran --%>
                            <c:if test="${totalResenas > 5 and empty resenasUsuario}">
                                <p style="font-size:var(--fs-xs);color:var(--text-muted);text-align:center;padding:var(--sp-2);">
                                    Mostrando las 5 reseñas más recientes de <strong><c:out value="${totalResenas}"/></strong> en total.
                                </p>
                            </c:if>
                            <c:if test="${totalResenas > (5 + resenasUsuario.size()) and not empty resenasUsuario}">
                                <p style="font-size:var(--fs-xs);color:var(--text-muted);text-align:center;padding:var(--sp-2);">
                                    Mostrando las 5 reseñas más recientes de otros clientes.
                                    Total de reseñas: <strong><c:out value="${totalResenas}"/></strong>.
                                </p>
                            </c:if>
                        </c:when>
                    </c:choose>

                    <%-- Formulario para publicar reseña --%>
                    <hr style="margin-bottom:var(--sp-6);border:none;border-top:1px solid var(--border-soft);">

                    <c:choose>
                        <%-- No logueado --%>
                        <c:when test="${empty usuarioLogueado}">
                            <div style="padding:var(--sp-5);background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-md);text-align:center;">
                                <p style="color:var(--text-muted);font-size:var(--fs-sm);margin-bottom:var(--sp-3);">
                                    Inicia sesión para dejar una reseña.
                                </p>
                                <a href="${pageContext.request.contextPath}/auth/login?redirect=/app/producto%3Fid=${producto.id}"
                                   class="btn btn--primary btn--sm">Iniciar sesión</a>
                            </div>
                        </c:when>
                        <%-- Logueado pero NO compró el producto --%>
                        <c:when test="${not yaCompro}">
                            <div style="padding:var(--sp-5);background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-md);text-align:center;">
                                <p style="color:var(--text-muted);font-size:var(--fs-sm);">
                                    Solo puedes reseñar productos que hayas comprado.
                                </p>
                            </div>
                        </c:when>
                        <%-- Compró pero ya agotó sus reseñas permitidas --%>
                        <c:when test="${yaCompro and not puedeResenar}">
                            <div style="padding:var(--sp-5);background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-md);text-align:center;">
                                <p style="color:var(--text-muted);font-size:var(--fs-sm);">
                                    Ya dejaste el máximo de reseñas permitidas para este producto.
                                </p>
                            </div>
                        </c:when>
                        <%-- Puede reseñar → mostrar formulario con reseñas restantes --%>
                        <c:otherwise>
                            <div style="max-width:540px;">
                                <h4 style="font-size:var(--fs-md);font-weight:var(--fw-medium);margin-bottom:var(--sp-2);">
                                    Escribe tu reseña
                                </h4>
                                <c:if test="${resenasRestantes > 0}">
                                    <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-bottom:var(--sp-4);">
                                        Puedes dejar <strong><c:out value="${resenasRestantes}"/></strong>
                                        reseña<c:if test="${resenasRestantes > 1}">s</c:if> más para este producto.
                                    </p>
                                </c:if>
                                <form method="POST" action="${pageContext.request.contextPath}/app/producto">
                                    <input type="hidden" name="productoId" value="${producto.id}">

                                    <%-- Calificación con estrellas clickeables --%>
                                    <div class="form-group" style="margin-bottom:var(--sp-4);">
                                        <label class="form-label">Calificación *</label>
                                        <div id="starRating" style="display:flex;gap:4px;font-size:1.8rem;cursor:pointer;margin-top:4px;">
                                            <span class="star" data-val="1" style="color:var(--border-medium);">★</span>
                                            <span class="star" data-val="2" style="color:var(--border-medium);">★</span>
                                            <span class="star" data-val="3" style="color:var(--border-medium);">★</span>
                                            <span class="star" data-val="4" style="color:var(--border-medium);">★</span>
                                            <span class="star" data-val="5" style="color:var(--border-medium);">★</span>
                                        </div>
                                        <input type="hidden" name="calificacion" id="calificacionInput" value="" required>
                                        <p id="starHint" style="font-size:var(--fs-xs);color:var(--color-error);display:none;margin-top:4px;">Selecciona una calificación.</p>
                                    </div>

                                    <div class="form-group" style="margin-bottom:var(--sp-4);">
                                        <label class="form-label" for="comentario">Comentario *</label>
                                        <textarea class="form-control" id="comentario" name="comentario"
                                                  rows="4" placeholder="Cuéntanos tu experiencia con este producto..."
                                                  style="resize:vertical;" required></textarea>
                                    </div>

                                    <button type="submit" class="btn btn--primary" id="btnResena">
                                        Publicar reseña
                                    </button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>

                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>

        <script>
            function changeQty(delta) {
                const input = document.getElementById('qtyInput');
                const val = parseInt(input.value) + delta;
                const max = parseInt(input.max);
                if (val >= 1 && val <= max) input.value = val;
            }

            // Stock actual por talla — desde el servidor (refleja compras realizadas)
            const stockPorTalla = {
                <c:forEach var="st" items="${stockPorTallaList}" varStatus="vs">
                    '${st.talla}': ${st.cantidad}<c:if test="${!vs.last}">,</c:if>
                </c:forEach>
            };

            // Selección de talla — ignora botones disabled
            function seleccionarTalla(btn, talla) {
                // No hacer nada si está deshabilitado
                if (btn.disabled || btn.classList.contains('size-btn--disabled')) return;
                // Quitar activo de todos los que NO están disabled
                document.querySelectorAll('.size-btn:not(.size-btn--disabled)').forEach(b => b.classList.remove('size-btn--active'));
                // Marcar el seleccionado
                btn.classList.add('size-btn--active');
                // Actualizar hidden input y label
                const input = document.getElementById('tallaInput');
                if (input) input.value = talla;
                const label = document.getElementById('tallaSeleccionadaLabel');
                if (label) label.textContent = talla;
                // Ocultar hint de error
                const hint = document.getElementById('tallaHint');
                if (hint) hint.style.display = 'none';
                // Actualizar max del qty input según stock de la talla seleccionada
                const qtyInput = document.getElementById('qtyInput');
                if (qtyInput && stockPorTalla[talla] !== undefined) {
                    qtyInput.max = stockPorTalla[talla];
                    // Si la cantidad actual supera el stock de la talla, ajustar
                    if (parseInt(qtyInput.value) > stockPorTalla[talla]) {
                        qtyInput.value = stockPorTalla[talla];
                    }
                }
            }

            // Validar que se seleccionó talla antes de agregar al carrito
            function validarTalla() {
                const input = document.getElementById('tallaInput');
                if (!input) return true; // producto sin tallas → OK directo
                if (!input.value) {
                    const hint = document.getElementById('tallaHint');
                    if (hint) hint.style.display = 'inline';
                    return false;
                }
                return true;
            }

            // Estrellas interactivas
            const stars = document.querySelectorAll('.star');
            const calInput = document.getElementById('calificacionInput');
            const starHint = document.getElementById('starHint');

            if (stars.length) {
                stars.forEach(star => {
                    star.addEventListener('mouseover', () => pintarEstrellas(star.dataset.val));
                    star.addEventListener('mouseout',  () => pintarEstrellas(calInput.value || 0));
                    star.addEventListener('click', () => {
                        calInput.value = star.dataset.val;
                        pintarEstrellas(star.dataset.val);
                        if (starHint) starHint.style.display = 'none';
                    });
                });

                document.getElementById('btnResena')?.addEventListener('click', (e) => {
                    if (!calInput.value) {
                        e.preventDefault();
                        if (starHint) starHint.style.display = 'block';
                    }
                });
            }

            function pintarEstrellas(valor) {
                stars.forEach(s => {
                    s.style.color = s.dataset.val <= valor
                        ? 'var(--color-secondary, #C9A96E)'
                        : 'var(--border-medium, #ccc)';
                });
            }
        </script>
    </body>
</html>
