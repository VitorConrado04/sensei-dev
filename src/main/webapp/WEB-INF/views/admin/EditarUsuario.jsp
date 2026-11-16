<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar usuário | SENSEI.DEV</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<jsp:include page="/WEB-INF/fragmentos/cabecalho-admin.jsp"><jsp:param name="aba" value="usuarios"/></jsp:include><main class="conteudo admin-conteudo">
<h1>Editar usuário</h1>
<p><a href="${pageContext.request.contextPath}/admin/usuarios">Voltar à lista</a></p>

<c:if test="${not empty param.erro}">
    <p class="erro">
        <c:choose>
            <c:when test="${param.erro == 'email'}">Esse e-mail já está cadastrado em outro usuário.</c:when>
            <c:otherwise>Confira os campos: nome e e-mail válidos e, para aluno, um semestre de 1 a 8.</c:otherwise>
        </c:choose>
    </p>
</c:if>

<form class="admin-form" action="${pageContext.request.contextPath}/admin/usuario-editar" method="post">
    <%-- id escondido: diz ao servlet qual usuario esta sendo editado --%>
    <input type="hidden" name="id" value="${usuario.id}">

    <label for="nome">Nome</label>
    <input type="text" id="nome" name="nome" maxlength="150" required value="<c:out value='${usuario.nome}'/>">

    <label for="email">E-mail</label>
    <input type="email" id="email" name="email" maxlength="150" required
           <c:if test="${usuario.tipo == 'ADMIN'}">readonly</c:if>
           value="<c:out value='${usuario.email}'/>">

    <%-- semestre so aparece para mentorando --%>
    <c:if test="${usuario.tipo == 'MENTORANDO'}">
        <label for="semestre">Semestre</label>
        <select id="semestre" name="semestre" required>
            <c:forEach var="s" begin="1" end="8">
                <option value="${s}" ${usuario.semestreAtual == s ? 'selected' : ''}>${s}º semestre</option>
            </c:forEach>
        </select>
    </c:if>

    <label>Tipo</label>
    <input type="text" value="${usuario.tipo}" disabled>

    <label>Status</label>
    <input type="text" value="${usuario.status}" disabled>

    <button type="submit">Salvar</button>
</form>
</main>
</body>
</html>
