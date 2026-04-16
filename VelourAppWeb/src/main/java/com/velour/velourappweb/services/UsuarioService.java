package com.velour.velourappweb.services;

import com.velour.velourappweb.dao.IUsuarioDAO;
import com.velour.velourappweb.dao.UsuarioDAO;
import com.velour.velourappweb.models.Usuario;
import java.util.List;

public class UsuarioService implements IUsuarioService {

    private final IUsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void registrar(Usuario usuario) {
        usuarioDAO.guardar(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioDAO.buscarPorCorreo(correo);
    }

    @Override
    public Usuario autenticar(String correo, String contrasenia) {
        return usuarioDAO.buscarPorCorreoYContrasenia(correo, contrasenia);
    }

    @Override
    public List<Usuario> obtenerClientes(int pagina, int tamano) {
        return usuarioDAO.listarClientes(pagina, tamano);
    }

    @Override
    public long contarClientes() {
        return usuarioDAO.contarClientes();
    }

    @Override
    public void actualizar(Usuario usuario) {
        usuarioDAO.actualizar(usuario);
    }

    @Override
    public void eliminar(Long id) {
        usuarioDAO.eliminar(id);
    }
}
