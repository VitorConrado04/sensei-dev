<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Notificações | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>
<c:choose>
    <c:when test="${tipoUsuario == 'MENTOR'}">
        <jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
            <jsp:param name="aba" value="notificacoes"/>
        </jsp:include>
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/fragmentos/cabecalho-aluno.jsp">
            <jsp:param name="aba" value="notificacoes"/>
        </jsp:include>
    </c:otherwise>
</c:choose>

<main class="conteudo">
    <div class="notificacoes-topo">
        <h1 class="titulo-secao">Notificações</h1>
        <c:if test="${notificacoesNaoLidas > 0}">
            <form action="${ctx}/notificacoes" method="post">
                <input type="hidden" name="acao" value="todas">
                <button class="notificacao-botao" type="submit">Marcar todas como lidas</button>
            </form>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty notificacoes}">
            <p class="vazio">Nenhuma notificação por enquanto.</p>
        </c:when>
        <c:otherwise>
            <ul class="lista-notificacoes">
                <c:forEach var="n" items="${notificacoes}">
                    <li class="notificacao${n.lida ? '' : ' nao-lida'}">
                        <div>
                            <p><c:out value="${n.mensagem}"/></p>
                            <small><c:out value="${n.criadoEmTexto}"/></small>
                            <c:if test="${n.mentoriaId != null}">
                                <c:choose>
                                    <c:when test="${tipoUsuario == 'MENTOR'}">
                                        <a class="notificacao-link" href="${ctx}/mentor-inicio">Ver agenda</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="notificacao-link" href="${ctx}/mentorias-aluno#mentoria-${n.mentoriaId}">Ver mentoria</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </div>
                        <div class="notificacao-acoes">
                            <c:if test="${not n.lida}">
                                <form action="${ctx}/notificacoes" method="post">
                                    <input type="hidden" name="acao" value="lida">
                                    <input type="hidden" name="id" value="${n.id}">
                                    <button class="notificacao-botao" type="submit">Marcar como lida</button>
                                </form>
                            </c:if>
                            <form action="${ctx}/notificacoes" method="post">
                                <input type="hidden" name="acao" value="excluir">
                                <input type="hidden" name="id" value="${n.id}">
                                <button class="notificacao-botao" type="submit">Excluir</button>
                            </form>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
