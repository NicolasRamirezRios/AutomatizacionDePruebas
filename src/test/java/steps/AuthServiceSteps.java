package steps;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceSteps {

    private String authServiceBaseUrl = "http://localhost:8004"; // URL base del servicio de autenticación
    private Response response;

    @Given("el servicio de autenticación está activo")
    public void el_servicio_de_autenticacion_esta_activo() {
        // Aquí simplemente aseguramos que la URL base esté configurada
        // Puedes agregar un paso para verificar la salud del servicio si es necesario
    }

    @When("registro un nuevo usuario con los siguientes datos")
    public void registro_un_nuevo_usuario_con_los_siguientes_datos(Map<String, String> userData) {
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
                .baseUri(authServiceBaseUrl)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/register");
    }

    @When("inicio sesión con el usuario {string} y la contraseña {string}")
    public void inicio_sesion_con_el_usuario_y_la_contraseña(String username, String password) {
        // Crear el cuerpo de la solicitud en x-www-form-urlencoded
        Map<String, String> formParams = new HashMap<>();
        formParams.put("username", username);
        formParams.put("password", password);

        // Enviar solicitud POST al endpoint de login
        response = given()
                .baseUri(authServiceBaseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParams(formParams)
                .when()
                .post("/login");
    }

    @Then("la respuesta del servicio de autenticación tiene un código de estado {int}")
    public void la_respuesta_del_servicio_de_autenticacion_tiene_un_codigo_de_estado(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("el mensaje de éxito es {string}")
    public void el_mensaje_de_exito_es(String message) {
        response.then().body("message", equalTo(message));
    }

    @Then("el token de acceso es devuelto")
    public void el_token_de_acceso_es_devuelto() {
        response.then().body("access_token", notNullValue());
    }

    @Then("el código de error es {int} y el mensaje es {string}")
    public void el_codigo_de_error_es_y_el_mensaje_es(int errorCode, String errorMessage) {
        response.then().statusCode(errorCode).body("message", equalTo(errorMessage));
    }
}
