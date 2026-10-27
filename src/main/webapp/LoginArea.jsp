<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SENSEI.DEV - Login</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<main class="tela-login">

    <header>
        <span class="logo">SENSEI.DEV</span>
    </header>

    <p class="link-login">
                Crie sua conta!
                <a href="${pageContext.request.contextPath}/CadastroAluno.jsp">Cadastre-se</a>
            </p>

    <c:if test="${param.cadastro == 'sucesso'}">
        <p class="mensagem" role="status">Cadastro concluído! Entre com seu e-mail e senha.</p>
    </c:if>
    <c:if test="${param.saiu == '1'}">
        <p class="mensagem" role="status">Você saiu da sua conta.</p>
    </c:if>
    <c:if test="${not empty param.erro}">
        <p class="erro visivel" role="alert">
            <c:choose>
                <c:when test="${param.erro == 'campos'}">Preencha e-mail e senha.</c:when>
                <c:when test="${param.erro == 'credenciais'}">E-mail ou senha incorretos.</c:when>
                <c:when test="${param.erro == 'sessao'}">Entre novamente para acessar essa página.</c:when>
                <c:otherwise>Não foi possível acessar o banco de dados. Tente novamente.</c:otherwise>
            </c:choose>
        </p>
    </c:if>

    <form id="form-login" action="${pageContext.request.contextPath}/login"
          method="post" novalidate>

        <div class="grupo">
            <label for="email">E-mail:</label>
            <input type="email" id="email" name="email" autocomplete="email" required>
        </div>

        <div class="grupo">
            <label for="senha">Senha:</label>
            <input type="password" id="senha" name="senha" autocomplete="current-password" required>
        </div>

        <p class="erro" id="erro-login" role="alert">Preencha e-mail e senha.</p>

        <button type="submit" class="btn-entrar">Entrar</button>
    </form>

    <footer class="redes">
        <a href="#" aria-label="Instagram">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <rect x="3" y="3" width="18" height="18" rx="5"/>
                <circle cx="12" cy="12" r="4"/>
                <circle cx="17.5" cy="6.5" r="1" fill="currentColor" stroke="none"/>
            </svg>
        </a>

        <a href="#" aria-label="LinkedIn">
            <svg viewBox="0 0 24 24" aria-hidden="true">
                <rect x="3" y="3" width="18" height="18" rx="3" fill="currentColor"/>
                <path fill="#1e1c22" d="M7 10h2v7H7zM8 7a1.1 1.1 0 1 1 0 2.2A1.1 1.1 0 0 1 8 7zm3.5 3h1.9v1c.4-.7 1.2-1.2 2.3-1.2 2 0 2.8 1.2 2.8 3.2V17h-2v-3.4c0-1-.3-1.7-1.3-1.7s-1.7.7-1.7 1.8V17h-2z"/>
            </svg>
        </a>

        <a href="#" aria-label="X (Twitter)">
            <svg viewBox="0 0 24 24" aria-hidden="true">
                <path fill="currentColor" d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>
            </svg>
        </a>
    </footer>
</main>

<script>
    const form = document.getElementById('form-login');
    const erro = document.getElementById('erro-login');

    // Validação simples (o form tem novalidate)
    form.addEventListener('submit', (e) => {
        const vazio = !form.email.value.trim() || !form.senha.value;
        erro.classList.toggle('visivel', vazio);
        if (vazio) e.preventDefault();
    });
</script>
</body>
</html>