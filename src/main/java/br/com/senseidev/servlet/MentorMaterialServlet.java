package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Sessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista, adiciona, edita e remove exclusivamente materiais do mentor autenticado.
 * Um material pode ser vinculado a uma mentoria; o mentorando dessa mentoria passa a consultá-lo.
 */
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

            // mentorias que podem receber material (as canceladas ficam de fora)
            List<Sessao> vinculaveis = new ArrayList<>();
            for (Sessao sessao : dao.listarSessoes(mentorId)) {
                if (!"CANCELADA".equals(sessao.getStatus())) vinculaveis.add(sessao);
            }
            req.setAttribute("mentorias", vinculaveis);
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
        String acao = req.getParameter("acao");

        if ("remover".equals(acao)) {
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
        String descricao = req.getParameter("descricao");
        titulo = titulo == null ? "" : titulo.trim();
        url = url == null ? "" : url.trim();
        descricao = descricao == null ? "" : descricao.trim();

        if (titulo.isEmpty() || titulo.length() > 150 || url.length() > 500 || descricao.length() > 500
                || !(url.startsWith("https://") || url.startsWith("http://"))) {
            resp.sendRedirect(destino + "?erro=1");
            return;
        }
        if (descricao.isEmpty()) descricao = null;

        // vínculo opcional com uma mentoria (campo vazio = material geral)
        Integer mentoriaId = null;
        String vinculo = req.getParameter("mentoriaId");
        if (vinculo != null && !vinculo.trim().isEmpty()) {
            try {
                mentoriaId = Integer.valueOf(vinculo.trim());
                if (mentoriaId <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                resp.sendRedirect(destino + "?erro=vinculo");
                return;
            }
        }

        try {
            boolean ok;
            if ("editar".equals(acao)) {
                int materialId;
                try {
                    materialId = Integer.parseInt(req.getParameter("id"));
                } catch (NumberFormatException e) {
                    resp.sendRedirect(destino + "?erro=1");
                    return;
                }
                ok = dao.editarMaterial(mentorId, materialId, titulo, url, descricao, mentoriaId);
            } else {
                ok = dao.inserirMaterial(mentorId, titulo, url, descricao, mentoriaId);
            }
            resp.sendRedirect(destino + (ok ? "?salvo=1" : "?erro=vinculo"));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível salvar o material.", e);
        }
    }
}
