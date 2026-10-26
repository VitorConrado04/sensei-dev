package br.com.senseidev.model;

import br.com.senseidev.util.Formato;

import java.time.LocalDateTime;

// uma linha da tabela mentoria, já com o nome do mentorando e da disciplina
// (no banco isso vem de um JOIN com usuario e disciplina)
public class Sessao {

    private Integer id;
    private String alunoNome;
    private String disciplina;
    private LocalDateTime inicio;
    private int horas;
    private String status;   // PENDENTE, CONFIRMADA, REALIZADA ou CANCELADA
    private Double nota;     // só tem valor depois que o aluno avalia

    public Sessao(Integer id, String alunoNome, String disciplina,
                  LocalDateTime inicio, int horas, String status, Double nota) {
        this.id = id;
        this.alunoNome = alunoNome;
        this.disciplina = disciplina;
        this.inicio = inicio;
        this.horas = horas;
        this.status = status;
        this.nota = nota;
    }

    public Integer getId() {
        return id;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public int getHoras() {
        return horas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getNota() {
        return nota;
    }

    // os getters abaixo existem só pro JSP não precisar mexer com data
    public String getDiaTexto() {
        return Formato.dia(inicio);
    }

    public String getHoraTexto() {
        return Formato.hora(inicio);
    }

    public String getAlunoPrimeiroNome() {
        return Formato.primeiroNome(alunoNome);
    }

    public String getIniciais() {
        return Formato.iniciais(alunoNome);
    }
}
