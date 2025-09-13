package com.example.auction.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

@Configuration
public class JacksonConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.postConfigurer(objectMapper -> {
            objectMapper.coercionConfigFor(LogicalType.Integer)
                    .setCoercion(CoercionInputShape.String, CoercionAction.Fail);
            objectMapper.coercionConfigFor(LogicalType.Float)
                    .setCoercion(CoercionInputShape.String, CoercionAction.Fail);
        });
    }
}
