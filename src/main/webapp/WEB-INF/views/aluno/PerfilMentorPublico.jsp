<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Perfil de <c:out value="${mentor.nome}"/> | SENSEI.DEV</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/mentor.css"><link rel="stylesheet" href="${ctx}/css/perfil-mentor-publico.css">
</head>
<body>
<header class="topo">
    <div class="topo-perfil">
        <c:choose>
            <c:when test="${not empty aluno.fotoUrl}"><img class="avatar" style="--tam:64px" src="${ctx}<c:out value='${aluno.fotoUrl}'/>" alt="Sua foto de perfil"></c:when>
            <c:otherwise><svg class="avatar avatar-padrao" style="--tam:64px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
        </c:choose>
        <div class="topo-info"><span class="topo-curso"><c:out value="${aluno.semestreAtual}"/>º semestre ADS</span><strong class="topo-nome"><c:out value="${aluno.nome}"/></strong></div>
    </div>
    <nav class="abas" aria-label="Navegação do mentorando">
        <a class="aba" href="${ctx}/inicio-aluno">Início</a><a class="aba" href="${ctx}/perfil-aluno">Perfil</a>
        <a class="aba" href="${ctx}/conta">Minha conta</a><a class="aba" href="${ctx}/logout">Sair</a>
    </nav>
    <a class="logo" href="${ctx}/inicio-aluno">SENSEI.DEV</a>
</header>
<main class="conteudo">
    <a class="voltar-busca" href="${ctx}/inicio-aluno">← Voltar à busca</a>
    <h1 class="titulo-secao titulo-perfil-publico">Perfil do mentor</h1>
    <form class="perfil-favorito" action="${ctx}/favoritos-aluno" method="post">
        <input type="hidden" name="token" value="${tokenFavorito}">
        <input type="hidden" name="mentor" value="${mentor.id}">
        <input type="hidden" name="retorno" value="perfil">
        <input type="hidden" name="acao" value="${favorito ? 'desfavoritar' : 'favoritar'}">
        <button type="submit" class="btn-pilula">${favorito ? '★ Remover dos favoritos' : '☆ Salvar nos favoritos'}</button>
    </form>
    <div class="perfil perfil-publico">
        <div>
            <section class="cartao cartao-perfil" aria-label="Resumo do mentor">
                <c:choose>
                    <c:when test="${not empty mentor.fotoUrl}"><img class="avatar" style="--tam:88px" src="${ctx}<c:out value='${mentor.fotoUrl}'/>" alt="Foto de <c:out value='${mentor.nome}'/>"></c:when>
                    <c:otherwise><svg class="avatar avatar-padrao" style="--tam:88px" viewBox="0 0 64 64" aria-hidden="true"><circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/><circle cx="32" cy="25" r="9" fill="#fff"/><path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/></svg></c:otherwise>
                </c:choose>
                <div class="resumo-mentor"><span id="perfil-curso"><c:choose><c:when test="${not empty mentor.semestreAtual}">${mentor.semestreAtual}º semestre de Análise e Desenvolvimento de Sistemas</c:when><c:otherwise>Semestre não informado</c:otherwise></c:choose></span>
                    <strong id="perfil-nome"><c:out value="${mentor.nome}"/></strong>
                    <span id="perfil-meta"><c:choose><c:when test="${mentor.totalAvaliacoes > 0}">★ <fmt:formatNumber value="${mentor.notaMedia}" minFractionDigits="1" maxFractionDigits="1"/> · ${mentor.totalAvaliacoes} ${mentor.totalAvaliacoes == 1 ? 'avaliação' : 'avaliações'}</c:when><c:otherwise>Sem avaliações</c:otherwise></c:choose> · ${mentor.totalMentorias} ${mentor.totalMentorias == 1 ? 'sessão realizada' : 'sessões realizadas'}</span>
                </div>
            </section>
            <h2 class="titulo-secao">Descrição</h2>
            <section class="cartao" aria-label="Descrição"><p class="perfil-descricao"><c:choose><c:when test="${not empty mentor.descricao}"><c:out value="${mentor.descricao}"/></c:when><c:otherwise>O mentor ainda não adicionou uma descrição.</c:otherwise></c:choose></p></section>
            <h2 class="titulo-secao">Experiência</h2>
            <section class="cartao" aria-label="Experiência"><p class="perfil-descricao"><c:choose><c:when test="${not empty mentor.experiencia}"><c:out value="${mentor.experiencia}"/></c:when><c:otherwise>O mentor ainda não adicionou informações sobre sua experiência.</c:otherwise></c:choose></p></section>
            <h2 class="titulo-secao">Disciplinas</h2>
            <section class="cartao" aria-label="Disciplinas que domina">
                <c:choose><c:when test="${empty disciplinas}"><p>O mentor ainda não cadastrou disciplinas.</p></c:when><c:otherwise><ul class="lista-disciplinas"><c:forEach var="d" items="${disciplinas}"><li><c:out value="${d.nome}"/> <small>· ${d.semestre}º semestre</small></li></c:forEach></ul></c:otherwise></c:choose>
            </section>
            <h2 class="titulo-secao">Avaliações</h2>
            <section aria-label="Avaliações recebidas">
                <c:choose><c:when test="${empty avaliacoes}"><p class="vazio">Este mentor ainda não recebeu avaliações.</p></c:when>
                    <c:otherwise><ul class="avaliacoes-publicas"><c:forEach var="avaliacao" items="${avaliacoes}"><li class="cartao avaliacao-publica"><div class="avaliacao-publica-topo"><strong><c:out value="${avaliacao.disciplina}"/></strong><span>★ <fmt:formatNumber value="${avaliacao.nota}" minFractionDigits="1" maxFractionDigits="1"/></span></div><small><c:out value="${avaliacao.dataTexto}"/></small><c:if test="${not empty avaliacao.comentario}"><p><c:out value="${avaliacao.comentario}"/></p></c:if></li></c:forEach></ul></c:otherwise>
                </c:choose>
            </section>
        </div>
        <section class="disponibilidade-publica" aria-labelledby="titulo-disponibilidade">
            <h2 id="titulo-disponibilidade" class="grade-titulo">DISPONIBILIDADE SEMANAL</h2>
            <c:choose><c:when test="${not temDisponibilidade}"><p class="vazio">O mentor ainda não cadastrou horários disponíveis.</p></c:when>
                <c:otherwise><div class="rolagem"><table class="grade"><thead><tr><th scope="col">Horários</th><th scope="col">Segunda</th><th scope="col">Terça</th><th scope="col">Quarta</th><th scope="col">Quinta</th><th scope="col">Sexta</th><th scope="col">Sábado</th></tr></thead><tbody><c:forEach var="linha" items="${grade}"><tr><th scope="row">${linha.hora}:00</th><c:forEach var="estado" items="${linha.dias}"><td class="cel"><span class="${estado == 'D' ? 'cel-d' : 'cel-x'}">${estado == 'D' ? 'Disponível' : '—'}</span></td></c:forEach></tr></c:forEach></tbody></table></div><p class="nota-disponibilidade">Horários semanais cadastrados pelo mentor. A solicitação ficará pendente até a confirmação dele.</p></c:otherwise>
            </c:choose>
        </section>
    </div>
    <section id="solicitar-mentoria" class="solicitar-mentoria cartao" aria-labelledby="titulo-solicitar">
        <h2 id="titulo-solicitar" class="titulo-secao">Solicitar mentoria</h2>
        <p>Escolha uma data e um horário cadastrado pelo mentor. Seu pedido ficará pendente até a confirmação dele.</p>
        <c:if test="${param.solicitacao == 'enviada'}"><p class="solicitacao-sucesso" role="status">Solicitação enviada! Aguarde a confirmação do mentor.</p></c:if>
        <c:if test="${param.erro == 'objetivo'}"><p class="solicitacao-erro" role="alert">Informe o objetivo da mentoria em até 500 caracteres.</p></c:if>
        <c:if test="${param.erro == 'horario'}"><p class="solicitacao-erro" role="alert">Não foi possível solicitar nesse horário. Confira os dados e escolha novamente.</p></c:if>
        <c:if test="${dataInvalida}"><p class="solicitacao-erro" role="alert">Escolha uma data válida.</p></c:if>
        <form class="selecionar-data" action="${ctx}/mentor-publico#solicitar-mentoria" method="get">
            <input type="hidden" name="id" value="${mentor.id}">
            <label for="data-mentoria">Data</label>
            <input id="data-mentoria" name="data" type="date" min="${dataMinima}" value="${dataSelecionada}" required>
            <label for="duracao-mentoria">Duração</label>
            <select id="duracao-mentoria" name="duracao">
                <option value="60" ${duracaoSelecionada == 60 ? 'selected' : ''}>1 hora</option>
                <option value="120" ${duracaoSelecionada == 120 ? 'selected' : ''}>2 horas</option>
            </select>
            <button class="btn-pilula" type="submit">Ver horários</button>
        </form>
        <c:if test="${not empty dataSelecionada}">
            <c:choose>
                <c:when test="${empty horariosMentoria}"><p class="vazio">Não há horários cadastrados para essa data e duração.</p></c:when>
                <c:otherwise>
                    <form class="enviar-solicitacao" action="${ctx}/solicitar-mentoria" method="post">
                        <input type="hidden" name="token" value="${tokenMentoria}">
                        <input type="hidden" name="mentor" value="${mentor.id}">
                        <input type="hidden" name="data" value="${dataSelecionada}">
                        <input type="hidden" name="duracao" value="${duracaoSelecionada}">
                        <label for="disciplina-mentoria">Disciplina</label>
                        <select id="disciplina-mentoria" name="disciplina" required>
                            <option value="">Selecione a disciplina</option>
                            <c:forEach var="d" items="${disciplinas}"><option value="${d.id}"><c:out value="${d.nome}"/></option></c:forEach>
                        </select>
                        <label for="hora-mentoria">Horário de início</label>
                        <select id="hora-mentoria" name="hora" required>
                            <c:forEach var="hora" items="${horariosMentoria}"><option value="${hora}">${hora}</option></c:forEach>
                        </select>
                        <label for="objetivo-mentoria">O que você deseja aprender nesta sessão?</label>
                        <textarea id="objetivo-mentoria" name="objetivo" maxlength="500" rows="4" required placeholder="Ex.: Tirar dúvidas sobre banco de dados"></textarea>
                        <button class="btn-pilula" type="submit">Enviar solicitação</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </c:if>
    </section>
</main>
</body>
</html>
