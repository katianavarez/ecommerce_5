<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><c:choose><c:when test="${not empty proveedor.id and proveedor.id > 0}">Editar</c:when><c:otherwise>Nuevo</c:otherwise></c:choose> Proveedor — Velour Admin</title>
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
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="admin-nav__link ">Dashboard</a>
                    <p class="admin-nav__section">Catálogo</p>
                    <a href="${pageContext.request.contextPath}/admin/productos" class="admin-nav__link ">Productos</a>
                    <a href="${pageContext.request.contextPath}/admin/proveedores" class="admin-nav__link admin-nav__link--active">Proveedores</a>
                    <p class="admin-nav__section">Ventas</p>
                    <a href="${pageContext.request.contextPath}/admin/pedidos" class="admin-nav__link ">Pedidos</a>
                    <p class="admin-nav__section">Comunidad</p>
                    <a href="${pageContext.request.contextPath}/admin/resenas" class="admin-nav__link ">Reseñas</a>
                    <a href="${pageContext.request.contextPath}/admin/clientes" class="admin-nav__link ">Clientes</a>
                </nav>
                <div class="admin-sidebar__footer">
                    <a href="${pageContext.request.contextPath}/admin/logout" class="admin-sidebar__logout">Cerrar sesión</a>
                </div>
            </aside>

            <div class="admin-content">
                <div class="admin-topbar">
                    <div>
                        <h1 class="admin-topbar__title">
                            <c:choose><c:when test="${not empty proveedor.id and proveedor.id > 0}">Editar Proveedor</c:when><c:otherwise>Nuevo Proveedor</c:otherwise></c:choose>
                        </h1>
                        <p class="admin-topbar__subtitle"><a href="${pageContext.request.contextPath}/admin/proveedores" style="color:var(--color-secondary);">← Volver a proveedores</a></p>
                    </div>
                </div>
                <div class="admin-body">
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);"><c:out value="${error}"/></div>
                    </c:if>

                    <form method="POST" action="${pageContext.request.contextPath}/admin/proveedores">
                        <input type="hidden" name="accion" value="${not empty proveedor.id and proveedor.id > 0 ? 'actualizar' : 'crear'}">
                        <c:if test="${not empty proveedor.id and proveedor.id > 0}">
                            <input type="hidden" name="id" value="${proveedor.id}">
                        </c:if>

                        <div class="admin-card">
                            <div class="admin-card__header"><h3 class="admin-card__title">Información del Proveedor</h3></div>
                            <div class="admin-card__body">
                                <div class="product-form">
                                    <div class="form-group">
                                        <label class="form-label">Nombre *</label>
                                        <input class="form-control" type="text" name="nombre" value="<c:out value='${proveedor.nombre}'/>" required maxlength="100">
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label class="form-label">RFC</label>
                                            <input class="form-control" type="text" name="rfc" value="<c:out value='${proveedor.rfc}'/>" maxlength="13" placeholder="ABC010101XYZ" style="text-transform:uppercase;">
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:4px;">Formato mexicano. Opcional.</p>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Teléfono</label>
                                            <input class="form-control" type="tel" name="telefono" value="<c:out value='${proveedor.telefono}'/>" maxlength="20" placeholder="6440000000">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Correo de contacto</label>
                                        <input class="form-control" type="email" name="correo" value="<c:out value='${proveedor.correo}'/>" placeholder="contacto@proveedor.com">
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Dirección</label>
                                        <textarea class="form-control" name="direccion" rows="2"><c:out value="${proveedor.direccion}"/></textarea>
                                    </div>

                                    <div style="display:flex;gap:var(--sp-3);margin-top:var(--sp-5);">
                                        <button type="submit" class="btn btn--gold">
                                            <c:choose>
                                                <c:when test="${not empty proveedor.id and proveedor.id > 0}">Guardar cambios</c:when>
                                                <c:otherwise>Crear proveedor</c:otherwise>
                                            </c:choose>
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/proveedores" class="btn btn--ghost">Cancelar</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
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
