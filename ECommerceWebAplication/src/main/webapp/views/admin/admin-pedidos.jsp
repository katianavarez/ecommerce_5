<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pedidos — Velour Admin</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/admin.css"/>
    </head>
    <body>
        <header class="site-header">
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo"><div class="site-logo__wordmark">Vel<span>our</span></div><div class="site-logo__sub">Moda de Autor</div></a>
                <div style="flex:1;display:flex;justify-content:center;"><span class="admin-header-badge">Panel de Administración</span></div>
                                <div class="header-actions" style="display:flex;align-items:center;gap:var(--sp-3);">
                    <a href="${pageContext.request.contextPath}/app/productos" class="header-icon" title="Ver tienda">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/logout" class="header-icon" title="Cerrar sesión">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                    </a>
                    <button class="admin-menu-toggle" id="adminMenuToggle">
                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
                    </button>
                </div>
                </div>
            </div>
        </header>
                <div class="admin-overlay" id="adminOverlay"></div>
        <div class="admin-layout">
            <aside class="admin-sidebar">
                <div class="admin-sidebar__header">
                    <p class="admin-sidebar__title">Administrador</p>
                    <p class="admin-sidebar__user"><c:out value="${sessionScope.usuarioNombre}"/></p>
                    <p class="admin-sidebar__role">Admin</p>
                </div>
                <nav class="admin-nav">
                    <p class="admin-nav__section">General</p>
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                        Dashboard
                    </a>
                    <p class="admin-nav__section">Catálogo</p>
                    <a href="${pageContext.request.contextPath}/admin/productos" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                        Productos
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categorias" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                        Categorías
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/proveedores" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M16 3h5v5"/><path d="M21 3l-7 7"/><path d="M8 21H3v-5"/><path d="M3 21l7-7"/></svg>
                        Proveedores
                    </a>
                    <p class="admin-nav__section">Ventas</p>
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link admin-nav__link--active">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
                        Pedidos
                        <c:if test="${pedidosPendientes > 0}">
                            <span class="admin-nav__badge admin-nav__badge--warn"><c:out value="${pedidosPendientes}"/></span>
                        </c:if>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/pagos" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="2" y="6" width="20" height="12" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/><line x1="6" y1="14" x2="10" y2="14"/></svg>
                        Pagos
                    </a>
                    <p class="admin-nav__section">Comunidad</p>
                    <a href="${pageContext.request.contextPath}/admin/resenas" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                        Reseñas
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/clientes" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                        Clientes
                    </a>
                </nav>
                <div class="admin-sidebar__footer">
                    <a href="${pageContext.request.contextPath}/admin/logout" class="admin-sidebar__logout">
                        <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                        Cerrar sesión
                    </a>
                </div>
            </aside>

            <div class="admin-content">
                <div class="admin-topbar">
                    <div>
                        <h1 class="admin-topbar__title">Gestión de Pedidos</h1>
                        <p class="admin-topbar__subtitle"><c:out value="${totalRegistros}"/> pedidos en total</p>
                    </div>
                </div>

                <div class="admin-body">
                    <c:if test="${not empty param.success}">
                        <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">✓ Pedido actualizado.</div>
                    </c:if>
                    <c:if test="${param.warn == 'stale'}">
                        <div style="background:#fff3cd;border:1px solid #ffc107;color:#856404;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            ⚠ El estado de este pedido ya fue modificado anteriormente. Se muestra el estado actual.
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);"><c:out value="${error}"/></div>
                    </c:if>

                    <!-- Vista detalle de un pedido específico -->
                    <c:choose>
                        <c:when test="${not empty pedido}">
                            <div class="admin-card">
                                <div class="admin-card__header">
                                    <h3 class="admin-card__title">Pedido #<c:out value="${pedido.numPedido}"/></h3>
                                    <span class="badge badge--${pedido.estado.name().toLowerCase()}"><c:out value="${pedido.estado}"/></span>
                                </div>
                                <div class="admin-card__body">
                                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--sp-6);">
                                        <div>
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);text-transform:uppercase;letter-spacing:0.1em;margin-bottom:var(--sp-2);">Cliente</p>
                                            <p><c:out value="${pedido.usuario.nombre}"/></p>
                                            <p style="font-size:var(--fs-sm);color:var(--text-muted);"><c:out value="${pedido.usuario.correo}"/></p>
                                        </div>
                                        <div>
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);text-transform:uppercase;letter-spacing:0.1em;margin-bottom:var(--sp-2);">Envío a</p>
                                            <p><c:out value="${pedido.direccionEnvio.calle}"/></p>
                                            <p style="font-size:var(--fs-sm);color:var(--text-muted);"><c:out value="${pedido.direccionEnvio.ciudad}"/>, <c:out value="${pedido.direccionEnvio.estado}"/> <c:out value="${pedido.direccionEnvio.codigoPostal}"/></p>
                                        </div>
                                        <div>
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);text-transform:uppercase;letter-spacing:0.1em;margin-bottom:var(--sp-2);">Pago</p>
                                            <p><c:out value="${pedido.pago.metodo}"/> · <c:out value="${pedido.pago.estado}"/></p>
                                            <p style="font-size:var(--fs-sm);color:var(--text-muted);">$<fmt:formatNumber value="${pedido.pago.monto}" maxFractionDigits="0"/></p>
                                        </div>
                                        <div>
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);text-transform:uppercase;letter-spacing:0.1em;margin-bottom:var(--sp-2);">Fecha</p>
                                            <p><c:out value="${pedido.fecha}"/></p>
                                        </div>
                                    </div>
                                    <!-- Artículos -->
                                    <h4 style="margin:var(--sp-6) 0 var(--sp-3);font-size:var(--fs-sm);">Artículos</h4>
                                    <table class="admin-table">
                                        <thead><tr><th>Producto</th><th>Cantidad</th><th>Precio u.</th><th>Subtotal</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="d" items="${pedido.detalles}">
                                                <tr>
                                                    <td><c:out value="${d.producto.nombre}"/></td>
                                                    <td><c:out value="${d.cantidad}"/></td>
                                                    <td>$<fmt:formatNumber value="${d.precioUnidad}" maxFractionDigits="0"/></td>
                                                    <td>$<fmt:formatNumber value="${d.precioUnidad * d.cantidad}" maxFractionDigits="0"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                    <p style="text-align:right;font-weight:var(--fw-semibold);margin-top:var(--sp-4);">Total: $<fmt:formatNumber value="${pedido.total}" maxFractionDigits="0"/></p>
                                    <!-- Actualizar estado con transiciones permitidas -->
                                    <c:choose>
                                        <c:when test="${pedido.estado.name() == 'ENTREGADO' || pedido.estado.name() == 'CANCELADO'}">
                                            <div style="margin-top:var(--sp-5);padding:var(--sp-3) var(--sp-4);background:var(--bg-muted);border-radius:var(--r-md);display:flex;align-items:center;gap:var(--sp-3);">
                                                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8" style="color:var(--text-muted);flex-shrink:0;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                                                <p style="font-size:var(--fs-sm);color:var(--text-muted);">
                                                    Este pedido está en estado final (<strong><c:out value="${pedido.estado}"/></strong>) y no puede modificarse.
                                                </p>
                                            </div>
                                            <div style="margin-top:var(--sp-3);">
                                                <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn--ghost btn--sm">← Volver</a>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="POST" action="${pageContext.request.contextPath}/admin/pedidos" style="margin-top:var(--sp-5);display:flex;gap:var(--sp-3);align-items:center;">
                                                <input type="hidden" name="accion" value="actualizarEstado">
                                                <input type="hidden" name="id" value="${pedido.id}">
                                                <input type="hidden" name="estadoActual" value="${pedido.estado.name()}">
                                                <select class="admin-select" name="nuevoEstado">
                                                    <c:choose>
                                                        <c:when test="${pedido.estado.name() == 'PENDIENTE'}">
                                                            <option value="PENDIENTE" selected>PENDIENTE (actual)</option>
                                                            <option value="ENVIADO">ENVIADO</option>
                                                            <option value="CANCELADO">CANCELADO</option>
                                                        </c:when>
                                                        <c:when test="${pedido.estado.name() == 'ENVIADO'}">
                                                            <option value="ENVIADO" selected>ENVIADO (actual)</option>
                                                            <option value="ENTREGADO">ENTREGADO</option>
                                                            <option value="CANCELADO">CANCELADO</option>
                                                        </c:when>
                                                    </c:choose>
                                                </select>
                                                <button type="submit" class="btn btn--gold btn--sm">Actualizar estado</button>
                                                <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn--ghost btn--sm">← Volver</a>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form id="formFiltrosPedidos" method="GET" action="${pageContext.request.contextPath}/admin/pedidos">
                                <%-- Tabs de estado --%>
                                <div class="admin-tabs" style="margin-bottom:var(--sp-4);">
                                    <button type="submit" name="estado" value="TODOS"
                                            class="admin-tab ${estadoSeleccionado == 'TODOS' || empty estadoSeleccionado ? 'admin-tab--active' : ''}">
                                        Todos (<c:out value="${conteosPorEstado['TODOS']}"/>)
                                    </button>
                                    <c:forEach var="est" items="${estados}">
                                        <button type="submit" name="estado" value="${est.name()}"
                                                class="admin-tab ${estadoSeleccionado == est.name() ? 'admin-tab--active' : ''}">
                                            <c:choose>
                                                <c:when test="${est.name() == 'PENDIENTE'}">Pendientes</c:when>
                                                <c:when test="${est.name() == 'ENVIADO'}">Enviados</c:when>
                                                <c:when test="${est.name() == 'ENTREGADO'}">Entregados</c:when>
                                                <c:when test="${est.name() == 'CANCELADO'}">Cancelados</c:when>
                                                <c:otherwise><c:out value="${est}"/></c:otherwise>
                                            </c:choose>
                                            (<c:out value="${conteosPorEstado[est.name()]}"/>)
                                        </button>
                                    </c:forEach>
                                </div>

                                <div class="admin-table-head">
                                    <h2 class="admin-table-head__title">
                                        <c:choose>
                                            <c:when test="${estadoSeleccionado == 'TODOS' || empty estadoSeleccionado}">Todos los Pedidos</c:when>
                                            <c:when test="${estadoSeleccionado == 'PENDIENTE'}">Pedidos Pendientes</c:when>
                                            <c:when test="${estadoSeleccionado == 'ENVIADO'}">Pedidos Enviados</c:when>
                                            <c:when test="${estadoSeleccionado == 'ENTREGADO'}">Pedidos Entregados</c:when>
                                            <c:when test="${estadoSeleccionado == 'CANCELADO'}">Pedidos Cancelados</c:when>
                                        </c:choose>
                                    </h2>
                                    <div class="admin-table-head__controls">
                                        <div class="admin-search">
                                            <input class="admin-search__input" type="search"
                                                   name="busqueda" placeholder="Buscar por # o cliente..."
                                                   value="<c:out value='${busquedaActual}'/>">
                                            <button class="admin-search__btn" type="submit">
                                                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
                                            </button>
                                        </div>
                                        <select class="admin-select" name="orden" onchange="this.form.submit()">
                                            <option value="reciente" ${ordenActual == 'reciente' ? 'selected' : ''}>Más recientes</option>
                                            <option value="mayorMonto" ${ordenActual == 'mayorMonto' ? 'selected' : ''}>Mayor monto</option>
                                            <option value="menorMonto" ${ordenActual == 'menorMonto' ? 'selected' : ''}>Menor monto</option>
                                        </select>
                                        <input type="hidden" name="estado" value="${estadoSeleccionado}">
                                    </div>
                                </div>
                            </form>

                            <!-- Lista de pedidos -->
                            <div class="admin-table-wrap">
                                <table class="admin-table">
                                    <thead>
                                        <tr><th># Pedido</th><th>Cliente</th><th>Fecha</th><th>Total</th><th>Estado</th><th>Acciones</th></tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty pedidos}">
                                                <tr><td colspan="6" style="text-align:center;color:var(--text-muted);padding:var(--sp-8);">No hay pedidos registrados.</td></tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="ped" items="${pedidos}">
                                                    <tr>
                                                        <td><strong style="color:var(--text-heading);">#<c:out value="${ped.numPedido}"/></strong></td>
                                                        <td>
                                                            <p style="font-weight:var(--fw-medium);"><c:out value="${ped.usuario.nombre}"/></p>
                                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);"><c:out value="${ped.usuario.correo}"/></p>
                                                        </td>
                                                        <td style="color:var(--text-muted);"><c:out value="${ped.fecha}"/></td>
                                                        <td><strong>$<fmt:formatNumber value="${ped.total}" maxFractionDigits="0"/></strong></td>
                                                        <td><span class="badge badge--${ped.estado.name().toLowerCase()}"><c:out value="${ped.estado}"/></span></td>
                                                        <td>
                                                            <div class="admin-table__actions">
                                                                <a href="${pageContext.request.contextPath}/admin/pedidos?accion=detalle&id=${ped.id}" class="admin-action-btn" title="Ver detalle">
                                                                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                                                </a>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                                <%-- Paginación pedidos --%>
                                <c:if test="${totalRegistros > 0}">
                                    <div style="padding:var(--sp-5) var(--sp-6);border-top:1px solid var(--border-soft);display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);">
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                            Mostrando <strong><c:out value="${(paginaActual - 1) * 10 + 1}"/></strong>&#8211;<strong><c:out value="${paginaActual * 10 > totalRegistros ? totalRegistros : paginaActual * 10}"/></strong> de <strong><c:out value="${totalRegistros}"/></strong>
                                        </p>
                                        <c:if test="${totalPaginas > 1}">
                                            <nav class="pagination" style="margin-top:0;flex-shrink:0;">
                                                <c:if test="${paginaActual > 1}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/pedidos?estado=${estadoSeleccionado}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${paginaActual - 1}">&#8249;</a>
                                                </c:if>
                                                <c:forEach var="i" begin="1" end="${totalPaginas}">
                                                    <c:choose>
                                                        <c:when test="${i == paginaActual}">
                                                            <a class="pagination__btn pagination__btn--active" href="#"><c:out value="${i}"/></a>
                                                        </c:when>
                                                        <c:when test="${i == 1 || i == totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)}">
                                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/pedidos?estado=${estadoSeleccionado}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${i}"><c:out value="${i}"/></a>
                                                        </c:when>
                                                        <c:when test="${i == paginaActual - 2 || i == paginaActual + 2}">
                                                            <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                                        </c:when>
                                                    </c:choose>
                                                </c:forEach>
                                                <c:if test="${paginaActual < totalPaginas}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/pedidos?estado=${estadoSeleccionado}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${paginaActual + 1}">&#8250;</a>
                                                </c:if>
                                            </nav>
                                        </c:if>
                                    </div>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <script>
            const adminToggle=document.getElementById('adminMenuToggle');const adminSidebar=document.querySelector('.admin-sidebar');const adminOverlay=document.getElementById('adminOverlay');
            function openAdminSidebar(){adminSidebar.classList.add('is-open');adminOverlay.classList.add('is-open');document.body.style.overflow='hidden';}
            function closeAdminSidebar(){adminSidebar.classList.remove('is-open');adminOverlay.classList.remove('is-open');document.body.style.overflow='';}
            if(adminToggle)adminToggle.addEventListener('click',()=>adminSidebar.classList.contains('is-open')?closeAdminSidebar():openAdminSidebar());
            if(adminOverlay)adminOverlay.addEventListener('click',closeAdminSidebar);
        </script>
    </body>
</html>
