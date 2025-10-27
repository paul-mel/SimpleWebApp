package com.learning.springboot.webapplication.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodoRepositoryTest {

    private static final String TEST_USER_1 = "testUser1";
    private static final String TEST_USER_2 = "testUser2";
    private static final String DESC1 = "Learn Spring Boot thoroughly";
    private static final String DESC2 = "Build a REST API project";
    private static final String DESC3 = "Write documentation";

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();

        todoRepository.saveAll(List.of(
                new Todo(0, TEST_USER_1, DESC1, LocalDate.now().plusDays(10), false),
                new Todo(0, TEST_USER_1, DESC2, LocalDate.now().plusDays(20), false),
                new Todo(0, TEST_USER_2, DESC3, LocalDate.now().plusDays(30), true)
        ));
    }

    @Test
    void findByUsername_ShouldReturnOnlyMatchingUserTodos() {
        List<Todo> Todos = todoRepository.findByUsername(TEST_USER_1);

        assertThat(Todos)
                .hasSize(2)
                .allMatch(todo -> todo.getUsername().equals(TEST_USER_1))
                .extracting(Todo::getDescription)
                .containsExactlyInAnyOrder(DESC1, DESC2);
    }

    @Test
    void findByUsername_CaseInsensitive_ShouldReturnResults() {
        List<Todo> johnUpper = todoRepository.findByUsername(TEST_USER_1.toUpperCase());

        assertThat(johnUpper)
                .hasSize(2)
                .allMatch(todo -> todo.getUsername().equals(TEST_USER_1));
    }

    @Test
    void saveAndFindById_ShouldPersistSuccessfully() {
        Todo todo = new Todo(0, TEST_USER_2, DESC2, LocalDate.now().plusDays(5), false);
        Todo saved = todoRepository.save(todo);

        Todo found = todoRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getUsername()).isEqualTo(TEST_USER_2);
        assertThat(found.getDescription()).isEqualTo(DESC2);
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        Todo existing = todoRepository.findAll().get(0);
        todoRepository.deleteById(existing.getId());

        assertThat(todoRepository.findById(existing.getId())).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTodos() {
        assertThat(todoRepository.findAll()).hasSize(3);
    }
}