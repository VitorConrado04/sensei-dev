<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Perfil do mentor. O MentorPerfilServlet (/mentor-perfil) manda: mentor, disciplinas e grade.
     É um único formulário. Cada elemento de edição tem a classe "so-edicao" e cada elemento
     de leitura a classe "so-leitura"; o js/mentor.js só liga/desliga a classe "editando"
     no form e o CSS decide o que aparece. Ao salvar, vai tudo junto num POST. --%>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Perfil | SENSEI.DEV</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600&family=Bruno+Ace&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/mentor.css">
</head>
<body>

<jsp:include page="/WEB-INF/fragmentos/cabecalho-mentor.jsp">
    <jsp:param name="aba" value="perfil"/>
</jsp:include>

<main class="conteudo">
    <c:if test="${param.salvo == '1'}"><p class="subtitulo" role="status">Perfil atualizado com sucesso.</p></c:if>
    <form id="form-perfil" class="perfil" action="${ctx}/mentor-perfil" method="post">

        <div>
            <div class="perfil-topo">
                <h1 class="titulo-secao">Meu Perfil</h1>
                <div class="botoes">
                    <button type="button" id="btn-editar" class="btn-pilula so-leitura">Editar Perfil</button>
                    <button type="button" id="btn-cancelar-edicao" class="btn-pilula so-edicao">Cancelar</button>
                    <button type="submit" class="btn-pilula btn-roxo so-edicao">Salvar perfil</button>
                </div>
            </div>

            <section class="cartao cartao-perfil" aria-label="Resumo do mentor">
                <c:choose>
                    <c:when test="${not empty mentor.fotoUrl}">
                        <img class="avatar" style="--tam:88px" src="${ctx}${mentor.fotoUrl}" alt="Foto de perfil do mentor"/>
                    </c:when>
                    <c:otherwise>
                        <svg class="avatar avatar-padrao" style="--tam:88px" viewBox="0 0 64 64" aria-hidden="true">
                            <circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/>
                            <circle cx="32" cy="25" r="9" fill="#fff"/>
                            <path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/>
                        </svg>
                    </c:otherwise>
                </c:choose>
                <div>
                    <span id="perfil-curso">${mentor.semestreAtual}º semestre de Análise e Desenvolvimento de Sistemas</span>
                    <strong id="perfil-nome"><c:out value="${mentor.nome}"/></strong>
                    <span id="perfil-meta">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2.8l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.6l-5.8 3.1 1.1-6.5L2.6 9.6l6.5-.9z" stroke-width="1.6" stroke-linejoin="round"/></svg>
                        <fmt:formatNumber value="${mentor.notaMedia}" minFractionDigits="1" maxFractionDigits="1"/>
                        <span class="ponto" aria-hidden="true"></span>
                        ${mentor.totalMentorias} Sessões Realizadas
                    </span>
                </div>
            </section>

            <h2 class="titulo-secao">Descrição</h2>
            <section class="cartao" aria-label="Descrição">
                <p class="perfil-descricao so-leitura"><c:out value="${mentor.descricao}"/></p>
                <textarea name="descricao" class="campo-texto so-edicao" maxlength="1000" rows="5" aria-label="Descrição do perfil"><c:out value="${mentor.descricao}"/></textarea>
            </section>

            <h2 class="titulo-secao">Disciplinas</h2>
            <section class="cartao" aria-label="Disciplinas que domina">
                <ul class="lista-disciplinas">
                    <c:forEach var="d" items="${disciplinas}">
                        <li><c:out value="${d}"/></li>
                    </c:forEach>
                </ul>
            </section>
        </div>

        <section aria-labelledby="t-disponibilidade">
            <h2 id="t-disponibilidade" class="grade-titulo">DISPONIBILIDADE</h2>
            <div class="rolagem">
                <table class="grade">
                    <thead>
                    <tr>
                        <th scope="col">Horários</th>
                        <th scope="col">Segunda</th>
                        <th scope="col">Terça</th>
                        <th scope="col">Quarta</th>
                        <th scope="col">Quinta</th>
                        <th scope="col">Sexta</th>
                        <th scope="col">Sábado</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="linha" items="${grade}">
                        <tr>
                            <th scope="row">${linha.hora}:00</th>
                            <%-- col.index é a posição do dia (0 = segunda ... 5 = sábado) --%>
                            <c:forEach var="estado" items="${linha.dias}" varStatus="col">
                                <c:choose>
                                    <%-- A = já tem sessão marcada, não dá pra mexer --%>
                                    <c:when test="${estado == 'A'}">
                                        <td class="cel cel-agendado" aria-label="1 sessão agendada">1</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="cel">
                                            <%-- modo leitura: só o texto --%>
                                            <span class="so-leitura ${estado == 'D' ? 'cel-d' : 'cel-x'}">${estado == 'D' ? 'Disponível' : 'X'}</span>

                                            <%-- modo edição: checkbox "disponível". Os marcados chegam no servlet
                                                 como "disp" com valor hora-dia, ex: 9-2 --%>
                                            <label class="celula-toggle so-edicao">
                                                <input type="checkbox" name="disp" value="${linha.hora}-${col.index}" ${estado == 'D' ? 'checked' : ''}>
                                                <span class="cel-botao"><span class="txt-d">Disponível</span><span class="txt-x">X</span></span>
                                            </label>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>

    </form>
</main>

<script src="${ctx}/js/mentor.js"></script>
</body>
</html>