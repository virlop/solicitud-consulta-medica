package com.obrasocial.solicitud_consulta_medica;

import java.util.List;
import java.util.Map;

public class FakeRandomizer {

    private static final List<Map<String, Object>> testCases = List.of(
        // Casos de prueba para la verificación de cobertura médica
        // ✅ Caso exitoso: apto OK, turno disponible OK
        Map.of(
            "num_socio", "123456",
            "especialidad", "Neurología",
            "motivo", "Dolor",
            "email", "1casoexitoso@correo.com",
            "fechaTurno", "2025-06-01"
        ),
        
        // ❌ Socio NO REGISTRADO num_socio = "999" --> (apto = false)
        Map.of(
            "num_socio", "999",
            "especialidad", "Pediatria",
            "motivo", "Dolor de cabeza",
            "email", "2socionoregistrado@correo.com",
            "fechaTurno", "2025-06-01"
        ),
        // ❌ Socio CON DEUDA num_socio = "998" --> (apto = false)
        Map.of(
            "num_socio", "998",
            "especialidad", "Neurología",
            "motivo", "Dolor",
            "email", "3sociocondeuda@correo.com",
            "fechaTurno", "2025-06-01"
        ),
        // ❌ Sin turno disponible (turnoDisponible = false)
        Map.of(
            "num_socio", "123456",
            "especialidad", "Dermatología",
            "motivo", "Lunares",
            "email", "4sinturnopordermatologia@correo.com",
            "fechaTurno", "2025-06-01"
        ),
        // ❌ Email inválido para notificar socio no apto (socio no apto, falla notificación)
        Map.of(
            "num_socio", "999",
            "apto", false,
            "especialidad", "Dermatología",
            "motivo", "Lunares",
            "email", "5emailinvilidosocionoapto.com", // EMAIL INVÁLIDO
            "fechaTurno", "2025-06-01"
        ),

        // ❌ Email inválido para notificar sin turno disponible (socio apto, NO turno disponible, falla notificación)
        Map.of(
            "num_socio", "34659",
            "especialidad", "Dermatología",
            "motivo", "Lunares",
            "email", "6emailinvilidoturnonodisp.com", // inválido
            "fechaTurno", "2025-06-01"
        ),

        // ❌ Turno duplicado (turno ya asignado)
        Map.of(
            "num_socio", "990",
            "especialidad", "Neurología",
            "motivo", "Dolor",
            "email", "7errorporturnoduplicado@correo.com",
            "fechaTurno", "2025-06-01"
        ),

        // ❌ Fecha de turno inválida
        Map.of(
            "num_socio", "889",
            "especialidad", "Cardiología",
            "motivo", "Taquicardia",
            "email", "8errorporfechadeturnoinvalida@gmail.com"
        )
    

    );

    private static int currentIndex = 0;

    public Map<String, Object> getRandom() {
        if (currentIndex >= testCases.size()) {
            currentIndex = 0; // por si hay más instancias que casos
        }
        return testCases.get(currentIndex++);
    }
}
