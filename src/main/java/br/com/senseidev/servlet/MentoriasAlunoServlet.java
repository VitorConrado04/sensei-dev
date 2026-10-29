package br.com.senseidev.servlet;

import br.com.senseidev.auth.TokenMentoria;
import br.com.senseidev.dao.AlteracaoMentoriaDao;
import br.com.senseidev.dao.MaterialAlunoDao;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

/** Lista as mentorias do aluno e recebe cancelamentos e reagendamentos. */
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
            req.setAttribute("tokenMentoria", TokenMentoria.obter(sessao));
            req.setAttribute("dataMinima", LocalDate.now().toString());
            prepararReagendamento(req, mentorandoId);
            // materiais que os mentores vincularam às mentorias deste aluno (mapa: id da mentoria -> lista)
            req.setAttribute("materiaisPorMentoria", new MaterialAlunoDao(getServletContext()).listarPorMentorando(mentorandoId));
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession sessao = req.getSession(false);
        Object usuarioId = sessao == null ? null : sessao.getAttribute("usuarioId");
        if (!(usuarioId instanceof Number)) {
            resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
            return;
        }
        if (!TokenMentoria.valido(sessao, req.getParameter("token"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        int mentorandoId = ((Number) usuarioId).intValue();
        try {
            UsuarioResumo usuario = new UsuarioDao(getServletContext()).buscarPorId(mentorandoId);
            if (usuario == null || !"MENTORANDO".equals(usuario.getTipo())
                    || !"ATIVO".equals(usuario.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/LoginArea.jsp?erro=sessao");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Não foi possível verificar a conta do aluno.", e);
        }
        int mentoriaId;
        try {
            mentoriaId = Integer.parseInt(req.getParameter("id"));
            if (mentoriaId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String acao = req.getParameter("acao");
        AlteracaoMentoriaDao dao = new AlteracaoMentoriaDao(getServletContext());
        boolean alterada;
        try {
            if ("cancelar".equals(acao)) {
                alterada = dao.cancelar(mentorandoId, mentoriaId);
            } else if ("reagendar".equals(acao)) {
                LocalDate data = LocalDate.parse(req.getParameter("data"));
                LocalTime hora = LocalTime.parse(req.getParameter("hora"));
                int duracao = Integer.parseInt(req.getParameter("duracao"));
                if (hora.getMinute() != 0 || hora.getSecond() != 0
                        || hora.getHour() < 7 || hora.getHour() > 18
                        || (duracao != 60 && duracao != 120)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                alterada = dao.reagendar(mentorandoId, mentoriaId,
                        LocalDateTime.of(data, hora), duracao);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (DateTimeParseException | NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (SQLException e) {
            throw new ServletException("Não foi possível alterar a mentoria.", e);
        }
        String resultado = alterada ? ("cancelar".equals(acao) ? "cancelada" : "reagendada") : "indisponivel";
        resp.sendRedirect(req.getContextPath() + "/mentorias-aluno?resultado=" + resultado);
    }

    private void prepararReagendamento(HttpServletRequest req, int mentorandoId) throws SQLException {
        String idTexto = req.getParameter("reagendar");
        if (idTexto == null) return;
        try {
            int id = Integer.parseInt(idTexto);
            AlteracaoMentoriaDao dao = new AlteracaoMentoriaDao(getServletContext());
            MentoriaAluno selecionada = dao.buscarAlteravel(mentorandoId, id);
            if (selecionada == null) return;
            req.setAttribute("mentoriaSelecionada", selecionada);
            int duracao = "120".equals(req.getParameter("duracao")) ? 120 : 60;
            req.setAttribute("duracaoSelecionada", duracao);
            String dataTexto = req.getParameter("data");
            if (dataTexto == null || dataTexto.isEmpty()) return;
            LocalDate data = LocalDate.parse(dataTexto);
            if (data.isBefore(LocalDate.now())) {
                req.setAttribute("dataInvalida", true);
                return;
            }
            req.setAttribute("dataSelecionada", data.toString());
            req.setAttribute("horariosReagendamento", dao.horariosParaReagendar(
                    mentorandoId, id, data, duracao));
        } catch (NumberFormatException | DateTimeParseException e) {
            req.setAttribute("dataInvalida", true);
        }
    }
}
