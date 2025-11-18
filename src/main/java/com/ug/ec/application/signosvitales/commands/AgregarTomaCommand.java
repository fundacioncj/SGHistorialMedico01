package com.ug.ec.application.signosvitales.commands;

import com.ug.ec.domain.signosvitales.valueobjects.TomaSignos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgregarTomaCommand {
    private String signosVitalesId;
    private TomaSignos toma;
}