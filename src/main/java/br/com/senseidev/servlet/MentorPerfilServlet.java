package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.DisciplinaDao;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.dao.MentorDisciplinaDao;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

            // Para o modo edição: todas as disciplinas do curso e quais o mentor já marcou
            req.setAttribute("todasDisciplinas", new DisciplinaDao(getServletContext()).listarDisciplinas());
            Map<Integer, Boolean> marcadas = new HashMap<>();
            for (Integer disciplinaId : new MentorDisciplinaDao(getServletContext()).listarIds(mentorId)) {
                marcadas.put(disciplinaId, true);
            }
            req.setAttribute("marcadas", marcadas);
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
        String destino = req.getContextPath() + "/mentor-perfil";

        // Lê as disciplinas marcadas antes de gravar qualquer coisa
        Set<Integer> disciplinaIds = new HashSet<>();
        String[] valores = req.getParameterValues("disciplina");
        if (valores != null) {
            if (valores.length > 100) {
                resp.sendRedirect(destino + "?erro=disciplinas");
                return;
            }
            try {
                for (String valor : valores) {
                    int disciplinaId = Integer.parseInt(valor);
                    if (disciplinaId <= 0) throw new NumberFormatException();
                    disciplinaIds.add(disciplinaId);
                }
            } catch (NumberFormatException e) {
                resp.sendRedirect(destino + "?erro=disciplinas");
                return;
            }
        }

        try {
            Mentor atual = dao.buscarMentor(mentorId);
            if (atual == null) throw new ServletException("O perfil deste mentor não foi encontrado.");

            String descricao = req.getParameter("descricao");
            if (descricao == null || descricao.trim().isEmpty()) descricao = atual.getDescricao();
            descricao = descricao == null ? "" : descricao.trim();
            if (descricao.length() > 1000) descricao = descricao.substring(0, 1000);

            String experiencia = req.getParameter("experiencia");
            if (experiencia == null) experiencia = atual.getExperiencia();
            experiencia = experiencia == null ? "" : experiencia.trim();
            if (experiencia.length() > 1000) experiencia = experiencia.substring(0, 1000);

            String[] marcadosArray = req.getParameterValues("disp");
            List<String> marcados = marcadosArray == null
                    ? new ArrayList<>() : Arrays.asList(marcadosArray);
            dao.salvarPerfil(mentorId, descricao, experiencia, marcados);
            new MentorDisciplinaDao(getServletContext()).salvar(mentorId, disciplinaIds);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível salvar o perfil do mentor.", e);
        }
        resp.sendRedirect(destino + "?salvo=1");
    }
}