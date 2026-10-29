package br.com.senseidev.model;

import br.com.senseidev.util.Formato;

import java.time.LocalDateTime;

// tabela material (o campo usos não existe no banco, seria um COUNT das mentorias que usaram)
public class Material {

    private Integer id;
    private String titulo;
    private String url;
    private LocalDateTime criadoEm;
    private int usos;
    private String descricao;
    private Integer mentoriaId;        // null = material geral, sem mentoria vinculada
    private String mentoriaTexto;      // "Banco de Dados · Ana · 12/11 14:00"

    public Material(Integer id, String titulo, String url, LocalDateTime criadoEm, int usos) {
        this.id = id;
        this.titulo = titulo;
        this.url = url;
        this.criadoEm = criadoEm;
        this.usos = usos;
    }

    public Material(Integer id, String titulo, String descricao, String url, LocalDateTime criadoEm,
                    Integer mentoriaId, String mentoriaTexto) {
        this(id, titulo, url, criadoEm, mentoriaId == null ? 0 : 1);
        this.descricao = descricao;
        this.mentoriaId = mentoriaId;
        this.mentoriaTexto = mentoriaTexto;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public int getUsos() {
        return usos;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getMentoriaId() {
        return mentoriaId;
    }

    public String getMentoriaTexto() {
        return mentoriaTexto;
    }

    public String getDiaTexto() {
        return Formato.dia(criadoEm);
    }
}
