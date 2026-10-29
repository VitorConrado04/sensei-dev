package br.com.senseidev.dao;

import br.com.senseidev.model.MentoriaAluno;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Avaliação que o mentorando faz de uma sessão já realizada. */
public class AvaliacaoDao {
    private final ServletContext contexto;

    public AvaliacaoDao(ServletContext contexto) { this.contexto = contexto; }

    /** Dados da sessão para o formulário. Devolve null se a sessão não é do aluno,
     *  se ainda não foi realizada ou se já foi avaliada. */
    public MentoriaAluno buscarAvaliavel(int mentorandoId, int mentoriaId) throws SQLException {
        String sql = "SELECT m.id, m.mentor_id, u.nome AS mentor_nome, u.status AS mentor_status, "
                + "d.nome AS disciplina, m.objetivo, m.data_hora_inicio, m.data_hora_fim, m.status "
                + "FROM mentoria m JOIN usuario u ON u.id = m.mentor_id "
                + "JOIN disciplina d ON d.id = m.disciplina_id "
                + "WHERE m.id = ? AND m.mentorando_id = ? AND m.status = 'REALIZADA' "
                + "AND NOT EXISTS (SELECT 1 FROM avaliacao a WHERE a.mentoria_id = m.id)";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentoriaId);
            ps.setInt(2, mentorandoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Timestamp inicio = rs.getTimestamp("data_hora_inicio");
                Timestamp fim = rs.getTimestamp("data_hora_fim");
                if (inicio == null || fim == null) return null;
                return new MentoriaAluno(rs.getInt("id"), rs.getInt("mentor_id"),
                        rs.getString("mentor_nome"), "ATIVO".equals(rs.getString("mentor_status")),
                        rs.getString("disciplina"), rs.getString("objetivo"),
                        inicio.toLocalDateTime(), fim.toLocalDateTime(), rs.getString("status"));
            }
        }
    }

    /** Grava a avaliação. As notas vêm na ordem: clareza, domínio, pontualidade, orientar, geral.
     *  Devolve false se a sessão não é do aluno ou não foi realizada. Se já existir avaliação
     *  para a sessão, o banco recusa (SQLException com código 23505). */
    public boolean registrar(int mentorandoId, int mentoriaId, int[] notas, String comentario)
            throws SQLException {
        String sqlConfere = "SELECT 1 FROM mentoria WHERE id = ? AND mentorando_id = ? AND status = 'REALIZADA'";
        String sqlGrava = "INSERT INTO avaliacao (mentoria_id, clareza, dominio_assunto, pontualidade, "
                + "capacidade_orientar, experiencia_geral, nota_final, comentario) VALUES (?,?,?,?,?,?,?,?)";

        int soma = 0;
        for (int nota : notas) soma += nota;
        BigDecimal notaFinal = BigDecimal.valueOf(soma)
                .divide(BigDecimal.valueOf(notas.length), 2, RoundingMode.HALF_UP);

        try (Connection c = Conexao.abrir(contexto)) {
            try (PreparedStatement ps = c.prepareStatement(sqlConfere)) {
                ps.setInt(1, mentoriaId);
                ps.setInt(2, mentorandoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(sqlGrava)) {
                ps.setInt(1, mentoriaId);
                for (int i = 0; i < notas.length; i++) ps.setInt(i + 2, notas[i]);
                ps.setBigDecimal(7, notaFinal);
                ps.setString(8, comentario);
                ps.executeUpdate();
            }

            NotificacaoDao.avisar(c, mentoriaId, "MENTOR", "AVALIACAO",
                    "{nome} avaliou a sessão de {quando}.");

            return true;
        }
    }
}