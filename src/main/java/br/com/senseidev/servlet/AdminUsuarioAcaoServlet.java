package br.com.senseidev.servlet;

import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Acoes do admin sobre um usuario: suspender, reativar e excluir (botoes da lista)
@WebServlet("/admin/usuario-acao")
public class AdminUsuarioAcaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String lista = req.getContextPath() + "/admin/usuarios";
        int id = lerId(req.getParameter("id"));
        String acao = req.getParameter("acao");

        try {
            UsuarioDao dao = new UsuarioDao(getServletContext());
            UsuarioResumo alvo = id > 0 ? dao.buscarPorId(id) : null;

            if (alvo == null) {
                resp.sendRedirect(lista);
                return;
            }
            // O admin nao pode ser suspenso nem excluido (evita trancar o sistema)
            if ("ADMIN".equals(alvo.getTipo())) {
                resp.sendRedirect(lista + "?erro=admin");
                return;
            }

            if ("suspender".equals(acao)) {
                dao.alterarStatus(id, "SUSPENSO");
                resp.sendRedirect(lista + "?ok=suspenso");
            } else if ("reativar".equals(acao)) {
                dao.alterarStatus(id, "ATIVO");
                resp.sendRedirect(lista + "?ok=reativado");
            } else if ("excluir".equals(acao)) {
                dao.excluirUsuario(id);
                resp.sendRedirect(lista + "?ok=excluido");
            } else {
                resp.sendRedirect(lista + "?erro=acao");
            }

        } catch (SQLException e) {
            // 23503 = violou chave estrangeira: o usuario tem dados ligados a ele
            if ("23503".equals(e.getSQLState())) {
                resp.sendRedirect(lista + "?erro=vinculos");
            } else {
                getServletContext().log("Erro na acao sobre o usuario.", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private static int lerId(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (RuntimeException e) {
            return 0;
        }
    }
}