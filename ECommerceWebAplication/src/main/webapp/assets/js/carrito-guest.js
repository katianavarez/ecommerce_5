// Render del carrito para usuarios NO autenticados.
//
// Para guests, el JSP renderiza siempre "carrito vacío" desde el servidor
// (porque la sesión Java está vacía). Aquí leemos cart-storage.js (localStorage)
// y, si tiene items, reescribimos el contenido del .cart-layout con la lista
// real y un resumen calculado client-side.
//
// Las mutaciones (-/+/eliminar/vaciar) tocan solo localStorage y vuelven a
// renderizar. Cuando el guest haga login, login.js llama volcarABackend() y
// estos items se persisten en BD.

import { obtener, actualizarCantidad, eliminar, vaciar, totales } from './cart-storage.js';
import { isLogged } from './auth.js';
import { escapeHTML, resolveImg, formatMoney, showSuccess } from './ui.js';
import { ctxPath } from './api.js';

if (!isLogged()) {
    renderGuest();
}

function renderGuest() {
    const items = obtener();
    const layout = document.querySelector('.cart-layout');
    if (!layout) return;

    if (items.length === 0) {
        // Dejar el render server-side de "carrito vacío".
        return;
    }

    const t = totales(items);
    const itemsHTML = items.map(it => `
        <div class="cart-item" data-id="${it.productoId}" data-talla="${escapeHTML(it.talla || '')}">
            <a href="${ctxPath('/app/producto?id=' + it.productoId)}" class="cart-item__img">
                <img src="${escapeHTML(resolveImg(it.imagenURL))}" alt="${escapeHTML(it.nombre)}">
            </a>
            <div class="cart-item__info">
                <h3 class="cart-item__name">${escapeHTML(it.nombre)}</h3>
                ${it.talla ? `<p style="font-size:var(--fs-xs);color:var(--text-muted);margin-top:2px;">Talla: <strong>${escapeHTML(it.talla)}</strong></p>` : ''}
                <p class="cart-item__price">${formatMoney(it.precioUnidad)}</p>
            </div>
            <div class="cart-item__actions">
                <div class="qty-control">
                    <button class="qty-btn" data-action="menos" ${it.cantidad <= 1 ? 'disabled title="Usa Eliminar para quitar este artículo"' : ''}>−</button>
                    <input class="qty-input" type="number" value="${it.cantidad}" readonly>
                    <button class="qty-btn" data-action="mas">+</button>
                </div>
                <button class="cart-item__remove" data-action="eliminar">Eliminar</button>
            </div>
        </div>
    `).join('');

    const envioHTML = t.envio === 0
        ? '<span class="summary-line--free">Gratis</span>'
        : `<span>${formatMoney(t.envio)}</span>`;

    layout.innerHTML = `
        <div>
            <h1 class="cart-title">Tu Carrito (${t.cantidad})</h1>
            <div class="cart-items">${itemsHTML}</div>
            <div style="display:flex;gap:var(--sp-3);margin-top:var(--sp-6);">
                <a href="${ctxPath('/app/productos')}" class="btn btn--ghost btn--sm">← Seguir comprando</a>
                <button class="btn btn--ghost btn--sm" data-action="vaciar" style="color:var(--color-error);">Vaciar carrito</button>
            </div>
        </div>
        <div class="order-summary">
            <h2 class="order-summary__title">Resumen del pedido</h2>
            <div class="summary-lines">
                <div class="summary-line">
                    <span>Subtotal (${t.cantidad} artículos)</span>
                    <span>${formatMoney(t.subtotal)}</span>
                </div>
                <div class="summary-line">
                    <span>Envío</span>
                    ${envioHTML}
                </div>
                <div class="summary-line summary-line--total">
                    <span>Total</span>
                    <span>${formatMoney(t.total)}</span>
                </div>
            </div>
            <a href="${ctxPath('/app/checkout')}" class="btn btn--gold btn--full btn--lg">Proceder al Pago</a>
            <p style="text-align:center;font-size:var(--fs-xs);color:var(--text-muted);margin-top:var(--sp-4);">
                Inicia sesión para finalizar la compra
            </p>
        </div>
    `;

    bindHandlers(layout);
}

function bindHandlers(layout) {
    layout.querySelectorAll('.cart-item').forEach(itemEl => {
        const productoId = parseInt(itemEl.dataset.id, 10);
        const talla = itemEl.dataset.talla || null;
        const cantidadActual = parseInt(itemEl.querySelector('.qty-input').value, 10);

        itemEl.querySelector('[data-action="menos"]')?.addEventListener('click', () => {
            if (cantidadActual <= 1) return;
            actualizarCantidad(productoId, talla, cantidadActual - 1);
            renderGuest();
        });
        itemEl.querySelector('[data-action="mas"]')?.addEventListener('click', () => {
            actualizarCantidad(productoId, talla, cantidadActual + 1);
            renderGuest();
        });
        itemEl.querySelector('[data-action="eliminar"]')?.addEventListener('click', () => {
            if (!window.confirm('¿Eliminar este producto del carrito?')) return;
            eliminar(productoId, talla);
            showSuccess('Producto eliminado');
            renderGuest();
        });
    });

    layout.querySelector('[data-action="vaciar"]')?.addEventListener('click', () => {
        if (!window.confirm('¿Vaciar todo el carrito?')) return;
        vaciar();
        showSuccess('Carrito vaciado');
        renderGuest();
    });
}
