<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Avaliações recebidas. O MentorAvaliacaoServlet (/mentor-avaliacao) manda:
     mentor, avaliacoes, totalAvaliacoes, notaGeral e criterios (nome do critério e a média). --%>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Avaliação | SENSEI.DEV</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
    <jsp:param name="aba" value="avaliacao"/>
</jsp:include>

<main class="conteudo">
    <h1 class="titulo-secao">Avaliações</h1>
    <p class="subtitulo">
        ${totalAvaliacoes} ${totalAvaliacoes == 1 ? 'avaliação recebida' : 'avaliações recebidas'}
    </p>

    <div class="avaliacoes">
        <div class="coluna-resumo">
            <section class="resumo" aria-label="Nota geral">
                <strong class="nota-grande">
                    <fmt:formatNumber value="${notaGeral}" minFractionDigits="1" maxFractionDigits="1"/>
                </strong>

                <%-- Estrelas: duas camadas iguais, uma vazia e outra cheia por cima.
                     A cheia é cortada pela largura: nota 4,9 de 5 = 98% (nota * 20). --%>
                <span class="estrelas" role="img" aria-label="Nota ${notaGeral} de 5">
                    <span class="estrelas-base">
                        <c:forEach begin="1" end="5">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2.8l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.6l-5.8 3.1 1.1-6.5L2.6 9.6l6.5-.9z" stroke-width="1.6" stroke-linejoin="round"/></svg>
                        </c:forEach>
                    </span>
                    <span class="estrelas-cheias" style="width: ${notaGeral * 20}%">
                        <c:forEach begin="1" end="5">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2.8l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.6l-5.8 3.1 1.1-6.5L2.6 9.6l6.5-.9z" stroke-width="1.6" stroke-linejoin="round"/></svg>
                        </c:forEach>
                    </span>
                </span>
            </section>

            <section aria-label="Nota por critério">
                <ul class="criterios">
                    <%-- percorrer um Map no forEach: ${c.key} é o nome e ${c.value} a média --%>
                    <c:forEach var="crit" items="${criterios}">
                        <li class="criterio">
                            <span>${crit.key}</span>
                            <span><fmt:formatNumber value="${crit.value}" minFractionDigits="1" maxFractionDigits="1"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </section>
        </div>

        <section aria-label="Avaliações recebidas">
            <ul class="lista-avaliacoes">
                <c:forEach var="a" items="${avaliacoes}">
                    <li class="avaliacao">
                        <span class="avatar" style="--tam:52px" aria-hidden="true">${a.iniciais}</span>
                        <div class="avaliacao-quem">
                            <span class="avaliacao-nome"><c:out value="${a.alunoNome}"/></span>
                            <span class="avaliacao-meta">
                                ${a.diaTexto}<span class="ponto" aria-hidden="true"></span><c:out value="${a.disciplina}"/>
                            </span>
                        </div>
                        <p class="avaliacao-comentario"><c:out value="${a.comentario}"/></p>
                        <strong class="avaliacao-nota">
                            <fmt:formatNumber value="${a.nota}" minFractionDigits="1" maxFractionDigits="1"/>
                        </strong>
                    </li>
                </c:forEach>
                <c:if test="${empty avaliacoes}">
                    <li class="vazio">Ainda não há avaliações. Elas aparecem depois das sessões.</li>
                </c:if>
            </ul>
        </section>
    </div>
</main>

</body>
</html>