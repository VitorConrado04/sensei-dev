package br.com.senseidev.servlet;

import br.com.senseidev.dao.InteresseDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/** Salva somente as disciplinas de interesse do mentorando autenticado. */
@WebServlet("/interesses-aluno")
public class InteresseAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        int mentorandoId = id instanceof Number ? ((Number) id).intValue() : 0;
        String destino = req.getContextPath() + "/perfil-aluno";
        if (mentorandoId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }

        String[] valores = req.getParameterValues("disciplina");
        Set<Integer> selecionadas = new HashSet<>();
        if (valores != null) {
            if (valores.length > 100) {
                resp.sendRedirect(destino + "?erro=disciplinas");
                return;
            }
            try {
                for (String valor : valores) {
                    int disciplinaId = Integer.parseInt(valor);
                    if (disciplinaId <= 0) throw new NumberFormatException();
                    selecionadas.add(disciplinaId);
                }
            } catch (NumberFormatException e) {
                resp.sendRedirect(destino + "?erro=disciplinas");
                return;
            }
        }
        try {
            new InteresseDao(getServletContext()).salvar(mentorandoId, selecionadas);
            resp.sendRedirect(destino + "?interesses=salvos");
        } catch (SQLException e) {
            getServletContext().log("Erro ao salvar disciplinas de interesse.", e);
            resp.sendRedirect(destino + "?erro=disciplinas");
        }
    }
}
