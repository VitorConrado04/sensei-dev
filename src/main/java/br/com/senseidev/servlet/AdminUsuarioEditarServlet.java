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
import java.util.Locale;

// Editar usuario (admin). doGet abre o formulario preenchido; doPost salva
@WebServlet("/admin/usuario-editar")
public class AdminUsuarioEditarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Abre o formulario com os dados atuais do usuario (link Editar da lista)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = lerId(req.getParameter("id"));
        try {
            UsuarioResumo usuario = id > 0 ? new UsuarioDao(getServletContext()).buscarPorId(id) : null;
            if (usuario == null) {
                // id invalido ou inexistente: volta para a lista
                resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
                return;
            }
            req.setAttribute("usuario", usuario);
            req.getRequestDispatcher("/WEB-INF/views/admin/EditarUsuario.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao buscar usuario.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Recebe o formulario e grava a alteracao
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos; antes do primeiro getParameter

        int id = lerId(req.getParameter("id"));
        String nome = limpar(req.getParameter("nome"));
        String email = limpar(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String semestreTexto = req.getParameter("semestre");

        try {
            UsuarioDao dao = new UsuarioDao(getServletContext());
            UsuarioResumo atual = id > 0 ? dao.buscarPorId(id) : null;
            if (atual == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
                return;
            }

            // O e-mail do admin nao muda (o sistema cria o admin inicial por esse e-mail)
            if ("ADMIN".equals(atual.getTipo())) {
                email = atual.getEmail();
            }

            // Semestre so existe para mentorando; para mentor e admin fica vazio
            Integer semestre = null;
            if ("MENTORANDO".equals(atual.getTipo())) {
                semestre = lerSemestre(semestreTexto);
            }

            boolean dadosInvalidos = nome.isEmpty() || nome.length() > 150
                    || email.isEmpty() || email.length() > 150
                    || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    || ("MENTORANDO".equals(atual.getTipo()) && semestre == null);
            if (dadosInvalidos) {
                resp.sendRedirect(req.getContextPath() + "/admin/usuario-editar?id=" + id + "&erro=dados");
                return;
            }

            dao.atualizarUsuario(id, nome, email, semestre);
            resp.sendRedirect(req.getContextPath() + "/admin/usuarios");

        } catch (SQLException e) {
            // 23505 = violou o UNIQUE do e-mail (ja existe outro usuario com ele)
            if ("23505".equals(e.getSQLState())) {
                resp.sendRedirect(req.getContextPath() + "/admin/usuario-editar?id=" + id + "&erro=email");
            } else {
                getServletContext().log("Erro ao atualizar usuario.", e);
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

    private static Integer lerSemestre(String valor) {
        try {
            int semestre = Integer.parseInt(valor);
            return semestre >= 1 && semestre <= 8 ? semestre : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}