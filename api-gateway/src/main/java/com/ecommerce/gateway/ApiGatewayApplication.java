package com.ecommerce.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}

@Tag(name = "Payment", description = "Gestion des transactions de paiement")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Operation(
        summary = "Initier un nouveau paiement",
        description = "Permet de créer une intention de paiement via Stripe ou PayPal."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paiement créé avec succès",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = PaymentResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Données de requête invalides"),
        @ApiResponse(responseCode = "401", description = "Authentification JWT échouée")
    })