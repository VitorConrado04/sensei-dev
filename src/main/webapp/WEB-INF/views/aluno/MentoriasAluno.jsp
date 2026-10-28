<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Minhas mentorias | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css?v=p2-08"><link rel="stylesheet" href="${ctx}/css/mentorias-aluno.css?v=3">
</head>
<body>
<header class="topo topo-mentorando">
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
    <h1 class="mentorias-titulo">Minhas Mentorias</h1>
    <nav class="atalhos-mentorias" aria-label="Seções das mentorias">
        <a href="#futuras">Próximas</a><a href="#realizadas">Realizadas</a><a href="#canceladas">Canceladas</a>
    </nav>
    <c:forEach var="grupo" items="${grupos}">
        <c:if test="${grupo.id != 'aguardando' or not empty grupo.mentorias}">
            <section id="${grupo.id}" class="grupo-mentorias" aria-labelledby="titulo-${grupo.id}">
                <h2 id="titulo-${grupo.id}" class="titulo-secao"><c:out value="${grupo.titulo}"/> <small>(<c:out value="${grupo.mentorias.size()}"/>)</small></h2>
                <c:choose>
                    <c:when test="${empty grupo.mentorias}"><p class="vazio"><c:out value="${grupo.mensagemVazia}"/></p></c:when>
                    <c:otherwise><div class="mentorias-lista"><c:forEach var="mentoria" items="${grupo.mentorias}">
                        <article class="mentoria-card">
                            <h3><c:out value="${mentoria.disciplina}"/></h3>
                            <p class="mentoria-objetivo"><c:out value="${mentoria.objetivo}"/></p>
                            <p class="mentoria-mentor">Com <c:choose><c:when test="${mentoria.mentorAtivo}"><c:url var="urlMentor" value="/mentor-publico"><c:param name="id" value="${mentoria.mentorId}"/></c:url><a href="${urlMentor}"><c:out value="${mentoria.mentorNome}"/></a></c:when><c:otherwise><c:out value="${mentoria.mentorNome}"/></c:otherwise></c:choose></p>
                            <div class="mentoria-detalhes"><span><c:out value="${mentoria.dataTexto}"/></span><span><c:out value="${mentoria.horaTexto}"/></span><span><c:choose><c:when test="${mentoria.duracaoMinutos == 60}">1 hora</c:when><c:when test="${mentoria.duracaoMinutos == 120}">2 horas</c:when><c:otherwise><c:out value="${mentoria.duracaoMinutos}"/> min</c:otherwise></c:choose></span></div>
                            <div class="mentoria-estado"><span>Status:</span><strong class="estado estado-${mentoria.status}"><c:choose><c:when test="${mentoria.status == 'PENDENTE'}">Pendente</c:when><c:when test="${mentoria.status == 'CONFIRMADA'}">Confirmada</c:when><c:when test="${mentoria.status == 'REALIZADA'}">Realizada</c:when><c:when test="${mentoria.status == 'CANCELADA'}">Cancelada</c:when><c:otherwise><c:out value="${mentoria.status}"/></c:otherwise></c:choose></strong></div>
                        </article>
                    </c:forEach></div></c:otherwise>
                </c:choose>
            </section>
        </c:if>
    </c:forEach>
</main>
</body>
</html>
