package com.velour.velourappweb.services;

import com.velour.velourappweb.models.Usuario;
import java.util.List;

public interface IUsuarioService {

    void registrar(Usuario usuario);
    Usuario buscarPorId(Long id);
    Usuario buscarPorCorreo(String correo);
    Usuario autenticar(String correo, String contrasenia);
    List<Usuario> obtenerClientes(int pagina, int tamano);
    long contarClientes();
    void actualizar(Usuario usuario);
    void eliminar(Long id);
}
