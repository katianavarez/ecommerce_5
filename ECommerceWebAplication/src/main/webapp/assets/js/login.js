import { apiPost, ctxPath } from './api.js';
import { setSession, isLogged } from './auth.js';
import { volcarABackend } from './cart-storage.js';
import { showError } from './ui.js';

const form = document.getElementById('loginForm');

if (form) {
    if (isLogged()) {
        window.location.replace(ctxPath('/app/productos'));
    }

    const errorBox = document.getElementById('loginError');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        ocultarError();

        const correo = (form.correo.value || '').trim();
        const contrasena = form.contrasena.value || '';

        if (!correo || !contrasena) {
            mostrarError('Correo y contraseña son requeridos.');
            return;
        }

        const btn = form.querySelector('button[type="submit"]');
        const original = btn ? btn.textContent : '';
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Iniciando sesión...';
        }

        try {
            const data = await apiPost('/auth/login',
                    {correo, contrasena},
                    {auth: false});

            setSession({
                token: data.token,
                nombre: data.nombre,
                rol: data.rol,
                usuarioId: data.usuarioId
            });

            try {
                await volcarABackend();
            } catch (_) {
            }

            const params = new URLSearchParams(window.location.search);
            const redirect = params.get('redirect');
            const ctx = ctxPath();
            let dest;
            if (redirect && redirect.length > 0) {
                const decoded = redirect.replace(/%3F/gi, '?');
                dest = decoded.startsWith(ctx) ? decoded : ctx + decoded;
            } else {
                dest = ctxPath('/app/productos');
            }
            window.location.assign(dest);

        } catch (err) {
            if (btn) {
                btn.disabled = false;
                btn.textContent = original;
            }
            mostrarError(err.message || 'No se pudo iniciar sesión.');
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
