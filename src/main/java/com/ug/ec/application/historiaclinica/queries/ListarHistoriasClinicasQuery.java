package com.ug.ec.application.historiaclinica.queries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListarHistoriasClinicasQuery {
    private Boolean soloActivas;
}