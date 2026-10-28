package br.com.senseidev.dao;

import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Cria pedidos pendentes a partir da disponibilidade semanal cadastrada. */
public class SolicitacaoMentoriaDao {
    private final ServletContext contexto;

    public SolicitacaoMentoriaDao(ServletContext contexto) { this.contexto = contexto; }

    public List<String> horariosCadastrados(int mentorId, LocalDate data, int duracaoMinutos)
            throws SQLException {
        try (Connection c = Conexao.abrir(contexto)) {
            return horariosCadastrados(c, mentorId, data, duracaoMinutos);
        }
    }

    List<String> horariosCadastrados(Connection c, int mentorId, LocalDate data,
                                             int duracaoMinutos) throws SQLException {
        List<String> opcoes = new ArrayList<>();
        if (data == null || data.isBefore(LocalDate.now())
                || (duracaoMinutos != 60 && duracaoMinutos != 120)
                || data.getDayOfWeek().getValue() > 6) return opcoes;

        String sql = "SELECT hora_inicio, hora_fim FROM disponibilidade " +
                "WHERE mentor_id = ? AND dia_semana = ? AND ativo = TRUE";
        Set<LocalTime> inicios = new HashSet<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            ps.setInt(2, data.getDayOfWeek().getValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        LocalTime inicio = LocalTime.parse(rs.getString("hora_inicio"));
                        LocalTime fim = LocalTime.parse(rs.getString("hora_fim"));
                        if (fim.equals(inicio.plusHours(1))) inicios.add(inicio);
                    } catch (RuntimeException e) {
                        // Ignora registros de horário inválidos, sem aceitar um intervalo incorreto.
                    }
                }
            }
        }
        for (int hora = 7; hora <= 18; hora++) {
            LocalTime inicio = LocalTime.of(hora, 0);
            if (LocalDateTime.of(data, inicio).isAfter(LocalDateTime.now())
                    && inicios.contains(inicio)
                    && (duracaoMinutos == 60 || inicios.contains(inicio.plusHours(1)))) {
                opcoes.add(inicio.toString());
            }
        }
        return opcoes;
    }

    /** Retorna false se mentor, disciplina ou horário deixaram de ser válidos. */
    public boolean solicitar(int mentorandoId, int mentorId, int disciplinaId,
                             LocalDateTime inicio, int duracaoMinutos, String objetivo)
            throws SQLException {
        LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);
        try (Connection c = Conexao.abrir(contexto)) {
            if (!horariosCadastrados(c, mentorId, inicio.toLocalDate(), duracaoMinutos)
                    .contains(inicio.toLocalTime().toString())) return false;
            String sql = "INSERT INTO mentoria (mentor_id, mentorando_id, disciplina_id, objetivo, " +
                    "data_hora_inicio, data_hora_fim, status) " +
                    "SELECT ?, ?, ?, ?, ?, ?, 'PENDENTE' WHERE " +
                    "EXISTS (SELECT 1 FROM usuario u JOIN mentor_perfil p ON p.id = u.id " +
                    "JOIN mentor_disciplina md ON md.mentor_id = u.id AND md.disciplina_id = ? " +
                    "WHERE u.id = ? AND u.tipo = 'MENTOR' AND u.status = 'ATIVO') " +
                    "AND EXISTS (SELECT 1 FROM usuario u JOIN mentorando_perfil p ON p.id = u.id " +
                    "WHERE u.id = ? AND u.tipo = 'MENTORANDO' AND u.status = 'ATIVO')";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, mentorId);
                ps.setInt(2, mentorandoId);
                ps.setInt(3, disciplinaId);
                ps.setString(4, objetivo);
                ps.setTimestamp(5, Timestamp.valueOf(inicio));
                ps.setTimestamp(6, Timestamp.valueOf(fim));
                ps.setInt(7, disciplinaId);
                ps.setInt(8, mentorId);
                ps.setInt(9, mentorandoId);
                return ps.executeUpdate() == 1;
            }
        }
    }
}
