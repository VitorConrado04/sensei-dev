<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<a class="aba" href="${pageContext.request.contextPath}/notificacoes"
   <c:if test="${param.atual == 'sim'}">aria-current="page"</c:if>>Notificações<c:if test="${notificacoesNaoLidas > 0}"><span class="selo-notif">${notificacoesNaoLidas}</span></c:if></a>