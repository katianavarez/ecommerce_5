// Detalle de producto consumiendo /api/productos/{id} y /api/resenas/producto/{id}
// vía Fetch API. Renderiza la página y monta los handlers de carrito y reseña.

import { apiGet, apiPost, ctxPath } from './api.js';
import { isLogged, getUserId } from './auth.js';
import { agregar as agregarAGuest } from './cart-storage.js';
import { showError, showSuccess, escapeHTML, formatMoney, resolveImg, getQueryParams } from './ui.js';

// Producto cacheado para que el handler del carrito pueda armar el item guest
// con todos los datos (nombre, imagen, precio) sin re-fetching.
let productoActual = null;

const meta = document.querySelector('meta[name="producto-id"]');
const productoId = meta ? parseInt(meta.content, 10) : 0;

if (!productoId) {
    mostrarError('Producto inválido.');
} else {
    cargar();
}

async function cargar() {
    try {
        const [producto, resenasData] = await Promise.all([
            apiGet(`/productos/${productoId}`, { auth: false }),
            apiGet(`/resenas/producto/${productoId}`, { auth: false })
        ]);
        renderProducto(producto);
        renderResenas(resenasData);
        revisarParams();
    } catch (err) {
        mostrarError(err.message || 'No se pudo cargar el producto.');
    }
}

function mostrarError(msg) {
    const loading = document.getElementById('loadingState');
    const errorEl = document.getElementById('errorState');
    const msgEl = document.getElementById('errorMessage');
    if (loading) loading.style.display = 'none';
    if (msgEl) msgEl.textContent = msg;
    if (errorEl) errorEl.style.display = 'block';
}

function revisarParams() {
    const params = getQueryParams();
    if (params.resenaOk) showSuccess('Tu reseña fue publicada correctamente. ¡Gracias!');
    if (params.resenaError) showError('No se pudo publicar la reseña.');
}

// Render del producto 

function renderProducto(p) {
    productoActual = p;
    document.title = `${p.nombre} — Velour`;
    document.getElementById('loadingState').style.display = 'none';
    document.getElementById('breadcrumbNombre').textContent = p.nombre;

    const mainImg = document.getElementById('mainImage');
    mainImg.src = resolveImg(p.imagenURL);
    mainImg.alt = p.nombre;
    mainImg.onerror = () => { mainImg.src = ctxPath('/assets/img/categoria/default.png'); mainImg.onerror = null; };

    document.getElementById('productInfo').innerHTML = buildInfoHTML(p);
    document.getElementById('detailLayout').style.display = '';
    document.getElementById('resenasTabs').style.display = '';

    montarHandlersTalla(p);
    montarHandlerCarrito(p);
}

function buildInfoHTML(p) {
    const tallasHTML = (p.tallasDisponibles && p.tallasDisponibles.length)
        ? buildTallasHTML(p)
        : '';

    const stockHTML = buildStockHTML(p);
    const formHTML = p.stock > 0 ? buildFormCarritoHTML(p) : '';
    const categoria = p.categoria ? escapeHTML(p.categoria.nombre) : '';

    return `
        <p class="product-info__category">${categoria}</p>
        <h1 class="product-info__name">${escapeHTML(p.nombre)}</h1>
        <div class="product-info__price">
            <span class="price-current">${formatMoney(p.precio)}</span>
        </div>
        <p class="product-info__shipping">
            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
            Envío gratis — llega en 3–5 días hábiles
        </p>
        <hr class="product-info__divider">
        <p style="font-size:var(--fs-sm);color:var(--text-muted);line-height:1.8;">
            ${escapeHTML(p.descripcion || '')}
        </p>
        ${tallasHTML}
        ${stockHTML}
        ${formHTML}
    `;
}

function buildTallasHTML(p) {
    const stockMap = {};
    (p.stockPorTalla || []).forEach(st => { stockMap[st.talla] = st.cantidad; });

    const opciones = p.tallasDisponibles.map(t => {
        const cantidad = stockMap[t] || 0;
        if (cantidad <= 0) {
            return `<button type="button" class="size-btn size-btn--disabled" disabled title="Sin stock">${escapeHTML(t)}</button>`;
        }
        return `<button type="button" class="size-btn" data-talla="${escapeHTML(t)}" data-stock="${cantidad}">${escapeHTML(t)}</button>`;
    }).join('');

    return `
        <div class="size-selector" style="margin-top:var(--sp-4);">
            <p class="size-selector__title">
                Talla: <strong id="tallaSeleccionadaLabel" style="color:var(--color-secondary);">—</strong>
                <span id="tallaHint" style="font-size:var(--fs-xs);color:var(--color-error);display:none;margin-left:8px;">
                    Selecciona una talla
                </span>
            </p>
            <div class="size-options">${opciones}</div>
        </div>
    `;
}

function buildStockHTML(p) {
    if (p.stock === 0) {
        return `<p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-error);">Agotado</p>`;
    }
    if (p.stock <= 5) {
        return `<p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-warning);">¡Solo ${p.stock} disponibles!</p>`;
    }
    return `<p style="margin-top:var(--sp-3);font-size:var(--fs-sm);color:var(--color-success, green);">En existencia</p>`;
}

function buildFormCarritoHTML(p) {
    const tieneTallas = p.tallasDisponibles && p.tallasDisponibles.length > 0;
    return `
        <form id="formCarrito" method="POST" action="${ctxPath('/app/carrito')}">
            <input type="hidden" name="accion" value="agregar">
            <input type="hidden" name="productoId" value="${p.id}">
            ${tieneTallas ? '<input type="hidden" name="talla" id="tallaInput" value="">' : ''}
            <div class="product-actions" style="margin-top:var(--sp-5);">
                <div class="qty-control">
                    <button type="button" class="qty-btn" id="qtyMinus">−</button>
                    <input class="qty-input" type="number" name="cantidad" value="1"
                           min="1" max="${p.stock}" id="qtyInput" readonly>
                    <button type="button" class="qty-btn" id="qtyPlus">+</button>
                </div>
                <button type="submit" class="btn btn--gold btn--lg" style="flex:1;">
                    Agregar al Carrito
                </button>
            </div>
        </form>
    `;
}

// Handlers 

function montarHandlersTalla(p) {
    const stockMap = {};
    (p.stockPorTalla || []).forEach(st => { stockMap[st.talla] = st.cantidad; });

    document.querySelectorAll('.size-btn:not(.size-btn--disabled)').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.size-btn:not(.size-btn--disabled)')
                .forEach(b => b.classList.remove('size-btn--active'));
            btn.classList.add('size-btn--active');

            const talla = btn.dataset.talla;
            const tallaInput = document.getElementById('tallaInput');
            if (tallaInput) tallaInput.value = talla;
            const label = document.getElementById('tallaSeleccionadaLabel');
            if (label) label.textContent = talla;
            const hint = document.getElementById('tallaHint');
            if (hint) hint.style.display = 'none';

            const qty = document.getElementById('qtyInput');
            const stockTalla = stockMap[talla];
            if (qty && stockTalla !== undefined) {
                qty.max = stockTalla;
                if (parseInt(qty.value, 10) > stockTalla) qty.value = stockTalla;
            }
        });
    });

    const minus = document.getElementById('qtyMinus');
    const plus = document.getElementById('qtyPlus');
    const qty = document.getElementById('qtyInput');
    if (minus && qty) minus.addEventListener('click', () => {
        const v = parseInt(qty.value, 10) - 1;
        if (v >= 1) qty.value = v;
    });
    if (plus && qty) plus.addEventListener('click', () => {
        const v = parseInt(qty.value, 10) + 1;
        if (v <= parseInt(qty.max, 10)) qty.value = v;
    });
}

function montarHandlerCarrito(p) {
    const form = document.getElementById('formCarrito');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const tallaInput = document.getElementById('tallaInput');
        if (tallaInput && !tallaInput.value) {
            const hint = document.getElementById('tallaHint');
            if (hint) hint.style.display = 'inline';
            return;
        }

        const cantidad = parseInt(document.getElementById('qtyInput').value, 10) || 1;
        const talla = tallaInput ? (tallaInput.value || null) : null;

        const btn = form.querySelector('button[type="submit"]');
        const orig = btn ? btn.textContent : '';
        if (btn) { btn.disabled = true; btn.textContent = 'Agregando...'; }

        try {
            if (isLogged()) {
                // Cliente autenticado → backend vía API REST.
                await apiPost('/carrito', { productoId: p.id, cantidad, talla });
            } else {
                // Guest → carrito en localStorage; al login se sincroniza con volcarABackend().
                agregarAGuest({
                    productoId: p.id,
                    nombre: p.nombre,
                    imagenURL: p.imagenURL,
                    precioUnidad: p.precio,
                    cantidad,
                    talla
                });
            }
            actualizarBadge(cantidad);
            showSuccess('Producto agregado al carrito');
        } catch (err) {
            showError(err.message || 'No se pudo agregar al carrito.');
        } finally {
            if (btn) { btn.disabled = false; btn.textContent = orig; }
        }
    });
}

function actualizarBadge(delta) {
    const badge = document.querySelector('.cart-badge');
    if (badge) {
        badge.textContent = String((parseInt(badge.textContent, 10) || 0) + delta);
        return;
    }
    const cartLink = document.querySelector('a[aria-label="Carrito"]');
    if (cartLink) {
        const span = document.createElement('span');
        span.className = 'cart-badge';
        span.textContent = String(delta);
        cartLink.appendChild(span);
    }
}

// Render de reseñas 

function renderResenas(data) {
    const tabBtn = document.querySelector('.tab-btn[data-tab="resenas"]');
    document.getElementById('totalResenasCount').textContent = data.total || 0;

    const container = document.getElementById('resenasContainer');
    const lista = data.resenas || [];

    if (lista.length === 0) {
        container.innerHTML = `<p style="color:var(--text-muted);margin-bottom:var(--sp-6);">Aún no hay reseñas para este producto.</p>`;
    } else {
        // Si el usuario está logueado, separamos sus reseñas de las de otros.
        const miId = getUserId();
        const propias = miId ? lista.filter(r => r.usuarioId === miId) : [];
        const otras = miId ? lista.filter(r => r.usuarioId !== miId) : lista;

        let html = '';
        if (propias.length) {
            html += `<p style="font-size:var(--fs-xs);letter-spacing:0.1em;text-transform:uppercase;color:var(--color-secondary);font-weight:var(--fw-medium);margin-bottom:var(--sp-3);">
                Tus reseñas</p>`;
            html += `<div class="reviews-list" style="margin-bottom:var(--sp-6);">` +
                propias.map(r => reviewCardHTML(r, true)).join('') +
                `</div>`;
        }
        if (otras.length) {
            if (propias.length) {
                html += `<hr style="margin-bottom:var(--sp-5);border:none;border-top:1px solid var(--border-soft);">`;
                html += `<p style="font-size:var(--fs-xs);letter-spacing:0.1em;text-transform:uppercase;color:var(--text-muted);font-weight:var(--fw-medium);margin-bottom:var(--sp-3);">
                    Reseñas recientes</p>`;
            }
            html += `<div class="reviews-list" style="margin-bottom:var(--sp-6);">` +
                otras.map(r => reviewCardHTML(r, false)).join('') +
                `</div>`;
        }
        container.innerHTML = html;
    }

    renderFormResena();
}

function reviewCardHTML(r, propia) {
    const stars = '★'.repeat(r.calificacion || 0);
    const borderStyle = propia ? 'border-left:2px solid var(--color-secondary);' : '';
    return `
        <div class="review-card" style="${borderStyle}">
            <div class="review-card__header">
                <div>
                    <p class="review-author">${escapeHTML(r.usuarioNombre || 'Anónimo')}</p>
                    <div class="stars">${stars}</div>
                </div>
                <p class="review-date">${escapeHTML(r.fecha || '')}</p>
            </div>
            <p class="review-text">${escapeHTML(r.comentario || '')}</p>
        </div>
    `;
}

function renderFormResena() {
    const container = document.getElementById('formResenaContainer');
    if (!container) return;

    if (!isLogged()) {
        container.innerHTML = `
            <div style="padding:var(--sp-5);background:var(--bg-surface);border:1px solid var(--border-soft);border-radius:var(--r-md);text-align:center;">
                <p style="color:var(--text-muted);font-size:var(--fs-sm);margin-bottom:var(--sp-3);">
                    Inicia sesión para dejar una reseña.
                </p>
                <a href="${ctxPath('/auth/login?redirect=/app/producto%3Fid=' + productoId)}" class="btn btn--primary btn--sm">Iniciar sesión</a>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div style="max-width:540px;">
            <h4 style="font-size:var(--fs-md);font-weight:var(--fw-medium);margin-bottom:var(--sp-4);">
                Escribe tu reseña
            </h4>
            <form id="formResena">
                <div class="form-group" style="margin-bottom:var(--sp-4);">
                    <label class="form-label">Calificación *</label>
                    <div id="starRating" style="display:flex;gap:4px;font-size:1.8rem;cursor:pointer;margin-top:4px;">
                        <span class="star" data-val="1" style="color:var(--border-medium);">★</span>
                        <span class="star" data-val="2" style="color:var(--border-medium);">★</span>
                        <span class="star" data-val="3" style="color:var(--border-medium);">★</span>
                        <span class="star" data-val="4" style="color:var(--border-medium);">★</span>
                        <span class="star" data-val="5" style="color:var(--border-medium);">★</span>
                    </div>
                    <input type="hidden" name="calificacion" id="calificacionInput" value="">
                    <p id="starHint" style="font-size:var(--fs-xs);color:var(--color-error);display:none;margin-top:4px;">Selecciona una calificación.</p>
                </div>
                <div class="form-group" style="margin-bottom:var(--sp-4);">
                    <label class="form-label" for="comentario">Comentario *</label>
                    <textarea class="form-control" id="comentario" name="comentario"
                              rows="4" placeholder="Cuéntanos tu experiencia con este producto..."
                              style="resize:vertical;" required></textarea>
                </div>
                <button type="submit" class="btn btn--primary" id="btnResena">Publicar reseña</button>
            </form>
        </div>
    `;

    montarHandlersResena();
}

function montarHandlersResena() {
    const stars = document.querySelectorAll('.star');
    const calInput = document.getElementById('calificacionInput');
    const starHint = document.getElementById('starHint');

    stars.forEach(star => {
        star.addEventListener('mouseover', () => pintarEstrellas(star.dataset.val));
        star.addEventListener('mouseout', () => pintarEstrellas(calInput.value || 0));
        star.addEventListener('click', () => {
            calInput.value = star.dataset.val;
            pintarEstrellas(star.dataset.val);
            if (starHint) starHint.style.display = 'none';
        });
    });

    const formResena = document.getElementById('formResena');
    formResena.addEventListener('submit', async (e) => {
        e.preventDefault();
        const calificacion = parseInt(calInput.value || '0', 10);
        const comentario = (document.getElementById('comentario').value || '').trim();

        if (!calificacion || calificacion < 1 || calificacion > 5) {
            if (starHint) starHint.style.display = 'block';
            return;
        }
        if (!comentario) { showError('El comentario es requerido.'); return; }

        const btn = document.getElementById('btnResena');
        const orig = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Publicando...';

        try {
            await apiPost('/resenas', { productoId, calificacion, comentario });
            showSuccess('Tu reseña fue publicada correctamente. ¡Gracias!');
            // Recargar la lista de reseñas
            const data = await apiGet(`/resenas/producto/${productoId}`, { auth: false });
            renderResenas(data);
        } catch (err) {
            showError(err.message || 'No se pudo publicar la reseña.');
            btn.disabled = false;
            btn.textContent = orig;
        }
    });
}

function pintarEstrellas(valor) {
    document.querySelectorAll('.star').forEach(s => {
        s.style.color = s.dataset.val <= valor
            ? 'var(--color-secondary, #C9A96E)'
            : 'var(--border-medium, #ccc)';
    });
}
