<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Usuários | SENSEI.DEV</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #eee; }
        form.acao { display: inline; margin-left: 6px; }
        .erro { color: #b00020; }
        .ok { color: #1b5e20; }
    </style>
</head>
<body>
<h1>Usuários</h1>
<p><a href="${pageContext.request.contextPath}/admin/inicio">Voltar ao painel</a></p>

<%-- mensagens devolvidas pelo servlet de acoes --%>
<c:choose>
    <c:when test="${param.erro == 'admin'}"><p class="erro">O administrador não pode ser suspenso nem excluído.</p></c:when>
    <c:when test="${param.erro == 'vinculos'}"><p class="erro">Este usuário tem dados vinculados (mentorias, horários, materiais...). Suspenda a conta em vez de excluir.</p></c:when>
    <c:when test="${param.erro == 'acao'}"><p class="erro">Ação inválida.</p></c:when>
    <c:when test="${param.ok == 'suspenso'}"><p class="ok">Usuário suspenso.</p></c:when>
    <c:when test="${param.ok == 'reativado'}"><p class="ok">Usuário reativado.</p></c:when>
    <c:when test="${param.ok == 'excluido'}"><p class="ok">Usuário excluído.</p></c:when>
</c:choose>

<table>
    <tr>
        <th>ID</th>
        <th>Nome</th>
        <th>E-mail</th>
        <th>Semestre</th>
        <th>Tipo</th>
        <th>Status</th>
        <th>Ações</th>
    </tr>
    <%-- um <tr> para cada usuario da lista enviada pelo servlet --%>
    <c:forEach var="u" items="${usuarios}">
        <tr>
            <td>${u.id}</td>
            <td><c:out value="${u.nome}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td><c:out value="${u.semestreAtual}" default="-"/></td>
            <td>${u.tipo}</td>
            <td>${u.status}</td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/usuario-editar?id=${u.id}">Editar</a>

                <%-- o admin nao tem botoes de suspender nem excluir --%>
                <c:if test="${u.tipo != 'ADMIN'}">
                    <form class="acao" action="${pageContext.request.contextPath}/admin/usuario-acao" method="post">
                        <input type="hidden" name="id" value="${u.id}">
                        <c:choose>
                            <c:when test="${u.status == 'ATIVO'}">
                                <button type="submit" name="acao" value="suspender">Suspender</button>
                            </c:when>
                            <c:otherwise>
                                <button type="submit" name="acao" value="reativar">Reativar</button>
                            </c:otherwise>
                        </c:choose>
                    </form>

                    <form class="acao" action="${pageContext.request.contextPath}/admin/usuario-acao" method="post"
                          onsubmit="return confirm('Excluir este usuário de vez? Esta ação não pode ser desfeita.');">
                        <input type="hidden" name="id" value="${u.id}">
                        <button type="submit" name="acao" value="excluir">Excluir</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>