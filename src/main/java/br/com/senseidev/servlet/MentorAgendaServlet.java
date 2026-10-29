package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Gerencia o que tira o mentor da agenda:
 *  - bloqueios pontuais (um dia inteiro ou um intervalo de horas numa data): criar, listar e excluir;
 *  - horários semanais: remover um horário de vez da disponibilidade.
 */
@WebServlet("/mentor-agenda")
public class MentorAgendaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        try {
            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("bloqueios", dao.listarBloqueios(mentorId, LocalDate.now(), null));
            req.setAttribute("horarios", dao.listarHorariosSemanais(mentorId));
            req.setAttribute("dataMinima", LocalDate.now().toString());
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar a agenda do mentor.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/mentor/AgendaMentor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());
        String destino = req.getContextPath() + "/mentor-agenda";
        String acao = req.getParameter("acao");

        try {
            if ("remover-bloqueio".equals(acao)) {
                Integer id = lerId(req.getParameter("id"));
                if (id != null) dao.removerBloqueio(mentorId, id);
                resp.sendRedirect(destino + "?salvo=bloqueio-removido");
                return;
            }

            if ("remover-horario".equals(acao)) {
                Integer id = lerId(req.getParameter("id"));
                boolean removido = id != null && dao.removerHorarioSemanal(mentorId, id);
                resp.sendRedirect(destino + (removido ? "?salvo=horario-removido" : "?erro=horario-agendado"));
                return;
            }

            if ("criar-bloqueio".equals(acao)) {
                LocalDate data;
                try {
                    data = LocalDate.parse(req.getParameter("data"));
                } catch (DateTimeParseException | NullPointerException e) {
                    resp.sendRedirect(destino + "?erro=data");
                    return;
                }
                if (data.isBefore(LocalDate.now())) {
                    resp.sendRedirect(destino + "?erro=data");
                    return;
                }

                // horas em branco = dia inteiro; se preencher, precisa preencher as duas
                String de = vazioParaNull(req.getParameter("horaInicio"));
                String ate = vazioParaNull(req.getParameter("horaFim"));
                if ((de == null) != (ate == null)) {
                    resp.sendRedirect(destino + "?erro=horas");
                    return;
                }
                if (de != null) {
                    try {
                        LocalTime inicio = LocalTime.parse(de);
                        LocalTime fim = LocalTime.parse(ate);
                        if (!inicio.isBefore(fim)) {
                            resp.sendRedirect(destino + "?erro=horas");
                            return;
                        }
                        de = inicio.toString();
                        ate = fim.toString();
                    } catch (DateTimeParseException e) {
                        resp.sendRedirect(destino + "?erro=horas");
                        return;
                    }
                }

                String motivo = vazioParaNull(req.getParameter("motivo"));
                if (motivo != null && motivo.length() > 255) {
                    resp.sendRedirect(destino + "?erro=motivo");
                    return;
                }

                boolean criado = dao.inserirBloqueio(mentorId, data, de, ate, motivo);
                resp.sendRedirect(destino + (criado ? "?salvo=bloqueio" : "?erro=conflito"));
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Não foi possível atualizar a agenda do mentor.", e);
        }
        resp.sendRedirect(destino);
    }

    private static Integer lerId(String valor) {
        try {
            int id = Integer.parseInt(valor);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String vazioParaNull(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
