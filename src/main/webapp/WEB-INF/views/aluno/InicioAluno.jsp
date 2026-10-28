<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Área do mentorando | SENSEI.DEV</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<main class="tela-login">
    <header><span class="logo">SENSEI.DEV</span></header>
    <section aria-labelledby="titulo-aluno">
        <h1 id="titulo-aluno">Olá, <c:out value="${sessionScope.usuarioNome}"/>!</h1>
        <p>Sua conta de mentorando foi autenticada com sucesso.</p>
        <p>Esta é a área inicial da conta. As funções de procurar mentores e solicitar mentorias ainda precisam ser desenvolvidas.</p>
        <p><a href="${pageContext.request.contextPath}/conta">Minha conta</a></p>
        <p><a href="${pageContext.request.contextPath}/perfil-aluno">Meu perfil</a></p>
        <p><a href="${pageContext.request.contextPath}/logout">Sair da conta</a></p>
    </section>
</main>
</body>
</html>
