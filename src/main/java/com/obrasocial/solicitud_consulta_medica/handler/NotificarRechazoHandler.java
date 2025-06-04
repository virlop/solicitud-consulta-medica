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

@Component
public class NotificarRechazoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificarRechazoHandler.class);

    @JobWorker(type = "notificarRechazo")
    public void notificarRechazo(
            final JobClient client,
            final ActivatedJob job,
            @Variable String num_socio,
            @Variable String email
    ) throws InterruptedException {

        try {
            logger.info("📨 Notificando rechazo al socio: {} con email: {}", num_socio, email);

            // Simulación de email inválido general
            if (email == null || !email.contains("@")) {
                logger.warn("❗ Email inválido: {}", email);
                client.newThrowErrorCommand(job)
                        .errorCode("emailInvalido")
                        .errorMessage("El email proporcionado no tiene formato válido")
                        .send()
                        .join();
                return;
            }

            // === Errores técnicos simulados ===
            if ("121".equals(num_socio)) throw new InterruptedException("Error API notificación");
            if ("122".equals(num_socio)) throw new InterruptedException("SMTP caído");

            // Simulación de notificación exitosa
            Map<String, Object> variables = new HashMap<>();
            variables.put("mensaje", "Notificación de rechazo enviada correctamente.");

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            logger.info("✅ Notificación de rechazo enviada al socio {}", num_socio);

        } catch (Exception e) {
            logger.error("❌ Error técnico al notificar rechazo al socio {}: {}", num_socio, e.getMessage(), e);

            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico notificando rechazo: " + e.getMessage())
                    .send()
                    .join();

            throw new InterruptedException("Error técnico notificando rechazo: " + e.getMessage());
        }
    }
}
