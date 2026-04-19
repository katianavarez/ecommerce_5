package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 *
 * @author PC
 */
public class CategoriaDAO {

    public List<Categoria> obtenerTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Categoria obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public java.util.Map<Integer, Long> contarProductosPorCategoria() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            java.util.List<Object[]> rows = em.createQuery(
                    "SELECT p.categoria.id, COUNT(p) FROM Producto p WHERE (p.activo = true OR p.activo IS NULL) GROUP BY p.categoria.id",
                    Object[].class).getResultList();
            java.util.Map<Integer, Long> mapa = new java.util.LinkedHashMap<>();
            for (Object[] row : rows) {
                mapa.put((Integer) row[0], (Long) row[1]);
            }
            return mapa;
        } finally {
            em.close();
        }
    }

    public Categoria guardar(Categoria categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
            return categoria;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Categoria actualizar(Categoria categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categoria updated = em.merge(categoria);
            em.getTransaction().commit();
            return updated;
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
            Categoria c = em.find(Categoria.class, id);
            if (c != null) {
                em.remove(c);
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
}
