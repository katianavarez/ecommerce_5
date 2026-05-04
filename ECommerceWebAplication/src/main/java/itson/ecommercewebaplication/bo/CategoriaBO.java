package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.CategoriaDAO;
import itson.ecommercewebaplication.models.Categoria;
import java.util.List;

public class CategoriaBO {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }

    public Categoria obtenerPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }

    public Categoria crear(Categoria categoria) throws Exception {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new Exception("El nombre de la categoría es requerido.");
        }
        String nombre = categoria.getNombre().trim();
        if (nombre.length() > 100) {
            throw new Exception("El nombre no puede tener más de 100 caracteres.");
        }
        if (categoriaDAO.obtenerPorNombre(nombre) != null) {
            throw new Exception("Ya existe una categoría con ese nombre.");
        }
        categoria.setNombre(nombre);
        return categoriaDAO.guardar(categoria);
    }

    public Categoria actualizar(Categoria categoria) throws Exception {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new Exception("El nombre de la categoría es requerido.");
        }
        Categoria existente = categoriaDAO.obtenerPorId(categoria.getId());
        if (existente == null) {
            throw new Exception("Categoría no encontrada.");
        }
        String nombre = categoria.getNombre().trim();
        if (nombre.length() > 100) {
            throw new Exception("El nombre no puede tener más de 100 caracteres.");
        }
        Categoria conMismoNombre = categoriaDAO.obtenerPorNombre(nombre);
        if (conMismoNombre != null && conMismoNombre.getId() != categoria.getId()) {
            throw new Exception("Ya existe otra categoría con ese nombre.");
        }
        categoria.setNombre(nombre);
        return categoriaDAO.actualizar(categoria);
    }

    public void eliminar(int id) throws Exception {
        Categoria existente = categoriaDAO.obtenerPorId(id);
        if (existente == null) {
            throw new Exception("Categoría no encontrada.");
        }
        long productosVinculados = categoriaDAO.contarProductos(id);
        if (productosVinculados > 0) {
            throw new Exception("No se puede eliminar: la categoría tiene "
                    + productosVinculados + " producto(s) asociado(s). "
                    + "Reasigna o elimina esos productos primero.");
        }
        categoriaDAO.eliminar(id);
    }

    public java.util.Map<Integer, Long> contarProductosPorCategoria() {
        return categoriaDAO.contarProductosPorCategoria();
    }
}
