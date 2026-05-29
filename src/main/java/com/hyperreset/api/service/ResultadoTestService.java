package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.ResultadoRequest;
import com.hyperreset.api.dto.response.ResultadoResponse;
import com.hyperreset.api.entity.ResultadoTest;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.ResultadoTestRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultadoTestService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoTestService.class);

    @Autowired
    private ResultadoTestRepository resultadoTestRepository;

    @Autowired
    private TestFisicoRepository testFisicoRepository;

    @Transactional(readOnly = true)
    public List<ResultadoResponse> getResultadosByTest(Long testId) {
        // Verify test exists
        testFisicoRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", testId));

        List<ResultadoTest> resultados = resultadoTestRepository.findByTestFisicoIdTestFisico(testId);
        return resultados.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResultadoResponse> getResultadosByDeportista(Long deportistaId) {
        List<ResultadoTest> resultados = resultadoTestRepository.findByTestFisicoDeportistaId(deportistaId);
        return resultados.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResultadoResponse createResultado(ResultadoRequest request) {
        log.debug("Creating new Resultado for test ID: {}", request.getTestId());

        TestFisico testFisico = testFisicoRepository.findById(request.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", request.getTestId()));

        // Map the simple request to the entity
        ResultadoTest resultado = new ResultadoTest();
        resultado.setTestFisico(testFisico);
        resultado.setValorObtenido(BigDecimal.valueOf(request.getValor()));
        resultado.setUnidadMedicion(request.getUnidad());
        resultado.setObservaciones(request.getObservaciones());

        // parametro field maps to tipoTest — we store it as part of the observaciones
        // since the entity doesn't have a generic "parametro" field but has tipoTest enum.
        // For now, we don't have a mapping from string parametro to TipoTest enum in the request,
        // so we store it in observaciones as a note.
        if (request.getParametro() != null) {
            String obs = request.getParametro();
            if (request.getObservaciones() != null) {
                obs += ": " + request.getObservaciones();
            }
            resultado.setObservaciones(obs);
        }

        resultado = resultadoTestRepository.save(resultado);
        log.debug("Resultado created with ID: {}", resultado.getIdResultado());

        return mapToResponse(resultado);
    }

    public void deleteResultado(Long id) {
        log.debug("Deleting Resultado with ID: {}", id);

        ResultadoTest resultado = resultadoTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResultadoTest", "id", id));

        resultadoTestRepository.delete(resultado);
        log.debug("Resultado {} deleted", id);
    }

    private ResultadoResponse mapToResponse(ResultadoTest resultado) {
        return new ResultadoResponse(
                resultado.getIdResultado(),
                resultado.getTestFisico().getIdTestFisico(),
                resultado.getTipoTest() != null ? resultado.getTipoTest().name() : resultado.getObservaciones(),
                resultado.getValorObtenido().doubleValue(),
                resultado.getUnidadMedicion(),
                resultado.getObservaciones()
        );
    }
}
