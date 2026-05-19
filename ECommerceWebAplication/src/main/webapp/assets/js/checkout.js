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

        // Cuerpo base + datos de tarjeta cuando aplica. El backend valida server-side
        // (PedidosApiServlet) para que un POST directo no salte la validación del front.
        const body = { calle, ciudad, estado, codigoPostal, metodoPago };
        if (metodoPago === 'TARJETA') {
            const numTarjeta = (document.getElementById('numTarjeta')?.value || '').replace(/\s/g, '');
            const vencimiento = document.getElementById('vencimiento')?.value || '';
            const cvv = document.getElementById('cvv')?.value || '';
            body.numTarjeta = numTarjeta;
            body.vencimiento = vencimiento;
            body.cvv = cvv;
        }

        const confirmBtn = document.querySelector('.order-summary .btn--gold');
        const originalText = confirmBtn ? confirmBtn.textContent : '';
        if (confirmBtn) {
            confirmBtn.disabled = true;
            confirmBtn.textContent = 'Procesando...';
        }

        try {
            const pedido = await apiPost('/pedidos', body);
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
