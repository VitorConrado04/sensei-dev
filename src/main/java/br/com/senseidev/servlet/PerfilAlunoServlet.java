package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

/** Perfil do mentorando: visualiza e altera o semestre e a foto da própria conta. */
@WebServlet("/perfil-aluno")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class PerfilAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            UsuarioResumo usuario = new UsuarioDao(getServletContext()).buscarPorId(idDaSessao(req));
            if (usuario == null || !"MENTORANDO".equals(usuario.getTipo())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            req.setAttribute("usuario", usuario);
            req.getRequestDispatcher("/WEB-INF/views/aluno/PerfilAluno.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar o perfil.", e);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String destino = req.getContextPath() + "/perfil-aluno";
        int id = idDaSessao(req);
        Path novaFoto = null;
        try {
            UsuarioDao dao = new UsuarioDao(getServletContext());
            UsuarioResumo atual = dao.buscarPorId(id);
            if (atual == null || !"MENTORANDO".equals(atual.getTipo())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            // Força a validação do multipart antes de ler o semestre. Em um envio
            // grande demais o Tomcat pode devolver null para getParameter().
            Part foto = req.getPart("foto");
            int semestre;
            try { semestre = Integer.parseInt(req.getParameter("semestre")); }
            catch (NumberFormatException e) { semestre = 0; }
            if (semestre < 1 || semestre > 8) {
                resp.sendRedirect(destino + "?erro=semestre");
                return;
            }

            String fotoUrl = atual.getFotoUrl();
            if (foto != null && foto.getSize() > 0) {
                String extensao = extensaoValida(foto);
                if (extensao == null) {
                    resp.sendRedirect(destino + "?erro=foto");
                    return;
                }
                String caminho = getServletContext().getRealPath("/uploads/mentorandos");
                if (caminho == null) throw new IOException("Diretório de fotos indisponível.");
                Path pasta = Paths.get(caminho);
                Files.createDirectories(pasta);
                novaFoto = pasta.resolve(UUID.randomUUID().toString() + extensao);
                try (InputStream entrada = foto.getInputStream()) { Files.copy(entrada, novaFoto); }
                fotoUrl = "/uploads/mentorandos/" + novaFoto.getFileName();
            }

            if (!dao.atualizarPerfilMentorando(id, semestre, fotoUrl)) {
                apagar(novaFoto);
                resp.sendRedirect(destino + "?erro=salvar");
                return;
            }
            if (novaFoto != null && atual.getFotoUrl() != null
                    && atual.getFotoUrl().startsWith("/uploads/mentorandos/")) {
                Path antiga = novaFoto.getParent().resolve(Paths.get(atual.getFotoUrl()).getFileName());
                try { Files.deleteIfExists(antiga); } catch (IOException e) { getServletContext().log("Foto antiga não removida.", e); }
            }
            resp.sendRedirect(destino + "?salvo=1");
        } catch (IllegalStateException | ServletException e) {
            apagar(novaFoto);
            resp.sendRedirect(destino + "?erro=foto");
        } catch (SQLException | IOException e) {
            apagar(novaFoto);
            throw new ServletException("Não foi possível salvar o perfil.", e);
        }
    }

    private static int idDaSessao(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        return id instanceof Number ? ((Number) id).intValue() : 0;
    }

    private static String extensaoValida(Part foto) throws IOException {
        byte[] assinatura = new byte[8];
        try (InputStream entrada = foto.getInputStream()) {
            if (entrada.read(assinatura) < 8) return null;
        }
        if ("image/png".equalsIgnoreCase(foto.getContentType())
                && assinatura[0] == (byte) 0x89 && assinatura[1] == 0x50
                && assinatura[2] == 0x4e && assinatura[3] == 0x47
                && assinatura[4] == 0x0d && assinatura[5] == 0x0a
                && assinatura[6] == 0x1a && assinatura[7] == 0x0a) return ".png";
        if ("image/jpeg".equalsIgnoreCase(foto.getContentType())
                && assinatura[0] == (byte) 0xff && assinatura[1] == (byte) 0xd8
                && assinatura[2] == (byte) 0xff) return ".jpg";
        return null;
    }

    private static void apagar(Path foto) {
        if (foto != null) try { Files.deleteIfExists(foto); } catch (IOException ignored) { }
    }
}
