package com.hyperreset.api.dto.request;

import com.hyperreset.api.entity.enums.TipoTest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TestFisicoRequest {

    @NotNull(message = "Deportista ID is required")
    private Long deportistaId;

    @NotNull(message = "Tipo de test is required")
    private TipoTest tipoTest;

    @Size(max = 500)
    private String notas;

    public TestFisicoRequest() {
    }

    public TestFisicoRequest(Long deportistaId, TipoTest tipoTest, String notas) {
        this.deportistaId = deportistaId;
        this.tipoTest = tipoTest;
        this.notas = notas;
    }

    public Long getDeportistaId() {
        return deportistaId;
    }

    public void setDeportistaId(Long deportistaId) {
        this.deportistaId = deportistaId;
    }

    public TipoTest getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(TipoTest tipoTest) {
        this.tipoTest = tipoTest;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
