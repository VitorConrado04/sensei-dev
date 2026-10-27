<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Materiais do mentor. O MentorMaterialServlet (/mentor-material) manda "mentor" e "materiais".
     Adicionar e remover são formulários POST pro mesmo servlet. --%>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Material | SENSEI.DEV</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
    <jsp:param name="aba" value="material"/>
</jsp:include>

<main class="conteudo">
    <div class="pagina-topo">
        <div>
            <h1 class="titulo-secao">Materiais Disponíveis</h1>
            <p class="subtitulo">Disponibilize materiais extras para seus mentorandos</p>
        </div>
        <button type="button" id="btn-add-material" class="btn-pilula">+ ADICIONAR MATERIAL</button>
    </div>

    <%-- o servlet redireciona com ?erro=1 quando título ou link não passam na validação --%>
    <c:if test="${param.erro == '1'}">
        <p class="erro" role="alert">Preencha o título e um link que comece com http:// ou https://.</p>
    </c:if>
    <c:if test="${param.salvo == '1'}">
        <p class="subtitulo" role="status">Material salvo com sucesso.</p>
    </c:if>

    <ul class="materiais">
        <c:forEach var="m" items="${materiais}">
            <li>
                <a class="material-card" href="<c:out value='${m.url}'/>" target="_blank" rel="noopener noreferrer">
                    <span class="material-titulo"><c:out value="${m.titulo}"/></span>
                    <span class="material-meta">
                        ${m.diaTexto}<span class="ponto" aria-hidden="true"></span>Link compartilhável
                    </span>
                </a>

                <%-- mesmo esquema do delete do lab 4: form com o id escondido --%>
                <form class="material-remover" action="${ctx}/mentor-material" method="post">
                    <input type="hidden" name="acao" value="remover">
                    <input type="hidden" name="id" value="${m.id}">
                    <button type="submit" onclick="return confirm('Remover este material?')">Remover</button>
                </form>
            </li>
        </c:forEach>
        <c:if test="${empty materiais}">
            <li class="vazio">Nenhum material ainda. Adicione um link pros seus mentorandos.</li>
        </c:if>
    </ul>
</main>

<%-- janelinha de novo material (elemento dialog do HTML, aberto pelo js/mentor.js) --%>
<dialog id="dialog-material" aria-labelledby="t-novo-material">
    <h2 id="t-novo-material">Novo material</h2>

    <form action="${ctx}/mentor-material" method="post">
        <div class="grupo">
            <label for="material-titulo">Título:</label>
            <input type="text" id="material-titulo" name="titulo" maxlength="150" required autocomplete="off">
        </div>

        <div class="grupo">
            <label for="material-url">Link:</label>
            <input type="url" id="material-url" name="url" maxlength="500" required
                   pattern="https?://.+" placeholder="https://github.com/..." autocomplete="off">
        </div>

        <div class="dialog-acoes">
            <button type="button" id="btn-cancelar-material" class="btn-pilula">Cancelar</button>
            <button type="submit" class="btn-pilula btn-roxo">Adicionar material</button>
        </div>
    </form>
</dialog>

<script src="${ctx}/js/mentor.js"></script>
</body>
</html>