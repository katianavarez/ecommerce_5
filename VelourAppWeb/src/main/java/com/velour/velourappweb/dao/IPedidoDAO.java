package com.velour.velourappweb.dao;

import com.velour.velourappweb.enums.EstadoPedido;
import com.velour.velourappweb.models.Pedido;
import java.util.List;

public interface IPedidoDAO {

    void guardar(Pedido pedido);
    Pedido buscarPorId(Long id);
    List<Pedido> listarTodos();
    List<Pedido> listarPorUsuario(Long usuarioId);
    void actualizarEstado(Long id, EstadoPedido estado);
}
