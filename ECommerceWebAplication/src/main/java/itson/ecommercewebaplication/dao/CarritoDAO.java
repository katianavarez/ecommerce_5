package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 *
 * @author PC
 */
public class CarritoDAO {

    public Carrito obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Carrito> q = em.createQuery(
                    "SELECT c FROM Carrito c LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.producto WHERE c.usuario.id = :uId",
                    Carrito.class);
            q.setParameter("uId", usuarioId);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Carrito obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Carrito.class, id);
        } finally {
            em.close();
        }
    }

    public Carrito guardar(Carrito carrito) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(carrito);
            em.getTransaction().commit();
            return carrito;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Carrito actualizar(Carrito carrito) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito updated = em.merge(carrito);
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
}
