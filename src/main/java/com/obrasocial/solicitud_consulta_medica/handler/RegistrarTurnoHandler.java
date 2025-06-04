package com.obrasocial.solicitud_consulta_medica.handler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
public class RegistrarTurnoHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarTurnoHandler.class);

    @JobWorker(type = "confirmarTurno")
    public void registrarTurno(
            final JobClient client,
            final ActivatedJob job,
            @Variable String num_socio,
            @Variable String fechaTurno // <-- NOTA: ahora es String
    ) throws InterruptedException {

        try {
            logger.info("📆 Registrando turno para socio: {} en fecha: {}", num_socio, fechaTurno);

            // === Validaciones de negocio ===

            if ("990".equals(num_socio)) {
                logger.warn("⚠️ Turno duplicado detectado para socio {}", num_socio);
                client.newThrowErrorCommand(job)
                        .errorCode("turnoDuplicado")
                        .errorMessage("El socio ya tiene un turno registrado.")
                        .send()
                        .join();
                return;
            }

            LocalDate fecha = LocalDate.parse(fechaTurno); // <-- Se convierte manualmente

            if (fecha.getYear() > 2025) {
                logger.warn("⚠️ Fecha inválida detectada: {}", fecha);
                client.newThrowErrorCommand(job)
                        .errorCode("fechaInvalida")
                        .errorMessage("La fecha del turno supera el año permitido.")
                        .send()
                        .join();
                return;
            }

            // === Simulación de errores técnicos ===
            if ("118".equals(num_socio)) throw new InterruptedException("Connection time out");
            if ("119".equals(num_socio)) throw new InterruptedException("Error DB");

            // === Turno confirmado exitosamente ===

            Map<String, Object> variables = new HashMap<>();
            variables.put("mensaje", "Turno confirmado exitosamente.");

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            logger.info("✅ Turno confirmado para socio {} en fecha {}", num_socio, fecha);

        } catch (Exception e) {
            logger.error("❌ Error técnico al registrar turno para socio {}: {}", num_socio, e.getMessage(), e);

            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico registrando turno: " + e.getMessage())
                    .send()
                    .join();

            throw new InterruptedException("Error técnico registrando turno: " + e.getMessage());
        }
    }
}
