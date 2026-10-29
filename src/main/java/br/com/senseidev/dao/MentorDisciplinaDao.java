package br.com.senseidev.dao;

import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/** Relação entre mentor e as disciplinas que ele domina (tabela mentor_disciplina). */
public class MentorDisciplinaDao {
    private final ServletContext contexto;

    public MentorDisciplinaDao(ServletContext contexto) { this.contexto = contexto; }

    public Set<Integer> listarIds(int mentorId) throws SQLException {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT disciplina_id FROM mentor_disciplina WHERE mentor_id = ?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    /** Troca a seleção inteira; erro em qualquer ID desfaz todas as mudanças. */
    public void salvar(int mentorId, Set<Integer> disciplinaIds) throws SQLException {
        try (Connection c = Conexao.abrir(contexto)) {
            c.setAutoCommit(false);
            try (PreparedStatement remover = c.prepareStatement(
                    "DELETE FROM mentor_disciplina WHERE mentor_id = ?");
                 PreparedStatement adicionar = c.prepareStatement(
                         "INSERT INTO mentor_disciplina (mentor_id, disciplina_id) " +
                                 "SELECT ?, id FROM disciplina WHERE id = ?")) {
                remover.setInt(1, mentorId);
                remover.executeUpdate();
                for (int disciplinaId : disciplinaIds) {
                    adicionar.setInt(1, mentorId);
                    adicionar.setInt(2, disciplinaId);
                    if (adicionar.executeUpdate() != 1) throw new SQLException("Disciplina inexistente.");
                }
                c.commit();
            } catch (SQLException e) {
                try { c.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                throw e;
            }
        }
    }
}