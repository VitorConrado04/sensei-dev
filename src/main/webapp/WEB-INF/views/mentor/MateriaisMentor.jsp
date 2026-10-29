<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Materiais do mentor. O MentorMaterialServlet (/mentor-material) manda "mentor", "materiais" e "mentorias"
     (as sessões que podem receber material). Adicionar, editar e remover são formulários POST pro mesmo servlet.
     Um material vinculado a uma mentoria aparece para o mentorando dessa mentoria em "Minhas mentorias". --%>
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
    <c:if test="${param.erro == 'vinculo'}">
        <p class="erro" role="alert">Não foi possível vincular: escolha uma das suas mentorias (não cancelada).</p>
    </c:if>
    <c:if test="${param.salvo == '1'}">
        <p class="subtitulo" role="status">Material salvo com sucesso.</p>
    </c:if>

    <ul class="materiais">
        <c:forEach var="m" items="${materiais}">
            <li>
                <a class="material-card" href="<c:out value='${m.url}'/>" target="_blank" rel="noopener noreferrer">
                    <span class="material-titulo"><c:out value="${m.titulo}"/></span>
                    <c:if test="${not empty m.descricao}">
                        <span class="material-descricao"><c:out value="${m.descricao}"/></span>
                    </c:if>
                    <span class="material-meta">
                        ${m.diaTexto}<span class="ponto" aria-hidden="true"></span>
                        <c:choose>
                            <c:when test="${m.mentoriaId != null}">Mentoria: <c:out value="${m.mentoriaTexto}"/></c:when>
                            <c:otherwise>Material geral</c:otherwise>
                        </c:choose>
                    </span>
                </a>

                <div class="material-acoes">
                    <%-- os data-* levam os valores atuais pro diálogo de edição (js/mentor.js) --%>
                    <button type="button" class="btn-editar-material"
                            data-id="${m.id}"
                            data-titulo="<c:out value='${m.titulo}'/>"
                            data-url="<c:out value='${m.url}'/>"
                            data-descricao="<c:out value='${m.descricao}'/>"
                            data-mentoria="${m.mentoriaId}">Editar</button>

                    <%-- mesmo esquema do delete do lab 4: form com o id escondido --%>
                    <form class="material-remover" action="${ctx}/mentor-material" method="post">
                        <input type="hidden" name="acao" value="remover">
                        <input type="hidden" name="id" value="${m.id}">
                        <button type="submit" onclick="return confirm('Remover este material?')">Remover</button>
                    </form>
                </div>
            </li>
        </c:forEach>
        <c:if test="${empty materiais}">
            <li class="vazio">Nenhum material ainda. Adicione um link pros seus mentorandos.</li>
        </c:if>
    </ul>
</main>

<%-- janelinha de material (elemento dialog do HTML, aberto pelo js/mentor.js).
     Serve para adicionar e para editar: o js troca o título, o "acao" e o "id" escondidos. --%>
<dialog id="dialog-material" aria-labelledby="t-novo-material">
    <h2 id="t-novo-material">Novo material</h2>

    <form action="${ctx}/mentor-material" method="post">
        <input type="hidden" name="acao" id="material-acao" value="adicionar">
        <input type="hidden" name="id" id="material-id" value="">

        <div class="grupo">
            <label for="material-titulo">Título:</label>
            <input type="text" id="material-titulo" name="titulo" maxlength="150" required autocomplete="off">
        </div>

        <div class="grupo">
            <label for="material-url">Link:</label>
            <input type="url" id="material-url" name="url" maxlength="500" required
                   pattern="https?://.+" placeholder="https://github.com/..." autocomplete="off">
        </div>

        <div class="grupo">
            <label for="material-descricao">Descrição (opcional):</label>
            <textarea id="material-descricao" name="descricao" maxlength="500" rows="3"></textarea>
        </div>

        <div class="grupo">
            <label for="material-mentoria">Vincular à mentoria:</label>
            <select id="material-mentoria" name="mentoriaId">
                <option value="">Nenhuma (material geral)</option>
                <c:forEach var="s" items="${mentorias}">
                    <option value="${s.id}"><c:out value="${s.rotulo}"/></option>
                </c:forEach>
            </select>
        </div>

        <div class="dialog-acoes">
            <button type="button" id="btn-cancelar-material" class="btn-pilula">Cancelar</button>
            <button type="submit" id="btn-salvar-material" class="btn-pilula btn-roxo">Adicionar material</button>
        </div>
    </form>
</dialog>

<script src="${ctx}/js/mentor.js"></script>
</body>
</html>