package br.com.senseidev.model;

/** Dados mínimos para a lista de mentores favoritos do aluno. */
public class MentorFavorito {
    private final int id;
    private final String nome;
    private final String fotoUrl;
    private final boolean ativo;

    public MentorFavorito(int id, String nome, String fotoUrl, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getFotoUrl() { return fotoUrl; }
    public boolean isAtivo() { return ativo; }
}
