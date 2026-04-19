package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Carrito;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class CarritoDAO implements ICarritoDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Carrito carrito) {
        try {
            em.getTransaction().begin();
            em.persist(carrito);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Carrito buscarPorUsuario(Long usuarioId) {
        try {
            return em.createQuery(
                    "SELECT c FROM Carrito c WHERE c.usuario.id = :uid",
                    Carrito.class)
                    .setParameter("uid", usuarioId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void actualizar(Carrito carrito) {
        try {
            em.getTransaction().begin();
            em.merge(carrito);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void eliminar(Long carritoId) {
        try {
            em.getTransaction().begin();
            Carrito carrito = em.find(Carrito.class, carritoId);
            if (carrito != null) {
                em.remove(carrito);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public void eliminarProducto(Long carritoId, Long productoId) {
        try {
            em.getTransaction().begin();
            Carrito carrito = em.find(Carrito.class, carritoId);
            if (carrito != null) {
                carrito.getDetalles().removeIf(d -> d.getProducto().getId().equals(productoId));
                em.merge(carrito);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
