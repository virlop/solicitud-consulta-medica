package com.obrasocial.solicitud_consulta_medica.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;

@Service
public class ConsultaMedicaService {

    @Autowired
    private ZeebeClient zeebeClient;

    public void verificarCobertura(ActivatedJob job) {
        String numSocio = (String) job.getVariablesAsMap().get("num_socio");
        boolean apto = numSocio != null && numSocio.startsWith("1");

        completeJob(job, Map.of("apto", apto));
    }

    public void revisarAgenda(ActivatedJob job) {
        String especialidad = (String) job.getVariablesAsMap().get("especialidad");
        boolean disponible = !"dermatología".equalsIgnoreCase(especialidad);

        completeJob(job, Map.of("turnoDisponible", disponible));
    }

    public void confirmarTurno(ActivatedJob job) {
        String numSocio = (String) job.getVariablesAsMap().get("num_socio");
        String fechaTurno = (String) job.getVariablesAsMap().get("fechaTurno");

        if ("duplicado".equalsIgnoreCase(numSocio)) {
            throw new RuntimeException("turnoDuplicado");
        }

        if ("2025-02-30".equals(fechaTurno)) {
            throw new RuntimeException("fechaInvalida");
        }

        completeJob(job, null);
    }

    public void notificarRechazo(ActivatedJob job) {
        String email = (String) job.getVariablesAsMap().get("email");

        if (email == null || !email.contains("@")) {
            throw new RuntimeException("emailInvalido");
        }

        completeJob(job, null);
    }

    public void notificarSinTurno(ActivatedJob job) {
        String email = (String) job.getVariablesAsMap().get("email");

        if (email == null || !email.contains("@")) {
            throw new RuntimeException("emailInvalido");
        }

        completeJob(job, null);
    }

    // Utilidad para completar el job con o sin variables
    private void completeJob(ActivatedJob job, Map<String, Object> variables) {
        CompleteJobCommandStep1 completeCmd = zeebeClient.newCompleteCommand(job.getKey());

        if (variables != null && !variables.isEmpty()) {
            completeCmd.variables(variables);
        }

        completeCmd.send().join();
    }
}
