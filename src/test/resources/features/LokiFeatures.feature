Feature: Gestión de logs en Loki a través de su API

Scenario: Buscar logs en Loki
Given Loki está corriendo
When busco logs que contengan la cadena "error"
Then debería ver logs devueltos que contengan "error"

Scenario: Enviar una nueva entrada de log a Loki
Given Loki está corriendo
When envío una entrada de log con el mensaje "Nueva entrada de log para pruebas"
Then la entrada de log debería estar disponible en Loki
