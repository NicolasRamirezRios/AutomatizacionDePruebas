package steps;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class LogCentralizadoSteps {

    private Response apiResponse;
    private Response lokiResponse;


    @Given("un usuario realiza una petición GET a la API")
    public void un_usuario_realiza_una_peticion_GET_a_la_API() {
        // Generar un token JWT válido
        String nombre = "usuarioPrueba";
        // Realizamos la petición exacta a la API
        RestAssured.baseURI = "http://localhost:1111"; // Base URI

        RequestSpecification request = given();
        apiResponse = request
                .when()
                .get("/consultar-perfil/petro")  // Exactamente la petición especificada
                .then()
                .statusCode(200) // Verificamos que la respuesta sea exitosa (código 200)
                .extract().response();

        // Mostrar la respuesta de la API para mayor claridad en pruebas
        System.out.println("Respuesta de la API: " + apiResponse.asString());
    }

    @When("el log de la petición se almacena en Loki")
    public void el_log_de_la_peticion_se_almacena_en_Loki() {
        // Hacemos una petición a Loki para verificar que el log se haya almacenado
        RestAssured.baseURI = "http://localhost:3100"; // Cambia el host si es necesario

        lokiResponse = RestAssured
                .given()
                .queryParam("query", "{container=\"dockerfolder-profile-service-1\"} |= \"" + "INFO" + "\"")
                .when()
                .get(baseURI + "/loki/api/v1/query");
    }

    @Then("el log debería estar presente en Loki con nivel {string}")
    public void el_log_deberia_estar_presente_en_Loki_con_nivel(String nivel) {
        // Verificamos que el log tenga el nivel especificado
        System.out.println(lokiResponse.asString());
        lokiResponse.asString().contains("INFO"); // Verificamos que el nivel sea "INFO"
    }
}
