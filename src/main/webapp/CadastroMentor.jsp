<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

        <h1>Cadastro de mentor</h1>

        <c:if test="${not empty param.erro}">
            <p role="alert" style="color:#b3261e;margin:0 0 1rem;width:100%;">
                <c:choose>
                    <c:when test="${param.erro == 'dados'}">Confira os campos. A senha deve ter pelo menos 8 caracteres, as senhas devem coincidir e o semestre deve ser selecionado.</c:when>
                    <c:when test="${param.erro == 'email'}">Esse e-mail já está cadastrado.</c:when>
                    <c:when test="${param.erro == 'foto'}">A foto deve ser PNG ou JPG e ter no máximo 2 MB.</c:when>
                    <c:otherwise>Não foi possível concluir o cadastro. Tente novamente.</c:otherwise>
                </c:choose>
            </p>
        </c:if>

        <form id="form-cadastro" action="${pageContext.request.contextPath}/cadastro-mentor"
              method="post" enctype="multipart/form-data" novalidate>

            <div class="grupo">
                <label for="nome">Digite seu nome:</label>
                <input type="text" id="nome" name="nome" maxlength="150" required autocomplete="name">
            </div>

            <div class="grupo">
                <label for="email">E-Mail:</label>
                <input type="email" id="email" name="email" maxlength="150" required autocomplete="email">
            </div>

            <div class="grupo">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required minlength="8" autocomplete="new-password">
            </div>

            <div class="grupo">
                <label for="confirmarSenha">Confirmar senha:</label>
                <input type="password" id="confirmarSenha" name="confirmarSenha" required autocomplete="new-password">
                <p class="erro-senha" id="erro-senha" role="alert">A senha deve ter pelo menos 8 caracteres e as senhas precisam coincidir.</p>
            </div>

            <div class="grupo">
                <label for="semestre">Semestre atual:</label>
                <select id="semestre" name="semestre" required>
                    <option value="" disabled selected>Selecione</option>
                    <option value="1">1º semestre</option>
                    <option value="2">2º semestre</option>
                    <option value="3">3º semestre</option>
                    <option value="4">4º semestre</option>
                    <option value="5">5º semestre</option>
                    <option value="6">6º semestre</option>
                    <option value="7">7º semestre</option>
                    <option value="8">8º semestre</option>
                </select>
                <p class="erro-senha" id="erro-semestre">Selecione seu semestre.</p>
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
    const semestre = document.getElementById('semestre');
    const erro = document.getElementById('erro-senha');
    const erroSemestre = document.getElementById('erro-semestre');
    const foto = document.getElementById('foto');
    const area = document.getElementById('upload-area');
    const previa = document.getElementById('previa');

    // Valida se as senhas coincidem antes de enviar
    form.addEventListener('submit', (e) => {
        if (senha.value.length < 8 || senha.value !== confirmar.value || !semestre.value) {
            e.preventDefault();
            erro.classList.toggle('visivel', senha.value.length < 8 || senha.value !== confirmar.value);
            erroSemestre.classList.toggle('visivel', !semestre.value);
            if (senha.value.length < 8) senha.focus();
            else if (senha.value !== confirmar.value) confirmar.focus();
            else semestre.focus();
        } else {
            erro.classList.remove('visivel');
            erroSemestre.classList.remove('visivel');
        }
    });

    semestre.addEventListener('change', () => {
        if (semestre.value) erroSemestre.classList.remove('visivel');
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