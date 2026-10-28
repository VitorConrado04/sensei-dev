package br.com.senseidev.dao;

import br.com.senseidev.model.Disciplina;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaDao {

    private final ServletContext contexto;

    public DisciplinaDao(ServletContext contexto) {
        this.contexto = contexto;
    }

    // READ: lista todas, em ordem de semestre e depois de nome
    public List<Disciplina> listarDisciplinas() throws SQLException {
        String sql = "SELECT id, nome, semestre FROM disciplina ORDER BY semestre, nome";
        List<Disciplina> lista = new ArrayList<>();

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Disciplina(rs.getInt("id"), rs.getString("nome"), rs.getInt("semestre")));
            }
        }
        return lista;
    }

    // READ: busca uma pela id (para preencher o formulario de edicao); null se nao existir
    public Disciplina buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, semestre FROM disciplina WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Disciplina(rs.getInt("id"), rs.getString("nome"), rs.getInt("semestre"));
                }
                return null;
            }
        }
    }

    // Ve se ja existe uma disciplina com esse nome (sem diferenciar maiuscula de minuscula).
    // idIgnorado: ao editar, a propria disciplina nao conta como repetida (ao criar, passar 0)
    public boolean nomeExiste(String nome, int idIgnorado) throws SQLException {
        String sql = "SELECT 1 FROM disciplina WHERE LOWER(nome) = LOWER(?) AND id <> ? FETCH FIRST 1 ROW ONLY";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, idIgnorado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // CREATE
    public void criarDisciplina(String nome, int semestre) throws SQLException {
        String sql = "INSERT INTO disciplina (nome, semestre) VALUES (?, ?)";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, semestre);
            ps.executeUpdate();
        }
    }

    // UPDATE: so a disciplina com esse id muda
    public void atualizarDisciplina(int id, String nome, int semestre) throws SQLException {
        String sql = "UPDATE disciplina SET nome = ?, semestre = ? WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, semestre);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    // DELETE: o banco recusa se a disciplina ja estiver ligada a mentor, aluno ou mentoria
    public void excluirDisciplina(int id) throws SQLException {
        String sql = "DELETE FROM disciplina WHERE id = ?";

        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}