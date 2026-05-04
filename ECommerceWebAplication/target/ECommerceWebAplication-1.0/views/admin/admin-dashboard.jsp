<%-- 
    Document   : admin-dashboard
    Created on : 9 abr 2026, 4:23:40 a.m.
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
        <title>Dashboard — Velour Admin</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/admin.css"/>
    </head>
    <body>
        <header class="site-header">
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo">
                    <div class="site-logo__wordmark">Vel<span>our</span></div>
                    <div class="site-logo__sub">Moda de Autor</div>
                </a>
                <div style="flex:1;display:flex;justify-content:center;">
                    <span class="admin-header-badge">Panel de Administración</span>
                </div>
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
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav__link admin-nav__link--active">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                        Dashboard
                    </a>
                    <p class="admin-nav__section">Catálogo</p>
                    <a href="${pageContext.request.contextPath}/admin/productos" class="admin-nav__link">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                        Productos
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categorias" class="admin-nav__link">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                        Categorías
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/proveedores" class="admin-nav__link">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M16 3h5v5"/><path d="M21 3l-7 7"/><path d="M8 21H3v-5"/><path d="M3 21l7-7"/></svg>
                        Proveedores
                    </a>
                    <p class="admin-nav__section">Ventas</p>
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
                        Pedidos
                        <c:if test="${pedidosPendientes > 0}">
                            <span class="admin-nav__badge admin-nav__badge--warn"><c:out value="${pedidosPendientes}"/></span>
                        </c:if>
                    </a>
                    <p class="admin-nav__section">Comunidad</p>
                    <a href="${pageContext.request.contextPath}/admin/resenas" class="admin-nav__link">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
                        Reseñas
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/clientes" class="admin-nav__link">
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
                        <h1 class="admin-topbar__title">Dashboard</h1>
                        <p class="admin-topbar__subtitle">Resumen general del sistema</p>
                    </div>
                </div>

                <div class="admin-body">
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            <c:out value="${error}"/>
                        </div>
                    </c:if>

                    <!-- KPIs -->
                    <div class="kpi-grid">
                        <div class="kpi-card">
                            <div class="kpi-card__label">
                                Ventas del Mes
                                <div class="kpi-card__icon kpi-card__icon--gold">
                                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                                </div>
                            </div>
                            <p class="kpi-card__value">$<fmt:formatNumber value="${ventasDelMes}" maxFractionDigits="0"/></p>
                        </div>
                        <div class="kpi-card">
                            <div class="kpi-card__label">
                                Total Pedidos
                                <div class="kpi-card__icon kpi-card__icon--navy">
                                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/></svg>
                                </div>
                            </div>
                            <p class="kpi-card__value"><c:out value="${totalPedidos}"/></p>
                        </div>
                        <div class="kpi-card">
                            <div class="kpi-card__label">
                                Clientes
                                <div class="kpi-card__icon kpi-card__icon--green">
                                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                                </div>
                            </div>
                            <p class="kpi-card__value"><c:out value="${totalUsuarios}"/></p>
                        </div>
                        <div class="kpi-card">
                            <div class="kpi-card__label">
                                Pedidos Pendientes
                                <div class="kpi-card__icon kpi-card__icon--warn">
                                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                                </div>
                            </div>
                            <p class="kpi-card__value"><c:out value="${pedidosPendientes}"/></p>
                            <c:if test="${pedidosPendientes > 0}">
                                <p class="kpi-card__change kpi-card__change--down">▼ Requieren atención</p>
                            </c:if>
                        </div>
                    </div>

                    <!-- Pedidos recientes -->
                    <div class="admin-table-wrap" style="margin-top:var(--sp-6);">
                        <div class="admin-table-head">
                            <h2 class="admin-table-head__title">Pedidos Recientes</h2>
                            <a href="${pageContext.request.contextPath}/admin/pedidos" class="btn btn--outline btn--sm">Ver todos</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty ultimosPedidos}">
                                <p style="padding:var(--sp-6);color:var(--text-muted);text-align:center;">Aún no hay pedidos registrados.</p>
                            </c:when>
                            <c:otherwise>
                                <table class="admin-table">
                                    <thead>
                                        <tr>
                                            <th>Pedido</th><th>Cliente</th><th>Fecha</th><th>Total</th><th>Estado</th><th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="pedido" items="${ultimosPedidos}">
                                            <tr>
                                                <td><span style="font-weight:var(--fw-medium);color:var(--text-heading);">#<c:out value="${pedido.numPedido}"/></span></td>
                                                <td><c:out value="${pedido.usuario.nombre}"/></td>
                                                <td style="color:var(--text-muted);"><c:out value="${pedido.fecha}"/></td>
                                                <td><strong>$<fmt:formatNumber value="${pedido.total}" maxFractionDigits="0"/></strong></td>
                                                <td><span class="badge badge--${pedido.estado.name().toLowerCase()}"><c:out value="${pedido.estado}"/></span></td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/admin/pedidos?accion=detalle&id=${pedido.id}"
                                                       class="admin-action-btn" title="Ver detalle">
                                                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Accesos rápidos -->
                    <div class="admin-card" style="margin-top:var(--sp-5);">
                        <div class="admin-card__header"><h3 class="admin-card__title">Accesos rápidos</h3></div>
                        <div class="admin-card__body" style="display:flex;gap:var(--sp-3);">
                            <a href="${pageContext.request.contextPath}/admin/productos?accion=nuevo" class="btn btn--gold">+ Agregar producto</a>
                            <a href="${pageContext.request.contextPath}/admin/pedidos?accion=filtrar&estado=PENDIENTE" class="btn btn--outline">Pedidos pendientes</a>
                            <a href="${pageContext.request.contextPath}/admin/resenas" class="btn btn--ghost">Moderar reseñas</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            const adminToggle = document.getElementById('adminMenuToggle');
            const adminSidebar = document.querySelector('.admin-sidebar');
            const adminOverlay = document.getElementById('adminOverlay');
            function openAdminSidebar() { adminSidebar.classList.add('is-open'); adminOverlay.classList.add('is-open'); document.body.style.overflow='hidden'; }
            function closeAdminSidebar() { adminSidebar.classList.remove('is-open'); adminOverlay.classList.remove('is-open'); document.body.style.overflow=''; }
            if (adminToggle) adminToggle.addEventListener('click', () => adminSidebar.classList.contains('is-open') ? closeAdminSidebar() : openAdminSidebar());
            if (adminOverlay) adminOverlay.addEventListener('click', closeAdminSidebar);
        </script>
    </body>
</html>
