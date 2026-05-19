package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.CategoriaDAO;
import itson.ecommercewebaplication.models.Categoria;
import java.util.List;

/**
 * Lógica de negocio de categorías. Valida nombre obligatorio y único,
 * recorta el largo máximo, y protege contra borrar una categoría que aún
 * tenga productos asociados (lo que dejaría productos huérfanos).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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

    /**
     * Elimina una categoría solo si no tiene productos asociados. Si los
     * tiene, lanza excepción pidiendo reasignarlos primero, para no romper
     * las FK de los productos.
     *
     * @throws Exception si la categoría no existe o aún tiene productos
     */
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
