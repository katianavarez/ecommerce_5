package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.CarritoDAO;
import itson.ecommercewebaplication.dao.ProductoDAO;
import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class CarritoBO {

    private final CarritoDAO carritoDAO = new CarritoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public Carrito obtenerPorUsuario(int usuarioId) {
        return carritoDAO.obtenerPorUsuario(usuarioId);
    }

    public void persistirCarrito(Usuario usuario, List<DetallePedido> itemsSesion) {
        if (usuario == null) {
            return;
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito existente = carritoDAO.obtenerPorUsuario(usuario.getId());

            if (itemsSesion == null || itemsSesion.isEmpty()) {
                if (existente != null) {
                    Carrito managed = em.find(Carrito.class, existente.getId());
                    if (managed != null) {
                        managed.getDetalles().clear();
                        managed.setTotal(0);
                        em.merge(managed);
                    }
                }
                em.getTransaction().commit();
                return;
            }

            double total = itemsSesion.stream()
                    .mapToDouble(d -> d.getPrecioUnidad() * d.getCantidad()).sum();

            if (existente == null) {
                List<DetallePedido> detallesBD = new ArrayList<>();
                for (DetallePedido item : itemsSesion) {
                    Producto prod = em.find(Producto.class, item.getProducto().getId());
                    if (prod != null) {
                        DetallePedido d = new DetallePedido(item.getCantidad(), item.getPrecioUnidad(), prod);
                        d.setTalla(item.getTalla());
                        detallesBD.add(d);
                    }
                }
                Usuario usuManaged = em.find(Usuario.class, usuario.getId());
                em.persist(new Carrito(usuManaged, detallesBD, total));
            } else {
                Carrito managed = em.find(Carrito.class, existente.getId());
                managed.getDetalles().clear();
                em.flush();
                for (DetallePedido item : itemsSesion) {
                    Producto prod = em.find(Producto.class, item.getProducto().getId());
                    if (prod != null) {
                        DetallePedido d = new DetallePedido(item.getCantidad(), item.getPrecioUnidad(), prod);
                        d.setTalla(item.getTalla());
                        managed.getDetalles().add(d);
                    }
                }
                managed.setTotal(total);
                em.merge(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[CarritoBO] Error al persistir carrito: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<DetallePedido> recuperarItemsDesdeDB(int usuarioId) {
        Carrito carrito = carritoDAO.obtenerPorUsuario(usuarioId);
        if (carrito == null || carrito.getDetalles() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(carrito.getDetalles());
    }

    public Carrito obtenerOCrear(Usuario usuario) {
        Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
        if (carrito == null) {
            carrito = new Carrito(usuario, new ArrayList<>(), 0.0);
            carritoDAO.guardar(carrito);
            // Recuperar con JOIN FETCH para tener detalles cargados
            carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
        }
        return carrito;
    }

    public Carrito agregarItem(Usuario usuario, int productoId, int cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        Producto producto = productoDAO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (producto.getStock() < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + producto.getStock());
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                Usuario usuManaged = em.find(Usuario.class, usuario.getId());
                carrito = new Carrito(usuManaged, new ArrayList<>(), 0.0);
                em.persist(carrito);
                em.flush();
            } else {
                carrito = em.find(Carrito.class, carrito.getId());
            }

            // Buscar si ya está en el carrito
            Producto prodManaged = em.find(Producto.class, productoId);
            boolean encontrado = false;
            for (DetallePedido d : carrito.getDetalles()) {
                if (d.getProducto().getId() == productoId) {
                    int nueva = d.getCantidad() + cantidad;
                    if (prodManaged.getStock() < nueva) {
                        throw new Exception("No hay suficiente stock. Disponible: " + prodManaged.getStock());
                    }
                    d.setCantidad(nueva);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                carrito.getDetalles().add(new DetallePedido(cantidad, prodManaged.getPrecio(), prodManaged));
            }

            recalcularTotal(carrito);
            em.merge(carrito);
            em.getTransaction().commit();
            return carritoDAO.obtenerPorUsuario(usuario.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Carrito actualizarCantidad(Usuario usuario, int productoId, int cantidad) throws Exception {
        if (cantidad < 0) {
            throw new Exception("La cantidad no puede ser negativa.");
        }

        Producto producto = productoDAO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (cantidad > 0 && producto.getStock() < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + producto.getStock());
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                throw new Exception("El carrito no existe.");
            }
            Carrito managed = em.find(Carrito.class, carrito.getId());

            if (cantidad == 0) {
                managed.getDetalles().removeIf(d -> d.getProducto().getId() == productoId);
            } else {
                boolean encontrado = false;
                for (DetallePedido d : managed.getDetalles()) {
                    if (d.getProducto().getId() == productoId) {
                        d.setCantidad(cantidad);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    throw new Exception("El producto no está en el carrito.");
                }
            }

            recalcularTotal(managed);
            em.merge(managed);
            em.getTransaction().commit();
            return carritoDAO.obtenerPorUsuario(usuario.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminarItem(Usuario usuario, int productoId) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                throw new Exception("El carrito no existe.");
            }
            Carrito managed = em.find(Carrito.class, carrito.getId());
            boolean eliminado = managed.getDetalles().removeIf(d -> d.getProducto().getId() == productoId);
            if (!eliminado) {
                throw new Exception("El producto no está en el carrito.");
            }
            recalcularTotal(managed);
            em.merge(managed);
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

    public void vaciar(Usuario usuario) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito != null) {
                Carrito managed = em.find(Carrito.class, carrito.getId());
                managed.getDetalles().clear();
                managed.setTotal(0);
                em.merge(managed);
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

    private void recalcularTotal(Carrito carrito) {
        double total = carrito.getDetalles().stream()
                .mapToDouble(d -> d.getPrecioUnidad() * d.getCantidad()).sum();
        carrito.setTotal(total);
    }
}
