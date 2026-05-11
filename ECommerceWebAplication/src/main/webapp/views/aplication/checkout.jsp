<%-- 
    Document   : checkout
    Created on : 9 abr 2026, 4:12:38 a.m.
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
        <title>Checkout — Velour</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/variables.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/base.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/layout.css"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles/paginas/cart.css"/>
    </head>
    <body>
        <header class="site-header">
            <div class="header-main">
                <a href="${pageContext.request.contextPath}/inicio" class="site-logo">
                    <div class="site-logo__wordmark">Vel<span>our</span></div>
                    <div class="site-logo__sub">Moda de Autor</div>
                </a>
                <div style="flex:1;text-align:center;">
                    <p style="font-size:var(--fs-xs);letter-spacing:0.15em;text-transform:uppercase;color:var(--text-muted);">Pago Seguro</p>
                </div>
                <span class="card-icon">SSL</span>
            </div>
        </header>

        <main>
            <div class="checkout-layout">
                <form id="checkoutForm" class="checkout-form" method="POST" action="${pageContext.request.contextPath}/app/checkout">

                    <c:if test="${not empty error}">
                        <div style="background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:var(--sp-3) var(--sp-4);border-radius:var(--r-md);margin-bottom:var(--sp-4);">
                            <c:out value="${error}"/>
                        </div>
                    </c:if>

                    <!-- Info de entrega -->
                    <div class="checkout-section">
                        <h2 class="checkout-section__title">
                            <span class="checkout-section__num">1</span> Información de Entrega
                        </h2>
                        <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                            <div class="form-group">
                                <label class="form-label">Nombre completo</label>
                                <input class="form-control" type="text" value="${sessionScope.clienteLogueado.nombre}" readonly>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Correo electrónico</label>
                                <input class="form-control" type="email" value="${sessionScope.clienteLogueado.correo}" readonly>
                            </div>
                            <c:if test="${not empty direccionPrincipal}">
                                <div style="background:rgba(201,169,110,0.08);border:1px solid var(--color-secondary);border-radius:var(--r-md);padding:var(--sp-3) var(--sp-4);margin-bottom:var(--sp-4);font-size:var(--fs-sm);display:flex;align-items:center;gap:var(--sp-3);">
                                    <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8" style="color:var(--color-secondary);flex-shrink:0;"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                                    <span>Dirección principal cargada. Puedes modificarla si lo deseas.</span>
                                </div>
                            </c:if>
                            <div class="form-group">
                                <label class="form-label">Dirección (calle y número) *</label>
                                <input class="form-control" type="text" name="calle" placeholder="Av. Ejemplo 123"
                                       value="${not empty direccionPrincipal ? direccionPrincipal.calle : ''}" required>
                            </div>
                            <div class="form-row form-row--3">
                                <div class="form-group">
                                    <label class="form-label">Ciudad *</label>
                                    <input class="form-control" type="text" name="ciudad" placeholder="Ciudad de México" value="${not empty direccionPrincipal ? direccionPrincipal.ciudad : ''}" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Estado *</label>
                                    <input class="form-control" type="text" name="estado" placeholder="CDMX" value="${not empty direccionPrincipal ? direccionPrincipal.estado : ''}" required>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Código Postal *</label>
                                    <input class="form-control" type="text" name="codigoPostal" placeholder="00000" value="${not empty direccionPrincipal ? direccionPrincipal.codigoPostal : ''}" required>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Método de pago -->
                    <div class="checkout-section">
                        <h2 class="checkout-section__title">
                            <span class="checkout-section__num">2</span> Método de Pago
                        </h2>
                        <div class="payment-methods">

                            <%-- TARJETA --%>
                            <div class="payment-method payment-method--active" id="pm-TARJETA">
                                <input type="radio" name="metodoPago" value="TARJETA" id="mp_TARJETA"
                                       checked onchange="mostrarPago('TARJETA')">
                                <label for="mp_TARJETA" class="payment-method__label">
                                    Tarjeta de crédito / débito
                                </label>
                                <div class="payment-method__cards">
                                    <span class="card-icon">VISA</span>
                                    <span class="card-icon">MC</span>
                                    <span class="card-icon">AMEX</span>
                                </div>
                            </div>
                            <%-- Campos tarjeta (visibles por defecto) --%>
                            <div id="fields-TARJETA" style="background:var(--bg-muted,#fafafa);border:1px solid var(--border-soft);border-radius:var(--r-md);padding:var(--sp-5);display:block;">
                                <div style="display:flex;flex-direction:column;gap:var(--sp-4);">
                                    <div class="form-group">
                                        <label class="form-label">Número de tarjeta</label>
                                        <input class="form-control" type="text" id="numTarjeta"
                                               placeholder="0000 0000 0000 0000" maxlength="19"
                                               oninput="formatearTarjeta(this)">
                                        <p id="errNumTarjeta" style="display:none;color:var(--color-error);font-size:var(--fs-xs);margin-top:4px;">
                                            Ingresa 16 dígitos válidos.
                                        </p>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label class="form-label">Vencimiento (MM/AA)</label>
                                            <input class="form-control" type="text" id="vencimiento"
                                                   placeholder="MM/AA" maxlength="5"
                                                   oninput="formatearVencimiento(this)">
                                            <p id="errVencimiento" style="display:none;color:var(--color-error);font-size:var(--fs-xs);margin-top:4px;">
                                                Fecha inválida o vencida.
                                            </p>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">CVV</label>
                                            <input class="form-control" type="password" id="cvv"
                                                   placeholder="•••" maxlength="3"
                                                   oninput="this.value=this.value.replace(/\D/g,'')">
                                            <p id="errCVV" style="display:none;color:var(--color-error);font-size:var(--fs-xs);margin-top:4px;">
                                                El CVV debe tener 3 dígitos.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <%-- TRANSFERENCIA --%>
                            <div class="payment-method" id="pm-TRANSFERENCIA">
                                <input type="radio" name="metodoPago" value="TRANSFERENCIA" id="mp_TRANSFERENCIA"
                                       onchange="mostrarPago('TRANSFERENCIA')">
                                <label for="mp_TRANSFERENCIA" class="payment-method__label">
                                    Transferencia bancaria
                                </label>
                            </div>
                            <%-- Info transferencia (oculto por defecto) --%>
                            <div id="fields-TRANSFERENCIA" style="display:none;background:var(--bg-muted,#fafafa);border:1px solid var(--border-soft);border-radius:var(--r-md);padding:var(--sp-5);">
                                <p style="font-size:var(--fs-sm);color:var(--text-heading);font-weight:var(--fw-medium);margin-bottom:var(--sp-3);">
                                    Datos para la transferencia
                                </p>
                                <div style="display:flex;flex-direction:column;gap:var(--sp-2);font-size:var(--fs-sm);color:var(--text-muted);">
                                    <div style="display:flex;justify-content:space-between;">
                                        <span>Banco:</span><strong style="color:var(--text-heading);">Velour Bank</strong>
                                    </div>
                                    <div style="display:flex;justify-content:space-between;">
                                        <span>Cuenta:</span><strong style="color:var(--text-heading);letter-spacing:0.05em;">1111 1111 1111 1111</strong>
                                    </div>
                                    <div style="display:flex;justify-content:space-between;">
                                        <span>CLABE:</span><strong style="color:var(--text-heading);letter-spacing:0.05em;">111111111111111111</strong>
                                    </div>
                                    <div style="display:flex;justify-content:space-between;">
                                        <span>Beneficiario:</span><strong style="color:var(--text-heading);">Velour S.A. de C.V.</strong>
                                    </div>
                                </div>
                                <div style="margin-top:var(--sp-4);padding:var(--sp-3) var(--sp-4);background:rgba(201,169,110,0.1);border-radius:var(--r-sm);border-left:3px solid var(--color-secondary);">
                                    <p style="font-size:var(--fs-xs);color:var(--text-muted);">
                                        Realiza la transferencia por el monto exacto y envía tu comprobante a
                                        <strong>pagos@velour.mx</strong> con el asunto
                                        <strong>"Comprobante de pago - Velour"</strong>.
                                        Tu pedido se procesará una vez verificado el pago.
                                    </p>
                                </div>
                            </div>

                            <%-- CONTRA ENTREGA --%>
                            <div class="payment-method" id="pm-CONTRA_ENTREGA">
                                <input type="radio" name="metodoPago" value="CONTRA_ENTREGA" id="mp_CONTRA_ENTREGA"
                                       onchange="mostrarPago('CONTRA_ENTREGA')">
                                <label for="mp_CONTRA_ENTREGA" class="payment-method__label">
                                    Pago contra entrega
                                </label>
                            </div>
                            <%-- Info contra entrega (oculto por defecto) --%>
                            <div id="fields-CONTRA_ENTREGA" style="display:none;background:var(--bg-muted,#fafafa);border:1px solid var(--border-soft);border-radius:var(--r-md);padding:var(--sp-5);">
                                <div style="display:flex;align-items:flex-start;gap:var(--sp-3);">
                                    <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24" style="flex-shrink:0;color:var(--color-secondary);margin-top:2px;">
                                        <path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
                                    </svg>
                                    <p style="font-size:var(--fs-sm);color:var(--text-muted);line-height:1.7;">
                                        Pagarás el monto total en <strong>efectivo</strong> al recibir tu pedido en la dirección indicada.
                                        Ten el monto exacto listo para facilitar la entrega.
                                    </p>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- Botón submit oculto (activado desde el resumen con validación) -->
                    <button type="submit" id="btnConfirmar" style="display:none;">Confirmar</button>
                </form>

                <!-- Resumen lateral -->
                <div class="order-summary">
                    <h2 class="order-summary__title">Resumen del Pedido</h2>
                    <div style="display:flex;flex-direction:column;gap:var(--sp-4);margin-bottom:var(--sp-5);padding-bottom:var(--sp-5);border-bottom:1px solid var(--border-soft);">
                        <c:forEach var="item" items="${carrito}">
                            <div style="display:flex;gap:var(--sp-3);align-items:center;">
                                <div style="width:48px;height:60px;border-radius:var(--r-sm);flex-shrink:0;overflow:hidden;">
                                    <img src="${item.producto.imagenURL.startsWith('http') ? item.producto.imagenURL : pageContext.request.contextPath.concat('/').concat(item.producto.imagenURL)}"
                                         alt="${item.producto.nombre}"
                                         style="width:100%;height:100%;object-fit:cover;">
                                </div>
                                <div style="flex:1;">
                                    <p style="font-size:var(--fs-sm);font-weight:var(--fw-medium);"><c:out value="${item.producto.nombre}"/></p>
                                    <p style="font-size:var(--fs-xs);color:var(--text-muted);">×${item.cantidad}</p>
                                </div>
                                <p style="font-size:var(--fs-sm);font-weight:var(--fw-semibold);">
                                    $<fmt:formatNumber value="${item.precioUnidad * item.cantidad}" maxFractionDigits="0"/>
                                </p>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="summary-lines">
                        <div class="summary-line"><span>Subtotal</span><span>$<fmt:formatNumber value="${subtotal}" maxFractionDigits="0"/></span></div>
                        <div class="summary-line">
                            <span>Envío</span>
                            <c:choose>
                                <c:when test="${subtotal >= 1500}"><span class="summary-line--free">Gratis</span></c:when>
                                <c:otherwise><span>$<fmt:formatNumber value="${costoEnvio}" maxFractionDigits="0"/></span></c:otherwise>
                            </c:choose>
                        </div>
                        <div class="summary-line summary-line--total"><span>Total</span><span>$<fmt:formatNumber value="${total}" maxFractionDigits="0"/></span></div>
                    </div>
                    <button type="button" class="btn btn--gold btn--full btn--lg" onclick="confirmarCompra()">
                        Confirmar Compra
                    </button>
                    <p style="text-align:center;font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-3);">
                        Al confirmar aceptas nuestros Términos y Condiciones
                    </p>
                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="footer-bottom"><p>© 2025 Velour. Todos los derechos reservados.</p></div>
        </footer>

        <script type="module" src="${pageContext.request.contextPath}/assets/js/checkout.js"></script>

        <script>
            // ── Mostrar/ocultar campos según método de pago ──
            function mostrarPago(metodo) {
                ['TARJETA', 'TRANSFERENCIA', 'CONTRA_ENTREGA'].forEach(m => {
                    const fields = document.getElementById('fields-' + m);
                    const pm     = document.getElementById('pm-' + m);
                    if (fields) fields.style.display = m === metodo ? 'block' : 'none';
                    if (pm)     pm.classList.toggle('payment-method--active', m === metodo);
                });
            }

            // ── Formatear número de tarjeta (grupos de 4) ──
            function formatearTarjeta(input) {
                let val = input.value.replace(/\D/g, '').substring(0, 16);
                input.value = val.replace(/(.{4})/g, '$1 ').trim();
            }

            // ── Formatear vencimiento MM/AA ──
            function formatearVencimiento(input) {
                let val = input.value.replace(/\D/g, '').substring(0, 4);
                if (val.length >= 3) val = val.substring(0, 2) + '/' + val.substring(2);
                input.value = val;
            }

            // ── Validar tarjeta y confirmar compra ──
            function confirmarCompra() {
                const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value;

                if (metodo === 'TARJETA') {
                    let ok = true;

                    // Número — 16 dígitos
                    const numRaw = (document.getElementById('numTarjeta').value || '').replace(/\s/g, '');
                    const errNum = document.getElementById('errNumTarjeta');
                    if (!/^\d{16}$/.test(numRaw)) { errNum.style.display = 'block'; ok = false; }
                    else errNum.style.display = 'none';

                    // Vencimiento — MM/AA, fecha futura
                    const venc    = document.getElementById('vencimiento').value;
                    const errVenc = document.getElementById('errVencimiento');
                    const vencOk  = validarVencimiento(venc);
                    if (!vencOk) { errVenc.style.display = 'block'; ok = false; }
                    else errVenc.style.display = 'none';

                    // CVV — exactamente 3 dígitos
                    const cvvVal = document.getElementById('cvv').value;
                    const errCVV = document.getElementById('errCVV');
                    if (!/^\d{3}$/.test(cvvVal)) { errCVV.style.display = 'block'; ok = false; }
                    else errCVV.style.display = 'none';

                    if (!ok) return;
                }

                document.getElementById('btnConfirmar').click();
            }

            function validarVencimiento(venc) {
                if (!venc || !/^\d{2}\/\d{2}$/.test(venc)) return false;
                const [mm, aa] = venc.split('/').map(Number);
                if (mm < 1 || mm > 12) return false;
                const hoy    = new Date();
                const mesHoy = hoy.getMonth() + 1;       // 1-12
                const aaHoy  = hoy.getFullYear() % 100;  // últimos 2 dígitos
                if (aa < aaHoy) return false;
                if (aa === aaHoy && mm < mesHoy) return false;
                return true;
            }
        </script>
    </body>
</html>
