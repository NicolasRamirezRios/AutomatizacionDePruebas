Feature: Pruebas de autenticación y autorización con JWT en Keycloak

  Scenario: Solicitar un token JWT usando client_credentials
    Given el cliente "Nicolas" con el secreto "twL4vlrZ3MPVM3ANoY5EV0RWhsAOZyvQ"
    When se solicita un token en el endpoint "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token"
    Then el token debe ser recibido exitosamente
    And el código de respuesta debe ser 200

  Scenario: Fallar en la autenticación por secret incorrecto
    Given el cliente "Nicolas" con un secreto incorrecto
    When se solicita un token en el endpoint "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token"
    Then no se debe recibir un token
    And el código de respuesta debe ser 401

  Scenario: Validar el JWT recibido por el servidor
    Given un token JWT válido enviado del servidor
    When se valida el token en el servidor usando la clave pública
    Then la respuesta debe indicar que el token es válido
    And el código de respuesta debe ser 200

  Scenario: Intentar acceder a Keycloak con un token expirado
    Given tengo un cliente con ID "Nicolas" y secreto "twL4vlrZ3MPVM3ANoY5EV0RWhsAOZyvQ"
    And tengo un token expirado "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ0WVF2WkFpQXZZT0txZG01TDF5ZDN3alg5bHpHeVZucy03VzZ2Z0M4QnFnIn0.eyJleHAiOjE3MjcwMjYwMDgsImlhdCI6MTcyNzAyNTcwOCwianRpIjoiYmVjZTA4M2YtNGJiYS00OGQ2LTgzNWUtZDJiOTM4MDY4OWM3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9SZWFsbV9QcnVlYmEiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYjZlYWVkZjUtOWFhNi00MTQ2LWI2YTItOTI3MjA1MzAxOGIzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiTmljb2xhcyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJlYWxtX3BydWViYSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImNsaWVudEhvc3QiOiIxNzIuMTguMC4xIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtbmljb2xhcyIsImNsaWVudEFkZHJlc3MiOiIxNzIuMTguMC4xIiwiY2xpZW50X2lkIjoiTmljb2xhcyJ9.Q3vqmeBIx_rXLoAkehXBFQf-iiAzh0md0yDybfb7ILQ2K33XGD8Mm7owRYbTDNOWfKSsGqmyl_MvBMQpzrKAr7W4V03zJ_DOMaGhq7lzPtj1v4blrwSDLr8Rx-mC7NMQLaAnDVob9WuOQAJWAf9S6cnIpKHS0czaWXEKfLPmbgS6c1odh0RNJoF0EqFzE8R3bz8aqkQUOaIz0L66Dc20BhUsuQFTpEsj1OY0M7Rh9qgp--jHSVtpUrucrJzTYZ6-zno0MhQDw4TmXstfd3IKTE9yOn5sm-2sFMJvqow0-_HHafrtwNmBfTXXLC_b51Ko-KwUsf_B4-ZGLorJQ7CHqg"
    When hago una solicitud de introspección al endpoint "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/token/introspect" con el token expirado
    Then debería recibir un código de respuesta 401
    And el mensaje de respuesta debería indicar que el token ha expirado

  Scenario: Intentar acceder a un endpoint seguro sin un token JWT
    Given no tengo un token JWT
    When hago una solicitud al endpoint seguro "http://localhost:8080/realms/Realm_Prueba/protocol/openid-connect/userinfo"
    Then el código de respuesta debe ser 401


