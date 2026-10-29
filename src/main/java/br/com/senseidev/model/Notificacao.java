package br.com.senseidev.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Aviso mostrado para o mentor ou para o mentorando. */
public class Notificacao {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final int id;
    private final Integer mentoriaId;   // pode ser nulo
    private final String tipo;
    private final String mensagem;
    private final boolean lida;
    private final LocalDateTime criadoEm;

    public Notificacao(int id, Integer mentoriaId, String tipo, String mensagem,
                       boolean lida, LocalDateTime criadoEm) {
        this.id = id;
        this.mentoriaId = mentoriaId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.lida = lida;
        this.criadoEm = criadoEm;
    }

    public int getId() { return id; }
    public Integer getMentoriaId() { return mentoriaId; }
    public String getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public boolean isLida() { return lida; }
    public String getCriadoEmTexto() { return criadoEm == null ? "" : criadoEm.format(FORMATO); }
}