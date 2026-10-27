package br.com.senseidev.util;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Centraliza a conexão com o H2 usando os parâmetros do WEB-INF/web.xml. */
public final class Conexao {
    private static final String URL_PADRAO = "jdbc:h2:./db/sensei";
    private static final String USUARIO_PADRAO = "sa";
    private static final String SENHA_PADRAO = "sa";

    private Conexao() { }

    public static Connection abrir(ServletContext contexto) throws SQLException {
        String url = parametro(contexto, "db.url", URL_PADRAO);
        String usuario = parametro(contexto, "db.user", USUARIO_PADRAO);
        String senha = parametro(contexto, "db.password", SENHA_PADRAO);
        carregarDriver();
        return DriverManager.getConnection(url, usuario, senha);
    }

    /** Mantido para ferramentas simples e testes fora do ciclo de vida do Servlet. */
    public static Connection abrir() throws SQLException {
        carregarDriver();
        return DriverManager.getConnection(URL_PADRAO, USUARIO_PADRAO, SENHA_PADRAO);
    }

    private static String parametro(ServletContext contexto, String nome, String padrao) {
        if (contexto == null) return padrao;
        String valor = contexto.getInitParameter(nome);
        return valor == null || valor.trim().isEmpty() ? padrao : valor.trim();
    }

    private static void carregarDriver() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do H2 não foi encontrado no classpath.", e);
        }
    }
}