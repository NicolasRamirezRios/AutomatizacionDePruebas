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

public class webServerSteps {

    private Response response;

    @Given("el servidor HTTP está corriendo en el puerto {int}")
    public void el_servidor_http_está_corrido_en_el_puerto(int port) {
        // Configurar RestAssured para apuntar al servidor
        RestAssured.baseURI = "http://localhost:8000/";
        RestAssured.port = port;
    }

    @Given("la base de datos tiene usuarios registrados")
    public void la_base_de_datos_tiene_usuarios_registrados() throws SQLException {
        // Verificar que la base de datos tiene usuarios
        if (!hayUsuariosEnLaBaseDeDatos()) {
            throw new SQLException("No hay usuarios registrados en la base de datos");
        }
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

    @And("la respuesta contiene una lista de usuarios con sus ID, nombres y fechas de registro")
    public void la_respuesta_contiene_una_lista_de_usuarios() {
        // Verificar que la respuesta contiene la lista de usuarios
        response.then().body("usuarios", not(emptyArray()));
        response.then().body("usuarios.id", everyItem(notNullValue()));
        response.then().body("usuarios.nombre", everyItem(notNullValue()));
        response.then().body("usuarios.fecha", everyItem(notNullValue()));
    }


    private boolean hayUsuariosEnLaBaseDeDatos() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/userdata";
        String user = "username";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {

            if (rs.next()) {
                return rs.getInt(1) > 0; // Devuelve true si hay al menos un usuario
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar usuarios en la base de datos: " + e.getMessage());
            throw e;
        }
        return false;
    }
}
