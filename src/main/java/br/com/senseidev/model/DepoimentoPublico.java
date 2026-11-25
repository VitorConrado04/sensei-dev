package br.com.senseidev.model;

import br.com.senseidev.util.Formato;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Avaliação real publicada na página inicial, com identificação reduzida do mentorando. */
public class DepoimentoPublico {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String nome;
    private final String iniciais;
    private final String disciplina;
    private final String comentario;
    private final double nota;
    private final LocalDateTime data;

    public DepoimentoPublico(String nomeCompleto, String disciplina, String comentario,
                             double nota, LocalDateTime data) {
        this.nome = nomePublico(nomeCompleto);
        this.iniciais = Formato.iniciais(nomeCompleto);
        this.disciplina = disciplina;
        this.comentario = comentario;
        this.nota = nota;
        this.data = data;
    }

    private static String nomePublico(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) return "Mentorado";
        String[] partes = nomeCompleto.trim().split("\\s+");
        if (partes.length == 1) return partes[0];
        return partes[0] + " " + partes[1].substring(0, 1).toUpperCase() + ".";
    }

    public String getNome() { return nome; }
    public String getIniciais() { return iniciais; }
    public String getDisciplina() { return disciplina; }
    public String getComentario() { return comentario; }
    public double getNota() { return nota; }
    public String getDataTexto() { return data == null ? "" : data.format(DATA); }
}
