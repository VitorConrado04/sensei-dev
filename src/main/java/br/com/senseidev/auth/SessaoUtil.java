package br.com.senseidev.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Centraliza a leitura segura do ID de mentor guardado na sessão HTTP. */
public final class SessaoUtil {
    private SessaoUtil() { }

    public static int obterMentorId(HttpServletRequest req) throws ServletException {
        HttpSession sessao = req.getSession(false);
        Object valor = sessao == null ? null : sessao.getAttribute("mentorId");
        if (valor instanceof Number) {
            int id = ((Number) valor).intValue();
            if (id > 0) return id;
        }
        throw new ServletException("Sessão de mentor ausente ou inválida.");
    }
}