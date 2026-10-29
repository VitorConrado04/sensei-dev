package br.com.senseidev.dao;

import br.com.senseidev.model.MentoriaAluno;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Lista somente as mentorias pertencentes ao mentorando autenticado. */
public class MentoriasAlunoDao {
    private static final String COLUNAS = "SELECT m.id, m.mentor_id, u.nome AS mentor_nome, " +
            "u.status AS mentor_status, d.nome AS disciplina, m.objetivo, " +
            "m.data_hora_inicio, m.data_hora_fim, m.status, a.nota_final " +
            "FROM mentoria m JOIN usuario u ON u.id = m.mentor_id " +
            "JOIN disciplina d ON d.id = m.disciplina_id " +
            "LEFT JOIN avaliacao a ON a.mentoria_id = m.id ";
    private final ServletContext contexto;

    public MentoriasAlunoDao(ServletContext contexto) { this.contexto = contexto; }

    public List<MentoriaAluno> listar(int mentorandoId) throws SQLException {
        String sql = COLUNAS + "WHERE m.mentorando_id = ? ORDER BY m.data_hora_inicio";
        return consultar(sql, mentorandoId);
    }

    /** Resumo da página inicial: somente as três próximas sessões ainda ativas. */
    public List<MentoriaAluno> listarProximas(int mentorandoId) throws SQLException {
        String sql = COLUNAS + "WHERE m.mentorando_id = ? " +
                "AND m.status IN ('PENDENTE','CONFIRMADA') " +
                "AND m.data_hora_inicio > CURRENT_TIMESTAMP " +
                "ORDER BY m.data_hora_inicio, m.id FETCH FIRST 3 ROWS ONLY";
        return consultar(sql, mentorandoId);
    }

    private List<MentoriaAluno> consultar(String sql, int mentorandoId) throws SQLException {
        List<MentoriaAluno> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp inicio = rs.getTimestamp("data_hora_inicio");
                    Timestamp fim = rs.getTimestamp("data_hora_fim");
                    if (inicio == null || fim == null) continue;

                    MentoriaAluno mentoria = new MentoriaAluno(
                            rs.getInt("id"),
                            rs.getInt("mentor_id"),
                            rs.getString("mentor_nome"),
                            "ATIVO".equals(rs.getString("mentor_status")),
                            rs.getString("disciplina"),
                            rs.getString("objetivo"),
                            inicio.toLocalDateTime(),
                            fim.toLocalDateTime(),
                            rs.getString("status")
                    );

                    double nota = rs.getDouble("nota_final");
                    if (!rs.wasNull()) {
                        mentoria.setNota(nota);
                    }

                    lista.add(mentoria);
                }
            }
        }
        return lista;
    }
}