package br.com.senseidev.model;

import java.util.List;

/** Indicadores e avaliações reais exibidos na página pública do Sensei.dev. */
public class DadosInicioPublico {
    private final int mentoresCadastrados;
    private final int mentoradosCadastrados;
    private final int totalMentorias;
    private final int mentoriasAtivas;
    private final int mentoriasPendentes;
    private final int mentoriasFinalizadas;
    private final List<DepoimentoPublico> depoimentos;

    public DadosInicioPublico(int mentoresCadastrados, int mentoradosCadastrados,
                              int totalMentorias, int mentoriasAtivas,
                              int mentoriasPendentes, int mentoriasFinalizadas,
                              List<DepoimentoPublico> depoimentos) {
        this.mentoresCadastrados = mentoresCadastrados;
        this.mentoradosCadastrados = mentoradosCadastrados;
        this.totalMentorias = totalMentorias;
        this.mentoriasAtivas = mentoriasAtivas;
        this.mentoriasPendentes = mentoriasPendentes;
        this.mentoriasFinalizadas = mentoriasFinalizadas;
        this.depoimentos = depoimentos;
    }

    public int getMentoresCadastrados() { return mentoresCadastrados; }
    public int getMentoradosCadastrados() { return mentoradosCadastrados; }
    public int getTotalMentorias() { return totalMentorias; }
    public int getMentoriasAtivas() { return mentoriasAtivas; }
    public int getMentoriasPendentes() { return mentoriasPendentes; }
    public int getMentoriasFinalizadas() { return mentoriasFinalizadas; }
    public List<DepoimentoPublico> getDepoimentos() { return depoimentos; }
}
