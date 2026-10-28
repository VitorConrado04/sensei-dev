<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Minha conta | SENSEI.DEV</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; max-width: 480px; }
        label { display: block; margin-top: 12px; font-weight: bold; }
        input, select { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; }
        fieldset { margin-top: 24px; border: 1px solid #ccc; }
        .erro { color: #b00020; }
        .ok { color: #1b5e20; }
        button { margin-top: 16px; padding: 8px 16px; }
    </style>
</head>
<body>
<h1>Minha conta</h1>

<%-- volta para a tela inicial de cada tipo de conta --%>
<p>
    <c:choose>
        <c:when test="${sessionScope.usuarioTipo == 'MENTOR'}">
            <a href="${pageContext.request.contextPath}/mentor-inicio">Voltar</a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/inicio-aluno">Voltar</a>
        </c:otherwise>
    </c:choose>
</p>

<c:choose>
    <c:when test="${param.ok == '1'}"><p class="ok">Dados salvos.</p></c:when>
    <c:when test="${param.erro == 'dados'}"><p class="erro">Confira os campos: nome e e-mail válidos e, para aluno, um semestre de 1 a 8.</p></c:when>
    <c:when test="${param.erro == 'email'}"><p class="erro">Esse e-mail já está em uso por outra conta.</p></c:when>
    <c:when test="${param.erro == 'senhaNova'}"><p class="erro">A senha nova precisa ter pelo menos 8 caracteres e a confirmação deve ser igual.</p></c:when>
    <c:when test="${param.erro == 'senhaAtual'}"><p class="erro">A senha atual não confere.</p></c:when>
</c:choose>

<form action="${pageContext.request.contextPath}/conta" method="post">
    <label for="nome">Nome</label>
    <input type="text" id="nome" name="nome" maxlength="150" required value="<c:out value='${usuario.nome}'/>">

    <label for="email">E-mail</label>
    <input type="email" id="email" name="email" maxlength="150" required value="<c:out value='${usuario.email}'/>">

    <%-- semestre so aparece para mentorando --%>
    <c:if test="${usuario.tipo == 'MENTORANDO'}">
        <label for="semestre">Semestre</label>
        <select id="semestre" name="semestre" required>
            <c:forEach var="s" begin="1" end="8">
                <option value="${s}" ${usuario.semestreAtual == s ? 'selected' : ''}>${s}º semestre</option>
            </c:forEach>
        </select>
    </c:if>

    <fieldset>
        <legend>Alterar senha (opcional)</legend>
        <p>Deixe em branco para manter a senha atual.</p>

        <label for="senhaAtual">Senha atual</label>
        <input type="password" id="senhaAtual" name="senhaAtual" autocomplete="current-password">

        <label for="senhaNova">Senha nova (mínimo 8 caracteres)</label>
        <input type="password" id="senhaNova" name="senhaNova" autocomplete="new-password">

        <label for="confirmarSenha">Confirmar senha nova</label>
        <input type="password" id="confirmarSenha" name="confirmarSenha" autocomplete="new-password">
    </fieldset>

    <button type="submit">Salvar</button>
</form>
</body>
</html>