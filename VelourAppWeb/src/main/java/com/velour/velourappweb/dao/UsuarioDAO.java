package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Usuario;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class UsuarioDAO implements IUsuarioDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Usuario buscarPorCorreoYContrasenia(String correo, String contrasenia) {
        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.correo = :correo AND u.contrasenia = :contrasenia",
                    Usuario.class)
                    .setParameter("correo", correo)
                    .setParameter("contrasenia", contrasenia)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.id", Usuario.class)
                .getResultList();
    }

    @Override
    public void actualizar(Usuario usuario) {
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
