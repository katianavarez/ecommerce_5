import { apiPost } from './api.js';
import { isLogged } from './auth.js';
import { agregar as agregarGuest } from './cart-storage.js';
import { showError, showSuccess } from './ui.js';

const form = document.getElementById('formCarrito');

if (form) {
    const productoIdEl = form.querySelector('input[name="productoId"]');
    const productoId = productoIdEl ? parseInt(productoIdEl.value, 10) : 0;
    const cantidadInput = form.querySelector('#qtyInput');
    const tallaInput = form.querySelector('#tallaInput');
    const tallaHint = document.getElementById('tallaHint');

    const nombre = form.dataset.productoNombre || '';
    const imagenURL = form.dataset.productoImagen || '';
    const precioUnidad = parseFloat(form.dataset.productoPrecio || '0');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (tallaInput && !tallaInput.value) {
            if (tallaHint)
                tallaHint.style.display = 'inline';
            return;
        }

        const cantidad = parseInt(cantidadInput ? cantidadInput.value : '1', 10) || 1;
        const talla = tallaInput ? (tallaInput.value || null) : null;

        const btn = form.querySelector('button[type="submit"]');
        const original = btn ? btn.textContent : '';
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Agregando...';
        }

        try {
            if (isLogged()) {
                await apiPost('/carrito', {productoId, cantidad, talla});
            } else {
                agregarGuest({productoId, nombre, imagenURL, precioUnidad, cantidad, talla});
            }
            actualizarBadge(cantidad);
            showSuccess('Producto agregado al carrito');
        } catch (err) {
            showError(err.message || 'No se pudo agregar al carrito.');
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = original;
            }
        }
    });
}

function actualizarBadge(delta) {
    const badge = document.querySelector('.cart-badge');
    if (badge) {
        const current = parseInt(badge.textContent, 10) || 0;
        badge.textContent = String(current + delta);
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

const formResena = document.getElementById('formResena');

if (formResena) {
    const productoId = parseInt(formResena.dataset.productoId || '0', 10);
    const calificacionInput = formResena.querySelector('#calificacionInput');
    const comentarioInput = formResena.querySelector('#comentario');
    const starHint = document.getElementById('starHint');

    formResena.addEventListener('submit', async (e) => {
        e.preventDefault();

        const calificacion = parseInt(calificacionInput ? calificacionInput.value : '0', 10);
        const comentario = (comentarioInput ? comentarioInput.value : '').trim();

        if (!calificacion || calificacion < 1 || calificacion > 5) {
            if (starHint)
                starHint.style.display = 'block';
            return;
        }
        if (!comentario) {
            showError('El comentario es requerido.');
            return;
        }

        const btn = formResena.querySelector('button[type="submit"]');
        const original = btn ? btn.textContent : '';
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Publicando...';
        }

        try {
            await apiPost('/resenas', {productoId, calificacion, comentario});
            const url = new URL(window.location.href);
            url.searchParams.set('resenaOk', '1');
            window.location.assign(url.toString());
        } catch (err) {
            if (btn) {
                btn.disabled = false;
                btn.textContent = original;
            }
            showError(err.message || 'No se pudo publicar la reseña.');
        }
    });
}
