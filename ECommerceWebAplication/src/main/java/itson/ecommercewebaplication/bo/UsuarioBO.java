package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.UsuarioDAO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.Direccion;
import itson.ecommercewebaplication.models.Usuario;
import itson.ecommercewebaplication.util.JPAUtil;
import itson.ecommercewebaplication.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Lógica de negocio de usuarios: login, registro, edición de perfil, cambio
 * de contraseña y gestión de cuentas (alta/baja lógica) que usa el admin.
 * Aquí viven las validaciones y reglas que no deben quedar en los servlets,
 * como el formato del correo, la longitud mínima de contraseña y el hasheo
 * con BCrypt antes de tocar la BD.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida las credenciales y devuelve el usuario si son correctas.
     * Usa un mensaje de error idéntico para correo inexistente y contraseña
     * incorrecta, de modo que un atacante no pueda deducir qué correos están
     * registrados. Si la contraseña aún está en texto plano (datos del seed),
     * la re-hashea con BCrypt en este primer login. El estado de la cuenta
     * (activa/inactiva) solo se revela después de validar la contraseña.
     *
     * @throws Exception si las credenciales son inválidas o la cuenta está dada de baja
     */
    public Usuario login(String correo, String contrasena) throws Exception {
        // Mensaje uniforme para no permitir enumeración de cuentas.
        final String CREDENCIALES_INVALIDAS = "Credenciales inválidas.";

        if (correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new Exception(CREDENCIALES_INVALIDAS);
        }
        Usuario u = usuarioDAO.obtenerPorCorreo(correo);
        if (u == null) {
            throw new Exception(CREDENCIALES_INVALIDAS);
        }
        String stored = u.getContrasena();
        boolean ok;
        if (PasswordUtil.esHash(stored)) {
            ok = PasswordUtil.verify(contrasena, stored);
        } else {
            ok = stored != null && stored.equals(contrasena);
            if (ok) {
                u.setContrasena(PasswordUtil.hash(contrasena));
                usuarioDAO.actualizar(u);
            }
        }
        if (!ok) {
            throw new Exception(CREDENCIALES_INVALIDAS);
        }
        // Solo después de validar credenciales se revela el estado de la cuenta.
        if (!u.isActivo()) {
            throw new Exception("Esta cuenta ha sido dada de baja. Contacta al administrador.");
        }
        return u;
    }

    public boolean esAdministrador(Usuario u) {
        return u != null && u.getRol() == Rol.ADMINISTRADOR;
    }

    /**
     * Registra un cliente nuevo junto con su dirección principal. Valida los
     * campos obligatorios, el formato de correo y que no exista otro usuario
     * con el mismo correo. La contraseña se hashea antes de persistir, y el
     * usuario y su dirección se guardan en una sola transacción.
     *
     * @throws Exception si falta algún dato, el correo ya existe o falla la persistencia
     */
    public Usuario registrar(Usuario usuario, Direccion direccion) throws Exception {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new Exception("El nombre completo es requerido.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new Exception("El correo electrónico es requerido.");
        }
        if (!usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("El formato del correo no es válido.");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            throw new Exception("La contraseña es requerida.");
        }
        if (usuario.getContrasena().length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }
        if (direccion == null
                || direccion.getCalle() == null || direccion.getCalle().isBlank()
                || direccion.getCiudad() == null || direccion.getCiudad().isBlank()
                || direccion.getEstado() == null || direccion.getEstado().isBlank()
                || direccion.getCodigoPostal() == null || direccion.getCodigoPostal().isBlank()) {
            throw new Exception("La dirección es requerida (calle, ciudad, estado, código postal).");
        }
        if (usuarioDAO.obtenerPorCorreo(usuario.getCorreo()) != null) {
            throw new Exception("El correo ya se encuentra registrado en el sistema.");
        }
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.CLIENTE);
        }
        usuario.setContrasena(PasswordUtil.hash(usuario.getContrasena()));

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            direccion.setUsuario(usuario);
            direccion.setPrincipal(true);
            em.persist(direccion);
            em.getTransaction().commit();
            return usuario;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al registrar usuario: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Usuario actualizarPerfil(int usuarioId, String nombre, String telefono) throws Exception {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception("El nombre completo es requerido.");
        }

        Usuario usuario = usuarioDAO.obtenerPorId(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }

        usuario.setNombre(nombre.trim());
        usuario.setTelefono(telefono != null ? telefono.trim() : "");
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Cambia la contraseña verificando primero la actual. Exige que la nueva
     * tenga al menos 8 caracteres y que coincida con la confirmación, y la
     * guarda hasheada con BCrypt.
     *
     * @throws Exception si la contraseña actual no coincide o la nueva no cumple las reglas
     */
    public void cambiarContrasena(int usuarioId, String contrasenaActual,
            String nuevaContrasena, String confirmar) throws Exception {
        if (contrasenaActual == null || contrasenaActual.isBlank()) {
            throw new Exception("La contraseña actual es requerida.");
        }
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            throw new Exception("La nueva contraseña es requerida.");
        }
        if (nuevaContrasena.length() < 8) {
            throw new Exception("La nueva contraseña debe tener al menos 8 caracteres.");
        }
        if (!nuevaContrasena.equals(confirmar)) {
            throw new Exception("Las contraseñas no coinciden.");
        }

        Usuario usuario = usuarioDAO.obtenerPorId(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }

        String stored = usuario.getContrasena();
        boolean ok;
        if (PasswordUtil.esHash(stored)) {
            ok = PasswordUtil.verify(contrasenaActual, stored);
        } else {
            ok = stored != null && stored.equals(contrasenaActual);
        }
        if (!ok) {
            throw new Exception("La contraseña actual no es correcta.");
        }

        usuario.setContrasena(PasswordUtil.hash(nuevaContrasena));
        usuarioDAO.actualizar(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public Usuario obtenerPorCorreo(String correo) {
        return usuarioDAO.obtenerPorCorreo(correo);
    }

    public Usuario actualizar(Usuario usuario) throws Exception {
        if (usuarioDAO.obtenerPorId(usuario.getId()) == null) {
            throw new Exception("Usuario no encontrado.");
        }
        return usuarioDAO.actualizar(usuario);
    }

    public void eliminar(int id) throws Exception {
        if (usuarioDAO.obtenerPorId(id) == null) {
            throw new Exception("Usuario no encontrado.");
        }
        usuarioDAO.eliminar(id);
    }

    public void activar(int id) throws Exception {
        if (usuarioDAO.obtenerPorId(id) == null) {
            throw new Exception("Usuario no encontrado.");
        }
        usuarioDAO.activar(id);
    }

    /** Conteo eficiente para el dashboard sin traer la lista completa a memoria. */
    public long contarClientes() {
        return usuarioDAO.contarPorRol(Rol.CLIENTE.name(), null);
    }

    public long contarClientesActivos() {
        return usuarioDAO.contarPorRol(Rol.CLIENTE.name(), Boolean.TRUE);
    }
}
