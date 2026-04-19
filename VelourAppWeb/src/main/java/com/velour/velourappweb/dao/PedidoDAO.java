package com.velour.velourappweb.dao;

import com.velour.velourappweb.enums.EstadoPedido;
import com.velour.velourappweb.models.Pedido;
import com.velour.velourappweb.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PedidoDAO implements IPedidoDAO {

    private final EntityManager em = JPAUtil.getInstance().getEntityManager();

    @Override
    public void guardar(Pedido pedido) {
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Pedido buscarPorId(Long id) {
        return em.find(Pedido.class, id);
    }

    @Override
    public List<Pedido> listarTodos() {
        return em.createQuery("SELECT p FROM Pedido p ORDER BY p.fecha DESC", Pedido.class)
                .getResultList();
    }

    @Override
    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return em.createQuery(
                "SELECT p FROM Pedido p WHERE p.usuario.id = :uid ORDER BY p.fecha DESC",
                Pedido.class)
                .setParameter("uid", usuarioId)
                .getResultList();
    }

    @Override
    public void actualizarEstado(Long id, EstadoPedido estado) {
        try {
            em.getTransaction().begin();
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido != null) {
                pedido.setEstado(estado);
                em.merge(pedido);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
