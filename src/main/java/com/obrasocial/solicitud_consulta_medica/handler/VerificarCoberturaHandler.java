package com.obrasocial.solicitud_consulta_medica.handler;

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
 * Worker que verifica la cobertura médica de un paciente/socio.
 */
@Component
public class VerificarCoberturaHandler {

    private static final Logger logger = LoggerFactory.getLogger(VerificarCoberturaHandler.class);

    @JobWorker(type = "verificarCobertura")
    public void handleVerificarCoberturaPaciente(
            final JobClient client,
            final ActivatedJob job,
            @Variable String num_socio) throws InterruptedException {

        try {
            logger.info("📋 Iniciando verificación de cobertura para socio: {}", num_socio);

            // Por defecto asumimos que el socio está apto
            boolean apto = true;

            // === REGLAS DE NEGOCIO ===

            // Socio no registrado
            if ("999".equals(num_socio)) {
                apto = false;
                logger.warn("❌ Socio no registrado - Número: {}", num_socio);
            }

            // Socio con deudas pendientes
            if ("998".equals(num_socio) || "995".equals(num_socio)) {
                apto = false;
                logger.warn("❌ Socio con deudas pendientes - Número: {}", num_socio);
            }

            // === ERRORES TÉCNICOS SIMULADOS ===

            if ("111".equals(num_socio)) {
                throw new InterruptedException("🚨 La API se encuentra caída");
            }

            if ("112".equals(num_socio)) {
                throw new InterruptedException("🚨 Connection time out");
            }

            if ("113".equals(num_socio)) {
                throw new InterruptedException("🚨 Error en base de datos");
            }

            // === RESPUESTA ===

            Map<String, Object> variables = new HashMap<>();
            variables.put("apto", apto);

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            logger.info("✅ Verificación completada. Socio {} - Apto: {}", num_socio, apto);

        } catch (Exception e) {
            logger.error("💥 Error técnico al verificar cobertura del socio {}: {}", num_socio, e.getMessage(), e);

            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico verificando cobertura: " + e.getMessage())
                    .send()
                    .join();

            throw new InterruptedException("Error técnico verificando cobertura: " + e.getMessage());
        }
    }
}
