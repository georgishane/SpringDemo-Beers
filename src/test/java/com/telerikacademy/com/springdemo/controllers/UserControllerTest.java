package com.telerikacademy.com.springdemo.controllers;

import com.telerikacademy.com.springdemo.controllers.rest.UserController;
import com.telerikacademy.com.springdemo.exceptions.GlobalExceptionHandler;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Comment;
import com.telerikacademy.com.springdemo.models.Role;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import com.telerikacademy.com.springdemo.services.BeerService;
import com.telerikacademy.com.springdemo.services.CommentService;
import com.telerikacademy.com.springdemo.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BeerService beerService;

    @MockBean
    private AuthenticationHelper authenticationHelper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CommentService commentService;

    @Test
    void getCommentsByUser_returnsComments_whenUserRequestsOwnComments() throws Exception {
        User john = createUser("john", "USER");
        Comment comment = createComment(john, "Glarus English Ale", "Great beer");

        Mockito.when(userService.getByUserName("john"))
                .thenReturn(john);

        Mockito.when(commentService.getCommentsByUser("john"))
                .thenReturn(List.of(comment));

        mockMvc.perform(get("/api/users/john/comments")
                        .principal(new TestingAuthenticationToken("john", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("john"))
                .andExpect(jsonPath("$[0].beerName").value("Glarus English Ale"))
                .andExpect(jsonPath("$[0].text").value("Great beer"));
    }

    @Test
    void getCommentsByUser_returnsForbidden_whenUserRequestsAnotherUsersComments() throws Exception {
        User john = createUser("john", "USER");

        Mockito.when(userService.getByUserName("john"))
                .thenReturn(john);

        mockMvc.perform(get("/api/users/maria/comments")
                        .principal(new TestingAuthenticationToken("john", null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You cannot view these comments"));

        verify(commentService, never()).getCommentsByUser("maria");
    }

    private static User createUser(String username, String roleName) {
        Role role = new Role();
        role.setName(roleName);

        User user = new User();
        user.setUsername(username);
        user.setRoles(Set.of(role));

        return user;
    }

    private static Comment createComment(User user, String beerName, String text) {
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName(beerName);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setBeer(beer);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.of(2026, 1, 10, 12, 30));

        return comment;
    }
}
