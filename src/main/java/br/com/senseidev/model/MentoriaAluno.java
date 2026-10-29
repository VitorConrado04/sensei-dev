package br.com.senseidev.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Dados de uma mentoria exibidos ao mentorando que a solicitou. */
public class MentoriaAluno {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final int id;
    private final int mentorId;
    private final String mentorNome;
    private final boolean mentorAtivo;
    private final String disciplina;
    private final String objetivo;
    private final LocalDateTime inicio;
    private final LocalDateTime fim;
    private final String status;
    private double nota;

    public MentoriaAluno(int id, int mentorId, String mentorNome, boolean mentorAtivo,
                         String disciplina, String objetivo, LocalDateTime inicio,
                         LocalDateTime fim, String status) {
        this.id = id;
        this.mentorId = mentorId;
        this.mentorNome = mentorNome;
        this.mentorAtivo = mentorAtivo;
        this.disciplina = disciplina;
        this.objetivo = objetivo;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
    }

    public int getId() { return id; }
    public int getMentorId() { return mentorId; }
    public String getMentorNome() { return mentorNome; }
    public boolean isMentorAtivo() { return mentorAtivo; }
    public String getDisciplina() { return disciplina; }
    public String getObjetivo() { return objetivo; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim() { return fim; }
    public String getStatus() { return status; }
    public boolean isPodeAlterar() {
        return ("PENDENTE".equals(status) || "CONFIRMADA".equals(status))
                && inicio.isAfter(LocalDateTime.now());
    }
    public String getDataTexto() { return inicio.format(DATA); }
    public String getHoraTexto() { return inicio.format(HORA); }
    public long getDuracaoMinutos() { return Duration.between(inicio, fim).toMinutes();}

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }
}
