<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Disciplinas | SENSEI.DEV</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        table { border-collapse: collapse; width: 100%; margin-top: 24px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background: #eee; }
        form.linha { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; }
        form.linha label { display: block; font-weight: bold; }
        form.linha input, form.linha select { padding: 8px; }
        form.acao { display: inline; margin-left: 6px; }
        .erro { color: #b00020; }
        .ok { color: #1b5e20; }
    </style>
</head>
<body>
<h1>Disciplinas</h1>
<p><a href="${pageContext.request.contextPath}/admin/inicio">Voltar ao painel</a></p>

<%-- mensagens devolvidas pelo servlet --%>
<c:choose>
    <c:when test="${param.erro == 'dados'}"><p class="erro">Confira os campos: informe o nome (até 150 caracteres) e um semestre de 1 a 8.</p></c:when>
    <c:when test="${param.erro == 'nome'}"><p class="erro">Já existe uma disciplina com esse nome.</p></c:when>
    <c:when test="${param.erro == 'vinculos'}"><p class="erro">Esta disciplina está em uso (mentores, interesses de alunos ou mentorias) e não pode ser excluída.</p></c:when>
    <c:when test="${param.ok == 'criada'}"><p class="ok">Disciplina criada.</p></c:when>
    <c:when test="${param.ok == 'atualizada'}"><p class="ok">Disciplina atualizada.</p></c:when>
    <c:when test="${param.ok == 'excluida'}"><p class="ok">Disciplina excluída.</p></c:when>
</c:choose>

<%-- formulario unico: cria quando o id esta vazio, edita quando vem preenchido (igual ao lab 5) --%>
<h2>
    <c:choose>
        <c:when test="${not empty disciplinaEdicao}">Editar disciplina</c:when>
        <c:otherwise>Nova disciplina</c:otherwise>
    </c:choose>
</h2>
<form class="linha" action="${pageContext.request.contextPath}/admin/disciplinas" method="post">
    <input type="hidden" name="acao" value="salvar">
    <input type="hidden" name="id" value="${disciplinaEdicao.id}">

    <div>
        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" maxlength="150" required size="40"
               value="<c:out value='${disciplinaEdicao.nome}'/>">
    </div>

    <div>
        <label for="semestre">Semestre</label>
        <select id="semestre" name="semestre" required>
            <c:forEach var="s" begin="1" end="8">
                <option value="${s}" ${disciplinaEdicao.semestre == s ? 'selected' : ''}>${s}º semestre</option>
            </c:forEach>
        </select>
    </div>

    <div>
        <button type="submit">Salvar</button>
        <c:if test="${not empty disciplinaEdicao}">
            <a href="${pageContext.request.contextPath}/admin/disciplinas">Cancelar</a>
        </c:if>
    </div>
</form>

<table>
    <tr>
        <th>ID</th>
        <th>Nome</th>
        <th>Semestre</th>
        <th>Ações</th>
    </tr>
    <c:forEach var="d" items="${disciplinas}">
        <tr>
            <td>${d.id}</td>
            <td><c:out value="${d.nome}"/></td>
            <td>${d.semestre}º</td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/disciplinas?editar=${d.id}">Editar</a>
                <form class="acao" action="${pageContext.request.contextPath}/admin/disciplinas" method="post"
                      onsubmit="return confirm('Excluir esta disciplina?');">
                    <input type="hidden" name="acao" value="excluir">
                    <input type="hidden" name="id" value="${d.id}">
                    <button type="submit">Excluir</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>