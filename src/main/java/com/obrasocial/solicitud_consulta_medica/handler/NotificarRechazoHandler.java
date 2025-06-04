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
public class NotificarRechazoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificarRechazoHandler.class);

    private final ConsultaMedicaService consultaMedicaService;

    @Autowired
    public NotificarRechazoHandler(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @JobWorker(type = "notificarRechazo")
    public void notificarRechazo(final JobClient client, final ActivatedJob job) {
        logger.info("📢 Notificando rechazo para job: {}", job.getKey());
        consultaMedicaService.notificarRechazo(job);
        logger.info("✅ Rechazo notificado para job: {}", job.getKey());
    }
}
