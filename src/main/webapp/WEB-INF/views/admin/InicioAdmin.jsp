<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Administração | SENSEI.DEV</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css"></head><body>
<jsp:include page="/WEB-INF/fragmentos/cabecalho-admin.jsp"><jsp:param name="aba" value="inicio"/></jsp:include>
<main class="conteudo admin-conteudo">
<span class="admin-legenda">PAINEL DO ADMINISTRADOR</span><h1>Olá, <c:out value="${sessionScope.usuarioNome}"/>!</h1><p>Cuide da comunidade e organize o conhecimento que conecta nossos alunos.</p>
<section class="admin-cards" aria-label="Gerenciamento da plataforma">
<a class="admin-card" href="${pageContext.request.contextPath}/admin/usuarios"><span class="admin-icone" aria-hidden="true">◎</span><h2>Usuários</h2><p>Gerencie mentores e mentorandos, edite perfis e acompanhe o acesso à plataforma.</p><strong>Gerenciar usuários ↗</strong></a>
<a class="admin-card" href="${pageContext.request.contextPath}/admin/disciplinas"><span class="admin-icone" aria-hidden="true">▤</span><h2>Disciplinas</h2><p>Organize as disciplinas por semestre e mantenha o catálogo de conhecimento atualizado.</p><strong>Gerenciar disciplinas ↗</strong></a>
</section><aside class="admin-nota"><span class="admin-legenda">NOSSA COMUNIDADE</span><h2>Conhecimento que se compartilha.</h2><p>Cada perfil e cada disciplina abrem espaço para novas conexões entre quem quer aprender e quem pode orientar.</p></aside>
</main></body></html>
