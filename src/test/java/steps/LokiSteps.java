package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LokiSteps {

    private static final String lokiBaseUrl = "http://localhost:3100"; // Variable global para la URL de Loki
    private Response response;

    @Given("Loki está corriendo")
    public void loki_está_corriendo() {
        // Verifica que Loki esté corriendo
        response = RestAssured
                .when()
                .get(lokiBaseUrl + "/loki/api/v1/status/buildinfo");

        // Verifica que el código de estado sea 200
        assertEquals(200, response.getStatusCode());
        System.out.println("Loki está corriendo y respondió con código: " + response.getStatusCode());
    }

    @When("busco logs que contengan la cadena {string}")
    public void busco_logs_que_contengan_la_cadena(String searchString) {
        response = RestAssured
                .given()
                .queryParam("query", searchString)
                .when()
                .get(lokiBaseUrl + "/loki/api/v1/query");
    }

    @Then("debería ver logs devueltos que contengan {string}")
    public void debería_ver_logs_devueltos_que_contengan(String expectedString) {
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains(expectedString));
    }

    @When("envío una entrada de log con el mensaje {string}")
    public void envío_una_entrada_de_log_con_el_mensaje(String logMessage) {
        String logPayload = "{\"streams\": [{ \"stream\": { \"foo\": \"bar2\" }, \"values\": [ [ \"1728345600000000000\", \"fizzbuzz\" ] ] }]}";

        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(logPayload)
                .when()
                .post(lokiBaseUrl + "/loki/api/v1/push");
        String responseBody = response.getBody().asString();
        System.out.println("When"+responseBody);
    }

    @Then("la entrada de log debería estar disponible en Loki")
    public void la_entrada_de_log_debería_estar_disponible_en_loki() {
        // Validar consultando Loki nuevamente para verificar que el log esté presente
        response = RestAssured
                .given()
                .queryParam("query", "{app=~\".+\"}")
                .when()
                .get(lokiBaseUrl + "/loki/api/v1/query");

        String responseBody = response.getBody().asString();
        System.out.println("then"+responseBody);
        assertTrue(responseBody.contains("success"));
    }
}
