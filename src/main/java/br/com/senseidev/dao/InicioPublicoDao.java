package br.com.senseidev.dao;

import br.com.senseidev.model.DadosInicioPublico;
import br.com.senseidev.model.DepoimentoPublico;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Consultas públicas para os indicadores e avaliações da página inicial. */
public class InicioPublicoDao {
    private static final int LIMITE_DEPOIMENTOS = 4;
    private final ServletContext contexto;

    public InicioPublicoDao(ServletContext contexto) {
        this.contexto = contexto;
    }

    public DadosInicioPublico buscarDados() throws SQLException {
        // O total soma as sessões confirmadas, pendentes e realizadas; canceladas ficam fora.
        // Para os perfis, entram apenas contas ativas com o respectivo perfil cadastrado.
        String sqlIndicadores = "SELECT "
                + "(SELECT COUNT(*) FROM usuario u JOIN mentor_perfil p ON p.id = u.id "
                + " WHERE u.tipo = 'MENTOR' AND u.status = 'ATIVO') AS mentores, "
                + "(SELECT COUNT(*) FROM usuario u JOIN mentorando_perfil p ON p.id = u.id "
                + " WHERE u.tipo = 'MENTORANDO' AND u.status = 'ATIVO') AS mentorados, "
                + "(SELECT COUNT(*) FROM mentoria WHERE status IN ('CONFIRMADA','PENDENTE','REALIZADA')) AS total_mentorias, "
                + "(SELECT COUNT(*) FROM mentoria WHERE status = 'CONFIRMADA') AS mentorias_ativas, "
                + "(SELECT COUNT(*) FROM mentoria WHERE status = 'PENDENTE') AS mentorias_pendentes, "
                + "(SELECT COUNT(*) FROM mentoria WHERE status = 'REALIZADA') AS mentorias_finalizadas";

        // A página pública exibe somente avaliações reais que possuem comentário.
        String sqlDepoimentos = "SELECT u.nome AS mentorando_nome, d.nome AS disciplina, "
                + "a.comentario, a.nota_final, a.criado_em "
                + "FROM avaliacao a "
                + "JOIN mentoria m ON m.id = a.mentoria_id "
                + "JOIN usuario u ON u.id = m.mentorando_id "
                + "JOIN disciplina d ON d.id = m.disciplina_id "
                + "WHERE a.comentario IS NOT NULL AND TRIM(a.comentario) <> '' "
                + "ORDER BY a.criado_em DESC, a.id DESC FETCH FIRST "
                + LIMITE_DEPOIMENTOS + " ROWS ONLY";

        try (Connection c = Conexao.abrir(contexto)) {
            int mentores;
            int mentorados;
            int totalMentorias;
            int mentoriasAtivas;
            int mentoriasPendentes;
            int mentoriasFinalizadas;

            try (PreparedStatement ps = c.prepareStatement(sqlIndicadores);
                 ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Não foi possível carregar os indicadores públicos.");
                mentores = rs.getInt("mentores");
                mentorados = rs.getInt("mentorados");
                totalMentorias = rs.getInt("total_mentorias");
                mentoriasAtivas = rs.getInt("mentorias_ativas");
                mentoriasPendentes = rs.getInt("mentorias_pendentes");
                mentoriasFinalizadas = rs.getInt("mentorias_finalizadas");
            }

            List<DepoimentoPublico> depoimentos = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sqlDepoimentos);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp criadoEm = rs.getTimestamp("criado_em");
                    depoimentos.add(new DepoimentoPublico(
                            rs.getString("mentorando_nome"),
                            rs.getString("disciplina"),
                            rs.getString("comentario"),
                            rs.getDouble("nota_final"),
                            criadoEm == null ? null : criadoEm.toLocalDateTime()));
                }
            }

            return new DadosInicioPublico(mentores, mentorados, totalMentorias,
                    mentoriasAtivas, mentoriasPendentes, mentoriasFinalizadas, depoimentos);
        }
    }
}
