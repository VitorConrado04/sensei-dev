package br.com.senseidev.servlet;

import br.com.senseidev.auth.SenhaUtil;
import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

// "Minha conta": mentor e mentorando editam os proprios dados e a senha
@WebServlet("/conta")
public class ContaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Abre o formulario com os dados de quem esta logado
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = idDaSessao(req);
        try {
            UsuarioResumo usuario = id > 0 ? new UsuarioDao(getServletContext()).buscarPorId(id) : null;
            if (usuario == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            req.setAttribute("usuario", usuario);
            req.getRequestDispatcher("/WEB-INF/views/conta/Conta.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao buscar a conta.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Salva os dados e, se pedido, troca a senha
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos; antes do primeiro getParameter

        String conta = req.getContextPath() + "/conta";
        int id = idDaSessao(req); // o id vem da SESSAO, nunca do formulario

        String nome = limpar(req.getParameter("nome"));
        String email = limpar(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String senhaAtual = req.getParameter("senhaAtual");
        String senhaNova = req.getParameter("senhaNova");
        String confirmarSenha = req.getParameter("confirmarSenha");

        try {
            UsuarioDao dao = new UsuarioDao(getServletContext());
            UsuarioResumo atual = id > 0 ? dao.buscarPorId(id) : null;
            if (atual == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }

            // Semestre so existe para mentorando
            Integer semestre = null;
            if ("MENTORANDO".equals(atual.getTipo())) {
                semestre = lerSemestre(req.getParameter("semestre"));
            }

            boolean dadosInvalidos = nome.isEmpty() || nome.length() > 150
                    || email.isEmpty() || email.length() > 150
                    || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                    || ("MENTORANDO".equals(atual.getTipo()) && semestre == null);
            if (dadosInvalidos) {
                resp.sendRedirect(conta + "?erro=dados");
                return;
            }

            // Troca de senha e opcional: so acontece se a senha nova foi preenchida
            boolean trocarSenha = senhaNova != null && !senhaNova.isEmpty();
            if (trocarSenha) {
                if (senhaNova.length() < 8 || !senhaNova.equals(confirmarSenha)) {
                    resp.sendRedirect(conta + "?erro=senhaNova");
                    return;
                }
                // Confere a senha atual antes de aceitar a nova
                if (!SenhaUtil.verificar(senhaAtual, dao.buscarHashSenha(id))) {
                    resp.sendRedirect(conta + "?erro=senhaAtual");
                    return;
                }
            }

            // Primeiro os dados (pode falhar por e-mail repetido), depois a senha
            dao.atualizarUsuario(id, nome, email, semestre);
            if (trocarSenha) {
                dao.atualizarSenha(id, SenhaUtil.gerarHash(senhaNova));
            }

            // Atualiza o nome mostrado nas telas ("Ola, ...")
            req.getSession().setAttribute("usuarioNome", nome);
            resp.sendRedirect(conta + "?ok=1");

        } catch (SQLException e) {
            // 23505 = violou o UNIQUE do e-mail (ja existe outro usuario com ele)
            if ("23505".equals(e.getSQLState())) {
                resp.sendRedirect(conta + "?erro=email");
            } else {
                getServletContext().log("Erro ao atualizar a conta.", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    // Le o id de quem esta logado, guardado na sessao pelo LoginServlet
    private static int idDaSessao(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        return id instanceof Number ? ((Number) id).intValue() : 0;
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