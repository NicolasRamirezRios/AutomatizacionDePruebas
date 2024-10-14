Feature: Solicitudes al web server

  Scenario: Listar usuarios exitosamente
    Given el servidor HTTP está corriendo en el puerto 8000
    And la base de datos tiene usuarios registrados
    When realizo una solicitud GET a "/listarUsuarios?1=5"
    Then la respuesta tiene un código de estado "200 OK"
    And la respuesta contiene una lista de usuarios con sus ID, nombres y fechas de registro

  Scenario: Fallo al realizar una solicitud GET a un endpoint no válido
    Given el servidor HTTP está corriendo en el puerto 8000
    When realizo una solicitud GET a "/listar?1=10"
    Then la respuesta tiene un código de estado "404 Not Found"