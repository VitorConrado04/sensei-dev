<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cadastro de Mentor | SENSEI.DEV</title>
    <link href="https://fonts.googleapis.com/css2?family=Bruno+Ace&family=Asap:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="pagina">

    <section class="painel-marca" aria-label="Marca">
        <span class="logo">SENSEI.DEV</span>
    </section>

    <section class="painel-form">
        <p class="link-login">
            Você já tem uma conta? <a href="${pageContext.request.contextPath}/LoginArea.jsp">Faça Log In</a>
        </p>

        <h1>Preencha seus dados</h1>
        <p>${erro}</p>

        <!-- Troque o action pelo seu Servlet (ex.: @WebServlet("/cadastro-mentor")) -->
        <form id="form-cadastro" action="${pageContext.request.contextPath}/cadastro-mentor"
              method="post" enctype="multipart/form-data" novalidate>

            <div class="grupo">
                <label for="nome">Digite seu nome:</label>
                <input type="text" id="nome" name="nome" required autocomplete="name">
            </div>

            <div class="grupo">
                <label for="email">E-Mail:</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>

            <div class="grupo">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required minlength="6" autocomplete="new-password">
            </div>

            <div class="grupo">
                <label for="confirmarSenha">Confirmar senha:</label>
                <input type="password" id="confirmarSenha" name="confirmarSenha" required autocomplete="new-password">
                <p class="erro-senha" id="erro-senha" role="alert">As senhas não coincidem.</p>
            </div>

            <div class="upload">
                <label for="foto">Foto de perfil</label>
                <div class="upload-area" id="upload-area">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <circle cx="8.5" cy="6.5" r="2.5"/>
                        <path d="M2 20l6-8 4 5 3-4 7 7z"/>
                    </svg>
                    <p>Faça um upload de uma imagem .png ou .jpg.</p>
                    <img class="previa" id="previa" alt="Prévia da foto de perfil">
                    <input type="file" id="foto" name="foto" accept=".png,.jpg,.jpeg,image/png,image/jpeg">
                </div>
            </div>

            <div class="acoes">
                <a class="link-aluno" href="${pageContext.request.contextPath}/CadastroAluno.jsp">
                    Sou Aluno
                    <svg viewBox="0 0 36 24" aria-hidden="true">
                        <path d="M0 8h20V0l16 12-16 12v-8H0z"/>
                    </svg>
                </a>
                <button type="submit" class="btn-cadastrar">Cadastre-se</button>
            </div>
        </form>
    </section>
</main>

<script>
    const form = document.getElementById('form-cadastro');
    const senha = document.getElementById('senha');
    const confirmar = document.getElementById('confirmarSenha');
    const erro = document.getElementById('erro-senha');
    const foto = document.getElementById('foto');
    const area = document.getElementById('upload-area');
    const previa = document.getElementById('previa');

    // Valida se as senhas coincidem antes de enviar
    form.addEventListener('submit', (e) => {
        if (senha.value !== confirmar.value) {
            e.preventDefault();
            erro.classList.add('visivel');
            confirmar.focus();
        } else {
            erro.classList.remove('visivel');
        }
    });

    confirmar.addEventListener('input', () => {
        erro.classList.toggle('visivel', confirmar.value !== '' && senha.value !== confirmar.value);
    });

    // Prévia da imagem escolhida
    foto.addEventListener('change', () => {
        const arquivo = foto.files[0];
        if (!arquivo) { area.classList.remove('com-imagem'); return; }

        if (!['image/png', 'image/jpeg'].includes(arquivo.type)) {
            alert('Escolha uma imagem .png ou .jpg.');
            foto.value = '';
            area.classList.remove('com-imagem');
            return;
        }
        previa.src = URL.createObjectURL(arquivo);
        area.classList.add('com-imagem');
    });
</script>
</body>
</html>
