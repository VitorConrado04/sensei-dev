package br.com.senseidev.model;

// uma linha da grade de disponibilidade: um horário e os 6 dias (segunda a sábado)
// cada posição de "dias" vale D (disponível), X (indisponível) ou A (já tem sessão agendada)
// no banco cada D vira uma linha da tabela disponibilidade (dia_semana, hora_inicio, hora_fim)
public class HorarioGrade {

    private int hora;
    private String[] dias;

    public HorarioGrade(int hora, String[] dias) {
        this.hora = hora;
        this.dias = dias;
    }

    public int getHora() {
        return hora;
    }

    public String[] getDias() {
        return dias;
    }
}
