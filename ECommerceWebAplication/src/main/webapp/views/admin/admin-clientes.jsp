<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Clientes — Velour Admin</title>
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
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link ">
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
                    <a href="${pageContext.request.contextPath}/admin/clientes" class="admin-nav__link admin-nav__link--active">
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
                        <h1 class="admin-topbar__title">Gestión de Clientes</h1>
                        <p class="admin-topbar__subtitle"><c:out value="${totalRegistros}"/> clientes registrados</p>
                    </div>
                </div>
                <div class="admin-body">
                    <c:if test="${not empty param.success}">
                        <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            <c:choose>
                                <c:when test="${param.success == 'deleted'}">✓ Cliente dado de baja correctamente.</c:when>
                                <c:when test="${param.success == 'reactivated'}">✓ Cliente reactivado correctamente.</c:when>
                                <c:otherwise>✓ Operación exitosa.</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);"><c:out value="${error}"/></div>
                    </c:if>

                    <%-- Tabs de filtro: Activos / Inactivos / Todos --%>
                    <div class="admin-tabs" style="margin-bottom:var(--sp-4);">
                        <a class="admin-tab ${filtroActual == 'activos' ? 'admin-tab--active' : ''}"
                           href="${pageContext.request.contextPath}/admin/clientes?filtro=activos">
                            Activos (<c:out value="${conteoActivos}"/>)
                        </a>
                        <a class="admin-tab ${filtroActual == 'inactivos' ? 'admin-tab--active' : ''}"
                           href="${pageContext.request.contextPath}/admin/clientes?filtro=inactivos">
                            Inactivos (<c:out value="${conteoInactivos}"/>)
                        </a>
                        <a class="admin-tab ${filtroActual == 'todos' ? 'admin-tab--active' : ''}"
                           href="${pageContext.request.contextPath}/admin/clientes?filtro=todos">
                            Todos (<c:out value="${conteoTodos}"/>)
                        </a>
                    </div>

                    <div class="admin-table-wrap">
                        <table class="admin-table">
                            <thead>
                                <tr><th>Cliente</th><th>Correo</th><th>Teléfono</th><th>Estado</th><th>Acciones</th></tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty clientes}">
                                        <tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:var(--sp-8);">No hay clientes en este filtro.</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="cliente" items="${clientes}">
                                            <tr style="${not cliente.activo ? 'opacity:0.6;' : ''}">
                                                <td>
                                                    <div style="display:flex;align-items:center;gap:var(--sp-3);">
                                                        <div style="width:36px;height:36px;border-radius:50%;background:var(--color-secondary-light);color:var(--color-secondary-hover);font-family:var(--font-display);font-size:var(--fs-md);font-weight:var(--fw-medium);display:flex;align-items:center;justify-content:center;flex-shrink:0;">
                                                            <c:out value="${empty cliente.nombre ? '?' : cliente.nombre.substring(0,1).toUpperCase()}"/>
                                                        </div>
                                                        <p style="font-weight:var(--fw-medium);color:var(--text-heading);"><c:out value="${cliente.nombre}"/></p>
                                                    </div>
                                                </td>
                                                <td style="color:var(--text-muted);font-size:var(--fs-sm);"><c:out value="${cliente.correo}"/></td>
                                                <td style="color:var(--text-muted);font-size:var(--fs-sm);"><c:out value="${cliente.telefono}"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${cliente.activo}">
                                                            <span class="badge badge--entregado">Activo</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge--cancelado">Inactivo</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${cliente.activo}">
                                                            <form method="POST" action="${pageContext.request.contextPath}/admin/clientes"
                                                                  data-nombre="<c:out value='${cliente.nombre}'/>"
                                                                  onsubmit="return confirm('¿Dar de baja al cliente ' + (this.dataset.nombre || '') + '?');" style="display:inline;">
                                                                <input type="hidden" name="accion" value="eliminar">
                                                                <input type="hidden" name="id" value="${cliente.id}">
                                                                <input type="hidden" name="filtro" value="${filtroActual}">
                                                                <button type="submit" class="admin-action-btn admin-action-btn--danger" title="Dar de baja">
                                                                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form method="POST" action="${pageContext.request.contextPath}/admin/clientes"
                                                                  data-nombre="<c:out value='${cliente.nombre}'/>"
                                                                  onsubmit="return confirm('¿Reactivar al cliente ' + (this.dataset.nombre || '') + '?');" style="display:inline;">
                                                                <input type="hidden" name="accion" value="reactivar">
                                                                <input type="hidden" name="id" value="${cliente.id}">
                                                                <input type="hidden" name="filtro" value="${filtroActual}">
                                                                <button type="submit" class="admin-action-btn" title="Reactivar" style="color:var(--color-success);">
                                                                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.4"/></svg>
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                        <%-- Paginación clientes --%>
                        <c:if test="${totalRegistros > 0}">
                            <div style="padding:var(--sp-5) var(--sp-6);border-top:1px solid var(--border-soft);display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);">
                                <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                    Mostrando <strong><c:out value="${(paginaActual - 1) * 10 + 1}"/></strong>&#8211;<strong><c:out value="${paginaActual * 10 > totalRegistros ? totalRegistros : paginaActual * 10}"/></strong> de <strong><c:out value="${totalRegistros}"/></strong>
                                </p>
                                <c:if test="${totalPaginas > 1}">
                                    <nav class="pagination" style="margin-top:0;flex-shrink:0;">
                                        <c:if test="${paginaActual > 1}">
                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${paginaActual - 1}">&#8249;</a>
                                        </c:if>
                                        <c:forEach var="i" begin="1" end="${totalPaginas}">
                                            <c:choose>
                                                <c:when test="${i == paginaActual}">
                                                    <a class="pagination__btn pagination__btn--active" href="#"><c:out value="${i}"/></a>
                                                </c:when>
                                                <c:when test="${i == 1 || i == totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${i}"><c:out value="${i}"/></a>
                                                </c:when>
                                                <c:when test="${i == paginaActual - 2 || i == paginaActual + 2}">
                                                    <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                                </c:when>
                                            </c:choose>
                                        </c:forEach>
                                        <c:if test="${paginaActual < totalPaginas}">
                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${paginaActual + 1}">&#8250;</a>
                                        </c:if>
                                    </nav>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
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
