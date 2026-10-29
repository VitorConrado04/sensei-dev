package br.com.senseidev.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// uma linha da tabela bloqueio_horario: um dia (ou parte dele) em que o mentor não atende
// hora_inicio e hora_fim vazias = dia inteiro bloqueado
public class Bloqueio {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private LocalDate data;
    private String horaInicio;   // "HH:mm" ou null
    private String horaFim;      // "HH:mm" ou null
    private String motivo;

    public Bloqueio(int id, LocalDate data, String horaInicio, String horaFim, String motivo) {
        this.id = id;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isDiaInteiro() {
        return horaInicio == null || horaInicio.isEmpty() || horaFim == null || horaFim.isEmpty();
    }

    public String getDataIso() {
        return data.toString();
    }

    public String getDataTexto() {
        return data.format(DATA);
    }

    // "Dia inteiro" ou "09:00 às 11:00"
    public String getPeriodoTexto() {
        return isDiaInteiro() ? "Dia inteiro" : horaInicio + " às " + horaFim;
    }
}
