package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Resenia;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author PC
 */
public class ResenaDAO {

    public List<Resenia> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Resenia r LEFT JOIN FETCH r.usuario LEFT JOIN FETCH r.producto ORDER BY r.fecha DESC",
                    Resenia.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Resenia obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Resenia.class, id);
        } finally {
            em.close();
        }
    }

    public List<Resenia> obtenerPorProducto(int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resenia> q = em.createQuery(
                    "SELECT r FROM Resenia r LEFT JOIN FETCH r.usuario WHERE r.producto.id = :pId ORDER BY r.fecha DESC",
                    Resenia.class);
            q.setParameter("pId", productoId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Resenia> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resenia> q = em.createQuery(
                    "SELECT r FROM Resenia r LEFT JOIN FETCH r.producto WHERE r.usuario.id = :uId ORDER BY r.fecha DESC",
                    Resenia.class);
            q.setParameter("uId", usuarioId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta cuántas resenias ha escrito el usuario para un producto
     * específico.
     */
    public long contarResenasPorUsuarioYProducto(int usuarioId, int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(r) FROM Resenia r "
                    + "WHERE r.usuario.id = :uId AND r.producto.id = :pId",
                    Long.class);
            q.setParameter("uId", usuarioId);
            q.setParameter("pId", productoId);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    public Resenia guardar(Resenia resena) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(resena);
            em.getTransaction().commit();
            return resena;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Resenia r = em.find(Resenia.class, id);
            if (r != null) {
                em.remove(r);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Últimas N resenias de un producto de OTROS usuarios (no del usuarioId).
     * Si usuarioId <= 0, devuelve las últimas N resenias del producto en
     * general.
     */
    public List<Resenia> obtenerRecientesPorProducto(int productoId, int usuarioId, int limite) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = usuarioId > 0
                    ? "SELECT r FROM Resenia r LEFT JOIN FETCH r.usuario "
                    + "WHERE r.producto.id = :pId AND r.usuario.id <> :uId ORDER BY r.fecha DESC"
                    : "SELECT r FROM Resenia r LEFT JOIN FETCH r.usuario "
                    + "WHERE r.producto.id = :pId ORDER BY r.fecha DESC";

            TypedQuery<Resenia> q = em.createQuery(jpql, Resenia.class);
            q.setParameter("pId", productoId);
            if (usuarioId > 0) {
                q.setParameter("uId", usuarioId);
            }
            q.setMaxResults(limite);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Resenias del usuario logueado para un producto específico.
     */
    public List<Resenia> obtenerDelUsuarioPorProducto(int usuarioId, int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Resenia> q = em.createQuery(
                    "SELECT r FROM Resenia r LEFT JOIN FETCH r.usuario "
                    + "WHERE r.producto.id = :pId AND r.usuario.id = :uId ORDER BY r.fecha DESC",
                    Resenia.class);
            q.setParameter("pId", productoId);
            q.setParameter("uId", usuarioId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Total de resenias de un producto (para el contador).
     */
    public long contarPorProducto(int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(r) FROM Resenia r WHERE r.producto.id = :pId", Long.class);
            q.setParameter("pId", productoId);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }
}
