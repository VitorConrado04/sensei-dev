package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentorDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// materiais que o mentor disponibiliza (listar, adicionar e remover)
@WebServlet("/mentor-material")
public class MentorMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;

        req.setAttribute("mentor", dao.buscarMentor(mentorId));
        req.setAttribute("materiais", dao.listarMateriais(mentorId));

        req.getRequestDispatcher("/MateriaisMentor.jsp").forward(req, resp);
    }

    // o mesmo POST atende dois formulários: o de remover (acao=remover) e o de adicionar
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // sem isso o Tomcat lê o corpo do POST em ISO-8859-1 e quebra os acentos
        req.setCharacterEncoding("UTF-8");

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;
        String destino = req.getContextPath() + "/mentor-material";

        if ("remover".equals(req.getParameter("acao"))) {
            try {
                dao.removerMaterial(mentorId, Integer.parseInt(req.getParameter("id")));
            } catch (NumberFormatException e) {
                // id inválido, só recarrega
            }
            resp.sendRedirect(destino);
            return;
        }

        String titulo = req.getParameter("titulo");
        String url = req.getParameter("url");

        if (titulo != null) {
            titulo = titulo.trim();
        }
        if (url != null) {
            url = url.trim();
        }

        if (!dadosValidos(titulo, url)) {
            // a página mostra a mensagem quando recebe ?erro=1
            resp.sendRedirect(destino + "?erro=1");
            return;
        }

        dao.inserirMaterial(mentorId, titulo, url);
        resp.sendRedirect(destino);
    }

    // os limites de tamanho são os mesmos das colunas da tabela material
    // e só aceita http/https pra ninguém salvar um link "javascript:..."
    private boolean dadosValidos(String titulo, String url) {
        if (titulo == null || titulo.isEmpty() || titulo.length() > 150) {
            return false;
        }
        if (url == null || url.length() > 500) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
