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
    private final ServletContext contexto;

    public MentoriasAlunoDao(ServletContext contexto) { this.contexto = contexto; }

    public List<MentoriaAluno> listar(int mentorandoId) throws SQLException {
        String sql = "SELECT m.id, m.mentor_id, u.nome AS mentor_nome, u.status AS mentor_status, " +
                "d.nome AS disciplina, m.objetivo, m.data_hora_inicio, m.data_hora_fim, m.status " +
                "FROM mentoria m JOIN usuario u ON u.id = m.mentor_id " +
                "JOIN disciplina d ON d.id = m.disciplina_id " +
                "WHERE m.mentorando_id = ? ORDER BY m.data_hora_inicio";
        List<MentoriaAluno> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp inicio = rs.getTimestamp("data_hora_inicio");
                    Timestamp fim = rs.getTimestamp("data_hora_fim");
                    if (inicio == null || fim == null) continue;
                    lista.add(new MentoriaAluno(rs.getInt("id"), rs.getInt("mentor_id"),
                            rs.getString("mentor_nome"), "ATIVO".equals(rs.getString("mentor_status")),
                            rs.getString("disciplina"), rs.getString("objetivo"),
                            inicio.toLocalDateTime(), fim.toLocalDateTime(), rs.getString("status")));
                }
            }
        }
        return lista;
    }
}
