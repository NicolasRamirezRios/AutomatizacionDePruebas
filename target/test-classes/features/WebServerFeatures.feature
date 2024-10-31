Feature: Solicitudes al web server

  Scenario: Listar usuarios exitosamente
    Given el servidor HTTP está corriendo en el puerto 8000
    When realizo una solicitud GET a "/Listar?pagina=2"
    Then la respuesta tiene un código de estado "200 OK"
    
  Scenario: Fallo al realizar una solicitud GET a un endpoint no válido
    Given el servidor HTTP está corriendo en el puerto 8000
    When realizo una solicitud GET a "/listar?1=10"
    Then la respuesta tiene un código de estado "404 Not Found"


