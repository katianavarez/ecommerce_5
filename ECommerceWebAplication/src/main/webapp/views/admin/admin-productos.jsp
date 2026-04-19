<%-- 
    Document   : admin-productos
    Created on : 9 abr 2026, 4:39:21 a.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Productos — Velour Admin</title>
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
                    <a href="${pageContext.request.contextPath}/admin/productos" class="admin-nav__link admin-nav__link--active">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                        Productos
                    </a>
                    <p class="admin-nav__section">Ventas</p>
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
                        Pedidos
                        <c:if test="${pedidosPendientes > 0}">
                            <span class="admin-nav__badge admin-nav__badge--warn"><c:out value="${pedidosPendientes}"/></span>
                        </c:if>
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
                        <h1 class="admin-topbar__title">Gestión de Productos</h1>
                        <p class="admin-topbar__subtitle"><c:out value="${productos.size()}"/> productos mostrados</p>
                    </div>
                    <div class="admin-topbar__actions">
                        <a href="${pageContext.request.contextPath}/admin/productos?accion=nuevo" class="btn btn--gold btn--sm">+ Agregar producto</a>
                    </div>
                </div>

                <div class="admin-body">
                    <c:if test="${not empty param.success}">
                        <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            <c:choose>
                                <c:when test="${param.success == 'created'}">✓ Producto creado exitosamente.</c:when>
                                <c:when test="${param.success == 'updated'}">✓ Producto actualizado exitosamente.</c:when>
                                <c:when test="${param.success == 'deleted'}">✓ Producto archivado. Ya no aparece en el catálogo.</c:when>
                                <c:when test="${param.success == 'reactivated'}">✓ Producto reactivado. Ya aparece en el catálogo.</c:when>
                            </c:choose>
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">${error}</div>
                    </c:if>

                    <%-- Tabs por categoría + búsqueda + orden --%>
                    <form id="formFiltrosProductos" method="GET" action="${pageContext.request.contextPath}/admin/productos">

                        <div class="admin-tabs" style="margin-bottom:var(--sp-4);flex-wrap:wrap;">
                            <button type="submit" name="categoriaId" value="TODAS"
                                    class="admin-tab ${categoriaSeleccionada == 'TODAS' || empty categoriaSeleccionada ? 'admin-tab--active' : ''}">
                                Todas
                            </button>
                            <c:forEach var="cat" items="${categorias}">
                                <button type="submit" name="categoriaId" value="${cat.id}"
                                        class="admin-tab ${categoriaSeleccionada == cat.id.toString() ? 'admin-tab--active' : ''}">
                                    <c:out value="${cat.nombre}"/>
                                </button>
                            </c:forEach>
                        </div>

                        <div class="admin-table-head">
                            <h2 class="admin-table-head__title">Catálogo</h2>
                            <div class="admin-table-head__controls">
                                <div class="admin-search">
                                    <input class="admin-search__input" type="search"
                                           name="busqueda" placeholder="Buscar producto..."
                                           value="${busquedaActual}">
                                    <button class="admin-search__btn" type="submit">
                                        <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
                                    </button>
                                </div>
                                <select class="admin-select" name="orden" onchange="this.form.submit()">
                                    <option value="reciente"   ${ordenActual == 'reciente'   ? 'selected' : ''}>Más recientes</option>
                                    <option value="mayorPrecio" ${ordenActual == 'mayorPrecio' ? 'selected' : ''}>Mayor precio</option>
                                    <option value="menorPrecio" ${ordenActual == 'menorPrecio' ? 'selected' : ''}>Menor precio</option>
                                    <option value="menorStock"  ${ordenActual == 'menorStock'  ? 'selected' : ''}>Menor stock</option>
                                </select>
                                <%-- Mantener categoría al ordenar --%>
                                <input type="hidden" name="categoriaId" value="${categoriaSeleccionada}">
                            </div>
                        </div>
                    </form>

                    <div class="admin-table-wrap">
                        <table class="admin-table">
                            <thead>
                                <tr><th>Producto</th><th>Categoría</th><th>Precio</th><th>Stock</th><th>Acciones</th></tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty productos}">
                                        <tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:var(--sp-8);">No hay productos registrados.</td></tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="prod" items="${productos}">
                                            <tr style="${not prod.activo ? 'opacity:0.55;' : ''}">
                                                <td>
                                                    <div class="admin-table__product">
                                                        <img class="admin-table__img" src="${prod.imagenURL.startsWith('http') ? prod.imagenURL : pageContext.request.contextPath.concat('/').concat(prod.imagenURL)}" alt="${prod.nombre}">
                                                        <div>
                                                            <p class="admin-table__name"><c:out value="${prod.nombre}"/>
                                                                <c:if test="${not prod.activo}">
                                                                    <span style="font-size:var(--fs-xs);background:var(--badge-cancelado-bg);color:var(--badge-cancelado-text);padding:1px 6px;border-radius:var(--r-full);margin-left:4px;font-weight:var(--fw-medium);text-transform:uppercase;letter-spacing:0.05em;">Archivado</span>
                                                                </c:if>
                                                            </p>
                                                            <p class="admin-table__sub">ID: ${prod.id}</p>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td style="color:var(--text-muted);"><c:out value="${prod.categoria.nombre}"/></td>
                                                <td><strong>$<fmt:formatNumber value="${prod.precio}" maxFractionDigits="0"/></strong></td>
                                                <td style="color:${prod.stock <= 5 ? 'var(--color-error)' : 'inherit'};">
                                                    <c:out value="${prod.stock}"/> uds.
                                                </td>
                                                <td>
                                                    <div class="admin-table__actions">
                                                        <c:choose>
                                                            <c:when test="${prod.activo}">
                                                                <%-- Activo: editar + archivar --%>
                                                                <a href="${pageContext.request.contextPath}/admin/productos?accion=editar&id=${prod.id}" class="admin-action-btn" title="Editar">
                                                                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                                                                </a>
                                                                <form method="POST" action="${pageContext.request.contextPath}/admin/productos" style="display:inline;"
                                                                      onsubmit="return confirm('¿Archivar «${prod.nombre}»? El producto dejará de aparecer en el catálogo pero el historial de pedidos se conserva.');">
                                                                    <input type="hidden" name="accion" value="eliminar">
                                                                    <input type="hidden" name="id" value="${prod.id}">
                                                                    <button type="submit" class="admin-action-btn admin-action-btn--danger" title="Archivar">
                                                                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/></svg>
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <%-- Archivado: solo botón reactivar --%>
                                                                <form method="POST" action="${pageContext.request.contextPath}/admin/productos" style="display:inline;"
                                                                      onsubmit="return confirm('¿Reactivar «${prod.nombre}»? Volverá a aparecer en el catálogo.');">
                                                                    <input type="hidden" name="accion" value="reactivar">
                                                                    <input type="hidden" name="id" value="${prod.id}">
                                                                    <button type="submit" class="admin-action-btn" title="Reactivar"
                                                                            style="color:var(--color-success);">
                                                                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.4"/></svg>
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                        <%-- Paginación --%>
                        <c:if test="${totalRegistros > 0}">
                            <div style="padding:var(--sp-5) var(--sp-6);border-top:1px solid var(--border-soft);display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);">
                                <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                    Mostrando <strong><c:out value="${(paginaActual - 1) * 10 + 1}"/></strong>&#8211;<strong><c:out value="${paginaActual * 10 > totalRegistros ? totalRegistros : paginaActual * 10}"/></strong> de <strong><c:out value="${totalRegistros}"/></strong>
                                </p>
                                <c:if test="${totalPaginas > 1}">
                                    <nav class="pagination" style="margin-top:0;flex-shrink:0;">
                                        <c:if test="${paginaActual > 1}">
                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/productos?categoriaId=${categoriaSeleccionada}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${paginaActual - 1}">&#8249;</a>
                                        </c:if>
                                        <c:forEach var="i" begin="1" end="${totalPaginas}">
                                            <c:choose>
                                                <c:when test="${i == paginaActual}">
                                                    <a class="pagination__btn pagination__btn--active" href="#"><c:out value="${i}"/></a>
                                                </c:when>
                                                <c:when test="${i == 1 || i == totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/productos?categoriaId=${categoriaSeleccionada}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${i}"><c:out value="${i}"/></a>
                                                </c:when>
                                                <c:when test="${i == paginaActual - 2 || i == paginaActual + 2}">
                                                    <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                                </c:when>
                                            </c:choose>
                                        </c:forEach>
                                        <c:if test="${paginaActual < totalPaginas}">
                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/productos?categoriaId=${categoriaSeleccionada}&busqueda=${busquedaActual}&orden=${ordenActual}&pagina=${paginaActual + 1}">&#8250;</a>
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
