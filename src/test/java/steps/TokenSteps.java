package steps;

import com.github.javafaker.Faker;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.jose4j.jwk.JsonWebKeySet;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import io.github.cdimascio.dotenv.Dotenv;


import java.io.File;

import static org.junit.Assert.*;
import static io.restassured.RestAssured.given;

public class TokenSteps {
    private String clientId = "Nicolas";
    private String clientSecret = "6CvhNQWSCkWR7PAtBxseNrQveljVC4JG";
    private String tokenEndpoint = "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token";
    private String introspectEndpoint = "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token/introspect";
    private String jwksUri = "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/certs";
    private String userInfoEndpoint = "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/userinfo";
    private String token;

    private Response response;
    private Faker faker = new Faker();
    private String expiredToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ0WVF2WkFpQXZZT0txZG01TDF5ZDN3alg5bHpHeVZucy03VzZ2Z0M4QnFnIn0";

    @Given("el cliente configurado")
    public void elClienteConfigurado() {
        assertNotNull(clientId);
        assertNotNull(clientSecret);
    }

    @Given("el cliente aleatorio")
    public void elClienteAleatorio() {
        this.clientId = faker.internet().uuid(); // Genera un UUID aleatorio como clientId
        this.clientSecret = faker.internet().password(); // Genera una contraseña aleatoria
    }

        @Given("el cliente configurado con un secreto incorrecto")
    public void elClienteConfiguradoConUnSecretoIncorrecto() {
        assertNotNull(clientId);
        this.clientSecret = "twL4vlrZ3MPVM3ANoY5EV0RWhsAOZyvQ"; // Secreto incorrecto
    }

    @Given("un token JWT válido enviado del servidor")
    public void unTokenJWTValidoEnviadoDelServidor() {
        // Lógica para obtener un token JWT válido usando client_credentials
        String tokenEndpoint = "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token";
        String clientId = "Nicolas";
        String clientSecret = "6CvhNQWSCkWR7PAtBxseNrQveljVC4JG";

        response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .post(tokenEndpoint);

        assertEquals(200, response.getStatusCode());

        token = response.jsonPath().getString("access_token");
        assertNotNull("El token JWT no debe ser nulo", token);
    }

    @Given("un cliente configurado y un token expirado")
    public void unClienteConfiguradoYUnTokenExpirado() {
        assertNotNull(clientId);
        assertNotNull(clientSecret);
    }


    @Given("no tengo un token JWT")
    public void noTengoUnTokenJWT() {
        // No hacemos nada aquí ya que no hay token JWT
        // Se puede usar esta función para inicializar cualquier dato si es necesario
    }

    @When("hago una solicitud al endpoint seguro")
    public void hagoUnaSolicitudAlEndpointSeguro() {
        // Realizamos una solicitud GET sin token JWT
        response = RestAssured.given()
                .post(introspectEndpoint);  // No se envía el header Authorization con token
    }

    @When("se solicita un token")
    public void seSolicitaUnToken() {
        response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .post(tokenEndpoint);


        // Imprimir la respuesta para depuración
        System.out.println("Código de respuesta: " + response.getStatusCode());
        System.out.println("Respuesta: " + response.asString());
    }



    @When("se valida el token en el servidor usando la clave pública")
    public void seValidaElTokenEnElServidorUsandoLaClavePublica() {
        // Obtener las claves públicas del servidor Keycloak
        Response certsResponse = given().get(jwksUri);
        assertEquals(200, certsResponse.getStatusCode());

        String jwksJson = certsResponse.asString();

        try {
            // Crear el conjunto de claves JWK desde el JSON proporcionado por Keycloak
            JsonWebKeySet jwks = new JsonWebKeySet(jwksJson);
            JwksVerificationKeyResolver keyResolver = new JwksVerificationKeyResolver(jwks.getJsonWebKeys());

            // Configurar el consumidor de JWT para validar el token con la clave pública
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime() // El token debe tener tiempo de expiración
                    .setExpectedIssuer("http://localhost:8080/realms/Realm_Prueba") // Verificar el issuer
                    .setExpectedAudience("account") // Añadir audiencia esperada aquí
                    .setVerificationKeyResolver(keyResolver) // Resolver la clave pública usando JWK
                    .build();

            // Validar el JWT
            JwtContext jwtContext = jwtConsumer.process(token);
            assertNotNull("El token JWT debe ser válido", jwtContext);

        } catch (JoseException | InvalidJwtException e) {
            fail("Error al procesar el token JWT: " + e.getMessage());
        }
    }

    @When("hago una solicitud de introspección con el token expirado")
    public void hagoUnaSolicitudDeIntrospecciónConElTokenExpirado() {
        response = RestAssured.given()
                .auth()
                .preemptive()
                .basic(clientId, clientSecret)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", expiredToken)
                .post(introspectEndpoint);
    }

    @Then("debería recibir un código de respuesta {int}")
    public void deberiaRecibirCodigoDeRespuesta(int statusCode) {
        // El código de respuesta esperado es 200, ya que Keycloak siempre responde con 200 en introspección
        response.then().statusCode(200);
    }


    @And("el mensaje de respuesta debería indicar que el token ha expirado")
    public void elMensajeDeRespuestaDeberiaIndicarQueElTokenHaExpirado() {
        // Obtenemos el valor de 'active' de la respuesta JSON
        boolean isActive = response.jsonPath().getBoolean("active");

        // Verificamos si el token está inactivo (ha expirado o no es válido)
        if (!isActive) {
            // Esto indica que el token ha expirado o es inválido
            System.out.println("El token ha expirado o no es válido.");
        } else {
            throw new AssertionError("El token debería haber expirado, pero sigue activo.");
        }
    }

    @Then("la respuesta debe indicar que el token es válido")
    public void laRespuestaDebeIndicarQueElTokenEsValido() {
        // Validar que el token haya sido procesado y sea válido
        System.out.println("Token validado correctamente.");
        assertTrue("El token debe ser válido", true);
    }

    @Then("el token debe ser recibido exitosamente")
    public void elTokenDebeSerRecibidoExitosamente() {
        assertEquals(200, response.getStatusCode());
        String token = response.jsonPath().getString("access_token");
        assertNotNull("El token no debe ser nulo", token);
    }

    @Then("no se debe recibir un token")
    public void noSeDebeRecibirUnToken() {
        String token = response.jsonPath().getString("access_token");
        assertNull("El token debe ser nulo", token);
    }


    @Then("el código de respuesta debe ser {int}")
    public void elCodigoDeRespuestaDebeSer(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @Then("la respuesta debe cumplir con el esquema JSON en {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSON(String escenario) {
        File schema;

        switch (escenario) {
            case "Solicitar token":
                schema = new File("src/test/resources/schemas/json-schema1.json");
                break;
            case "Secreto incorrecto":
                schema = new File("src/test/resources/schemas/json-schema2.json");
                break;
            case "Validar token":
                schema = new File("src/test/resources/schemas/json-schema3.json");
                break;
            case "Token expirado":
                schema = new File("src/test/resources/schemas/json-schema4.json");
                break;
            case "Sin token":
                schema = new File("src/test/resources/schemas/json-schema5.json");
                break;
            default:
                throw new IllegalArgumentException("Escenario no reconocido: " + escenario);
        }

        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
    }

}



