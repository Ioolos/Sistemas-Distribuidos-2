package com.banco.microservicios;

import java.sql.*;

public class BaseDatos {
    private static final String BD = "jdbc:sqlite:Banco.db"; // Debes colocar el archivo auth.db en la raíz del proyecto o indicar ruta absoluta


    public static boolean validarCuenta(String numero, int nip) {
        String sql = "SELECT * FROM cuentas WHERE numero = ? AND nip = ?";
        try (Connection conn = DriverManager.getConnection(BD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.setInt(2, nip);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true si encuentra una coincidencia
        } catch (SQLException e) {
            System.out.println("Error en la base de datos: " + e.getMessage());
            return false;
        }
    }
    
    public static double consultarSaldo(String cuenta) {
        String sql = "SELECT saldo FROM Cuentas WHERE numero = ?";
        try (Connection conn = DriverManager.getConnection(BD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cuenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("saldo");
            } else {
                return -1; // Cuenta no encontrada
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar saldo: " + e.getMessage());
            return -1;
        }
    }

    public static boolean actualizarSaldo(String cuenta, double nuevoSaldo) {
        String sql = "UPDATE Cuentas SET saldo = ? WHERE numero = ?";
        try (Connection conn = DriverManager.getConnection(BD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoSaldo);
            stmt.setString(2, cuenta);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar saldo: " + e.getMessage());
            return false;
        }
    }
    
    public static void registrarMovimiento(String cuenta, String tipo, double monto) {
        String sql = "INSERT INTO Movimientos (cuenta, tipo, monto, fecha) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(BD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cuenta);
            stmt.setString(2, tipo);
            stmt.setDouble(3, monto);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar movimiento: " + e.getMessage());
        }
    }

    public static void registrarTransferencia(String origen, String destino, double monto) {
        String sql = "INSERT INTO Transferencias (cuenta_origen, cuenta_destino, monto, fecha) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = DriverManager.getConnection(BD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, origen);
            stmt.setString(2, destino);
            stmt.setDouble(3, monto);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar transferencia: " + e.getMessage());
        }
    }
}
