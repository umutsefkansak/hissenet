package com.infina.hissenet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "HisseNet API",
                version     = "v1.0",
                description = "Hisse, fiyat ve geçmiş verisi CRUD API’leri"
        ),
        servers = {
                @Server(url = "/", description = "Same origin"),
                @Server(url="https://api.hissenet.com", description="Prod")
        }
)

public class OpenApiConfig {
    // Tüm konfigürasyon annotation’lar ile sağlanıyor
}
