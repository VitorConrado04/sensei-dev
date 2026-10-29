<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Avaliar sessão | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css"><link rel="stylesheet" href="${ctx}/css/mentorias-aluno.css">
</head>
<body>
<jsp:include page="/WEB-INF/fragmentos/cabecalho-aluno.jsp"><jsp:param name="aba" value="mentorias"/></jsp:include>
    <div class="topo-perfil">
        <c:choose>
            <c:when test="${not empty usuario.fotoUrl}"><img class="avatar" style="--tam:64px" src="${ctx}<c:out value='${usuario.fotoUrl}'/>" alt="Sua foto de perfil"></c:when>
            <c:otherwise><svg class="avatar avatar-padrao" style="--tam:64px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
        </c:choose>
        <div class="topo-info"><span class="topo-curso"><c:out value="${usuario.semestreAtual}"/>º semestre ADS</span><strong class="topo-nome"><c:out value="${usuario.nome}"/></strong></div>
    </div>
    <nav class="abas" aria-label="Navegação do mentorando">
        <a class="aba" href="${ctx}/inicio-aluno">Início</a><a class="aba" href="${ctx}/mentorias-aluno" aria-current="page">Mentorias</a>
        <a class="aba" href="${ctx}/perfil-aluno">Perfil</a><a class="aba" href="${ctx}/conta">Minha conta</a><a class="aba" href="${ctx}/logout">Sair</a>
    </nav>
    <a class="logo" href="${ctx}/inicio-aluno">SENSEI.DEV</a>
</header>

<main class="conteudo minhas-mentorias">
    <h1 class="mentorias-titulo">Avaliar sessão</h1>
    <p class="mentoria-mentor">
        <c:out value="${mentoria.disciplina}"/> com <c:out value="${mentoria.mentorNome}"/>,
        em <c:out value="${mentoria.dataTexto}"/> às <c:out value="${mentoria.horaTexto}"/>.
    </p>
    <c:if test="${param.erro == 'notas'}"><p class="mentoria-aviso erro" role="alert">Escolha uma nota de 1 a 5 em todos os critérios.</p></c:if>

    <form class="avaliacao-form" action="${ctx}/avaliar-sessao" method="post">
        <input type="hidden" name="token" value="${tokenMentoria}">
        <input type="hidden" name="id" value="${mentoria.id}">

        <fieldset class="criterio"><legend>Clareza da explicação</legend>
            <div class="notas"><c:forEach var="n" begin="1" end="5"><label><input type="radio" name="clareza" value="${n}" required><span>${n}</span></label></c:forEach></div>
        </fieldset>
        <fieldset class="criterio"><legend>Domínio do assunto</legend>
            <div class="notas"><c:forEach var="n" begin="1" end="5"><label><input type="radio" name="dominio" value="${n}" required><span>${n}</span></label></c:forEach></div>
        </fieldset>
        <fieldset class="criterio"><legend>Pontualidade</legend>
            <div class="notas"><c:forEach var="n" begin="1" end="5"><label><input type="radio" name="pontualidade" value="${n}" required><span>${n}</span></label></c:forEach></div>
        </fieldset>
        <fieldset class="criterio"><legend>Capacidade de orientar</legend>
            <div class="notas"><c:forEach var="n" begin="1" end="5"><label><input type="radio" name="orientar" value="${n}" required><span>${n}</span></label></c:forEach></div>
        </fieldset>
        <fieldset class="criterio"><legend>Experiência geral</legend>
            <div class="notas"><c:forEach var="n" begin="1" end="5"><label><input type="radio" name="geral" value="${n}" required><span>${n}</span></label></c:forEach></div>
        </fieldset>

        <label for="comentario">Comentário (opcional)</label>
        <textarea id="comentario" name="comentario" class="campo-texto" maxlength="1000" rows="4"></textarea>

        <div class="mentoria-acoes">
            <a class="mentoria-acao" href="${ctx}/mentorias-aluno#realizadas">Voltar</a>
            <button class="mentoria-acao" type="submit">Enviar avaliação</button>
        </div>
    </form>
</main>
</body>
</html>