import { apiPost, ctxPath } from './api.js';
import { setSession } from './auth.js';
import { volcarABackend } from './cart-storage.js';
import { showError } from './ui.js';

const form = document.getElementById('registroForm');

if (form) {
    const errorBox = document.getElementById('registroError');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        ocultarError();

        const get = (n) => (form.elements[n]?.value || '').trim();
        const nombre       = get('nombre');
        const correo       = get('correo');
        const telefono     = get('telefono');
        const calle        = get('calle');
        const ciudad       = get('ciudad');
        const estado       = get('estado');
        const codigoPostal = get('codigoPostal');
        const contrasena   = form.elements['contrasena']?.value || '';

        if (!nombre || !correo || !contrasena
                || !calle || !ciudad || !estado || !codigoPostal) {
            mostrarError('Todos los campos marcados con * son requeridos.');
            return;
        }
        if (contrasena.length < 8) {
            mostrarError('La contraseña debe tener al menos 8 caracteres.');
            return;
        }

        const btn = form.querySelector('button[type="submit"]');
        const original = btn ? btn.textContent : '';
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Creando cuenta...';
        }

        try {
            const data = await apiPost('/auth/registro', {
                nombre, correo, contrasena, telefono,
                calle, ciudad, estado, codigoPostal
            }, { auth: false });

            setSession({
                token: data.token,
                nombre: data.nombre,
                rol: data.rol,
                usuarioId: data.usuarioId
            });

            try { await volcarABackend(); } catch (_) { /* no fatal */ }

            window.location.assign(ctxPath('/app/productos'));
        } catch (err) {
            if (btn) {
                btn.disabled = false;
                btn.textContent = original;
            }
            mostrarError(err.message || 'No se pudo crear la cuenta.');
        }
    });

    function mostrarError(msg) {
        if (errorBox) {
            errorBox.textContent = msg;
            errorBox.style.display = 'block';
        } else {
            showError(msg);
        }
    }
    function ocultarError() {
        if (errorBox) {
            errorBox.textContent = '';
            errorBox.style.display = 'none';
        }
    }
}
