package dao.impl;

import dao.EnvioDAO;
import entities.Envio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnvioDAOImpl implements EnvioDAO {

    @Override
    public Integer crear(Envio envio, Connection conn) throws SQLException {
        // NO seteamos "eliminado" acá: en SQL Server tiene DEFAULT 0
        String sql = "INSERT INTO Envio (tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, envio.getTracking());
            ps.setString(2, envio.getEmpresa());
            ps.setString(3, envio.getTipo());

            if (envio.getCosto() == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setDouble(4, envio.getCosto());
            }

            if (envio.getFechaDespacho() == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(envio.getFechaDespacho()));
            }

            if (envio.getFechaEstimada() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(envio.getFechaEstimada()));
            }

            ps.setString(7, envio.getEstado());

            int filas = ps.executeUpdate();
            if (filas == 0) return null;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    envio.setId((long) id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public Envio buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT id, eliminado, tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado " +
                     "FROM Envio WHERE id = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Envio envio = new Envio();
                envio.setId(rs.getLong("id"));
                envio.setEliminado(rs.getBoolean("eliminado"));
                envio.setTracking(rs.getString("tracking"));
                envio.setEmpresa(rs.getString("empresa"));
                envio.setTipo(rs.getString("tipo"));

                double costo = rs.getDouble("costo");
                if (rs.wasNull()) envio.setCosto(null); else envio.setCosto(costo);

                Date fd = rs.getDate("fechaDespacho");
                envio.setFechaDespacho(fd != null ? fd.toLocalDate() : null);

                Date fe = rs.getDate("fechaEstimada");
                envio.setFechaEstimada(fe != null ? fe.toLocalDate() : null);

                envio.setEstado(rs.getString("estado"));
                return envio;
            }
        }
    }

    @Override
    public List<Envio> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT id, eliminado, tracking, empresa, tipo, costo, fechaDespacho, fechaEstimada, estado " +
                     "FROM Envio WHERE eliminado = 0 ORDER BY id";
        List<Envio> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Envio envio = new Envio();
                envio.setId(rs.getLong("id"));
                envio.setEliminado(rs.getBoolean("eliminado"));
                envio.setTracking(rs.getString("tracking"));
                envio.setEmpresa(rs.getString("empresa"));
                envio.setTipo(rs.getString("tipo"));

                double costo = rs.getDouble("costo");
                if (rs.wasNull()) envio.setCosto(null); else envio.setCosto(costo);

                Date fd = rs.getDate("fechaDespacho");
                envio.setFechaDespacho(fd != null ? fd.toLocalDate() : null);

                Date fe = rs.getDate("fechaEstimada");
                envio.setFechaEstimada(fe != null ? fe.toLocalDate() : null);

                envio.setEstado(rs.getString("estado"));
                lista.add(envio);
            }
        }
        return lista;
    }

    @Override
    public boolean actualizar(Envio envio, Connection conn) throws SQLException {
        String sql = "UPDATE Envio SET tracking=?, empresa=?, tipo=?, costo=?, fechaDespacho=?, fechaEstimada=?, estado=? " +
                     "WHERE id = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, envio.getTracking());
            ps.setString(2, envio.getEmpresa());
            ps.setString(3, envio.getTipo());

            if (envio.getCosto() == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setDouble(4, envio.getCosto());
            }

            if (envio.getFechaDespacho() == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(envio.getFechaDespacho()));
            }

            if (envio.getFechaEstimada() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(envio.getFechaEstimada()));
            }

            ps.setString(7, envio.getEstado());
            ps.setLong(8, envio.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarLogico(int id, Connection conn) throws SQLException {
        String sql = "UPDATE Envio SET eliminado = 1 WHERE id = ? AND eliminado = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
