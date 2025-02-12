package br.com.fiap.hackathon.reservafacil.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FaixaEtariaEnum {
    FAIXA_0_a_18(0, 18, "00 a 18"),
    FAIXA_19_a_28(19, 28, "19 a 28"),
    FAIXA_29_a_33(29, 33, "29 a 33"),
    FAIXA_34_a_43(34, 43, "34 a 43"),
    FAIXA_44_a_53(44, 53, "44 a 53"),
    FAIXA_54_a_58(54, 58, "54 a 58"),
    FAIXA_59_MAIS(59, Integer.MAX_VALUE, "59+");

    private Integer mim;
    private Integer max;
    private String faixaEtaria;

    public static FaixaEtariaEnum valueOf(Integer idade) {

        return Arrays.stream(FaixaEtariaEnum.values())
                .filter(faixa -> idade >= faixa.getMim() && idade <= faixa.getMax())
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Idade fora do range de faixas etarias"));
    }

    public static String getFaixaEtaria(Integer idade) {
        return valueOf(idade).getFaixaEtaria();
    }
}
