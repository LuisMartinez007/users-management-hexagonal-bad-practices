package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetAllUsersUseCase;
import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public final class GetAllUsersService implements GetAllUsersUseCase {

    private final GetAllUsersPort getAllUsersPort;

    @Override
    public List<UserModel> execute() {
        // Regla 5 - CORREGIDO: Se retorna Collections.emptyList() en lugar de null.
        // Regla 21 - CORREGIDO: No se usan códigos especiales (null) para lista vacía.
        final List<UserModel> users = getAllUsersPort.getAll();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }
}