package br.com.senseidev.dao;

import br.com.senseidev.model.MentorFavorito;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Favoritos sempre vinculados ao mentorando autenticado. */
public class FavoritoDao {
    private final ServletContext contexto;

    public FavoritoDao(ServletContext contexto) { this.contexto = contexto; }

    public List<MentorFavorito> listar(int mentorandoId) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.foto_url, u.status FROM favorito f " +
                "JOIN usuario u ON u.id = f.mentor_id WHERE f.mentorando_id = ? " +
                "AND u.tipo = 'MENTOR' ORDER BY f.criado_em DESC, u.nome";
        List<MentorFavorito> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new MentorFavorito(rs.getInt("id"),
                        rs.getString("nome"), rs.getString("foto_url"),
                        "ATIVO".equals(rs.getString("status"))));
            }
        }
        return lista;
    }

    public boolean eFavorito(int mentorandoId, int mentorId) throws SQLException {
        String sql = "SELECT 1 FROM favorito WHERE mentorando_id = ? AND mentor_id = ?";
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            ps.setInt(2, mentorId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void adicionar(int mentorandoId, int mentorId) throws SQLException {
        String sql = "INSERT INTO favorito (mentorando_id, mentor_id) " +
                "SELECT ?, u.id FROM usuario u JOIN mentor_perfil p ON p.id = u.id " +
                "WHERE u.id = ? AND u.tipo = 'MENTOR' AND u.status = 'ATIVO' " +
                "AND NOT EXISTS (SELECT 1 FROM favorito f WHERE f.mentorando_id = ? AND f.mentor_id = u.id)";
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            ps.setInt(2, mentorId);
            ps.setInt(3, mentorandoId);
            try {
                ps.executeUpdate();
            } catch (SQLException e) {
                // Dois pedidos simultâneos podem tentar inserir o mesmo favorito.
                if (!"23505".equals(e.getSQLState())) throw e;
            }
        }
    }

    public void remover(int mentorandoId, int mentorId) throws SQLException {
        String sql = "DELETE FROM favorito WHERE mentorando_id = ? AND mentor_id = ?";
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            ps.setInt(2, mentorId);
            ps.executeUpdate();
        }
    }
}
