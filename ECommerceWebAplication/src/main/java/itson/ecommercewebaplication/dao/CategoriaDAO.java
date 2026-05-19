package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Acceso a datos de categorías. Aparte del CRUD que usa el panel admin,
 * ofrece conteos de productos por categoría que sirven para mostrar
 * cuántas prendas tiene cada una en los filtros del catálogo y en el
 * dashboard.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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

    /**
     * Busca una categoría por nombre sin distinguir mayúsculas. Devuelve
     * null si no existe; se usa para evitar categorías duplicadas al crear.
     */
    public Categoria obtenerPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categoria> q = em.createQuery(
                    "SELECT c FROM Categoria c WHERE LOWER(c.nombre) = LOWER(:nombre)",
                    Categoria.class);
            q.setParameter("nombre", nombre);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public long contarProductos(int categoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :cId",
                    Long.class);
            q.setParameter("cId", categoriaId);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Devuelve un mapa {@code categoriaId -> número de productos activos}
     * en una sola consulta agrupada, para no disparar un COUNT por cada
     * categoría al pintar los filtros del catálogo.
     */
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
