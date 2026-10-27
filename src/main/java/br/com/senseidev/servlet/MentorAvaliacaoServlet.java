package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Avaliacao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Consulta avaliações recebidas pelo mentor autenticado. */
@WebServlet("/mentor-avaliacao")
public class MentorAvaliacaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        try {
            List<Avaliacao> avaliacoes = dao.listarAvaliacoes(mentorId);
            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("avaliacoes", avaliacoes);
            req.setAttribute("totalAvaliacoes", avaliacoes.size());
            req.setAttribute("notaGeral", dao.buscarNotaGeral(mentorId));
            req.setAttribute("criterios", dao.buscarMediasPorCriterio(mentorId));
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar as avaliações do mentor.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/mentor/AvaliacaoMentor.jsp").forward(req, resp);
    }
}