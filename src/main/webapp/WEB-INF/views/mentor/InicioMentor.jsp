<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Tela inicial do mentor. Quem chama é o MentorInicioServlet (/mentor-inicio),
     que manda: mentor, historico, proximas, visao (dia/semana/mes), tituloPeriodo, dataAnterior,
     dataProxima, dataRef e, conforme a visão, semanas (mês) ou dias (semana/dia).
     O c:out troca < e > por texto, assim um nome com HTML não quebra a página. --%>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Início | SENSEI.DEV</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
    <jsp:param name="aba" value="inicio"/>
</jsp:include>

<main class="conteudo inicio">
    <h1 class="so-leitor">Início do mentor</h1>

    <%-- coluna da esquerda: sessões que já aconteceram --%>
    <section aria-labelledby="t-historico">
        <h2 id="t-historico" class="titulo-secao">Histórico de sessões</h2>
        <ul class="lista-sessoes">
            <c:forEach var="s" items="${historico}">
                <li class="bloco">
                    <article class="sessao">
                        <div class="sessao-topo"><span>${s.diaTexto}</span><span>${s.horaTexto}</span></div>
                        <div class="sessao-corpo">
                            <span class="avatar" style="--tam:52px" aria-hidden="true">${s.iniciais}</span>
                            <span class="sessao-nome"><c:out value="${s.alunoNome}"/></span>
                        </div>
                        <div class="sessao-duracao">${s.horas}h</div>
                    </article>
                    <p class="pilula-info">
                        <span><c:out value="${s.disciplina}"/></span>
                        <span>Nota:
                            <c:choose>
                                <c:when test="${s.nota != null}">
                                    <fmt:formatNumber value="${s.nota}" minFractionDigits="1" maxFractionDigits="1"/>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </p>
                </li>
            </c:forEach>
            <c:if test="${empty historico}">
                <li class="vazio">Nenhuma sessão realizada ainda.</li>
            </c:if>
        </ul>
    </section>

    <%-- meio: agenda em três visões (dia, semana, mês). As setas e as abas recarregam a página
         com ?visao=dia|semana|mes&data=aaaa-mm-dd --%>
    <section class="calendario" aria-label="Agenda de sessões">
        <nav class="cal-visoes" aria-label="Visão da agenda">
            <a href="${ctx}/mentor-inicio?visao=dia&amp;data=${dataRef}" <c:if test="${visao == 'dia'}">aria-current="page"</c:if>>Dia</a>
            <a href="${ctx}/mentor-inicio?visao=semana&amp;data=${dataRef}" <c:if test="${visao == 'semana'}">aria-current="page"</c:if>>Semana</a>
            <a href="${ctx}/mentor-inicio?visao=mes&amp;data=${dataRef}" <c:if test="${visao == 'mes'}">aria-current="page"</c:if>>Mês</a>
            <a class="cal-hoje" href="${ctx}/mentor-inicio?visao=${visao}">Hoje</a>
        </nav>

        <div class="cal-topo">
            <a class="cal-nav" href="${ctx}/mentor-inicio?visao=${visao}&amp;data=${dataAnterior}" aria-label="Período anterior">
                <svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="15 5 8 12 15 19"/></svg>
            </a>
            <h2 class="cal-mes"><c:out value="${tituloPeriodo}"/></h2>
            <a class="cal-nav" href="${ctx}/mentor-inicio?visao=${visao}&amp;data=${dataProxima}" aria-label="Próximo período">
                <svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="9 5 16 12 9 19"/></svg>
            </a>
        </div>

        <c:choose>
            <%-- ===== visão do dia: lista das sessões e bloqueios, em ordem ===== --%>
            <c:when test="${visao == 'dia'}">
                <c:forEach var="dia" items="${dias}">
                    <ul class="agenda-dia">
                        <c:forEach var="b" items="${dia.bloqueios}">
                            <li class="agenda-item bloqueio">
                                <strong><c:out value="${b.periodoTexto}"/></strong>
                                <span>Horário bloqueado<c:if test="${not empty b.motivo}"> · <c:out value="${b.motivo}"/></c:if></span>
                            </li>
                        </c:forEach>
                        <c:forEach var="s" items="${dia.sessoes}">
                            <li class="agenda-item ${s.status == 'PENDENTE' ? 'pendente' : ''}">
                                <strong>${s.periodoTexto}</strong>
                                <span><c:out value="${s.alunoNome}"/> · <c:out value="${s.disciplina}"/>
                                    · ${s.duracaoTexto}
                                    <c:if test="${s.status == 'PENDENTE'}"> · aguardando confirmação</c:if></span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty dia.sessoes and empty dia.bloqueios}">
                            <li class="vazio">Nenhuma sessão nem bloqueio neste dia.</li>
                        </c:if>
                    </ul>
                </c:forEach>
            </c:when>

            <%-- ===== visão da semana: uma coluna por dia (domingo a sábado) ===== --%>
            <c:when test="${visao == 'semana'}">
                <table class="cal-tabela cal-semana">
                    <thead>
                    <tr>
                        <c:forEach var="dia" items="${dias}">
                            <th scope="col" class="${dia.hoje ? 'hoje' : ''}">
                                <a href="${ctx}/mentor-inicio?visao=dia&amp;data=${dia.dataIso}">${dia.rotulo}</a>
                            </th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <c:forEach var="dia" items="${dias}">
                            <td class="cal-dia ${dia.hoje ? 'hoje' : ''}">
                                <c:forEach var="b" items="${dia.bloqueios}">
                                    <span class="cal-evento bloqueio" title="<c:out value='${b.motivo}'/>">
                                        <strong>Bloqueado</strong><br><c:out value="${b.periodoTexto}"/>
                                    </span>
                                </c:forEach>
                                <c:forEach var="s" items="${dia.sessoes}">
                                    <span class="cal-evento ${s.status == 'PENDENTE' ? 'pendente' : ''}">
                                        <strong>${s.periodoTexto}</strong><br>
                                        <c:out value="${s.alunoPrimeiroNome}"/> · ${s.duracaoTexto}
                                    </span>
                                </c:forEach>
                            </td>
                        </c:forEach>
                    </tr>
                    </tbody>
                </table>
            </c:when>

            <%-- ===== visão do mês (padrão) ===== --%>
            <c:otherwise>
                <table class="cal-tabela">
                    <thead>
                    <tr>
                        <th scope="col"><span class="longo">Domingo</span><span class="curto">Dom</span></th>
                        <th scope="col"><span class="longo">Segunda</span><span class="curto">Seg</span></th>
                        <th scope="col"><span class="longo">Terça</span><span class="curto">Ter</span></th>
                        <th scope="col"><span class="longo">Quarta</span><span class="curto">Qua</span></th>
                        <th scope="col"><span class="longo">Quinta</span><span class="curto">Qui</span></th>
                        <th scope="col"><span class="longo">Sexta</span><span class="curto">Sex</span></th>
                        <th scope="col"><span class="longo">Sábado</span><span class="curto">Sáb</span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%-- um forEach por dentro do outro: semanas (linhas) e dias (colunas) --%>
                    <c:forEach var="semana" items="${semanas}">
                        <tr>
                            <c:forEach var="dia" items="${semana}">
                                <td class="cal-dia ${dia.foraDoMes ? 'fora' : ''} ${dia.hoje ? 'hoje' : ''}">
                                    <a class="cal-num" href="${ctx}/mentor-inicio?visao=dia&amp;data=${dia.dataIso}">${dia.dia}</a>
                                    <c:forEach var="b" items="${dia.bloqueios}">
                                        <span class="cal-evento bloqueio" title="<c:out value='${b.periodoTexto}'/>">
                                            <span class="ev-prefixo">Bloqueado</span>
                                        </span>
                                    </c:forEach>
                                    <c:forEach var="s" items="${dia.sessoes}">
                                        <%-- sessão pendente aparece mais clara (classe "pendente") --%>
                                        <span class="cal-evento ${s.status == 'PENDENTE' ? 'pendente' : ''}" title="${s.periodoTexto} (${s.duracaoTexto})">
                                            ${s.horaTexto} <span class="ev-prefixo">Aula com </span><c:out value="${s.alunoPrimeiroNome}"/>
                                        </span>
                                    </c:forEach>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- coluna da direita: sessões que o mentor ainda precisa confirmar ou já confirmou --%>
    <section aria-labelledby="t-proximas">
        <h2 id="t-proximas" class="titulo-secao">Próximas sessões</h2>
        <ul class="lista-sessoes">
            <c:forEach var="s" items="${proximas}">
                <li class="bloco">
                    <article class="sessao">
                        <div class="sessao-topo"><span>${s.diaTexto}</span><span>${s.horaTexto}</span></div>
                        <div class="sessao-corpo">
                            <span class="avatar" style="--tam:52px" aria-hidden="true">${s.iniciais}</span>
                            <span class="sessao-nome"><c:out value="${s.alunoNome}"/></span>
                        </div>
                        <div class="sessao-duracao">${s.horas}h</div>
                    </article>

                    <%-- os dois botões enviam o mesmo form, o name/value do botão clicado diz a ação --%>
                    <form class="acoes" action="${ctx}/mentor-inicio" method="post">
                        <input type="hidden" name="id" value="${s.id}">
                        <input type="hidden" name="visao" value="${visao}">
                        <input type="hidden" name="data" value="${dataRef}">
                        <c:choose>
                            <c:when test="${s.status == 'CONFIRMADA'}">
                                <c:choose>
                                    <c:when test="${s.jaComecou}">
                                        <button type="submit" name="acao" value="realizar" class="pilula-acao realizar"
                                                onclick="return confirm('Marcar esta sessão como realizada?')">Marcar como realizada</button>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="pilula-acao confirmada">Confirmada</span>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" name="acao" value="confirmar" class="pilula-acao confirmar">Confirmar</button>
                            </c:otherwise>
                        </c:choose>
                        <button type="submit" name="acao" value="cancelar" class="pilula-acao cancelar"
                                onclick="return confirm('Cancelar esta sessão?')">Cancelar</button>
                    </form>
                </li>
            </c:forEach>
            <c:if test="${empty proximas}">
                <li class="vazio">Nada pra confirmar. Pedidos de mentoria novos aparecem aqui.</li>
            </c:if>
        </ul>
    </section>
</main>

</body>
</html>