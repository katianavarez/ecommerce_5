<%-- 
    Document   : cuenta
    Created on : 9 abr 2026, 3:40:34 a.m.
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
        <title>Mi Cuenta — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/auth.css"/>
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
                    <a href="${pageContext.request.contextPath}/app/productos" class="main-nav__link">Colección</a>
                </nav>
                <div class="header-right">
                    <div class="header-actions">
                        <a href="${pageContext.request.contextPath}/app/cuenta" class="header-icon" aria-label="Mi cuenta">
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
                    <button class="header-burger" aria-label="Menú" id="burgerBtn">
                        <span></span><span></span><span></span>
                    </button>
                </div>
            </div>
            <nav class="mobile-nav" id="mobileNav">
                <a href="${pageContext.request.contextPath}/inicio" class="mobile-nav__link">Inicio</a>
                <a href="${pageContext.request.contextPath}/app/productos" class="mobile-nav__link">Colección</a>
                <a href="${pageContext.request.contextPath}/app/carrito" class="mobile-nav__link">Carrito</a>
                <a href="${pageContext.request.contextPath}/auth/logout" class="mobile-nav__link">Cerrar sesión</a>
            </nav>
        </header>

        <main>
            <div class="account-layout">

                <%-- Sidebar --%>
                <aside class="account-sidebar">
                    <div class="account-sidebar__profile">
                        <div class="account-sidebar__avatar">
                            <c:out value="${sessionScope.clienteLogueado.nombre.substring(0,1).toUpperCase()}"/>
                        </div>
                        <p class="account-sidebar__name"><c:out value="${sessionScope.clienteLogueado.nombre}"/></p>
                        <p class="account-sidebar__email"><c:out value="${sessionScope.clienteLogueado.correo}"/></p>
                    </div>
                    <nav class="account-nav">
                        <a href="#pedidos" class="account-nav__link account-nav__link--active" data-section="pedidos">
                            <svg class="account-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
                            Mis Pedidos
                        </a>
                        <a href="#perfil" class="account-nav__link" data-section="perfil">
                            <svg class="account-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                            Editar Perfil
                        </a>
                        <a href="#direcciones" class="account-nav__link" data-section="direcciones">
                            <svg class="account-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                            Mis Direcciones
                        </a>
                        <a href="${pageContext.request.contextPath}/auth/logout"
                           class="account-nav__link"
                           style="color:var(--color-error);border-top:1px solid var(--border-soft);margin-top:var(--sp-2);padding-top:var(--sp-5);">
                            <svg class="account-nav__icon" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                            Cerrar sesión
                        </a>
                    </nav>
                </aside>

                <%-- Contenido --%>
                <div class="account-content">

                    <%-- ── PEDIDOS ─────────────────────────────────────────── --%>
                    <div id="section-pedidos">
                        <h2 class="account-section-title">Mis Pedidos</h2>
                        <c:choose>
                            <c:when test="${empty pedidos and paginaPedidos == 1}">
                                <div style="text-align:center;padding:var(--sp-10);color:var(--text-muted);">
                                    <p style="font-size:var(--fs-lg);margin-bottom:var(--sp-4);">Aún no tienes pedidos</p>
                                    <a href="${pageContext.request.contextPath}/app/productos" class="btn btn--primary">Explorar colección</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="orders-list">
                                    <c:forEach var="pedido" items="${pedidos}">
                                        <div class="order-card">
                                            <div class="order-card__header">
                                                <div>
                                                    <p class="order-card__id">#<c:out value="${pedido.numPedido}"/></p>
                                                    <p class="order-card__date"><c:out value="${pedido.fecha}"/></p>
                                                </div>
                                                <span class="badge badge--${pedido.estado.name().toLowerCase()}">
                                                    <c:out value="${pedido.estado}"/>
                                                </span>
                                            </div>
                                            <div class="order-card__items">
                                                <c:forEach var="detalle" items="${pedido.detalles}" end="2">
                                                    <div class="order-thumb">
                                                        <img src="${detalle.producto.imagenURL.startsWith('http') ? detalle.producto.imagenURL : pageContext.request.contextPath.concat('/').concat(detalle.producto.imagenURL)}"
                                                             alt="${detalle.producto.nombre}"
                                                             style="width:100%;height:100%;object-fit:cover;">
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <div class="order-card__footer">
                                                <div>
                                                    <p style="font-size:var(--fs-xs);color:var(--text-muted);">
                                                        <c:out value="${pedido.detalles.size()}"/> artículo(s)
                                                    </p>
                                                    <p class="order-total">$<fmt:formatNumber value="${pedido.total}" maxFractionDigits="0"/></p>
                                                </div>
                                                <a href="${pageContext.request.contextPath}/app/confirmacion?pedidoId=${pedido.id}"
                                                   class="btn btn--ghost btn--sm">Ver detalle</a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <%-- Paginación de pedidos --%>
                                <c:if test="${totalPedidos > 0}">
                                    <div style="display:flex;align-items:center;justify-content:space-between;gap:var(--sp-4);margin-top:var(--sp-6);flex-wrap:wrap;">
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
                                            Mostrando
                                            <strong><c:out value="${(paginaPedidos - 1) * 5 + 1}"/>&#8211;<c:out value="${paginaPedidos * 5 > totalPedidos ? totalPedidos : paginaPedidos * 5}"/></strong>
                                            de <strong><c:out value="${totalPedidos}"/></strong> pedidos
                                        </p>
                                        <c:if test="${totalPagPed > 1}">
                                            <nav class="pagination" style="margin-top:0;flex-shrink:0;">
                                                <c:if test="${paginaPedidos > 1}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/app/cuenta?seccion=pedidos&paginaPedidos=${paginaPedidos - 1}">&#8249;</a>
                                                </c:if>
                                                <c:forEach var="i" begin="1" end="${totalPagPed}">
                                                    <c:choose>
                                                        <c:when test="${i == paginaPedidos}">
                                                            <a class="pagination__btn pagination__btn--active" href="#"><c:out value="${i}"/></a>
                                                        </c:when>
                                                        <c:when test="${i == 1 || i == totalPagPed || (i >= paginaPedidos - 1 && i <= paginaPedidos + 1)}">
                                                            <a class="pagination__btn" href="${pageContext.request.contextPath}/app/cuenta?seccion=pedidos&paginaPedidos=${i}"><c:out value="${i}"/></a>
                                                        </c:when>
                                                        <c:when test="${i == paginaPedidos - 2 || i == paginaPedidos + 2}">
                                                            <span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">&#8230;</span>
                                                        </c:when>
                                                    </c:choose>
                                                </c:forEach>
                                                <c:if test="${paginaPedidos < totalPagPed}">
                                                    <a class="pagination__btn" href="${pageContext.request.contextPath}/app/cuenta?seccion=pedidos&paginaPedidos=${paginaPedidos + 1}">&#8250;</a>
                                                </c:if>
                                            </nav>
                                        </c:if>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- ── PERFIL ──────────────────────────────────────────── --%>
                    <div id="section-perfil" style="display:none;">
                        <h2 class="account-section-title">Editar Perfil</h2>

                        <%-- Mensajes globales de perfil --%>
                        <c:if test="${not empty exito}">
                            <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                                ✓ <c:out value="${exito}"/>
                            </div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                                ⚠ <c:out value="${error}"/>
                            </div>
                        </c:if>

                        <%-- Formulario datos personales --%>
                        <div style="max-width:520px;background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-lg);padding:var(--sp-6);margin-bottom:var(--sp-6);">
                            <p style="font-size:var(--fs-sm);font-weight:var(--fw-medium);color:var(--text-heading);margin-bottom:var(--sp-5);">Información personal</p>
                            <form method="POST" action="${pageContext.request.contextPath}/app/cuenta">
                                <input type="hidden" name="accion" value="actualizarPerfil">
                                <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                    <div class="form-group">
                                        <label class="form-label">Nombre completo</label>
                                        <input class="form-control" type="text" name="nombre"
                                               value="<c:out value='${sessionScope.clienteLogueado.nombre}'/>"
                                               placeholder="Tu nombre completo" required>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Correo electrónico</label>
                                        <%-- El correo no es editable: es el identificador único --%>
                                        <input class="form-control" type="email"
                                               value="<c:out value='${sessionScope.clienteLogueado.correo}'/>"
                                               readonly
                                               style="background:var(--bg-muted,#f5f5f5);cursor:not-allowed;"
                                               title="El correo no puede modificarse">
                                        <p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:4px;">
                                            El correo electrónico no puede modificarse.
                                        </p>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Teléfono</label>
                                        <input class="form-control" type="tel" name="telefono"
                                               value="<c:out value='${sessionScope.clienteLogueado.telefono}'/>"
                                               placeholder="+52 55 0000 0000">
                                    </div>
                                    <button type="submit" class="btn btn--primary btn--sm" style="align-self:flex-start;">
                                        Guardar cambios
                                    </button>
                                </div>
                            </form>
                        </div>

                        <%-- Formulario cambiar contraseña --%>
                        <div style="max-width:520px;background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-lg);padding:var(--sp-6);">
                            <p style="font-size:var(--fs-sm);font-weight:var(--fw-medium);color:var(--text-heading);margin-bottom:var(--sp-5);">Cambiar contraseña</p>

                            <%-- Mensajes específicos de contraseña --%>
                            <c:if test="${not empty exitoContrasena}">
                                <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                    ✓ <c:out value="${exitoContrasena}"/>
                                </div>
                            </c:if>
                            <c:if test="${not empty errorContrasena}">
                                <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);font-size:var(--fs-sm);">
                                    ⚠ <c:out value="${errorContrasena}"/>
                                </div>
                            </c:if>

                            <form method="POST" action="${pageContext.request.contextPath}/app/cuenta" id="formContrasena">
                                <input type="hidden" name="accion" value="cambiarContrasena">
                                <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                    <div class="form-group">
                                        <label class="form-label">Contraseña actual</label>
                                        <input class="form-control" type="password" name="contrasenaActual"
                                               placeholder="••••••••" required>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label"
                                               style="display:flex;justify-content:space-between;align-items:center;">
                                            Nueva contraseña
                                            <span id="pwdCounter2" style="font-size:var(--fs-xs);color:var(--text-muted);font-weight:normal;text-transform:none;letter-spacing:normal;">
                                                0 / 8 mín.
                                            </span>
                                        </label>
                                        <input class="form-control" type="password" id="nuevaContrasena"
                                               name="nuevaContrasena" placeholder="Mínimo 8 caracteres"
                                               minlength="8" required>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Confirmar nueva contraseña</label>
                                        <input class="form-control" type="password" id="confirmarContrasena"
                                               name="confirmarContrasena" placeholder="Repite la nueva contraseña"
                                               minlength="8" required>
                                        <p id="pwdMatchHint" style="display:none;margin-top:4px;font-size:var(--fs-xs);color:var(--color-error);">
                                            Las contraseñas no coinciden.
                                        </p>
                                    </div>
                                    <button type="submit" class="btn btn--primary btn--sm" style="align-self:flex-start;">
                                        Actualizar contraseña
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <%-- ── DIRECCIONES ─────────────────────────────────────── --%>
                    <div id="section-direcciones" style="display:none;">
                        <h2 class="account-section-title">Mis Direcciones</h2>

                        <%-- Mensajes --%>
                        <c:if test="${not empty exitoDireccion}">
                            <div style="background:#d4edda;border:1px solid #c3e6cb;color:#155724;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                                ✓ <c:out value="${exitoDireccion}"/>
                            </div>
                        </c:if>
                        <c:if test="${not empty errorDireccion}">
                            <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-5);font-size:var(--fs-sm);">
                                ⚠ <c:out value="${errorDireccion}"/>
                            </div>
                        </c:if>

                        <%-- Grid de direcciones guardadas --%>
                        <div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--sp-4);max-width:700px;margin-bottom:var(--sp-6);">

                            <c:forEach var="dir" items="${direcciones}" varStatus="status">
                                <div style="border:1.5px solid ${dir.principal ? 'var(--color-secondary)' : 'var(--border-soft)'};border-radius:var(--r-md);padding:var(--sp-5);position:relative;">
                                    <c:if test="${dir.principal}">
                                        <span class="badge badge--new" style="position:absolute;top:var(--sp-3);right:var(--sp-3);">Principal</span>
                                    </c:if>
                                    <p style="font-weight:var(--fw-medium);margin-bottom:var(--sp-2);padding-right:var(--sp-10);">
                                        <c:out value="${sessionScope.clienteLogueado.nombre}"/>
                                    </p>
                                    <p style="font-size:var(--fs-sm);color:var(--text-muted);line-height:1.8;">
                                        <c:out value="${dir.calle}"/><br>
                                        <c:out value="${dir.ciudad}"/>, <c:out value="${dir.estado}"/> <c:out value="${dir.codigoPostal}"/>
                                    </p>
                                    <div style="display:flex;gap:var(--sp-2);margin-top:var(--sp-4);flex-wrap:wrap;">
                                        <%-- Botón Hacer principal (solo si no lo es ya) --%>
                                        <c:if test="${not dir.principal}">
                                            <form method="POST" action="${pageContext.request.contextPath}/app/cuenta" style="margin:0;">
                                                <input type="hidden" name="accion" value="marcarPrincipal">
                                                <input type="hidden" name="direccionId" value="${dir.id}">
                                                <button type="submit" class="btn btn--sm"
                                                        style="color:var(--color-secondary);border:1.5px solid var(--color-secondary);">
                                                    Hacer principal
                                                </button>
                                            </form>
                                        </c:if>
                                        <%-- Botón Editar --%>
                                        <button class="btn btn--ghost btn--sm"
                                                onclick="abrirEditar(${dir.id},'<c:out value="${dir.calle}"/>','<c:out value="${dir.ciudad}"/>','<c:out value="${dir.estado}"/>','<c:out value="${dir.codigoPostal}"/>')">
                                            Editar
                                        </button>
                                        <%-- Botón Eliminar --%>
                                        <form method="POST" action="${pageContext.request.contextPath}/app/cuenta"
                                              onsubmit="return confirm('¿Eliminar esta dirección?');" style="margin:0;">
                                            <input type="hidden" name="accion" value="eliminarDireccion">
                                            <input type="hidden" name="direccionId" value="${dir.id}">
                                            <button type="submit" class="btn btn--sm"
                                                    style="color:var(--color-error);border:1.5px solid var(--border-soft);">
                                                Eliminar
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>

                            <%-- Tarjeta agregar nueva --%>
                            <div style="border:1.5px dashed var(--border-medium);border-radius:var(--r-md);padding:var(--sp-5);display:flex;flex-direction:column;align-items:center;justify-content:center;gap:var(--sp-3);cursor:pointer;min-height:150px;"
                                 onclick="abrirAgregar()">
                                <span style="font-size:2rem;color:var(--text-light);">+</span>
                                <p style="font-size:var(--fs-sm);color:var(--text-muted);">Agregar dirección</p>
                            </div>
                        </div>

                        <%-- ── Modal Agregar dirección ─────────────────────── --%>
                        <div id="modalAgregar" style="display:none;max-width:480px;background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-lg);padding:var(--sp-6);">
                            <h4 style="font-size:var(--fs-md);font-weight:var(--fw-medium);margin-bottom:var(--sp-5);">Nueva dirección</h4>
                            <form method="POST" action="${pageContext.request.contextPath}/app/cuenta">
                                <input type="hidden" name="accion" value="agregarDireccion">
                                <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                    <div class="form-group">
                                        <label class="form-label">Calle y número *</label>
                                        <input class="form-control" type="text" name="calle" placeholder="Av. Ejemplo 123" required>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label class="form-label">Ciudad *</label>
                                            <input class="form-control" type="text" name="ciudad" placeholder="Ciudad" required>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Estado *</label>
                                            <input class="form-control" type="text" name="estado" placeholder="Estado" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Código postal *</label>
                                        <input class="form-control" type="text" name="codigoPostal" placeholder="00000" maxlength="10" required>
                                    </div>
                                    <div style="display:flex;gap:var(--sp-3);">
                                        <button type="submit" class="btn btn--primary btn--sm">Guardar dirección</button>
                                        <button type="button" class="btn btn--ghost btn--sm" onclick="cerrarModales()">Cancelar</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <%-- ── Modal Editar dirección ──────────────────────── --%>
                        <div id="modalEditar" style="display:none;max-width:480px;background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-lg);padding:var(--sp-6);">
                            <h4 style="font-size:var(--fs-md);font-weight:var(--fw-medium);margin-bottom:var(--sp-5);">Editar dirección</h4>
                            <form method="POST" action="${pageContext.request.contextPath}/app/cuenta">
                                <input type="hidden" name="accion" value="editarDireccion">
                                <input type="hidden" name="direccionId" id="editDireccionId">
                                <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                    <div class="form-group">
                                        <label class="form-label">Calle y número *</label>
                                        <input class="form-control" type="text" name="calle" id="editCalle" required>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label class="form-label">Ciudad *</label>
                                            <input class="form-control" type="text" name="ciudad" id="editCiudad" required>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Estado *</label>
                                            <input class="form-control" type="text" name="estado" id="editEstado" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label">Código postal *</label>
                                        <input class="form-control" type="text" name="codigoPostal" id="editCP" maxlength="10" required>
                                    </div>
                                    <div style="display:flex;gap:var(--sp-3);">
                                        <button type="submit" class="btn btn--primary btn--sm">Actualizar</button>
                                        <button type="button" class="btn btn--ghost btn--sm" onclick="cerrarModales()">Cancelar</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-top">
                <div class="footer-brand">
                    <div class="footer-brand__logo">Vel<span>our</span></div>
                    <p class="footer-brand__text">Moda premium diseñada para la mujer contemporánea.</p>
                </div>
                <div class="footer-col"><p class="footer-col__title">Tienda</p><div class="footer-col__links"><a href="#">Colección</a><a href="#">Sale</a></div></div>
                <div class="footer-col"><p class="footer-col__title">Ayuda</p><div class="footer-col__links"><a href="#">Envíos</a><a href="#">Devoluciones</a><a href="#">Contacto</a></div></div>
                <div class="footer-col"><p class="footer-col__title">Empresa</p><div class="footer-col__links"><a href="#">Sobre Velour</a></div></div>
            </div>
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>

        <script>
            // Burger menu
            document.getElementById('burgerBtn').addEventListener('click', () =>
                document.getElementById('mobileNav').classList.toggle('is-open'));

            // Secciones del sidebar
            const SECCIONES = ['pedidos', 'perfil', 'direcciones'];

            function activarSeccion(nombre) {
                document.querySelectorAll('.account-nav__link[data-section]').forEach(l =>
                    l.classList.remove('account-nav__link--active'));
                const link = document.querySelector('.account-nav__link[data-section="' + nombre + '"]');
                if (link) link.classList.add('account-nav__link--active');
                SECCIONES.forEach(s => {
                    const el = document.getElementById('section-' + s);
                    if (el) el.style.display = s === nombre ? 'block' : 'none';
                });
            }

            document.querySelectorAll('.account-nav__link[data-section]').forEach(link => {
                link.addEventListener('click', (e) => {
                    e.preventDefault();
                    activarSeccion(link.dataset.section);
                });
            });

            // Si el servidor indica sección activa (tras un POST) → abrirla
            const seccionServidor = '<c:out value="${seccionActiva}"/>';
            if (seccionServidor && seccionServidor.trim() !== '') {
                activarSeccion(seccionServidor.trim());
            }

            // Contador de caracteres nueva contraseña
            const nuevaPwd    = document.getElementById('nuevaContrasena');
            const confirmarPwd = document.getElementById('confirmarContrasena');
            const counter2    = document.getElementById('pwdCounter2');
            const matchHint   = document.getElementById('pwdMatchHint');

            if (nuevaPwd) {
                nuevaPwd.addEventListener('input', () => {
                    const len = nuevaPwd.value.length;
                    counter2.textContent = len + ' / 8 mín.';
                    counter2.style.color = len >= 8 ? 'var(--color-success, green)' : 'var(--text-muted)';
                });
            }

            // Validación confirmar contraseña
            if (confirmarPwd) {
                confirmarPwd.addEventListener('input', () => {
                    matchHint.style.display =
                        confirmarPwd.value.length > 0 && confirmarPwd.value !== nuevaPwd.value
                        ? 'block' : 'none';
                });
            }

            // Bloquear envío si no coinciden
            const formContrasena = document.getElementById('formContrasena');
            if (formContrasena) {
                formContrasena.addEventListener('submit', (e) => {
                    if (nuevaPwd.value !== confirmarPwd.value) {
                        e.preventDefault();
                        matchHint.style.display = 'block';
                        confirmarPwd.focus();
                    }
                });
            }

            // ── Modales de dirección ──────────────────────────
            function abrirAgregar() {
                cerrarModales();
                document.getElementById('modalAgregar').style.display = 'block';
            }

            function abrirEditar(id, calle, ciudad, estado, cp) {
                cerrarModales();
                document.getElementById('editDireccionId').value = id;
                document.getElementById('editCalle').value       = calle;
                document.getElementById('editCiudad').value      = ciudad;
                document.getElementById('editEstado').value      = estado;
                document.getElementById('editCP').value          = cp;
                document.getElementById('modalEditar').style.display = 'block';
            }

            function cerrarModales() {
                document.getElementById('modalAgregar').style.display = 'none';
                document.getElementById('modalEditar').style.display  = 'none';
            }
        </script>
    </body>
</html>
