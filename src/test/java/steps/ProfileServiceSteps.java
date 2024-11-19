package steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class ProfileServiceSteps {

    private String baseUrl = "http://profile-service:8002"; // Cambia por la URL de tu servicio FastAPI
    private Response response;
    private String tokenValido = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4M1kzY2pERVZiZ2ktTHNNZ0pSbTJndWpTU3VmSGE0OHBvWFoxelZqa2EwIn0.eyJleHAiOjE3MzE5MjI3MjIsImlhdCI6MTczMTkyMjQyMiwianRpIjoiZjEzYzFjNTItODNlYi00MmVkLWIyYzYtOGRjOTI0MDg1ZmRkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9maW5hbCIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIxMzc2MjEzYS02Y2I2LTRlNDYtOGRlMy04NzE5M2VmZDRlYjMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnby1hdXRoLXNlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiNmZhODUxN2MtM2Y3ZS00NWExLThiNmUtZjZlMDgxZWU0ZGUyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1maW5hbCIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI2ZmE4NTE3Yy0zZjdlLTQ1YTEtOGI2ZS1mNmUwODFlZTRkZTIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InBldHJvIiwiZ2l2ZW5fbmFtZSI6IiIsImZhbWlseV9uYW1lIjoiIn0.nO8t9_kwEBLaL1VKkp8UK9uaDdGDpTmh2A7IE2rdZnmvF26tnXKRy05s00I1CAPM5LFNAALfjGP39Y1qtVh_jpcJ3mVSu9Pqf4q70K5NakcCtA_Y69weLdrFkn7aKdZLAmPNwOJZUwi_q2l24NydzytER_CO1eXWnqxevPYuoIfkqIquSw7Knd2QkxMz13rHr_1ydKGQU1FGA0VrR3-_wzyqV-PH0hkpXugnjqsN1KKTZ-RiZHQ45nmRGmfKg3HDE9-1XLcU7mNwGaasNlQnaXxRwnW6ddXz6GXh7tMIspq3FRDcATH1-tP_v2QVlOP_4eJwbQewOurtjzxhtVtJ4w";  // Un JWT token válido para pruebas


    @Given("El servicio de perfiles está activo")
    public void el_servicio_de_perfiles_está_activo() {
        // Aquí se puede hacer una comprobación inicial, como un ping o verificar la conexión
    }

    @When("Realizo una solicitud GET al endpoint {string}")
    public void realizo_una_solicitud_GET_al_endpoint(String endpoint) {
        response = given().baseUri(baseUrl)
                .when().get(endpoint);
    }

    @When("Realizo una solicitud POST al endpoint {string} con el siguiente cuerpo:")
    public void realizo_una_solicitud_POST_al_endpoint_con_el_siguiente_cuerpo(String endpoint, String requestBody) {
        response = given().baseUri(baseUrl)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenValido)
                .body(requestBody)
                .when().post(endpoint);
    }

    @Then("La respuesta tiene un estado {int}")
    public void la_respuesta_tiene_un_código_de_estado(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Then("La respuesta contiene el {string} {string}")
    public void la_respuesta_contiene_el_mensaje(String tipo, String expectedMessage) {
        response.then().body(tipo, equalTo(expectedMessage));
    }

    @Then("El campo {string} tiene el valor {string}")
    public void el_campo_tiene_el_valor(String fieldName, String expectedValue) {
        response.then().body(fieldName, equalTo(expectedValue));
    }

}
