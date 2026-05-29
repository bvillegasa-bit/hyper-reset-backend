package com.hyperreset.api.service;

import com.hyperreset.api.dto.request.MaterialRequest;
import com.hyperreset.api.dto.response.MaterialResponse;
import com.hyperreset.api.entity.Coach;
import com.hyperreset.api.entity.Material;
import com.hyperreset.api.entity.enums.TipoMaterial;
import com.hyperreset.api.exception.ResourceNotFoundException;
import com.hyperreset.api.repository.CoachRepository;
import com.hyperreset.api.repository.MaterialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialService.class);

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMateriales() {
        log.debug("Fetching all materiales");
        return materialRepository.findAll().stream()
                .map(this::toMaterialResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        log.debug("Fetching material by id: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
        return toMaterialResponse(material);
    }

    public MaterialResponse createMaterial(MaterialRequest request, Long coachUserId) {
        log.debug("Creating material by coachUserId: {}", coachUserId);

        Coach coach = coachRepository.findByUsuarioId(coachUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "usuarioId", coachUserId));

        TipoMaterial tipoMaterial;
        try {
            tipoMaterial = request.getTipoMaterial() != null
                    ? TipoMaterial.valueOf(request.getTipoMaterial().toUpperCase())
                    : TipoMaterial.DOCUMENTO;
        } catch (IllegalArgumentException e) {
            tipoMaterial = TipoMaterial.DOCUMENTO;
        }

        Material material = new Material(
                coach,
                request.getTitulo(),
                tipoMaterial,
                request.getUrlRecurso() != null ? request.getUrlRecurso() : ""
        );
        material.setDescripcionMaterial(request.getDescripcion());

        Material saved = materialRepository.save(material);
        log.info("Created material with id: {}", saved.getIdMaterial());

        return toMaterialResponse(saved);
    }

    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        log.debug("Updating material with id: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));

        material.setTituloMaterial(request.getTitulo());
        material.setDescripcionMaterial(request.getDescripcion());

        if (request.getTipoMaterial() != null) {
            try {
                material.setTipoMaterial(TipoMaterial.valueOf(request.getTipoMaterial().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tipoMaterial: {}, keeping current", request.getTipoMaterial());
            }
        }

        if (request.getUrlRecurso() != null) {
            material.setUrlContenido(request.getUrlRecurso());
        }

        Material saved = materialRepository.save(material);
        log.info("Updated material with id: {}", id);

        return toMaterialResponse(saved);
    }

    public void deleteMaterial(Long id) {
        log.debug("Deleting material with id: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", id));
        materialRepository.delete(material);
        log.info("Deleted material with id: {}", id);
    }

    private MaterialResponse toMaterialResponse(Material material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getIdMaterial());
        response.setTitulo(material.getTituloMaterial());
        response.setDescripcion(material.getDescripcionMaterial());
        response.setTipoMaterial(material.getTipoMaterial().name());
        response.setUrlRecurso(material.getUrlContenido());
        response.setFechaPublicacion(material.getFechaSubida().toLocalDate());
        return response;
    }
}
