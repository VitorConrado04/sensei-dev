package br.com.senseidev.dao;

import br.com.senseidev.model.MentoriaAluno;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Cancela ou reagenda apenas mentorias futuras do mentorando dono da sessão. */
public class AlteracaoMentoriaDao {
    private final ServletContext contexto;

    public AlteracaoMentoriaDao(ServletContext contexto) { this.contexto = contexto; }

    public boolean cancelar(int mentorandoId, int mentoriaId) throws SQLException {
        String sql = "UPDATE mentoria SET status='CANCELADA', atualizado_em=CURRENT_TIMESTAMP " +
                "WHERE id=? AND mentorando_id=? AND status IN ('PENDENTE','CONFIRMADA') " +
                "AND data_hora_inicio > CURRENT_TIMESTAMP";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentoriaId);
            ps.setInt(2, mentorandoId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Devolve a mentoria somente se ainda puder ser alterada pelo próprio mentorando. */
    public MentoriaAluno buscarAlteravel(int mentorandoId, int mentoriaId) throws SQLException {
        for (MentoriaAluno mentoria : new MentoriasAlunoDao(contexto).listar(mentorandoId)) {
            if (mentoria.getId() == mentoriaId && mentoria.isPodeAlterar()) return mentoria;
        }
        return null;
    }

    public List<String> horariosParaReagendar(int mentorandoId, int mentoriaId,
                                               LocalDate data, int duracaoMinutos) throws SQLException {
        List<String> livres = new ArrayList<>();
        MentoriaAluno mentoria = buscarAlteravel(mentorandoId, mentoriaId);
        if (mentoria == null || !mentoria.isMentorAtivo() || data == null) return livres;
        try (Connection c = Conexao.abrir(contexto)) {
            for (String hora : new SolicitacaoMentoriaDao(contexto).horariosCadastrados(
                    c, mentoria.getMentorId(), data, duracaoMinutos, mentoriaId)) {
                LocalDateTime inicio = LocalDateTime.of(data, LocalTime.parse(hora));
                LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);
                if (inicio.equals(mentoria.getInicio()) && fim.equals(mentoria.getFim())) continue;
                if (!horarioOcupado(c, mentoria.getMentorId(), mentoriaId, inicio, fim)
                        && !horarioBloqueado(c, mentoria.getMentorId(), inicio, fim)) livres.add(hora);
            }
        }
        return livres;
    }

    /** Mantém a sessão antiga no histórico e cria uma nova solicitação pendente. */
    public boolean reagendar(int mentorandoId, int mentoriaId, LocalDateTime novoInicio,
                             int duracaoMinutos) throws SQLException {
        if (novoInicio == null || !novoInicio.isAfter(LocalDateTime.now())
                || (duracaoMinutos != 60 && duracaoMinutos != 120)) return false;
        try (Connection c = Conexao.abrir(contexto)) {
            c.setAutoCommit(false);
            try {
                int mentorId;
                int disciplinaId;
                String objetivo;
                LocalDateTime inicioAntigo;
                LocalDateTime fimAntigo;
                String sqlBusca = "SELECT mentor_id, disciplina_id, objetivo, data_hora_inicio, " +
                        "data_hora_fim FROM mentoria WHERE id=? AND mentorando_id=? " +
                        "AND status IN ('PENDENTE','CONFIRMADA') " +
                        "AND data_hora_inicio > CURRENT_TIMESTAMP FOR UPDATE";
                try (PreparedStatement ps = c.prepareStatement(sqlBusca)) {
                    ps.setInt(1, mentoriaId);
                    ps.setInt(2, mentorandoId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return false;
                        mentorId = rs.getInt("mentor_id");
                        disciplinaId = rs.getInt("disciplina_id");
                        objetivo = rs.getString("objetivo");
                        inicioAntigo = rs.getTimestamp("data_hora_inicio").toLocalDateTime();
                        fimAntigo = rs.getTimestamp("data_hora_fim").toLocalDateTime();
                    }
                }
                LocalDateTime novoFim = novoInicio.plusMinutes(duracaoMinutos);
                if (novoInicio.equals(inicioAntigo) && novoFim.equals(fimAntigo)) return false;
                List<String> horarios = new SolicitacaoMentoriaDao(contexto).horariosCadastrados(
                        c, mentorId, novoInicio.toLocalDate(), duracaoMinutos, mentoriaId);
                if (!horarios.contains(novoInicio.toLocalTime().toString())
                        || !mentorAtivo(c, mentorId, disciplinaId)
                        || horarioOcupado(c, mentorId, mentoriaId, novoInicio, novoFim)
                        || horarioBloqueado(c, mentorId, novoInicio, novoFim)) return false;

                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE mentoria SET status='CANCELADA', atualizado_em=CURRENT_TIMESTAMP " +
                                "WHERE id=? AND mentorando_id=? AND status IN ('PENDENTE','CONFIRMADA')")) {
                    ps.setInt(1, mentoriaId);
                    ps.setInt(2, mentorandoId);
                    if (ps.executeUpdate() != 1) return false;
                }
                String sqlNovo = "INSERT INTO mentoria (mentor_id, mentorando_id, disciplina_id, " +
                        "mentoria_origem_id, objetivo, data_hora_inicio, data_hora_fim, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDENTE')";
                try (PreparedStatement ps = c.prepareStatement(sqlNovo)) {
                    ps.setInt(1, mentorId);
                    ps.setInt(2, mentorandoId);
                    ps.setInt(3, disciplinaId);
                    ps.setInt(4, mentoriaId);
                    ps.setString(5, objetivo);
                    ps.setTimestamp(6, Timestamp.valueOf(novoInicio));
                    ps.setTimestamp(7, Timestamp.valueOf(novoFim));
                    ps.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private boolean mentorAtivo(Connection c, int mentorId, int disciplinaId) throws SQLException {
        String sql = "SELECT 1 FROM usuario u JOIN mentor_perfil p ON p.id=u.id " +
                "JOIN mentor_disciplina md ON md.mentor_id=u.id AND md.disciplina_id=? " +
                "WHERE u.id=? AND u.tipo='MENTOR' AND u.status='ATIVO'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, disciplinaId);
            ps.setInt(2, mentorId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private boolean horarioOcupado(Connection c, int mentorId, int mentoriaId,
                                   LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        return ConflitoHorario.ocupado(c, mentorId, mentoriaId, inicio, fim);
    }

    private boolean horarioBloqueado(Connection c, int mentorId, LocalDateTime inicio,
                                     LocalDateTime fim) throws SQLException {
        return ConflitoHorario.bloqueado(c, mentorId, inicio, fim);
    }
}
