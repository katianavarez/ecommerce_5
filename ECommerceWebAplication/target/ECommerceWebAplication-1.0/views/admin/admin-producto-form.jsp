<%-- 
    Document   : admin-producto-form
    Created on : 9 abr 2026, 4:45:38 a.m.
    Author     : PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><c:choose><c:when test="${not empty producto}">Editar</c:when><c:otherwise>Nuevo</c:otherwise></c:choose> Producto — Velour Admin</title>
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
                        <h1 class="admin-topbar__title">
                            <c:choose><c:when test="${not empty producto}">Editar Producto</c:when><c:otherwise>Nuevo Producto</c:otherwise></c:choose>
                        </h1>
                        <p class="admin-topbar__subtitle"><a href="${pageContext.request.contextPath}/admin/productos" style="color:var(--color-secondary);">← Volver a productos</a></p>
                    </div>
                </div>
                <div class="admin-body">
                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);"><c:out value="${error}"/></div>
                    </c:if>

                    <form method="POST" action="${pageContext.request.contextPath}/admin/productos">
                        <input type="hidden" name="accion" value="${not empty producto ? 'actualizar' : 'crear'}">
                        <c:if test="${not empty producto}">
                            <input type="hidden" name="id" value="${producto.id}">
                        </c:if>
                        <c:if test="${not empty producto and empty producto.stockPorTalla}">
                            <input type="hidden" name="stock" value="${producto.stock}">
                        </c:if>

                        <div class="admin-card">
                            <div class="admin-card__header"><h3 class="admin-card__title">Información del Producto</h3></div>
                            <div class="admin-card__body">
                                <div class="product-form">
                                    <div class="form-group">
                                        <label class="form-label">Nombre *</label>
                                        <input class="form-control" type="text" name="nombre" value="<c:out value='${producto.nombre}'/>" required>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Descripción *</label>
                                        <textarea class="form-control" name="descripcion" rows="4" required><c:out value="${producto.descripcion}"/></textarea>
                                    </div>
                                    <%-- Precio y categoría --%>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label class="form-label">Precio (MXN) *</label>
                                            <input class="form-control" type="number" step="0.01" name="precio" value="${producto.precio}" required>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Categoría *</label>
                                            <select class="form-control" name="categoriaId" required>
                                                <option value="">-- Seleccionar --</option>
                                                <c:forEach var="cat" items="${categorias}">
                                                    <option value="${cat.id}" ${producto.categoria.id == cat.id ? 'selected' : ''}>
                                                        <c:out value="${cat.nombre}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>

                                    <%-- Proveedor (opcional) --%>
                                    <div class="form-group">
                                        <label class="form-label">Proveedor</label>
                                        <select class="form-control" name="proveedorId">
                                            <option value="">-- Sin proveedor --</option>
                                            <c:forEach var="prov" items="${proveedores}">
                                                <option value="${prov.id}" ${producto.proveedor.id == prov.id ? 'selected' : ''}>
                                                    <c:out value="${prov.nombre}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:4px;">Opcional. Solo se muestran proveedores activos.</p>
                                    </div>

                                    <%-- Color — botones predefinidos --%>
                                    <div class="form-group">
                                        <label class="form-label">Color del producto *</label>
                                        <input type="hidden" name="color" id="colorSeleccionado" value="${producto.color}">
                                        <div style="display:flex;flex-wrap:wrap;gap:var(--sp-2);margin-top:var(--sp-2);">
                                            <c:forEach var="col" items="${colores}">
                                                <button type="button"
                                                        class="color-btn-admin ${producto.color == col.name() ? 'color-btn-admin--active' : ''}"
                                                        data-color="${col.name()}"
                                                        title="${col.nombre}"
                                                        onclick="seleccionarColor(this)"
                                                        style="width:32px;height:32px;border-radius:50%;background:${col.hex};border:2px solid ${producto.color == col.name() ? 'var(--color-secondary)' : 'var(--border-soft)'};cursor:pointer;position:relative;transition:transform 0.15s;">
                                                </button>
                                            </c:forEach>
                                        </div>
                                        <p id="colorLabel" style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-2);">
                                            Color seleccionado: <strong id="colorNombre">
                                                <c:choose>
                                                    <c:when test="${not empty producto.color}"><c:out value="${producto.colorNombre}"/></c:when>
                                                    <c:otherwise>Ninguno</c:otherwise>
                                                </c:choose>
                                            </strong>
                                        </p>
                                        <p id="colorError" style="display:none;color:var(--color-error);font-size:var(--fs-xs);margin-top:4px;">
                                            ⚠ Selecciona un color para el producto.
                                        </p>
                                    </div>

                                    <%-- URL de imagen con preview --%>
                                    <div class="form-group">
                                        <label class="form-label">URL de imagen *</label>
                                        <input class="form-control" type="text" name="imagenURL"
                                               id="imagenURL"
                                               value="${producto.imagenURL}"
                                               placeholder="assets/img/categoria/imagen.png  o  https://..."
                                               oninput="previewImagen(this.value)"
                                               required>
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:4px;">
                                            Puedes usar rutas locales (<code>assets/img/...</code>) o URLs externas (<code>https://...</code>).
                                        </p>
                                        <div id="imgPreview" style="margin-top:var(--sp-3);display:none;">
                                            <img id="imgPreviewEl" src="" alt="Preview"
                                                 style="max-width:180px;max-height:220px;border-radius:var(--r-md);border:1px solid var(--border-soft);object-fit:cover;">
                                            <p id="imgError" style="display:none;color:var(--color-error);font-size:var(--fs-xs);margin-top:4px;">
                                                No se pudo cargar la imagen. Verifica la URL.
                                            </p>
                                        </div>
                                    </div>

                                    <%-- Stock distribuido por talla --%>
                                    <div class="form-group">
                                        <label class="form-label">Stock por talla</label>
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-bottom:var(--sp-3);">
                                            Indica cuántas unidades hay de cada talla. Deja en 0 si esa talla no aplica.
                                            El stock total se calcula automáticamente.
                                        </p>
                                        <div class="form-row" style="grid-template-columns:repeat(5,1fr);">
                                            <c:forEach var="talla" items="${tallas}">
                                                <div class="form-group" style="text-align:center;">
                                                    <label class="form-label" style="text-align:center;display:block;">
                                                        <c:out value="${talla}"/>
                                                    </label>
                                                    <input class="form-control stock-talla-input"
                                                           type="number"
                                                           name="stock_${talla}"
                                                           id="stock_${talla}"
                                                           min="0" value="0"
                                                           style="text-align:center;"
                                                           oninput="actualizarStockTotal()">
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <div style="margin-top:var(--sp-3);padding:var(--sp-3) var(--sp-4);background:rgba(201,169,110,0.1);border-radius:var(--r-sm);border-left:3px solid var(--color-secondary);">
                                            Stock total calculado: <strong id="stockTotalLabel">0</strong> unidades
                                        </div>
                                        <%-- Si es editar, cargar stocks actuales via JS --%>
                                        <c:if test="${not empty producto and not empty producto.stockPorTalla}">
                                            <script>
                                                window.addEventListener('DOMContentLoaded', function() {
                                                    <c:forEach var="st" items="${producto.stockPorTalla}">
                                                        var inp = document.getElementById('stock_${st.talla}');
                                                        if (inp) inp.value = '${st.cantidad}';
                                                    </c:forEach>
                                                    actualizarStockTotal();
                                                });
                                            </script>
                                        </c:if>
                                        <%-- Si hay stock pero sin distribución por talla, pre-llenar equitativamente --%>
                                        <c:if test="${not empty producto and producto.stock > 0 and empty producto.stockPorTalla}">
                                            <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-2);">
                                                Stock actual sin distribución: <strong>${producto.stock}</strong> unidades.
                                                Distribuye el stock entre las tallas.
                                            </p>
                                        </c:if>
                                    </div>

                                    <div style="display:flex;gap:var(--sp-3);margin-top:var(--sp-5);">
                                        <button type="submit" class="btn btn--gold" onclick="return validarFormProducto()">
                                            <c:choose>
                                                <c:when test="${not empty producto}">Guardar cambios</c:when>
                                                <c:otherwise>Crear producto</c:otherwise>
                                            </c:choose>
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/productos" class="btn btn--ghost">Cancelar</a>
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

            // ── Selector de color ──────────────────────────────
            function seleccionarColor(btn) {
                document.querySelectorAll('.color-btn-admin').forEach(b => {
                    b.style.border = '2px solid var(--border-soft)';
                    b.style.transform = 'scale(1)';
                });
                btn.style.border = '2px solid var(--color-secondary)';
                btn.style.transform = 'scale(1.2)';
                document.getElementById('colorSeleccionado').value = btn.dataset.color;
                document.getElementById('colorNombre').textContent = btn.getAttribute('title');
                // Ocultar error de color si estaba visible
                const err = document.getElementById('colorError');
                if (err) err.style.display = 'none';
            }

            // ── Validar formulario antes de enviar ─────────────
            function validarFormProducto() {
                const color = document.getElementById('colorSeleccionado').value;
                if (!color || color.trim() === '') {
                    const err = document.getElementById('colorError');
                    if (err) {
                        err.style.display = 'block';
                        err.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }
                    return false; // bloquea el submit
                }
                return true;
            }

            // ── Contador de stock total ────────────────────────
            function actualizarStockTotal() {
                let total = 0;
                document.querySelectorAll('.stock-talla-input').forEach(inp => {
                    total += parseInt(inp.value) || 0;
                });
                document.getElementById('stockTotalLabel').textContent = total;
            }

            // ── Preview de imagen ──────────────────────────────
            function previewImagen(url) {
                if (!url || url.trim() === '') {
                    document.getElementById('imgPreview').style.display = 'none';
                    return;
                }
                const img = document.getElementById('imgPreviewEl');
                const errMsg = document.getElementById('imgError');
                const preview = document.getElementById('imgPreview');

                // Si es URL externa, usarla directo; si es local, anteponer contextPath
                const src = url.startsWith('http') ? url :
                            (window.location.pathname.split('/').slice(0,2).join('/') + '/' + url);
                img.src = src;
                preview.style.display = 'block';
                img.onerror = () => { errMsg.style.display = 'block'; img.style.display = 'none'; };
                img.onload  = () => { errMsg.style.display = 'none';  img.style.display = 'block'; };
            }

            // Inicializar preview y stock si hay producto al cargar
            window.addEventListener('DOMContentLoaded', function() {
                const urlField = document.getElementById('imagenURL');
                if (urlField && urlField.value) previewImagen(urlField.value);
                actualizarStockTotal();
            });
        </script>
    </body>
</html>
