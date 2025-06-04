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

/**
 * Worker para revisar la disponibilidad de turnos en la agenda médica.
 * Aplica reglas de negocio según especialidad, motivo y número de socio.
 */
@Component
public class ConsultarTurnosHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarTurnosHandler.class);

    @JobWorker(type = "revisarAgenda")
    public void revisarAgenda(
            final JobClient client,
            final ActivatedJob job,
            @Variable String especialidad,
            @Variable String motivo,
            @Variable String num_socio
    ) throws InterruptedException {

        try {
            logger.info("📅 Revisión de agenda: especialidad={}, motivo={}, socio={}",
                    especialidad, motivo, num_socio);

            boolean turnoDisponible = false;
            LocalDate fechaTurno = null;
            String mensaje = "No hay turnos para la especialidad o motivo indicados.";

            // === REGLAS DE NEGOCIO ===

            if (especialidad.equalsIgnoreCase("Cardiología")) {
                if (motivo.equalsIgnoreCase("Fiebre")) {
                    // No se otorgan turnos para fiebre en cardiología
                    logger.info("🔕 Motivo 'Fiebre' no válido para Cardiología");
                } else if (motivo.equalsIgnoreCase("Taquicardia")) {
                    turnoDisponible = true;
                    fechaTurno = LocalDate.of(2035, 6, 15);
                    mensaje = "Turno especial para Cardiología - Taquicardia en 2035";
                } else {
                    turnoDisponible = true;
                    fechaTurno = LocalDate.now().plusDays(2);
                    mensaje = String.format("Turno de Cardiología disponible para el %s", fechaTurno);
                }
            }

            else if (especialidad.equalsIgnoreCase("Pediatria")) {
                if (motivo.equalsIgnoreCase("Dolor de cabeza")) {
                    turnoDisponible = true;
                    fechaTurno = LocalDate.now().plusDays(2);
                    mensaje = String.format("Turno de Pediatría disponible para el %s", fechaTurno);
                } else if (num_socio.equalsIgnoreCase("000")) {
                    turnoDisponible = true;
                    fechaTurno = LocalDate.now().plusDays(1);
                    mensaje = String.format("Turno prioritario de Pediatría disponible para el %s", fechaTurno);
                }
            }

            else if (especialidad.equalsIgnoreCase("Neurología") &&
                    motivo.equalsIgnoreCase("Dolor")) {
                turnoDisponible = true;
                fechaTurno = LocalDate.now();
                mensaje = String.format("Turno de Neurología disponible para el %s", fechaTurno);
            }

            else if (especialidad.equalsIgnoreCase("Dermatología") &&
                    motivo.equalsIgnoreCase("Lunares")) {
                turnoDisponible = false;
                mensaje = "No hay turnos disponibles para Dermatología - Lunares";
            }

            // === ERRORES TÉCNICOS SIMULADOS ===

            if ("114".equals(num_socio)) throw new InterruptedException("🚨 API caída");
            if ("115".equals(num_socio)) throw new InterruptedException("🚨 Timeout");
            if ("116".equals(num_socio)) throw new InterruptedException("🚨 Error de base de datos");

            // === RESPUESTA ===

            Map<String, Object> variables = new HashMap<>();
            variables.put("turnoDisponible", turnoDisponible);

            if (turnoDisponible) {
                variables.put("fechaTurno", fechaTurno.toString()); // como string ISO 8601
                logger.info("✅ Turno asignado para {} el {}", especialidad, fechaTurno);
            } else {
                logger.warn("❗ No hay turnos disponibles: {}", mensaje);
            }

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            logger.info("📤 Resultado enviado: {}", mensaje);

        } catch (Exception e) {
            logger.error("❌ Error al revisar agenda del socio {}: {}", num_socio, e.getMessage(), e);

            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico revisando agenda: " + e.getMessage())
                    .send()
                    .join();

            throw new InterruptedException("Error técnico revisando agenda: " + e.getMessage());
        }
    }
}
