Feature: Pruebas al servicio notificación

  Scenario: Envío correcto de una notificación
    Given un destinatario de notificación "alejandro.sanchezc1@uqvirtual.edu.co"
    And un mensaje de notificación "Hola, este es un mensaje de prueba"
    And un canal de notificación "email"
    When envío la notificación
    Then estado de la respuesta debe ser 200
    And la respuesta debe contener los detalles de la notificación

  Scenario: Recuperar todas las notificaciones
    When solicito todas las notificaciones
    Then estado de la respuesta debe ser 200
    And la respuesta debe contener una lista de notificaciones

  Scenario: Recuperar una notificación específica por ID
    Given una notificación ID 1
    When solicito la notificación por ID
    Then estado de la respuesta debe ser 200
    And la respuesta debe contener la notificación con ID 1