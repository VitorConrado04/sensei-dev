package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Mentor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// perfil do mentor: mostra os dados e salva a descrição e a disponibilidade
@WebServlet("/mentor-perfil")
public class MentorPerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;

        req.setAttribute("mentor", dao.buscarMentor(mentorId));
        req.setAttribute("disciplinas", dao.listarDisciplinas(mentorId));
        req.setAttribute("grade", dao.buscarGrade(mentorId));

        req.getRequestDispatcher("/PerfilMentor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;
        Mentor atual = dao.buscarMentor(mentorId);

        String descricao = req.getParameter("descricao");
        if (descricao == null || descricao.isBlank()) {
            descricao = atual.getDescricao();   // descrição vazia: mantém a que já existia
        }
        descricao = descricao.trim();
        if (descricao.length() > 1000) {        // limite da coluna descricao
            descricao = descricao.substring(0, 1000);
        }

        // cada checkbox marcado chega com o mesmo nome ("disp"), então vem como um array
        // se nenhum estiver marcado o parâmetro nem existe e o retorno é null
        String[] marcadosArray = req.getParameterValues("disp");
        List<String> marcados = new ArrayList<>();
        if (marcadosArray != null) {
            marcados = Arrays.asList(marcadosArray);
        }

        dao.salvarPerfil(mentorId, descricao, marcados);

        resp.sendRedirect(req.getContextPath() + "/mentor-perfil");
    }
}
