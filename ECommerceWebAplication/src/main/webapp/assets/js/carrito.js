import { apiPut, apiDelete } from './api.js';
import { isLogged } from './auth.js';
import { showError, showSuccess } from './ui.js';

if (isLogged()) {
    const items = document.querySelectorAll('.cart-item');

    items.forEach(itemEl => {
        const productoIdEl = itemEl.querySelector('input[name="productoId"]');
        const tallaEl = itemEl.querySelector('input[name="talla"]');
        if (!productoIdEl)
            return;
        const productoId = parseInt(productoIdEl.value, 10);
        const talla = tallaEl ? (tallaEl.value || null) : null;

        const formEliminar = itemEl.querySelector('form input[value="eliminar"]')?.form;
        if (formEliminar) {
            formEliminar.addEventListener('submit', async (e) => {
                e.preventDefault();
                if (!await confirmar('¿Eliminar este producto del carrito?'))
                    return;
                try {
                    let path = `/carrito/producto/${productoId}`;
                    if (talla)
                        path += `/talla/${encodeURIComponent(talla)}`;
                    await apiDelete(path);
                    showSuccess('Producto eliminado');
                    window.location.reload();
                } catch (err) {
                    showError(err.message || 'No se pudo eliminar.');
                }
            });
        }

        const formActualizar = itemEl.querySelector('form input[value="actualizar"]')?.form;
        if (formActualizar) {
            formActualizar.addEventListener('submit', async (e) => {
                e.preventDefault();
                const submitter = e.submitter;
                const nuevaCantidad = submitter
                        ? parseInt(submitter.value, 10)
                        : parseInt(formActualizar.querySelector('input[name="cantidad"]').value, 10);
                if (isNaN(nuevaCantidad) || nuevaCantidad < 0)
                    return;
                try {
                    await apiPut('/carrito', {productoId, cantidad: nuevaCantidad, talla});
                    window.location.reload();
                } catch (err) {
                    showError(err.message || 'No se pudo actualizar la cantidad.');
                }
            });
        }
    });

    document.querySelectorAll('form').forEach(form => {
        const accion = form.querySelector('input[name="accion"]');
        if (accion && accion.value === 'vaciar') {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                if (!await confirmar('¿Vaciar todo el carrito?'))
                    return;
                try {
                    await apiDelete('/carrito');
                    showSuccess('Carrito vaciado');
                    window.location.reload();
                } catch (err) {
                    showError(err.message || 'No se pudo vaciar el carrito.');
                }
            });
        }
    });
}

function confirmar(msg) {
    return Promise.resolve(window.confirm(msg));
}
