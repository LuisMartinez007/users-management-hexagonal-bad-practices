package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.Objects;

public class UserApplicationMapper {

  public static UserModel fromCreateCommandToModel(final CreateUserCommand command) {
    final String userId   = command.id();
    final String userName = command.name();
    final String email    = command.email();   // Regla 24 - CORREGIDO: nombre consistente
    final String password = command.password();
    final String role     = command.role();

    return UserModel.create(
            new UserId(userId),
            new UserName(userName),
            new UserEmail(email),
            UserPassword.fromPlainText(password),
            UserRole.fromString(role));
}

  public static UserModel fromUpdateCommandToModel(
        final UpdateUserCommand command, final UserPassword currentPassword) {
    final UserPassword passwordToUse;
    if (command.password() == null || command.password().isBlank()) {
        passwordToUse = currentPassword;
    } else {
        passwordToUse = UserPassword.fromPlainText(command.password());
    }
    final String email = command.email();   // Regla 24 - CORREGIDO: mismo nombre que arriba
    return new UserModel(
            new UserId(command.id()),
            new UserName(command.name()),
            new UserEmail(email),
            passwordToUse,
            UserRole.fromString(command.role()),
            UserStatus.fromString(command.status()));
  }

  public static UserId fromGetUserByIdQueryToUserId(final GetUserByIdQuery query) {
    return new UserId(query.id());
  }

  public static UserId fromDeleteCommandToUserId(final DeleteUserCommand command) {
    return new UserId(command.id());
  }

  // Clean Code - Regla 21 (no retornar banderas de error):
  // Este método retorna 1, 2, 3 o -1 como códigos de resultado para representar roles.
  // La regla dice: no usar valores especiales (-1, null, "ERROR", false) para señalar errores.
  // El contrato de salida NO diferencia ausencia, falla y éxito:
  //   - ¿Qué significa -1? ¿Error de parseo? ¿Rol desconocido? ¿No autorizado?
  //   - El llamador DEBE recordar qué valor representa cada caso — frágil y opaco.
  // Solución: lanzar IllegalArgumentException o usar Optional<Integer> con semántica clara.
  // Regla 21 - CORREGIDO: se lanza excepción en lugar de retornar -1.
  public static int roleToCode(final String role) {
      if (Objects.isNull(role) || role.isBlank()) {
          throw new IllegalArgumentException("El rol no puede ser nulo o vacío.");
      }
      if ("ADMIN".equalsIgnoreCase(role))    return 1;
      if ("MEMBER".equalsIgnoreCase(role))   return 2;
      if ("REVIEWER".equalsIgnoreCase(role)) return 3;
      throw new IllegalArgumentException("Rol desconocido: " + role);
  }
}
