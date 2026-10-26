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

    public Material(Integer id, String titulo, String url, LocalDateTime criadoEm, int usos) {
        this.id = id;
        this.titulo = titulo;
        this.url = url;
        this.criadoEm = criadoEm;
        this.usos = usos;
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

    public String getDiaTexto() {
        return Formato.dia(criadoEm);
    }
}
