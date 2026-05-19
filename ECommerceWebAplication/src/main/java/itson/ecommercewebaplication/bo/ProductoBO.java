package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ProductoDAO;
import itson.ecommercewebaplication.models.Producto;
import java.util.List;

/**
 * Lógica de negocio del catálogo. Centraliza las validaciones al crear y
 * actualizar productos (nombre no vacío, precio positivo, stock no negativo,
 * categoría obligatoria) y ofrece las consultas que consume tanto el panel
 * admin como la API del catálogo del cliente.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class ProductoBO {

    private final ProductoDAO productoDAO = new ProductoDAO();

    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    public List<Producto> obtenerTodosParaAdmin() {
        return productoDAO.obtenerTodosParaAdmin();
    }

    public List<Producto> obtenerPaginados(int p, int t) {
        return productoDAO.obtenerPaginados(p, t);
    }

    public long contarProductos() {
        return productoDAO.contarProductos();
    }

    public Producto obtenerPorId(int id) {
        return productoDAO.obtenerPorId(id);
    }

    public List<Producto> obtenerPorCategoria(int cId) {
        return productoDAO.obtenerPorCategoria(cId);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }

    public double obtenerPrecioMaximo() {
        return productoDAO.obtenerPrecioMaximo();
    }

    public List<Producto> filtrar(List<Integer> categorias, List<String> tallas,
            String color, Double precioMin, Double precioMax,
            boolean soloConStock) {
        return productoDAO.filtrar(categorias, tallas, color, precioMin, precioMax, soloConStock);
    }

    public List<Producto> obtenerPorIds(List<Integer> ids) {
        return productoDAO.obtenerPorIds(ids);
    }

    /**
     * Valida y guarda un producto nuevo. Rechaza nombre vacío, precio menor
     * o igual a cero, stock negativo o categoría faltante.
     *
     * @throws Exception si algún campo no cumple las reglas del catálogo
     */
    public Producto crear(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new Exception("El nombre del producto es requerido.");
        }
        if (producto.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a cero.");
        }
        if (producto.getStock() < 0) {
            throw new Exception("El stock no puede ser negativo.");
        }
        if (producto.getCategoria() == null) {
            throw new Exception("La categoría es requerida.");
        }
        return productoDAO.guardar(producto);
    }

    public Producto actualizar(Producto producto) throws Exception {
        if (productoDAO.obtenerPorId(producto.getId()) == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (producto.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a cero.");
        }
        return productoDAO.actualizar(producto);
    }

    public void eliminar(int id) throws Exception {
        if (productoDAO.obtenerPorId(id) == null) {
            throw new Exception("Producto no encontrado.");
        }
        productoDAO.eliminar(id);
    }

    public void reactivar(int id) throws Exception {
        productoDAO.reactivar(id);
    }

    public boolean verificarStock(int productoId, int cantidad) {
        Producto p = productoDAO.obtenerPorId(productoId);
        return p != null && p.getStock() >= cantidad;
    }

    /**
     * Descuenta unidades del stock tras verificar que haya suficientes.
     *
     * @throws Exception si no hay stock suficiente para la cantidad pedida
     */
    public void reducirStock(int productoId, int cantidad) throws Exception {
        if (!verificarStock(productoId, cantidad)) {
            throw new Exception("Stock insuficiente.");
        }
        productoDAO.actualizarStock(productoId, -cantidad);
    }

    public void restaurarStock(int productoId, int cantidad) {
        productoDAO.actualizarStock(productoId, cantidad);
    }
}
