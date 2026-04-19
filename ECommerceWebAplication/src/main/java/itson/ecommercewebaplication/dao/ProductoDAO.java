package itson.ecommercewebaplication.dao;

import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author PC
 */
public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE (p.activo = true OR p.activo IS NULL) ORDER BY p.id DESC", Producto.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Devuelve TODOS los productos (activos e inactivos) para el panel admin.
     */
    public List<Producto> obtenerTodosParaAdmin() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "ORDER BY p.activo DESC, p.id DESC", Producto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPaginados(int pagina, int tamano) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Integer> idsQuery = em.createQuery(
                    "SELECT p.id FROM Producto p WHERE (p.activo = true OR p.activo IS NULL) ORDER BY p.id DESC", Integer.class);
            idsQuery.setFirstResult((pagina - 1) * tamano);
            idsQuery.setMaxResults(tamano);
            List<Integer> ids = idsQuery.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }
            List<Producto> pagResult = em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "WHERE p.id IN :ids ORDER BY p.id DESC", Producto.class)
                    .setParameter("ids", ids).getResultList();
            // Cargar tallasDisponibles en query separada
            em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE p.id IN :ids", Producto.class)
                    .setParameter("ids", ids).getResultList();
            return pagResult;
        } finally {
            em.close();
        }
    }

    public long contarProductos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Producto p WHERE (p.activo = true OR p.activo IS NULL)", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Producto obtenerPorId(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Query 1: producto + categoria + tallasDisponibles
            List<Producto> r = em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE p.id = :id", Producto.class)
                    .setParameter("id", id).getResultList();
            if (r.isEmpty()) {
                return null;
            }
            Producto p = r.get(0);

            // Query 2: mismo producto con JOIN FETCH sobre stockPorTalla
            // Se hace por separado para evitar MultipleBagFetchException
            List<Producto> r2 = em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.stockPorTalla "
                    + "WHERE p.id = :id", Producto.class)
                    .setParameter("id", id).getResultList();
            if (!r2.isEmpty()) {
                p.setStockPorTalla(r2.get(0).getStockPorTalla());
            }
            return p;
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPorCategoria(int categoriaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE (p.activo = true OR p.activo IS NULL) AND p.categoria.id = :cId", Producto.class)
                    .setParameter("cId", categoriaId).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE (p.activo = true OR p.activo IS NULL) AND LOWER(p.nombre) LIKE LOWER(:n)", Producto.class)
                    .setParameter("n", "%" + nombre + "%").getResultList();
        } finally {
            em.close();
        }
    }

    public List<Producto> obtenerPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Query 1: producto + categoria (sin colecciones para evitar MultipleBagFetch)
            List<Producto> result = em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "WHERE p.id IN :ids "
                    + "AND (p.activo = true OR p.activo IS NULL)",
                    Producto.class)
                    .setParameter("ids", ids).getResultList();
            // Query 2: cargar tallasDisponibles
            em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE p.id IN :ids",
                    Producto.class)
                    .setParameter("ids", ids).getResultList();
            return result;
        } finally {
            em.close();
        }
    }

    /**
     * Filtrado avanzado: categorías (múltiple), tallas, color, precio máximo,
     * solo con stock.
     */
    public List<Producto> filtrar(List<Integer> categoriaIds, List<String> tallas,
            String color, Double precioMin, Double precioMax,
            boolean soloConStock) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT p.id FROM Producto p "
                    + "LEFT JOIN p.tallasDisponibles t ");

            List<String> condiciones = new ArrayList<>();
            condiciones.add("(p.activo = true OR p.activo IS NULL)");
            if (categoriaIds != null && !categoriaIds.isEmpty()) {
                condiciones.add("p.categoria.id IN :catIds");
            }
            if (tallas != null && !tallas.isEmpty()) {
                condiciones.add("t IN :tallas");
            }
            if (color != null && !color.isBlank()) {
                condiciones.add("p.color = :color");
            }
            if (precioMin != null) {
                condiciones.add("p.precio >= :pMin");
            }
            if (precioMax != null) {
                condiciones.add("p.precio <= :pMax");
            }
            if (soloConStock) {
                condiciones.add("p.stock > 0");
            }

            if (!condiciones.isEmpty()) {
                jpql.append("WHERE ").append(String.join(" AND ", condiciones));
            }

            TypedQuery<Integer> q = em.createQuery(jpql.toString(), Integer.class);
            if (categoriaIds != null && !categoriaIds.isEmpty()) {
                q.setParameter("catIds", categoriaIds);
            }
            if (tallas != null && !tallas.isEmpty()) {
                List<Tallas> tallasEnum = new ArrayList<>();
                for (String t : tallas) {
                    tallasEnum.add(Tallas.valueOf(t));
                }
                q.setParameter("tallas", tallasEnum);
            }
            if (color != null && !color.isBlank()) {
                q.setParameter("color", color);
            }
            if (precioMin != null) {
                q.setParameter("pMin", precioMin);
            }
            if (precioMax != null) {
                q.setParameter("pMax", precioMax);
            }

            List<Integer> ids = q.getResultList();
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }

            return em.createQuery(
                    "SELECT DISTINCT p FROM Producto p "
                    + "LEFT JOIN FETCH p.categoria "
                    + "LEFT JOIN FETCH p.tallasDisponibles "
                    + "WHERE p.id IN :ids ORDER BY p.precio ASC", Producto.class)
                    .setParameter("ids", ids).getResultList();
        } finally {
            em.close();
        }
    }

    public double obtenerPrecioMaximo() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double max = em.createQuery("SELECT MAX(p.precio) FROM Producto p", Double.class).getSingleResult();
            return max != null ? max : 10000.0;
        } finally {
            em.close();
        }
    }

    public Producto guardar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            return producto;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Producto actualizar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto updated = em.merge(producto);
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
     * Soft delete: marca el producto como inactivo (activo = false). El
     * producto desaparece del catálogo pero se conserva el historial de
     * pedidos. Si ya estaba inactivo, no hace nada.
     */
    public void eliminar(int id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id);
            if (p == null) {
                em.getTransaction().rollback();
                return;
            }
            p.setActivo(false);
            em.merge(p);
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

    /**
     * Reactiva un producto archivado (activo = true).
     */
    public void reactivar(int id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id);
            if (p != null) {
                p.setActivo(true);
                em.merge(p);
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

    public void actualizarStock(int productoId, int delta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, productoId);
            if (p != null) {
                p.setStock(p.getStock() + delta);
                em.merge(p);
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
