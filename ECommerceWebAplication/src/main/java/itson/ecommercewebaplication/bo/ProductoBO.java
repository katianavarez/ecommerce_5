package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ProductoDAO;
import itson.ecommercewebaplication.models.Producto;
import java.util.List;

/**
 *
 * @author PC
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
