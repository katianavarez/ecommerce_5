// Sesión del cliente en localStorage. El servidor sigue siendo la fuente de verdad
// (cada request manda Bearer y el filtro re-valida); aquí guardamos lo mínimo
// para personalizar la UI y poder pintar el header sin esperar respuesta de API.

const KEY_TOKEN  = 'velour_token';
const KEY_NOMBRE = 'velour_nombre';
const KEY_ROL    = 'velour_rol';
const KEY_ID     = 'velour_uid'; // se llena en el primer GET de carrito/pedidos

export function getToken()  { return localStorage.getItem(KEY_TOKEN); }
export function getNombre() { return localStorage.getItem(KEY_NOMBRE); }
export function getRol()    { return localStorage.getItem(KEY_ROL); }
export function getUserId() {
    const v = localStorage.getItem(KEY_ID);
    return v ? parseInt(v, 10) : null;
}

export function isLogged() { return !!getToken(); }

export function setSession({ token, nombre, rol, usuarioId }) {
    if (token)  localStorage.setItem(KEY_TOKEN,  token);
    if (nombre) localStorage.setItem(KEY_NOMBRE, nombre);
    if (rol)    localStorage.setItem(KEY_ROL,    rol);
    if (usuarioId != null) localStorage.setItem(KEY_ID, String(usuarioId));
}

export function setUserId(id) {
    if (id != null) localStorage.setItem(KEY_ID, String(id));
}

export function clearSession() {
    localStorage.removeItem(KEY_TOKEN);
    localStorage.removeItem(KEY_NOMBRE);
    localStorage.removeItem(KEY_ROL);
    localStorage.removeItem(KEY_ID);
}

/**
 * Decodifica el payload de un JWT sin validar firma (la firma la valida el backend).
 * Útil para sacar el correo (sub) y el rol sin esperar a una request.
 */
export function decodeToken(token = getToken()) {
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    try {
        const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        const padded = payload + '='.repeat((4 - payload.length % 4) % 4);
        return JSON.parse(atob(padded));
    } catch {
        return null;
    }
}

export function isTokenExpired(token = getToken()) {
    const payload = decodeToken(token);
    if (!payload || !payload.exp) return true;
    return Date.now() >= payload.exp * 1000;
}
