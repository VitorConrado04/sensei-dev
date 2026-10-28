package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Lista os usuarios para o admin. Rota /admin/*: o AutenticacaoFiltro ja barra quem nao e ADMIN
@WebServlet("/admin/usuarios")
public class AdminUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // Guarda a lista no request e encaminha para o JSP
            req.setAttribute("usuarios", new UsuarioDao(getServletContext()).listarUsuarios());
            req.getRequestDispatcher("/WEB-INF/views/admin/Usuarios.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao listar usuarios.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}