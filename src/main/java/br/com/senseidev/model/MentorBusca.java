package br.com.senseidev.model;

/** Dados públicos necessários à lista de busca de mentores. */
public class MentorBusca {
    private final int id;
    private final String nome;
    private final String descricao;
    private final String experiencia;
    private final double notaMedia;
    private final int totalAvaliacoes;

    public MentorBusca(int id, String nome, String descricao, String experiencia,
                       double notaMedia, int totalAvaliacoes) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.notaMedia = notaMedia;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getExperiencia() { return experiencia; }
    public double getNotaMedia() { return notaMedia; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
}
