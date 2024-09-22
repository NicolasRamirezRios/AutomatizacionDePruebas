Feature: Listar usuarios
  Como un cliente de la API
  Quiero listar los usuarios paginados
  Para ver los usuarios registrados en el sistema

  Scenario: Listar usuarios exitosamente
    Given el servidor HTTP está corriendo en el puerto 8000
    And la base de datos tiene usuarios registrados
    When realizo una solicitud GET a "/listarUsuarios?1=5"
    Then la respuesta tiene un código de estado "200 OK"
    And la respuesta contiene una lista de usuarios con sus ID, nombres y fechas de registro

