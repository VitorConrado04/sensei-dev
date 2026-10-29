package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.dao.NotificacaoDao;
import br.com.senseidev.dao.UsuarioDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Lista as notificações de quem está logado (mentor ou mentorando) e permite lê-las e excluí-las. */
@WebServlet("/notificacoes")
public class NotificacaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        int usuarioId = ((Number) sessao.getAttribute("usuarioId")).intValue();
        String tipo = String.valueOf(sessao.getAttribute("usuarioTipo"));
        try {
            if ("MENTOR".equals(tipo)) {
                req.setAttribute("mentor",
                        new MentorDao(getServletContext()).buscarMentor(SessaoUtil.obterMentorId(req)));
            } else {
                req.setAttribute("usuario", new UsuarioDao(getServletContext()).buscarPorId(usuarioId));
            }
            req.setAttribute("tipoUsuario", tipo);
            req.setAttribute("notificacoes", new NotificacaoDao(getServletContext()).listar(usuarioId));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar as notificações.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/notificacoes/Notificacoes.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        int usuarioId = ((Number) sessao.getAttribute("usuarioId")).intValue();
        String acao = req.getParameter("acao");
        try {
            NotificacaoDao dao = new NotificacaoDao(getServletContext());
            if ("todas".equals(acao)) {
                dao.marcarTodasComoLidas(usuarioId);
            } else {
                int id = lerId(req.getParameter("id"));
                if (id > 0 && "lida".equals(acao)) dao.marcarComoLida(usuarioId, id);
                else if (id > 0 && "excluir".equals(acao)) dao.excluir(usuarioId, id);
            }
        } catch (SQLException e) {
            throw new ServletException("Não foi possível atualizar as notificações.", e);
        }
        resp.sendRedirect(req.getContextPath() + "/notificacoes");
    }

    private static int lerId(String texto) {
        try { return Integer.parseInt(texto); }
        catch (NumberFormatException e) { return 0; }
    }
}