Feature: Registro de usuario
  Scenario: Registrar un usuario con datos válidos
    Given el servicio de autenticación está activo
    When registro un nuevo usuario con los siguientes datos
      | username  | usuario_prueb0aa8  |
      | firstName | New             |
      | lastName  | User            |
      | email     | test@ejem0plaa0o8.com|
      | enabled   | true            |
      | password  | AUYH24&25@DI2   |
    Then la respuesta del servicio de autenticación tiene un código de estado 201
    And el mensaje de éxito es "User created successfully"

  Scenario: Intentar registrar un usuario sin contraseña
    Given el servicio de autenticación está activo
    When registro un nuevo usuario con los siguientes datos
      | username  | usuario2        |
      | firstName | New             |
      | lastName  | User            |
      | email     | new@user.com    |
      | enabled   | true            |
    Then el código de error es 400 y el mensaje es "Password is required"

  Scenario: Iniciar sesión con credenciales válidas
    Given el servicio de autenticación está activo
    When inicio sesión con el usuario "petro" y la contraseña "petro"
    Then la respuesta del servicio de autenticación tiene un código de estado 200
    And el token de acceso es devuelto

  Scenario: Intentar iniciar sesión con contraseña incorrecta
    Given el servicio de autenticación está activo
    When inicio sesión con el usuario "abaab" y la contraseña "incorrectPassword"
    Then el código de error es 401 y el mensaje es "Credenciales inválidas"