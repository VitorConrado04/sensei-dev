package br.com.senseidev.servlet;

import br.com.senseidev.model.Disciplina;
import br.com.senseidev.auth.SenhaUtil;
import br.com.senseidev.dao.DisciplinaDao;
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
import java.util.LinkedHashSet;
import java.util.Set;
import br.com.senseidev.dao.MentorDisciplinaDao;

@WebServlet("/cadastro-mentor")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024, fileSizeThreshold = 1024 * 1024)
public class CadastroMentorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Abre a tela de cadastro ja com a lista de disciplinas (vem do banco).
    // A pagina CadastroMentor.jsp nao busca sozinha, por isso passa por aqui.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("disciplinas", new DisciplinaDao(getServletContext()).listarDisciplinas());
            req.getRequestDispatcher("/CadastroMentor.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao listar disciplinas.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }






    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nome = trim(req.getParameter("nome"));
        String email = trim(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String senha = req.getParameter("senha");
        String confirmarSenha = req.getParameter("confirmarSenha");
        Integer semestre = parseSemestre(req.getParameter("semestre"));
        Set<Integer> disciplinas = lerDisciplinas(req.getParameterValues("disciplina"));

        if (nome.isEmpty() || nome.length() > 150 || email.isEmpty() || email.length() > 150
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || senha == null || senha.length() < 8 || confirmarSenha == null || !senha.equals(confirmarSenha)
                || semestre == null) {
            redirecionarErro(req, resp, "dados");
            return;
        }

        if (disciplinas.isEmpty()) {
            redirecionarErro(req, resp, "disciplinas");
            return;
        }

        String fotoUrl = null;
        Path arquivoFoto = null;
        try {
            Part parteFoto = req.getPart("foto");
            if (parteFoto != null && parteFoto.getSize() > 0) {
                String tipo = parteFoto.getContentType();
                final String extensao;
                if ("image/png".equalsIgnoreCase(tipo)) extensao = ".png";
                else if ("image/jpeg".equalsIgnoreCase(tipo)) extensao = ".jpg";
                else {
                    redirecionarErro(req, resp, "foto");
                    return;
                }
                String caminho = getServletContext().getRealPath("/uploads/mentores");
                if (caminho == null) {
                    redirecionarErro(req, resp, "foto");
                    return;
                }
                Path diretorio = new File(caminho).toPath();
                Files.createDirectories(diretorio);
                String nomeArquivo = UUID.randomUUID().toString().replace("-", "") + extensao;
                arquivoFoto = diretorio.resolve(nomeArquivo);
                try (InputStream entrada = parteFoto.getInputStream()) {
                    Files.copy(entrada, arquivoFoto, StandardCopyOption.REPLACE_EXISTING);
                }
                fotoUrl = "/uploads/mentores/" + nomeArquivo;
            }

            MentorDao dao = new MentorDao(getServletContext());
            if (dao.emailCadastrado(email)) {
                apagarFoto(arquivoFoto);
                redirecionarErro(req, resp, "email");
                return;
            }
            DisciplinaDao disciplinaDao = new DisciplinaDao(getServletContext());
            for (int disciplinaId : disciplinas) {
                // recusa disciplina que nao existe ou de semestre acima do semestre do mentor
                Disciplina escolhida = disciplinaDao.buscarPorId(disciplinaId);
                if (escolhida == null || escolhida.getSemestre() > semestre) {
                    apagarFoto(arquivoFoto);
                    redirecionarErro(req, resp, "disciplinas");
                    return;
                }
            }
            int mentorId = dao.cadastrarMentor(nome, email, SenhaUtil.gerarHash(senha), semestre, fotoUrl);
            new MentorDisciplinaDao(getServletContext()).salvar(mentorId, disciplinas);
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?cadastro=sucesso");
        } catch (IllegalStateException e) {
            apagarFoto(arquivoFoto);
            redirecionarErro(req, resp, "foto");
        } catch (SQLException e) {
            apagarFoto(arquivoFoto);
            getServletContext().log("Erro ao cadastrar mentor.", e);
            redirecionarErro(req, resp, "23505".equals(e.getSQLState()) ? "email" : "indisponivel");
        }
    }

    private static String trim(String valor) { return valor == null ? "" : valor.trim(); }

    private static Integer parseSemestre(String valor) {
        try {
            int semestre = Integer.parseInt(valor);
            return semestre >= 1 && semestre <= 5 ? semestre : null;
        } catch (RuntimeException e) { return null; }
    }

    // Converte os valores marcados (checkboxes "disciplina") em numeros; ignora o que nao for numero.
    private static Set<Integer> lerDisciplinas(String[] valores) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (valores == null) return ids;
        for (String valor : valores) {
            try { ids.add(Integer.parseInt(valor.trim())); } catch (RuntimeException e) { /* ignora */ }
        }
        return ids;
    }





    private static void apagarFoto(Path foto) {
        if (foto == null) return;
        try { Files.deleteIfExists(foto); } catch (IOException ignored) { }
    }

    private void redirecionarErro(HttpServletRequest req, HttpServletResponse resp, String codigo) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/cadastro-mentor?erro=" + codigo);
    }
}