package br.com.senseidev.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Avaliação publicada no perfil, sem dados pessoais do mentorando. */
public class AvaliacaoPublica {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final String disciplina;
    private final String comentario;
    private final double nota;
    private final LocalDateTime data;

    public AvaliacaoPublica(String disciplina, String comentario, double nota, LocalDateTime data) {
        this.disciplina = disciplina;
        this.comentario = comentario;
        this.nota = nota;
        this.data = data;
    }

    public String getDisciplina() { return disciplina; }
    public String getComentario() { return comentario; }
    public double getNota() { return nota; }
    public String getDataTexto() { return data == null ? "" : data.format(FORMATO); }
}
