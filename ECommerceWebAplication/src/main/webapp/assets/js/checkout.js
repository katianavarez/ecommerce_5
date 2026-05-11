import { apiPost, ctxPath } from './api.js';
import { showError } from './ui.js';

const form = document.getElementById('checkoutForm');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (typeof window.validarPagoFrontend === 'function' && !window.validarPagoFrontend()) {
            return;
        }

        const calle = form.querySelector('input[name="calle"]').value.trim();
        const ciudad = form.querySelector('input[name="ciudad"]').value.trim();
        const estado = form.querySelector('input[name="estado"]').value.trim();
        const codigoPostal = form.querySelector('input[name="codigoPostal"]').value.trim();
        const metodoPago = (form.querySelector('input[name="metodoPago"]:checked') || {}).value;

        if (!calle || !ciudad || !estado || !codigoPostal || !metodoPago) {
            showError('Faltan datos: revisa dirección y método de pago.');
            return;
        }

        const confirmBtn = document.querySelector('.order-summary .btn--gold');
        const originalText = confirmBtn ? confirmBtn.textContent : '';
        if (confirmBtn) {
            confirmBtn.disabled = true;
            confirmBtn.textContent = 'Procesando...';
        }

        try {
            const pedido = await apiPost('/pedidos', {
                calle, ciudad, estado, codigoPostal, metodoPago
            });
            const id = pedido && pedido.id != null ? pedido.id : '';
            window.location.assign(`${ctxPath('/app/confirmacion')}?pedidoId=${id}`);
        } catch (err) {
            if (confirmBtn) {
                confirmBtn.disabled = false;
                confirmBtn.textContent = originalText;
            }
            showError(err.message || 'No se pudo procesar el pedido.');
        }
    });
}
