package com.learning.springboot.webapplication.todo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest {

    private static final String USER = "testUser";
    private static final String ADMIN = "admin";
    private static final String SHORT_DESC = "short";
    private static final String VALID_DESC = "This is a valid description";
    private static final String TODO_DESC = "Complete Spring Security setup";

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructorAndGetters_ShouldReturnCorrectValues() {
        LocalDate date = LocalDate.of(2025, 12, 31);
        Todo todo = new Todo(1, USER, "Learn Spring Boot basics", date, false);

        assertEquals(1, todo.getId());
        assertEquals(USER, todo.getUsername());
        assertEquals("Learn Spring Boot basics", todo.getDescription());
        assertEquals(date, todo.getTargetDate());
        assertFalse(todo.isDone());
    }

    @Test
    void setters_ShouldUpdateAllFields() {
        Todo todo = new Todo();
        LocalDate date = LocalDate.now();

        todo.setId(2);
        todo.setUsername(USER);
        todo.setDescription(VALID_DESC);
        todo.setTargetDate(date);
        todo.setDone(true);

        assertEquals(2, todo.getId());
        assertEquals(USER, todo.getUsername());
        assertEquals(VALID_DESC, todo.getDescription());
        assertEquals(date, todo.getTargetDate());
        assertTrue(todo.isDone());
    }

    @Test
    void descriptionTooShort_ShouldFailValidation() {
        Todo todo = new Todo(1, USER, SHORT_DESC, LocalDate.now(), false);

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);

        assertFalse(violations.isEmpty());
        assertEquals("Enter at least 10 characters", violations.iterator().next().getMessage());
    }

    @Test
    void validDescription_ShouldPassValidation() {
        Todo todo = new Todo(1, USER, VALID_DESC, LocalDate.now(), false);

        Set<ConstraintViolation<Todo>> violations = validator.validate(todo);

        assertTrue(violations.isEmpty());
    }

    @Test
    void toString_ShouldContainAllFields() {
        LocalDate date = LocalDate.of(2025, 10, 27);
        Todo todo = new Todo(3, ADMIN, TODO_DESC, date, true);

        String str = todo.toString();

        assertTrue(str.contains("id=3"));
        assertTrue(str.contains(ADMIN));
        assertTrue(str.contains(TODO_DESC));
        assertTrue(str.contains("2025-10-27"));
        assertTrue(str.contains("done=true"));
    }
}