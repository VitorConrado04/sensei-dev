package br.com.senseidev.dao;

import br.com.senseidev.model.Material;
import br.com.senseidev.util.Conexao;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Materiais que o mentorando pode consultar: somente os vinculados às mentorias dele (não canceladas). */
public class MaterialAlunoDao {
    private final ServletContext contexto;

    public MaterialAlunoDao(ServletContext contexto) { this.contexto = contexto; }

    /** Mapa mentoria_id -> materiais dessa mentoria, do mais novo para o mais antigo. */
    public Map<Integer, List<Material>> listarPorMentorando(int mentorandoId) throws SQLException {
        String sql = "SELECT mt.id, mt.mentoria_id, mt.titulo, mt.descricao, mt.arquivo_url, mt.criado_em "
                + "FROM material mt JOIN mentoria m ON m.id = mt.mentoria_id "
                + "WHERE m.mentorando_id = ? AND m.status <> 'CANCELADA' "
                + "ORDER BY mt.criado_em DESC, mt.id DESC";
        Map<Integer, List<Material>> resultado = new HashMap<>();
        try (Connection c = Conexao.abrir(contexto); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mentorandoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("criado_em");
                    int mentoriaId = rs.getInt("mentoria_id");
                    Material material = new Material(rs.getInt("id"), rs.getString("titulo"),
                            rs.getString("descricao"), rs.getString("arquivo_url"),
                            ts == null ? LocalDateTime.now() : ts.toLocalDateTime(), mentoriaId, null);
                    List<Material> lista = resultado.get(mentoriaId);
                    if (lista == null) { lista = new ArrayList<>(); resultado.put(mentoriaId, lista); }
                    lista.add(material);
                }
            }
        }
        return resultado;
    }
}
