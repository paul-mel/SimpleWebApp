package com.learning.springboot.webapplication.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    private static final String LIST_TODOS_URL = "/list-Todos";
    private static final String ADD_TODOS_URL = "/add-todo";
    private static final String USERNAME= "testUser";
    private static final String LIST_TODOS_VIEW_NAME = "listTodos";
    private static final String DESCRIPTION = "description";
    private static final String TARGET_DATE = "targetDate";
    private static final String DONE = "done";
    private static final String DELETE_TODO_URL = "/delete-todo";
    private static final String UPDATE_TODO_URL = "/update-todo";
    private static final String TODO_VIEW = "todo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    private Todo existingTodo;

    @BeforeEach
    void setup() {
        todoRepository.deleteAll();
        todoRepository.save(new Todo(0, "testUser", "Learn Spring Boot", LocalDate.now().plusDays(10), false));
        existingTodo = todoRepository.save(new Todo(0, USERNAME, "Learn Spring Boot", LocalDate.now().plusDays(10), false));
    }

    @Test
    @WithMockUser(username =USERNAME)
    void listTodos_ShouldReturnViewWithTodos() throws Exception {
        mockMvc.perform(get(LIST_TODOS_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(LIST_TODOS_VIEW_NAME))
                .andExpect(model().attributeExists("todos"));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void addTodo_WithValidationError_ShouldReturnTodoView() throws Exception {
        mockMvc.perform(post(ADD_TODOS_URL)
                        .with(csrf())
                        .param(DESCRIPTION, "short") // validation fails
                        .param(TARGET_DATE, LocalDate.now().plusDays(1).toString())
                        .param(DONE, "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo"))
                .andExpect(model().attributeHasFieldErrors("todo", DESCRIPTION));
    }

    @Test
    @WithMockUser(username = "testUser")
    void addTodo_ValidInput_ShouldRedirect() throws Exception {
        mockMvc.perform(post(ADD_TODOS_URL)
                        .with(csrf())
                        .param(DESCRIPTION, "Finish learning Spring Security")
                        .param(TARGET_DATE, LocalDate.now().plusDays(1).toString())
                        .param(DONE, "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("list-Todos"));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void deleteTodo_ShouldRemoveTodoAndRedirect() throws Exception {
        mockMvc.perform(get(DELETE_TODO_URL).param("id", String.valueOf(existingTodo.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("list-Todos"));

        // verify it was removed
        boolean exists = todoRepository.findById(existingTodo.getId()).isPresent();
        assert !exists;
    }

    @Test
    @WithMockUser(username = USERNAME)
    void showUpdateTodoPage_ShouldLoadTodoIntoModel() throws Exception {
        mockMvc.perform(get(UPDATE_TODO_URL).param("id", String.valueOf(existingTodo.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name(TODO_VIEW))
                .andDo(result -> {
                    Todo todoFromModel = (Todo) result.getModelAndView().getModel().get("todo");
                    assertThat(todoFromModel).usingRecursiveComparison()
                            .isEqualTo(existingTodo);
                });
    }


}