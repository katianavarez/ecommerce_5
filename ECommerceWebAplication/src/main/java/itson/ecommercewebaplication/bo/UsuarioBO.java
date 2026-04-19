package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.UsuarioDAO;
import itson.ecommercewebaplication.enums.Rol;
import itson.ecommercewebaplication.models.Usuario;
import java.util.List;

/**
 *
 * @author PC
 */
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String correo, String contrasena) throws Exception {
        if (correo == null || correo.isBlank()) {
            throw new Exception("El correo es requerido.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new Exception("La contraseña es requerida.");
        }
        Usuario u = usuarioDAO.obtenerPorCorreo(correo);
        if (u == null || !u.getContraseña().equals(contrasena)) {
            throw new Exception("El correo o la contraseña son incorrectos.");
        }
        return u;
    }

    public boolean esAdministrador(Usuario u) {
        return u != null && u.getRol() == Rol.ADMINISTRADOR;
    }

    public Usuario registrar(Usuario usuario) throws Exception {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new Exception("El nombre completo es requerido.");
        }
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new Exception("El correo electrónico es requerido.");
        }
        if (!usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("El formato del correo no es válido.");
        }
        if (usuario.getContraseña() == null || usuario.getContraseña().isBlank()) {
            throw new Exception("La contraseña es requerida.");
        }
        if (usuario.getContraseña().length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }
        if (usuarioDAO.obtenerPorCorreo(usuario.getCorreo()) != null) {
            throw new Exception("El correo ya se encuentra registrado en el sistema.");
        }
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.CLIENTE);
        }
        return usuarioDAO.guardar(usuario);
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
        if (!usuario.getContraseña().equals(contrasenaActual)) {
            throw new Exception("La contraseña actual no es correcta.");
        }

        usuario.setContraseña(nuevaContrasena);
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
}
