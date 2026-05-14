import { apiPut } from './api.js';
import { getUserId, getToken, getRol, setSession } from './auth.js';
import { showError, showSuccess } from './ui.js';

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

            // Mantener token y rol previos; actualizar nombre/usuarioId locales.
            setSession({
                token: getToken(),
                nombre: updated.nombre,
                rol: getRol(),
                usuarioId: updated.id
            });

            // Refrescar UI sin recarga.
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
