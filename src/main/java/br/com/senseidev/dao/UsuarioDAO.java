package br.com.senseidev.dao;

import br.com.senseidev.model.Mentor;
import br.com.senseidev.model.Mentorando;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UsuarioDAO {

    public void salvarMentor(Mentor mentor) throws SQLException {

        String sqlUsuario = "INSERT INTO usuario (nome, email, senha, semestre_atual, foto_url) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlPerfil = "INSERT INTO mentor_perfil (id, descricao, experiencia) "
                + "VALUES (?, ?, ?)";

        try (Connection con = ConexaoFactory.getConexao()) {

            con.setAutoCommit(false);

            try {
                // 1) grava na tabela usuario
                try (PreparedStatement psUsuario =
                             con.prepareStatement(sqlUsuario, new String[]{"ID"})) {

                    psUsuario.setString(1, mentor.getNome());
                    psUsuario.setString(2, mentor.getEmail());
                    psUsuario.setString(3, mentor.getSenhaHash());
                    if (mentor.getSemestreAtual() == null) {
                        psUsuario.setNull(4, Types.INTEGER);
                    } else {
                        psUsuario.setInt(4, mentor.getSemestreAtual());
                    }
                    psUsuario.setString(5, mentor.getFotoUrl());
                    psUsuario.executeUpdate();

                    // 2) pega o id que o banco acabou de gerar
                    try (ResultSet chaves = psUsuario.getGeneratedKeys()) {
                        if (chaves.next()) {
                            mentor.setId(chaves.getInt(1));
                        } else {
                            throw new SQLException("O banco não devolveu o id do usuário.");
                        }
                    }
                }

                // 3) grava na tabela mentor_perfil usando esse mesmo id
                try (PreparedStatement psPerfil = con.prepareStatement(sqlPerfil)) {
                    psPerfil.setInt(1, mentor.getId());
                    psPerfil.setString(2, mentor.getDescricao());
                    psPerfil.setString(3, mentor.getExperiencia());
                    psPerfil.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
    public void salvarMentorando(Mentorando mentorando) throws SQLException {

        String sqlUsuario = "INSERT INTO usuario (nome, email, senha, semestre_atual, foto_url) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlPerfil = "INSERT INTO mentorando_perfil (id) VALUES (?)";

        try (Connection con = ConexaoFactory.getConexao()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement psUsuario =
                             con.prepareStatement(sqlUsuario, new String[]{"ID"})) {

                    psUsuario.setString(1, mentorando.getNome());
                    psUsuario.setString(2, mentorando.getEmail());
                    psUsuario.setString(3, mentorando.getSenhaHash());
                    if (mentorando.getSemestreAtual() == null) {
                        psUsuario.setNull(4, Types.INTEGER);
                    } else {
                        psUsuario.setInt(4, mentorando.getSemestreAtual());
                    }
                    psUsuario.setString(5, mentorando.getFotoUrl());
                    psUsuario.executeUpdate();

                    try (ResultSet chaves = psUsuario.getGeneratedKeys()) {
                        if (chaves.next()) {
                            mentorando.setId(chaves.getInt(1));
                        } else {
                            throw new SQLException("O banco não devolveu o id do usuário.");
                        }
                    }
                }

                try (PreparedStatement psPerfil = con.prepareStatement(sqlPerfil)) {
                    psPerfil.setInt(1, mentorando.getId());
                    psPerfil.executeUpdate();
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}