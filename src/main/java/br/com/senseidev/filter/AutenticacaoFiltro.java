package br.com.senseidev.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Protege rotas privadas e impede que um mentorando navegue na área do mentor. */
@WebFilter(urlPatterns = {
        "/mentor-inicio", "/mentor-perfil", "/mentor-material", "/mentor-avaliacao", "/inicio-aluno"
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

        boolean rotaAluno = req.getServletPath().equals("/inicio-aluno");
        String tipoEsperado = rotaAluno ? "MENTORANDO" : "MENTOR";
        if (!tipoEsperado.equals(tipo)) {
            if ("MENTOR".equals(tipo)) resp.sendRedirect(req.getContextPath() + "/mentor-inicio");
            else if ("MENTORANDO".equals(tipo)) resp.sendRedirect(req.getContextPath() + "/inicio-aluno");
            else resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }

        if (!rotaAluno) {
            Object mentorId = sessao.getAttribute("mentorId");
            if (!(mentorId instanceof Number) || ((Number) mentorId).intValue() <= 0) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}