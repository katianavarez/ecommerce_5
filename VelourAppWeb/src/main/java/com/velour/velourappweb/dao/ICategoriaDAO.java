package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Categoria;
import java.util.List;

public interface ICategoriaDAO {

    void guardar(Categoria categoria);
    Categoria buscarPorId(Long id);
    Categoria buscarPorNombre(String nombre);
    List<Categoria> listarTodas();
    void eliminar(Long id);
}
