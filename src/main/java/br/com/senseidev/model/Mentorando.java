package br.com.senseidev.model;



public class Mentorando extends Usuario {

    public Mentorando(String nome, String email,
                      String senhaHash, Integer semestreAtual,
                      String fotoUrl) {

        super(nome, email, senhaHash, semestreAtual, fotoUrl);
        setTipo("MENTORANDO");
    }
}