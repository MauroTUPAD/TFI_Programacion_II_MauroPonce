package dao;

import entities.Pedido;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {
    Integer crear(Pedido a, Connection conn) throws SQLException;
    Pedido buscarPorId(int id, Connection conn) throws SQLException;
    List<Pedido> listarTodos(Connection conn) throws SQLException;
    boolean actualizar(Pedido a, Connection conn) throws SQLException;
    boolean eliminarLogico(int id, Connection conn) throws SQLException;
}
