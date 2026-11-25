<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="pt_BR" scope="request"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:useBean id="agora" class="java.util.Date"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Conecte-se a outros alunos, compartilhe conhecimento e evolua com mentorias no Sensei.dev.">
    <title>SENSEI.DEV | Aprenda com quem já percorreu o caminho</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Asap:wght@400;500;600;700&family=Bruno+Ace&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/inicio-publico.css?v=1">
</head>
<body>
<header class="cabecalho-publico">
    <a class="marca" href="${ctx}/" aria-label="Sensei.dev - página inicial">SENSEI<span>.DEV</span></a>

    <nav class="navegacao-publica" aria-label="Navegação principal">
        <a href="#numeros">Comunidade</a>
        <a href="#depoimentos">Depoimentos</a>
        <a href="#participar">Como participar</a>
    </nav>

    <div class="acoes-cabecalho">
        <c:choose>
            <c:when test="${not empty urlPainel}">
                <a class="botao botao-contorno" href="${urlPainel}">Meu painel</a>
            </c:when>
            <c:otherwise>
                <a class="link-entrar" href="${ctx}/LoginArea.jsp">Entrar</a>
                <a class="botao botao-pequeno" href="${ctx}/CadastroAluno.jsp">Cadastrar-se</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<main>
    <section class="hero">
        <div class="hero-conteudo">
            <span class="etiqueta"><span class="ponto-etiqueta"></span> MENTORIA ENTRE ALUNOS</span>
            <h1>Aprenda com quem já <span>percorreu o caminho.</span></h1>
            <p class="hero-descricao">
                Conecte-se com alunos que já passaram pelas mesmas disciplinas, tire dúvidas,
                compartilhe experiências e avance na sua jornada acadêmica e profissional.
            </p>
            <div class="hero-acoes">
                <a class="botao botao-principal" href="${ctx}/CadastroAluno.jsp">Encontrar um mentor <span aria-hidden="true">→</span></a>
                <a class="botao botao-secundario" href="${ctx}/CadastroMentor.jsp">Quero ser mentor</a>
            </div>
            <p class="hero-nota">Conhecimento compartilhado transforma trajetórias.</p>
        </div>

        <div class="hero-visual" aria-hidden="true">
            <div class="orbita orbita-externa"></div>
            <div class="orbita orbita-interna"></div>
            <div class="selo-central">
                <span class="selo-simbolo">S</span>
                <span class="selo-texto">SENSEI.DEV</span>
                <span class="selo-subtexto">CRESCEMOS JUNTOS</span>
            </div>
            <span class="ponto-flutuante ponto-um">01</span>
            <span class="ponto-flutuante ponto-dois">02</span>
            <span class="ponto-flutuante ponto-tres">03</span>
            <div class="cartao-flutuante">
                <span class="cartao-icone" aria-hidden="true">✦</span>
                <span><strong>Aprender</strong><small>Compartilhar · Evoluir</small></span>
            </div>
        </div>
    </section>

    <section class="numeros secao-largura" id="numeros" aria-labelledby="titulo-numeros">
        <div class="cabecalho-secao">
            <div>
                <span class="sobretitulo">NOSSA COMUNIDADE</span>
                <h2 id="titulo-numeros">O aprendizado acontece em conjunto.</h2>
            </div>
            <p>Dados atualizados diretamente com os cadastros e as mentorias da plataforma.</p>
        </div>

        <c:choose>
            <c:when test="${dadosDisponiveis}">
                <div class="grade-indicadores">
                    <article class="indicador">
                        <span class="indicador-icone" aria-hidden="true">✧</span>
                        <strong class="indicador-numero"><fmt:formatNumber value="${dados.mentoresCadastrados}" groupingUsed="true"/></strong>
                        <h3>Mentores cadastrados</h3>
                        <p>Perfis ativos prontos para compartilhar conhecimento.</p>
                    </article>

                    <article class="indicador">
                        <span class="indicador-icone" aria-hidden="true">◎</span>
                        <strong class="indicador-numero"><fmt:formatNumber value="${dados.mentoradosCadastrados}" groupingUsed="true"/></strong>
                        <h3>Mentorados cadastrados</h3>
                        <p>Alunos que fazem parte da nossa comunidade.</p>
                    </article>

                    <article class="indicador indicador-mentorias">
                        <span class="indicador-icone" aria-hidden="true">⌘</span>
                        <strong class="indicador-numero"><fmt:formatNumber value="${dados.totalMentorias}" groupingUsed="true"/></strong>
                        <h3>Mentorias contabilizadas</h3>
                        <div class="legenda-status" aria-label="Situação das mentorias">
                            <span><i class="status status-ativa"></i><b><fmt:formatNumber value="${dados.mentoriasAtivas}" groupingUsed="true"/></b> ativas</span>
                            <span><i class="status status-pendente"></i><b><fmt:formatNumber value="${dados.mentoriasPendentes}" groupingUsed="true"/></b> pendentes</span>
                            <span><i class="status status-finalizada"></i><b><fmt:formatNumber value="${dados.mentoriasFinalizadas}" groupingUsed="true"/></b> finalizadas</span>
                        </div>
                    </article>
                </div>
            </c:when>
            <c:otherwise>
                <div class="aviso-dados" role="status">
                    <strong>Os indicadores estão temporariamente indisponíveis.</strong>
                    <span>Não foi possível consultar o banco de dados agora. Tente novamente mais tarde.</span>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="depoimentos secao-largura" id="depoimentos" aria-labelledby="titulo-depoimentos">
        <div class="cabecalho-secao cabecalho-depoimentos">
            <div>
                <span class="sobretitulo">QUEM JÁ VIVEU ESSA EXPERIÊNCIA</span>
                <h2 id="titulo-depoimentos">Palavras de quem aprende com a gente.</h2>
            </div>
            <p>Depoimentos publicados a partir das avaliações enviadas pelos mentorados após as sessões.</p>
        </div>

        <c:choose>
            <c:when test="${not empty dados.depoimentos}">
                <div class="grade-depoimentos">
                    <c:forEach var="depoimento" items="${dados.depoimentos}">
                        <article class="depoimento-card">
                            <div class="depoimento-nota">
                                <span aria-hidden="true">★</span>
                                <strong><fmt:formatNumber value="${depoimento.nota}" minFractionDigits="1" maxFractionDigits="1"/></strong>
                                <small>/ 5</small>
                            </div>
                            <blockquote><c:out value="${depoimento.comentario}"/></blockquote>
                            <div class="depoimento-autor">
                                <span class="avatar-depoimento" aria-hidden="true"><c:out value="${depoimento.iniciais}"/></span>
                                <span class="autor-info">
                                    <strong><c:out value="${depoimento.nome}"/></strong>
                                    <small><c:out value="${depoimento.disciplina}"/></small>
                                </span>
                                <time class="data-depoimento"><c:out value="${depoimento.dataTexto}"/></time>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:when test="${dadosDisponiveis}">
                <div class="depoimentos-vazios">
                    <span aria-hidden="true">✧</span>
                    <strong>As próximas histórias podem começar aqui.</strong>
                    <p>Assim que os mentorados enviarem avaliações com comentários, elas aparecerão nesta seção.</p>
                </div>
            </c:when>
        </c:choose>
    </section>

    <section class="participar secao-largura" id="participar" aria-labelledby="titulo-participar">
        <div class="participar-intro">
            <span class="sobretitulo">FAÇA PARTE</span>
            <h2 id="titulo-participar">Todo mundo tem algo para ensinar. E algo para aprender.</h2>
            <p>Escolha como você quer fazer parte da comunidade Sensei.dev.</p>
        </div>

        <div class="grade-participar">
            <article class="participar-card participar-mentor">
                <span class="participar-numero">01 / ENSINAR</span>
                <div class="participar-icone" aria-hidden="true">↗</div>
                <h3>Seja um mentor</h3>
                <p>Transforme o que você já aprendeu em novas oportunidades. Oriente outros alunos, compartilhe experiências e fortaleça sua própria jornada.</p>
                <ul>
                    <li>Compartilhe seu conhecimento.</li>
                    <li>Ajude colegas a superar desafios.</li>
                    <li>Construa experiência ensinando.</li>
                </ul>
                <a class="botao botao-card" href="${ctx}/CadastroMentor.jsp">Quero ser mentor <span aria-hidden="true">→</span></a>
            </article>

            <article class="participar-card participar-mentorado">
                <span class="participar-numero">02 / APRENDER</span>
                <div class="participar-icone" aria-hidden="true">⌁</div>
                <h3>Seja um mentorado</h3>
                <p>Encontre alguém que já percorreu o caminho que você está trilhando. Tire dúvidas, troque ideias e avance no seu ritmo.</p>
                <ul>
                    <li>Encontre mentores por disciplina.</li>
                    <li>Solicite uma sessão de mentoria.</li>
                    <li>Avalie sua experiência após a sessão.</li>
                </ul>
                <a class="botao botao-card" href="${ctx}/CadastroAluno.jsp">Quero encontrar um mentor <span aria-hidden="true">→</span></a>
            </article>
        </div>
    </section>
</main>

<footer class="rodape-publico">
    <a class="marca marca-rodape" href="${ctx}/">SENSEI<span>.DEV</span></a>
    <p>O conhecimento cresce quando é compartilhado.</p>
    <span class="rodape-direitos">© <fmt:formatDate value="${agora}" pattern="yyyy"/> SENSEI.DEV · Todos os direitos reservados.</span>
</footer>
</body>
</html>
