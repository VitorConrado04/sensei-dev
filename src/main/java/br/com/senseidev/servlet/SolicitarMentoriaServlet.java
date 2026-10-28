package br.com.senseidev.servlet;

import br.com.senseidev.auth.TokenMentoria;
import br.com.senseidev.dao.SolicitacaoMentoriaDao;

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

/** Recebe a solicitação do mentorando e a registra como PENDENTE. */
@WebServlet("/solicitar-mentoria")
public class SolicitarMentoriaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
        int mentorId;
        int disciplinaId;
        int duracao;
        LocalDate data;
        LocalTime hora;
        try {
            mentorId = Integer.parseInt(req.getParameter("mentor"));
            disciplinaId = Integer.parseInt(req.getParameter("disciplina"));
            duracao = Integer.parseInt(req.getParameter("duracao"));
            data = LocalDate.parse(req.getParameter("data"));
            hora = LocalTime.parse(req.getParameter("hora"));
        } catch (NumberFormatException | DateTimeParseException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (mentorId <= 0 || disciplinaId <= 0 || (duracao != 60 && duracao != 120)
                || hora.getMinute() != 0 || hora.getHour() < 7 || hora.getHour() > 18) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String objetivo = req.getParameter("objetivo");
        objetivo = objetivo == null ? "" : objetivo.trim();
        String perfil = req.getContextPath() + "/mentor-publico?id=" + mentorId;
        if (objetivo.isEmpty() || objetivo.length() > 500) {
            resp.sendRedirect(perfil + "&erro=objetivo#solicitar-mentoria");
            return;
        }
        LocalDateTime inicio = LocalDateTime.of(data, hora);
        if (!inicio.isAfter(LocalDateTime.now())) {
            resp.sendRedirect(perfil + "&erro=horario#solicitar-mentoria");
            return;
        }
        try {
            boolean criada = new SolicitacaoMentoriaDao(getServletContext()).solicitar(
                    ((Number) usuarioId).intValue(), mentorId, disciplinaId, inicio, duracao, objetivo);
            resp.sendRedirect(perfil + (criada ? "&solicitacao=enviada" : "&erro=horario")
                    + "#solicitar-mentoria");
        } catch (SQLException e) {
            throw new ServletException("Não foi possível solicitar a mentoria.", e);
        }
    }
}
