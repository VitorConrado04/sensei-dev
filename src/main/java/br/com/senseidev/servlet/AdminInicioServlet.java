package br.com.senseidev.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Pagina inicial do admin. O AutenticacaoFiltro ja barra quem nao e ADMIN (rota /admin/*)
@WebServlet("/admin/inicio")
public class AdminInicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/admin/InicioAdmin.jsp").forward(req, resp);
    }
}