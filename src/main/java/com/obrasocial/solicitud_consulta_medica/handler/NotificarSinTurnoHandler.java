package com.obrasocial.solicitud_consulta_medica.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.obrasocial.solicitud_consulta_medica.services.ConsultaMedicaService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Component
public class NotificarSinTurnoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificarSinTurnoHandler.class);

    private final ConsultaMedicaService consultaMedicaService;

    @Autowired
    public NotificarSinTurnoHandler(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @JobWorker(type = "notificarSinTurno")
    public void notificarSinTurno(final JobClient client, final ActivatedJob job) {
        logger.info("📢 Notificando falta de turno para job: {}", job.getKey());
        consultaMedicaService.notificarSinTurno(job);
        logger.info("✅ Notificación enviada para job: {}", job.getKey());
    }
}
