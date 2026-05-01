# Explicación de Reglas — Users Management System

---

## Reglas de Arquitectura Hexagonal (Guía 1)

**Dominio libre de infraestructura**
`UserModel` no depende de frameworks ni de `UserEntity`. Los puertos (`CreateUserUseCase`, `SaveUserPort`) separan las dependencias de entrada y salida.

**DTOs usan `record`**
`CreateUserCommand`, `DeleteUserCommand`, `LoginCommand` y demás commands/queries son `record`, lo que garantiza inmutabilidad sin setters.

**Estados con `enum`**
`UserRole` y `UserStatus` son enums con método `fromString()` que lanza excepción ante valores inválidos.

**Validaciones solo en interfaces públicas**
Las anotaciones `@NotNull`, `@Valid`, `@NotBlank` se declaran únicamente en los puertos de entrada (`CreateUserUseCase`, `GetUserByIdUseCase`, etc.), nunca en las implementaciones.

**`@UtilityClass` para clases sin estado**
`DatabaseConnectionFactory`, `UserPersistenceMapper` y `ValidatorProvider` usan `@UtilityClass`, lo que genera constructor privado y hace los métodos estáticos automáticamente.

**No loguear PII**
Se eliminaron los logs que contenían emails y nombres en `CreateUserService`, `CreateUserHandler` y `LoginHandler`. Solo se registran mensajes genéricos sin datos personales.

**No hay logs en el dominio**
Los value objects (`UserEmail`, `UserId`, `UserName`) no contienen ningún logger. Los logs existen únicamente en adapters y entrypoints.

**Mappers entre capas**
`UserDesktopMapper`, `UserApplicationMapper` y `UserPersistenceMapper` convierten tipos entre capas, evitando que una capa conozca los tipos de otra.

**No retornar `null`**
`GetAllUsersService` retorna `Collections.emptyList()`. Los puertos de búsqueda retornan `Optional<UserModel>` en lugar de `null`.

**Excepciones en lugar de códigos de error**
`UserApplicationMapper.roleToCode()` lanza `IllegalArgumentException` en lugar de retornar `-1`. `UserDesktopMapper.requireValidId()` lanza excepción en lugar de retornar un valor especial.

---

## Reglas de Clean Code (Guía 2)

**Regla 1 — Una sola responsabilidad por función**
`LoginService` divide su lógica en `findUserOrFail()`, `verifyPasswordOrFail()` y `verifyUserIsActiveOrFail()`. Cada método hace exactamente una cosa.

**Regla 3 — Un solo nivel de abstracción**
`EmailNotificationService.sendNotification()` orquesta en alto nivel delegando los detalles a `loadTemplate()`, `renderTemplate()`, `buildDestination()` y `sendOrFail()`.

**Regla 4 — Métodos sin estado de instancia son `static`**
`EmailNotificationService.renderTemplate()` se declara `static` porque no usa estado de la instancia.

**Regla 6 — Sin parámetros booleanos de control**
Se eliminó el parámetro booleano que controlaba si se enviaba notificación en `UpdateUserService`. Ahora siempre se llama `emailNotificationService.notifyUserUpdated(updatedUser)` directamente.

**Regla 7 — Sin efectos secundarios ocultos**
El método `sendOrFail()` comunica exactamente lo que hace: envía o lanza excepción. No realiza acciones adicionales no evidentes.

**Regla 8 — Separar comandos y consultas (CQS)**
En `LoginService`, `findUserOrFail()` solo consulta y `verifyPasswordOrFail()` solo verifica. Ningún método consulta y modifica estado al mismo tiempo.

**Regla 11 — Centralizar lógica, evitar duplicación**
`EmailNotificationService` centraliza el flujo en un método genérico privado `sendNotification()`, evitando duplicar la cadena `loadTemplate → renderTemplate → buildDestination → send` en cada método público.

**Regla 13 — Evitar clases utilitarias innecesarias**
`UserValidationUtils` es el antipatrón: agrupa métodos que pertenecen a sus objetos de dominio. La validación de estado pertenece al modelo; la validación de formato pertenece al value object.

**Regla 16 — Reducir condicionales complejos**
`UserResponsePrinter.getStatusLabel()` reemplaza una cadena `if/else` por un `Map.of()` declarativo con `getOrDefault()`.

**Regla 17 — Condiciones claras y expresivas**
`UpdateUserService.ensureEmailIsNotTakenByAnotherUser()` consulta el email una sola vez, asigna el resultado a una variable de nombre descriptivo (`emailBelongsToAnotherUser`) y luego evalúa la condición.

**Regla 18 — Sin valores mágicos**
`UserPassword` define `MINIMUM_LENGTH = 8` y `BCRYPT_COST = 12`. `UserManagementCli` define `MENU_BORDER` como constante en lugar de repetir el literal de cadena.

**Regla 19 — Sin acoplamiento temporal**
`DependencyContainer` llama a `userRepository.init()` después del constructor, estableciendo un orden implícito frágil no protegido por ninguna interfaz. La inicialización debería ocurrir en el constructor.

**Regla 20 — Tipos de dominio en lugar de primitivos**
Los puertos y servicios reciben `UserId`, `UserEmail`, `UserStatus` en lugar de `String` e `int` desnudos, protegiendo las invariantes de dominio.

**Regla 21 — Sin códigos de error ambiguos**
Se eliminó el retorno de `-1` en `roleToCode()`. Ahora se lanza `IllegalArgumentException` con mensaje descriptivo ante rol inválido o nulo.

**Regla 22 — Código fácil de refactorizar**
El patrón `init()` en `UserRepositoryMySQL` crea acoplamiento rígido: si se cambia la implementación, hay que actualizar también `DependencyContainer`. La inicialización debería ocurrir en el constructor.

**Naming — Sin abreviaturas**
Se detectaron abreviaturas en `UpdateUserHandler` (`pw`, `upd`) y en `ConsoleIO` (`v`, `r`). Los nombres deben ser descriptivos: `password`, `updatedUser`, `value`, `rawInput`.

---

## Reglas de Pruebas (Guía 1, Regla 11)

**Estructura AAA**
Los tests usan comentarios `// Arrange`, `// Act`, `// Assert` para documentar cada fase. Ejemplos correctos: `DeleteUserServiceTest`, `LoginServiceTest`, `UpdateUserServiceTest`.

**`@DisplayName` descriptivo**
Cada test debe tener `@DisplayName` que describa el comportamiento esperado en lenguaje natural. Los tests sin esta anotación pierden valor documental.

**Aserciones semánticas**
Se reemplaza `assertTrue(result != null)` por `assertNotNull(result)`, `assertTrue(x.equals(y))` por `assertEquals(x, y)`, y `assertTrue(result == expected)` por `assertSame(expected, result)`.