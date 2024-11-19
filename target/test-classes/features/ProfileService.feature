Feature: Perfil de usuario

  Scenario: Verificar que el servicio de ping responde correctamente
    Given El servicio de perfiles está activo
    When Realizo una solicitud GET al endpoint "/ping"
    Then La respuesta tiene un estado 200
    And La respuesta contiene el "message" "pong"

  Scenario: Intentar consultar un perfil inexistente
    Given El servicio de perfiles está activo
    When Realizo una solicitud GET al endpoint "/consultar-perfil/9999"
    Then La respuesta tiene un estado 404
    And La respuesta contiene el "detail" "Perfil no encontrado"

  Scenario: Crear un perfil y obtenerlo exitosamente
    Given El servicio de perfiles está activo
    When Realizo una solicitud POST al endpoint "/actualizar-perfil" con el siguiente cuerpo:
      """
      {
        "nickname": "petro",
        "bio": "presi",
        "public_contact": true,
        "website": "https://gustavito.com",
        "organization": "pacto historico",
        "country": "COL",
        "social_links": "https://twitter.com/petrogustavo"
      }
      """
    Then La respuesta tiene un estado 200
    And La respuesta contiene el "status" "Perfil actualizado"

  Scenario: Obtener el perfil recién creado
    Given El servicio de perfiles está activo
    When Realizo una solicitud GET al endpoint "/consultar-perfil/john_doe"
    Then La respuesta tiene un estado 200
    And El campo "nickname" tiene el valor "john_doe"
    And El campo "bio" tiene el valor "Software developer"
