package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.Map;



public class LokiSteps {

    private static final String lokiBaseUrl = "http://localhost:3100"; // Variable global para la URL de Loki
    private Response response;
    private String logsJson;
    private Map<String, String> etiquetas;
    private String mensajeEsperado;

    @Given("un conjunto de logs en formato JSON")
    public void unConjuntoDeLogsEnFormatoJSON() {
        // Crear un objeto JSON con los logs
         logsJson = "{\"stream\":\"test_stream\",\"values\":[[1640995200, \"info\", \"mensaje de log\"]]}\n";
    }

    @When("envío los logs a Loki")
    public void envíoLosLogsALoki() {
         response = given()
                .contentType(ContentType.JSON)
                .body(logsJson)
                .post(lokiBaseUrl+"/loki/api/v1/push");

        System.out.println(response.getBody().asString());
    }

    @And("el código de estado debe ser {int}")
    public void elCódigoDeEstadoDebeSer(int arg0) {
        this.response.then().statusCode(arg0);
    }

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
    @When("envío un log de prueba con la cadena {string}")
    public void envío_un_log_de_prueba_con_la_cadena(String searchString) {
        // JSON del log de prueba que se enviará a Loki
        String logJson = """
            {
              "streams": [
                {
                  "stream": { "job": "status" },
                  "values": [
                    ["%s", "Este es un log de prueba con la palabra %s"]
                  ]
                }
              ]
            }
            """.formatted(String.valueOf(System.currentTimeMillis() * 1000000), searchString);

        // Envía el log a Loki
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(logJson)
                .post(lokiBaseUrl + "/loki/api/v1/push");

        // Verifica que la respuesta sea 204, lo que indica éxito en el envío
        assertEquals(204, response.getStatusCode());
        System.out.println("Log enviado a Loki con respuesta: " + response.getStatusCode());
    }

    @And("busco logs que contengan la cadena {string}")
    public void busco_logs_que_contengan_la_cadena(String searchString) {
        response = RestAssured
                .given()
                .queryParam("query", "{job=\"status\"} |= \"" + searchString + "\"")
                .when()
                .get(lokiBaseUrl + "/loki/api/v1/query");

        System.out.println(response.getBody().asString());
    }


    @Then("debería ver logs devueltos que contengan {string}")
    public void debería_ver_logs_devueltos_que_contengan(String expectedString) {
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains(expectedString));
    }

    @When("envío una solicitud para preparar a Loki para el cierre")
    public void envio_solicitud_para_preparar_a_loki_para_el_cierre() {
        // Envía una solicitud POST al endpoint para preparar el cierre
        response = RestAssured
                .when()
                .post(lokiBaseUrl + "/ingester/prepare_shutdown");

        System.out.println("Respuesta de preparación de cierre: " + response.getStatusCode());
    }

    @Then("Loki debería estar preparado para el cierre sin pérdida de datos")
    public void loki_deberia_estar_preparado_para_el_cierre_sin_perdida_de_datos() {
        // Verifica que el código de estado sea 204, lo que indica que la operación fue exitosa
        assertEquals(204, response.getStatusCode());
        System.out.println("Loki está preparado para el cierre con código: " + response.getStatusCode());
    }

    @When("solicito las métricas de Loki")
    public void solicito_las_metricas_de_loki() {
        // Envía una solicitud GET al endpoint de métricas
        response = RestAssured
                .when()
                .get(lokiBaseUrl + "/metrics");

        System.out.println("Respuesta de métricas de Loki: " + response.getBody().asString());
    }

    @Then("debería recibir las métricas correctamente")
    public void deberia_recibir_las_metricas_correctamente() {
        // Verifica que el código de estado de la respuesta sea 200
        assertThat("El código de estado no es 200", response.getStatusCode(), equalTo(200));

        // Verifica que la respuesta contenga algunas métricas clave, como 'loki_'
        String responseBody = response.getBody().asString();
        assertThat("El cuerpo de la respuesta no contiene 'loki_' como se esperaba.", responseBody, containsString("loki_"));
        System.out.println("Las métricas de Loki fueron recibidas correctamente.");
    }

    @When("verifico la configuración actual del sistema")
    public void verifico_la_configuracion_actual_del_sistema() {
        // Envía una solicitud GET al endpoint de configuración
        response = RestAssured
                .when()
                .get(lokiBaseUrl + "/config");

        System.out.println("Respuesta de configuración de Loki: " + response.getBody().asString());
    }

    @Then("la respuesta debe incluir la configuración actual del sistema")
    public void la_respuesta_debe_incluir_la_configuracion_actual_del_sistema() {
        // Verifica que el código de estado de la respuesta sea 200
        assertThat("El código de estado no es 200", response.getStatusCode(), equalTo(200));

        // Verifica que la respuesta contenga la configuración esperada
        String responseBody = response.getBody().asString();
        assertThat("La respuesta no incluye la configuración esperada.", responseBody, containsString("config"));
        System.out.println("La configuración actual del sistema fue recibida correctamente.");
    }

}
