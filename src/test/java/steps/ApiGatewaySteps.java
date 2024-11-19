package steps;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ApiGatewaySteps {

    private String apiGatewayBaseUrl = "http://kong:1111"; // API Gateway
    private Response response;
    private String accessToken;

    @Given("el API Gateway está activo")
    public void el_api_gateway_esta_activo() {
        // Validar que el API Gateway responda
        given()
                .baseUri(apiGatewayBaseUrl)
                .when()
                .get("/ping")
                .then()
                .statusCode(200);
    }

    @When("registro un nuevo usuario en el servicio de autenticación con los siguientes datos")
    public void registro_un_nuevo_usuario_en_el_servicio_de_autenticacion(Map<String, String> userData) {
        // Mapear los datos del usuario a un JSON
        Map<String, Object> body = new HashMap<>();
        body.put("username", userData.get("username"));
        body.put("firstName", userData.get("firstName"));
        body.put("lastName", userData.get("lastName"));
        body.put("email", userData.get("email"));
        body.put("enabled", Boolean.parseBoolean(userData.get("enabled")));
        body.put("password", userData.get("password"));

        // Enviar solicitud POST al endpoint de registro
        response = given()
                .baseUri(apiGatewayBaseUrl)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/register");
    }


    @When("inicio sesión a través del API Gateway con el usuario {string} y la contraseña {string}")
    public void inicio_sesion_a_traves_del_api_gateway(String username, String password) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("username", username);
        formParams.put("password", password);

        try {
            response = given()
                    .baseUri(apiGatewayBaseUrl)
                    .contentType("application/x-www-form-urlencoded")
                    .formParams(formParams)
                    .when()
                    .post("/login");

            // Validar que la respuesta no sea nula
            if (response != null && response.getStatusCode() == 200) {
                System.out.println("todo joya papi");
                accessToken = response.jsonPath().getString("access_token");
            } else {
                System.out.println("Error en la solicitud: Código de estado " + (response != null ? response.getStatusCode() : "sin respuesta"));
            }
        } catch (Exception e) {
            System.out.println("Error durante la solicitud: " + e.getMessage());
        }
    }

    @When("veo el perfil vacío para el usuario {string}")
    public void veo_el_perfil_vacio_para_el_usuario_registrado(String usuario) {
        response = given()
                .baseUri(apiGatewayBaseUrl)
                .contentType(ContentType.JSON)
                .when()
                .get("/consultar-perfil/" + usuario);
    }

    @Then("la respuesta dio un estado {int}")
    public void la_respuesta_tiene_un_codigo_de_estado(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("el mensaje exitoso es {string}")
    public void el_mensaje_de_exito_es(String message) {
        response.then().body("message", equalTo(message));
    }

    @Then("el token de acceso devuelve")
    public void el_token_de_acceso_devuelve() {
        response.then().body("access_token", notNullValue());
    }
}