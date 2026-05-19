package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ResenaDAO;
import itson.ecommercewebaplication.models.Resenia;
import java.util.List;

/**
 * Lógica de negocio de reseñas. Valida las reseñas antes de guardarlas
 * (calificación entre 1 y 5, comentario y referencias obligatorias) y
 * calcula el promedio de un producto para mostrarlo en su ficha.
 * 
 * La regla de "una reseña por usuario y producto" y la de "solo reseña
 * quien compró" se aplican en el servlet de la API, que consulta los
 * conteos que expone esta clase antes de invocar {@link #crear(Resenia)}.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class ResenaBO {

    private final ResenaDAO resenaDAO = new ResenaDAO();

    public List<Resenia> obtenerTodas() {
        return resenaDAO.obtenerTodas();
    }

    public Resenia obtenerPorId(int id) {
        return resenaDAO.obtenerPorId(id);
    }

    public List<Resenia> obtenerPorProducto(int productoId) {
        return resenaDAO.obtenerPorProducto(productoId);
    }

    public List<Resenia> obtenerPorUsuario(int usuarioId) {
        return resenaDAO.obtenerPorUsuario(usuarioId);
    }

    /**
     * Valida y guarda una reseña. Exige calificación entre 1 y 5, comentario
     * no vacío y que vengan tanto el usuario como el producto.
     *
     * @throws Exception si la calificación está fuera de rango o falta algún dato
     */
    public Resenia crear(Resenia resena) throws Exception {
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new Exception("La calificación debe estar entre 1 y 5");
        }
        if (resena.getComentario() == null || resena.getComentario().isBlank()) {
            throw new Exception("El comentario es requerido");
        }
        if (resena.getUsuario() == null) {
            throw new Exception("El usuario es requerido");
        }
        if (resena.getProducto() == null) {
            throw new Exception("El producto es requerido");
        }
        return resenaDAO.guardar(resena);
    }

    public void eliminar(int id) throws Exception {
        if (resenaDAO.obtenerPorId(id) == null) {
            throw new Exception("Resenia no encontrada");
        }
        resenaDAO.eliminar(id);
    }

    /** Promedio de calificaciones de un producto, o 0 si aún no tiene reseñas. */
    public double calcularPromedio(int productoId) {
        List<Resenia> resenas = resenaDAO.obtenerPorProducto(productoId);
        if (resenas.isEmpty()) {
            return 0.0;
        }
        return resenas.stream().mapToInt(Resenia::getCalificacion).average().orElse(0.0);
    }

    public long contarResenasPorUsuarioYProducto(int usuarioId, int productoId) {
        return resenaDAO.contarResenasPorUsuarioYProducto(usuarioId, productoId);
    }

    public List<Resenia> obtenerRecientesPorProducto(int productoId, int usuarioId, int limite) {
        return resenaDAO.obtenerRecientesPorProducto(productoId, usuarioId, limite);
    }

    public List<Resenia> obtenerDelUsuarioPorProducto(int usuarioId, int productoId) {
        return resenaDAO.obtenerDelUsuarioPorProducto(usuarioId, productoId);
    }

    public long contarPorProducto(int productoId) {
        return resenaDAO.contarPorProducto(productoId);
    }
}
