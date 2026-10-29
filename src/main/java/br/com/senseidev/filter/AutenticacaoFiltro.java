package br.com.senseidev.filter;

import br.com.senseidev.dao.NotificacaoDao;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Protege rotas privadas e impede que um mentorando navegue na área do mentor. */
@WebFilter(urlPatterns = {
        "/mentor-inicio", "/mentor-agenda", "/mentor-perfil", "/mentor-material", "/mentor-avaliacao", "/inicio-aluno",
        "/admin/*", "/conta", "/perfil-aluno", "/interesses-aluno", "/mentor-publico", "/favoritos-aluno",
        "/solicitar-mentoria", "/mentorias-aluno", "/avaliar-sessao", "/notificacoes"
})
public class AutenticacaoFiltro implements Filter {
    @Override public void init(FilterConfig filterConfig) { }
    @Override public void destroy() { }

    @Override
    public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // Evita manter páginas privadas no cache do navegador depois do logout.
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        Object tipo = sessao == null ? null : sessao.getAttribute("usuarioTipo");

        if (!(id instanceof Number) || ((Number) id).intValue() <= 0 || tipo == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }

        // Quantidade de notificações não lidas, para o selo no menu
        if ("GET".equals(req.getMethod()) && ("MENTOR".equals(tipo) || "MENTORANDO".equals(tipo))) {
            try {
                req.setAttribute("notificacoesNaoLidas", new NotificacaoDao(req.getServletContext())
                        .contarNaoLidas(((Number) id).intValue()));
            } catch (SQLException e) {
                req.getServletContext().log("Não foi possível contar as notificações.", e);
            }
        }

        String rota = req.getServletPath();
        // /conta e /notificacoes sao abertas para mentor e mentorando
        if (rota.equals("/conta") || rota.equals("/notificacoes")) {
            if ("MENTOR".equals(tipo) || "MENTORANDO".equals(tipo)) {
                chain.doFilter(request, response);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/inicio");
            }
            return;
        }

        String tipoEsperado;
        if (rota.startsWith("/admin")) tipoEsperado = "ADMIN";
        else if (rota.equals("/inicio-aluno") || rota.equals("/perfil-aluno")
                || rota.equals("/interesses-aluno") || rota.equals("/mentor-publico")
                || rota.equals("/favoritos-aluno") || rota.equals("/solicitar-mentoria")
                || rota.equals("/mentorias-aluno") || rota.equals("/avaliar-sessao")) tipoEsperado = "MENTORANDO";
        else tipoEsperado = "MENTOR";

        // Conta do tipo errado e mandada para a propria area
        if (!tipoEsperado.equals(tipo)) {
            if ("MENTOR".equals(tipo)) resp.sendRedirect(req.getContextPath() + "/mentor-inicio");
            else if ("MENTORANDO".equals(tipo)) resp.sendRedirect(req.getContextPath() + "/inicio-aluno");
            else if ("ADMIN".equals(tipo)) resp.sendRedirect(req.getContextPath() + "/admin/inicio");
            else resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }

        // Rotas de mentor exigem tambem o mentorId na sessao
        if ("MENTOR".equals(tipoEsperado)) {
            Object mentorId = sessao.getAttribute("mentorId");
            if (!(mentorId instanceof Number) || ((Number) mentorId).intValue() <= 0) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}