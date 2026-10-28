package br.com.senseidev.dao;

import br.com.senseidev.model.AvaliacaoPublica;
import br.com.senseidev.model.Disciplina;
import br.com.senseidev.model.HorarioGrade;
import br.com.senseidev.model.PerfilMentorPublico;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Consultas somente de leitura para o perfil de mentor visto pelo aluno. */
public class PerfilMentorPublicoDao {
    private final ServletContext contexto;

    public PerfilMentorPublicoDao(ServletContext contexto) { this.contexto = contexto; }

    public PerfilMentorPublico buscar(int id) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.semestre_atual, u.foto_url, p.descricao, p.experiencia, " +
                "COALESCE((SELECT AVG(a.nota_final) FROM avaliacao a " +
                "JOIN mentoria m ON m.id = a.mentoria_id WHERE m.mentor_id = u.id), 0) AS nota_media, " +
                "(SELECT COUNT(*) FROM avaliacao a JOIN mentoria m ON m.id = a.mentoria_id " +
                "WHERE m.mentor_id = u.id) AS total_avaliacoes, " +
                "(SELECT COUNT(*) FROM mentoria m WHERE m.mentor_id = u.id " +
                "AND m.status = 'REALIZADA') AS total_mentorias " +
                "FROM usuario u JOIN mentor_perfil p ON p.id = u.id " +
                "WHERE u.id = ? AND u.tipo = 'MENTOR' AND u.status = 'ATIVO'";
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int semestre = rs.getInt("semestre_atual");
                Integer semestreAtual = rs.wasNull() ? null : semestre;
                return new PerfilMentorPublico(rs.getInt("id"), rs.getString("nome"),
                        semestreAtual, rs.getString("foto_url"), rs.getString("descricao"),
                        rs.getString("experiencia"), rs.getDouble("nota_media"),
                        rs.getInt("total_avaliacoes"), rs.getInt("total_mentorias"));
            }
        }
    }

    public List<Disciplina> disciplinas(int mentorId) throws SQLException {
        String sql = "SELECT d.id, d.nome, d.semestre FROM mentor_disciplina md " +
                "JOIN disciplina d ON d.id = md.disciplina_id WHERE md.mentor_id = ? " +
                "ORDER BY d.semestre, d.nome";
        List<Disciplina> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new Disciplina(rs.getInt("id"),
                        rs.getString("nome"), rs.getInt("semestre")));
            }
        }
        return lista;
    }

    public List<AvaliacaoPublica> avaliacoes(int mentorId) throws SQLException {
        String sql = "SELECT d.nome AS disciplina, a.comentario, a.nota_final, a.criado_em " +
                "FROM avaliacao a JOIN mentoria m ON m.id = a.mentoria_id " +
                "JOIN disciplina d ON d.id = m.disciplina_id WHERE m.mentor_id = ? " +
                "ORDER BY a.criado_em DESC, a.id DESC FETCH FIRST 20 ROWS ONLY";
        List<AvaliacaoPublica> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp data = rs.getTimestamp("criado_em");
                    lista.add(new AvaliacaoPublica(rs.getString("disciplina"),
                            rs.getString("comentario"), rs.getDouble("nota_final"),
                            data == null ? null : data.toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    /** Grade semanal cadastrada; a checagem de uma data real pertence à solicitação. */
    public List<HorarioGrade> disponibilidadeSemanal(int mentorId) throws SQLException {
        String sql = "SELECT dia_semana, hora_inicio FROM disponibilidade " +
                "WHERE mentor_id = ? AND ativo = TRUE";
        Set<String> ativos = new HashSet<>();
        try (Connection c = Conexao.abrir(contexto);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dia = rs.getInt("dia_semana");
                    try {
                        int hora = LocalTime.parse(rs.getString("hora_inicio")).getHour();
                        if (dia >= 1 && dia <= 6 && hora >= 7 && hora <= 18) ativos.add(hora + ":" + dia);
                    } catch (DateTimeParseException | NullPointerException e) {
                        // Um horário inválido não impede a leitura dos demais.
                    }
                }
            }
        }
        List<HorarioGrade> grade = new ArrayList<>();
        for (int hora = 7; hora <= 18; hora++) {
            String[] dias = new String[6];
            for (int dia = 1; dia <= 6; dia++) dias[dia - 1] = ativos.contains(hora + ":" + dia) ? "D" : "X";
            grade.add(new HorarioGrade(hora, dias));
        }
        return grade;
    }
}
