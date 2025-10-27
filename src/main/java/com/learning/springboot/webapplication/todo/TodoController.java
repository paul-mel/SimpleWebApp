package com.learning.springboot.webapplication.todo;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@SessionAttributes("name")
public class TodoController {

    private static final String TODO ="todo";
    private static final String REDIRECT_LIST_TODOS ="redirect:list-Todos";

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @RequestMapping("list-Todos")
    public String listAllTodos(ModelMap model) {
        List<Todo> todos = todoRepository.findByUsername(getTodoUsername(model));
        model.addAttribute("todos", todos);
        return "listTodos";
    }

    @GetMapping(value = "add-todo")
    public String showNewTodoPage(ModelMap model) {
        String username = getLoggedInUsername();
        Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
        model.put(TODO, todo);
        return TODO;
    }

    @PostMapping(value = "add-todo")
    public String addNewTodo(ModelMap model, @Valid Todo todo, BindingResult result) {

        if (result.hasErrors()) {
            return TODO;
        }

        todo.setUsername(getTodoUsername(model));
        todoRepository.save(todo);
        return REDIRECT_LIST_TODOS;
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id) {
        todoRepository.deleteById(id);
        return REDIRECT_LIST_TODOS;
    }

    @GetMapping(value = "update-todo")
    public String showUpdateTodoPage(@RequestParam int id, ModelMap model) {
        Todo todo = todoRepository.findById(id).get();
        model.addAttribute(TODO, todo);
        return TODO;
    }

    @PostMapping(value = "update-todo")
    public String updateTodo(ModelMap model, @Valid Todo todo, BindingResult result) {

        if (result.hasErrors()) {
            return TODO;
        }

        todo.setUsername(getTodoUsername(model));
        todoRepository.save(todo);

        return "redirect:list-Todos";
    }

    private String getTodoUsername(ModelMap model) {
        String username = (String) model.get("name");
        return username;
    }

    private String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
