package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Acceso a datos del carrito persistente. La consulta principal trae el
 * carrito de un usuario junto con sus detalles y los productos asociados
 * en una sola query ({@code JOIN FETCH}), para evitar problemas de
 * lazy-loading al usar el carrito fuera del EntityManager.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class CarritoDAO {

    /**
     * Trae el carrito del usuario con sus detalles y productos ya cargados.
     * Devuelve null si el usuario todavía no tiene carrito en BD.
     */
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
