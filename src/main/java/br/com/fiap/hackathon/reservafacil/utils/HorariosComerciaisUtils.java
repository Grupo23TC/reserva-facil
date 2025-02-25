package br.com.fiap.hackathon.reservafacil.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HorariosComerciaisUtils {

    public static List<LocalDateTime> gerarIntervalosComerciais(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<LocalDateTime> intervalos = new ArrayList<>();
        LocalDateTime atual = dataInicio;

        while (!atual.isAfter(dataFim)) {
            int hora = atual.getHour();

            // Verifica se o horário está dentro do horário comercial (06:00 - 19:00)
            if (hora >= 6 && hora <= 19) {
                intervalos.add(atual);
            }

            atual = atual.plusMinutes(15);
        }

        return intervalos;
    }
}
