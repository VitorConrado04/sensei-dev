<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header class="topo">
<div class="topo-perfil"><span class="avatar" aria-hidden="true">A</span><div class="topo-info"><span class="topo-curso">Administração</span><strong class="topo-nome"><c:out value="${sessionScope.usuarioNome}"/></strong><span class="topo-meta">Gestão da comunidade</span></div></div>
<nav class="abas" aria-label="Navegação do administrador">
<a class="aba" href="${pageContext.request.contextPath}/admin/inicio" ${param.aba == 'inicio' ? 'aria-current="page"' : ''}>Início</a>
<a class="aba" href="${pageContext.request.contextPath}/admin/usuarios" ${param.aba == 'usuarios' ? 'aria-current="page"' : ''}>Usuários</a>
<a class="aba" href="${pageContext.request.contextPath}/admin/disciplinas" ${param.aba == 'disciplinas' ? 'aria-current="page"' : ''}>Disciplinas</a>
<a class="aba" href="${pageContext.request.contextPath}/logout">Sair</a>
</nav><a class="logo" href="${pageContext.request.contextPath}/admin/inicio">SENSEI.DEV</a></header>
