package com.microservice.BookingService.apiConfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Booking Microservice API",                    // API Title
                version = "v1",                                  // API Version
                description = "API for flight booking system",    // API Description
                termsOfService = "http://example.com/terms",      // Terms of service URL (optional)
                contact = @Contact(
                        name = "Vinay Jadaun",                           // Contact Person Name
                        email = "vinayjadaun11112003@example.com",               // Contact Email
                        url = "https://vinayjadaun-com.vercel.app/"                   // Contact URL
                ),
                license = @License(
                        name = "MIT",                                // License Name
                        url = "http://opensource.org/licenses/MIT"   // License URL
                )
        )
)
public class OpenApiConfig {
    // No methods required here; this class just configures OpenAPI metadata.
}
