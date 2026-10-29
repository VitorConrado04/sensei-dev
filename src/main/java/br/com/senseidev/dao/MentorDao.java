package br.com.senseidev.dao;

import br.com.senseidev.auth.SenhaUtil;
import br.com.senseidev.model.Avaliacao;
import br.com.senseidev.model.Bloqueio;
import br.com.senseidev.model.HorarioGrade;
import br.com.senseidev.model.HorarioSemanal;
import br.com.senseidev.model.Material;
import br.com.senseidev.model.Mentor;
import br.com.senseidev.model.Sessao;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Acesso a dados da área do mentor. Nenhum dado de demonstração é criado aqui. */
public class MentorDao {
    private static final int PRIMEIRA_HORA = 7;
    private static final int ULTIMA_HORA = 18;
    private final ServletContext contexto;

    public MentorDao(ServletContext contexto) {
        this.contexto = contexto;
    }

    public boolean emailCadastrado(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = LOWER(?) FETCH FIRST 1 ROW ONLY";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Registra usuário e perfil de mentor de forma atômica. */
    public int cadastrarMentor(String nome, String email, String senhaHash,
                               Integer semestreAtual, String fotoUrl) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nome, email, senha, semestre_atual, foto_url, status, tipo) "
                + "VALUES (?, ?, ?, ?, ?, 'ATIVO', 'MENTOR')";
        String sqlPerfil = "INSERT INTO mentor_perfil (id, descricao, experiencia, nota_media, total_mentorias) "
                + "VALUES (?, '', '', 0, 0)";
        try (Connection c = Conexao.abrir(contexto)) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nome);
                    ps.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));
                    ps.setString(3, senhaHash);
                    if (semestreAtual == null) ps.setNull(4, java.sql.Types.INTEGER);
                    else ps.setInt(4, semestreAtual);
                    ps.setString(5, fotoUrl);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("O banco não retornou o ID do novo usuário.");
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(sqlPerfil)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        }
    }

    /**
     * Registra um mentorando e seu perfil na mesma transação.
     * A conta só passa a existir se as duas inserções forem concluídas.
     */
    public int cadastrarMentorando(String nome, String email, String senhaHash,
                                   Integer semestreAtual, String fotoUrl) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nome, email, senha, semestre_atual, foto_url, status , tipo) "
                + "VALUES (?, ?, ?, ?, ?, 'ATIVO', 'MENTORANDO')";
        String sqlPerfil = "INSERT INTO mentorando_perfil (id) VALUES (?)";

        try (Connection c = Conexao.abrir(contexto)) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nome.trim());
                    ps.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));
                    ps.setString(3, senhaHash);
                    if (semestreAtual == null) ps.setNull(4, java.sql.Types.INTEGER);
                    else ps.setInt(4, semestreAtual);
                    ps.setString(5, fotoUrl);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("O banco não retornou o ID do novo usuário.");
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(sqlPerfil)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        }
    }

    /**
     * Autentica qualquer tipo de conta que tenha um perfil válido.
     * Senhas legadas em texto puro são migradas para PBKDF2 após o primeiro login válido.
     */
    public br.com.senseidev.model.UsuarioAutenticado autenticarUsuario(String email, String senha)
            throws SQLException {
        String sql = "SELECT u.id, u.nome, u.senha, "
                + "CASE WHEN u.tipo = 'ADMIN' THEN 'ADMIN' "
                + "WHEN mp.id IS NOT NULL THEN 'MENTOR' "
                + "WHEN ap.id IS NOT NULL THEN 'MENTORANDO' ELSE NULL END AS tipo "
                + "FROM usuario u "
                + "LEFT JOIN mentor_perfil mp ON mp.id = u.id "
                + "LEFT JOIN mentorando_perfil ap ON ap.id = u.id "
                + "WHERE LOWER(u.email) = LOWER(?) AND UPPER(u.status) = 'ATIVO' "
                + "FETCH FIRST 1 ROW ONLY";

        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String tipo = rs.getString("tipo");
                String armazenada = rs.getString("senha");
                String nome = rs.getString("nome");
                if (tipo == null || !SenhaUtil.verificar(senha, armazenada)) return null;

                int id = rs.getInt("id");
                if (SenhaUtil.precisaAtualizar(armazenada)) {
                    String hashNovo = SenhaUtil.gerarHash(senha);
                    try (PreparedStatement update = c.prepareStatement(
                            "UPDATE usuario SET senha = ? WHERE id = ? AND senha = ?")) {
                        update.setString(1, hashNovo);
                        update.setInt(2, id);
                        update.setString(3, armazenada);
                        update.executeUpdate();
                    }
                }
                return new br.com.senseidev.model.UsuarioAutenticado(id, nome, tipo);
            }
        }
    }

    /** Retorna o mentor se as credenciais forem válidas; caso contrário, null. */
    public Mentor autenticarMentor(String email, String senha) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.email, u.senha, u.semestre_atual, u.foto_url, u.status, u.criado_em, "
                + "p.descricao, p.experiencia, "
                + "COALESCE((SELECT AVG(a.nota_final) FROM avaliacao a JOIN mentoria ma ON ma.id=a.mentoria_id "
                + "WHERE ma.mentor_id=p.id), p.nota_media, 0) AS nota_media, "
                + "(SELECT COUNT(*) FROM mentoria mt WHERE mt.mentor_id=p.id AND mt.status='REALIZADA') AS total_mentorias "
                + "FROM usuario u JOIN mentor_perfil p ON p.id=u.id "
                + "WHERE LOWER(u.email)=LOWER(?) AND UPPER(u.status)='ATIVO' FETCH FIRST 1 ROW ONLY";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String armazenada = rs.getString("senha");
                if (!SenhaUtil.verificar(senha, armazenada)) return null;
                int id = rs.getInt("id");
                Mentor mentor = mapearMentor(rs, armazenada);
                if (SenhaUtil.precisaAtualizar(armazenada)) {
                    String hashNovo = SenhaUtil.gerarHash(senha);
                    try (PreparedStatement update = c.prepareStatement(
                            "UPDATE usuario SET senha=? WHERE id=? AND senha=?")) {
                        update.setString(1, hashNovo);
                        update.setInt(2, id);
                        update.setString(3, armazenada);
                        update.executeUpdate();
                    }
                    mentor.setSenhaHash(hashNovo);
                }
                return mentor;
            }
        }
    }

    public Mentor buscarMentor(int mentorId) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.email, u.senha, u.semestre_atual, u.foto_url, u.status, u.criado_em, "
                + "p.descricao, p.experiencia, "
                + "COALESCE((SELECT AVG(a.nota_final) FROM avaliacao a JOIN mentoria ma ON ma.id=a.mentoria_id "
                + "WHERE ma.mentor_id=p.id), p.nota_media, 0) AS nota_media, "
                + "(SELECT COUNT(*) FROM mentoria mt WHERE mt.mentor_id=p.id AND mt.status='REALIZADA') AS total_mentorias "
                + "FROM usuario u JOIN mentor_perfil p ON p.id=u.id WHERE u.id=?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapearMentor(rs, rs.getString("senha")) : null; }
        }
    }

    private Mentor mapearMentor(ResultSet rs, String senhaHash) throws SQLException {
        int semestreValor = rs.getInt("semestre_atual");
        Integer semestre = rs.wasNull() ? null : semestreValor;
        Mentor m = new Mentor(rs.getString("nome"), rs.getString("email"), senhaHash,
                semestre, rs.getString("foto_url"), rs.getString("descricao"), rs.getString("experiencia"));
        m.setId(rs.getInt("id"));
        m.setStatus(rs.getString("status"));
        Timestamp criadoEm = rs.getTimestamp("criado_em");
        if (criadoEm != null) m.setCriadoEm(criadoEm.toLocalDateTime());
        m.setNotaMedia(rs.getDouble("nota_media"));
        m.setTotalMentorias(rs.getInt("total_mentorias"));
        return m;
    }

    public List<String> listarDisciplinas(int mentorId) throws SQLException {
        String sql = "SELECT d.nome FROM mentor_disciplina md JOIN disciplina d ON d.id=md.disciplina_id "
                + "WHERE md.mentor_id=? ORDER BY d.nome";
        List<String> resultado = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) resultado.add(rs.getString(1)); }
        }
        return resultado;
    }

    public List<HorarioGrade> buscarGrade(int mentorId) throws SQLException {
        Set<String> disponiveis = new HashSet<>();
        String sqlDisp = "SELECT dia_semana, hora_inicio FROM disponibilidade WHERE mentor_id=? AND ativo=TRUE";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sqlDisp)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalTime hora = parseHora(rs.getString("hora_inicio"));
                    if (hora != null) disponiveis.add(chaveSlot(hora.getHour(), rs.getInt("dia_semana") - 1));
                }
            }
        }
        Set<String> agendados = buscarSlotsAgendados(mentorId);
        List<HorarioGrade> grade = new ArrayList<>();
        for (int hora = PRIMEIRA_HORA; hora <= ULTIMA_HORA; hora++) {
            String[] dias = new String[6];
            for (int col = 0; col < dias.length; col++) {
                String chave = chaveSlot(hora, col);
                dias[col] = agendados.contains(chave) ? "A" : (disponiveis.contains(chave) ? "D" : "X");
            }
            grade.add(new HorarioGrade(hora, dias));
        }
        return grade;
    }

    private Set<String> buscarSlotsAgendados(int mentorId) throws SQLException {
        Set<String> slots = new HashSet<>();
        String sql = "SELECT data_hora_inicio, data_hora_fim FROM mentoria WHERE mentor_id=? "
                + "AND status IN ('PENDENTE','CONFIRMADA') AND data_hora_fim > CURRENT_TIMESTAMP";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ini = rs.getTimestamp(1);
                    Timestamp fim = rs.getTimestamp(2);
                    if (ini == null || fim == null) continue;
                    // marca cada hora coberta pela sessão (uma sessão de 2h ocupa dois horários da grade)
                    LocalDateTime hora = ini.toLocalDateTime().withMinute(0).withSecond(0).withNano(0);
                    LocalDateTime limite = fim.toLocalDateTime();
                    while (hora.isBefore(limite)) {
                        int dia = hora.getDayOfWeek().getValue(); // segunda=1 ... sábado=6
                        if (dia >= 1 && dia <= 6) slots.add(chaveSlot(hora.getHour(), dia - 1));
                        hora = hora.plusHours(1);
                    }
                }
            }
        }
        return slots;
    }

    /** Atualiza descrição, experiência e disponibilidade sem sobrescrever horários com sessões marcadas. */
    public void salvarPerfil(int mentorId, String descricao, String experiencia,
                             List<String> marcados) throws SQLException {
        Set<String> selecionados = new HashSet<>(marcados == null ? Collections.emptyList() : marcados);
        Set<String> agendados = buscarSlotsAgendados(mentorId);
        try (Connection c = Conexao.abrir(contexto)) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE mentor_perfil SET descricao=?, experiencia=? WHERE id=?")) {
                    ps.setString(1, descricao);
                    ps.setString(2, experiencia);
                    ps.setInt(3, mentorId);
                    if (ps.executeUpdate() == 0) throw new SQLException("Perfil de mentor não encontrado.");
                }
                for (int hora = PRIMEIRA_HORA; hora <= ULTIMA_HORA; hora++) {
                    for (int col = 0; col < 6; col++) {
                        String chave = chaveSlot(hora, col);
                        if (agendados.contains(chave)) continue;
                        int diaBanco = col + 1;
                        String inicio = String.format(java.util.Locale.ROOT, "%02d:00", hora);
                        String fim = String.format(java.util.Locale.ROOT, "%02d:00", hora + 1);
                        boolean ativo = selecionados.contains(chave);
                        if (!ativo) {
                            // horário desmarcado: apaga a linha de vez (sem deixar registro inativo)
                            try (PreparedStatement ps = c.prepareStatement(
                                    "DELETE FROM disponibilidade WHERE mentor_id=? AND dia_semana=? AND hora_inicio=?")) {
                                ps.setInt(1, mentorId);
                                ps.setInt(2, diaBanco);
                                ps.setString(3, inicio);
                                ps.executeUpdate();
                            }
                            continue;
                        }
                        int atualizados;
                        try (PreparedStatement ps = c.prepareStatement(
                                "UPDATE disponibilidade SET ativo=TRUE, hora_fim=? WHERE mentor_id=? AND dia_semana=? AND hora_inicio=?")) {
                            ps.setString(1, fim);
                            ps.setInt(2, mentorId);
                            ps.setInt(3, diaBanco);
                            ps.setString(4, inicio);
                            atualizados = ps.executeUpdate();
                        }
                        if (atualizados == 0) {
                            try (PreparedStatement ps = c.prepareStatement(
                                    "INSERT INTO disponibilidade (mentor_id, dia_semana, hora_inicio, hora_fim, ativo) VALUES (?,?,?,?,TRUE)")) {
                                ps.setInt(1, mentorId);
                                ps.setInt(2, diaBanco);
                                ps.setString(3, inicio);
                                ps.setString(4, fim);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                rollback(c, e);
                throw e;
            }
        }
    }

    /** Horários semanais cadastrados (um por linha), já marcando quais têm sessão agendada. */
    public List<HorarioSemanal> listarHorariosSemanais(int mentorId) throws SQLException {
        Set<String> agendados = buscarSlotsAgendados(mentorId);
        String sql = "SELECT id, dia_semana, hora_inicio, hora_fim FROM disponibilidade "
                + "WHERE mentor_id=? AND ativo=TRUE ORDER BY dia_semana, hora_inicio";
        List<HorarioSemanal> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dia = rs.getInt("dia_semana");
                    LocalTime hora = parseHora(rs.getString("hora_inicio"));
                    boolean agendado = hora != null && dia >= 1 && dia <= 6
                            && agendados.contains(chaveSlot(hora.getHour(), dia - 1));
                    lista.add(new HorarioSemanal(rs.getInt("id"), dia, rs.getString("hora_inicio"),
                            rs.getString("hora_fim"), agendado));
                }
            }
        }
        return lista;
    }

    /** Remove de vez um horário semanal. Devolve false se não existe ou se tem sessão marcada. */
    public boolean removerHorarioSemanal(int mentorId, int horarioId) throws SQLException {
        for (HorarioSemanal h : listarHorariosSemanais(mentorId)) {
            if (h.getId() == horarioId && h.isAgendado()) return false;
        }
        String sql = "DELETE FROM disponibilidade WHERE id=? AND mentor_id=?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, horarioId);
            ps.setInt(2, mentorId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Bloqueios do mentor entre as datas (inclusive). Se "ate" for null, não há data final. */
    public List<Bloqueio> listarBloqueios(int mentorId, LocalDate de, LocalDate ate) throws SQLException {
        String sql = "SELECT id, data, hora_inicio, hora_fim, motivo FROM bloqueio_horario "
                + "WHERE mentor_id=? AND data>=? " + (ate == null ? "" : "AND data<=? ")
                + "ORDER BY data, hora_inicio";
        List<Bloqueio> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            ps.setDate(2, java.sql.Date.valueOf(de));
            if (ate != null) ps.setDate(3, java.sql.Date.valueOf(ate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Bloqueio(rs.getInt("id"), rs.getDate("data").toLocalDate(),
                            rs.getString("hora_inicio"), rs.getString("hora_fim"), rs.getString("motivo")));
                }
            }
        }
        return lista;
    }

    /**
     * Cria um bloqueio. horaInicio/horaFim null = dia inteiro.
     * Devolve false (sem gravar) se já existe sessão PENDENTE/CONFIRMADA dentro do período.
     */
    public boolean inserirBloqueio(int mentorId, LocalDate data, String horaInicio, String horaFim,
                                   String motivo) throws SQLException {
        boolean diaInteiro = horaInicio == null || horaFim == null;
        LocalDateTime de = diaInteiro ? data.atStartOfDay() : LocalDateTime.of(data, LocalTime.parse(horaInicio));
        LocalDateTime ate = diaInteiro ? data.plusDays(1).atStartOfDay() : LocalDateTime.of(data, LocalTime.parse(horaFim));
        try (Connection c = Conexao.abrir(contexto)) {
            if (ConflitoHorario.ocupado(c, mentorId, 0, de, ate)) return false;
            String sql = "INSERT INTO bloqueio_horario (mentor_id, data, hora_inicio, hora_fim, motivo) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, mentorId);
                ps.setDate(2, java.sql.Date.valueOf(data));
                if (diaInteiro) { ps.setNull(3, java.sql.Types.VARCHAR); ps.setNull(4, java.sql.Types.VARCHAR); }
                else { ps.setString(3, horaInicio); ps.setString(4, horaFim); }
                if (motivo == null || motivo.isEmpty()) ps.setNull(5, java.sql.Types.VARCHAR);
                else ps.setString(5, motivo);
                ps.executeUpdate();
            }
        }
        return true;
    }

    public void removerBloqueio(int mentorId, int bloqueioId) throws SQLException {
        String sql = "DELETE FROM bloqueio_horario WHERE id=? AND mentor_id=?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bloqueioId);
            ps.setInt(2, mentorId);
            ps.executeUpdate();
        }
    }

    public List<Sessao> listarSessoes(int mentorId) throws SQLException {
        String sql = "SELECT m.id, u.nome AS aluno_nome, d.nome AS disciplina, m.data_hora_inicio, "
                + "m.data_hora_fim, m.status, a.nota_final "
                + "FROM mentoria m JOIN usuario u ON u.id=m.mentorando_id "
                + "JOIN disciplina d ON d.id=m.disciplina_id LEFT JOIN avaliacao a ON a.mentoria_id=m.id "
                + "WHERE m.mentor_id=? ORDER BY m.data_hora_inicio";
        List<Sessao> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp inicioTs = rs.getTimestamp("data_hora_inicio");
                    Timestamp fimTs = rs.getTimestamp("data_hora_fim");
                    if (inicioTs == null || fimTs == null) continue;
                    LocalDateTime inicio = inicioTs.toLocalDateTime();
                    long minutos = Math.max(0, Duration.between(inicio, fimTs.toLocalDateTime()).toMinutes());
                    int horas = (int) Math.max(1, (minutos + 59) / 60);
                    double notaValor = rs.getDouble("nota_final");
                    Double nota = rs.wasNull() ? null : notaValor;
                    lista.add(new Sessao(rs.getInt("id"), rs.getString("aluno_nome"),
                            rs.getString("disciplina"), inicio, horas, rs.getString("status"), nota));
                }
            }
        }
        return lista;
    }

    public void atualizarStatusSessao(int mentorId, int sessaoId, String status) throws SQLException {
        if (!"CONFIRMADA".equals(status) && !"CANCELADA".equals(status)) return;
        String sql = "UPDATE mentoria SET status=?, atualizado_em=CURRENT_TIMESTAMP "
                + "WHERE id=? AND mentor_id=? AND status IN ('PENDENTE','CONFIRMADA')";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, sessaoId);
            ps.setInt(3, mentorId);
            if (ps.executeUpdate() == 1) {
                String texto = "CONFIRMADA".equals(status)
                        ? "{nome} confirmou a sua mentoria de {quando}."
                        : "{nome} cancelou a sua mentoria de {quando}.";
                NotificacaoDao.avisar(c, sessaoId, "MENTORANDO", status, texto);
            }
        }
    }

    /** Marca como REALIZADA uma sessão confirmada que já começou. Devolve false se não foi possível. */
    public boolean marcarComoRealizada(int mentorId, int sessaoId) throws SQLException {
        String sql = "UPDATE mentoria SET status='REALIZADA', atualizado_em=CURRENT_TIMESTAMP "
                + "WHERE id=? AND mentor_id=? AND status='CONFIRMADA' "
                + "AND data_hora_inicio <= CURRENT_TIMESTAMP";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sessaoId);
            ps.setInt(2, mentorId);
            if (ps.executeUpdate() != 1) return false;
            NotificacaoDao.avisar(c, sessaoId, "MENTORANDO", "REALIZADA",
                    "A mentoria de {quando} com {nome} foi marcada como realizada. Que tal avaliar a sessão?");
            return true;
        }
    }

    public List<Material> listarMateriais(int mentorId) throws SQLException {
        String sql = "SELECT mt.id, mt.titulo, mt.descricao, mt.arquivo_url, mt.criado_em, mt.mentoria_id, "
                + "d.nome AS disciplina, u.nome AS aluno_nome, m.data_hora_inicio "
                + "FROM material mt LEFT JOIN mentoria m ON m.id=mt.mentoria_id "
                + "LEFT JOIN disciplina d ON d.id=m.disciplina_id "
                + "LEFT JOIN usuario u ON u.id=m.mentorando_id "
                + "WHERE mt.mentor_id=? ORDER BY mt.criado_em DESC, mt.id DESC";
        List<Material> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("criado_em");
                    LocalDateTime criadoEm = ts == null ? LocalDateTime.now() : ts.toLocalDateTime();
                    int vinculo = rs.getInt("mentoria_id");
                    Integer mentoriaId = rs.wasNull() ? null : vinculo;
                    String texto = null;
                    if (mentoriaId != null) {
                        Timestamp quando = rs.getTimestamp("data_hora_inicio");
                        texto = rs.getString("disciplina") + " · " + br.com.senseidev.util.Formato.primeiroNome(rs.getString("aluno_nome"))
                                + (quando == null ? "" : " · " + quando.toLocalDateTime()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")));
                    }
                    lista.add(new Material(rs.getInt("id"), rs.getString("titulo"), rs.getString("descricao"),
                            rs.getString("arquivo_url"), criadoEm, mentoriaId, texto));
                }
            }
        }
        return lista;
    }

    /** A mentoria existe, é deste mentor e não está cancelada? (só assim o material pode ser vinculado) */
    private boolean mentoriaDoMentor(Connection c, int mentorId, int mentoriaId) throws SQLException {
        String sql = "SELECT 1 FROM mentoria WHERE id=? AND mentor_id=? AND status<>'CANCELADA'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentoriaId);
            ps.setInt(2, mentorId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static void definirMentoria(PreparedStatement ps, int indice, Integer mentoriaId) throws SQLException {
        if (mentoriaId == null) ps.setNull(indice, java.sql.Types.INTEGER);
        else ps.setInt(indice, mentoriaId);
    }

    /** Cria o material. Devolve false (sem gravar) se a mentoria vinculada não for deste mentor. */
    public boolean inserirMaterial(int mentorId, String titulo, String url, String descricao,
                                   Integer mentoriaId) throws SQLException {
        String sql = "INSERT INTO material (mentor_id, mentoria_id, titulo, descricao, arquivo_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Conexao.abrir(contexto)) {
            if (mentoriaId != null && !mentoriaDoMentor(c, mentorId, mentoriaId)) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, mentorId);
                definirMentoria(ps, 2, mentoriaId);
                ps.setString(3, titulo);
                ps.setString(4, descricao);
                ps.setString(5, url);
                ps.executeUpdate();
            }
            if (mentoriaId != null) avisarMaterial(c, mentoriaId);
        }
        return true;
    }

    /** Edita título, link, descrição e vínculo. Devolve false se o material não é do mentor ou o vínculo é inválido. */
    public boolean editarMaterial(int mentorId, int materialId, String titulo, String url, String descricao,
                                  Integer mentoriaId) throws SQLException {
        try (Connection c = Conexao.abrir(contexto)) {
            Integer anterior = null;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT mentoria_id FROM material WHERE id=? AND mentor_id=?")) {
                ps.setInt(1, materialId);
                ps.setInt(2, mentorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    int valor = rs.getInt(1);
                    anterior = rs.wasNull() ? null : valor;
                }
            }
            boolean mudouVinculo = mentoriaId != null && !mentoriaId.equals(anterior);
            if (mudouVinculo && !mentoriaDoMentor(c, mentorId, mentoriaId)) return false;
            String sql = "UPDATE material SET titulo=?, arquivo_url=?, descricao=?, mentoria_id=? WHERE id=? AND mentor_id=?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, titulo);
                ps.setString(2, url);
                ps.setString(3, descricao);
                definirMentoria(ps, 4, mentoriaId);
                ps.setInt(5, materialId);
                ps.setInt(6, mentorId);
                if (ps.executeUpdate() != 1) return false;
            }
            if (mudouVinculo) avisarMaterial(c, mentoriaId);
        }
        return true;
    }

    private void avisarMaterial(Connection c, int mentoriaId) throws SQLException {
        NotificacaoDao.avisar(c, mentoriaId, "MENTORANDO", "NOVO_MATERIAL",
                "{nome} disponibilizou um material para a mentoria de {quando}.");
    }

    public void removerMaterial(int mentorId, int materialId) throws SQLException {
        String sql = "DELETE FROM material WHERE id=? AND mentor_id=?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setInt(2, mentorId);
            ps.executeUpdate();
        }
    }

    public List<Avaliacao> listarAvaliacoes(int mentorId) throws SQLException {
        String sql = "SELECT u.nome AS aluno_nome, d.nome AS disciplina, a.comentario, a.nota_final, a.criado_em "
                + "FROM avaliacao a JOIN mentoria m ON m.id=a.mentoria_id "
                + "JOIN usuario u ON u.id=m.mentorando_id JOIN disciplina d ON d.id=m.disciplina_id "
                + "WHERE m.mentor_id=? ORDER BY a.criado_em DESC";
        List<Avaliacao> lista = new ArrayList<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("criado_em");
                    lista.add(new Avaliacao(rs.getString("aluno_nome"), rs.getString("disciplina"),
                            rs.getString("comentario"), rs.getDouble("nota_final"),
                            ts == null ? LocalDateTime.now() : ts.toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    public double buscarNotaGeral(int mentorId) throws SQLException {
        String sql = "SELECT AVG(a.nota_final) FROM avaliacao a JOIN mentoria m ON m.id=a.mentoria_id WHERE m.mentor_id=?";
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { double valor = rs.getDouble(1); return rs.wasNull() ? 0.0 : valor; }
            }
        }
        return 0.0;
    }

    public Map<String, Double> buscarMediasPorCriterio(int mentorId) throws SQLException {
        String sql = "SELECT AVG(a.clareza), AVG(a.dominio_assunto), AVG(a.pontualidade), "
                + "AVG(a.capacidade_orientar), AVG(a.experiencia_geral) FROM avaliacao a "
                + "JOIN mentoria m ON m.id=a.mentoria_id WHERE m.mentor_id=?";
        Map<String, Double> criterios = new LinkedHashMap<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String[] nomes = {"Clareza da explicação", "Domínio do assunto", "Pontualidade",
                            "Capacidade de orientar", "Experiência geral"};
                    for (int i = 0; i < nomes.length; i++) {
                        double valor = rs.getDouble(i + 1);
                        criterios.put(nomes[i], rs.wasNull() ? 0.0 : valor);
                    }
                }
            }
        }
        if (criterios.isEmpty()) {
            for (String nome : Arrays.asList("Clareza da explicação", "Domínio do assunto", "Pontualidade",
                    "Capacidade de orientar", "Experiência geral")) criterios.put(nome, 0.0);
        }
        return criterios;
    }

    private static LocalTime parseHora(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        String texto = valor.trim();
        if (texto.matches("^[0-9]:[0-5][0-9]$")) texto = "0" + texto;
        try { return LocalTime.parse(texto); }
        catch (RuntimeException e) { return null; }
    }

    private static String chaveSlot(int hora, int colunaDia) {
        return hora + "-" + colunaDia;
    }

    private static void rollback(Connection c, Exception original) {
        try { c.rollback(); }
        catch (SQLException rollbackErro) { original.addSuppressed(rollbackErro); }
    }
}