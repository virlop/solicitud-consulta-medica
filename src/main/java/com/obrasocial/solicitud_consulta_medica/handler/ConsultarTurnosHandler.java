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
public class ConsultarTurnosHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarTurnosHandler.class);

    private final ConsultaMedicaService consultaMedicaService;

    @Autowired
    public ConsultarTurnosHandler(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @JobWorker(type = "revisarAgenda")
    public void revisarAgenda(final JobClient client, final ActivatedJob job) {
        logger.info("📅 Consultando agenda para job: {}", job.getKey());
        consultaMedicaService.revisarAgenda(job);
        logger.info("✅ Consulta de agenda completada para job: {}", job.getKey());
    }
}
