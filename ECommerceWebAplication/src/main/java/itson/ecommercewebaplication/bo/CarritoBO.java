package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.CarritoDAO;
import itson.ecommercewebaplication.dao.ProductoDAO;
import itson.ecommercewebaplication.enums.Tallas;
import itson.ecommercewebaplication.models.Carrito;
import itson.ecommercewebaplication.models.DetallePedido;
import itson.ecommercewebaplication.models.Producto;
import itson.ecommercewebaplication.models.StockTalla;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lógica de negocio del carrito persistente del cliente logueado. Maneja
 * agregar, actualizar cantidad, eliminar y vaciar items, validando siempre
 * que haya stock disponible (consultando por talla cuando la prenda la usa).
 * Incluye además {@link #fusionar(List, List)}, que une el carrito que el
 * cliente tenía como invitado (guardado en el navegador) con el que ya
 * tuviera en BD al momento de iniciar sesión, sumando cantidades cuando
 * coinciden producto y talla.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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

    /** Compatibilidad: agregar sin talla (productos sin tallas). */
    public Carrito agregarItem(Usuario usuario, int productoId, int cantidad) throws Exception {
        return agregarItem(usuario, productoId, cantidad, null);
    }

    /**
     * Agrega un producto al carrito del usuario. Si ya existía la misma
     * combinación de producto y talla, suma a la cantidad anterior; si no,
     * crea una línea nueva. En ambos casos verifica que haya stock suficiente
     * antes de confirmar.
     *
     * @param talla talla seleccionada, o null para productos sin talla
     * @throws Exception si la cantidad es menor o igual a cero, el producto no
     *                   existe, o no hay stock suficiente
     */
    public Carrito agregarItem(Usuario usuario, int productoId, int cantidad, String talla) throws Exception {
        if (cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        Producto producto = productoDAO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (talla != null && talla.isBlank()) talla = null;
        int stockDisponible = stockDisponiblePara(producto, talla);
        if (stockDisponible < cantidad) {
            throw new Exception("Stock insuficiente. Disponible: " + stockDisponible);
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

            Producto prodManaged = em.find(Producto.class, productoId);
            boolean encontrado = false;
            for (DetallePedido d : carrito.getDetalles()) {
                if (d.getProducto().getId() == productoId && Objects.equals(d.getTalla(), talla)) {
                    int nueva = d.getCantidad() + cantidad;
                    if (stockDisponiblePara(prodManaged, talla) < nueva) {
                        throw new Exception("No hay suficiente stock. Disponible: "
                                + stockDisponiblePara(prodManaged, talla));
                    }
                    d.setCantidad(nueva);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                DetallePedido nuevo = new DetallePedido(cantidad, prodManaged.getPrecio(), prodManaged);
                nuevo.setTalla(talla);
                carrito.getDetalles().add(nuevo);
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

    private int stockDisponiblePara(Producto producto, String tallaStr) {
        if (tallaStr != null && producto.getStockPorTalla() != null && !producto.getStockPorTalla().isEmpty()) {
            try {
                Tallas talla = Tallas.valueOf(tallaStr);
                for (StockTalla st : producto.getStockPorTalla()) {
                    if (st.getTalla() == talla) return st.getCantidad();
                }
                return 0;
            } catch (IllegalArgumentException ignored) {
                return 0;
            }
        }
        return producto.getStock();
    }

    public Carrito actualizarCantidad(Usuario usuario, int productoId, int cantidad) throws Exception {
        return actualizarCantidad(usuario, productoId, cantidad, null);
    }

    /**
     * Fija la cantidad de una línea del carrito a un valor exacto. Si la
     * cantidad es cero, la línea se elimina; si es mayor, se valida que haya
     * stock suficiente antes de actualizarla.
     *
     * @param talla talla de la línea a modificar, o null si no aplica
     * @throws Exception si la cantidad es negativa, el carrito o la línea no
     *                   existen, o no hay stock suficiente
     */
    public Carrito actualizarCantidad(Usuario usuario, int productoId, int cantidad, String talla) throws Exception {
        if (cantidad < 0) {
            throw new Exception("La cantidad no puede ser negativa.");
        }

        Producto producto = productoDAO.obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado.");
        }
        if (talla != null && talla.isBlank()) talla = null;
        if (cantidad > 0) {
            int stockDisponible = stockDisponiblePara(producto, talla);
            if (stockDisponible < cantidad) {
                throw new Exception("Stock insuficiente. Disponible: " + stockDisponible);
            }
        }
        final String tallaFinal = talla;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                throw new Exception("El carrito no existe.");
            }
            Carrito managed = em.find(Carrito.class, carrito.getId());

            if (cantidad == 0) {
                managed.getDetalles().removeIf(d ->
                        d.getProducto().getId() == productoId
                        && Objects.equals(d.getTalla(), tallaFinal));
            } else {
                boolean encontrado = false;
                for (DetallePedido d : managed.getDetalles()) {
                    if (d.getProducto().getId() == productoId
                            && Objects.equals(d.getTalla(), tallaFinal)) {
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

    /** Compatibilidad: elimina TODAS las variantes de talla del producto. */
    public void eliminarItem(Usuario usuario, int productoId) throws Exception {
        eliminarItem(usuario, productoId, null);
    }

    /**
     * Si {@code talla == null}, elimina todas las líneas con ese producto.
     * Si {@code talla != null}, elimina solo la línea (producto + talla) específica.
     */
    public void eliminarItem(Usuario usuario, int productoId, String talla) throws Exception {
        if (talla != null && talla.isBlank()) talla = null;
        final String tallaFinal = talla;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Carrito carrito = carritoDAO.obtenerPorUsuario(usuario.getId());
            if (carrito == null) {
                throw new Exception("El carrito no existe.");
            }
            Carrito managed = em.find(Carrito.class, carrito.getId());
            boolean eliminado = managed.getDetalles().removeIf(d -> {
                if (d.getProducto().getId() != productoId) return false;
                if (tallaFinal == null) return true;
                return Objects.equals(d.getTalla(), tallaFinal);
            });
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

   
    /**
     * Combina el carrito guardado en BD con el que el cliente traía como
     * invitado. Cuando una misma combinación de producto y talla aparece en
     * ambos, suma las cantidades; el resto se agrega tal cual. Se usa al
     * iniciar sesión para que el cliente no pierda lo que había agregado
     * antes de loguearse.
     */
    public static List<DetallePedido> fusionar(List<DetallePedido> deBD, List<DetallePedido> deSesion) {
        if (deBD == null || deBD.isEmpty()) {
            return deSesion != null ? new ArrayList<>(deSesion) : new ArrayList<>();
        }
        if (deSesion == null || deSesion.isEmpty()) {
            return new ArrayList<>(deBD);
        }
        List<DetallePedido> resultado = new ArrayList<>(deBD);
        for (DetallePedido itemSesion : deSesion) {
            boolean encontrado = false;
            for (DetallePedido itemBD : resultado) {
                if (itemBD.getProducto().getId() == itemSesion.getProducto().getId()
                        && Objects.equals(itemBD.getTalla(), itemSesion.getTalla())) {
                    itemBD.setCantidad(itemBD.getCantidad() + itemSesion.getCantidad());
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                resultado.add(itemSesion);
            }
        }
        return resultado;
    }
}
