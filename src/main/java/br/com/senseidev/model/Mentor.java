package br.com.senseidev.model;


public class Mentor extends Usuario {

    private String descricao;
    private String experiencia;
    private double notaMedia;
    private int totalMentorias;

    public Mentor(String nome, String email,
                  String senhaHash, Integer semestreAtual,
                  String fotoUrl, String descricao,
                  String experiencia) {

        super(nome, email, senhaHash, semestreAtual, fotoUrl);
        setTipo("MENTOR");

        this.descricao = descricao;
        this.experiencia = experiencia;
        this.notaMedia = 0.0;
        this.totalMentorias = 0;
    }


    // os get e os set
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public int getTotalMentorias() {
        return totalMentorias;
    }

    public void setTotalMentorias(int totalMentorias) {
        this.totalMentorias = totalMentorias;
    }

}