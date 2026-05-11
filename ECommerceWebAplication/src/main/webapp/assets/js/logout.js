import { apiPost, ctxPath } from './api.js';
import { clearSession } from './auth.js';
import { vaciar } from './cart-storage.js';

const links = document.querySelectorAll('[data-action="logout"]');

links.forEach(el => {
    el.addEventListener('click', async (e) => {
        e.preventDefault();
        try {
            await apiPost('/auth/logout', {}, {auth: false});
        } catch (_) {
        }
        clearSession();
        vaciar();
        window.location.assign(ctxPath('/auth/login'));
    });
});
