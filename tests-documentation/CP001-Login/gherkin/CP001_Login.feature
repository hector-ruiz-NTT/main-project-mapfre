Feature: Login exitoso en Mapfre SGO
  Como usuario del sistema SGO
  Quiero poder iniciar sesión con mis credenciales
  Para acceder a las funcionalidades del sistema

  Scenario: Login exitoso con credenciales válidas
    Given navego a la página de login 'https://mapfrespain-test.appiancloud.com/suite/sites/sgo?signin=native'
    Then se muestra el formulario de login
    And el campo 'Nombre de usuario' está visible
    And el campo 'Contraseña' está visible
    And el botón 'Entrar' está visible
    
    When introduzco el usuario 'SGO_PRUEBAS1' en el campo de usuario
    And introduzco la contraseña 'Mapfre2023' en el campo de contraseña
    And pulso el botón 'ENTRAR'
    And espero a que el DOM de la pantalla cargue completamente
    
    Then soy redirigido a la página principal de SGO
    And la URL contiene '/suite/sites/sgo'
    And se muestra el menú de navegación
    And puedo ver las opciones del menú principal:
      | Opción |
      | Bandejas |
      | Crear Solicitud |
      | Cuadro Resumen |
      | Informe Supervisor |
      | Coordinador |
      | Módulo de Búsqueda |
    And el usuario 'SGO_PRUEBAS1' está logueado correctamente
