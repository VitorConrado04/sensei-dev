<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- volta para a tela inicial de cada tipo de conta --%>
<c:set var="voltar" value="${sessionScope.usuarioTipo == 'MENTOR' ? '/mentor-inicio' : '/inicio-aluno'}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Minha conta | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <%-- mentor.css traz a paleta e as fontes do app; conta.css so estiliza esta tela --%>
    <link rel="stylesheet" href="${ctx}/css/mentor.css">
    <link rel="stylesheet" href="${ctx}/css/conta.css?v=1">
</head>
<body>
<main class="conta-pagina">
    <a class="conta-logo" href="${ctx}${voltar}">SENSEI.DEV</a>

    <section class="conta-card" aria-labelledby="titulo-conta">
        <h1 id="titulo-conta">Minha conta</h1>
        <a class="conta-voltar" href="${ctx}${voltar}">&larr; Voltar</a>

        <c:choose>
            <c:when test="${param.ok == '1'}"><p class="conta-aviso ok" role="status">Dados salvos.</p></c:when>
            <c:when test="${param.erro == 'dados'}"><p class="conta-aviso erro" role="alert">Confira os campos: nome e e-mail válidos e, para aluno, um semestre de 1 a 5.</p></c:when>
            <c:when test="${param.erro == 'email'}"><p class="conta-aviso erro" role="alert">Esse e-mail já está em uso por outra conta.</p></c:when>
            <c:when test="${param.erro == 'senhaNova'}"><p class="conta-aviso erro" role="alert">A senha nova precisa ter pelo menos 8 caracteres e a confirmação deve ser igual.</p></c:when>
            <c:when test="${param.erro == 'senhaAtual'}"><p class="conta-aviso erro" role="alert">A senha atual não confere.</p></c:when>
        </c:choose>

        <form action="${ctx}/conta" method="post">
            <div class="conta-campo">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" maxlength="150" required value="<c:out value='${usuario.nome}'/>">
            </div>

            <div class="conta-campo">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email" maxlength="150" required value="<c:out value='${usuario.email}'/>">
            </div>

            <%-- semestre so aparece para mentorando --%>
            <c:if test="${usuario.tipo == 'MENTORANDO'}">
                <div class="conta-campo">
                    <label for="semestre">Semestre</label>
                    <select id="semestre" name="semestre" required>
                        <c:forEach var="s" begin="1" end="5">
                            <option value="${s}" ${usuario.semestreAtual == s ? 'selected' : ''}>${s}º semestre</option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

            <fieldset class="conta-senha">
                <legend>Alterar senha (opcional)</legend>
                <p>Deixe em branco para manter a senha atual.</p>

                <div class="conta-campo">
                    <label for="senhaAtual">Senha atual</label>
                    <input type="password" id="senhaAtual" name="senhaAtual" autocomplete="current-password">
                </div>

                <div class="conta-campo">
                    <label for="senhaNova">Senha nova (mínimo 8 caracteres)</label>
                    <input type="password" id="senhaNova" name="senhaNova" autocomplete="new-password">
                </div>

                <div class="conta-campo">
                    <label for="confirmarSenha">Confirmar senha nova</label>
                    <input type="password" id="confirmarSenha" name="confirmarSenha" autocomplete="new-password">
                </div>
            </fieldset>

            <button type="submit" class="conta-salvar">Salvar</button>
        </form>
    </section>
</main>
</body>
</html>