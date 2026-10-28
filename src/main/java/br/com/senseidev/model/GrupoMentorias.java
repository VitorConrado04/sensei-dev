package br.com.senseidev.model;

import java.util.List;

/** Uma seção da tela Minhas mentorias. */
public class GrupoMentorias {
    private final String id;
    private final String titulo;
    private final String mensagemVazia;
    private final List<MentoriaAluno> mentorias;

    public GrupoMentorias(String id, String titulo, String mensagemVazia,
                          List<MentoriaAluno> mentorias) {
        this.id = id;
        this.titulo = titulo;
        this.mensagemVazia = mensagemVazia;
        this.mentorias = mentorias;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getMensagemVazia() { return mensagemVazia; }
    public List<MentoriaAluno> getMentorias() { return mentorias; }
}
