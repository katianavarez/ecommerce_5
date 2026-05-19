package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Acceso a datos de direcciones de envío. Permite listar las direcciones
 * de un usuario (la principal primero) y garantiza, mediante
 * {@link #marcarPrincipal(int, int)}, que solo una quede marcada como
 * principal por usuario.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class DireccionDAO {

    public Direccion obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Direccion.class, id);
        } finally {
            em.close();
        }
    }

    public List<Direccion> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Direccion> q = em.createQuery(
                    "SELECT d FROM Direccion d WHERE d.usuario.id = :uId ORDER BY d.principal DESC, d.id ASC",
                    Direccion.class);
            q.setParameter("uId", usuarioId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Direccion guardar(Direccion direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(direccion);
            em.getTransaction().commit();
            return direccion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Direccion actualizar(Direccion direccion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Direccion updated = em.merge(direccion);
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

    /**
     * Marca la dirección dada como principal y quita el flag
     * de todas las demás del mismo usuario (solo una puede ser principal).
     */
    public void marcarPrincipal(int direccionId, int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Quitar principal a todas las del usuario
            em.createQuery(
                "UPDATE Direccion d SET d.principal = false WHERE d.usuario.id = :uid")
                .setParameter("uid", usuarioId)
                .executeUpdate();
            // Marcar la seleccionada
            em.createQuery(
                "UPDATE Direccion d SET d.principal = true WHERE d.id = :id")
                .setParameter("id", direccionId)
                .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Direccion d = em.find(Direccion.class, id);
            if (d != null) {
                em.remove(d);
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
