<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Clientes — Velour Admin</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/paginas/admin.css">
    </head>

    <body>
        <%@include file="/WEB-INF/jsp/fragments/header-admin.jspf" %>

        <div class="admin-overlay" id="adminOverlay"></div>

        <div class="admin-layout">

            <%@include file="/WEB-INF/jsp/fragments/sidebar-admin.jspf" %>

            <div class="admin-content">
                <div class="admin-topbar">
                    <div>
                        <h1 class="admin-topbar__title">Gestión de Clientes</h1>
                        <p class="admin-topbar__subtitle">${requestScope.totalClientes} clientes registrados</p>
                    </div>
                </div>

                <div class="admin-body">

                    <div class="admin-tabs">
                        <button class="admin-tab admin-tab--active">Todos (${requestScope.totalClientes})</button>
                        <button class="admin-tab">Activos (${requestScope.totalActivos})</button>
                        <button class="admin-tab">VIP (${requestScope.totalVip})</button>
                        <button class="admin-tab">Sin compras (${requestScope.totalSinCompras})</button>
                    </div>

                    <div class="admin-table-wrap">
                        <div class="admin-table-head">
                            <h2 class="admin-table-head__title">Clientes</h2>
                            <div class="admin-table-head__controls">
                                <div class="admin-search">
                                    <input class="admin-search__input" type="search" placeholder="Buscar cliente...">
                                    <button class="admin-search__btn">
                                        <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.35-4.35"/></svg>
                                    </button>
                                </div>
                                <select class="admin-select">
                                    <option>Más recientes</option>
                                    <option>Mayor gasto</option>
                                    <option>Más pedidos</option>
                                </select>
                            </div>
                        </div>

                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Cliente</th>
                                    <th>Registro</th>
                                    <th>Pedidos</th>
                                    <th>Gasto total</th>
                                    <th>Último pedido</th>
                                    <th>Estado</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty requestScope.clientes}">
                                    <tr>
                                        <td colspan="7" style="text-align:center; color:var(--text-muted); padding:var(--sp-8);">
                                            No hay clientes registrados.
                                        </td>
                                    </tr>
                                </c:if>
                                <c:if test="${not empty requestScope.clientes}">
                                    <c:forEach var="cliente" items="${requestScope.clientes}">
                                        <tr>
                                            <td>
                                                <div style="display:flex; align-items:center; gap:var(--sp-3);">
                                                    <div style="width:36px;height:36px;border-radius:50%;background:var(--color-secondary-light);color:var(--color-secondary-hover);font-family:var(--font-display);font-size:var(--fs-md);font-weight:var(--fw-medium);display:flex;align-items:center;justify-content:center;flex-shrink:0;">
                                                        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8" width="16" height="16"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                                    </div>
                                                    <div>
                                                        <p style="font-weight:var(--fw-medium); color:var(--text-heading);"><c:out value="${cliente.nombre}"/></p>
                                                        <p style="font-size:var(--fs-xs); color:var(--text-muted);"><c:out value="${cliente.correo}"/></p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td style="color:var(--text-muted);">${cliente.fechaRegistro}</td>
                                            <td><strong>—</strong></td>
                                            <td><strong>—</strong></td>
                                            <td style="color:var(--text-muted);">—</td>
                                            <td><span class="badge badge--entregado">Activo</span></td>
                                            <td>
                                                <button class="admin-action-btn" title="Ver perfil">
                                                    <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:if>
                            </tbody>
                        </table>

                        <div style="padding:var(--sp-5) var(--sp-6); border-top:1px solid var(--border-soft); display:flex; align-items:center; justify-content:space-between;">
                            <p style="font-size:var(--fs-xs); color:var(--text-muted);">Mostrando ${requestScope.clientes.size()} de ${requestScope.totalClientes} clientes</p>
                            <nav class="pagination" style="margin-top:0;">
                                <c:if test="${requestScope.paginaActual > 1}">
                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${requestScope.paginaActual - 1}">‹</a>
                                </c:if>
                                <c:forEach var="i" begin="1" end="${requestScope.totalPaginas}">
                                    <c:choose>
                                        <c:when test="${i == requestScope.paginaActual}">
                                            <span class="pagination__btn pagination__btn--active"><c:out value="${i}"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${i}"><c:out value="${i}"/></a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${requestScope.paginaActual < requestScope.totalPaginas}">
                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/admin/clientes?pagina=${requestScope.paginaActual + 1}">›</a>
                                </c:if>
                            </nav>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // Toggle sidebar admin (mobile)
            const adminToggle = document.getElementById('adminMenuToggle');
            const adminSidebar = document.querySelector('.admin-sidebar');
            const adminOverlay = document.getElementById('adminOverlay');

            function openAdminSidebar() {
                adminSidebar.classList.add('is-open');
                adminOverlay.classList.add('is-open');
                document.body.style.overflow = 'hidden';
            }

            function closeAdminSidebar() {
                adminSidebar.classList.remove('is-open');
                adminOverlay.classList.remove('is-open');
                document.body.style.overflow = '';
            }

            if (adminToggle) {
                adminToggle.addEventListener('click', () => {
                    adminSidebar.classList.contains('is-open') ? closeAdminSidebar() : openAdminSidebar();
                });
            }

            if (adminOverlay) {
                adminOverlay.addEventListener('click', closeAdminSidebar);
            }
        </script>


    </body>
</html>
