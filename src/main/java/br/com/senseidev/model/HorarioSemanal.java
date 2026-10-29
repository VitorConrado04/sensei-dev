package br.com.senseidev.model;

// uma linha da tabela disponibilidade (um horário que se repete toda semana)
public class HorarioSemanal {

    private static final String[] DIAS = {"", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

    private int id;
    private int diaSemana;       // 1 = segunda ... 6 = sábado
    private String horaInicio;
    private String horaFim;
    private boolean agendado;    // true = já existe sessão marcada nesse horário (não pode remover)

    public HorarioSemanal(int id, int diaSemana, String horaInicio, String horaFim, boolean agendado) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.agendado = agendado;
    }

    public int getId() {
        return id;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public String getDiaTexto() {
        return diaSemana >= 1 && diaSemana <= 7 ? DIAS[diaSemana] : "?";
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public boolean isAgendado() {
        return agendado;
    }
}
