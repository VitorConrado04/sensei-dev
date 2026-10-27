package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** Lista, adiciona e remove exclusivamente materiais do mentor autenticado. */
@WebServlet("/mentor-material")
public class MentorMaterialServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        try {
            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("materiais", dao.listarMateriais(mentorId));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar os materiais.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/mentor/MateriaisMentor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        String destino = req.getContextPath() + "/mentor-material";

        if ("remover".equals(req.getParameter("acao"))) {
            try {
                int materialId = Integer.parseInt(req.getParameter("id"));
                dao.removerMaterial(mentorId, materialId);
            } catch (NumberFormatException ignored) {
                // ID inválido: não modifica o banco.
            } catch (SQLException e) {
                throw new ServletException("Não foi possível remover o material.", e);
            }
            resp.sendRedirect(destino);
            return;
        }

        String titulo = req.getParameter("titulo");
        String url = req.getParameter("url");
        titulo = titulo == null ? "" : titulo.trim();
        url = url == null ? "" : url.trim();

        if (titulo.isEmpty() || titulo.length() > 150 || url.length() > 500
                || !(url.startsWith("https://") || url.startsWith("http://"))) {
            resp.sendRedirect(destino + "?erro=1");
            return;
        }
        try {
            dao.inserirMaterial(mentorId, titulo, url);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível salvar o material.", e);
        }
        resp.sendRedirect(destino + "?salvo=1");
    }
}