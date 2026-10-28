package br.com.senseidev.dao;

import br.com.senseidev.model.UsuarioResumo;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class UsuarioDao {

    private final ServletContext contexto;

    // O servlet entrega o contexto; a classe Conexao usa ele para ler a URL do banco do web.xml
    public UsuarioDao(ServletContext contexto) {
        this.contexto = contexto;
    }

    // Lista todos os usuarios (admin, mentores e mentorandos) para o painel do admin
    public List<UsuarioResumo> listarUsuarios() throws SQLException {
        String sql = "SELECT id, nome, email, semestre_atual, foto_url, tipo, status FROM usuario ORDER BY id";
        List<UsuarioResumo> lista = new ArrayList<>();

        // try com parenteses: fecha a conexao, o statement e o resultado sozinho no fim
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    // Busca um usuario pelo id; devolve null se nao existir
    public UsuarioResumo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, email, semestre_atual, foto_url, tipo, status FROM usuario WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montar(rs);
                }
                return null;
            }
        }
    }

    // Atualiza nome, e-mail e semestre de um usuario. semestreAtual pode ser null (mentor e admin)
    public void atualizarUsuario(int id, String nome, String email, Integer semestreAtual) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, semestre_atual = ? WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            // semestre e INT e pode ser vazio: setNull manda NULL de verdade para o banco
            if (semestreAtual == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, semestreAtual);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    // Muda o status da conta: "ATIVO" ou "SUSPENSO". O login so aceita contas ATIVO
    public void alterarStatus(int id, String status) throws SQLException {
        String sql = "UPDATE usuario SET status = ? WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // Exclui o usuario. O perfil (mentor_perfil / mentorando_perfil) sai junto, por causa do
    // ON DELETE CASCADE. Se houver outros dados ligados (mentoria, material...), o banco recusa
    public void excluirUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Transforma a linha atual do resultado em um objeto UsuarioResumo
    private UsuarioResumo montar(ResultSet rs) throws SQLException {
        // semestre_atual pode ser NULL no banco; getInt devolveria 0, entao confere com wasNull
        int semestre = rs.getInt("semestre_atual");
        Integer semestreAtual = rs.wasNull() ? null : semestre;

        return new UsuarioResumo(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                semestreAtual,
                rs.getString("foto_url"),
                rs.getString("tipo"),
                rs.getString("status"));
    }
}