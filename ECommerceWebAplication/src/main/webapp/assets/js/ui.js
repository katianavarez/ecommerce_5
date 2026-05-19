// Helpers de UI compartidos por todas las páginas.

/** Escapa un string para insertarlo seguro como texto/atributo dentro de innerHTML. */
export function escapeHTML(s) {
    if (s === null || s === undefined) return '';
    return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/** Formatea número como moneda MXN sin decimales. */
export function formatMoney(n) {
    const value = Number(n) || 0;
    return '$' + Math.round(value).toLocaleString('es-MX');
}

/**
 * Inserta un mensaje (success | error | info) en el contenedor con id="flash"
 * o lo crea al inicio del <main> si no existe. Auto-desaparece a los 4s.
 */
export function flash(message, kind = 'info') {
    let host = document.getElementById('flash');
    if (!host) {
        host = document.createElement('div');
        host.id = 'flash';
        host.style.cssText = 'position:fixed;top:90px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:8px;max-width:360px;';
        document.body.appendChild(host);
    }
    const colors = {
        success: { bg: '#d4edda', border: '#c3e6cb', fg: '#155724', icon: '✓' },
        error:   { bg: '#f8d7da', border: '#f5c6cb', fg: '#721c24', icon: '⚠' },
        info:    { bg: '#d1ecf1', border: '#bee5eb', fg: '#0c5460', icon: 'ℹ' }
    };
    const c = colors[kind] || colors.info;
    const div = document.createElement('div');
    div.style.cssText = `background:${c.bg};border:1px solid ${c.border};color:${c.fg};padding:12px 16px;border-radius:6px;font-size:14px;box-shadow:0 4px 12px rgba(0,0,0,0.08);transition:opacity 0.3s;`;
    div.innerHTML = `${c.icon} ${escapeHTML(message)}`;
    host.appendChild(div);
    setTimeout(() => {
        div.style.opacity = '0';
        setTimeout(() => div.remove(), 300);
    }, 4000);
}

export const showError   = (msg) => flash(msg, 'error');
export const showSuccess = (msg) => flash(msg, 'success');
export const showInfo    = (msg) => flash(msg, 'info');

/**
 * Resuelve la URL de imagen del backend: si es absoluta, la devuelve;
 * si es relativa, le antepone el contextPath.
 */
export function resolveImg(url) {
    if (!url) return '';
    if (/^https?:\/\//.test(url)) return url;
    const path = window.location.pathname;
    const ctx = path.split('/')[1] || '';
    return ctx ? `/${ctx}/${url}` : '/' + url;
}

/** Lee parámetros de la URL como objeto plano. */
export function getQueryParams() {
    const params = {};
    new URL(window.location.href).searchParams.forEach((v, k) => {
        if (params[k] === undefined) params[k] = v;
        else params[k] = [].concat(params[k], v);
    });
    return params;
}

/** Reemplaza la URL preservando estado del navegador, sin recargar. */
export function setQueryParams(params) {
    const url = new URL(window.location.href);
    url.search = '';
    Object.entries(params).forEach(([k, v]) => {
        if (v === undefined || v === null || v === '') return;
        if (Array.isArray(v)) v.forEach(x => url.searchParams.append(k, x));
        else url.searchParams.set(k, v);
    });
    window.history.replaceState({}, '', url.toString());
}
