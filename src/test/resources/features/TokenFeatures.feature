Feature: Pruebas de autenticación y autorización con JWT en Keycloak

  Scenario: Solicitar un token JWT usando client_credentials
    Given el cliente configurado
    When se solicita un token
    Then el token debe ser recibido exitosamente
    And el código de respuesta debe ser 200
    And la respuesta debe cumplir con el esquema JSON en el escenario 1

  Scenario: Fallar en la autenticación por secret incorrecto
    Given el cliente configurado con un secreto incorrecto
    When se solicita un token
    Then no se debe recibir un token
    And el código de respuesta debe ser 401
    And la respuesta debe cumplir con el esquema JSON en el escenario 2

  Scenario: Validar el JWT recibido por el servidor
    Given un token JWT válido enviado del servidor
    When se valida el token en el servidor usando la clave pública
    Then la respuesta debe indicar que el token es válido
    And el código de respuesta debe ser 200
    And la respuesta debe cumplir con el esquema JSON en el escenario 3

  Scenario: Intentar acceder a Keycloak con un token expirado
    Given un cliente configurado y un token expirado
    When hago una solicitud de introspección con el token expirado
    Then debería recibir un código de respuesta 401
    And el mensaje de respuesta debería indicar que el token ha expirado
    And la respuesta debe cumplir con el esquema JSON en el escenario 4

  Scenario: Intentar acceder a un endpoint seguro sin un token JWT
    Given no tengo un token JWT
    When hago una solicitud al endpoint seguro
    Then el código de respuesta debe ser 401
    And la respuesta debe cumplir con el esquema JSON en el escenario 5

  Scenario: Solicitar un token JWT usando cliente aleatorio
    Given el cliente aleatorio
    When se solicita un token
    Then la respuesta debe cumplir con el esquema JSON en el escenario 2

