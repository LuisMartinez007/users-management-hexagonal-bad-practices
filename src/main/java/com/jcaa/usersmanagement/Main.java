package com.jcaa.usersmanagement;

import com.jcaa.usersmanagement.infrastructure.config.DependencyContainer;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.UserManagementCli;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import lombok.extern.java.Log;
import java.util.Scanner;

// Regla 24 - CORREGIDO: se usa @Log de Lombok (mismo framework que el resto del proyecto).
// Regla 1  - CORREGIDO: se extraen métodos con nombre claro para separar responsabilidades.
@Log
public final class Main {

    public static void main(final String[] args) {
        log.info("Starting Users Management System...");
        final DependencyContainer container = buildContainer();
        runCli(container);
    }

    private static DependencyContainer buildContainer() {
        return new DependencyContainer();
    }

    private static void runCli(final DependencyContainer container) {
        try (final Scanner scanner = new Scanner(System.in)) {
            final ConsoleIO console = new ConsoleIO(scanner, System.out);
            new UserManagementCli(container.userController(), console).start();
        }
    }
}