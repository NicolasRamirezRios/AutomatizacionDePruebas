Feature: Interactuar con Loki

  Scenario: Consultar el estado de Loki
    Given Loki está corriendo
    When solicito las métricas de Loki
    Then debería recibir las métricas correctamente
    And el código de estado debe ser 200

  Scenario: Verificar la configuración actual de Loki
    Given Loki está corriendo
    When verifico la configuración actual del sistema
    Then la respuesta debe incluir la configuración actual del sistema
    And el código de estado debe ser 200

  Scenario: Enviar y buscar logs en Loki
    Given Loki está corriendo
    When envío un log de prueba con la cadena "success"
    And busco logs que contengan la cadena "success"
    Then debería ver logs devueltos que contengan "success"

  Scenario: Preparar a Loki para un cierre
    Given Loki está corriendo
    When envío una solicitud para preparar a Loki para el cierre
    Then Loki debería estar preparado para el cierre sin pérdida de datos
    And el código de estado debe ser 204

