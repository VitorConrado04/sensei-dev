package br.com.senseidev.auth;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/** Token da sessão para o formulário de solicitação de mentoria. */
public final class TokenMentoria {
    private static final String CHAVE = "tokenMentoria";

    private TokenMentoria() { }

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
