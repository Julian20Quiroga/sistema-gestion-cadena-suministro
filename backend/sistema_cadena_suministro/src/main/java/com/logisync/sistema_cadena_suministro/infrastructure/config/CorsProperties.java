package com.logisync.sistema_cadena_suministro.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/** CORS settings: app.cors.* (env: APP_CORS_ALLOWED_ORIGINS, ...). Defaults are permissive for development. */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        @DefaultValue("*") List<String> allowedOrigins,
        @DefaultValue({ "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" }) List<String> allowedMethods,
        @DefaultValue("*") List<String> allowedHeaders,
        @DefaultValue("3600") long maxAgeSeconds) {
}
