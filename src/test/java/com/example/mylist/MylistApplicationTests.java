package com.example.mylist;

import com.example.mylist.infrastructure.persistence.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateAndListTodoForAuthenticatedUser() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(post("/api/todos")
                        .with(googleUser("abhinay@example.com", "Abhinay Kumar Sahu"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Buy groceries",
                                  "taskDescription": "Milk and bread",
                                  "dueDate": "2026-04-10",
                                  "priority": "high",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.taskDescription").value("Milk and bread"))
                .andExpect(jsonPath("$.dueDate").value("2026-04-10"))
                .andExpect(jsonPath("$.priority").value("high"));

        mockMvc.perform(get("/api/todos")
                        .with(googleUser("abhinay@example.com", "Abhinay Kumar Sahu")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Buy groceries"))
                .andExpect(jsonPath("$[0].taskDescription").value("Milk and bread"))
                .andExpect(jsonPath("$[0].dueDate").value("2026-04-10"))
                .andExpect(jsonPath("$[0].priority").value("high"));
    }

    @Test
    void shouldKeepTodosIsolatedPerGoogleAccount() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(post("/api/todos")
                        .with(googleUser("owner-one@example.com", "Owner One"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Private task",
                                  "taskDescription": "Only first user should see this",
                                  "priority": "medium",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/todos")
                        .with(googleUser("owner-two@example.com", "Owner Two")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldUpdateExistingTodo() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(post("/api/todos")
                        .with(googleUser("abhinay@example.com", "Abhinay Kumar Sahu"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Old title",
                                  "taskDescription": "Old description",
                                  "priority": "low",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated());

        int todoId = todoRepository.findAll().get(0).getId();

        mockMvc.perform(put("/api/todos/{id}", todoId)
                        .with(googleUser("abhinay@example.com", "Abhinay Kumar Sahu"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New title",
                                  "taskDescription": "New description",
                                  "completed": true,
                                  "dueDate": "2026-04-15",
                                  "priority": "high"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.taskDescription").value("New description"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.dueDate").value("2026-04-15"))
                .andExpect(jsonPath("$.priority").value("high"));
    }

    @Test
    void shouldReturnUnauthorizedForAnonymousApiRequest() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldExposeAuthenticatedProfile() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .with(googleUser("abhinay@example.com", "Abhinay Kumar Sahu")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("abhinay@example.com"))
                .andExpect(jsonPath("$.name").value("Abhinay Kumar Sahu"));
    }

    private OAuth2LoginRequestPostProcessor googleUser(String email, String name) {
        return oauth2Login()
                .attributes(attributes -> {
                    attributes.put("email", email);
                    attributes.put("name", name);
                    attributes.put("picture", "https://example.com/avatar.png");
                });
    }
}
