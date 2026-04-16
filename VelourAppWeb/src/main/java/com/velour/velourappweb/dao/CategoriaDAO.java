package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Categoria;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class CategoriaDAO implements ICategoriaDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Categoria categoria) {
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Categoria buscarPorId(Long id) {
        return em.find(Categoria.class, id);
    }

    @Override
    public Categoria buscarPorNombre(String nombre) {
        try {
            return em.createQuery("SELECT c FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Categoria> listarTodas() {
        return em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class)
                .getResultList();
    }

    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null) {
                em.remove(categoria);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
