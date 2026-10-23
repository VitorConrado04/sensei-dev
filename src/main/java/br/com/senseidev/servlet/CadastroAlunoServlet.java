package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDAO;
import br.com.senseidev.model.Mentorando;
import br.com.senseidev.util.SenhaUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cadastro-aluno")
@MultipartConfig
public class CadastroAlunoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");
        String semestreTexto = request.getParameter("semestre");

        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || senha == null || senha.length() < 6) {
            voltarComErro(request, response, "Preencha nome, e-mail e uma senha de pelo menos 6 caracteres.");
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            voltarComErro(request, response, "As senhas não coincidem.");
            return;
        }

        Integer semestre;
        try {
            semestre = Integer.valueOf(semestreTexto);
        } catch (NumberFormatException e) {
            voltarComErro(request, response, "Selecione o seu semestre.");
            return;
        }

        Mentorando mentorando = new Mentorando(nome.trim(), email.trim(),
                SenhaUtil.gerarHash(senha), semestre, null);

        try {
            new UsuarioDAO().salvarMentorando(mentorando);
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                voltarComErro(request, response, "Esse e-mail já está cadastrado.");
            } else {
                throw new ServletException("Erro ao salvar o aluno", e);
            }
            return;
        }

        response.sendRedirect(request.getContextPath() + "/LoginArea.jsp");
    }

    private void voltarComErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws ServletException, IOException {
        request.setAttribute("erro", mensagem);
        request.getRequestDispatcher("/CadastroAluno.jsp").forward(request, response);
    }
}