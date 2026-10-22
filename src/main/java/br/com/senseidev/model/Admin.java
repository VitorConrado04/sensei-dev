package br.com.senseidev.model;

public class Admin extends Usuario {
    public Admin(String nome, String email, String senhaHash){

        super(nome, email, senhaHash, null, null );
        setTipo("ADMIN");
    }
}
