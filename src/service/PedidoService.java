package service;

import entities.Envio;
import entities.Pedido;
import java.util.List;

public interface PedidoService {
    void crearPedidoCompleto(Pedido a, Envio b) throws Exception;
    void actualizarPedido(Pedido a) throws Exception;
    void eliminarPedido(int idPedido) throws Exception;
    Pedido obtenerPedidoPorId(int idPedido) throws Exception;
    List<Pedido> listarPedidos() throws Exception;
}
