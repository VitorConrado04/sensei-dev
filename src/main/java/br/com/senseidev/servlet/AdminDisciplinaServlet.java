package br.com.senseidev.servlet;

import br.com.senseidev.dao.DisciplinaDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// CRUD de disciplina (admin). Rota /admin/*: o AutenticacaoFiltro ja barra quem nao e ADMIN
@WebServlet("/admin/disciplinas")
public class AdminDisciplinaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Mostra a lista; se vier ?editar=ID, tambem preenche o formulario com essa disciplina
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            DisciplinaDao dao = new DisciplinaDao(getServletContext());
            req.setAttribute("disciplinas", dao.listarDisciplinas());

            int editarId = lerInt(req.getParameter("editar"));
            if (editarId > 0) {
                req.setAttribute("disciplinaEdicao", dao.buscarPorId(editarId));
            }
            req.getRequestDispatcher("/WEB-INF/views/admin/Disciplinas.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao listar disciplinas.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Recebe o formulario (acao=salvar) ou o botao Excluir (acao=excluir)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos; antes do primeiro getParameter

        String lista = req.getContextPath() + "/admin/disciplinas";
        String acao = req.getParameter("acao");
        int id = lerInt(req.getParameter("id"));
        // Em caso de erro ao salvar, volta ao formulario (ja em modo edicao se tinha id)
        String volta = lista + (id > 0 ? "?editar=" + id + "&" : "?");

        try {
            DisciplinaDao dao = new DisciplinaDao(getServletContext());

            if ("excluir".equals(acao)) {
                if (id > 0) dao.excluirDisciplina(id);
                resp.sendRedirect(lista + "?ok=excluida");

            } else if ("salvar".equals(acao)) {
                String nome = limpar(req.getParameter("nome"));
                Integer semestre = lerSemestre(req.getParameter("semestre"));

                if (nome.isEmpty() || nome.length() > 150 || semestre == null) {
                    resp.sendRedirect(volta + "erro=dados");
                    return;
                }
                if (dao.nomeExiste(nome, id)) {
                    resp.sendRedirect(volta + "erro=nome");
                    return;
                }

                // Igual ao lab 5: id vazio = cadastrar; id preenchido = atualizar
                if (id > 0) {
                    dao.atualizarDisciplina(id, nome, semestre);
                    resp.sendRedirect(lista + "?ok=atualizada");
                } else {
                    dao.criarDisciplina(nome, semestre);
                    resp.sendRedirect(lista + "?ok=criada");
                }

            } else {
                resp.sendRedirect(lista);
            }

        } catch (SQLException e) {
            // 23503 = violou chave estrangeira: a disciplina esta em uso
            if ("23503".equals(e.getSQLState())) {
                resp.sendRedirect(lista + "?erro=vinculos");
            } else {
                getServletContext().log("Erro na acao sobre a disciplina.", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private static int lerInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    private static Integer lerSemestre(String valor) {
        try {
            int semestre = Integer.parseInt(valor);
            return semestre >= 1 && semestre <= 8 ? semestre : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}