// Wrapper sobre fetch que añade Authorization desde localStorage,
// parsea JSON y propaga errores con el message del backend.
//
// El backend responde { success, message, data } o el data directo en endpoints viejos;
// aquí siempre devolvemos lo que el caller necesite (data) y lanzamos Error si !ok.

import { getToken, clearSession } from './auth.js';

const BASE = (() => {
    // contextPath: /ECommerceWebAplication o vací´o si está en la raíz
    const path = window.location.pathname;
    const ctx = path.split('/')[1] || '';
    return ctx ? '/' + ctx : '';
})();

export const API_BASE = BASE + '/api';

async function request(method, path, { body, query, auth = true } = {}) {
    const url = new URL(API_BASE + path, window.location.origin);
    if (query) {
        Object.entries(query).forEach(([k, v]) => {
            if (v === undefined || v === null || v === '') return;
            if (Array.isArray(v)) v.forEach(x => url.searchParams.append(k, x));
            else url.searchParams.append(k, v);
        });
    }

    const headers = {};
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    if (auth) {
        const token = getToken();
        if (token) headers['Authorization'] = 'Bearer ' + token;
    }

    const res = await fetch(url.toString(), {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined
    });

    // 401 - solo limpiar sesión y mostrar "Sesión expirada" si HABÍA token previo
    // (token vencido o cuenta deshabilitada). Si no hay token, era un intento de
    // login fallido: dejamos que el flujo siga al manejo normal de errores para
    // que el caller (login.js) vea el mensaje real del backend ("Credenciales inválidas").
    if (res.status === 401 && getToken()) {
        clearSession();
        const err = new Error('Sesión expirada. Inicia sesión de nuevo.');
        err.status = 401;
        throw err;
    }

    // Sin contenido (204) o body vacío.
    const text = await res.text();
    let data = null;
    if (text) {
        try { data = JSON.parse(text); }
        catch { data = text; }
    }

    if (!res.ok) {
        const msg = (data && data.message) ? data.message : `Error ${res.status}`;
        const err = new Error(msg);
        err.status = res.status;
        err.data = data;
        throw err;
    }

    // Convención del backend: { success, message, data } - devolver data.
    if (data && typeof data === 'object' && 'success' in data && 'data' in data) {
        return data.data;
    }
    return data;
}

export const apiGet    = (path, opts) => request('GET',    path, opts);
export const apiPost   = (path, body, opts) => request('POST',   path, { ...opts, body });
export const apiPut    = (path, body, opts) => request('PUT',    path, { ...opts, body });
export const apiPatch  = (path, body, opts) => request('PATCH',  path, { ...opts, body });
export const apiDelete = (path, opts) => request('DELETE', path, opts);

export function ctxPath(suffix = '') {
    return BASE + suffix;
}
