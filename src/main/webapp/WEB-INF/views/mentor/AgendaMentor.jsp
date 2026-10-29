<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Agenda do mentor: o que tira horários da agenda. O MentorAgendaServlet (/mentor-agenda) manda
     "mentor", "bloqueios" (de hoje em diante), "horarios" (disponibilidade semanal) e "dataMinima".
     Bloqueio = pontual (um dia ou parte dele). Remover horário = tira da disponibilidade semanal de vez. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Agenda | SENSEI.DEV</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
    <jsp:param name="aba" value="agenda"/>
</jsp:include>

<main class="conteudo">
    <h1 class="titulo-secao">Agenda</h1>
    <p class="subtitulo">Bloqueie datas em que não pode atender e gerencie seus horários semanais.</p>

    <c:if test="${param.salvo == 'bloqueio'}"><p class="subtitulo" role="status">Horário bloqueado.</p></c:if>
    <c:if test="${param.salvo == 'bloqueio-removido'}"><p class="subtitulo" role="status">Bloqueio removido.</p></c:if>
    <c:if test="${param.salvo == 'horario-removido'}"><p class="subtitulo" role="status">Horário removido da sua disponibilidade.</p></c:if>
    <c:if test="${param.erro == 'data'}"><p class="erro" role="alert">Escolha uma data válida, de hoje em diante.</p></c:if>
    <c:if test="${param.erro == 'horas'}"><p class="erro" role="alert">Preencha o horário de início e o de fim (o início deve ser antes do fim) ou deixe os dois em branco para bloquear o dia inteiro.</p></c:if>
    <c:if test="${param.erro == 'motivo'}"><p class="erro" role="alert">O motivo pode ter no máximo 255 caracteres.</p></c:if>
    <c:if test="${param.erro == 'conflito'}"><p class="erro" role="alert">Você já tem sessão pendente ou confirmada nesse período. Cancele a sessão antes de bloquear.</p></c:if>
    <c:if test="${param.erro == 'horario-agendado'}"><p class="erro" role="alert">Não foi possível remover: há sessão marcada nesse horário.</p></c:if>

    <div class="agenda-colunas">

        <%-- ================= BLOQUEIOS ================= --%>
        <section aria-labelledby="t-bloqueios">
            <h2 id="t-bloqueios" class="titulo-secao">Bloqueios de horário</h2>

            <form class="cartao form-bloqueio" action="${ctx}/mentor-agenda" method="post">
                <input type="hidden" name="acao" value="criar-bloqueio">
                <div class="grupo">
                    <label for="bloq-data">Data:</label>
                    <input type="date" id="bloq-data" name="data" min="${dataMinima}" required>
                </div>
                <div class="grupo-linha">
                    <div class="grupo">
                        <label for="bloq-inicio">Das:</label>
                        <input type="time" id="bloq-inicio" name="horaInicio">
                    </div>
                    <div class="grupo">
                        <label for="bloq-fim">Até:</label>
                        <input type="time" id="bloq-fim" name="horaFim">
                    </div>
                </div>
                <p class="dica">Deixe as horas em branco para bloquear o dia inteiro.</p>
                <div class="grupo">
                    <label for="bloq-motivo">Motivo (opcional):</label>
                    <input type="text" id="bloq-motivo" name="motivo" maxlength="255" autocomplete="off">
                </div>
                <div class="dialog-acoes">
                    <button type="submit" class="btn-pilula btn-roxo">Bloquear</button>
                </div>
            </form>

            <ul class="lista-agenda">
                <c:forEach var="b" items="${bloqueios}">
                    <li class="cartao item-agenda">
                        <div>
                            <strong><c:out value="${b.dataTexto}"/></strong>
                            <span><c:out value="${b.periodoTexto}"/></span>
                            <c:if test="${not empty b.motivo}"><small><c:out value="${b.motivo}"/></small></c:if>
                        </div>
                        <form action="${ctx}/mentor-agenda" method="post">
                            <input type="hidden" name="acao" value="remover-bloqueio">
                            <input type="hidden" name="id" value="${b.id}">
                            <button type="submit" class="pilula-acao cancelar"
                                    onclick="return confirm('Excluir este bloqueio?')">Excluir</button>
                        </form>
                    </li>
                </c:forEach>
                <c:if test="${empty bloqueios}">
                    <li class="vazio">Nenhum bloqueio futuro.</li>
                </c:if>
            </ul>
        </section>

        <%-- ================= HORÁRIOS SEMANAIS ================= --%>
        <section aria-labelledby="t-horarios">
            <h2 id="t-horarios" class="titulo-secao">Horários semanais</h2>
            <p class="subtitulo">Remover um horário o tira da sua disponibilidade de vez. Para adicionar horários, use a grade em
                <a class="link-suave" href="${ctx}/mentor-perfil">Perfil</a>.</p>

            <ul class="lista-agenda">
                <c:set var="diaAnterior" value="0"/>
                <c:forEach var="h" items="${horarios}">
                    <c:if test="${h.diaSemana != diaAnterior}">
                        <li class="agenda-dia-titulo"><c:out value="${h.diaTexto}"/></li>
                        <c:set var="diaAnterior" value="${h.diaSemana}"/>
                    </c:if>
                    <li class="cartao item-agenda">
                        <div><strong><c:out value="${h.horaInicio}"/> às <c:out value="${h.horaFim}"/></strong></div>
                        <c:choose>
                            <c:when test="${h.agendado}">
                                <span class="pilula-acao confirmada">Sessão marcada</span>
                            </c:when>
                            <c:otherwise>
                                <form action="${ctx}/mentor-agenda" method="post">
                                    <input type="hidden" name="acao" value="remover-horario">
                                    <input type="hidden" name="id" value="${h.id}">
                                    <button type="submit" class="pilula-acao cancelar"
                                            onclick="return confirm('Remover este horário da sua disponibilidade?')">Remover de vez</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:forEach>
                <c:if test="${empty horarios}">
                    <li class="vazio">Você ainda não cadastrou horários. Marque-os na grade de disponibilidade do Perfil.</li>
                </c:if>
            </ul>
        </section>
    </div>
</main>

</body>
</html>
