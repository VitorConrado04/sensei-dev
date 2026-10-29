package br.com.senseidev.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Regras compartilhadas: um horário do mentor pode estar ocupado por outra sessão ou bloqueado. */
final class ConflitoHorario {
    private ConflitoHorario() { }

    /** Serializa alterações na agenda do mesmo mentor dentro da transação. */
    static boolean travarMentor(Connection c, int mentorId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM mentor_perfil WHERE id=? FOR UPDATE")) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Existe outra sessão PENDENTE/CONFIRMADA do mentor que se sobrepõe a [inicio, fim)? */
    static boolean ocupado(Connection c, int mentorId, int ignorarMentoriaId,
                           LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        String sql = "SELECT 1 FROM mentoria WHERE mentor_id=? AND id<>? " +
                "AND status IN ('PENDENTE','CONFIRMADA') " +
                "AND data_hora_inicio < ? AND data_hora_fim > ? FETCH FIRST 1 ROW ONLY";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            ps.setInt(2, ignorarMentoriaId);
            ps.setTimestamp(3, Timestamp.valueOf(fim));
            ps.setTimestamp(4, Timestamp.valueOf(inicio));
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** O mentor bloqueou (dia inteiro ou parte do dia) algo que se sobrepõe a [inicio, fim)? */
    static boolean bloqueado(Connection c, int mentorId, LocalDateTime inicio,
                             LocalDateTime fim) throws SQLException {
        String sql = "SELECT hora_inicio, hora_fim FROM bloqueio_horario WHERE mentor_id=? AND data=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            ps.setDate(2, java.sql.Date.valueOf(inicio.toLocalDate()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String de = rs.getString("hora_inicio");
                    String ate = rs.getString("hora_fim");
                    if (de == null || ate == null || de.trim().isEmpty() || ate.trim().isEmpty()) return true;
                    try {
                        LocalDateTime bloqueioInicio = LocalDateTime.of(inicio.toLocalDate(), LocalTime.parse(de));
                        LocalDateTime bloqueioFim = LocalDateTime.of(inicio.toLocalDate(), LocalTime.parse(ate));
                        if (bloqueioInicio.isBefore(fim) && bloqueioFim.isAfter(inicio)) return true;
                    } catch (RuntimeException e) {
                        return true;   // registro de horário inválido: por segurança, considera bloqueado
                    }
                }
            }
        }
        return false;
    }
}
