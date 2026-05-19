import { apiGet, ctxPath } from './api.js';
import { showError, formatMoney, escapeHTML, resolveImg } from './ui.js';

const grid = document.querySelector('.products-grid');
const formFiltros = document.getElementById('formFiltros');
const formBusqueda = document.querySelector('.search-form');
const titulo = document.querySelector('.shop-title');
const contadorMostrando = document.querySelector('.shop-main p[style*="font-size:var(--fs-xs)"]');
const ctx = ctxPath();
const PAGE_SIZE = 6;

let paginaActual = 1;

function leerFiltros() {
    const params = new URLSearchParams();
    if (formBusqueda) {
        const inp = formBusqueda.querySelector('input[name="busqueda"]');
        if (inp && inp.value.trim())
            params.append('busqueda', inp.value.trim());
    }
    if (formFiltros) {
        formFiltros.querySelectorAll('input[name="categoriaId"]:checked').forEach(cb => {
            params.append('categoriaId', cb.value);
        });
        formFiltros.querySelectorAll('input[name="talla"]:checked').forEach(cb => {
            params.append('talla', cb.value);
        });
        const color = formFiltros.querySelector('input[name="color"]');
        if (color && color.value)
            params.append('color', color.value);
        const precio = formFiltros.querySelector('input[name="precioMax"]');
        if (precio && precio.value)
            params.append('precioMax', precio.value);
        const stock = formFiltros.querySelector('input[name="soloConStock"]');
        if (stock && stock.checked)
            params.append('soloConStock', 'true');
    }
    params.append('page', String(paginaActual));
    params.append('size', String(PAGE_SIZE));
    return params;
}

async function aplicarFiltrosFetch() {
    if (!grid)
        return;

    const params = leerFiltros();
    const query = {};
    for (const [k, v] of params) {
        if (query[k] === undefined)
            query[k] = v;
        else
            query[k] = [].concat(query[k], v);
    }

    grid.style.opacity = '0.5';

    try {
        const data = await apiGet('/productos', {query, auth: false});
        renderizarGrid(data.productos || []);
        actualizarTitulo(query.busqueda);
        actualizarContador(data.total || 0, data.page || 1, data.size || PAGE_SIZE);
        renderPaginacion(data.totalPaginas || 1, data.page || 1);
        actualizarUrl(params);
    } catch (err) {
        showError(err.message || 'No se pudo cargar el catálogo.');
    } finally {
        grid.style.opacity = '1';
    }
}

function actualizarContador(total, page, size) {
    if (!contadorMostrando) return;
    if (total === 0) {
        contadorMostrando.innerHTML = '';
        return;
    }
    const desde = (page - 1) * size + 1;
    const hasta = Math.min(page * size, total);
    contadorMostrando.innerHTML =
        `Mostrando <strong>${desde}–${hasta}</strong> de <strong>${total}</strong> productos`;
}

function renderPaginacion(totalPaginas, paginaActualNum) {
    const nav = document.querySelector('nav.pagination');
    if (!nav) return;

    if (totalPaginas <= 1) {
        nav.style.display = 'none';
        return;
    }
    nav.style.display = '';

    const partes = [];
    if (paginaActualNum > 1) {
        partes.push(`<button type="button" class="pagination__btn" data-page="${paginaActualNum - 1}">‹</button>`);
    }
    for (let i = 1; i <= totalPaginas; i++) {
        if (i === paginaActualNum) {
            partes.push(`<a class="pagination__btn pagination__btn--active" href="#">${i}</a>`);
        } else if (i === 1 || i === totalPaginas || (i >= paginaActualNum - 1 && i <= paginaActualNum + 1)) {
            partes.push(`<button type="button" class="pagination__btn" data-page="${i}">${i}</button>`);
        } else if (i === paginaActualNum - 2 || i === paginaActualNum + 2) {
            partes.push(`<span style="display:inline-flex;align-items:center;justify-content:center;width:2.25rem;height:2.25rem;font-size:var(--fs-sm);color:var(--text-muted);">…</span>`);
        }
    }
    if (paginaActualNum < totalPaginas) {
        partes.push(`<button type="button" class="pagination__btn" data-page="${paginaActualNum + 1}">›</button>`);
    }
    nav.innerHTML = partes.join('');

    nav.querySelectorAll('button[data-page]').forEach(btn => {
        btn.addEventListener('click', () => {
            paginaActual = parseInt(btn.dataset.page, 10);
            aplicarFiltrosFetch();
        });
    });
}

function renderizarGrid(productos) {
    if (!grid)
        return;
    if (productos.length === 0) {
        grid.innerHTML = `
            <div style="grid-column:1/-1;text-align:center;padding:var(--sp-12);color:var(--text-muted);">
                <p style="font-size:var(--fs-lg);">No se encontraron productos</p>
            </div>`;
        return;
    }
    grid.innerHTML = productos.map(p => {
        const img = resolveImg(p.imagenURL);
        const cat = p.categoria || '';
        return `
            <a href="${ctx}/app/producto?id=${p.id}" class="product-card" style="text-decoration:none;">
                <div class="product-card__img-wrap">
                    <img src="${escapeHTML(img)}" alt="${escapeHTML(p.nombre)}">
                </div>
                <div class="product-card__body">
                    <p class="product-card__cat">${escapeHTML(cat)}</p>
                    <h3 class="product-card__name">${escapeHTML(p.nombre)}</h3>
                    <div class="product-card__price" style="margin-top:var(--sp-2);">
                        <span class="product-card__price-current">${formatMoney(p.precio)}</span>
                    </div>
                </div>
            </a>`;
    }).join('');
}

function actualizarTitulo(busqueda) {
    if (!titulo)
        return;
    titulo.textContent = busqueda ? `Resultados para: "${busqueda}"` : 'Toda la Colección';
}

function actualizarUrl(params) {
    const url = new URL(window.location.href);
    url.search = params.toString();
    window.history.replaceState({}, '', url.toString());
}

window.aplicarFiltros = aplicarFiltrosFetch;

// Sobreescribir irAPagina (definida inline en productos.jsp para fallback no-JS)
// para que use Fetch en lugar de submit del form de paginación.
window.irAPagina = function (num) {
    paginaActual = num;
    aplicarFiltrosFetch();
};

if (formBusqueda) {
    formBusqueda.addEventListener('submit', (e) => {
        e.preventDefault();
        paginaActual = 1;
        aplicarFiltrosFetch();
    });
}
if (formFiltros) {
    formFiltros.addEventListener('submit', (e) => {
        e.preventDefault();
        paginaActual = 1;
        aplicarFiltrosFetch();
    });
}
