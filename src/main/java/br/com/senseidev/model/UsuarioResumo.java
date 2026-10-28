package br.com.senseidev.model;

/** Dados de um usuario para o painel do admin (sem a senha). */
public class UsuarioResumo {
    private final int id;
    private final String nome;
    private final String email;
    private final Integer semestreAtual; // pode ser null (mentor e admin nao informam)
    private final String fotoUrl;
    private final String tipo;           // ADMIN, MENTOR ou MENTORANDO
    private final String status;         // ATIVO ou SUSPENSO

    public UsuarioResumo(int id, String nome, String email, Integer semestreAtual,
                         String fotoUrl, String tipo, String status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.semestreAtual = semestreAtual;
        this.fotoUrl = fotoUrl;
        this.tipo = tipo;
        this.status = status;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Integer getSemestreAtual() { return semestreAtual; }
    public String getFotoUrl() { return fotoUrl; }
    public String getTipo() { return tipo; }
    public String getStatus() { return status; }
}