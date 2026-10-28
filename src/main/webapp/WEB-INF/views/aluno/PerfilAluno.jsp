<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Perfil do mentorando | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css"><link rel="stylesheet" href="${ctx}/css/perfil-aluno.css">
</head>
<body>
<header class="topo">
    <div class="topo-perfil">
        <c:choose>
            <c:when test="${not empty usuario.fotoUrl}"><img class="avatar" style="--tam:64px" src="${ctx}<c:out value='${usuario.fotoUrl}'/>" alt="Sua foto de perfil"></c:when>
            <c:otherwise><svg class="avatar avatar-padrao" style="--tam:64px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
        </c:choose>
        <div class="topo-info"><span class="topo-curso">${usuario.semestreAtual}º semestre ADS</span><strong class="topo-nome"><c:out value="${usuario.nome}"/></strong></div>
    </div>
    <nav class="abas" aria-label="Navegação do mentorando">
        <a class="aba" href="${ctx}/inicio-aluno">Início</a><a class="aba" href="${ctx}/perfil-aluno" aria-current="page">Perfil</a>
        <a class="aba" href="${ctx}/conta">Minha conta</a><a class="aba" href="${ctx}/logout">Sair</a>
    </nav>
    <a class="logo" href="${ctx}/inicio-aluno">SENSEI.DEV</a>
</header>
<main class="conteudo">
    <div class="perfil-topo"><h1 class="titulo-secao">Meu Perfil</h1><button type="button" id="editar-perfil" class="btn-pilula">Editar Perfil</button></div>
    <c:if test="${param.salvo == '1'}"><p class="aviso sucesso" role="status">Perfil atualizado com sucesso.</p></c:if>
    <c:if test="${param.interesses == 'salvos'}"><p class="aviso sucesso" role="status">Disciplinas de interesse atualizadas.</p></c:if>
    <c:if test="${not empty param.erro}"><p class="aviso erro" role="alert"><c:choose><c:when test="${param.erro == 'semestre'}">Escolha um semestre entre 1 e 8.</c:when><c:when test="${param.erro == 'foto'}">Escolha uma foto PNG ou JPG válida, com até 2 MB.</c:when><c:when test="${param.erro == 'disciplinas'}">Não foi possível salvar as disciplinas. Revise a seleção.</c:when><c:otherwise>Não foi possível salvar o perfil.</c:otherwise></c:choose></p></c:if>
    <div class="perfil-aluno-grid">
        <div>
            <section class="cartao resumo-aluno" aria-label="Resumo do mentorando">
                <c:choose>
                    <c:when test="${not empty usuario.fotoUrl}"><img class="avatar" style="--tam:88px" src="${ctx}<c:out value='${usuario.fotoUrl}'/>" alt="Sua foto de perfil"></c:when>
                    <c:otherwise><svg class="avatar avatar-padrao" style="--tam:88px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
                </c:choose>
                <div><span class="resumo-curso">${usuario.semestreAtual}º semestre de Análise e Desenvolvimento de Sistemas</span><strong><c:out value="${usuario.nome}"/></strong></div>
            </section>
            <h2 class="titulo-secao secao-aluno">Descrição</h2><section class="cartao descricao-aluno" aria-label="Descrição"></section>
        </div>
        <div>
            <div class="interesse-topo"><h2 class="titulo-secao secao-aluno interesse-titulo">Disciplinas de interesse</h2><button type="button" id="editar-interesses" class="btn-pilula">Editar</button></div>
            <section class="cartao interesse-aluno" aria-label="Disciplinas de interesse">
                <ul class="interesse-lista"><c:forEach var="d" items="${disciplinas}"><c:if test="${marcadas[d.id]}"><li><c:out value="${d.nome}"/></li></c:if></c:forEach></ul>
                <c:if test="${empty marcadas}"><p>Você ainda não escolheu disciplinas de interesse.</p></c:if>
            </section>
        </div>
    </div>
    <dialog id="dialog-edicao" aria-labelledby="titulo-edicao">
        <form action="${ctx}/perfil-aluno" method="post" enctype="multipart/form-data">
            <h2 id="titulo-edicao" class="titulo-secao">Editar Perfil</h2>
            <label for="semestre">Semestre atual</label><select id="semestre" name="semestre" required><c:forEach var="s" begin="1" end="8"><option value="${s}" ${usuario.semestreAtual == s ? 'selected' : ''}>${s}º semestre</option></c:forEach></select>
            <label for="foto">Nova foto de perfil</label><input type="file" id="foto" name="foto" accept="image/png,image/jpeg">
            <p class="ajuda">PNG ou JPG de até 2 MB. Deixe vazio para manter a foto atual.</p>
            <p id="erro-foto-local" class="aviso erro" role="alert" hidden>A foto deve ter até 2 MB.</p>
            <div class="dialog-acoes"><button type="button" id="cancelar-edicao" class="btn-pilula">Cancelar</button><button type="submit" class="btn-pilula btn-roxo">Salvar perfil</button></div>
        </form>
    </dialog>
    <dialog id="dialog-interesses" aria-labelledby="titulo-interesses">
        <form action="${ctx}/interesses-aluno" method="post">
            <h2 id="titulo-interesses" class="titulo-secao">Disciplinas de interesse</h2>
            <p class="ajuda">Marque as disciplinas em que você busca orientação.</p>
            <div class="opcoes-interesse"><c:forEach var="d" items="${disciplinas}"><label class="opcao-interesse"><input type="checkbox" name="disciplina" value="${d.id}" ${marcadas[d.id] ? 'checked' : ''}><span><c:out value="${d.nome}"/><small>${d.semestre}º semestre</small></span></label></c:forEach></div>
            <div class="dialog-acoes"><button type="button" id="cancelar-interesses" class="btn-pilula">Cancelar</button><button type="submit" class="btn-pilula btn-roxo">Salvar interesses</button></div>
        </form>
    </dialog>
</main>
<script>
const dialogo = document.getElementById('dialog-edicao');
document.getElementById('editar-perfil').addEventListener('click', () => dialogo.showModal());
document.getElementById('cancelar-edicao').addEventListener('click', () => dialogo.close());
const dialogoInteresses = document.getElementById('dialog-interesses');
document.getElementById('editar-interesses').addEventListener('click', () => dialogoInteresses.showModal());
document.getElementById('cancelar-interesses').addEventListener('click', () => dialogoInteresses.close());
dialogoInteresses.addEventListener('close', () => dialogoInteresses.querySelector('form').reset());
dialogo.querySelector('form').addEventListener('submit', event => {
    const foto = document.getElementById('foto').files[0];
    const erro = document.getElementById('erro-foto-local');
    if (foto && foto.size > 2 * 1024 * 1024) {
        event.preventDefault();
        erro.hidden = false;
    } else {
        erro.hidden = true;
    }
});
</script>
</body>
</html>
