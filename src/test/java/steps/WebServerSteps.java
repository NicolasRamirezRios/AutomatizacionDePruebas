package steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.*;

public class WebServerSteps {

    private Response response;

    @Given("el servidor HTTP está corriendo en el puerto {int}")
    public void el_servidor_http_está_corrido_en_el_puerto(int port) {
        // Configurar RestAssured para apuntar al servidor
        RestAssured.baseURI = "http://localhost:8000/";
        RestAssured.port = port;
    }


    @When("realizo una solicitud GET a {string}")
    public void realizo_una_solicitud_get_a(String endpoint) {
        // Realizar la solicitud GET
        response = RestAssured.given()
                .when()
                .get(endpoint);
    }

    @Then("la respuesta tiene un código de estado {string}")
    public void la_respuesta_tiene_un_codigo_de_estado(String statusCode) {
        // Verificar el código de estado
        response.then().statusCode(Integer.parseInt(statusCode.split(" ")[0]));
    }


}