package br.com.senseidev.servlet;

import br.com.senseidev.dao.PerfilMentorPublicoDao;
import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.HorarioGrade;
import br.com.senseidev.model.PerfilMentorPublico;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Perfil público e somente de leitura de um mentor para um mentorando autenticado. */
@WebServlet("/mentor-publico")
public class PerfilMentorPublicoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId;
        try {
            mentorId = Integer.parseInt(req.getParameter("id"));
            if (mentorId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        HttpSession sessao = req.getSession(false);
        Object usuarioId = sessao == null ? null : sessao.getAttribute("usuarioId");
        if (!(usuarioId instanceof Number)) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }

        try {
            UsuarioResumo aluno = new UsuarioDao(getServletContext())
                    .buscarPorId(((Number) usuarioId).intValue());
            if (aluno == null || !"MENTORANDO".equals(aluno.getTipo())
                    || !"ATIVO".equals(aluno.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }

            PerfilMentorPublicoDao dao = new PerfilMentorPublicoDao(getServletContext());
            PerfilMentorPublico mentor = dao.buscar(mentorId);
            if (mentor == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            List<HorarioGrade> grade = dao.disponibilidadeSemanal(mentorId);
            boolean temDisponibilidade = false;
            for (HorarioGrade linha : grade) {
                for (String dia : linha.getDias()) {
                    if ("D".equals(dia)) temDisponibilidade = true;
                }
            }
            req.setAttribute("aluno", aluno);
            req.setAttribute("mentor", mentor);
            req.setAttribute("disciplinas", dao.disciplinas(mentorId));
            req.setAttribute("avaliacoes", dao.avaliacoes(mentorId));
            req.setAttribute("grade", grade);
            req.setAttribute("temDisponibilidade", temDisponibilidade);
            req.getRequestDispatcher("/WEB-INF/views/aluno/PerfilMentorPublico.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar o perfil do mentor.", e);
        }
    }
}
