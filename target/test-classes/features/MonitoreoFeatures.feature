Feature: Verificación del servicio de monitoreo Prometheus

  Scenario: Verificar si Prometheus está en funcionamiento
    Given el servicio de Prometheus está activo
    When hago una solicitud GET al endpoint runtimeinfo
    Then la respuesta tiene un código de estado 200
    And la respuesta contiene información sobre el tiempo de ejecución

  Scenario: Verificar la recolección de métricas
    Given el servicio de Prometheus está activo
    When hago una solicitud GET al endpoint query con el parámetro "up"
    Then la respuesta tiene un código de estado 200
    And la respuesta contiene datos de las métricas recolectadas

  Scenario: Todos los servicios deben estar funcionando
    Given el servicio de Prometheus está activo
    When realizo una consulta para verificar el estado de los servicios
    Then todos los servicios deben estar en estado "up"