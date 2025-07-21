package com.ug.ec.infrastructure.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

/**
 * Configuración de métricas para la aplicación.
 * Configura métricas personalizadas y aspectos para medir tiempos de ejecución.
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    /**
     * Personaliza el registro de métricas con etiquetas comunes
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "SGHistorialMedico");
    }

    /**
     * Configura el aspecto para la anotación @Timed
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Configura listeners para métricas de MongoDB
     */
    @Bean
    public MongoClientSettings.Builder mongoMetricsSettings(MeterRegistry registry) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.addCommandListener(new MongoMetricsCommandListener(registry));
        builder.applyToConnectionPoolSettings(settings -> 
            settings.addConnectionPoolListener(new MongoMetricsConnectionPoolListener(registry)));
        return builder;
    }

    /**
     * Contador para consultas externas creadas
     */
    @Bean
    public Counter consultaExternaCreatedCounter(MeterRegistry registry) {
        return Counter.builder("app.consulta_externa.created")
                .description("Número de consultas externas creadas")
                .register(registry);
    }

    /**
     * Contador para consultas externas actualizadas
     */
    @Bean
    public Counter consultaExternaUpdatedCounter(MeterRegistry registry) {
        return Counter.builder("app.consulta_externa.updated")
                .description("Número de consultas externas actualizadas")
                .register(registry);
    }

    /**
     * Timer para medir el tiempo de procesamiento de consultas externas
     */
    @Bean
    public Timer consultaExternaProcessingTimer(MeterRegistry registry) {
        return Timer.builder("app.consulta_externa.processing_time")
                .description("Tiempo de procesamiento de consultas externas")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    /**
     * Contador para aciertos de caché
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("app.cache.hits")
                .description("Número de aciertos de caché")
                .tag("cache", "catalogos")
                .register(registry);
    }

    /**
     * Contador para fallos de caché
     */
    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("app.cache.misses")
                .description("Número de fallos de caché")
                .tag("cache", "catalogos")
                .register(registry);
    }
}