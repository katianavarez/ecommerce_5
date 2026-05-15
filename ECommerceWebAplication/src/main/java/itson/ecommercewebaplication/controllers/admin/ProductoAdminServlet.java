package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.bo.ProductoBO;
import itson.ecommercewebaplication.bo.ProveedorBO;
import itson.ecommercewebaplication.enums.Colores;
import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.Proveedor;
import itson.ecommercewebaplication.models.StockTalla;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import itson.ecommercewebaplication.util.PaginacionUtil;

/**
 *
 * @author PC
 */
@WebServlet(name = "ProductoAdminServlet", urlPatterns = {"/admin/productos"})
public class ProductoAdminServlet extends HttpServlet {

    private ProductoBO productoBO;
    private CategoriaBO categoriaBO;
    private ProveedorBO proveedorBO;

    @Override
    public void init() throws ServletException {
        productoBO = new ProductoBO();
        categoriaBO = new CategoriaBO();
        proveedorBO = new ProveedorBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                mostrarFormNuevo(req, res);
            } else if ("editar".equals(accion)) {
                mostrarFormEditar(req, res);
            } else if ("eliminar".equals(accion)) {
                eliminar(req, res);
            } else if ("reactivar".equals(accion)) {
                reactivar(req, res);
            } else {
                listar(req, res);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo completar la operación. " + e.getMessage());
            listar(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("crear".equals(accion)) {
                crear(req, res);
            } else if ("actualizar".equals(accion)) {
                actualizar(req, res);
            } else if ("eliminar".equals(accion)) {
                try {
                    eliminar(req, res);
                } catch (Exception ex) {
                    req.setAttribute("error", ex.getMessage());
                    listar(req, res);
                }
            } else if ("reactivar".equals(accion)) {
                reactivar(req, res);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo guardar el producto. " + e.getMessage());
            cargarFormAtributos(req);
            req.getRequestDispatcher("/views/admin/admin-producto-form.jsp").forward(req, res);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String busqueda = req.getParameter("busqueda");
        String categoriaStr = req.getParameter("categoriaId");
        String orden = req.getParameter("orden");
        if (orden == null || orden.isBlank()) {
            orden = "reciente";
        }

        List<Producto> productos;

        // Obtener base según búsqueda o categoría
        if (busqueda != null && !busqueda.isBlank()) {
            productos = productoBO.buscarPorNombre(busqueda);
        } else if (categoriaStr != null && !categoriaStr.isBlank() && !categoriaStr.equals("TODAS")) {
            try {
                int catId = Integer.parseInt(categoriaStr);
                productos = productoBO.obtenerPorCategoria(catId);
            } catch (NumberFormatException e) {
                productos = productoBO.obtenerTodosParaAdmin();
            }
        } else {
            productos = productoBO.obtenerTodosParaAdmin();
        }

        switch (orden) {
            case "mayorPrecio":
                productos = productos.stream()
                        .sorted((a, b) -> Double.compare(b.getPrecio(), a.getPrecio()))
                        .collect(Collectors.toList());
                break;
            case "menorPrecio":
                productos = productos.stream()
                        .sorted((a, b) -> Double.compare(a.getPrecio(), b.getPrecio()))
                        .collect(Collectors.toList());
                break;
            case "menorStock":
                productos = productos.stream()
                        .sorted((a, b) -> Integer.compare(a.getStock(), b.getStock()))
                        .collect(Collectors.toList());
                break;
            default: 
                break;
        }

        PaginacionUtil.paginar(req, productos, "productos", PaginacionUtil.TAMANO_ADMIN);
        req.setAttribute("categorias", categoriaBO.obtenerTodas());
        req.setAttribute("categoriaSeleccionada", categoriaStr != null ? categoriaStr : "TODAS");
        req.setAttribute("busquedaActual", busqueda != null ? busqueda : "");
        req.setAttribute("ordenActual", orden);
        req.getRequestDispatcher("/views/admin/admin-productos.jsp").forward(req, res);
    }

    private void mostrarFormNuevo(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        cargarFormAtributos(req);
        req.getRequestDispatcher("/views/admin/admin-producto-form.jsp").forward(req, res);
    }

    private void mostrarFormEditar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("producto", productoBO.obtenerPorId(id));
        cargarFormAtributos(req);
        req.getRequestDispatcher("/views/admin/admin-producto-form.jsp").forward(req, res);
    }

    private void cargarFormAtributos(HttpServletRequest req) {
        req.setAttribute("categorias", categoriaBO.obtenerTodas());
        req.setAttribute("proveedores", proveedorBO.obtenerActivos());
        req.setAttribute("tallas", Tallas.values());
        req.setAttribute("colores", Colores.values());
    }

    private void crear(HttpServletRequest req, HttpServletResponse res) throws Exception {
        productoBO.crear(buildProductoFromRequest(req));
        res.sendRedirect(req.getContextPath() + "/admin/productos?success=created");
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse res) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Producto p = buildProductoFromRequest(req);
        p.setId(id);
        Producto existente = productoBO.obtenerPorId(id);
        if (existente != null) {
            p.setActivo(existente.isActivo());
        }
        productoBO.actualizar(p);
        res.sendRedirect(req.getContextPath() + "/admin/productos?success=updated");
    }

    private void reactivar(HttpServletRequest req, HttpServletResponse res) throws Exception {
        productoBO.reactivar(Integer.parseInt(req.getParameter("id")));
        res.sendRedirect(req.getContextPath() + "/admin/productos?success=reactivated");
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse res) throws Exception {
        productoBO.eliminar(Integer.parseInt(req.getParameter("id")));
        res.sendRedirect(req.getContextPath() + "/admin/productos?success=deleted");
    }

    private Producto buildProductoFromRequest(HttpServletRequest req) {
        String nombre = req.getParameter("nombre");
        String descripcion = req.getParameter("descripcion");
        double precio = Double.parseDouble(req.getParameter("precio"));
        String imagenURL = req.getParameter("imagenURL");
        int categoriaId = Integer.parseInt(req.getParameter("categoriaId"));

        String colorStr = req.getParameter("color");
        String colorNombre = null;
        if (colorStr != null && !colorStr.isBlank()) {
            try {
                Colores.valueOf(colorStr);
                colorNombre = colorStr;
            } catch (Exception ignored) {
            }
        }

        List<StockTalla> stockPorTalla = new ArrayList<>();
        List<Tallas> tallasDisp = new ArrayList<>();
        int totalStock = 0;

        for (Tallas talla : Tallas.values()) {
            String param = req.getParameter("stock_" + talla.name());
            int cantidad = 0;
            if (param != null && !param.isBlank()) {
                try {
                    cantidad = Integer.parseInt(param);
                } catch (NumberFormatException ignored) {
                }
            }
            if (cantidad > 0) {
                stockPorTalla.add(new StockTalla(talla, cantidad));
                tallasDisp.add(talla);
                totalStock += cantidad;
            }
        }

        if (stockPorTalla.isEmpty()) {
            String stockParam = req.getParameter("stock");
            if (stockParam != null && !stockParam.isBlank()) {
                try {
                    totalStock = Integer.parseInt(stockParam);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        Categoria categoria = categoriaBO.obtenerPorId(categoriaId);

        Proveedor proveedor = null;
        String proveedorStr = req.getParameter("proveedorId");
        if (proveedorStr != null && !proveedorStr.isBlank()) {
            try {
                proveedor = proveedorBO.obtenerPorId(Integer.parseInt(proveedorStr));
            } catch (NumberFormatException ignored) {}
        }

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setImagenURL(imagenURL);
        p.setStock(totalStock);
        p.setColor(colorNombre);
        p.setCategoria(categoria);
        p.setProveedor(proveedor);
        p.setTallasDisponibles(tallasDisp);
        p.setStockPorTalla(stockPorTalla.isEmpty() ? null : stockPorTalla);
        return p;
    }
}
