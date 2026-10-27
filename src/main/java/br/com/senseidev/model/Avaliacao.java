package br.com.senseidev.model;

import br.com.senseidev.util.Formato;

import java.time.LocalDateTime;

// tabela avaliacao ligada à mentoria (por isso tem aluno e disciplina)
public class Avaliacao {

    private String alunoNome;
    private String disciplina;
    private String comentario;
    private double nota;          // nota_final da tabela
    private LocalDateTime data;

    public Avaliacao(String alunoNome, String disciplina, String comentario,
                     double nota, LocalDateTime data) {
        this.alunoNome = alunoNome;
        this.disciplina = disciplina;
        this.comentario = comentario;
        this.nota = nota;
        this.data = data;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public String getComentario() {
        return comentario;
    }

    public double getNota() {
        return nota;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getDiaTexto() {
        return Formato.dia(data);
    }

    public String getIniciais() {
        return Formato.iniciais(alunoNome);
    }
}