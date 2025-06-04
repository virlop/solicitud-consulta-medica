package com.obrasocial.solicitud_consulta_medica;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.ZeebeClient;

@SpringBootApplication
public class SolicitudConsultaMedicaApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudConsultaMedicaApplication.class);

    // ⚠️ Este es el ID del proceso definido en tu modelo BPMN
    private static final String PROCESS_ID = "Process_SolicitudConsultaMedica";
    private static final int NUM_INSTANCES = 8; //CAMBIO EN NUMERO DE INSTANCIAS PARA EJECUTAR VARIAS PARA PROBAR TODOS LOS CAMINOS

    @Autowired
    private ZeebeClient zeebeClient;

    public static void main(String[] args) {
        SpringApplication.run(SolicitudConsultaMedicaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        startProcessInstances(NUM_INSTANCES);
    }

    public void startProcessInstances(int numInstances) {
        logger.info("🔄 Iniciando {} instancias del proceso: {}", numInstances, PROCESS_ID);
        for (int i = 0; i < numInstances; i++) {
            FakeRandomizer fakeRandomizer = new FakeRandomizer();
            Map<String, Object> variables = fakeRandomizer.getRandom();

            logger.info("🩺 Enviando solicitud de consulta para socio: {}", variables.get("num_socio"));
            var event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

            logger.info("✅ Proceso iniciado con ID: {}", event.getProcessInstanceKey());
        }
        logger.info("✅ Todas las instancias fueron creadas.");
    }
}
