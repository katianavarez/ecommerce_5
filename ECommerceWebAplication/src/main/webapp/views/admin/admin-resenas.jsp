<%-- 
    Document   : admin-resenas
    Created on : 9 abr 2026, 4:33:44 a.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reseñas — Velour Admin</title>
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
                    <p class="admin-nav__section">Ventas</p>
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link ">
                        <svg class="admin-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
                        Pedidos
                        <c:if test="${pedidosPendientes > 0}">
                            <span class="admin-nav__badge admin-nav__badge--warn"><c:out value="${pedidosPendientes}"/></span>
                        </c:if>
                    </a>
                    <p class="admin-nav__section">Comunidad</p>
                    <a href="${pageContext.request.contextPath}/admin/resenas" class="admin-nav__link admin-nav__link--active">
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
                        <h1 class="admin-topbar__title">Moderación de Reseñas</h1>
                        <p class="admin-topbar__subtitle"><c:out value="${resenas.size()}"/> reseñas</p>
                    </div>
                </div>
                <div class="admin-body">
                    <c:if test="${not empty param.success}">
                        <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">✓ Reseña eliminada.</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">${error}</div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty resenas}">
                            <div style="text-align:center;padding:var(--sp-10);color:var(--text-muted);">No hay reseñas registradas.</div>
                        </c:when>
                        <c:otherwise>
                            <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                <c:forEach var="resena" items="${resenas}">
                                    <div class="review-admin-card">
                                        <div class="review-admin-card__header">
                                            <div class="review-admin-card__user">
                                                <div class="review-admin-card__avatar">
                                                    <c:out value="${resena.usuario.nombre.substring(0,1).toUpperCase()}"/>
                                                </div>
                                                <div>
                                                    <p class="review-admin-card__name"><c:out value="${resena.usuario.nombre}"/></p>
                                                    <p class="review-admin-card__product">
                                                        <c:out value="${resena.producto.nombre}"/>
                                                    </p>
                                                </div>
                                            </div>
                                            <div style="display:flex;flex-direction:column;align-items:flex-end;gap:var(--sp-1);">
                                                <p class="review-admin-card__date"><c:out value="${resena.fecha}"/></p>
                                                <div class="stars" style="font-size:var(--fs-xs);">
                                                    <c:forEach begin="1" end="${resena.calificacion}">★</c:forEach>
                                                </div>
                                            </div>
                                        </div>
                                        <p class="review-admin-card__body"><c:out value="${resena.comentario}"/></p>
                                        <div class="review-admin-card__footer">
                                            <span style="font-size:var(--fs-xs);color:var(--text-muted);">Calificación: <c:out value="${resena.calificacion}"/>/5</span>
                                            <form method="POST" action="${pageContext.request.contextPath}/admin/resenas"
                                                  onsubmit="return confirm('¿Eliminar esta reseña?');">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${resena.id}">
                                                <button type="submit" class="btn btn--ghost btn--sm" style="color:var(--color-error);">Eliminar</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <%-- Paginación reseñas --%>
                    <c:if test="${totalRegistros > 0}">
                        <div style="padding:var(--sp-5) var(--sp-6);border-top:1px solid var(--border-soft);display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);">
                            <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                Mostrando <strong><c:out value="${(paginaActual - 1) * 10 + 1}"/></strong>&#8211;<strong><c:out value="${paginaActual * 10 > totalRegistros ? totalRegistros : paginaActual * 10}"/></strong> de <strong><c:out value="${totalRegistros}"/></strong>
                            </p>
                            <c:if test="${totalPaginas > 1}">
                                <nav class="pagination" style="margin-top:0;flex-shrink:0;">
                                    <c:if test="${paginaActual > 1}">
                                        <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/resenas?pagina=${paginaActual - 1}">&#8249;</a>
                                    </c:if>
                                    <c:forEach var="i" begin="1" end="${totalPaginas}">
                                        <c:choose>
                                            <c:when test="${i == paginaActual}">
                                                <a class="pagination__btn pagination__btn--active" href="#"><c:out value="${i}"/></a>
                                            </c:when>
                                            <c:when test="${i == 1 || i == totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)}">
                                                <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/resenas?pagina=${i}"><c:out value="${i}"/></a>
                                            </c:when>
                                            <c:when test="${i == paginaActual - 2 || i == paginaActual + 2}">
                                                <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                    <c:if test="${paginaActual < totalPaginas}">
                                        <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/resenas?pagina=${paginaActual + 1}">&#8250;</a>
                                    </c:if>
                                </nav>
                            </c:if>
                        </div>
                    </c:if>
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
