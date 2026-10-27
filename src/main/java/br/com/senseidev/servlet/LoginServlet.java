package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.UsuarioAutenticado;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

/** Autentica mentores e mentorandos usando as credenciais armazenadas no H2. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=campos");
            return;
        }

        try {
            UsuarioAutenticado usuario = new MentorDao(getServletContext())
                    .autenticarUsuario(email.trim().toLowerCase(Locale.ROOT), senha);

            if (usuario == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=credenciais");
                return;
            }

            // Evita reutilizar uma sessão que existia antes do login.
            HttpSession antiga = req.getSession(false);
            if (antiga != null) antiga.invalidate();

            HttpSession sessao = req.getSession(true);
            sessao.setMaxInactiveInterval(30 * 60);
            sessao.setAttribute("usuarioId", usuario.getId());
            sessao.setAttribute("usuarioNome", usuario.getNome());
            sessao.setAttribute("usuarioTipo", usuario.getTipo());

            if ("MENTOR".equals(usuario.getTipo())) {
                // Mantido também como mentorId para os Servlets da área do mentor.
                sessao.setAttribute("mentorId", usuario.getId());
                resp.sendRedirect(req.getContextPath() + "/mentor-inicio");
            } else if ("MENTORANDO".equals(usuario.getTipo())) {
                resp.sendRedirect(req.getContextPath() + "/inicio-aluno");
            } else {
                sessao.invalidate();
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=credenciais");
            }
        } catch (SQLException e) {
            getServletContext().log("Erro ao autenticar usuário.", e);
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=indisponivel");
        }
    }
}