package com.velour.velourappweb.dao;

import com.velour.velourappweb.models.Carrito;

public interface ICarritoDAO {

    void guardar(Carrito carrito);
    Carrito buscarPorUsuario(Long usuarioId);
    void actualizar(Carrito carrito);
    void eliminar(Long carritoId);
    void eliminarProducto(Long carritoId, Long productoId);
}
