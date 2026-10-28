package br.com.senseidev.dao;

import br.com.senseidev.model.MentorBusca;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Busca apenas perfis ativos, com filtros combináveis e parâmetros preparados. */
public class MentorBuscaDao {
    private final ServletContext contexto;

    public MentorBuscaDao(ServletContext contexto) { this.contexto = contexto; }

    public List<MentorBusca> buscar(Integer semestre, List<Integer> disciplinas, Double notaMinima)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT resultado.id, resultado.nome, resultado.descricao, resultado.experiencia, " +
                "resultado.nota_media, resultado.total_avaliacoes FROM (" +
                "SELECT u.id, u.nome, p.descricao, p.experiencia, " +
                "COALESCE((SELECT AVG(a.nota_final) FROM avaliacao a " +
                "JOIN mentoria m ON m.id = a.mentoria_id WHERE m.mentor_id = u.id), 0) AS nota_media, " +
                "(SELECT COUNT(*) FROM avaliacao a JOIN mentoria m ON m.id = a.mentoria_id " +
                "WHERE m.mentor_id = u.id) AS total_avaliacoes " +
                "FROM usuario u JOIN mentor_perfil p ON p.id = u.id " +
                "WHERE u.tipo = 'MENTOR' AND u.status = 'ATIVO'");

        if (semestre != null || !disciplinas.isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM mentor_disciplina md " +
                    "JOIN disciplina d ON d.id = md.disciplina_id WHERE md.mentor_id = u.id");
            if (semestre != null) sql.append(" AND d.semestre = ?");
            if (!disciplinas.isEmpty()) {
                sql.append(" AND d.id IN (");
                for (int i = 0; i < disciplinas.size(); i++) {
                    if (i > 0) sql.append(',');
                    sql.append('?');
                }
                sql.append(')');
            }
            sql.append(')');
        }
        sql.append(") resultado");
        if (notaMinima != null) sql.append(" WHERE resultado.nota_media >= ?");
        sql.append(" ORDER BY resultado.nota_media DESC, resultado.nome ASC FETCH FIRST 100 ROWS ONLY");

        List<MentorBusca> mentores = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int parametro = 1;
            if (semestre != null) ps.setInt(parametro++, semestre);
            for (Integer disciplina : disciplinas) ps.setInt(parametro++, disciplina);
            if (notaMinima != null) ps.setDouble(parametro, notaMinima);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mentores.add(new MentorBusca(rs.getInt("id"), rs.getString("nome"),
                            rs.getString("descricao"), rs.getString("experiencia"),
                            rs.getDouble("nota_media"), rs.getInt("total_avaliacoes")));
                }
            }
        }
        return mentores;
    }

}
