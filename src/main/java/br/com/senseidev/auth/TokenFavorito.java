package br.com.senseidev.auth;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/** Token por sessão para os formulários que alteram favoritos. */
public final class TokenFavorito {
    private static final String CHAVE = "tokenFavorito";

    private TokenFavorito() { }

    public static String obter(HttpSession sessao) {
        synchronized (sessao) {
            Object valor = sessao.getAttribute(CHAVE);
            if (valor instanceof String) return (String) valor;
            String novo = UUID.randomUUID().toString();
            sessao.setAttribute(CHAVE, novo);
            return novo;
        }
    }

    public static boolean valido(HttpSession sessao, String enviado) {
        if (sessao == null || enviado == null) return false;
        Object esperado = sessao.getAttribute(CHAVE);
        return esperado instanceof String && MessageDigest.isEqual(
                ((String) esperado).getBytes(StandardCharsets.UTF_8),
                enviado.getBytes(StandardCharsets.UTF_8));
    }
}
