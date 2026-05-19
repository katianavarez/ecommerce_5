import { apiGet, apiPut, apiDelete, ctxPath } from './api.js';
import { getUserId, getToken, getRol, setSession } from './auth.js';
import { showError, showSuccess, escapeHTML, formatMoney, resolveImg } from './ui.js';

const formPerfil     = document.getElementById('formPerfil');
const formContrasena = document.getElementById('formContrasena');

if (formPerfil) {
    formPerfil.addEventListener('submit', async (e) => {
        e.preventDefault();
        const userId = getUserId();
        if (!userId) { showError('Sesión inválida. Inicia sesión de nuevo.'); return; }

        const nombre   = (formPerfil.elements['nombre']?.value || '').trim();
        const telefono = (formPerfil.elements['telefono']?.value || '').trim();

        if (!nombre) { showError('El nombre es requerido.'); return; }

        const btn = formPerfil.querySelector('button[type="submit"]');
        const orig = btn ? btn.textContent : '';
        if (btn) { btn.disabled = true; btn.textContent = 'Guardando...'; }

        try {
            const updated = await apiPut(`/usuarios/${userId}`, { nombre, telefono });

            setSession({
                token: getToken(),
                nombre: updated.nombre,
                rol: getRol(),
                usuarioId: updated.id
            });

            const nombreEl = document.querySelector('.account-sidebar__name');
            if (nombreEl) nombreEl.textContent = updated.nombre;
            const avatar = document.querySelector('.account-sidebar__avatar');
            if (avatar) avatar.textContent = (updated.nombre || '?').substring(0, 1).toUpperCase();

            showSuccess('Perfil actualizado.');
        } catch (err) {
            showError(err.message || 'No se pudo actualizar el perfil.');
        } finally {
            if (btn) { btn.disabled = false; btn.textContent = orig; }
        }
    });
}

// Contraseña (PUT /api/usuarios/{id}/password) 
if (formContrasena) {
    formContrasena.addEventListener('submit', async (e) => {
        e.preventDefault();
        const userId = getUserId();
        if (!userId) { showError('Sesión inválida. Inicia sesión de nuevo.'); return; }

        const actual    = formContrasena.elements['contrasenaActual']?.value || '';
        const nueva     = formContrasena.elements['nuevaContrasena']?.value || '';
        const confirmar = formContrasena.elements['confirmarContrasena']?.value || '';

        if (nueva.length < 8) { showError('La nueva contraseña debe tener al menos 8 caracteres.'); return; }
        if (nueva !== confirmar) { showError('Las contraseñas no coinciden.'); return; }

        const btn = formContrasena.querySelector('button[type="submit"]');
        const orig = btn ? btn.textContent : '';
        if (btn) { btn.disabled = true; btn.textContent = 'Actualizando...'; }

        try {
            await apiPut(`/usuarios/${userId}/password`, { actual, nueva, confirmar });
            formContrasena.reset();
            showSuccess('Contraseña actualizada correctamente.');
        } catch (err) {
            showError(err.message || 'No se pudo cambiar la contraseña.');
        } finally {
            if (btn) { btn.disabled = false; btn.textContent = orig; }
        }
    });
}

// Historial de pedidos (GET /api/pedidos/usuario/{id}) 
const PEDIDOS_POR_PAGINA = 5;
let pedidosCache = [];
let paginaActual = 1;

cargarPedidos();

async function cargarPedidos() {
    // Preferimos localStorage (cliente que llegó por la API REST);
    // fallback al meta tag inyectado por el servlet (clienteLogueado de sesión).
    let userId = getUserId();
    if (!userId) {
        const meta = document.querySelector('meta[name="usuario-id"]');
        if (meta && meta.content) userId = parseInt(meta.content, 10);
    }

    const loading = document.getElementById('pedidosLoading');
    const empty   = document.getElementById('pedidosEmpty');
    const error   = document.getElementById('pedidosError');
    const content = document.getElementById('pedidosContent');

    if (!userId) {
        if (loading) loading.style.display = 'none';
        if (error)   error.style.display = 'block';
        return;
    }

    try {
        const data = await apiGet(`/pedidos/usuario/${userId}`);
        pedidosCache = (data && data.pedidos) ? data.pedidos : [];

        if (loading) loading.style.display = 'none';

        if (pedidosCache.length === 0) {
            if (empty) empty.style.display = 'block';
            return;
        }

        if (content) content.style.display = 'block';
        renderPagina(1);
    } catch (err) {
        if (loading) loading.style.display = 'none';
        if (error) error.style.display = 'block';
        console.error('Error al cargar pedidos:', err);
    }
}

function renderPagina(pagina) {
    paginaActual = pagina;
    const total = pedidosCache.length;
    const totalPaginas = Math.max(1, Math.ceil(total / PEDIDOS_POR_PAGINA));
    if (paginaActual < 1) paginaActual = 1;
    if (paginaActual > totalPaginas) paginaActual = totalPaginas;

    const desde = (paginaActual - 1) * PEDIDOS_POR_PAGINA;
    const hasta = Math.min(desde + PEDIDOS_POR_PAGINA, total);

    const list = document.getElementById('pedidosList');
    list.innerHTML = pedidosCache.slice(desde, hasta).map(orderCardHTML).join('');

    list.querySelectorAll('button[data-cancel-id]').forEach(btn => {
        btn.addEventListener('click', () => cancelarPedido(parseInt(btn.dataset.cancelId, 10), btn));
    });

    renderPaginacion(total, totalPaginas, desde, hasta);
}

function orderCardHTML(p) {
    const estadoClase = (p.estado || '').toLowerCase();
    const puedeCancel = p.estado === 'PENDIENTE';
    const thumbs = (p.thumbnails || []).map(t => `
        <div class="order-thumb">
            <img src="${escapeHTML(resolveImg(t.imagenURL))}"
                 alt="${escapeHTML(t.nombre || '')}"
                 style="width:100%;height:100%;object-fit:cover;"
                 onerror="this.src='${ctxPath('/assets/img/categoria/default.png')}';this.onerror=null;">
        </div>
    `).join('');

    const btnCancelar = puedeCancel
        ? `<button class="btn btn--outline btn--sm" data-cancel-id="${p.id}" style="margin-right:var(--sp-2);">Cancelar</button>`
        : '';

    return `
        <div class="order-card">
            <div class="order-card__header">
                <div>
                    <p class="order-card__id">#${escapeHTML(p.numPedido || '')}</p>
                    <p class="order-card__date">${escapeHTML(p.fecha || '')}</p>
                </div>
                <span class="badge badge--${estadoClase}">${escapeHTML(p.estado || '')}</span>
            </div>
            <div class="order-card__items">${thumbs}</div>
            <div class="order-card__footer">
                <div>
                    <p style="font-size:var(--fs-xs);color:var(--text-muted);">${p.numItems || 0} artículo(s)</p>
                    <p class="order-total">${formatMoney(p.total)}</p>
                </div>
                <div style="display:flex;align-items:center;gap:var(--sp-2);">
                    ${btnCancelar}
                    <a href="${ctxPath('/app/confirmacion?pedidoId=' + p.id)}" class="btn btn--ghost btn--sm">Ver detalle</a>
                </div>
            </div>
        </div>
    `;
}

async function cancelarPedido(pedidoId, btn) {
    if (!confirm('¿Seguro que quieres cancelar este pedido? Esta acción no se puede deshacer.')) return;
    const orig = btn ? btn.textContent : '';
    if (btn) { btn.disabled = true; btn.textContent = 'Cancelando...'; }
    try {
        await apiDelete(`/pedidos/${pedidoId}`);
        showSuccess('Pedido cancelado correctamente.');
        // Recargar la lista de pedidos para reflejar el nuevo estado.
        await cargarPedidos();
    } catch (err) {
        showError(err.message || 'No se pudo cancelar el pedido.');
        if (btn) { btn.disabled = false; btn.textContent = orig; }
    }
}

function renderPaginacion(total, totalPaginas, desde, hasta) {
    const cont = document.getElementById('pedidosPaginacion');
    if (!cont) return;

    let html = `
        <p style="font-size:var(--fs-xs);color:var(--text-muted);white-space:nowrap;">
            Mostrando <strong>${desde + 1}–${hasta}</strong> de <strong>${total}</strong> pedidos
        </p>
    `;

    if (totalPaginas > 1) {
        let nav = `<nav class="pagination" style="margin-top:0;flex-shrink:0;">`;
        if (paginaActual > 1) nav += `<button class="pagination__btn" data-page="${paginaActual - 1}">‹</button>`;
        for (let i = 1; i <= totalPaginas; i++) {
            if (i === paginaActual) {
                nav += `<button class="pagination__btn pagination__btn--active">${i}</button>`;
            } else if (i === 1 || i === totalPaginas || (i >= paginaActual - 1 && i <= paginaActual + 1)) {
                nav += `<button class="pagination__btn" data-page="${i}">${i}</button>`;
            } else if (i === paginaActual - 2 || i === paginaActual + 2) {
                nav += `<span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">…</span>`;
            }
        }
        if (paginaActual < totalPaginas) nav += `<button class="pagination__btn" data-page="${paginaActual + 1}">›</button>`;
        nav += `</nav>`;
        html += nav;
    }

    cont.innerHTML = html;

    cont.querySelectorAll('button[data-page]').forEach(btn => {
        btn.addEventListener('click', () => renderPagina(parseInt(btn.dataset.page, 10)));
    });
}
