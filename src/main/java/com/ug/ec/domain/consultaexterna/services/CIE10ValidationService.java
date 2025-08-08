package com.ug.ec.domain.consultaexterna.services;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

import static com.ug.ec.infrastructure.config.CacheConfig.CACHE_CATALOGOS;

/**
 * Servicio para validación de códigos CIE-10
 * Implementa validación de formato y catálogo básico de códigos comunes
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CIE10ValidationService {

    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    // Patrón para validar formato de códigos CIE-10: una letra seguida de 2-3 dígitos, opcionalmente seguido de un punto y 1-2 dígitos
    private static final Pattern CIE10_PATTERN = Pattern.compile("^[A-Z][0-9]{2}(\\.[0-9]{1,2})?$");

    /**
     * Valida si un código CIE-10 tiene el formato estructural correcto.
     * <p>
     * Formato esperado: Una letra, dos dígitos, opcionalmente un punto y de 1 a 4 dígitos.
     * Ejemplos válidos: A00, R51, C34.9, Z00.00
     *
     * @param codigoCie10 El código a validar.
     * @return {@code true} si el código es estructuralmente válido, {@code false} en caso contrario.
     */
    public boolean esCodigoValido(String codigoCie10) {
        if (codigoCie10 == null || codigoCie10.trim().isEmpty()) {
            return false;
        }
        return CIE10_PATTERN.matcher(codigoCie10).matches();
    }

    // Catálogo básico de códigos CIE-10 comunes con sus descripciones
    private static final Map<String, String> CATALOGO_CIE10 = initCatalogo();

    private static Map<String, String> initCatalogo() {
        Map<String, String> catalogo = new HashMap<>();

        // Enfermedades infecciosas (A00-B99)
        catalogo.put("A00", "Cólera");
        catalogo.put("A15", "Tuberculosis respiratoria");
        catalogo.put("A90", "Dengue");
        catalogo.put("B20", "Enfermedad por VIH");

        // Neoplasias (C00-D48)
        catalogo.put("C50", "Tumor maligno de la mama");
        catalogo.put("C61", "Tumor maligno de la próstata");

        // Enfermedades endocrinas (E00-E90)
        catalogo.put("E10", "Diabetes mellitus insulinodependiente");
        catalogo.put("E11", "Diabetes mellitus no insulinodependiente");
        catalogo.put("E66", "Obesidad");

        // Trastornos mentales (F00-F99)
        catalogo.put("F32", "Episodio depresivo");
        catalogo.put("F41", "Otros trastornos de ansiedad");

        // Enfermedades del sistema nervioso (G00-G99)
        catalogo.put("G40", "Epilepsia");
        catalogo.put("G43", "Migraña");

        // Enfermedades del sistema circulatorio (I00-I99)
        catalogo.put("I10", "Hipertensión esencial (primaria)");
        catalogo.put("I21", "Infarto agudo de miocardio");
        catalogo.put("I50", "Insuficiencia cardíaca");

        // Enfermedades del sistema respiratorio (J00-J99)
        catalogo.put("J00", "Rinofaringitis aguda");
        catalogo.put("J18", "Neumonía");
        catalogo.put("J44", "Enfermedad pulmonar obstructiva crónica");
        catalogo.put("J45", "Asma");

        // Enfermedades del sistema digestivo (K00-K93)
        catalogo.put("K29", "Gastritis y duodenitis");
        catalogo.put("K80", "Colelitiasis");

        // Enfermedades del sistema genitourinario (N00-N99)
        catalogo.put("N18", "Enfermedad renal crónica");
        catalogo.put("N39", "Otros trastornos del sistema urinario");

        // Embarazo, parto y puerperio (O00-O99)
        catalogo.put("O00", "Embarazo ectópico");
        catalogo.put("O80", "Parto único espontáneo");

        // Ciertas afecciones originadas en el periodo perinatal (P00-P96)
        catalogo.put("P07", "Trastornos relacionados con duración corta de la gestación y con bajo peso al nacer");

        // Malformaciones congénitas (Q00-Q99)
        catalogo.put("Q24", "Otras malformaciones congénitas del corazón");

        // Síntomas y signos (R00-R99)
        catalogo.put("R50", "Fiebre de origen desconocido");
        catalogo.put("R51", "Cefalea");

        // Traumatismos y envenenamientos (S00-T98)
        catalogo.put("S06", "Traumatismo intracraneal");
        catalogo.put("T14", "Traumatismo de región no especificada del cuerpo");

        // Factores que influyen en el estado de salud (Z00-Z99)
        catalogo.put("Z00", "Examen general");
        catalogo.put("Z71", "Consulta para asesoramiento");

        return catalogo;
    }

    /**
     * Valida si un código CIE-10 tiene el formato correcto
     * @param codigo Código CIE-10 a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public boolean validarFormatoCIE10(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        return CIE10_PATTERN.matcher(codigo.trim()).matches();
    }

    /**
     * Verifica si un código CIE-10 existe en el catálogo básico
     * @param codigo Código CIE-10 a verificar
     * @return true si el código existe en el catálogo, false en caso contrario
     */
    public boolean existeEnCatalogo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        // Normalizar el código (quitar espacios y convertir a mayúsculas)
        String codigoNormalizado = codigo.trim().toUpperCase();

        // Si el código tiene punto, verificar directamente
        if (codigoNormalizado.contains(".")) {
            return CATALOGO_CIE10.containsKey(codigoNormalizado);
        }

        // Si el código no tiene punto, verificar si existe como categoría principal
        return CATALOGO_CIE10.containsKey(codigoNormalizado);
    }

    /**
     * Obtiene la descripción de un código CIE-10
     * @param codigo Código CIE-10
     * @return Descripción del código o null si no existe
     */
    @Timed(value = "app.cie10.descripcion", description = "Tiempo de obtención de descripción CIE-10")
    @Cacheable(value = CACHE_CATALOGOS, key = "'descripcion:' + #codigo?.trim()?.toUpperCase()")
    public String obtenerDescripcion(String codigo) {
        log.debug("Cache miss: Obteniendo descripción para código CIE-10: {}", codigo);
        cacheMissCounter.increment();

        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }

        String codigoNormalizado = codigo.trim().toUpperCase();
        String descripcion = CATALOGO_CIE10.get(codigoNormalizado);

        // La próxima vez será un cache hit
        if (descripcion != null) {
            log.debug("Descripción encontrada para código CIE-10: {}", codigo);
        }

        return descripcion;
    }

    /**
     * Valida un código CIE-10 completo (formato y existencia en catálogo)
     * @param codigo Código CIE-10 a validar
     * @return Lista de errores encontrados (vacía si es válido)
     */
    public List<String> validarCIE10(String codigo) {
        List<String> errores = new ArrayList<>();

        if (codigo == null || codigo.trim().isEmpty()) {
            errores.add("El código CIE-10 es obligatorio");
            return errores;
        }

        String codigoNormalizado = codigo.trim().toUpperCase();

        if (!validarFormatoCIE10(codigoNormalizado)) {
            errores.add("El formato del código CIE-10 es inválido. Debe ser una letra seguida de 2-3 dígitos, opcionalmente con un punto y 1-2 dígitos más");
        }

        if (!existeEnCatalogo(codigoNormalizado)) {
            errores.add("El código CIE-10 no existe en el catálogo o no es válido");
        }

        return errores;
    }

    /**
     * Verifica si un diagnóstico requiere atención especializada basado en su código CIE-10
     * @param codigo Código CIE-10
     * @return true si requiere atención especializada, false en caso contrario
     */
    @Timed(value = "app.cie10.atencion_especializada", description = "Tiempo de verificación de atención especializada")
    @Cacheable(value = CACHE_CATALOGOS, key = "'atencionEspecializada:' + #codigo?.trim()?.toUpperCase()")
    public boolean requiereAtencionEspecializada(String codigo) {
        log.debug("Cache miss: Verificando si código CIE-10 requiere atención especializada: {}", codigo);
        cacheMissCounter.increment();

        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        String codigoBase = codigo.trim().toUpperCase().split("\\.")[0];

        // Códigos que típicamente requieren atención especializada
        List<String> codigosEspecializados = List.of(
            // Cardiovascular
            "I10", "I11", "I20", "I21", "I25", "I50",
            // Endocrinología
            "E10", "E11", "E66",
            // Neurología
            "G20", "G35", "G40",
            // Oncología
            "C00", "C50", "C61", "C34",
            // Nefrología
            "N18", "N19",
            // Neumología
            "J44", "J45", "J84"
        );

        boolean requiere = codigosEspecializados.contains(codigoBase);
        log.debug("Código CIE-10 {} {} atención especializada", codigo, requiere ? "requiere" : "no requiere");
        return requiere;
    }

    /**
     * Verifica si un diagnóstico es compatible con un tratamiento específico
     * @param codigoCIE10 Código CIE-10 del diagnóstico
     * @param medicamento Nombre del medicamento
     * @return true si son compatibles, false en caso contrario
     */
    @Cacheable(value = CACHE_CATALOGOS, key = "'compatibilidad:' + #codigoCIE10?.trim()?.toUpperCase() + ':' + #medicamento?.toLowerCase()")
    public boolean esDiagnosticoCompatibleConTratamiento(String codigoCIE10, String medicamento) {
        log.debug("Cache miss: Verificando compatibilidad entre diagnóstico {} y medicamento {}", codigoCIE10, medicamento);
        if (codigoCIE10 == null || medicamento == null) {
            return false;
        }

        String codigoBase = codigoCIE10.trim().toUpperCase().split("\\.")[0];
        String medicamentoLower = medicamento.toLowerCase();

        // Ejemplos de compatibilidad diagnóstico-tratamiento
        Map<String, List<String>> compatibilidades = new HashMap<>();

        // Hipertensión
        compatibilidades.put("I10", List.of("enalapril", "losartan", "amlodipino", "hidroclorotiazida", "captopril"));

        // Diabetes
        compatibilidades.put("E10", List.of("insulina", "metformina", "glibenclamida", "sitagliptina"));
        compatibilidades.put("E11", List.of("metformina", "glibenclamida", "sitagliptina", "empagliflozina"));

        // Asma/EPOC
        compatibilidades.put("J44", List.of("salbutamol", "ipratropio", "budesonida", "fluticasona", "tiotropio"));
        compatibilidades.put("J45", List.of("salbutamol", "budesonida", "fluticasona", "montelukast"));

        // Infecciones
        List<String> antibioticos = List.of("amoxicilina", "azitromicina", "ciprofloxacino", "ceftriaxona", "penicilina");
        compatibilidades.put("J18", antibioticos); // Neumonía
        compatibilidades.put("N39", antibioticos); // Infección urinaria

        // Dolor/Inflamación
        List<String> analgesicos = List.of("paracetamol", "ibuprofeno", "naproxeno", "diclofenaco");
        compatibilidades.put("M54", analgesicos); // Dorsalgia
        compatibilidades.put("R52", analgesicos); // Dolor no especificado

        // Verificar compatibilidad
        if (compatibilidades.containsKey(codigoBase)) {
            List<String> medicamentosCompatibles = compatibilidades.get(codigoBase);

            return medicamentosCompatibles.stream()
                    .anyMatch(med -> medicamentoLower.contains(med));
        }

        // Si no hay reglas específicas, asumir que es compatible
        return true;
    }
}