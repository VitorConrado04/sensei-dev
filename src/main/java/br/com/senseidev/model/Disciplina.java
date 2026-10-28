package br.com.senseidev.model;

/** Uma disciplina da grade do curso (tabela disciplina). */
public class Disciplina {
    private final int id;
    private final String nome;
    private final int semestre;

    public Disciplina(int id, String nome, int semestre) {
        this.id = id;
        this.nome = nome;
        this.semestre = semestre;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getSemestre() { return semestre; }
}