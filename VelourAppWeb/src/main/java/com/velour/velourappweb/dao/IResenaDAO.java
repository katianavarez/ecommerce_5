package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Resena;
import java.util.List;

public interface IResenaDAO {

    void guardar(Resena resena);
    Resena buscarPorId(Long id);
    List<Resena> listarTodas();
    List<Resena> listarPorProducto(Long productoId);
    void eliminar(Long id);
}
