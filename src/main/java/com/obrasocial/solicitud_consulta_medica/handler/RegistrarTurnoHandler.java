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
public class RegistrarTurnoHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarTurnoHandler.class);

    private final ConsultaMedicaService consultaMedicaService;

    @Autowired
    public RegistrarTurnoHandler(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @JobWorker(type = "confirmarTurno")
    public void confirmarTurno(final JobClient client, final ActivatedJob job) {
        logger.info("📅 Registrando turno para job: {}", job.getKey());
        consultaMedicaService.confirmarTurno(job);
        logger.info("✅ Turno registrado para job: {}", job.getKey());
    }
}
