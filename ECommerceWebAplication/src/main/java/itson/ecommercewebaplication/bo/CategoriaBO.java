package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.CategoriaDAO;
import itson.ecommercewebaplication.models.Categoria;
import java.util.List;

/**
 *
 * @author PC
 */
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
            throw new Exception("El nombre de la categoría es requerido");
        }
        return categoriaDAO.guardar(categoria);
    }

    public Categoria actualizar(Categoria categoria) throws Exception {
        if (categoriaDAO.obtenerPorId(categoria.getId()) == null) {
            throw new Exception("Categoría no encontrada");
        }
        return categoriaDAO.actualizar(categoria);
    }

    public void eliminar(int id) throws Exception {
        if (categoriaDAO.obtenerPorId(id) == null) {
            throw new Exception("Categoría no encontrada");
        }
        categoriaDAO.eliminar(id);
    }

    public java.util.Map<Integer, Long> contarProductosPorCategoria() {
        return categoriaDAO.contarProductosPorCategoria();
    }
}
