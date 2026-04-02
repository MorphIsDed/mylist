package com.example.mylist;

import com.example.mylist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MylistApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TodoRepository todoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateAndListTodo() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Buy groceries",
                                  "taskDescription": "Milk and bread"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.taskDescription").value("Milk and bread"));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Buy groceries"))
                .andExpect(jsonPath("$[0].taskDescription").value("Milk and bread"));
    }

    @Test
    void shouldUpdateExistingTodo() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Old title",
                                  "taskDescription": "Old description"
                                }
                                """))
                .andExpect(status().isCreated());

        int todoId = todoRepository.findAll().get(0).getId();

        mockMvc.perform(put("/todos/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New title",
                                  "taskDescription": "New description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.taskDescription").value("New description"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingTodo() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(delete("/todos/9999"))
                .andExpect(status().isNotFound());
    }
}
