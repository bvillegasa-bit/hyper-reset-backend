package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.TestFisicoRequest;
import com.hyperreset.api.dto.response.TestFisicoResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Deportista;
import com.hyperreset.api.entity.TestFisico;
import com.hyperreset.api.entity.enums.EstadoTest;
import com.hyperreset.api.exception.BadRequestException;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.DeportistaRepository;
import com.hyperreset.api.repository.TestFisicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestFisicoService {

    private static final Logger log = LoggerFactory.getLogger(TestFisicoService.class);

    @Autowired
    private TestFisicoRepository testFisicoRepository;

    @Autowired
    private DeportistaRepository deportistaRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Transactional(readOnly = true)
    public List<TestFisicoResponse> getAllTests() {
        List<TestFisico> tests = testFisicoRepository.findAll();
        return tests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestFisicoResponse getTestById(Long id) {
        TestFisico test = testFisicoRepository.findByIdWithDeportista(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", id));
        return mapToResponse(test);
    }

    @Transactional(readOnly = true)
    public List<TestFisicoResponse> getTestsByDeportista(Long deportistaId) {
        // Verify deportista exists
        deportistaRepository.findById(deportistaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", deportistaId));

        List<TestFisico> tests = testFisicoRepository.findByDeportistaId(deportistaId);
        return tests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TestFisicoResponse createTest(TestFisicoRequest request, Long userId) {
        log.debug("Creating new TestFisico for deportista ID: {}, userId: {}", request.getDeportistaId(), userId);

        Deportista deportista = deportistaRepository.findById(request.getDeportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", request.getDeportistaId()));

        // Coach is optional - DEPORTISTA self-testing won't have a coach record
        Coach coach = coachRepository.findById(userId).orElse(null);

        TestFisico test = new TestFisico();
        test.setDeportista(deportista);
        test.setCoach(coach);
        test.setTipoTest(request.getTipoTest());
        test.setLugar(request.getNotas() != null ? request.getNotas() : "");
        test.setFechaEjecucion(LocalDateTime.now());
        test.setEstadoTest(EstadoTest.EN_PROGRESO);

        test = testFisicoRepository.save(test);
        log.debug("TestFisico created with ID: {}", test.getIdTestFisico());

        return mapToResponse(test);
    }

    public TestFisicoResponse updateTest(Long id, TestFisicoRequest request) {
        log.debug("Updating TestFisico with ID: {}", id);

        TestFisico test = testFisicoRepository.findByIdWithDeportista(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", id));

        if (request.getDeportistaId() != null) {
            Deportista deportista = deportistaRepository.findById(request.getDeportistaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Deportista", "id", request.getDeportistaId()));
            test.setDeportista(deportista);
        }

        if (request.getNotas() != null) {
            test.setLugar(request.getNotas());
        }

        test = testFisicoRepository.save(test);
        return mapToResponse(test);
    }

    public void deleteTest(Long id) {
        log.debug("Deleting TestFisico with ID: {}", id);

        TestFisico test = testFisicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", id));

        testFisicoRepository.delete(test);
        log.debug("TestFisico {} deleted", id);
    }

    public TestFisicoResponse completarTest(Long id) {
        log.debug("Completing TestFisico with ID: {}", id);

        TestFisico test = testFisicoRepository.findByIdWithDeportista(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestFisico", "id", id));

        if (test.getEstadoTest() == EstadoTest.COMPLETADO) {
            throw new BadRequestException("El test ya está completado");
        }

        test.setEstadoTest(EstadoTest.COMPLETADO);
        test = testFisicoRepository.save(test);

        log.debug("TestFisico {} marked as COMPLETADO", id);
        return mapToResponse(test);
    }

    private TestFisicoResponse mapToResponse(TestFisico test) {
        String deportistaNombre = test.getDeportista().getUsuario().getNombres()
                + " " + test.getDeportista().getUsuario().getApellidos();

        String tipoTestStr = test.getTipoTest() != null ? test.getTipoTest().name() : null;

        return new TestFisicoResponse(
                test.getIdTestFisico(),
                deportistaNombre,
                tipoTestStr,
                test.getFechaEjecucion().toLocalDate(),
                test.getLugar(),
                test.getEstadoTest() == EstadoTest.COMPLETADO
        );
    }
}
