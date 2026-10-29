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
    <link rel="stylesheet" href="${ctx}/css/mentor.css?v=p2-08"><link rel="stylesheet" href="${ctx}/css/mentorias-aluno.css?v=p3-10">
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
        <jsp:include page="/WEB-INF/fragmentos/link-notificacoes.jsp"/>
        <a class="aba" href="${ctx}/perfil-aluno">Perfil</a><a class="aba" href="${ctx}/conta">Minha conta</a><a class="aba" href="${ctx}/logout">Sair</a>
    </nav>
    <a class="logo" href="${ctx}/inicio-aluno">SENSEI.DEV</a>
</header>
<main class="conteudo minhas-mentorias">
    <h1 class="mentorias-titulo">Minhas Mentorias</h1>
    <c:if test="${param.resultado == 'cancelada'}"><p class="mentoria-aviso sucesso" role="status">Mentoria cancelada.</p></c:if>
    <c:if test="${param.resultado == 'reagendada'}"><p class="mentoria-aviso sucesso" role="status">Novo horário solicitado. Aguarde a confirmação do mentor.</p></c:if>
    <c:if test="${param.resultado == 'indisponivel'}"><p class="mentoria-aviso erro" role="alert">Não foi possível alterar a mentoria. Verifique se ela ainda está disponível.</p></c:if>
    <c:if test="${param.resultado == 'avaliada'}"><p class="mentoria-aviso sucesso" role="status">Avaliação enviada. Obrigado!</p></c:if>
    <c:if test="${param.resultado == 'avaliacao-indisponivel'}"><p class="mentoria-aviso erro" role="alert">Não foi possível avaliar esta sessão. Ela pode já ter sido avaliada.</p></c:if>
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
                        <article id="mentoria-${mentoria.id}" class="mentoria-card">
                            <h3><c:out value="${mentoria.disciplina}"/></h3>
                            <p class="mentoria-objetivo"><c:out value="${mentoria.objetivo}"/></p>
                            <p class="mentoria-mentor">Com <c:choose><c:when test="${mentoria.mentorAtivo}"><c:url var="urlMentor" value="/mentor-publico"><c:param name="id" value="${mentoria.mentorId}"/></c:url><a href="${urlMentor}"><c:out value="${mentoria.mentorNome}"/></a></c:when><c:otherwise><c:out value="${mentoria.mentorNome}"/></c:otherwise></c:choose></p>
                            <div class="mentoria-detalhes"><span><c:out value="${mentoria.dataTexto}"/></span><span><c:out value="${mentoria.horaTexto}"/></span><span><c:choose><c:when test="${mentoria.duracaoMinutos == 60}">1 hora</c:when><c:when test="${mentoria.duracaoMinutos == 120}">2 horas</c:when><c:otherwise><c:out value="${mentoria.duracaoMinutos}"/> min</c:otherwise></c:choose></span></div>
                            <div class="mentoria-estado"><span>Status:</span><strong class="estado estado-${mentoria.status}"><c:choose><c:when test="${mentoria.status == 'PENDENTE'}">Pendente</c:when><c:when test="${mentoria.status == 'CONFIRMADA'}">Confirmada</c:when><c:when test="${mentoria.status == 'REALIZADA'}">Realizada</c:when><c:when test="${mentoria.status == 'CANCELADA'}">Cancelada</c:when><c:otherwise><c:out value="${mentoria.status}"/></c:otherwise></c:choose></strong></div>

                            <%-- materiais que o mentor vinculou a esta mentoria --%>
                            <c:set var="materiais" value="${materiaisPorMentoria[mentoria.id]}"/>
                            <c:if test="${not empty materiais}">
                                <div class="mentoria-materiais">
                                    <h4>Materiais do mentor</h4>
                                    <ul>
                                        <c:forEach var="material" items="${materiais}">
                                            <li>
                                                <a href="<c:out value='${material.url}'/>" target="_blank" rel="noopener noreferrer"><c:out value="${material.titulo}"/></a>
                                                <c:if test="${not empty material.descricao}"><small><c:out value="${material.descricao}"/></small></c:if>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>

                            <c:if test="${mentoria.status == 'REALIZADA'}">
                                <div class="mentoria-acoes">
                                    <c:choose>
                                        <c:when test="${mentoria.nota != null}">
                                            <span>Sua avaliação: <strong><c:out value="${mentoria.nota}"/></strong></span>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="mentoria-acao" href="${ctx}/avaliar-sessao?id=${mentoria.id}">Avaliar sessão</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>

                            <c:if test="${mentoria.podeAlterar}">
                                <div class="mentoria-acoes">
                                    <a class="mentoria-acao reagendar" href="${ctx}/mentorias-aluno?reagendar=${mentoria.id}#mentoria-${mentoria.id}">Reagendar</a>
                                    <form action="${ctx}/mentorias-aluno" method="post">
                                        <input type="hidden" name="token" value="${tokenMentoria}">
                                        <input type="hidden" name="id" value="${mentoria.id}">
                                        <input type="hidden" name="acao" value="cancelar">
                                        <button class="mentoria-acao cancelar" type="submit">Cancelar mentoria</button>
                                    </form>
                                </div>
                            </c:if>
                            <c:if test="${not empty mentoriaSelecionada and mentoriaSelecionada.id == mentoria.id}">
                                <div class="reagendar-mentoria">
                                    <h4>Escolher outro horário</h4>
                                    <p>A nova solicitação ficará pendente de confirmação.</p>
                                    <form class="reagendar-data" action="${ctx}/mentorias-aluno#mentoria-${mentoria.id}" method="get">
                                        <input type="hidden" name="reagendar" value="${mentoria.id}">
                                        <label for="data-${mentoria.id}">Data</label>
                                        <input id="data-${mentoria.id}" name="data" type="date" min="${dataMinima}" value="${dataSelecionada}" required>
                                        <label for="duracao-${mentoria.id}">Duração</label>
                                        <select id="duracao-${mentoria.id}" name="duracao">
                                            <option value="60" ${duracaoSelecionada == 60 ? 'selected' : ''}>1 hora</option>
                                            <option value="120" ${duracaoSelecionada == 120 ? 'selected' : ''}>2 horas</option>
                                        </select>
                                        <button class="mentoria-acao" type="submit">Ver horários</button>
                                    </form>
                                    <c:if test="${dataInvalida}"><p class="mentoria-aviso erro" role="alert">Escolha uma data válida.</p></c:if>
                                    <c:if test="${not empty dataSelecionada}">
                                        <c:choose>
                                            <c:when test="${empty horariosReagendamento}"><p class="vazio">Não há horários livres nessa data.</p></c:when>
                                            <c:otherwise>
                                                <form class="reagendar-enviar" action="${ctx}/mentorias-aluno" method="post">
                                                    <input type="hidden" name="token" value="${tokenMentoria}">
                                                    <input type="hidden" name="id" value="${mentoria.id}">
                                                    <input type="hidden" name="acao" value="reagendar">
                                                    <input type="hidden" name="data" value="${dataSelecionada}">
                                                    <input type="hidden" name="duracao" value="${duracaoSelecionada}">
                                                    <label for="hora-${mentoria.id}">Horário</label>
                                                    <select id="hora-${mentoria.id}" name="hora" required><c:forEach var="hora" items="${horariosReagendamento}"><option value="${hora}">${hora}</option></c:forEach></select>
                                                    <button class="mentoria-acao" type="submit">Solicitar novo horário</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </div>
                            </c:if>
                        </article>
                    </c:forEach></div></c:otherwise>
                </c:choose>
            </section>
        </c:if>
    </c:forEach>
</main>
</body>
</html>