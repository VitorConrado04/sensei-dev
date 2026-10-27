package br.com.senseidev.model;

/** Dados mínimos de uma conta autenticada, sem expor o hash da senha. */
public final class UsuarioAutenticado {
    private final int id;
    private final String nome;
    private final String tipo;

    public UsuarioAutenticado(int id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
}