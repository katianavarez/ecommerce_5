package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ResenaDAO;
import itson.ecommercewebaplication.models.Resenia;
import java.util.List;

/**
 *
 * @author PC
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
