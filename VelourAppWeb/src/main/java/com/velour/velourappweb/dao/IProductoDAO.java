package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Producto;
import java.util.List;

public interface IProductoDAO {

    void guardar(Producto producto);
    Producto buscarPorId(Long id);
    List<Producto> listarTodos();
    List<Producto> listarPorCategoria(Long categoriaId);
    List<Producto> buscarPorNombre(String nombre);
    void actualizar(Producto producto);
    void eliminar(Long id);
}
