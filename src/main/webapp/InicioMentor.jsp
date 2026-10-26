<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Tela inicial do mentor. Quem chama é o MentorInicioServlet (/mentor-inicio),
     que manda: mentor, historico, proximas, semanas, mesTitulo, mesAnterior e mesProximo.
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

    <%-- meio: calendário do mês. As setas recarregam a página com ?mes=aaaa-mm --%>
    <section class="calendario" aria-label="Calendário de sessões">
        <div class="cal-topo">
            <a class="cal-nav" href="${ctx}/mentor-inicio?mes=${mesAnterior}" aria-label="Mês anterior">
                <svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="15 5 8 12 15 19"/></svg>
            </a>
            <h2 class="cal-mes">${mesTitulo}</h2>
            <a class="cal-nav" href="${ctx}/mentor-inicio?mes=${mesProximo}" aria-label="Próximo mês">
                <svg viewBox="0 0 24 24" aria-hidden="true"><polyline points="9 5 16 12 9 19"/></svg>
            </a>
        </div>

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
                            <span class="cal-num">${dia.dia}</span>
                            <c:forEach var="s" items="${dia.sessoes}">
                                <%-- sessão pendente aparece mais clara (classe "pendente") --%>
                                <span class="cal-evento ${s.status == 'PENDENTE' ? 'pendente' : ''}" title="${s.horaTexto}">
                                    <span class="ev-prefixo">Aula com </span><c:out value="${s.alunoPrimeiroNome}"/>
                                </span>
                            </c:forEach>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>
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
                        <c:choose>
                            <c:when test="${s.status == 'CONFIRMADA'}">
                                <span class="pilula-acao confirmada">Confirmada</span>
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
