package com.ug.ec.domain.consultaexterna.services;

import com.ug.ec.domain.consultaexterna.valueobjects.Prescripcion;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Servicio que verifica interacciones medicamentosas entre prescripciones
 */
@Slf4j
@Service
public class InteraccionesMedicamentosService {

    // Mapa que relaciona medicamentos con sus posibles interacciones
    private final Map<String, Set<String>> interaccionesMedicamentosas;
    
    // Mapa que relaciona medicamentos con sus descripciones de interacción
    private final Map<String, String> descripcionesInteracciones;
    
    public InteraccionesMedicamentosService() {
        // Inicializar mapas con reglas predefinidas
        this.interaccionesMedicamentosas = inicializarInteraccionesMedicamentosas();
        this.descripcionesInteracciones = inicializarDescripcionesInteracciones();
        log.info("InteraccionesMedicamentosService inicializado con {} reglas de interacción", 
                interaccionesMedicamentosas.size());
    }
    
    /**
     * Verifica interacciones entre una nueva prescripción y las prescripciones existentes
     * @param nuevaPrescripcion Nueva prescripción a verificar
     * @param prescripcionesExistentes Lista de prescripciones existentes
     * @return Lista de descripciones de interacciones encontradas
     */
    public List<String> verificarInteracciones(Prescripcion nuevaPrescripcion, 
                                              List<Prescripcion> prescripcionesExistentes) {
        if (nuevaPrescripcion == null || prescripcionesExistentes == null || prescripcionesExistentes.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> interaccionesEncontradas = new ArrayList<>();
        String nuevoMedicamento = normalizarNombreMedicamento(nuevaPrescripcion.getMedicamento());
        
        // Verificar interacciones con cada prescripción existente
        for (Prescripcion prescripcionExistente : prescripcionesExistentes) {
            String medicamentoExistente = normalizarNombreMedicamento(prescripcionExistente.getMedicamento());
            
            // Verificar si hay interacción entre los medicamentos
            if (tienenInteraccion(nuevoMedicamento, medicamentoExistente)) {
                String descripcionInteraccion = obtenerDescripcionInteraccion(nuevoMedicamento, medicamentoExistente);
                interaccionesEncontradas.add(descripcionInteraccion);
                
                log.warn("Interacción medicamentosa detectada: {} con {}", 
                        nuevaPrescripcion.getMedicamento(), prescripcionExistente.getMedicamento());
            }
        }
        
        return interaccionesEncontradas;
    }
    
    /**
     * Verifica si dos medicamentos tienen interacción
     * @param medicamento1 Primer medicamento
     * @param medicamento2 Segundo medicamento
     * @return true si hay interacción, false en caso contrario
     */
    private boolean tienenInteraccion(String medicamento1, String medicamento2) {
        // Verificar si el medicamento1 tiene interacción con el medicamento2
        Set<String> interaccionesMed1 = interaccionesMedicamentosas.get(medicamento1);
        if (interaccionesMed1 != null && interaccionesMed1.contains(medicamento2)) {
            return true;
        }
        
        // Verificar si el medicamento2 tiene interacción con el medicamento1
        Set<String> interaccionesMed2 = interaccionesMedicamentosas.get(medicamento2);
        return interaccionesMed2 != null && interaccionesMed2.contains(medicamento1);
    }
    
    /**
     * Obtiene la descripción de la interacción entre dos medicamentos
     * @param medicamento1 Primer medicamento
     * @param medicamento2 Segundo medicamento
     * @return Descripción de la interacción
     */
    private String obtenerDescripcionInteraccion(String medicamento1, String medicamento2) {
        // Buscar la descripción específica para esta combinación de medicamentos
        String clave = medicamento1 + "-" + medicamento2;
        String descripcion = descripcionesInteracciones.get(clave);
        
        // Si no hay descripción específica, buscar la combinación inversa
        if (descripcion == null) {
            clave = medicamento2 + "-" + medicamento1;
            descripcion = descripcionesInteracciones.get(clave);
        }
        
        // Si aún no hay descripción, usar una genérica
        if (descripcion == null) {
            descripcion = "Posible interacción entre " + medicamento1 + " y " + medicamento2 + 
                         ". Consulte con un farmacéutico.";
        }
        
        return descripcion;
    }
    
    /**
     * Normaliza el nombre de un medicamento para búsquedas
     * @param nombreMedicamento Nombre del medicamento
     * @return Nombre normalizado
     */
    private String normalizarNombreMedicamento(String nombreMedicamento) {
        if (nombreMedicamento == null) {
            return "";
        }
        
        // Convertir a minúsculas y eliminar espacios al inicio y final
        String normalizado = nombreMedicamento.toLowerCase().trim();
        
        // Eliminar información de dosis o presentación (entre paréntesis)
        int parentesisIndex = normalizado.indexOf('(');
        if (parentesisIndex > 0) {
            normalizado = normalizado.substring(0, parentesisIndex).trim();
        }
        
        return normalizado;
    }
    
    /**
     * Inicializa el mapa de interacciones medicamentosas
     * @return Mapa inicializado
     */
    private Map<String, Set<String>> inicializarInteraccionesMedicamentosas() {
        Map<String, Set<String>> mapa = new HashMap<>();
        
        // Interacciones con warfarina
        mapa.put("warfarina", new HashSet<>(Arrays.asList(
            "aspirina", "ibuprofeno", "naproxeno", "diclofenaco", "ketorolaco", // AINEs
            "fluconazol", "itraconazol", "ketoconazol", // Antifúngicos
            "ciprofloxacino", "levofloxacino", "moxifloxacino", // Fluoroquinolonas
            "amiodarona", "propafenona", // Antiarrítmicos
            "omeprazol", "esomeprazol", // Inhibidores de la bomba de protones
            "simvastatina", "atorvastatina", // Estatinas
            "carbamazepina", "fenitoína", "fenobarbital" // Anticonvulsivantes
        )));
        
        // Interacciones con inhibidores de la MAO
        mapa.put("selegilina", new HashSet<>(Arrays.asList(
            "fluoxetina", "paroxetina", "sertralina", "escitalopram", "citalopram", // ISRS
            "venlafaxina", "duloxetina", // IRSN
            "tramadol", "meperidina", "morfina", // Opioides
            "pseudoefedrina", "fenilefrina" // Descongestionantes
        )));
        mapa.put("tranilcipromina", mapa.get("selegilina"));
        mapa.put("fenelzina", mapa.get("selegilina"));
        
        // Interacciones con antibióticos macrólidos
        mapa.put("eritromicina", new HashSet<>(Arrays.asList(
            "simvastatina", "atorvastatina", "lovastatina", // Estatinas
            "warfarina", // Anticoagulantes
            "carbamazepina", // Anticonvulsivantes
            "digoxina", // Cardiotónicos
            "sildenafil", "tadalafil", "vardenafil" // Inhibidores de la PDE-5
        )));
        mapa.put("claritromicina", mapa.get("eritromicina"));
        mapa.put("azitromicina", new HashSet<>(Arrays.asList(
            "warfarina", // Anticoagulantes
            "digoxina" // Cardiotónicos
        )));
        
        // Interacciones con estatinas
        mapa.put("simvastatina", new HashSet<>(Arrays.asList(
            "eritromicina", "claritromicina", // Macrólidos
            "itraconazol", "ketoconazol", // Antifúngicos
            "ciclosporina", // Inmunosupresores
            "gemfibrozilo", "fenofibrato", // Fibratos
            "amiodarona", // Antiarrítmicos
            "verapamilo", "diltiazem" // Bloqueadores de canales de calcio
        )));
        mapa.put("atorvastatina", new HashSet<>(Arrays.asList(
            "eritromicina", "claritromicina", // Macrólidos
            "itraconazol", "ketoconazol", // Antifúngicos
            "ciclosporina", // Inmunosupresores
            "gemfibrozilo", "fenofibrato" // Fibratos
        )));
        
        // Interacciones con digoxina
        mapa.put("digoxina", new HashSet<>(Arrays.asList(
            "amiodarona", // Antiarrítmicos
            "verapamilo", "diltiazem", // Bloqueadores de canales de calcio
            "espironolactona", // Diuréticos ahorradores de potasio
            "eritromicina", "claritromicina", "azitromicina" // Macrólidos
        )));
        
        // Interacciones con benzodiacepinas
        mapa.put("diazepam", new HashSet<>(Arrays.asList(
            "fluconazol", "itraconazol", "ketoconazol", // Antifúngicos
            "eritromicina", "claritromicina", // Macrólidos
            "cimetidina", // Antihistamínicos H2
            "omeprazol", // Inhibidores de la bomba de protones
            "alcohol" // Alcohol
        )));
        mapa.put("alprazolam", mapa.get("diazepam"));
        mapa.put("midazolam", mapa.get("diazepam"));
        
        return mapa;
    }
    
    /**
     * Inicializa el mapa de descripciones de interacciones
     * @return Mapa inicializado
     */
    private Map<String, String> inicializarDescripcionesInteracciones() {
        Map<String, String> mapa = new HashMap<>();
        
        // Descripciones de interacciones específicas
        mapa.put("warfarina-aspirina", "La aspirina aumenta el riesgo de sangrado cuando se usa con warfarina. " +
                                      "Considere un analgésico alternativo como el paracetamol.");
        
        mapa.put("warfarina-ibuprofeno", "El ibuprofeno aumenta el riesgo de sangrado cuando se usa con warfarina. " +
                                        "Considere un analgésico alternativo como el paracetamol.");
        
        mapa.put("warfarina-fluconazol", "El fluconazol puede aumentar significativamente el efecto anticoagulante " +
                                        "de la warfarina, incrementando el riesgo de sangrado. Se recomienda " +
                                        "monitorización estrecha del INR y posible ajuste de dosis.");
        
        mapa.put("simvastatina-eritromicina", "La eritromicina puede aumentar significativamente los niveles de " +
                                            "simvastatina, incrementando el riesgo de miopatía y rabdomiólisis. " +
                                            "Considere suspender temporalmente la simvastatina o usar una estatina " +
                                            "alternativa como rosuvastatina.");
        
        mapa.put("digoxina-amiodarona", "La amiodarona aumenta los niveles séricos de digoxina, incrementando el " +
                                       "riesgo de toxicidad digitálica. Se recomienda reducir la dosis de digoxina " +
                                       "en aproximadamente un 50% y monitorizar los niveles séricos.");
        
        mapa.put("selegilina-fluoxetina", "La combinación de inhibidores de la MAO como selegilina con ISRS como " +
                                         "fluoxetina puede provocar síndrome serotoninérgico, una condición " +
                                         "potencialmente mortal. Estas medicaciones no deben combinarse.");
        
        return mapa;
    }
}