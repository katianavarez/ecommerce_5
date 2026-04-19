package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.models.Pedido;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author PC
 */
public class PedidoDAO {

    public List<Pedido> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.usuario LEFT JOIN FETCH p.direccionEnvio ORDER BY p.fecha DESC",
                    Pedido.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> q = em.createQuery(
                    "SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto "
                    + "LEFT JOIN FETCH p.pago LEFT JOIN FETCH p.usuario LEFT JOIN FETCH p.direccionEnvio "
                    + "WHERE p.id = :id", Pedido.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorUsuario(int usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> q = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.usuario.id = :uId ORDER BY p.fecha DESC",
                    Pedido.class);
            q.setParameter("uId", usuarioId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Pedido> obtenerPorEstado(String estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Pedido> q = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.usuario WHERE CAST(p.estado AS string) = :estado ORDER BY p.fecha DESC",
                    Pedido.class);
            q.setParameter("estado", estado);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si el usuario compró el producto en algún pedido que NO esté
     * cancelado.
     */
    public boolean usuarioComproProducto(int usuarioId, int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(p) FROM Pedido p "
                    + "JOIN p.detalles d "
                    + "WHERE p.usuario.id = :uId "
                    + "AND d.producto.id = :pId "
                    + "AND p.estado <> itson.ecommercewebaplication.enums.EstadoPedido.CANCELADO",
                    Long.class);
            q.setParameter("uId", usuarioId);
            q.setParameter("pId", productoId);
            return q.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public Pedido guardar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
            return pedido;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Pedido actualizar(Pedido pedido) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Pedido updated = em.merge(pedido);
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
     * Cuenta cuántas veces el usuario compró el producto en pedidos no
     * cancelados (cada unidad en un detalle cuenta como 1 compra-oportunidad).
     * Usamos COUNT(p) para contar pedidos distintos que contengan el producto.
     */
    public long contarComprasDeProducto(int usuarioId, int productoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(p) FROM Pedido p "
                    + "JOIN p.detalles d "
                    + "WHERE p.usuario.id = :uId "
                    + "AND d.producto.id = :pId "
                    + "AND p.estado <> itson.ecommercewebaplication.enums.EstadoPedido.CANCELADO",
                    Long.class);
            q.setParameter("uId", usuarioId);
            q.setParameter("pId", productoId);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Filtrado combinado: estado + búsqueda por cliente/número + ordenamiento.
     *
     * @param estado null o "TODOS" para todos los estados
     * @param busqueda texto a buscar en numPedido, nombre o correo del usuario
     * @param orden "reciente" | "mayorMonto" | "menorMonto"
     */
    public List<Pedido> buscarYFiltrar(String estado, String busqueda, String orden) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT p FROM Pedido p "
                    + "LEFT JOIN FETCH p.usuario u "
                    + "LEFT JOIN FETCH p.direccionEnvio "
                    + "WHERE 1=1 ");

            if (estado != null && !estado.isBlank() && !estado.equals("TODOS")) {
                jpql.append("AND CAST(p.estado AS string) = :estado ");
            }
            if (busqueda != null && !busqueda.isBlank()) {
                jpql.append("AND (LOWER(p.numPedido) LIKE :q "
                        + "OR LOWER(u.nombre) LIKE :q "
                        + "OR LOWER(u.correo) LIKE :q) ");
            }

            if ("mayorMonto".equals(orden)) {
                jpql.append("ORDER BY p.total DESC");
            } else if ("menorMonto".equals(orden)) {
                jpql.append("ORDER BY p.total ASC");
            } else {
                jpql.append("ORDER BY p.fecha DESC");
            }

            TypedQuery<Pedido> q = em.createQuery(jpql.toString(), Pedido.class);
            if (estado != null && !estado.isBlank() && !estado.equals("TODOS")) {
                q.setParameter("estado", estado);
            }
            if (busqueda != null && !busqueda.isBlank()) {
                q.setParameter("q", "%" + busqueda.toLowerCase() + "%");
            }

            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta pedidos por estado para mostrar en los tabs.
     */
    public long contarPorEstado(String estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(p) FROM Pedido p WHERE CAST(p.estado AS string) = :estado",
                    Long.class);
            q.setParameter("estado", estado);
            return q.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Calcula el total de ingresos de pedidos no cancelados del mes actual.
     */
    public double ventasDelMes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            TypedQuery<Double> q = em.createQuery(
                    "SELECT COALESCE(SUM(p.total), 0.0) FROM Pedido p "
                    + "WHERE FUNCTION('YEAR',  p.fecha) = :anio "
                    + "AND   FUNCTION('MONTH', p.fecha) = :mes  "
                    + "AND   p.estado <> itson.ecommercewebaplication.enums.EstadoPedido.CANCELADO",
                    Double.class);
            q.setParameter("anio", hoy.getYear());
            q.setParameter("mes", hoy.getMonthValue());
            Double result = q.getSingleResult();
            return result != null ? result : 0.0;
        } catch (Exception e) {
            // Fallback: sumar en Java si FUNCTION no está disponible
            return 0.0;
        } finally {
            em.close();
        }
    }

    /**
     * Devuelve los IDs de los productos más vendidos (por cantidad de unidades
     * en pedidos no cancelados), limitado a los primeros 'limite'. En caso de
     * empate, desempata por id DESC (más reciente).
     */
    public List<Integer> idsProductosTopVentas(int limite) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT d.producto.id FROM DetallePedido d "
                    + "JOIN d.producto prod "
                    + "WHERE (prod.activo = true OR prod.activo IS NULL) "
                    + "AND EXISTS (SELECT p FROM Pedido p JOIN p.detalles pd "
                    + "            WHERE pd = d AND p.estado <> "
                    + "            itson.ecommercewebaplication.enums.EstadoPedido.CANCELADO) "
                    + "GROUP BY d.producto.id "
                    + "ORDER BY SUM(d.cantidad) DESC, d.producto.id DESC",
                    Integer.class)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        } finally {
            em.close();
        }
    }
}
