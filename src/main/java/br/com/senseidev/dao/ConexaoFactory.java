package br.com.senseidev.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {

    private static final String URL = "jdbc:h2:./db/sensei";
    private static final String USUARIO = "sa";
    private static final String SENHA = "sa";

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do H2 não encontrado", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}