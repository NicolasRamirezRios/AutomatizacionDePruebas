package steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class NotificationSteps {

    private Response response;
    private String destinatario;
    private String mensaje;
    private String canal;
    private long notificationId;

    @Given("un destinatario de notificación {string}")
    public void aNotificationRecipient(String recipient) {
        destinatario = recipient;
        RestAssured.baseURI = "http://localhost:9070/notifications";
    }

    @Given("un mensaje de notificación {string}")
    public void aNotificationMessage(String message) {
        mensaje = message;
    }

    @Given("un canal de notificación {string}")
    public void aNotificationChannel(String channel) {
        canal = channel;
    }

    @When("envío la notificación")
    public void iSendTheNotification() {
        response = given()
                .queryParam("recipient", destinatario)
                .queryParam("message", mensaje)
                .queryParam("channel", canal)
                .post("/send");

        // Obtener el ID de la notificación recién creada si es necesario en futuros pasos
        notificationId = response.jsonPath().getLong("id");
    }

    @Then("estado de la respuesta debe ser {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("la respuesta debe contener los detalles de la notificación")
    public void theResponseShouldContainTheNotificationDetails() {
        response.then()
                .body("recipient", equalTo(destinatario))
                .body("message", equalTo(mensaje))
                .body("channel", equalTo(canal));
        System.out.println(response.getBody().asString());
    }

    @When("solicito todas las notificaciones")
    public void iRequestAllNotifications() {
        response = given().get();
    }

    @Then("la respuesta debe contener una lista de notificaciones")
    public void theResponseShouldContainAListOfNotifications() {
        response.then().body("$", is(not(empty())));
        System.out.println(response.getBody().asString());
    }

    @Given("una notificación ID {long}")
    public void aNotificationID(long id) {
        notificationId = id;
    }

    @When("solicito la notificación por ID")
    public void iRequestTheNotificationByID() {
        response = given().get("/" + notificationId);
    }

    @Then("la respuesta debe contener la notificación con ID {long}")
    public void theResponseShouldContainTheNotificationWithID(long id) {
        response.then().body("id", equalTo((int) id));
        System.out.println(response.getBody().asString());
    }
}
