package br.com.senseidev.dao;

import br.com.senseidev.model.Notificacao;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoDao {
    private static final DateTimeFormatter QUANDO = DateTimeFormatter.ofPattern("dd/MM 'às' HH:mm");
    private final ServletContext contexto;

    public NotificacaoDao(ServletContext contexto) { this.contexto = contexto; }

    /**
     * Cria um aviso sobre uma mentoria, usando a conexão que quem chamou já tem aberta
     * (assim, quando há transação, o aviso entra nela).
     * destino: "MENTOR" ou "MENTORANDO". O modelo aceita {nome} (a outra pessoa) e {quando}.
     */
    public static void avisar(Connection c, int mentoriaId, String destino, String tipo, String modelo)
            throws SQLException {
        String sqlBusca = "SELECT m.mentor_id, m.mentorando_id, m.data_hora_inicio, "
                + "um.nome AS mentor_nome, ua.nome AS aluno_nome "
                + "FROM mentoria m JOIN usuario um ON um.id = m.mentor_id "
                + "JOIN usuario ua ON ua.id = m.mentorando_id WHERE m.id = ?";
        int usuarioId;
        String nome;
        LocalDateTime inicio;
        try (PreparedStatement ps = c.prepareStatement(sqlBusca)) {
            ps.setInt(1, mentoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                Timestamp momento = rs.getTimestamp("data_hora_inicio");
                if (momento == null) return;
                inicio = momento.toLocalDateTime();
                if ("MENTOR".equals(destino)) {
                    usuarioId = rs.getInt("mentor_id");
                    nome = rs.getString("aluno_nome");
                } else {
                    usuarioId = rs.getInt("mentorando_id");
                    nome = rs.getString("mentor_nome");
                }
            }
        }
        String mensagem = modelo.replace("{nome}", nome).replace("{quando}", inicio.format(QUANDO));
        if (mensagem.length() > 500) mensagem = mensagem.substring(0, 500);

        String sqlInsere = "INSERT INTO notificacao (usuario_id, mentoria_id, tipo, mensagem) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sqlInsere)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, mentoriaId);
            ps.setString(3, tipo);
            ps.setString(4, mensagem);
            ps.executeUpdate();
        }
    }

    public List<Notificacao> listar(int usuarioId) throws SQLException {
        String sql = "SELECT id, mentoria_id, tipo, mensagem, lida, criado_em FROM notificacao "
                + "WHERE usuario_id = ? ORDER BY criado_em DESC, id DESC FETCH FIRST 100 ROWS ONLY";
        List<Notificacao> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mentoria = rs.getInt("mentoria_id");
                    Integer mentoriaId = rs.wasNull() ? null : mentoria;
                    Timestamp criado = rs.getTimestamp("criado_em");
                    lista.add(new Notificacao(rs.getInt("id"), mentoriaId, rs.getString("tipo"),
                            rs.getString("mensagem"), rs.getBoolean("lida"),
                            criado == null ? null : criado.toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    public int contarNaoLidas(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificacao WHERE usuario_id = ? AND lida = FALSE";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public void marcarComoLida(int usuarioId, int id) throws SQLException {
        executar("UPDATE notificacao SET lida = TRUE WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    public void marcarTodasComoLidas(int usuarioId) throws SQLException {
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE notificacao SET lida = TRUE WHERE usuario_id = ? AND lida = FALSE")) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        }
    }

    public void excluir(int usuarioId, int id) throws SQLException {
        executar("DELETE FROM notificacao WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    private void executar(String sql, int id, int usuarioId) throws SQLException {
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }
}