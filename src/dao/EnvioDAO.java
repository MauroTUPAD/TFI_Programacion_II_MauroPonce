package dao;

import entities.Envio;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface EnvioDAO {
    Integer crear(Envio b, Connection conn) throws SQLException;
    Envio buscarPorId(int id, Connection conn) throws SQLException;
    List<Envio> listarTodos(Connection conn) throws SQLException;
    boolean actualizar(Envio b, Connection conn) throws SQLException;
    boolean eliminarLogico(int id, Connection conn) throws SQLException;
}
