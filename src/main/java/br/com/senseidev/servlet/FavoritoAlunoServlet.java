package br.com.senseidev.servlet;

import br.com.senseidev.auth.TokenFavorito;
import br.com.senseidev.dao.FavoritoDao;
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

/** Marca e desmarca um mentor favorito sem abrir outra tela. */
@WebServlet("/favoritos-aluno")
public class FavoritoAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/inicio-aluno#favoritos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        if (!(id instanceof Number)) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }
        if (!TokenFavorito.valido(sessao, req.getParameter("token"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        int mentorId;
        try {
            mentorId = Integer.parseInt(req.getParameter("mentor"));
            if (mentorId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String acao = req.getParameter("acao");
        if (!"favoritar".equals(acao) && !"desfavoritar".equals(acao)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int mentorandoId = ((Number) id).intValue();
        try {
            UsuarioResumo aluno = new UsuarioDao(getServletContext()).buscarPorId(mentorandoId);
            if (aluno == null || !"MENTORANDO".equals(aluno.getTipo())
                    || !"ATIVO".equals(aluno.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            FavoritoDao dao = new FavoritoDao(getServletContext());
            if ("favoritar".equals(acao)) dao.adicionar(mentorandoId, mentorId);
            else dao.remover(mentorandoId, mentorId);
            if ("perfil".equals(req.getParameter("retorno")))
                resp.sendRedirect(req.getContextPath() + "/mentor-publico?id=" + mentorId);
            else if ("busca".equals(req.getParameter("retorno")))
                resp.sendRedirect(destinoBusca(req, mentorId));
            else resp.sendRedirect(req.getContextPath() + "/inicio-aluno#favoritos");
        } catch (SQLException e) {
            throw new ServletException("Não foi possível atualizar os favoritos.", e);
        }
    }

    private static String destinoBusca(HttpServletRequest req, int mentorId) {
        StringBuilder destino = new StringBuilder(req.getContextPath()).append("/inicio-aluno");
        Integer semestre = inteiro(req.getParameter("filtroSemestre"), 1, 8);
        if (semestre != null) adicionarParametro(destino, "semestre", semestre);
        String[] disciplinas = req.getParameterValues("filtroDisciplina");
        if (disciplinas != null) {
            for (int i = 0; i < Math.min(disciplinas.length, 20); i++) {
                Integer disciplina = inteiro(disciplinas[i], 1, Integer.MAX_VALUE);
                if (disciplina != null) adicionarParametro(destino, "disciplina", disciplina);
            }
        }
        Double nota = nota(req.getParameter("filtroNota"));
        if (nota != null) destino.append(destino.indexOf("?") < 0 ? '?' : '&')
                .append("nota=").append(nota);
        Integer selecionado = inteiro(req.getParameter("selecionado"), 1, Integer.MAX_VALUE);
        if (selecionado != null) adicionarParametro(destino, "mentor", selecionado);
        return destino.append("#mentor-").append(mentorId).toString();
    }

    private static void adicionarParametro(StringBuilder destino, String nome, int valor) {
        destino.append(destino.indexOf("?") < 0 ? '?' : '&').append(nome).append('=').append(valor);
    }

    private static Integer inteiro(String valor, int minimo, int maximo) {
        if (valor == null) return null;
        try {
            int numero = Integer.parseInt(valor);
            return numero >= minimo && numero <= maximo ? numero : null;
        } catch (NumberFormatException e) { return null; }
    }

    private static Double nota(String valor) {
        if (valor == null) return null;
        try {
            double numero = Double.parseDouble(valor);
            return Double.isFinite(numero) && numero >= 1 && numero <= 5 ? numero : null;
        } catch (NumberFormatException e) { return null; }
    }
}
