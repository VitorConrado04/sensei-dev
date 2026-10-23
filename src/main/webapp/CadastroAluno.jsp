<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SENSEI.DEV - Cadastro de Aluno</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="pagina">

    <!-- Painel esquerdo -->
    <section class="painel-marca">
        <span class="logo">SENSEI.DEV</span>
    </section>

    <!-- Painel direito -->
    <section class="painel-form">
        <p class="link-login">
            Você já tem uma conta?
            <a href="${pageContext.request.contextPath}/LoginArea.jsp">Faça Log In</a>
        </p>

        <h1>Preencha seus dados</h1>

        <form id="form-cadastro"
              action="${pageContext.request.contextPath}/cadastro-aluno"
              method="post" enctype="multipart/form-data" novalidate>

            <div class="grupo">
                <label for="nome">Digite seu nome:</label>
                <input type="text" id="nome" name="nome" required>
            </div>

            <div class="grupo">
                <label for="email">E-Mail:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="grupo">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required>
            </div>

            <div class="grupo">
                <label for="confirmar-senha">Confirmar senha:</label>
                <input type="password" id="confirmar-senha" name="confirmarSenha" required>
                <p class="erro-senha" id="erro-senha">As senhas não coincidem.</p>
            </div>

            <!-- Foto + Semestre lado a lado -->
            <div class="linha-dupla">

                <div class="upload">
                    <label for="foto">Foto de perfil</label>
                    <div class="upload-area" id="upload-area">
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <circle cx="12" cy="7" r="3.2"/>
                            <path d="M2 21l6.5-9 4.5 6 3-4 6 7z"/>
                        </svg>
                        <p>Faça um upload de uma imagem .png ou .jpg.</p>
                        <input type="file" id="foto" name="foto" accept="image/png, image/jpeg">
                        <img class="previa" id="previa" alt="Prévia da foto de perfil">
                    </div>
                </div>

                <div class="grupo grupo-semestre">
                    <label for="semestre">Semestre atual:</label>
                    <select id="semestre" name="semestre" required>
                        <option value="" disabled selected>Selecione</option>
                        <option value="1">1º semestre</option>
                        <option value="2">2º semestre</option>
                        <option value="3">3º semestre</option>
                        <option value="4">4º semestre</option>
                        <option value="5">5º semestre</option>
                    </select>
                    <p class="erro-senha" id="erro-semestre">Selecione seu semestre.</p>
                </div>

            </div>

            <div class="acoes">
                <a class="link-aluno" href="${pageContext.request.contextPath}/CadastroMentor.jsp">
                    Sou Mentor
                    <svg viewBox="0 0 36 24" aria-hidden="true">
                        <path fill="currentColor" d="M0 9h22V2l14 10-14 10v-7H0z"/>
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
    const confirmar = document.getElementById('confirmar-senha');
    const erroSenha = document.getElementById('erro-senha');
    const semestre = document.getElementById('semestre');
    const erroSemestre = document.getElementById('erro-semestre');
    const foto = document.getElementById('foto');
    const area = document.getElementById('upload-area');
    const previa = document.getElementById('previa');

    // Prévia da foto
    foto.addEventListener('change', () => {
        const arquivo = foto.files[0];
        if (arquivo) {
            previa.src = URL.createObjectURL(arquivo);
            area.classList.add('com-imagem');
        } else {
            area.classList.remove('com-imagem');
        }
    });

    // Validação no envio (o form tem novalidate)
    form.addEventListener('submit', (e) => {
        let ok = true;

        if (senha.value !== confirmar.value) {
            erroSenha.classList.add('visivel');
            ok = false;
        } else {
            erroSenha.classList.remove('visivel');
        }

        if (!semestre.value) {
            erroSemestre.classList.add('visivel');
            ok = false;
        } else {
            erroSemestre.classList.remove('visivel');
        }

        if (!ok) e.preventDefault();
    });
</script>
</body>
</html>
