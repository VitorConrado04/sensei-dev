package br.com.senseidev.servlet;

import br.com.senseidev.dao.MentorDao;
import br.com.senseidev.model.DiaCalendario;
import br.com.senseidev.model.Sessao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// tela inicial do mentor: histórico, calendário do mês e próximas sessões
@WebServlet("/mentor-inicio")
public class MentorInicioServlet extends HttpServlet {

    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MentorDao dao = new MentorDao();
        int mentorId = MentorDao.MENTOR_TESTE;

        List<Sessao> todas = dao.listarSessoes(mentorId);

        List<Sessao> historico = new ArrayList<>();
        List<Sessao> proximas = new ArrayList<>();
        List<Sessao> agenda = new ArrayList<>();   // tudo que não foi cancelado, vai pro calendário

        for (Sessao s : todas) {
            String status = s.getStatus();
            if (status.equals("REALIZADA")) {
                historico.add(s);
            }
            if (status.equals("PENDENTE") || status.equals("CONFIRMADA")) {
                proximas.add(s);
            }
            if (!status.equals("CANCELADA")) {
                agenda.add(s);
            }
        }

        // a lista vem da mais antiga pra mais nova, no histórico queremos o contrário
        Collections.reverse(historico);

        YearMonth mes = escolherMes(req.getParameter("mes"), proximas);

        req.setAttribute("mentor", dao.buscarMentor(mentorId));
        req.setAttribute("historico", historico);
        req.setAttribute("proximas", proximas);
        req.setAttribute("semanas", montarSemanas(mes, agenda));
        req.setAttribute("mesTitulo", MESES[mes.getMonthValue() - 1] + " " + mes.getYear());
        req.setAttribute("mesAnterior", mes.minusMonths(1).toString());   // ex: 2026-09
        req.setAttribute("mesProximo", mes.plusMonths(1).toString());

        req.getRequestDispatcher("/InicioMentor.jsp").forward(req, resp);
    }

    // os botões Confirmar e Cancelar mandam o id da sessão e a ação
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String id = req.getParameter("id");
        String acao = req.getParameter("acao");

        if (id != null && acao != null) {
            try {
                int sessaoId = Integer.parseInt(id);
                MentorDao dao = new MentorDao();

                if (acao.equals("confirmar")) {
                    dao.atualizarStatusSessao(MentorDao.MENTOR_TESTE, sessaoId, "CONFIRMADA");
                } else if (acao.equals("cancelar")) {
                    dao.atualizarStatusSessao(MentorDao.MENTOR_TESTE, sessaoId, "CANCELADA");
                }
            } catch (NumberFormatException e) {
                // id que não é número, ignora e só recarrega a tela
            }
        }

        // redirect pra o F5 não repetir o POST
        resp.sendRedirect(req.getContextPath() + "/mentor-inicio");
    }

    // usa o mês da URL (?mes=2026-09); se não vier, abre no mês da próxima sessão
    private YearMonth escolherMes(String parametro, List<Sessao> proximas) {
        if (parametro != null) {
            try {
                return YearMonth.parse(parametro);
            } catch (DateTimeParseException e) {
                // parâmetro inválido, cai nas opções abaixo
            }
        }
        if (!proximas.isEmpty()) {
            return YearMonth.from(proximas.get(0).getInicio());
        }
        return YearMonth.now();
    }

    // monta a tabela do calendário: uma lista de semanas, cada semana com 7 dias (domingo a sábado)
    private List<List<DiaCalendario>> montarSemanas(YearMonth mes, List<Sessao> agenda) {

        LocalDate primeiroDoMes = mes.atDay(1);

        // quantos dias voltar até o domingo da primeira semana (dom = 0, seg = 1 ...)
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

                // coloca na célula as sessões que acontecem nesse dia
                for (Sessao s : agenda) {
                    if (s.getInicio().toLocalDate().equals(dia)) {
                        celula.getSessoes().add(s);
                    }
                }

                semana.add(celula);
                dia = dia.plusDays(1);
            }
            semanas.add(semana);
        }
        return semanas;
    }
}
