package br.com.senseidev.dao;

import br.com.senseidev.model.Usuario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UsuarioDao {
    // Recebe qualquer Usuario (Mentor, Mentorando ou Admin) e grava na tabela USUARIO
    public void createUsuario(Usuario usuario) {

        // Cada ? e preenchido abaixo, na mesma ordem. ID e CRIADO_EM ficam de fora
        // porque o proprio banco preenche (auto incremento e data atual)
        String SQL = "INSERT INTO USUARIO (NOME, EMAIL, SENHA, SEMESTRE_ATUAL, FOTO_URL, STATUS, TIPO) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection connection = DriverManager.getConnection("jdbc:h2:./db/sensei", "sa", "sa");

            System.out.println("success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, usuario.getNome());
            preparedStatement.setString(2, usuario.getEmail());
            preparedStatement.setString(3, usuario.getSenhaHash());
            preparedStatement.setObject(4, usuario.getSemestreAtual());
            // ? 4 = SEMESTRE_ATUAL: e INT e pode ser vazio (mentor nao informa).
            // setObject aceita o numero (aluno) e tambem null (mentor)
            preparedStatement.setString(5, usuario.getFotoUrl());
            preparedStatement.setString(6, usuario.getStatus());
            preparedStatement.setString(7, usuario.getTipo());
            preparedStatement.execute();

            System.out.println("success in insert usuario");

            connection.close();

        } catch (Exception e) {

            // Qualquer erro tem que cai aqui (ex.: email repetido, ja que EMAIL e UNIQUE)
            System.out.println("fail in database connection");
            System.out.println("Error: " + e.getMessage());

        }

    }

// Criei a linha de perfil do mentorando (mesmo id do usuario)
// O id do perfil tem que ser o mesmo do usuario (relacao 1 para 1)
public void createMentorandoPerfil(String email) {
    String SQL = "INSERT INTO MENTORANDO_PERFIL (ID) SELECT ID FROM USUARIO WHERE EMAIL = ?";

    try{
        Connection connection = DriverManager.getConnection("jdbc:h2:./db/sensei", "sa", "sa");
        System.out.println("success in database connection");
        PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        preparedStatement.setString(1, email); // ? 1 = EMAIL do usuario recem cadastrado
        preparedStatement.execute();
        System.out.println("success in insert mentorando perfil");
        connection.close();

    } catch (Exception e) {
        System.out.println("fail in database connection");
        System.out.println("Error: " + e.getMessage());
        }
    }

public void createMentorPerfil(String email) {
    // O SELECT busca o ID do usuario pelo email. DESCRICAO e EXPERIENCIA ficam vazias
    // por enquanto (a tela de cadastro nao pergunta) e NOTA_MEDIA e TOTAL_MENTORIAS
    // comecam em 0 pelos DEFAULT do banco
    String SQL = "INSERT INTO MENTOR_PERFIL (ID) SELECT ID FROM USUARIO WHERE EMAIL = ?";

    try {
        Connection connection = DriverManager.getConnection("jdbc:h2:./db/sensei", "sa", "sa");

        System.out.println("success in database connection");

        PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        preparedStatement.setString(1, email); // ? 1 = EMAIL do usuario acabamos de cadastrar
        preparedStatement.execute();

        System.out.println("success in insert mentor perfil");

        connection.close();
    } catch (Exception e) {

        System.out.println("fail in database connection");
        System.out.println("Error: " + e.getMessage());

        }
    }
}

