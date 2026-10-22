package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.Mentor;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Permite ler formularios multipart/form-data (o formulario tem upload de foto)
@MultipartConfig
// Mesma URL que o action do formulario de CadastroMentor.jsp ja usa
@WebServlet("/cadastro-mentor")
public class CadastroMentorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Acentos chegarem certos; tem que vir antes do primeiro getParameter
        req.setCharacterEncoding("UTF-8");

        // Le os campos do formulario pelo atributo name de cada input
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        // O formulario do mentor nao pede semestre, descricao nem experiencia,
        // entao esses campos e a foto vao como null (ordem do construtor do Mentor:
        // nome, email, senha, semestre, foto, descricao, experiencia)
        Mentor mentor = new Mentor(nome, email, senha, null, null, null, null);

        UsuarioDao usuarioDao = new UsuarioDao();
        usuarioDao.createUsuario(mentor);     // grava em USUARIO (tipo MENTOR)
        usuarioDao.createMentorPerfil(email); // grava em MENTOR_PERFIL com o mesmo id

        // Usa redirect (e nao forward): o navegador abre uma nova pagina (/LoginArea.jsp).
        // Assim, se o usuario apertar F5, o formulario nao e reenviado e nao cadastra de novo
        resp.sendRedirect("/LoginArea.jsp");
    }

}