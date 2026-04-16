package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Resena;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ResenaDAO implements IResenaDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Resena resena) {
        try {
            em.getTransaction().begin();
            em.persist(resena);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Resena buscarPorId(Long id) {
        return em.find(Resena.class, id);
    }

    @Override
    public List<Resena> listarTodas() {
        return em.createQuery("SELECT r FROM Resena r ORDER BY r.fecha DESC", Resena.class)
                .getResultList();
    }

    @Override
    public List<Resena> listarPorProducto(Long productoId) {
        return em.createQuery(
                "SELECT r FROM Resena r WHERE r.producto.id = :pid ORDER BY r.fecha DESC",
                Resena.class)
                .setParameter("pid", productoId)
                .getResultList();
    }

    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();
            Resena resena = em.find(Resena.class, id);
            if (resena != null) {
                em.remove(resena);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
