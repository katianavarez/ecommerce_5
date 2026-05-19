// Sincroniza el badge del carrito en el header cuando el usuario NO está logueado.
//
// Las JSPs renderizan el badge desde sessionScope.carrito (sesión Java), pero el
// guest mantiene su carrito en localStorage (cart-storage.js), así que sin este
// módulo el badge siempre marcaría 0 al navegar entre páginas. Aquí leemos el
// cart-storage y actualizamos el DOM al cargar la página.

import { isLogged } from './auth.js';
import { contarItems } from './cart-storage.js';

if (!isLogged()) {
    sincronizarBadgeGuest();
}

function sincronizarBadgeGuest() {
    const count = contarItems();
    const cartLink = document.querySelector('a[aria-label="Carrito"]');
    if (!cartLink) return;

    let badge = cartLink.querySelector('.cart-badge');
    if (count === 0) {
        if (badge) badge.remove();
        return;
    }

    if (badge) {
        badge.textContent = String(count);
    } else {
        badge = document.createElement('span');
        badge.className = 'cart-badge';
        badge.textContent = String(count);
        cartLink.appendChild(badge);
    }
}
