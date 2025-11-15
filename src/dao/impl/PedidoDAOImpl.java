package dao.impl;

import dao.PedidoDAO;
import entities.Envio;
import entities.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public Integer crear(Pedido pedido, Connection conn) throws SQLException {
        // Tampoco seteamos "eliminado": usa DEFAULT 0 en SQL Server
        String sql = "INSERT INTO Pedido (numero, fecha, clienteNombre, total, estado, id_envio) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pedido.getNumero());
            ps.setDate(2, Date.valueOf(pedido.getFecha()));
            ps.setString(3, pedido.getClienteNombre());
            ps.setDouble(4, pedido.getTotal());
            ps.setString(5, pedido.getEstado());

            if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null) {
                ps.setLong(6, pedido.getEnvio().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            int filas = ps.executeUpdate();
            if (filas == 0) return null;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    pedido.setId((long) id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public Pedido buscarPorId(int id, Connection conn) throws SQLException {
        String sql =
            "SELECT p.id, p.eliminado, p.numero, p.fecha, p.clienteNombre, p.total, p.estado, p.id_envio, " +
            "       e.id AS e_id, e.eliminado AS e_eliminado, e.tracking, e.empresa, e.tipo, e.costo, " +
            "       e.fechaDespacho, e.fechaEstimada, e.estado AS e_estado " +
            "FROM Pedido p " +
            "LEFT JOIN Envio e ON p.id_envio = e.id " +
            "WHERE p.id = ? AND p.eliminado = 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Envio envio = null;
                Object eIdObj = rs.getObject("e_id");
                if (eIdObj != null && !rs.getBoolean("e_eliminado")) {
                    envio = new Envio();
                    envio.setId(((Number) eIdObj).longValue());
                    envio.setEliminado(rs.getBoolean("e_eliminado"));
                    envio.setTracking(rs.getString("tracking"));
                    envio.setEmpresa(rs.getString("empresa"));
                    envio.setTipo(rs.getString("tipo"));

                    double costo = rs.getDouble("costo");
                    if (rs.wasNull()) envio.setCosto(null); else envio.setCosto(costo);

                    Date fd = rs.getDate("fechaDespacho");
                    envio.setFechaDespacho(fd != null ? fd.toLocalDate() : null);

                    Date fe = rs.getDate("fechaEstimada");
                    envio.setFechaEstimada(fe != null ? fe.toLocalDate() : null);

                    envio.setEstado(rs.getString("e_estado"));
                }

                Pedido p = new Pedido();
                p.setId(rs.getLong("id"));
                p.setEliminado(rs.getBoolean("eliminado"));
                p.setNumero(rs.getString("numero"));
                p.setFecha(rs.getDate("fecha").toLocalDate());
                p.setClienteNombre(rs.getString("clienteNombre"));
                p.setTotal(rs.getDouble("total"));
                p.setEstado(rs.getString("estado"));
                p.setEnvio(envio);

                return p;
            }
        }
    }

    @Override
    public List<Pedido> listarTodos(Connection conn) throws SQLException {
        String sql =
            "SELECT p.id, p.eliminado, p.numero, p.fecha, p.clienteNombre, p.total, p.estado, p.id_envio, " +
            "       e.id AS e_id, e.eliminado AS e_eliminado, e.tracking " +
            "FROM Pedido p " +
            "LEFT JOIN Envio e ON p.id_envio = e.id " +
            "WHERE p.eliminado = 0 " +
            "ORDER BY p.id";

        List<Pedido> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Envio envio = null;
                Object eIdObj = rs.getObject("e_id");
                if (eIdObj != null && !rs.getBoolean("e_eliminado")) {
                    envio = new Envio();
                    envio.setId(((Number) eIdObj).longValue());
                    envio.setTracking(rs.getString("tracking"));
                }

                Pedido p = new Pedido();
                p.setId(rs.getLong("id"));
                p.setEliminado(rs.getBoolean("eliminado"));
                p.setNumero(rs.getString("numero"));
                p.setFecha(rs.getDate("fecha").toLocalDate());
                p.setClienteNombre(rs.getString("clienteNombre"));
                p.setTotal(rs.getDouble("total"));
                p.setEstado(rs.getString("estado"));
                p.setEnvio(envio);

                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public boolean actualizar(Pedido pedido, Connection conn) throws SQLException {
        String sql =
            "UPDATE Pedido SET numero=?, fecha=?, clienteNombre=?, total=?, estado=?, id_envio=? " +
            "WHERE id = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pedido.getNumero());
            ps.setDate(2, Date.valueOf(pedido.getFecha()));
            ps.setString(3, pedido.getClienteNombre());
            ps.setDouble(4, pedido.getTotal());
            ps.setString(5, pedido.getEstado());

            if (pedido.getEnvio() != null && pedido.getEnvio().getId() != null) {
                ps.setLong(6, pedido.getEnvio().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setLong(7, pedido.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarLogico(int id, Connection conn) throws SQLException {
        String sql = "UPDATE Pedido SET eliminado = 1 WHERE id = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // BONUS opcional: buscar por número
    public Pedido buscarPorNumero(String numero, Connection conn) throws SQLException {
        String sql = "SELECT id FROM Pedido WHERE numero = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return buscarPorId(id, conn);
                }
            }
        }
        return null;
    }
}
