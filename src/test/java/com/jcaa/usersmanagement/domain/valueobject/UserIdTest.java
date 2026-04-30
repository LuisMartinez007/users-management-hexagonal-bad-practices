package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// VIOLACIÓN Regla 11: se eliminó @DisplayName de la clase y de todos los métodos.
// Los tests deben tener nombres descriptivos con @DisplayName para documentar el comportamiento.
class UserIdTest {

  @ParameterizedTest
    @DisplayName("crea UserId con valor recortado (trim)")
    @ValueSource(strings = {" user123 ", "  user123  ", "user123\t"})
    void shouldCreateUserIdWithTrimmedValue(final String input) {
        // Arrange
        final String expectedId = "user123";

        // Act
        final UserId userId = new UserId(input);

        // Assert
        assertEquals(expectedId, userId.toString(), "el valor debe ser el mismo después del trim");
    }

  @Test
  void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
    assertThrows(NullPointerException.class, () -> new UserId(null));
  }

  @ParameterizedTest
    @DisplayName("lanza InvalidUserIdException cuando el id es vacío o solo espacios")
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r", "\f", "\b"})
  void shouldThrowIllegalArgumentExceptionWhenUserIdIsEmpty(String input) {
    assertThrows(InvalidUserIdException.class, () -> new UserId(input));
  }
}
