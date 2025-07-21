package com.ug.ec.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché para la aplicación.
 * Utiliza Caffeine como proveedor de caché por su alto rendimiento.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Nombres de las cachés utilizadas en la aplicación
     */
    public static final String CACHE_CONSULTAS_POR_CEDULA = "consultasPorCedula";
    public static final String CACHE_CONSULTAS_POR_HISTORIA_CLINICA = "consultasPorHistoriaClinica";
    public static final String CACHE_CONSULTAS_POR_MEDICO = "consultasPorMedico";
    public static final String CACHE_CATALOGOS = "catalogos";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configurar las cachés con diferentes políticas según su uso
        cacheManager.setCacheNames(Arrays.asList(
                CACHE_CONSULTAS_POR_CEDULA,
                CACHE_CONSULTAS_POR_HISTORIA_CLINICA,
                CACHE_CONSULTAS_POR_MEDICO,
                CACHE_CATALOGOS
        ));
        
        // Configuración por defecto para las cachés
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());
        
        return cacheManager;
    }
    
    /**
     * Caché específica para catálogos con tiempo de expiración más largo
     * ya que estos datos cambian con menos frecuencia
     */
    @Bean
    public Caffeine<Object, Object> catalogosCacheSpec() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
                .recordStats();
    }
}