Feature: Verificar logs de una petición GET a un servicio y su registro en Loki

  Scenario: Realizar una petición GET a la API y verificar el log en Loki
    Given un usuario realiza una petición GET a la API
    When el log de la petición se almacena en Loki
    Then el log debería estar presente en Loki con nivel "INFO"
