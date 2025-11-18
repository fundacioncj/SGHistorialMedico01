package com.ug.ec.domain.historiaclinica.enums;

public enum TipoAntecedente {

    CARDIOPATIA("Cardiopatía"),
    HIPERTENSION("Hipertensión"),
    ENF_C_VASCULAR("Enfermedad Cerebrovascular"),
    ENDOCRINO_METABOLICO("Endocrino Metabólico"),
    CANCER("Cáncer"),
    TUBERCULOSIS("Tuberculosis"),
    ENF_MENTAL("Enfermedad Mental"),
    ENF_INFECCIOSA("Enfermedad Infecciosa"),
    MAL_FORMACION("Malformación"),
    OTRO("Otro");

    private final String descripcion;

    TipoAntecedente(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
