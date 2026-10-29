package br.com.senseidev.servlet;

import br.com.senseidev.auth.SessaoUtil;
import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.Bloqueio;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Início do mentor: histórico, próximas sessões e a agenda em três visões (dia, semana e mês).
 * Parâmetros da agenda: ?visao=dia|semana|mes e ?data=aaaa-mm-dd (dia de referência).
 */
@WebServlet("/mentor-inicio")
public class MentorInicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private static final String[] DIAS_EXTENSO = {"Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira",
            "Quinta-feira", "Sexta-feira", "Sábado"};
    private static final DateTimeFormatter CURTA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter COMPLETA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

            String visao = normalizarVisao(req.getParameter("visao"));
            LocalDate hoje = LocalDate.now();
            LocalDate ref = lerData(req.getParameter("data"), req.getParameter("mes"));
            if (ref == null) {
                ref = "mes".equals(visao) && !proximas.isEmpty()
                        ? proximas.get(0).getInicio().toLocalDate() : hoje;
            }

            req.setAttribute("mentor", dao.buscarMentor(mentorId));
            req.setAttribute("historico", historico);
            req.setAttribute("proximas", proximas);
            req.setAttribute("visao", visao);

            if ("mes".equals(visao)) {
                YearMonth mes = YearMonth.from(ref);
                LocalDate primeiro = mes.atDay(1).minusDays(mes.atDay(1).getDayOfWeek().getValue() % 7);
                List<List<DiaCalendario>> semanas = montarSemanas(mes, agenda,
                        dao.listarBloqueios(mentorId, primeiro, primeiro.plusDays(42)));
                req.setAttribute("semanas", semanas);
                req.setAttribute("tituloPeriodo", MESES[mes.getMonthValue() - 1] + " " + mes.getYear());
                req.setAttribute("dataAnterior", mes.minusMonths(1).atDay(1).toString());
                req.setAttribute("dataProxima", mes.plusMonths(1).atDay(1).toString());
                // ao trocar para semana/dia a partir do mês: hoje, se estiver neste mês; senão o dia 1
                req.setAttribute("dataRef", YearMonth.from(hoje).equals(mes) ? hoje.toString() : mes.atDay(1).toString());
            } else if ("semana".equals(visao)) {
                LocalDate domingo = ref.minusDays(ref.getDayOfWeek().getValue() % 7);
                LocalDate sabado = domingo.plusDays(6);
                List<Bloqueio> bloqueios = dao.listarBloqueios(mentorId, domingo, sabado);
                List<DiaCalendario> dias = new ArrayList<>();
                for (int i = 0; i < 7; i++) dias.add(celula(domingo.plusDays(i), false, hoje, agenda, bloqueios));
                req.setAttribute("dias", dias);
                req.setAttribute("tituloPeriodo", domingo.format(CURTA) + " a " + sabado.format(COMPLETA));
                req.setAttribute("dataAnterior", ref.minusWeeks(1).toString());
                req.setAttribute("dataProxima", ref.plusWeeks(1).toString());
                req.setAttribute("dataRef", ref.toString());
            } else {
                List<Bloqueio> bloqueios = dao.listarBloqueios(mentorId, ref, ref);
                List<DiaCalendario> dias = new ArrayList<>();
                dias.add(celula(ref, false, hoje, agenda, bloqueios));
                req.setAttribute("dias", dias);
                req.setAttribute("tituloPeriodo", DIAS_EXTENSO[ref.getDayOfWeek().getValue() % 7] + ", " + ref.format(COMPLETA));
                req.setAttribute("dataAnterior", ref.minusDays(1).toString());
                req.setAttribute("dataProxima", ref.plusDays(1).toString());
                req.setAttribute("dataRef", ref.toString());
            }
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
                else if ("realizar".equals(acao)) dao.marcarComoRealizada(mentorId, sessaoId);
            } catch (NumberFormatException ignored) {
                // ID inválido: não modifica o banco e apenas recarrega a página.
            } catch (SQLException e) {
                throw new ServletException("Não foi possível atualizar o status da sessão.", e);
            }
        }
        // volta para a mesma visão da agenda em que o mentor estava
        String destino = req.getContextPath() + "/mentor-inicio";
        String visao = req.getParameter("visao");
        LocalDate data = lerData(req.getParameter("data"), null);
        if (visao != null && ("dia".equals(visao) || "semana".equals(visao) || "mes".equals(visao)) && data != null) {
            destino += "?visao=" + visao + "&data=" + data;
        }
        resp.sendRedirect(destino);
    }

    private static String normalizarVisao(String valor) {
        if ("dia".equals(valor) || "semana".equals(valor)) return valor;
        return "mes";
    }

    /** Lê ?data=aaaa-mm-dd; aceita também o antigo ?mes=aaaa-mm. Devolve null se ausente ou inválido. */
    private static LocalDate lerData(String data, String mes) {
        if (data != null && !data.isEmpty()) {
            try { return LocalDate.parse(data); }
            catch (DateTimeParseException ignored) { /* tenta o parâmetro antigo abaixo */ }
        }
        if (mes != null && !mes.isEmpty()) {
            try { return YearMonth.parse(mes).atDay(1); }
            catch (DateTimeParseException ignored) { /* usa o padrão */ }
        }
        return null;
    }

    private DiaCalendario celula(LocalDate dia, boolean foraDoMes, LocalDate hoje,
                                 List<Sessao> agenda, List<Bloqueio> bloqueios) {
        DiaCalendario celula = new DiaCalendario(dia, foraDoMes, dia.equals(hoje));
        for (Sessao sessao : agenda) {
            if (sessao.getInicio().toLocalDate().equals(dia)) celula.getSessoes().add(sessao);
        }
        for (Bloqueio bloqueio : bloqueios) {
            if (bloqueio.getData().equals(dia)) celula.getBloqueios().add(bloqueio);
        }
        return celula;
    }

    private List<List<DiaCalendario>> montarSemanas(YearMonth mes, List<Sessao> agenda,
                                                    List<Bloqueio> bloqueios) {
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
                semana.add(celula(dia, foraDoMes, hoje, agenda, bloqueios));
                dia = dia.plusDays(1);
            }
            semanas.add(semana);
        }
        return semanas;
    }
}
