package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Usuario;
import java.util.List;

public interface IUsuarioDAO {

    void guardar(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorCorreo(String correo);
    Usuario buscarPorCorreoYContrasenia(String correo, String contrasenia);
    List<Usuario> listarTodos();
    void actualizar(Usuario usuario);
    void eliminar(Long id);
}
