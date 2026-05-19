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

// Auto-hidratación desde el JSP 
// Si el usuario inició sesión por el servlet web (no por la API), el servlet
// pobla la sesión Java y deja el JWT en un <meta name="jwt-token">. Aquí lo
// trasladamos a localStorage para que el resto del JS pueda consumir la API
// con Authorization: Bearer.
(function bootstrapFromMeta() {
    try {
        if (getToken()) return; // ya hay token en localStorage
        const tokMeta = document.querySelector('meta[name="jwt-token"]');
        if (!tokMeta || !tokMeta.content) return;
        const get = (n) => document.querySelector(`meta[name="${n}"]`)?.content || null;
        const id = parseInt(get('jwt-usuario-id') || '0', 10);
        setSession({
            token: tokMeta.content,
            nombre: get('jwt-nombre'),
            rol: get('jwt-rol'),
            usuarioId: id > 0 ? id : null
        });
    } catch {
        // Sin DOM disponible o storage bloqueado — silenciamos: la API simplemente fallará 401.
    }
})();
