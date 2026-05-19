package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Acceso a datos de usuarios. Encapsula las consultas JPQL y el manejo del
 * EntityManager para que el {@code UsuarioBO} trabaje con objetos sin
 * preocuparse de transacciones ni conexiones.
 * 
 * Tanto eliminar como activar trabajan sobre la bandera {@code activo}
 * (baja lógica), nunca se borra físicamente un usuario para no perder la
 * trazabilidad de sus pedidos.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class UsuarioDAO {

    public List<Usuario> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombre", Usuario.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Busca un usuario por su correo (que es único). Devuelve null si no
     * existe ninguno, en lugar de propagar la excepción de JPA. Lo usan
     * el login y la validación de correo duplicado en el registro.
     */
    public Usuario obtenerPorCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> q = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class);
            q.setParameter("correo", correo);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Usuario guardar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return usuario;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario actualizar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario updated = em.merge(usuario);
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

    /** Baja lógica: marca al usuario como inactivo sin borrarlo de la BD. */
    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario u = em.find(Usuario.class, id);
            if (u != null) {
                u.setActivo(false);
                em.merge(u);
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

    /** Reactiva una cuenta dada de baja (vuelve a poner activo = true). */
    public void activar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario u = em.find(Usuario.class, id);
            if (u != null) {
                u.setActivo(true);
                em.merge(u);
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

    /** Cuenta usuarios filtrando por rol y opcionalmente por estado activo, sin cargar entidades. */
    public long contarPorRol(String rolName, Boolean soloActivos) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol";
            if (soloActivos != null) {
                jpql += " AND u.activo = :activo";
            }
            jakarta.persistence.TypedQuery<Long> q = em.createQuery(jpql, Long.class);
            q.setParameter("rol", itson.ecommercewebaplication.enums.Rol.valueOf(rolName));
            if (soloActivos != null) {
                q.setParameter("activo", soloActivos);
            }
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }
}
