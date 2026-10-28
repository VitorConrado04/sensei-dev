<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Painel do admin | SENSEI.DEV</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<main class="tela-login">
    <header><span class="logo">SENSEI.DEV</span></header>
    <section aria-labelledby="titulo-admin">
        <h1 id="titulo-admin">Olá, <c:out value="${sessionScope.usuarioNome}"/>!</h1>
        <p>Painel do administrador. Aqui vai ficar o gerenciamento de usuários e disciplinas.</p>
        <p><a href="${pageContext.request.contextPath}/admin/usuarios">Gerenciar usuários</a></p>
        <p><a href="${pageContext.request.contextPath}/logout">Sair da conta</a></p>
    </section>
</main>
</body>
</html>