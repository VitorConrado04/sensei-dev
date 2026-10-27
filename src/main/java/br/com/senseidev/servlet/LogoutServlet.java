package br.com.senseidev.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        encerrarSessao(req);
        resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?saiu=1");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    private void encerrarSessao(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        if (sessao != null) sessao.invalidate();
    }
}