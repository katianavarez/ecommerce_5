package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Proveedor;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProveedorDAO {

    public List<Proveedor> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Proveedor p ORDER BY p.activo DESC, p.nombre",
                    Proveedor.class).getResultList();
        } finally {
            em.close();
        }
    }

    /** Solo proveedores activos. Útil para el dropdown del form de productos. */
    public List<Proveedor> obtenerActivos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre",
                    Proveedor.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Proveedor obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Proveedor.class, id);
        } finally {
            em.close();
        }
    }

    public Proveedor guardar(Proveedor proveedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(proveedor);
            em.getTransaction().commit();
            return proveedor;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Proveedor actualizar(Proveedor proveedor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor updated = em.merge(proveedor);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /** Soft delete: marca como inactivo. No borra para preservar histórico de productos. */
    public void archivar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor p = em.find(Proveedor.class, id);
            if (p != null) {
                p.setActivo(false);
                em.merge(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void reactivar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor p = em.find(Proveedor.class, id);
            if (p != null) {
                p.setActivo(true);
                em.merge(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
