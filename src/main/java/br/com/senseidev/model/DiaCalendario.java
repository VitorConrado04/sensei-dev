package br.com.senseidev.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// uma célula do calendário do mês (inclui os dias do mês vizinho que completam a semana)
public class DiaCalendario {

    private LocalDate data;
    private boolean foraDoMes;
    private boolean hoje;
    private List<Sessao> sessoes = new ArrayList<>();

    public DiaCalendario(LocalDate data, boolean foraDoMes, boolean hoje) {
        this.data = data;
        this.foraDoMes = foraDoMes;
        this.hoje = hoje;
    }

    public int getDia() {
        return data.getDayOfMonth();
    }

    public boolean isForaDoMes() {
        return foraDoMes;
    }

    public boolean isHoje() {
        return hoje;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }
}
