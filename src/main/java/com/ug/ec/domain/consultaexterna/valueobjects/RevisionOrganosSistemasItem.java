package com.ug.ec.domain.consultaexterna.valueobjects;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionOrganosSistemasItem {

    @NotNull(message = "El órgano o sistema es obligatorio")
    private TipoOrganoSistema tipo;

    private boolean presente;

    private String descripcion;

    public enum TipoOrganoSistema {

        PIEL_ANEXOS("Piel y Anexos"),
        ORGANOS_SENTIDOS("Órganos de los Sentidos"),
        RESPIRATORIO("Respiratorio"),
        CARDIO_VASCULAR("Cardio - Vascular"),
        DIGESTIVO("Digestivo"),
        GENITO_URINARIO("Genito - Urinario"),
        MUSCULO_ESQUELETICO("Músculo - Esquelético"),
        ENDOCRINO("Endocrino"),
        HEMO_LINFATICO("Hemo - Linfático"),
        NERVIOSO("Nervioso");

        private final String etiqueta;

        TipoOrganoSistema(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }
}