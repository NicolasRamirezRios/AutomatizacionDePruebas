Feature: Validación del API Gateway para enrutar servicios de autenticación y perfiles
  Como desarrollador
  Quiero validar que el API Gateway enrute correctamente entre los servicios
  Para asegurar el funcionamiento adecuado de los endpoints combinados

  Scenario: Registrar un nuevo usuario y crear perfil asociado
    When registro un nuevo usuario en el servicio de autenticación con los siguientes datos
      | key      | value            |
      | username | UsuarioNuevo1        |
      | firstName| New              |
      | lastName | User             |
      | email    | user@t80st.com    |
      | enabled  | true             |
      | password | secret123        |
    And el mensaje exitoso es "User created successfully"

    When inicio sesión a través del API Gateway con el usuario "UsuarioNuevo1" y la contraseña "secret123"
    Then la respuesta dio un estado 200
    And el token de acceso devuelve

    When veo el perfil vacío para el usuario "UsuarioNuevo1"
    And la respuesta dio un estado 200
