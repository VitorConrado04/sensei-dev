package br.com.senseidev.servlet;

import br.com.senseidev.auth.TokenMentoria;
import br.com.senseidev.dao.AvaliacaoDao;
import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.MentoriaAluno;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Formulário e gravação da avaliação de uma sessão realizada (feita pelo mentorando). */
@WebServlet("/avaliar-sessao")
public class AvaliarSessaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // mesma ordem usada pelo AvaliacaoDao.registrar
    private static final String[] CRITERIOS = {"clareza", "dominio", "pontualidade", "orientar", "geral"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            UsuarioResumo usuario = alunoAutenticado(req);
            if (usuario == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            int mentoriaId = lerId(req.getParameter("id"));
            MentoriaAluno mentoria = mentoriaId > 0
                    ? new AvaliacaoDao(getServletContext()).buscarAvaliavel(usuario.getId(), mentoriaId)
                    : null;
            if (mentoria == null) {
                resp.sendRedirect(req.getContextPath() + "/mentorias-aluno?resultado=avaliacao-indisponivel");
                return;
            }
            req.setAttribute("usuario", usuario);
            req.setAttribute("mentoria", mentoria);
            req.setAttribute("tokenMentoria", TokenMentoria.obter(req.getSession(false)));
            req.getRequestDispatcher("/WEB-INF/views/aluno/AvaliarSessao.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar o formulário de avaliação.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sessao = req.getSession(false);
        if (!TokenMentoria.valido(sessao, req.getParameter("token"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            UsuarioResumo usuario = alunoAutenticado(req);
            if (usuario == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            int mentoriaId = lerId(req.getParameter("id"));
            if (mentoriaId <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int[] notas = new int[CRITERIOS.length];
            try {
                for (int i = 0; i < CRITERIOS.length; i++) {
                    notas[i] = Integer.parseInt(req.getParameter(CRITERIOS[i]));
                    if (notas[i] < 1 || notas[i] > 5) throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/avaliar-sessao?id=" + mentoriaId + "&erro=notas");
                return;
            }

            String comentario = req.getParameter("comentario");
            comentario = comentario == null ? "" : comentario.trim();
            if (comentario.length() > 1000) comentario = comentario.substring(0, 1000);
            if (comentario.isEmpty()) comentario = null;

            boolean gravada;
            try {
                gravada = new AvaliacaoDao(getServletContext())
                        .registrar(usuario.getId(), mentoriaId, notas, comentario);
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505) gravada = false;   // sessão já avaliada
                else throw e;
            }
            resp.sendRedirect(req.getContextPath() + "/mentorias-aluno?resultado="
                    + (gravada ? "avaliada" : "avaliacao-indisponivel"));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível salvar a avaliação.", e);
        }
    }

    /** Devolve o aluno logado e ativo, ou null se a sessão não for de um mentorando válido. */
    private UsuarioResumo alunoAutenticado(HttpServletRequest req) throws SQLException {
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        if (!(id instanceof Number)) return null;
        UsuarioResumo usuario = new UsuarioDao(getServletContext()).buscarPorId(((Number) id).intValue());
        if (usuario == null || !"MENTORANDO".equals(usuario.getTipo())
                || !"ATIVO".equals(usuario.getStatus())) return null;
        return usuario;
    }

    private static int lerId(String texto) {
        try { return Integer.parseInt(texto); }
        catch (NumberFormatException e) { return 0; }
    }
}