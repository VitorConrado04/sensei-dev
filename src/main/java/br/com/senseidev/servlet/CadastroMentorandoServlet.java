package br.com.senseidev.servlet;

import br.com.senseidev.auth.SenhaUtil;
import br.com.senseidev.dao.MentorDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

/** Processa o cadastro de mentorando; antes, o formulário só enviava para o próprio JSP. */
@WebServlet("/cadastro-aluno")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024,
        fileSizeThreshold = 1024 * 1024)
public class CadastroMentorandoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nome = trim(req.getParameter("nome"));
        String email = trim(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String senha = req.getParameter("senha");
        String confirmarSenha = req.getParameter("confirmarSenha");
        Integer semestre = parseSemestre(req.getParameter("semestre"));

        if (nome.isEmpty() || nome.length() > 150 || email.isEmpty() || email.length() > 150
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || senha == null || senha.length() < 8
                || confirmarSenha == null || !senha.equals(confirmarSenha)
                || semestre == null) {
            redirecionarErro(req, resp, "dados");
            return;
        }

        Path fotoSalva = null;
        try {
            Part foto = req.getPart("foto");
            String fotoUrl = null;
            if (foto != null && foto.getSize() > 0) {
                String tipo = foto.getContentType();
                String extensao;
                if ("image/png".equalsIgnoreCase(tipo)) extensao = ".png";
                else if ("image/jpeg".equalsIgnoreCase(tipo)) extensao = ".jpg";
                else {
                    redirecionarErro(req, resp, "foto");
                    return;
                }

                String caminhoReal = getServletContext().getRealPath("/uploads/mentorandos");
                if (caminhoReal == null) {
                    redirecionarErro(req, resp, "foto");
                    return;
                }
                Path pasta = new File(caminhoReal).toPath();
                Files.createDirectories(pasta);
                fotoSalva = pasta.resolve(UUID.randomUUID().toString().replace("-", "") + extensao);
                try (InputStream entrada = foto.getInputStream()) {
                    Files.copy(entrada, fotoSalva, StandardCopyOption.REPLACE_EXISTING);
                }
                fotoUrl = "/uploads/mentorandos/" + fotoSalva.getFileName();
            }

            MentorDao dao = new MentorDao(getServletContext());
            if (dao.emailCadastrado(email)) {
                apagarFoto(fotoSalva);
                redirecionarErro(req, resp, "email");
                return;
            }
            dao.cadastrarMentorando(nome, email, SenhaUtil.gerarHash(senha), semestre, fotoUrl);
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?cadastro=sucesso");
        } catch (IllegalStateException e) {
            apagarFoto(fotoSalva);
            redirecionarErro(req, resp, "foto");
        } catch (SQLException e) {
            apagarFoto(fotoSalva);
            getServletContext().log("Erro ao cadastrar mentorando.", e);
            redirecionarErro(req, resp, "23505".equals(e.getSQLState()) ? "email" : "indisponivel");
        }
    }

    private static String trim(String valor) { return valor == null ? "" : valor.trim(); }

    private static Integer parseSemestre(String valor) {
        try {
            int semestre = Integer.parseInt(valor);
            return semestre >= 1 && semestre <= 8 ? semestre : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static void apagarFoto(Path foto) {
        if (foto == null) return;
        try { Files.deleteIfExists(foto); } catch (IOException ignored) { }
    }

    private void redirecionarErro(HttpServletRequest req, HttpServletResponse resp, String codigo)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/CadastroAluno.jsp?erro=" + codigo);
    }
}