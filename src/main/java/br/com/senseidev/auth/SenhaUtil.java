package br.com.senseidev.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Gera e verifica hashes de senha usando PBKDF2-HMAC-SHA-256. */
public final class SenhaUtil {
    private static final String PREFIXO = "pbkdf2-sha256$";
    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtil() { }

    public static String gerarHash(String senha) {
        if (senha == null) throw new IllegalArgumentException("A senha não pode ser nula.");
        byte[] salt = new byte[TAMANHO_SALT];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senha.toCharArray(), salt, ITERACOES);
        return PREFIXO + ITERACOES + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /** Também aceita temporariamente senhas antigas em texto puro para facilitar migração. */
    public static boolean verificar(String senhaInformada, String valorArmazenado) {
        if (senhaInformada == null || valorArmazenado == null) return false;
        if (!valorArmazenado.startsWith(PREFIXO)) {
            return MessageDigest.isEqual(
                    senhaInformada.getBytes(StandardCharsets.UTF_8),
                    valorArmazenado.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] partes = valorArmazenado.split("\\$", -1);
            if (partes.length != 4 || !"pbkdf2-sha256".equals(partes[0])) return false;
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 10_000 || iteracoes > 2_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(senhaInformada.toCharArray(), salt, iteracoes);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean precisaAtualizar(String valorArmazenado) {
        return valorArmazenado == null || !valorArmazenado.startsWith(PREFIXO);
    }

    private static byte[] derivar(char[] senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar o hash da senha.", e);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(senha, '\0');
        }
    }
}