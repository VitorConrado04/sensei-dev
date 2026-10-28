package br.com.senseidev.servlet;

import br.com.senseidev.dao.DisciplinaDao;
import br.com.senseidev.dao.MentorBuscaDao;
import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.Disciplina;
import br.com.senseidev.model.MentorBusca;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Início do mentorando com a busca de mentores do protótipo. */
@WebServlet("/inicio-aluno")
public class InicioAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession sessao = req.getSession(false);
            Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
            if (!(id instanceof Number)) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            UsuarioResumo usuario = new UsuarioDao(getServletContext()).buscarPorId(((Number) id).intValue());
            if (usuario == null || !"MENTORANDO".equals(usuario.getTipo())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            List<Disciplina> disciplinas = new DisciplinaDao(getServletContext()).listarDisciplinas();
            Integer semestre = inteiroOpcional(req.getParameter("semestre"), 1, 8);
            Double notaMinima = notaOpcional(req.getParameter("nota"));
            List<Integer> selecionadas = new ArrayList<>();
            Set<Integer> validas = new HashSet<>();
            for (Disciplina disciplina : disciplinas) validas.add(disciplina.getId());
            String[] valores = req.getParameterValues("disciplina");
            if (valores != null) {
                for (String valor : valores) {
                    Integer disciplinaId = inteiroOpcional(valor, 1, Integer.MAX_VALUE);
                    if (disciplinaId != null && validas.contains(disciplinaId)
                            && !selecionadas.contains(disciplinaId)) selecionadas.add(disciplinaId);
                }
            }
            req.setAttribute("usuario", usuario);
            req.setAttribute("disciplinas", disciplinas);
            req.setAttribute("semestre", semestre);
            req.setAttribute("notaMinima", notaMinima);
            req.setAttribute("selecionadas", selecionadas);
            Map<Integer, Boolean> marcadas = new HashMap<>();
            for (Integer disciplinaId : selecionadas) marcadas.put(disciplinaId, true);
            req.setAttribute("marcadas", marcadas);
            MentorBuscaDao buscaDao = new MentorBuscaDao(getServletContext());
            List<MentorBusca> mentores = buscaDao.buscar(semestre, selecionadas, notaMinima);
            req.setAttribute("mentores", mentores);
            Integer mentorId = inteiroOpcional(req.getParameter("mentor"), 1, Integer.MAX_VALUE);
            if (mentorId != null) {
                for (MentorBusca mentor : mentores) {
                    if (mentor.getId() == mentorId) {
                        req.setAttribute("mentorSelecionado", mentor);
                        break;
                    }
                }
            }
            req.getRequestDispatcher("/WEB-INF/views/aluno/InicioAluno.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível buscar os mentores.", e);
        }
    }

    private static Integer inteiroOpcional(String valor, int minimo, int maximo) {
        if (valor == null || valor.isEmpty()) return null;
        try {
            int numero = Integer.parseInt(valor);
            return numero >= minimo && numero <= maximo ? numero : null;
        } catch (NumberFormatException e) { return null; }
    }

    private static Double notaOpcional(String valor) {
        if (valor == null || valor.isEmpty()) return null;
        try {
            double numero = Double.parseDouble(valor);
            return Double.isFinite(numero) && numero >= 1 && numero <= 5 ? numero : null;
        } catch (NumberFormatException e) { return null; }
    }
}
