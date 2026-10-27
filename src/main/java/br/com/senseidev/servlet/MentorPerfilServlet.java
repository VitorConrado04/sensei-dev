package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Mentor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Consulta e atualiza apenas o perfil do mentor autenticado. */
@WebServlet("/mentor-perfil")
public class MentorPerfilServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        try {
            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("disciplinas", dao.listarDisciplinas(mentorId));
            req.setAttribute("grade", dao.buscarGrade(mentorId));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar o perfil do mentor.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/mentor/PerfilMentor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        try {
            Mentor atual = dao.buscarMentor(mentorId);
            if (atual == null) throw new ServletException("O perfil deste mentor não foi encontrado.");

            String descricao = req.getParameter("descricao");
            if (descricao == null || descricao.trim().isEmpty()) descricao = atual.getDescricao();
            descricao = descricao == null ? "" : descricao.trim();
            if (descricao.length() > 1000) descricao = descricao.substring(0, 1000);

            String[] marcadosArray = req.getParameterValues("disp");
            List<String> marcados = marcadosArray == null
                    ? new ArrayList<>() : Arrays.asList(marcadosArray);
            dao.salvarPerfil(mentorId, descricao, marcados);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível salvar o perfil do mentor.", e);
        }
        resp.sendRedirect(req.getContextPath() + "/mentor-perfil?salvo=1");
    }
}