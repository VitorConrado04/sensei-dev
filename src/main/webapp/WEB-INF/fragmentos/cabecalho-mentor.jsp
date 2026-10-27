<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Cabeçalho das 4 telas do mentor. Cada tela inclui este arquivo e manda
     a aba ativa por jsp:param (aqui vira ${param.aba}, igual o ${param.id} do lab 5).
     O objeto "mentor" vem do servlet. Fica em WEB-INF pra não abrir direto pela URL. --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<header class="topo">
    <div class="topo-perfil">
        <svg class="avatar avatar-padrao" style="--tam:64px" viewBox="0 0 64 64" aria-hidden="true">
            <circle cx="32" cy="32" r="29" fill="none" stroke="#fff" stroke-width="4"/>
            <circle cx="32" cy="25" r="9" fill="#fff"/>
            <path d="M13.5 50C15 40 23 35 32 35s17 5 18.5 15a29 29 0 0 1-37 0z" fill="#fff"/>
        </svg>
        <div class="topo-info">
            <%-- o curso não existe no banco ainda, por isso está fixo --%>
            <span class="topo-curso">${mentor.semestreAtual}º semestre ADS</span>
            <strong class="topo-nome"><c:out value="${mentor.nome}"/></strong>
            <span class="topo-meta">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2.8l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.6l-5.8 3.1 1.1-6.5L2.6 9.6l6.5-.9z" stroke-width="1.6" stroke-linejoin="round"/></svg>
                <fmt:formatNumber value="${mentor.notaMedia}" minFractionDigits="1" maxFractionDigits="1"/>
                <span class="ponto" aria-hidden="true"></span>
                ${mentor.totalMentorias} Sessões
            </span>
        </div>
    </div>

    <nav class="abas" aria-label="Navegação do mentor">
        <a class="aba" href="${ctx}/mentor-inicio" <c:if test="${param.aba == 'inicio'}">aria-current="page"</c:if>>Início</a>
        <a class="aba" href="${ctx}/mentor-material" <c:if test="${param.aba == 'material'}">aria-current="page"</c:if>>Material</a>
        <a class="aba" href="${ctx}/mentor-avaliacao" <c:if test="${param.aba == 'avaliacao'}">aria-current="page"</c:if>>Avaliação</a>
        <a class="aba" href="${ctx}/mentor-perfil" <c:if test="${param.aba == 'perfil'}">aria-current="page"</c:if>>Perfil</a>
        <a class="aba" href="${ctx}/logout">Sair</a>
    </nav>

    <a class="logo" href="${ctx}/mentor-inicio">SENSEI.DEV</a>
</header>