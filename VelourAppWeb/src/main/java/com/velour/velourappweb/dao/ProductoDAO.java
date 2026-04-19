package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Producto;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProductoDAO implements IProductoDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Producto producto) {
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Producto buscarPorId(Long id) {
        return em.find(Producto.class, id);
    }

    @Override
    public List<Producto> listarTodos() {
        return em.createQuery("SELECT p FROM Producto p ORDER BY p.id", Producto.class)
                .getResultList();
    }

    @Override
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return em.createQuery(
                "SELECT p FROM Producto p WHERE p.categoria.id = :catId ORDER BY p.nombre",
                Producto.class)
                .setParameter("catId", categoriaId)
                .getResultList();
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return em.createQuery(
                "SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE :nombre ORDER BY p.nombre",
                Producto.class)
                .setParameter("nombre", "%" + nombre.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public void actualizar(Producto producto) {
        try {
            em.getTransaction().begin();
            em.merge(producto);
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
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
