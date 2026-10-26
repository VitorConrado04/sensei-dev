package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Avaliacao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// avaliações que o mentor recebeu (só leitura)
@WebServlet("/mentor-avaliacao")
public class MentorAvaliacaoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;

        List<Avaliacao> avaliacoes = dao.listarAvaliacoes(mentorId);

        req.setAttribute("mentor", dao.buscarMentor(mentorId));
        req.setAttribute("avaliacoes", avaliacoes);
        req.setAttribute("totalAvaliacoes", avaliacoes.size());
        req.setAttribute("notaGeral", dao.buscarNotaGeral(mentorId));
        req.setAttribute("criterios", dao.buscarMediasPorCriterio(mentorId));

        req.getRequestDispatcher("/AvaliacaoMentor.jsp").forward(req, resp);
    }
}
