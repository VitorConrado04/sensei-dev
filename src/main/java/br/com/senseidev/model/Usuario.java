
package br.com.senseidev.model;

import java.time.LocalDateTime;

public abstract class Usuario {
    private String tipo;
    private Integer id;
    private String nome;
    private String email;
    private String senhaHash;
    private Integer semestreAtual;
    private String fotoUrl;
    private String status;
    private LocalDateTime criadoEm;

    protected Usuario(String nome, String email,
                      String senhaHash, Integer semestreAtual,
                      String fotoUrl) {

        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.semestreAtual = semestreAtual;
        this.fotoUrl = fotoUrl;
        this.status = "ATIVO";
    }

    protected Usuario(Integer id, String nome, String email,
                      String senhaHash, Integer semestreAtual,
                      String fotoUrl, String status,
                      LocalDateTime criadoEm) {

        this(nome, email, senhaHash, semestreAtual, fotoUrl);

        this.id = id;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    // os get e os set
    // só os get

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Integer getSemestreAtual() {
        return semestreAtual;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public String getTipo() { return tipo;}


    // só os set
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public void setSemestreAtual(Integer semestreAtual) {
        this.semestreAtual = semestreAtual;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setTipo(String tipo){this.tipo = tipo;}
}
