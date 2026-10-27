package br.com.senseidev.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Página inicial mínima e protegida para confirmar que a conta de mentorando autenticou. */
@WebServlet("/inicio-aluno")
public class InicioAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/aluno/InicioAluno.jsp").forward(req, resp);
    }
}