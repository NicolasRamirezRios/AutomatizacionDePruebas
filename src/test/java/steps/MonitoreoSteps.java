package steps;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class MonitoreoSteps {

    private String prometheusBaseUrl;
    private Response response;

    @Given("el servicio de Prometheus está activo")
    public void el_servicio_de_Prometheus_está_activo_en() {
        prometheusBaseUrl = "http://prometheus:9090";
    }

    @When("hago una solicitud GET al endpoint runtimeinfo")
    public void hago_una_solicitud_GET_al_endpoint() {
        response = given().baseUri(prometheusBaseUrl).when().get(prometheusBaseUrl+"/api/v1/status/runtimeinfo");
    }

    @When("hago una solicitud GET al endpoint query con el parámetro {string}")
    public void hago_una_solicitud_GET_al_endpoint_con_el_parámetro(String query) {
        response = given().baseUri(prometheusBaseUrl).param("query", query).when().get("/api/v1/query");
        System.out.println(response.getBody().asString());
    }

    @Then("la respuesta tiene un código de estado {int}")
    public void la_respuesta_tiene_un_código_de_estado(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("la respuesta contiene información sobre el tiempo de ejecución")
    public void la_respuesta_contiene_información_sobre_el_tiempo_de_ejecución() {
        response.then().body("data", notNullValue());
    }

    @Then("la respuesta contiene datos de las métricas recolectadas")
    public void la_respuesta_contiene_datos_de_las_métricas_recolectadas() {
        response.then().body("data.result", notNullValue());
        System.out.println(response.getBody().asString());
    }

    @When("realizo una consulta para verificar el estado de los servicios")
    public void queryServiceStatus() {
        response = given()
                .baseUri(prometheusBaseUrl)
                .queryParam("query", "up")
                .when()
                .get("/api/v1/query")
                .then()
                .extract()
                .response();
    }

    @Then("todos los servicios deben estar en estado \"up\"")
    public void validateServicesAreUp() {

        Assertions.assertEquals(200, response.getStatusCode(), "La consulta a Prometheus no fue exitosa.");

        // Extraer la lista de métricas en el resultado, con tipo explícito
        List<Map<String, Object>> metrics = response.jsonPath().getList("data.result");

        // Verificar que el segundo valor en "value" sea "1" para cada métrica
        boolean allServicesUp = metrics.stream()
                .allMatch(metric -> {
                    List<Object> valueList = (List<Object>) metric.get("value");
                    return "1".equals(valueList.get(1).toString());
                });

        System.out.println("¿Todos los servicios están activos? " + allServicesUp);
        System.out.println(response.getBody().asString());
        Assertions.assertTrue(allServicesUp, "No todos los servicios están en estado 'up'.");
    }
}
