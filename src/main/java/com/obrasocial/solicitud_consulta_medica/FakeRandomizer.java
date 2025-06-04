package com.obrasocial.solicitud_consulta_medica;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.github.javafaker.Faker;

public class FakeRandomizer {

    final Faker faker = new Faker();
    final Map<String, Object> inputVariables = new HashMap<>();
    final String uniqueId = uuid();

    public FakeRandomizer() {
        inputVariables.put("num_socio", "1" + faker.number().digits(5)); // empieza con "1" para que sea apto
        inputVariables.put("especialidad", randomEspecialidad());
        inputVariables.put("motivo", faker.lorem().sentence());
        inputVariables.put("apto", false);  // Podés simular con false si querés probar los rechazos
        inputVariables.put("turnoDisponible", false);
        inputVariables.put("fechaTurno", LocalDate.now().plusDays(5).toString());
        inputVariables.put("email", faker.internet().emailAddress());
    }

    public Map<String, Object> getRandom() {
        return inputVariables;
    }

    public static final String uuid() {
        String result = java.util.UUID.randomUUID().toString();
        return result.replaceAll("-", "").substring(0, 7);
    }

    private String randomEspecialidad() {
        String[] especialidades = {
            "Cardiología", "Pediatría", "Dermatología", "Neurología", "Traumatología"
        };
        return especialidades[faker.random().nextInt(especialidades.length)];
    }
}
