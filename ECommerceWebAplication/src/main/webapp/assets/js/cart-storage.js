// Carrito guest en localStorage. Cuando el usuario hace login,
// volcamos cada item al backend y limpiamos el localStorage.
//
// Estructura del item:
//   { productoId, nombre, imagenURL, precioUnidad, cantidad, talla }

import { apiPost } from './api.js';

const KEY = 'velour_cart_guest';
const ENVIO_GRATIS_DESDE = 1500;
const COSTO_ENVIO = 150;

function read() {
    try {
        const raw = localStorage.getItem(KEY);
        if (!raw) return [];
        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch {
        return [];
    }
}

function write(items) {
    localStorage.setItem(KEY, JSON.stringify(items));
}

function sameItem(a, b) {
    return a.productoId === b.productoId && (a.talla || null) === (b.talla || null);
}

export function obtener() {
    return read();
}

export function agregar({ productoId, nombre, imagenURL, precioUnidad, cantidad, talla }) {
    const items = read();
    const nuevo = {
        productoId: Number(productoId),
        nombre,
        imagenURL,
        precioUnidad: Number(precioUnidad),
        cantidad: Number(cantidad),
        talla: talla || null
    };
    const existente = items.find(it => sameItem(it, nuevo));
    if (existente) existente.cantidad += nuevo.cantidad;
    else items.push(nuevo);
    write(items);
    return items;
}

export function actualizarCantidad(productoId, talla, cantidad) {
    const items = read();
    const ref = { productoId: Number(productoId), talla: talla || null };
    const idx = items.findIndex(it => sameItem(it, ref));
    if (idx === -1) return items;
    if (cantidad <= 0) items.splice(idx, 1);
    else items[idx].cantidad = Number(cantidad);
    write(items);
    return items;
}

export function eliminar(productoId, talla) {
    const ref = { productoId: Number(productoId), talla: talla || null };
    const items = read().filter(it => !sameItem(it, ref));
    write(items);
    return items;
}

export function vaciar() {
    localStorage.removeItem(KEY);
}

export function totales(items = read()) {
    const cantidad = items.reduce((s, it) => s + (it.cantidad || 0), 0);
    const subtotal = items.reduce((s, it) => s + (it.precioUnidad * it.cantidad), 0);
    const envio = items.length === 0 ? 0 : (subtotal >= ENVIO_GRATIS_DESDE ? 0 : COSTO_ENVIO);
    return { cantidad, subtotal, envio, total: subtotal + envio };
}

export function contarItems() {
    return totales().cantidad;
}

/**
 * Vuelca el carrito guest al backend usando POST /api/carrito y limpia el local.
 * Se llama justo después de que el cliente inicia sesión.
 */
export async function volcarABackend() {
    const items = read();
    if (items.length === 0) return;
    for (const it of items) {
        try {
            await apiPost('/carrito', {
                productoId: it.productoId,
                cantidad: it.cantidad,
                talla: it.talla
            });
        } catch (e) {
            console.warn('No se pudo volcar item al backend:', it, e.message);
        }
    }
    vaciar();
}
