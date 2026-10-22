package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.Mentorando;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Permite ler formularios enviados como multipart/form-data (o formulario tem upload de foto).
// Sem isso, req.getParameter() vai devolver null para todos os campos
@MultipartConfig
// Define a URL deste servlet; o action do formulario aponta para ela
@WebServlet("/cadastro-mentorando")
public class CadastroMentorandoServlet extends HttpServlet {
    // doPost porque o formulario envia method="post"
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Dizer que o texto recebido esta em UTF-8, para acentos (ex.: "Joao") chegarem certos.
        // Tem que vir ANTES do primeiro getParameter
        request.setCharacterEncoding("UTF-8");

        // Ler os campos do formulario pelo atributo name de cada input
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String semestre = request.getParameter("semestre"); // chega como texto, exemplo: "2"

        // Montar o objeto: Integer.parseInt converte o texto "2" no numero 2.
        // O ultimo argumento e a foto, que fica null por enquanto
        Mentorando mentorando = new Mentorando(nome, email, senha, Integer.parseInt(semestre), null);

        // Entregar ao DAO, que faz o INSERT no banco
        UsuarioDao usuarioDao = new UsuarioDao();
        usuarioDao.createUsuario(mentorando);

        // Depois de cadastrar, manda o usuario para a tela de login
        // Usei o redirect (e nao forward):porque o navegador abre uma nova pagina (/LoginArea.jsp).
        // Assim, se o usuario apertar F5, o formulario nao e reenviado e nao cadastra de novo
        response.sendRedirect("/LoginArea.jsp");
    }
}