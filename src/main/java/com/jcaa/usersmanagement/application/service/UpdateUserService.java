package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.mapper.UserApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import java.util.Optional;
import java.util.Set;

@Log
@RequiredArgsConstructor
public final class UpdateUserService implements UpdateUserUseCase {

    private final UpdateUserPort updateUserPort;
    private final GetUserByIdPort getUserByIdPort;
    private final GetUserByEmailPort getUserByEmailPort;
    private final EmailNotificationService emailNotificationService;
    private final Validator validator;

    @Override
    public UserModel execute(final UpdateUserCommand command) {
        validateCommand(command);
        final UserId userId     = new UserId(command.id());
        final UserModel current = findExistingUserOrFail(userId);
        final UserEmail newEmail = new UserEmail(command.email());
        ensureEmailIsNotTakenByAnotherUser(newEmail, userId);
        final UserModel userToUpdate =
                UserApplicationMapper.fromUpdateCommandToModel(command, current.getPassword());
        final UserModel updatedUser = updateUserPort.update(userToUpdate);
        // Regla 6 - CORREGIDO: se elimina el parámetro booleano; siempre se notifica.
        emailNotificationService.notifyUserUpdated(updatedUser);
        return updatedUser;
    }

    private void validateCommand(final UpdateUserCommand command) {
        final Set<ConstraintViolation<UpdateUserCommand>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private UserModel findExistingUserOrFail(final UserId userId) {
        return getUserByIdPort
                .getById(userId)
                .orElseThrow(() -> UserNotFoundException.becauseIdWasNotFound(userId.value()));
    }

    // Regla 17/25/26/27 - CORREGIDO: lógica clara y sin llamadas redundantes al repositorio.
    // Se consulta el email UNA sola vez y se razona sobre el resultado.
    private void ensureEmailIsNotTakenByAnotherUser(
            final UserEmail newEmail, final UserId ownerId) {
        final Optional<UserModel> existingOwner = getUserByEmailPort.getByEmail(newEmail);
        final boolean emailBelongsToAnotherUser = existingOwner.isPresent()
                && !existingOwner.get().getId().equals(ownerId);
        if (emailBelongsToAnotherUser) {
            throw UserAlreadyExistsException.becauseEmailAlreadyExists(newEmail.value());
        }
    }
}