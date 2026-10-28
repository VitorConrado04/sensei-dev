package br.com.senseidev.model;

/** Informações do mentor que podem ser vistas por um mentorando. */
public class PerfilMentorPublico {
    private final int id;
    private final String nome;
    private final Integer semestreAtual;
    private final String fotoUrl;
    private final String descricao;
    private final String experiencia;
    private final double notaMedia;
    private final int totalAvaliacoes;
    private final int totalMentorias;

    public PerfilMentorPublico(int id, String nome, Integer semestreAtual, String fotoUrl,
                              String descricao, String experiencia, double notaMedia,
                              int totalAvaliacoes, int totalMentorias) {
        this.id = id;
        this.nome = nome;
        this.semestreAtual = semestreAtual;
        this.fotoUrl = fotoUrl;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.notaMedia = notaMedia;
        this.totalAvaliacoes = totalAvaliacoes;
        this.totalMentorias = totalMentorias;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public Integer getSemestreAtual() { return semestreAtual; }
    public String getFotoUrl() { return fotoUrl; }
    public String getDescricao() { return descricao; }
    public String getExperiencia() { return experiencia; }
    public double getNotaMedia() { return notaMedia; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
    public int getTotalMentorias() { return totalMentorias; }
}
