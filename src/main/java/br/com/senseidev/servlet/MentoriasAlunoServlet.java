package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentoriasAlunoDao;
import br.com.senseidev.dao.UsuarioDao;
import br.com.senseidev.model.GrupoMentorias;
import br.com.senseidev.model.MentoriaAluno;
import br.com.senseidev.model.UsuarioResumo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

/** Organiza as mentorias do aluno por situação, sem permitir alterações nesta etapa. */
@WebServlet("/mentorias-aluno")
public class MentoriasAlunoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sessao = req.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioId");
        if (!(id instanceof Number)) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }
        try {
            int mentorandoId = ((Number) id).intValue();
            UsuarioResumo usuario = new UsuarioDao(getServletContext()).buscarPorId(mentorandoId);
            if (usuario == null || !"MENTORANDO".equals(usuario.getTipo())
                    || !"ATIVO".equals(usuario.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
            List<MentoriaAluno> futuras = new ArrayList<>();
            List<MentoriaAluno> aguardando = new ArrayList<>();
            List<MentoriaAluno> realizadas = new ArrayList<>();
            List<MentoriaAluno> canceladas = new ArrayList<>();
            LocalDateTime agora = LocalDateTime.now();
            for (MentoriaAluno mentoria : new MentoriasAlunoDao(getServletContext()).listar(mentorandoId)) {
                String status = mentoria.getStatus();
                if ("REALIZADA".equals(status)) realizadas.add(mentoria);
                else if ("CANCELADA".equals(status)) canceladas.add(mentoria);
                else if ("PENDENTE".equals(status) || "CONFIRMADA".equals(status)) {
                    if (mentoria.getFim().isAfter(agora)) futuras.add(mentoria);
                    else aguardando.add(mentoria);
                }
            }
            Collections.reverse(aguardando);
            Collections.reverse(realizadas);
            Collections.reverse(canceladas);
            req.setAttribute("usuario", usuario);
            req.setAttribute("grupos", Arrays.asList(
                    new GrupoMentorias("futuras", "Próximas mentorias",
                            "Você ainda não tem mentorias futuras.", futuras),
                    new GrupoMentorias("aguardando", "Aguardando atualização",
                            "Nenhuma mentoria antiga aguarda atualização.", aguardando),
                    new GrupoMentorias("realizadas", "Realizadas",
                            "Você ainda não tem mentorias realizadas.", realizadas),
                    new GrupoMentorias("canceladas", "Canceladas",
                            "Você não tem mentorias canceladas.", canceladas)));
            req.getRequestDispatcher("/WEB-INF/views/aluno/MentoriasAluno.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar as mentorias do aluno.", e);
        }
    }
}
