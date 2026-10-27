package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.DiaCalendario;
import br.com.senseidev.model.Sessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Início do mentor: histórico, calendário e próximas sessões. */
@WebServlet("/mentor-inicio")
public class MentorInicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int mentorId = SessaoUtil.obterMentorId(req);
        MentorDao dao = new MentorDao(getServletContext());

        try {
            List<Sessao> todas = dao.listarSessoes(mentorId);
            List<Sessao> historico = new ArrayList<>();
            List<Sessao> proximas = new ArrayList<>();
            List<Sessao> agenda = new ArrayList<>();

            for (Sessao sessao : todas) {
                String status = sessao.getStatus();
                if ("REALIZADA".equals(status)) historico.add(sessao);
                if ("PENDENTE".equals(status) || "CONFIRMADA".equals(status)) proximas.add(sessao);
                if (!"CANCELADA".equals(status)) agenda.add(sessao);
            }
            Collections.reverse(historico);
            YearMonth mes = escolherMes(req.getParameter("mes"), proximas);

            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("historico", historico);
            req.setAttribute("proximas", proximas);
            req.setAttribute("semanas", montarSemanas(mes, agenda));
            req.setAttribute("mesTitulo", MESES[mes.getMonthValue() - 1] + " " + mes.getYear());
            req.setAttribute("mesAnterior", mes.minusMonths(1).toString());
            req.setAttribute("mesProximo", mes.plusMonths(1).toString());
        } catch (SQLException e) {
            throw new ServletException("Não foi possível carregar a agenda do mentor.", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/mentor/InicioMentor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int mentorId = SessaoUtil.obterMentorId(req);
        String id = req.getParameter("id");
        String acao = req.getParameter("acao");

        if (id != null && acao != null) {
            try {
                int sessaoId = Integer.parseInt(id);
                MentorDao dao = new MentorDao(getServletContext());
                if ("confirmar".equals(acao)) dao.atualizarStatusSessao(mentorId, sessaoId, "CONFIRMADA");
                else if ("cancelar".equals(acao)) dao.atualizarStatusSessao(mentorId, sessaoId, "CANCELADA");
            } catch (NumberFormatException ignored) {
                // ID inválido: não modifica o banco e apenas recarrega a página.
            } catch (SQLException e) {
                throw new ServletException("Não foi possível atualizar o status da sessão.", e);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/mentor-inicio");
    }

    private YearMonth escolherMes(String parametro, List<Sessao> proximas) {
        if (parametro != null) {
            try { return YearMonth.parse(parametro); }
            catch (DateTimeParseException ignored) { /* usa o mês padrão abaixo */ }
        }
        if (!proximas.isEmpty()) return YearMonth.from(proximas.get(0).getInicio());
        return YearMonth.now();
    }

    private List<List<DiaCalendario>> montarSemanas(YearMonth mes, List<Sessao> agenda) {
        LocalDate primeiroDoMes = mes.atDay(1);
        int deslocamento = primeiroDoMes.getDayOfWeek().getValue() % 7;
        LocalDate dia = primeiroDoMes.minusDays(deslocamento);
        int totalSemanas = (int) Math.ceil((deslocamento + mes.lengthOfMonth()) / 7.0);
        LocalDate hoje = LocalDate.now();
        List<List<DiaCalendario>> semanas = new ArrayList<>();

        for (int i = 0; i < totalSemanas; i++) {
            List<DiaCalendario> semana = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                boolean foraDoMes = !YearMonth.from(dia).equals(mes);
                DiaCalendario celula = new DiaCalendario(dia, foraDoMes, dia.equals(hoje));
                for (Sessao sessao : agenda) {
                    if (sessao.getInicio().toLocalDate().equals(dia)) celula.getSessoes().add(sessao);
                }
                semana.add(celula);
                dia = dia.plusDays(1);
            }
            semanas.add(semana);
        }
        return semanas;
    }
}