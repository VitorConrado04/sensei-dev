package br.com.senseidev.util;

import java.time.LocalDateTime;

// métodos pequenos pra formatar o que aparece nas telas do mentor
public class Formato {

    private static final String[] DIAS = {"Dom.", "Seg.", "Ter.", "Qua.", "Qui.", "Sex.", "Sáb."};

    private Formato() {
    }

    // "Seg. 07"
    public static String dia(LocalDateTime data) {
        // getValue() vai de 1 (segunda) até 7 (domingo), o % 7 deixa o domingo na posição 0
        String nomeDia = DIAS[data.getDayOfWeek().getValue() % 7];
        return nomeDia + " " + String.format("%02d", data.getDayOfMonth());
    }

    // "15:00"
    public static String hora(LocalDateTime data) {
        return String.format("%02d:%02d", data.getHour(), data.getMinute());
    }

    public static String primeiroNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "";
        }
        return nome.trim().split("\\s+")[0];
    }

    // "Mariano Silva" vira "MS" (usado no lugar da foto)
    public static String iniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "?";
        }
        String[] partes = nome.trim().split("\\s+");
        String resultado = partes[0].substring(0, 1);
        if (partes.length > 1) {
            resultado += partes[1].substring(0, 1);
        }
        return resultado.toUpperCase();
    }
}