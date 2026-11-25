package br.com.senseidev.servlet;

import br.com.senseidev.dao.InicioPublicoDao;
import br.com.senseidev.model.DadosInicioPublico;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Página pública com indicadores e avaliações carregados do banco de dados. */
@WebServlet("/inicio-publico")
public class InicioPublicoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        try {
            DadosInicioPublico dados = new InicioPublicoDao(getServletContext()).buscarDados();
            req.setAttribute("dados", dados);
            req.setAttribute("dadosDisponiveis", true);
        } catch (SQLException e) {
            getServletContext().log("Não foi possível carregar os dados públicos da página inicial.", e);
            req.setAttribute("dadosDisponiveis", false);
        }

        HttpSession sessao = req.getSession(false);
        if (sessao != null) {
            Object tipo = sessao.getAttribute("usuarioTipo");
            if ("MENTOR".equals(tipo)) {
                req.setAttribute("urlPainel", req.getContextPath() + "/mentor-inicio");
            } else if ("MENTORANDO".equals(tipo)) {
                req.setAttribute("urlPainel", req.getContextPath() + "/inicio-aluno");
            } else if ("ADMIN".equals(tipo)) {
                req.setAttribute("urlPainel", req.getContextPath() + "/admin/inicio");
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/publico/InicioPublico.jsp").forward(req, resp);
    }
}
