<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Encontre seu mentor | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css"><link rel="stylesheet" href="${ctx}/css/inicio-aluno.css">
</head>
<body>
<header class="topo">
    <div class="topo-perfil">
        <c:choose>
            <c:when test="${not empty usuario.fotoUrl}"><img class="avatar" style="--tam:64px" src="${ctx}<c:out value='${usuario.fotoUrl}'/>" alt="Sua foto de perfil"></c:when>
            <c:otherwise><svg class="avatar avatar-padrao" style="--tam:64px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
        </c:choose>
        <div class="topo-info"><span class="topo-curso"><c:out value="${usuario.semestreAtual}"/>º semestre ADS</span><strong class="topo-nome"><c:out value="${usuario.nome}"/></strong></div>
    </div>
    <nav class="abas" aria-label="Navegação do mentorando">
        <a class="aba" href="${ctx}/inicio-aluno" aria-current="page">Início</a><a class="aba" href="${ctx}/perfil-aluno">Perfil</a>
        <a class="aba" href="${ctx}/conta">Minha conta</a><a class="aba" href="${ctx}/logout">Sair</a>
    </nav>
    <a class="logo" href="${ctx}/inicio-aluno">SENSEI.DEV</a>
</header>
<main class="conteudo busca-layout">
    <form class="filtros" action="${ctx}/inicio-aluno" method="get" aria-labelledby="titulo-busca">
        <h1 id="titulo-busca" class="titulo-secao">Encontre seu Mentor</h1>
        <p>Escolha sua matéria:</p>
        <label class="campo-semestre" for="semestre">Semestre:
            <select id="semestre" name="semestre">
                <option value="">Todos</option>
                <c:forEach var="s" begin="1" end="8"><option value="${s}" ${semestre == s ? 'selected' : ''}>${s}º semestre</option></c:forEach>
            </select>
        </label>
        <fieldset class="materias"><legend class="so-leitor">Disciplinas</legend>
            <c:forEach var="d" items="${disciplinas}">
                <label class="materia"><input type="checkbox" name="disciplina" value="${d.id}" ${marcadas[d.id] ? 'checked' : ''}><span><c:out value="${d.nome}"/></span></label>
            </c:forEach>
        </fieldset>
        <label class="campo-nota" for="nota">Avaliação mínima:
            <select id="nota" name="nota">
                <option value="">Qualquer nota</option>
                <c:forEach var="n" begin="1" end="5"><option value="${n}" ${notaMinima == n ? 'selected' : ''}>${n} estrela(s) ou mais</option></c:forEach>
            </select>
        </label>
        <div class="acoes-busca"><button type="submit" class="btn-pilula btn-busca">Buscar Mentores</button><a href="${ctx}/inicio-aluno">Limpar filtros</a></div>
    </form>
    <section class="resultados" aria-labelledby="titulo-resultados">
        <h2 id="titulo-resultados" class="titulo-secao">Mentores disponíveis</h2>
        <p class="resultado-contagem"><c:out value="${mentores.size()}"/> ${mentores.size() == 1 ? 'mentor exibido' : 'mentores exibidos'}</p>
        <c:if test="${not empty mentorSelecionado}">
            <section id="mentor-selecionado" class="mentor-selecionado" aria-labelledby="titulo-selecionado">
                <span class="etiqueta-selecionado">Mentor selecionado</span>
                <h3 id="titulo-selecionado"><c:out value="${mentorSelecionado.nome}"/></h3>
                <c:url var="perfilSelecionado" value="/mentor-publico"><c:param name="id" value="${mentorSelecionado.id}"/></c:url>
                <a class="mentor-acao mentor-acao-perfil" href="${perfilSelecionado}">Ver perfil completo</a>
            </section>
        </c:if>
        <c:choose>
            <c:when test="${empty mentores}"><p class="vazio">Nenhum mentor encontrado com esses filtros. Tente outra disciplina, semestre ou nota.</p></c:when>
            <c:otherwise><div class="lista-mentores"><c:forEach var="mentor" items="${mentores}">
                <article class="mentor-card ${mentorSelecionado.id == mentor.id ? 'mentor-card-selecionado' : ''}">
                    <div class="mentor-card-topo"><span class="mentor-avatar" aria-hidden="true"><c:out value="${mentor.nome.substring(0,1)}"/></span><h3><c:out value="${mentor.nome}"/></h3>
                        <span class="mentor-nota"><c:choose><c:when test="${mentor.totalAvaliacoes > 0}">★ <fmt:formatNumber value="${mentor.notaMedia}" minFractionDigits="1" maxFractionDigits="1"/> <small>(<c:out value="${mentor.totalAvaliacoes}"/> ${mentor.totalAvaliacoes == 1 ? 'avaliação' : 'avaliações'})</small></c:when><c:otherwise>Sem avaliações</c:otherwise></c:choose></span>
                    </div>
                    <c:choose><c:when test="${not empty mentor.experiencia}"><p><c:out value="${mentor.experiencia}"/></p></c:when><c:when test="${not empty mentor.descricao}"><p><c:out value="${mentor.descricao}"/></p></c:when><c:otherwise><p>Este mentor ainda não adicionou uma descrição.</p></c:otherwise></c:choose>
                    <c:choose>
                        <c:when test="${mentorSelecionado.id == mentor.id}"><span class="mentor-acao mentor-acao-atual">Selecionado</span></c:when>
                        <c:otherwise>
                            <c:url var="urlSelecionar" value="/inicio-aluno">
                                <c:if test="${not empty semestre}"><c:param name="semestre" value="${semestre}"/></c:if>
                                <c:forEach var="disciplinaId" items="${selecionadas}"><c:param name="disciplina" value="${disciplinaId}"/></c:forEach>
                                <c:if test="${not empty notaMinima}"><c:param name="nota" value="${notaMinima}"/></c:if>
                                <c:param name="mentor" value="${mentor.id}"/>
                            </c:url>
                            <a class="mentor-acao" href="${urlSelecionar}#mentor-selecionado">Selecionar mentor</a>
                        </c:otherwise>
                    </c:choose>
                    <c:url var="urlPerfil" value="/mentor-publico"><c:param name="id" value="${mentor.id}"/></c:url>
                    <a class="mentor-acao mentor-acao-perfil" href="${urlPerfil}">Ver perfil</a>
                </article>
            </c:forEach></div></c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
