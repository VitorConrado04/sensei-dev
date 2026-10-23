package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDAO;
import br.com.senseidev.model.Mentor;
import br.com.senseidev.util.SenhaUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cadastro-mentor")
@MultipartConfig
public class CadastroMentorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");

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

        Mentor mentor = new Mentor(nome.trim(), email.trim(), SenhaUtil.gerarHash(senha),
                null, null, null, null);

        try {
            new UsuarioDAO().salvarMentor(mentor);
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                voltarComErro(request, response, "Esse e-mail já está cadastrado.");
            } else {
                throw new ServletException("Erro ao salvar o mentor", e);
            }
            return;
        }

        response.sendRedirect(request.getContextPath() + "/LoginArea.jsp");
    }

    private void voltarComErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws ServletException, IOException {
        request.setAttribute("erro", mensagem);
        request.getRequestDispatcher("/CadastroMentor.jsp").forward(request, response);
    }
}